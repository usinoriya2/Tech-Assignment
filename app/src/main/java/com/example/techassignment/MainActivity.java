package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.techassignment.Models.Repository;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout pullToRefresh;
    private RelativeLayout noInternetLayout;
    private LinearLayout repoLayout;
    private Button retryButton;
    private Handler handler;
    private Runnable r;
    private JsonArrayRequest mJsonArrayRequest;
    private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        getRepositoryData();
    }

    private void bindViews(){
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
        repoLayout = findViewById(R.id.repo_layout);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        retryButton = findViewById(R.id.retry_button);
        pullToRefresh = findViewById(R.id.pull_to_refresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRepositoryData();
                pullToRefresh.setRefreshing(false);
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRepositoryData();
            }
        });

    }


    private void getRepositoryData(){
        noInternetLayout.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        repoLayout.removeAllViews();
        mJsonArrayRequest = new JsonArrayRequest(
                "https://private-anon-cf6ffd2614-githubtrendingapi.apiary-mock.com/repositories", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                setRepositoryData(response);
                checkAndSaveCache(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Fetch fail",error.toString());
                checkAndFetchCache();
            }
        });

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.add(mJsonArrayRequest);
    }

    private void setRepositoryData(JSONArray response){
        List<Repository> repositoryList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                Repository repository = new Repository(jsonObject.getString("author"),jsonObject.getString("name"),jsonObject.getString("avatar"),
                        jsonObject.getString("url"),jsonObject.getString("description"),jsonObject.getString("language"),jsonObject.getString("languageColor"),
                        jsonObject.getInt("stars"),jsonObject.getInt("forks"),jsonObject.getInt("currentPeriodStars"));
                repositoryList.add(repository);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for(Repository repository: repositoryList){
            makeRepositoryLayout(repoLayout, repository);
        }
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
    }

    private void makeRepositoryLayout( LinearLayout view, Repository repository){
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View repoView = inflater.inflate(R.layout.repository, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 10;
        repoView.setLayoutParams(lp);
        TextView author = repoView.findViewById(R.id.author);
        TextView name = repoView.findViewById(R.id.repo_name);
        TextView description = repoView.findViewById(R.id.description);
        ImageView avatar = repoView.findViewById(R.id.avatar);
        final LinearLayout descriptionLayout = repoView.findViewById(R.id.description_layout);
        CardView languageColor = repoView.findViewById(R.id.language_color);
        TextView language = repoView.findViewById(R.id.language);
        TextView stars = repoView.findViewById(R.id.stars);
        TextView forks = repoView.findViewById(R.id.forks);

        author.setText(repository.getAuthor());
        name.setText(repository.getName());
        description.setText(repository.getDescription());
//        languageColor.setCardBackgroundColor(repository.getLanguageColor());
        language.setText(repository.getLanguage());
        stars.setText(String.valueOf(repository.getStars()));
        forks.setText(String.valueOf(repository.getForks()));

        Uri imgUri=Uri.parse(repository.getAvatar());
        avatar.setImageURI(null);
        avatar.setImageURI(imgUri);

        repoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descriptionLayout.getVisibility() == View.GONE){
                    descriptionLayout.setVisibility(View.VISIBLE);
                }else{
                    descriptionLayout.setVisibility(View.GONE);
                }
            }
        });

        view.addView(repoView);
    }

    private void checkAndSaveCache(JSONArray response)  {
        try{
            if(!PreferenceManager.getDefaultSharedPreferences(this).contains("JSONCache") || !PreferenceManager.getDefaultSharedPreferences(this).contains("TimestampCache")){
                saveCurrentToCache(response);
            }else{
                if(PreferenceManager.getDefaultSharedPreferences(this).getString("JSONCache","") == null||
                        PreferenceManager.getDefaultSharedPreferences(this).getString("TimestampCache","") == null){
                    saveCurrentToCache(response);
                }else{
                    JSONArray jsonCache = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString("JSONCache",""));
                    long cachedTimestamp = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(this).getString("TimestampCache",""));
                    Long currentTimestamp = System.currentTimeMillis()/1000;
                    if(currentTimestamp - cachedTimestamp > 7200 && !jsonCache.equals(response)){
                        saveCurrentToCache(response);
                    }
                }
            }
        }catch(JSONException e){
            Log.e("Cache fetch error",e.toString());
        }
    }

    private void saveCurrentToCache(JSONArray currentResponse){
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("JSONCache",currentResponse.toString()).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("TimestampCache",String.valueOf(System.currentTimeMillis()/1000)).apply();
    }

    private void checkAndFetchCache(){
        if(!PreferenceManager.getDefaultSharedPreferences(this).contains("JSONCache") || !PreferenceManager.getDefaultSharedPreferences(this).contains("TimestampCache")){
            noInternetLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(View.GONE);
            shimmerFrameLayout.stopShimmerAnimation();
        }else{
            if(PreferenceManager.getDefaultSharedPreferences(this).getString("JSONCache","") == null||
                    PreferenceManager.getDefaultSharedPreferences(this).getString("TimestampCache","") == null){
                noInternetLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmerAnimation();
            }else{
                try{
                    JSONArray jsonCache = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString("JSONCache",""));
                    setRepositoryData(jsonCache);
                }catch(JSONException e){
                    Log.e("Cache fetch error",e.toString());
                }
            }
        }
    }
}
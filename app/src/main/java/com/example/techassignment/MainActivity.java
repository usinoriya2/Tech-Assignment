package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.techassignment.Models.Repository;
import com.example.techassignment.Utilities.ApiClient;
import com.example.techassignment.Utilities.ApiInterface;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout pullToRefresh;
    private RelativeLayout noInternetLayout;
    private LinearLayout repoLayout;
    public static int jsonArraySize = 0;
    private static final String cacheName = "JSONCache";
    private static final String timeStampCache = "TimestampCache";
    private static String REPO_URL = "https://api.github.com/search/repositories?q=android&per_page=50&sort=stars&page=1&order=desc&since=daily";
    private Button retryButton;
    private Handler handler;
    private Runnable r;
    private JsonObjectRequest mJsonObjectRequest;
    private RequestQueue mRequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        getRepositoryData();
    }

    private void bindViews(){
        shimmerFrameLayout = findViewById(R.id.shimmer_frame_layout);
        repoLayout = findViewById(R.id.repo_layout);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        retryButton = findViewById(R.id.retry_button);
        pullToRefresh = findViewById(R.id.pull_to_refresh);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getRepositoryData();
                pullToRefresh.setRefreshing(false);
            }
        });

//        retryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getRepositoryData();
//            }
//        });

    }

    private void getRepositoryData(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Repository>> call = apiInterface.getRepositoryList();
        call.enqueue(new Callback<List<Repository>>() {

            @Override
            public void onResponse(Call<List<Repository>> call, retrofit2.Response<List<Repository>> response) {
                Log.v("response",response.body().toString());
                List<Repository> repositoryList = response.body();
                for(Repository repository: repositoryList){
                    makeRepositoryLayout(repoLayout,repository);
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                if(t instanceof NetworkError){

                }else{

                }
                Log.v("error",t.toString());
            }
        });

    }

//    private void getRepositoryData(){
//        noInternetLayout.setVisibility(View.GONE);
//        shimmerFrameLayout.setVisibility(View.VISIBLE);
//        shimmerFrameLayout.startShimmerAnimation();
//        repoLayout.removeAllViews();
////        new JsonArrayRequest()
//        mJsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, REPO_URL,null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try{
//                    JSONArray jsonArray = (JSONArray) response.get("items");
//                    jsonArraySize = jsonArray.length();
//                    setRepositoryData(jsonArray);
//                    Cache.checkAndSaveCache(jsonArray, getApplicationContext(),cacheName,timeStampCache);
//                }catch(Exception e){
//
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.v("Fetch fail",error.toString());
//                checkAndFetchCache();
//            }
//        });
//
//        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
//        mRequestQueue.add(mJsonObjectRequest);
//    }

//    private void setRepositoryData(JSONArray response){
//        List<Repository> repositoryList = new ArrayList<>();
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                JSONObject jsonObject = response.getJSONObject(i);
//                Repository repository = new Repository(jsonObject.getString("name"),jsonObject.getString("full_name"),jsonObject.getJSONObject("owner").getString("avatar_url"),
//                        owner, jsonObject.getString("description"),jsonObject.getString("language"),"",
//                        jsonObject.getInt("stargazers_count"),jsonObject.getInt("forks"),0);
//                repositoryList.add(repository);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        for(Repository repository: repositoryList){
//            makeRepositoryLayout(repoLayout, repository);
//        }
//        shimmerFrameLayout.setVisibility(View.GONE);
//        shimmerFrameLayout.stopShimmerAnimation();
//    }

    private void makeRepositoryLayout( LinearLayout view, Repository repository){
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View repoView = inflater.inflate(R.layout.repository, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 10;
        repoView.setLayoutParams(lp);
        TextView author = repoView.findViewById(R.id.author);
        TextView repoName = repoView.findViewById(R.id.repo_name);
        TextView description = repoView.findViewById(R.id.description);
        ImageView avatar = repoView.findViewById(R.id.avatar);
        final LinearLayout descriptionLayout = repoView.findViewById(R.id.description_layout);
        CardView languageColor = repoView.findViewById(R.id.language_color);
        TextView language = repoView.findViewById(R.id.language);
        TextView stars = repoView.findViewById(R.id.stars);
        TextView forks = repoView.findViewById(R.id.forks);

        author.setText(repository.getName());
        repoName.setText(repository.getFullName());
        description.setText(repository.getDescription());
//        languageColor.setCardBackgroundColor(Color.parseColor(repository.getLanguageColor()));
//        if(repository.getLanguage() != null && !repository.getLanguage().equals("null")){
//            language.setText(repository.getLanguage());
//        }else{
//            language.setText(R.string.not_available);
//        }

//        stars.setText(String.valueOf(repository.getS()));
//        forks.setText(String.valueOf(repository.getForks()));

        RequestOptions requestOptions = new RequestOptions().override(300, 300);
//        Glide.with(getApplicationContext()).asBitmap().apply(requestOptions).load(repository.getOwner().getAvatarUrl()).into(avatar);

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
//                    setRepositoryData(jsonCache);
                }catch(JSONException e){
                    Log.e("Cache fetch error",e.toString());
                }
            }
        }
    }

}
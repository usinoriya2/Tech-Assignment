package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.example.techassignment.Adapters.RepositoryAdapter;
import com.example.techassignment.Models.Repository;
import com.example.techassignment.Utilities.ApiClient;
import com.example.techassignment.Utilities.ApiInterface;
import com.example.techassignment.Utilities.Cache;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private RecyclerView recyclerView;
    private RepositoryAdapter repositoryAdapter;
    private static final String cacheName = "RepositoryDataCache";
    private static final String timeStampCache = "TimestampCache";
    private static String REPO_URL = "https://api.github.com/search/repositories?q=android&per_page=50&sort=stars&page=1&order=desc&since=daily";
    private Button retryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        init();
        getRepositoryData();
    }

    private void bindViews(){
        shimmerFrameLayout = findViewById(R.id.shimmer_frame_layout);
        recyclerView = findViewById(R.id.recycler_view);
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

    private void init(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void getRepositoryData(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        if(repositoryAdapter!=null){
            repositoryAdapter.clear();
        }
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<List<Repository>> call = apiInterface.getRepositoryList();
        call.enqueue(new Callback<List<Repository>>() {

            @Override
            public void onResponse(Call<List<Repository>> call, retrofit2.Response<List<Repository>> response) {
                Log.v("response",response.body().toString());
                List<Repository> repositoryList = response.body();
                setRecyclerView(repositoryList);
                Cache.checkAndSaveCache(repositoryList, getApplicationContext(), cacheName, timeStampCache);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
//                if(t instanceof NetworkError){
//                    setRecyclerView(null);
//                }else{
//                    noInternetLayout.setVisibility(View.VISIBLE);
//                    shimmerFrameLayout.setVisibility(View.GONE);
//                    shimmerFrameLayout.stopShimmerAnimation();
//                }
                Log.v("error",t.toString());
                setRecyclerView(null);
            }
        });

    }

    private void setRecyclerView(List<Repository> repositoryList){
        if(repositoryList == null){
            if(Cache.isCachePresent(getApplicationContext(),cacheName,timeStampCache)){
                try{
                    String cachedData = Cache.fetchCache(getApplicationContext(), cacheName);
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Repository> repositoryListCache = objectMapper.readValue(cachedData, new TypeReference<List<Repository>>(){});
                    bindRecyclerView(repositoryListCache);
                }catch(Exception e){
                    Log.v("object mapper exception",e.toString());
                }
            }else{
                noInternetLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmerAnimation();
            }
        }else{
            bindRecyclerView(repositoryList);
        }
    }

    private void bindRecyclerView(List<Repository> repositoryList){
        repositoryAdapter = new RepositoryAdapter(repositoryList, getApplicationContext());
//        repositoryAdapter.setItemClickListener(itemCLickListener);
        recyclerView.setAdapter(repositoryAdapter);
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
    }

}
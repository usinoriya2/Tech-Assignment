package com.example.techassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.NetworkError;
import com.example.techassignment.Adapters.RepositoryAdapter;
import com.example.techassignment.Models.Repository;
import com.example.techassignment.Models.RepositoryData;
import com.example.techassignment.Utilities.ApiClient;
import com.example.techassignment.Utilities.ApiInterface;
import com.example.techassignment.Utilities.CacheUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout pullToRefresh;
    private RelativeLayout noInternetLayout;
    private RecyclerView recyclerView;
    private RepositoryAdapter repositoryAdapter;
    private List<Repository> repositoryList;
    private static final String cacheName = "RepositoryDataCache";
    private static final String timeStampCache = "TimestampCache";
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
        ApiInterface apiInterface = ApiClient.getClient();

        Call<RepositoryData> call = apiInterface.getRepositoryList("android","50","stars","1","desc","daily");
        call.enqueue(new Callback<RepositoryData>() {

            @Override
            public void onResponse(Call<RepositoryData> call, retrofit2.Response<RepositoryData> response) {
                Log.v("response",response.body().toString());
                repositoryList = response.body().getRepositoryList();
                setRecyclerView(false);
                CacheUtils.checkAndSaveCache(repositoryList, getApplicationContext(), cacheName, timeStampCache);
            }

            @Override
            public void onFailure(Call<RepositoryData> call, Throwable t) {
                if(t instanceof NetworkError || t instanceof UnknownHostException){
                    Log.v("Network Error",t.toString());
                    setRecyclerView(true);
                }else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmerAnimation();
                }

            }
        });

    }

    private void setRecyclerView( boolean isNetworkError){
        if(isNetworkError){
            if(CacheUtils.isCachePresent(getApplicationContext(),cacheName,timeStampCache)){
                try{
                    String cachedData = CacheUtils.fetchCache(getApplicationContext(), cacheName);
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
        recyclerView.setAdapter(repositoryAdapter);
        shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
    }

}
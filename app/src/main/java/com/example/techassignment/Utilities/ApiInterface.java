package com.example.techassignment.Utilities;

import com.example.techassignment.Models.Repository;
import com.example.techassignment.Models.RepositoryData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/search/repositories")
    Call<RepositoryData> getRepositoryList(@Query("q") String q, @Query("per_page") String perPage, @Query("sort") String sort,
                                           @Query("page") String page, @Query("order") String order, @Query("since") String since);
}

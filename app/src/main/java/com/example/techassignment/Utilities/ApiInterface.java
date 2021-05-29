package com.example.techassignment.Utilities;

import com.example.techassignment.Models.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/repositories?q=android&per_page=50&sort=stars&page=1&order=desc")
    Call<List<Repository>> getRepositoryList();
}

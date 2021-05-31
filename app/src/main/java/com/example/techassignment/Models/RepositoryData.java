package com.example.techassignment.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RepositoryData {

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("incomplete_results")
    private boolean incompleteResults;

    @SerializedName("items")
    private List<Repository> repositoryList;

    public RepositoryData(){

    }

    public RepositoryData(int totalCount, boolean incompleteResults, List<Repository> repositoryList) {
        this.totalCount = totalCount;
        this.incompleteResults = incompleteResults;
        this.repositoryList = repositoryList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<Repository> getRepositoryList() {
        return repositoryList;
    }

    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }
}

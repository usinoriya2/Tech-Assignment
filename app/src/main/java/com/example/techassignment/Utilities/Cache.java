package com.example.techassignment.Utilities;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.example.techassignment.Models.Repository;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class Cache {
    public static void checkAndSaveCache(List<Repository> repositoryList, Context context, String cacheName, String timestamp)  {
        try{
            if(!PreferenceManager.getDefaultSharedPreferences(context).contains(cacheName) || !PreferenceManager.getDefaultSharedPreferences(context).contains(timestamp)){
                saveCurrentToCache(repositoryList, context, cacheName, timestamp);
            }else{
                if(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,"") == null||
                        PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,"") == null){
                    saveCurrentToCache(repositoryList, context, cacheName, timestamp);
                }else{
                    JSONArray jsonCache = new JSONArray(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,""));
                    long cachedTimestamp = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,""));
                    Long currentTimestamp = System.currentTimeMillis()/1000;
                    if(currentTimestamp - cachedTimestamp > 7200 && !jsonCache.equals(repositoryList)){
                        saveCurrentToCache(repositoryList, context, cacheName, timestamp);
                    }
                }
            }
        }catch(JSONException e){
            Log.e("Cache fetch error",e.toString());
        }
    }

    public static void saveCurrentToCache(List<Repository> repositoryList, Context context, String cacheName, String timestamp){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(cacheName,repositoryList.toString()).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(timestamp,String.valueOf(System.currentTimeMillis()/1000)).apply();
    }

//    private void checkAndFetchCache(Context context, String cacheName, String timestamp){
//        if(!PreferenceManager.getDefaultSharedPreferences(context).contains(cacheName) || !PreferenceManager.getDefaultSharedPreferences(context).contains(timestamp)){
//            noInternetLayout.setVisibility(View.VISIBLE);
//            shimmerFrameLayout.setVisibility(View.GONE);
//            shimmerFrameLayout.stopShimmerAnimation();
//        }else{
//            if(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,"") == null||
//                    PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,"") == null){
//                noInternetLayout.setVisibility(View.VISIBLE);
//                shimmerFrameLayout.setVisibility(View.GONE);
//                shimmerFrameLayout.stopShimmerAnimation();
//            }else{
//                try{
//                    JSONArray jsonCache = new JSONArray(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,""));
//                    setRepositoryData(jsonCache);
//                }catch(JSONException e){
//                    Log.e("Cache fetch error",e.toString());
//                }
//            }
//        }
//    }
}

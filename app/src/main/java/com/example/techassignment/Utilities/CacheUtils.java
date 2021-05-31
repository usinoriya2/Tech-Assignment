package com.example.techassignment.Utilities;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.example.techassignment.Models.Repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class CacheUtils {
    public static void checkAndSaveCache(List<Repository> repositoryList, Context context, String cacheName, String timestamp)  {
        if(!PreferenceManager.getDefaultSharedPreferences(context).contains(cacheName) || !PreferenceManager.getDefaultSharedPreferences(context).contains(timestamp)){
            saveCurrentToCache(repositoryList, context, cacheName, timestamp);
        }else{
            if(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,"") == null||
                    PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,"") == null){
                saveCurrentToCache(repositoryList, context, cacheName, timestamp);
            }else{
                try{
                    ObjectMapper objectMapper = new ObjectMapper();
                    long cachedTimestamp = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,""));
                    Long currentTimestamp = System.currentTimeMillis()/1000;
                    if(currentTimestamp - cachedTimestamp > 7200 ){
                        saveCurrentToCache(repositoryList, context, cacheName, timestamp);
                    }
                }catch(Exception e){
                    Log.v("object mapper exception",e.toString());
                }
            }
        }
    }

    private static void saveCurrentToCache(List<Repository> repositoryList, Context context, String cacheName, String timestamp){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String repoListString = objectMapper.writeValueAsString(repositoryList);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString(cacheName,repoListString).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString(timestamp,String.valueOf(System.currentTimeMillis()/1000)).apply();
        }catch(Exception e){
            Log.v("mapper exception", e.toString());
        }

    }

    public static boolean isCachePresent(Context context, String cacheName, String timestamp){
        if(!PreferenceManager.getDefaultSharedPreferences(context).contains(cacheName) || !PreferenceManager.getDefaultSharedPreferences(context).contains(timestamp)){
            return false;
        }else{
            if(PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,"") == null||
                    PreferenceManager.getDefaultSharedPreferences(context).getString(timestamp,"") == null){
                return false;
            }else{
                return true;
            }
        }
    }

    public static String fetchCache(Context context, String cacheName){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(cacheName,"");
    }
}

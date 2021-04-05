package com.example.libnetwork;

public abstract class JsonCallback<T> {
    public void onSuccess(ApiResponse<T> response){

    }
    public void onError(ApiResponse<T> response){

    }
    public void OnCacheSuccess(ApiResponse<T> response){

    }
}

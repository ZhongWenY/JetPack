package com.example.myapplication;

import android.app.Application;
import android.app.ApplicationErrorReport;

import com.example.libnetwork.ApiService;

public class MyApplication extends Application {
    private static final String basUrl = "http://123.56.232.18:8080/serverdemo";
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init(basUrl, null);
    }
}

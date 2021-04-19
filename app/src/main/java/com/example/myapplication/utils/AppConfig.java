package com.example.myapplication.utils;

import android.app.Application;
import android.content.res.AssetManager;

import com.alibaba.fastjson.TypeReference;
import com.example.libcommon.AppGlobals;
import com.example.myapplication.model.BottomBar;
import com.example.myapplication.model.Destination;
import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AppConfig {
    private  static HashMap<String, Destination> sDestConfig;

    private static BottomBar sBottomBar;

    public static HashMap<String,Destination> getDestination(){
        if(sDestConfig==null){
            String content=parseFile("destination.json");
            sDestConfig=JSON.parseObject(content,new TypeReference<HashMap<String, Destination>>(){

            });
        }
        return sDestConfig;
    }
    public static BottomBar getBottomBar(){
        if(sBottomBar==null){
            String content=parseFile("main_tabs_config.json");
            sBottomBar =JSON.parseObject(content,BottomBar.class);
        }
        return sBottomBar;
    }
    public static String parseFile(String fileName){
        AssetManager assets=
                AppGlobals.getsApplication().getResources().getAssets();
        InputStream stream=null;
        BufferedReader reader=null;
        StringBuilder builder=new StringBuilder();
        try {
            stream=assets.open(fileName);
            reader=new BufferedReader(new InputStreamReader(stream));
            String line=null;
            while((line=reader.readLine())!=null){
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(stream!=null)
                    stream.close();
                if(reader!=null)
                    reader.close();
            }catch (Exception e){

            }
        }
        return builder.toString();
    }
}

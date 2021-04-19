package com.example.myapplication.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.JsonCallback;
import com.example.libnetwork.Request;
import com.example.myapplication.AbsViewModel;
import com.example.myapplication.model.Feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Feed> {

    private  volatile boolean withCache = true;
    private String mFeedType;
    private static final String TAG = "HomeViewModel";

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public void setFeedType(String feedType) {

        mFeedType = feedType;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            Log.e("homeviewmodel", "loadInitial: ");
            loadData(0, callback);
            withCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向后加载分页数据的
            Log.e("homeviewmodel", "loadAfter: ");
            Log.d(TAG,"page key"+params.key);
            loadData(params.key, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    private void loadData(int key, ItemKeyedDataSource.LoadCallback<Feed> callback) {

        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", 0)
                .addParam("feedId", key)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());
        if(withCache){
            request.cacheStratege(Request.CACHE_ONLY);
            request.excute(new JsonCallback<List<Feed>>() {
                @Override
                public void OnCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.d("OnCacheSuccess",":"+response.body.size());
                }
            });
        }
        try{
            Request netRequest = withCache?request.clone() : request;
            netRequest.cacheStratege(key==0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.excute();
            List<Feed> data = response.body == null?Collections.emptyList() : response.body;

            callback.onResult(data);

            if(key > 0){
                //通过LiveData发送数据告诉UI层，是否应该主动关闭上拉加载分页动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }
}

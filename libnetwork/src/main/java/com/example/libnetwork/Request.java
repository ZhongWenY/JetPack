package com.example.libnetwork;



import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.example.libnetwork.cache.Cache;
import com.example.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public abstract class Request<T,R extends Request> implements Cloneable {
    protected String mUrl;
    protected HashMap<String,String> headers = new HashMap<>();
    protected HashMap<String,Object> params = new HashMap<>();

    public static final int CACHE_ONLY = 1;
    public static final int CACHE_FIRST = 2;
    public static final int NET_ONLY = 3;
    public static final int NET_CACHE = 4;
    private String cacheKey ;
    protected Type mType;
    protected Class mClaz;

    public static final String TAG = "Request";
    private int mCacheStratege;

    public Request(String url){
        mUrl=url;
    }

    @IntDef({CACHE_ONLY,CACHE_FIRST,NET_ONLY,NET_CACHE})
    public @interface CacheStrategy{

    }

    public R addHeader(String key, String vaule){
        headers.put(key,vaule);
        return (R)this;
    }
    public R addParam(String key,Object value){
        try {
            Field field = value.getClass().getField("TYPE");
            Class claz = (Class)field.get(null);
            if(claz.isPrimitive()){
                params.put(key,value);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    public R cacheStratege(@CacheStrategy int cacheStratege){
        mCacheStratege = cacheStratege;
        return (R)this;
    }
    public R cacheKey(String key){
        this.cacheKey = key;
        return (R) this;
    }
    @SuppressLint("RestrictedApi")
    public void excute(JsonCallback<T> callback){
        if(mCacheStratege != NET_ONLY){
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if(callback != null&& response.body != null){
                        callback.OnCacheSuccess(response);
                    }
                }
            });
        }
        if(mCacheStratege != CACHE_ONLY ){
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> response = new ApiResponse<>();
                    response.message = e.getMessage();
                    callback.onError(response);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> apiResponse =parseResponse(response,callback);
                    if(apiResponse.success){
                        callback.onSuccess(apiResponse);
                    }else{
                        callback.onError(apiResponse);
                    }
                }
            });
        }

    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey)?generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.body = (T) cache;
        result.success = true;
        return result;
    }

    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                    Log.d(TAG, "parseResponse: mType");
                } else if (mClaz != null) {
                    result.body = (T) convert.convert(content, mClaz);
                    Log.d(TAG, "parseResponse: claz");
                } else {
                    Log.d(TAG, "parseResponse: 无法解析");
                }

            } else {
                message = content;
            }
        }catch (IOException e) {
            message = e.getMessage();
            success = false;
        }
        result.success = success;
        result.status = status;
        result.message = message;

        if(mCacheStratege!=NET_ONLY&&result.success&&result.body!=null&&result.body instanceof Serializable)
        {
            saveCache(result.body);
        }
        return  result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey)?generateCacheKey():cacheKey;
        CacheManager.save(key,body);
    }

    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }

    public R responseType(Type type){
        mType=type;
        return (R) this;
    }
    public R responseType(Class claz){
        mClaz=claz;
        return (R) this;
    }
    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return  call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder) ;

    private void addHeaders(okhttp3.Request.Builder builder) {
        for(Map.Entry<String,String> entry : headers.entrySet()){
            builder.addHeader(entry.getKey(),entry.getValue());
        }
    }

    public ApiResponse<T> excute(){
        if (mCacheStratege == CACHE_ONLY){
            return readCache();
        }
        ApiResponse<T> result = null;
        try {
            Response response = getCall().execute();
            Log.d(TAG,"response"+response.message());
            result = parseResponse(response, null);
        } catch (IOException e) {
            e.printStackTrace();
            if(result == null){
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }
        }
        return result;
    }

    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request<T, R>) super.clone();
    }
}

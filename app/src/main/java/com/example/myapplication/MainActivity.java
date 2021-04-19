package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONObject;
import com.example.libnetwork.ApiResponse;
import com.example.libnetwork.ApiService;
import com.example.libnetwork.GetRequest;
import com.example.libnetwork.JsonCallback;
import com.example.myapplication.utils.NavGraphBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private  static  NavController navController = null;
    private  static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavGraphBuilder.build(navController,this,fragment.getId());

        navView.setOnNavigationItemSelectedListener(this);

        ApiService.get("/user/query")
                .addParam("userId",0)
                .excute(new JsonCallback<Object>() {
                    @Override
                    public void onSuccess(ApiResponse<Object> response) {
                        super.onSuccess(response);
                        Log.d(TAG,"remote network success:"+response.body);
                    }

                    @Override
                    public void onError(ApiResponse<Object> response) {
                        super.onError(response);
                        Log.d(TAG,"remote network fail :"+response.message);
                    }
                });

//        GetRequest<JSONObject> request = new GetRequest<JSONObject>("http://www.imooc.com");
//        HandlerThread mHandlerThread = new HandlerThread("handleThread");
//        mHandlerThread.start();
//        Handler mHandler=new Handler(mHandlerThread.getLooper()){
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                request.excute(new JsonCallback<JSONObject>() {
//                    @Override
//                    public void onSuccess(ApiResponse<JSONObject> response) {
//                        Log.d(TAG,"response:"+ response.body);
//                        super.onSuccess(response);
//                    }
//                });
////                Log.d(TAG,"response:"+ (String)response.body);
//            }
//        };
//        mHandler.sendEmptyMessage(0);
//        request.excute(new JsonCallback<JSONObject>() {
//            @Override
//            public void onSuccess(ApiResponse<JSONObject> response) {
//                super.onSuccess(response);
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        navController.navigate(menuItem.getItemId());
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}
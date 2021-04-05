package com.example.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.model.BottomBar;
import com.example.myapplication.model.Destination;
import com.example.myapplication.utils.AppConfig;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.List;

public class AppBottomBar extends BottomNavigationView {

    private static String TAG="AppBottomBar";
    private static  int[] sIcons = new int[]{R.drawable.icon_tab_home,R.drawable.icon_tab_sofa
            ,R.drawable.icon_tab_publish,R.drawable.icon_tab_find,R.drawable.icon_tab_mine};
    public AppBottomBar(Context context) {
        this(context,null);
    }

    public AppBottomBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        BottomBar bottomBar = AppConfig.getBottomBar();
        List<BottomBar.Tabs> tabs = bottomBar.getTabs();

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(bottomBar.getActiveColor()),Color.parseColor(bottomBar.getInActiveColor())};
        ColorStateList colorStateList=new ColorStateList(states,colors);
        setItemTextColor(colorStateList);
        setItemIconTintList(colorStateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        setSelectedItemId(bottomBar.getSelectTab());

        Log.d(TAG,"Tabs 数量为"+tabs.size());
        for(int i=0; i<tabs.size(); i++){
            BottomBar.Tabs tab=tabs.get(i);
            Log.d(TAG, "AppBottomBar: enable "+tab.isEnable());
            if(!tab.isEnable()){
                return;
            }
            int id = getId(tab.getPageUrl());
            Log.d(TAG,"Tabs id为"+id);
            if(id<0) continue;
            MenuItem item = getMenu().add(0,id,tab.getIndex(),tab.getTitle());
            item.setIcon(sIcons[tab.getIndex()]);
        }

        for(int i=0; i<tabs.size(); i++){
            BottomBar.Tabs tab = tabs.get(i);
            int iconSize = dp2px(tab.getSize());

            BottomNavigationMenuView menuView = (BottomNavigationMenuView)getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView)menuView.getChildAt(tab.getIndex());
            itemView.setIconSize(iconSize);

            if(TextUtils.isEmpty(tab.getTitle())){
                int tintColor = TextUtils.isEmpty(tab.gettintColor()) ? Color.parseColor("#ff678f") : Color.parseColor(tab.gettintColor());
                itemView.setIconTintList(ColorStateList.valueOf(tintColor));
                //禁止掉点按时 上下浮动的效果
                itemView.setShifting(false);
            }
        }
    }

    private int dp2px(int size) {
        float value = getContext().getResources().getDisplayMetrics().density*size+0.5f;
        return (int)value;
    }

    private int getId(String pageUrl){
        Destination destination = AppConfig.getDestination().get(pageUrl);
        if(destination == null)
            return -1;
        return destination.getId();
    }
}

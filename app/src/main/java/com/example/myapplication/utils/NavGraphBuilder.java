package com.example.myapplication.utils;

import android.content.ComponentName;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.example.libcommon.AppGlobals;
import com.example.myapplication.navigator.FixFragmentNavigator;
import com.example.myapplication.model.Destination;

import java.util.HashMap;

public class NavGraphBuilder {
    public static void build(NavController controller, FragmentActivity activity, int containerId){
        NavigatorProvider provider=controller.getNavigatorProvider();

//        FragmentNavigator fragmentNavigator=provider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator=new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator=provider.getNavigator(ActivityNavigator.class);

        NavGraph navGraph=new NavGraph(new NavGraphNavigator(provider));

        HashMap<String, Destination> destConfig = AppConfig.getDestination();
        for(Destination value : destConfig.values()){
            if(value.isIsFragment()){
                FragmentNavigator.Destination destination=fragmentNavigator.createDestination();
                destination.setClassName(value.getClassName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());

                navGraph.addDestination(destination);
            }else {
                ActivityNavigator.Destination destination=activityNavigator.createDestination();
                destination.setComponentName(new ComponentName(AppGlobals.getsApplication().getPackageName(),value.getClassName()));
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());

                navGraph.addDestination(destination);
            }

            if(value.isAsStarter()){
                navGraph.setStartDestination(value.getId());
            }
        }

        controller.setGraph(navGraph);

    }
}

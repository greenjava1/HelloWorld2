package com.example.administrator.helloworld;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by Administrator on 2017/12/6.
 */

public class AppMy extends Application {
    private static final String TAG = "AppMy";
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        // 程序创建的时候执行
        context = getApplicationContext();
        Log.d(TAG, "onCreate");
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    public static Context getContext()
    {
        return context;
    }
}

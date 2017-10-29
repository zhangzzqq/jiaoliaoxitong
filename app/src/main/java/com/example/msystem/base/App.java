package com.example.msystem.base;

import android.app.Application;
import android.content.Context;

import com.example.msystem.utils.ACache;
import com.example.msystem.utils.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;


/**
 * Created by stevenZhang on 2017/7/14.
 */

public class App extends Application {

    public static ACache mCache;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        mCache = ACache.get(this);
        context = getApplicationContext();
        ToastUtils.getInstance().initContext(this);
        CrashReport.initCrashReport(getApplicationContext(), "900056286", false);
    }


    public static Context getContext(){


        return context;
    }


}

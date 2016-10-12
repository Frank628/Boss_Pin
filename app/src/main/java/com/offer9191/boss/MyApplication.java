package com.offer9191.boss;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by OfferJiShu01 on 2016/8/30.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        Fresco.initialize(this);
        ShareSDK.initSDK(this);
        CrashReport.initCrashReport(getApplicationContext(),"900054375",false);
        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }
}

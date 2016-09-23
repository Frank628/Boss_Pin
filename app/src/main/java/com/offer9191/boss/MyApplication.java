package com.offer9191.boss;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.xutils.x;

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
    }
}

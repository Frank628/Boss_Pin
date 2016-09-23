package com.offer9191.boss.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by OfferJiShu01 on 2016/9/2.
 */
public class BridgerWebView extends WebView {
    public BridgerWebView(Context context) {
        this(context,null);
    }

    public BridgerWebView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }




    private void init(Context context){
        this.getSettings().setJavaScriptEnabled(true);//支持js脚本
        this.getSettings().setLoadsImagesAutomatically(true);
        // 自适应屏幕
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        // 缩放
        this.getSettings().setSupportZoom(true);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDomStorageEnabled(true);
        String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath();
        this.getSettings().setAppCachePath(appCachePath);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAppCacheEnabled(true);
        this.clearCache(true);
        this.clearHistory();

    }
}

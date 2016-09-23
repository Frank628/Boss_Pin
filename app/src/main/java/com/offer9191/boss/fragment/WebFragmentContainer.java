package com.offer9191.boss.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.base.BaseFragment;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.widget.BridgerWebView;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/8/22.
 */

@ContentView(R.layout.web_container_withnav)
public class WebFragmentContainer extends BaseFragment {
    @ViewInject(R.id.webview)BridgerWebView webView;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    private  String url="";
    public static WebFragmentContainer newInstance(String murl){
        WebFragmentContainer webFragmentContainer =new WebFragmentContainer();
        Bundle args = new Bundle();
        args.putString("tag", murl);
        Log.i("newInstance",murl);
        webFragmentContainer.setArguments(args);
        return webFragmentContainer;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url= getArguments() != null ? getArguments().getString("tag") : "";
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("shouldOverrideUrl",url);
                Intent intent =new Intent(getActivity(), WebActivityContainer.class);
                intent.putExtra(Constants.IS_NEED_NAVIGATION,false);
                intent.putExtra("url",url);
                startActivity(intent);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(getActivity(),"onReceivedError",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Toast.makeText(getActivity(),"onReceivedHttpError",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Toast.makeText(getActivity(),"onReceivedSslError",Toast.LENGTH_SHORT).show();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                navigationLayout.setCenterText(title);
            }

        });

        webView.loadUrl(url);

    }


    @Override
    public void onStart() {
        super.onStart();
    }




}

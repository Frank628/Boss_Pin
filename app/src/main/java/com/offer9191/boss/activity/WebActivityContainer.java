package com.offer9191.boss.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.widget.BridgerWebView;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.web_container_black)
public class WebActivityContainer extends BaseActivity {
    @ViewInject(R.id.webview)BridgerWebView webView;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra(Constants.IS_NEED_NAVIGATION,false)){
            navigationLayout.setVisibility(View.VISIBLE);
            navigationLayout.setLeftText("", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }else{
            navigationLayout.setVisibility(View.GONE);
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("shouldOverrideUrl",url);
                if (url.startsWith("loginout:")){
                    final NormalDialog dialog = new NormalDialog(WebActivityContainer.this);
                    dialog.content("登录超时，请重新登录！")//
                            .btnNum(1)//
                            .titleTextSize(21)//
                            .btnText("确定")
                            .titleLineHeight(0)//
                            .showAnim(new BounceTopEnter())//
                            .dismissAnim(new SlideBottomExit())//
                            .show();

                    dialog.setOnBtnClickL(

                            new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    Intent intent =new Intent(WebActivityContainer.this, LoginActivity.class);
                                    startActivity(intent);
                                    WebActivityContainer.this.finish();
                                    dialog.dismiss();
                                }
                            });

                    return  true;
                }else{
                    return super.shouldOverrideUrlLoading(view,url);
                }


            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(WebActivityContainer.this,"onReceivedError",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Toast.makeText(WebActivityContainer.this,"onReceivedHttpError",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Toast.makeText(WebActivityContainer.this,"onReceivedSslError",Toast.LENGTH_SHORT).show();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                navigationLayout.setCenterText(title);
            }

        });
        webView.addJavascriptInterface(new JsInterface(), "control");
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
    public class JsInterface {
        @JavascriptInterface
        public void back() {
            WebActivityContainer.this.finish();
        }
    }
}

package com.offer9191.boss.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.SysApplication;
import com.offer9191.boss.activity.candidate.CandidateDetailActivity;
import com.offer9191.boss.activity.order.OrderDetailActivity;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.WebOrderDetailJson;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.BridgerWebView;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;
import com.wang.avi.AVLoadingIndicatorView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.web_container_black)
public class WebActivityContainer extends BaseActivity {
    @ViewInject(R.id.webview)BridgerWebView webView;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.avi)AVLoadingIndicatorView avi;
    private long mExitTime;
    private String currentUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avi.setIndicator("BallSpinFadeLoaderIndicator");
        currentUrl=getIntent().getStringExtra("url");
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
                currentUrl=url;
                Log.i("shouldOverrideUrl",url);
                if (url.startsWith("loginout:relogin")){
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
                }else if(url.startsWith("msgorder:detail?json=")){
                    String str[]=url.split("=");
                    String json="";
                    if (str.length>1){
                        json=str[1];
                    }
                    try {
                        WebOrderDetailJson webOrderDetailJson= GsonTools.changeGsonToBean(json,WebOrderDetailJson.class);
                        Intent intent= new Intent(WebActivityContainer.this, OrderDetailActivity.class);
                        intent.putExtra("url",Constants.WEB_URL+"BossApp/detail/positiondetail.html?jobid="+webOrderDetailJson.JobOrderID+"&sessionid="+ MyInfoManager.getSessionID(getApplicationContext()));
                        intent.putExtra("shareurl",Constants.WEB_URL+"BossApp/share/positiondetail.html?jobid="+webOrderDetailJson.JobOrderID+"&sessionid="+ MyInfoManager.getSessionID(getApplicationContext()));
                        intent.putExtra("status",webOrderDetailJson.JobOrderStatus);
                        intent.putExtra("CompanyInterviewJobId",webOrderDetailJson.CompanyInterviewJobId);
                        intent.putExtra("JobOrderID",webOrderDetailJson.JobOrderID);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    return true;
                }else if(url.startsWith("msgrecommend:detail?userid=")){
                    String str[]=url.split("=");
                    String id="";
                    if (str.length>1){
                        id=str[1];
                    }
                    Intent intent =new Intent(WebActivityContainer.this, CandidateDetailActivity.class);
                    intent.putExtra("url",Constants.WEB_URL+"BossApp/detail/userdetail.html?userid="+id+"&sessionid="+ MyInfoManager.getSessionID(WebActivityContainer.this));
                    intent.putExtra("shareurl",Constants.WEB_URL+"BossApp/share/userdetail.html?userid="+id+"&sessionid="+ MyInfoManager.getSessionID(WebActivityContainer.this));
                    intent.putExtra("candidateId",id);
                    startActivity(intent);
                    return true;
                }else if (url.startsWith("loginout:loginout")){
                    final NormalDialog dialog = new NormalDialog(WebActivityContainer.this);
                    dialog.content("确认退出当前账号？")//
                            .style(NormalDialog.STYLE_TWO)//
                            .titleTextSize(18)//
                            .title("退出")
                            .showAnim(new BounceTopEnter())//
                            .dismissAnim(new SlideBottomExit())//
                            .show();

                    dialog.setOnBtnClickL(
                            new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {

                                    dialog.dismiss();
                                }
                            },
                            new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    dialog.dismiss();
                                    Intent intent =new Intent(WebActivityContainer.this, LoginActivity.class);
                                    startActivity(intent);
                                    WebActivityContainer.this.finish();

                                }
                            });
                    return  true;
                }else if(url.startsWith("loginout:exit")){
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Object mHelperUtils;
                        Toast.makeText(WebActivityContainer.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    } else {
                        SysApplication.getInstance().exit();
                    }
                    return true;
                }else if(url.startsWith("share:job?url")){
                    String string=url.trim().substring("share:job?url=".length(),url.trim().length());
                    CommUtils.share("Boss聘","BOSS聘-聘精英",string,WebActivityContainer.this);
                    return true;
                }else if(url.startsWith("share:user?url")){
                    String string=url.trim().substring("share:user?url=".length(),url.trim().length());
                    CommUtils.share("Boss聘","BOSS聘-精英推荐",string,WebActivityContainer.this);
                    return true;
                }else{
                    avi.setVisibility(View.VISIBLE);
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
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress==100){
                    avi.setVisibility(View.GONE);
                }
            }
        });
        webView.addJavascriptInterface(new JsInterface(), "control");
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.reload();
    }

    public class JsInterface {
        @JavascriptInterface
        public void back() {
            WebActivityContainer.this.finish();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (currentUrl.contains("BossApp/manager/index.html")) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    SysApplication.getInstance().exit();
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }else{
            webView.loadUrl("javascript:pageback()");
            return true;
        }

    }
}

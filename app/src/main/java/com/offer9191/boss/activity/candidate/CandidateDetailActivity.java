package com.offer9191.boss.activity.candidate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.widget.BridgerWebView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/9/23.
 */
@ContentView(R.layout.activity_candidate_detail)
public class CandidateDetailActivity extends BaseActivity {
    @ViewInject(R.id.webview)BridgerWebView webView;
    @ViewInject(R.id.tv_center)TextView tv_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColor(this,R.drawable.hxr_status,false);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("shouldOverrideUrl",url);
                if (url.startsWith("loginout:")){
                    return  true;
                }else{
                    return super.shouldOverrideUrlLoading(view,url);
                }


            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        });
        Log.i("url",getIntent().getStringExtra("url"));
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
    @Event(R.id.tv_right)
    private void shareClick(View view){
        CommUtils.share("Boss聘","aaa","aaa",getIntent().getStringExtra("url"),"",this);
    }
    @Event(R.id.tv_right2)
    private void editClick(View view){
        Intent intent =new Intent(CandidateDetailActivity.this, AddCandidateActivity.class);
        intent.putExtra("name",getResources().getString(R.string.edit_candidate));
        intent.putExtra("candidateId",getIntent().getStringExtra("candidateId"));
        startActivity(intent);
    }
    @Event(R.id.tv_left)
    private void back(View view){
        onBackPressed();
    }
}

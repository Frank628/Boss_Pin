package com.offer9191.boss.activity.order;

import android.content.Intent;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.BridgerWebView;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;
import com.offer9191.boss.widget.progressbutton.iml.ActionProcessButton;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by OfferJiShu01 on 2016/9/23.
 */
@ContentView(R.layout.activity_order_detail)
public class OrderDetailActivity extends BaseActivity {
    @ViewInject(R.id.webview)BridgerWebView webView;
    @ViewInject(R.id.tv_center)TextView tv_center;
    @ViewInject(R.id.ll_bottom)LinearLayout ll_bottom;
    @ViewInject(R.id.btn_submit)ActionProcessButton btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_center.setText("订单详情");
        if (CommUtils.getOrderStatus(getIntent().getStringExtra("status")).equals("已承接")){
            ll_bottom.setVisibility(View.INVISIBLE);
            btn_submit.setVisibility(View.VISIBLE);
        }else if(CommUtils.getOrderStatus(getIntent().getStringExtra("status")).equals("未处理")){
            ll_bottom.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.INVISIBLE);
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("shouldOverrideUrl",url);
                if (url.startsWith("loginout:")){
                    return  true;
                }else if(url.contains("jobUserlist.html")){
                    Intent intent =new Intent(OrderDetailActivity.this,WebActivityContainer.class);
                    intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    return true;
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

    @Override
    protected void onResume() {
        super.onResume();
        webView.reload();
    }

    @Event(R.id.tv_right)
    private void shareClick(View view){
        CommUtils.share("Boss聘","BOSS聘-聘精英",getIntent().getStringExtra("shareurl"),this);
    }
    @Event(R.id.tv_left)
    private void back(View view){
       onBackPressed();
    }
    @Event(R.id.btn_submit)
    private void addCandidate(View view){
        Intent intent =new Intent(this, AlreadyRecommendedCandidatesActivity.class);
        intent.putExtra("orderId",getIntent().getStringExtra("JobOrderID"));
        intent.putExtra("companyInterviewJobId",getIntent().getStringExtra("CompanyInterviewJobId"));
        startActivity(intent);
    }
    @Event(R.id.tv_refuse)
    private void refuse(View view){
        final NormalDialog dialog = new NormalDialog(OrderDetailActivity.this);
        dialog.content(getString(R.string.notice_refuse_order))//
                .contentTextColor(Color.parseColor("#666666"))
                .isTitleShow(true)
                .title(getString(R.string.is_refuse_order))
                .titleTextSize(18)
                .titleTextColor(Color.parseColor("#333333"))
                .style(NormalDialog.STYLE_TWO)//
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
                        refuseOrder(getIntent().getStringExtra("JobOrderID"));
                        dialog.dismiss();
                    }
                });

    }
    @Event(R.id.tv_undertake)
    private void tv_undertake(View view){
        undertakeOrder(getIntent().getStringExtra("JobOrderID"));
    }
    private void undertakeOrder(String orderId){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/AcceptJobOrder");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(OrderDetailActivity.this));
        params.addBodyParameter("jobOrderId",orderId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AcceptJobOrder",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        ll_bottom.setVisibility(View.INVISIBLE);
                        btn_submit.setVisibility(View.VISIBLE);
                        webView.reload();
                    }else{
                        Toast.makeText(OrderDetailActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                dismissProgressDialog();
            }
        });
    }
    private void refuseOrder(String orderId){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/RejectJobOrder");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(OrderDetailActivity.this));
        params.addBodyParameter("jobOrderId",orderId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("RejectJobOrder",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        OrderDetailActivity.this.finish();
                    }else{
                        Toast.makeText(OrderDetailActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                dismissProgressDialog();
            }
        });
    }
}

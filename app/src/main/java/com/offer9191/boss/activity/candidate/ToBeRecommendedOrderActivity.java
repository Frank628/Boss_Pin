package com.offer9191.boss.activity.candidate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.order.OrderFilterActivity;
import com.offer9191.boss.activity.order.ToBeRecommendedCandidateFilterActivity;
import com.offer9191.boss.activity.order.ToBeRecommendedCandidatesActivity;
import com.offer9191.boss.adapter.MultiCandidateSelectAdapter;
import com.offer9191.boss.adapter.MultiOrderSelectAdapter;
import com.offer9191.boss.adapter.baseadpater.CommonAdapter;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.jsonbean.JobOrderListJson;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.LoadMoreListView;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.progressbutton.iml.ActionProcessButton;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by OfferJiShu01 on 2016/9/22.
 */
@ContentView(R.layout.activity_toberecommendedorders)
public class ToBeRecommendedOrderActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.rotate_header_list_view_frame)PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.rotate_header_list_view)LoadMoreListView lv;
    @ViewInject(R.id.btn_submit)ActionProcessButton btn_submit;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @ViewInject(R.id.ib_delete)ImageButton ib_delete;
    private int pageindex=0, pagesize=20;
    private String JobOrderStatus=CommUtils.getOrderStatusCode("已承接")+","+CommUtils.getOrderStatusCode("未处理"),JobTypeCode="",key="",orderIds="",companyInterviewJobIds="",candidateId="";
    private List<CityJson.DistrictsOne> zhinenglist;
    private static final int FILTER_REQUEST=2121;
    private List<JobOrderListJson.JobOrderOne> jobList=new ArrayList<JobOrderListJson.JobOrderOne>();
    private MultiOrderSelectAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        candidateId=getIntent().getStringExtra("CandidateID");
        navigationLayout.setCenterText(getResources().getString(R.string.select_orders));
        navigationLayout.setRightText(getResources().getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ToBeRecommendedOrderActivity.this, OrderFilterActivity.class);
                intent.putExtra("zhineng", (Serializable)zhinenglist);
                intent.putExtra("status", JobOrderStatus);
                startActivityForResult(intent,FILTER_REQUEST);
            }
        });
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageindex=0;
                getTobeOrders(pageindex);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
        adapter =new MultiOrderSelectAdapter(jobList, ToBeRecommendedOrderActivity.this, new MultiOrderSelectAdapter.OnMultiSelected() {
            @Override
            public void onSelected(List<String> list,List<String> list2) {
                orderIds= CommUtils.getStringfromList(list);
                companyInterviewJobIds= CommUtils.getStringfromList(list2);
            }
        });
        lv.setAdapter(adapter);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTobeOrders(pageindex);
            }
        });
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_content.setText("");
            }
        });
        edt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    ib_delete.setVisibility(View.VISIBLE);
                }else{
                    ib_delete.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(edt_content.getText().toString().trim())){
                    refresh();
                    InputMethodManager imm = (InputMethodManager) ToBeRecommendedOrderActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                }

            }
        });
        edt_content .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    refresh();
                    InputMethodManager imm = (InputMethodManager) ToBeRecommendedOrderActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
    }

    @Event(value = R.id.btn_submit)
    private void recommendCandidateClick(View view){
        recommendOrders(candidateId,orderIds,companyInterviewJobIds);
    }
    private void getTobeOrders(int pagenum){
        pagenum++;
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/GetJobOrderList");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ToBeRecommendedOrderActivity.this));
        params.addBodyParameter("JobTypeCode",JobTypeCode);
        params.addBodyParameter("candidateID",candidateId);
        params.addBodyParameter("key", edt_content.getText().toString().trim());
        params.addBodyParameter("JobOrderStatus",JobOrderStatus );
        params.addBodyParameter("pageindex", pagenum+"");
        params.addBodyParameter("pagesize", pagesize+"");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetJobOrderList",result);
                processOrderlist(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mPtrFrame.refreshComplete();
            }
            @Override
            public void onCancelled(CancelledException cex) {
                mPtrFrame.refreshComplete();
            }
            @Override
            public void onFinished() {  mPtrFrame.refreshComplete();}
        });
    }
    private void processOrderlist(String json){
        try {
            JobOrderListJson jobOrderListJson = GsonTools.changeGsonToBean(json,JobOrderListJson.class);
            if (jobOrderListJson.code==0){
                lv.setTotalNum(jobOrderListJson.data.total);
                pageindex++;
                if (pageindex==1){
                    jobList.clear();
                    jobList.addAll(jobOrderListJson.data.jobList);
                }else{
                    for (int i = 0; i < jobOrderListJson.data.jobList.size(); i++) {
                        jobList.add( jobOrderListJson.data.jobList.get(i));
                    }
                }
                if (adapter!=null) {
                    adapter.notifyDataSetChanged();
                }

            }else{
                Toast.makeText(ToBeRecommendedOrderActivity.this,jobOrderListJson.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void recommendOrders(String candidateId,String orderIds,String companyInterviewJobIds){
        if (TextUtils.isEmpty(orderIds)){
            Toast.makeText(ToBeRecommendedOrderActivity.this,getString(R.string.no_select_orders),Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/AddCandidateRecommendBycandidateId");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ToBeRecommendedOrderActivity.this));
        params.addBodyParameter("candidateId", candidateId);
        params.addBodyParameter("jobOrderIds", orderIds);
        params.addBodyParameter("companyInterviewJobIds", companyInterviewJobIds);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AddCandidateRe",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        Toast.makeText(ToBeRecommendedOrderActivity.this,getString(R.string.recommend_candidate_success),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ToBeRecommendedOrderActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() { dismissProgressDialog(); }
        });
    }
    private void refresh(){
        jobList.clear();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
        mPtrFrame.autoRefresh();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case FILTER_REQUEST:
                    JobOrderStatus=data.getStringExtra("status");
                    JobTypeCode=data.getStringExtra("position");
                    if (TextUtils.isEmpty(JobOrderStatus)){
                        JobOrderStatus =CommUtils.getOrderStatusCode("已承接")+","+CommUtils.getOrderStatusCode("未处理");
                    }
                    zhinenglist=(List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    refresh();
                    break;
            }
        }
    }
}

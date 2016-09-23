package com.offer9191.boss.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import com.offer9191.boss.adapter.baseadpater.CommonAdapter;
import com.offer9191.boss.adapter.baseadpater.ViewHolder;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CandidateListJson;
import com.offer9191.boss.jsonbean.RecommendedCandidateListJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.LoadMoreListView;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by OfferJiShu01 on 2016/9/20.
 */
@ContentView(R.layout.activity_alreadyrecommendedcandidates)
public class AlreadyRecommendedCandidatesActivity extends BaseActivity {
    private int pageindex=0, pagesize=20;
    private String orderId="",companyInterviewJobId="";
    private List<RecommendedCandidateListJson.RecommendedCandidateOne> candidateList=new ArrayList<>();
    CommonAdapter<RecommendedCandidateListJson.RecommendedCandidateOne> adapter;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.rotate_header_list_view_frame)PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.rotate_header_list_view)LoadMoreListView lv;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @ViewInject(R.id.ib_delete)ImageButton ib_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderId=getIntent().getStringExtra("orderId");
        companyInterviewJobId=getIntent().getStringExtra("companyInterviewJobId");
        navigationLayout.setCenterText(getResources().getString(R.string.already_recommended_candidates));
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
                getAlreadyCandidates(pageindex,orderId);
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
        adapter=new CommonAdapter<RecommendedCandidateListJson.RecommendedCandidateOne>(this,candidateList,R.layout.item_recommended_candidate) {
            @Override
            public void convert(ViewHolder helper, RecommendedCandidateListJson.RecommendedCandidateOne item, int position) {
                helper.setText(R.id.tv_name,item.CandidateName);
                helper.setText(R.id.tv_position,item.CandidatePosition);
                helper.setText(R.id.tv_industry,item.VocationNames);
                helper.setText(R.id.tv_phone,item.CandidateMobile);
                helper.setText(R.id.tv_cvstatus, CommUtils.getCandidateStatus(item.JobCandidateStatus));
            }
        };
        lv.setAdapter(adapter);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getAlreadyCandidates(pageindex,orderId);
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
                refresh();
                InputMethodManager imm = (InputMethodManager) AlreadyRecommendedCandidatesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
            }
        });
        edt_content .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    refresh();
                    InputMethodManager imm = (InputMethodManager) AlreadyRecommendedCandidatesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    private void getAlreadyCandidates(int pagenum, String orderId){
        pagenum++;
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/GetCandidateRecommendList");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(AlreadyRecommendedCandidatesActivity.this));
        params.addBodyParameter("jobOrderId",orderId);
        params.addBodyParameter("key",edt_content.getText().toString().trim());
        params.addBodyParameter("pageindex", pagenum+"");
        params.addBodyParameter("pagesize", pagesize+"");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetCandidateRec",result);
                processAlreadyCandidates(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() { mPtrFrame.refreshComplete(); }
        });
    }
    private void processAlreadyCandidates(String json){
        try {
            RecommendedCandidateListJson recommendedCandidateListJson = GsonTools.changeGsonToBean(json,RecommendedCandidateListJson.class);
            if (recommendedCandidateListJson.code==0){
                lv.setTotalNum(recommendedCandidateListJson.data.total);
                pageindex++;
                if (pageindex==1){
                    candidateList.clear();
                    candidateList.addAll(recommendedCandidateListJson.data.candidateRecommendList);
                }else{
                    for (int i = 0; i < recommendedCandidateListJson.data.candidateRecommendList.size(); i++) {
                        candidateList.add( recommendedCandidateListJson.data.candidateRecommendList.get(i));
                    }
                }
                if (adapter!=null) {
                    adapter.notifyDataSetChanged();
                }
            }else{
                Toast.makeText(AlreadyRecommendedCandidatesActivity.this,recommendedCandidateListJson.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void refresh(){
        candidateList.clear();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
        mPtrFrame.autoRefresh();
    }
    @Event(value = R.id.btn_submit)
    private void addcandidate(View view){
        Intent intent=new Intent(this,ToBeRecommendedCandidatesActivity.class);
        intent.putExtra("orderId",orderId);
        intent.putExtra("companyInterviewJobId",companyInterviewJobId);
        startActivity(intent);
    }
}

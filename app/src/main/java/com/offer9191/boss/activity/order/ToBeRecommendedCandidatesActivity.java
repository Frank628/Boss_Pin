package com.offer9191.boss.activity.order;

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
import com.offer9191.boss.activity.candidate.AddCandidateActivity;
import com.offer9191.boss.activity.candidate.CandidateFilterActivity;
import com.offer9191.boss.adapter.MultiCandidateSelectAdapter;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CandidateListJson;
import com.offer9191.boss.jsonbean.CityJson;
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
 * Created by OfferJiShu01 on 2016/9/20.
 */
@ContentView(R.layout.activity_toberecommendedcandidates)
public class ToBeRecommendedCandidatesActivity extends BaseActivity{
    private int pageindex=0, pagesize=20;
    private String orderId="",candidateId="",companyInterviewJobId="";
    private List<CandidateListJson.CandidateOne> candidateList=new ArrayList<>();
    MultiCandidateSelectAdapter adapter;
    private  static final int FILTER_REQUEST=1110;
    private String candidateGender="",ageFrom="",ageTo="",jobTypeCodes="";
    private List<CityJson.DistrictsOne> zhinenglist;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.rotate_header_list_view_frame)PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.rotate_header_list_view)LoadMoreListView lv;
    @ViewInject(R.id.btn_submit) ActionProcessButton btn_submit;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @ViewInject(R.id.ib_delete)ImageButton ib_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderId=getIntent().getStringExtra("orderId");
        companyInterviewJobId=getIntent().getStringExtra("companyInterviewJobId");
        navigationLayout.setCenterText(getResources().getString(R.string.select_candidate));
        navigationLayout.setRightText(getResources().getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ToBeRecommendedCandidatesActivity.this, ToBeRecommendedCandidateFilterActivity.class);
                intent.putExtra("zhineng", (Serializable)zhinenglist);
                intent.putExtra("gender", candidateGender);
                intent.putExtra("ageFrom", ageFrom);
                intent.putExtra("ageTo", ageTo);
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
                getTobeCandidates(pageindex,orderId);
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
        adapter =new MultiCandidateSelectAdapter(candidateList, ToBeRecommendedCandidatesActivity.this.getApplicationContext(), new MultiCandidateSelectAdapter.OnMultiSelected() {
            @Override
            public void onSelected(List<String> list) {
                candidateId=CommUtils.getStringfromList(list);
            }
        });
        lv.setAdapter(adapter);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getTobeCandidates(pageindex,orderId);
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
                    InputMethodManager imm = (InputMethodManager) ToBeRecommendedCandidatesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                }
            }
        });
        edt_content .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    refresh();
                    InputMethodManager imm = (InputMethodManager) ToBeRecommendedCandidatesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
    }
    private void getTobeCandidates(int pagenum,String orderId){
        pagenum++;
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/GetMyCandidateList");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ToBeRecommendedCandidatesActivity.this));
        params.addBodyParameter("candidateStatus", CommUtils.getCVStatusCode("已通过"));
        params.addBodyParameter("jobOrderID", orderId);
        params.addBodyParameter("key", edt_content.getText().toString());
        params.addBodyParameter("candidateGender", candidateGender);
        params.addBodyParameter("jobTypeCodes", jobTypeCodes);
        params.addBodyParameter("ageTo", ageTo);
        params.addBodyParameter("ageFrom", ageFrom);
        params.addBodyParameter("pageindex", pagenum+"");
        params.addBodyParameter("pagesize", pagesize+"");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetMyCandidate",result);
                processTobeCandidates(result);
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

    private void processTobeCandidates(String json){
        try {
            CandidateListJson candidateListJson = GsonTools.changeGsonToBean(json,CandidateListJson.class);
            if (candidateListJson.code==0){
                lv.setTotalNum(candidateListJson.data.total);
                pageindex++;
                if (pageindex==1){
                    candidateList.clear();
                    candidateList.addAll(candidateListJson.data.candidateList);
                }else{
                    for (int i = 0; i < candidateListJson.data.candidateList.size(); i++) {
                        candidateList.add( candidateListJson.data.candidateList.get(i));
                    }
                }
                if (adapter!=null) {
                    adapter.notifyDataSetChanged();
                }
            }else{
                Toast.makeText(ToBeRecommendedCandidatesActivity.this,candidateListJson.msg,Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void recommendCandidate(String candidateId,final String orderId,String companyInterviewJobId){
        if (TextUtils.isEmpty(candidateId)){
            Toast.makeText(ToBeRecommendedCandidatesActivity.this,getString(R.string.no_select_candidate),Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/AddCandidateRecommend");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ToBeRecommendedCandidatesActivity.this));
        params.addBodyParameter("candidateIds", candidateId);
        params.addBodyParameter("jobOrderId", orderId);
        params.addBodyParameter("companyInterviewJobId", companyInterviewJobId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AddCandidateRe",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        pageindex=0;
                        getTobeCandidates(pageindex,orderId);
                        Toast.makeText(ToBeRecommendedCandidatesActivity.this,getString(R.string.recommend_candidate_success),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ToBeRecommendedCandidatesActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
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
    @Event(value = R.id.btn_submit)
    private void recommendCandidateClick(View view){
        recommendCandidate(candidateId,orderId,companyInterviewJobId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case FILTER_REQUEST:
                    jobTypeCodes=data.getStringExtra("position");
                    ageFrom=data.getStringExtra("ageFrom");
                    ageTo=data.getStringExtra("ageTo");
                    candidateGender=data.getStringExtra("gender");
                    zhinenglist=(List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    refresh();
                    break;
            }
        }
    }
    private void refresh(){
        candidateList.clear();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
        mPtrFrame.autoRefresh();
    }
}

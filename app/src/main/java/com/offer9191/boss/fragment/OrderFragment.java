package com.offer9191.boss.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.my.ActivitySetting;
import com.offer9191.boss.activity.order.AlreadyRecommendedCandidatesActivity;
import com.offer9191.boss.activity.order.OrderDetailActivity;
import com.offer9191.boss.activity.order.OrderFilterActivity;
import com.offer9191.boss.adapter.baseadpater.CommonAdapter;
import com.offer9191.boss.adapter.baseadpater.ViewHolder;
import com.offer9191.boss.base.BaseFragment;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.jsonbean.JobOrderListJson;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.LoadMoreListView;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;

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
 * Created by OfferJiShu01 on 2016/9/7.
 */
@ContentView(R.layout.fragment_order)
public class OrderFragment extends BaseFragment {
    private static final int FILTER_REQUEST=2121;
    private int pageindex=0, pagesize=20;
    private String JobOrderStatus=CommUtils.getOrderStatusCode("已承接")+","+CommUtils.getOrderStatusCode("未处理"),JobTypeCode="",key="";
    private List<CityJson.DistrictsOne> zhinenglist;
    @ViewInject(R.id.rotate_header_list_view_frame)PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.rotate_header_list_view)LoadMoreListView lv;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @ViewInject(R.id.ib_delete)ImageButton ib_delete;
    private List<JobOrderListJson.JobOrderOne> jobList=new ArrayList<JobOrderListJson.JobOrderOne>();
    private CommonAdapter<JobOrderListJson.JobOrderOne> adapter;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageindex=0;
                getOrder(pageindex);
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
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
                if (charSequence.length()>0)
                    ib_delete.setVisibility(View.VISIBLE);
                else
                    ib_delete.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
                key=edt_content.getText().toString().trim();
            }
        });
        edt_content .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    refresh();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edt_content.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });
        adapter =new CommonAdapter<JobOrderListJson.JobOrderOne>(getActivity(),jobList,R.layout.item_order) {
            @Override
            public void convert(ViewHolder helper, final JobOrderListJson.JobOrderOne item, final int position) {
                helper.setText(R.id.tv_position,item.JobTitle);
                helper.setText(R.id.tv_address,item.ProvinceName+"-"+item.CityName);
                helper.setText(R.id.tv_status, CommUtils.getOrderStatus(item.JobOrderStatus));
                helper.setText(R.id.tv_salary, item.PositionLevel);
                helper.setText(R.id.tv_company, item.CompanyName);
                helper.setText(R.id.tv_time, item.CreatedTime);
                helper.getView(R.id.tv_salary).setBackgroundResource(getSalaryBackground(item.PositionLevel));
                helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent= new Intent(getActivity(), OrderDetailActivity.class);
                        intent.putExtra("url",Constants.WEB_URL+"BossApp/detail/positiondetail.html?jobid="+item.JobOrderID+"&sessionid="+ MyInfoManager.getSessionID(getActivity()));
                        intent.putExtra("shareurl",Constants.WEB_URL+"BossApp/share/positiondetail.html?jobid="+item.JobOrderID+"&sessionid="+ MyInfoManager.getSessionID(getActivity()));
                        intent.putExtra("status",item.JobOrderStatus);
                        intent.putExtra("CompanyInterviewJobId",item.CompanyInterviewJobId);
                        intent.putExtra("JobOrderID",item.JobOrderID);
                        startActivity(intent);
                    }
                });
                if (CommUtils.getOrderStatus(item.JobOrderStatus).equals("已承接")){
                    helper.getView(R.id.rl_recommendcandidate).setVisibility(View.VISIBLE);
                    helper.getView(R.id.ll_weichuli).setVisibility(View.GONE);
                    helper.getView(R.id.rl_recommendcandidate).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent =new Intent(getActivity(), AlreadyRecommendedCandidatesActivity.class);
                            intent.putExtra("orderId",item.JobOrderID);
                            intent.putExtra("companyInterviewJobId",item.CompanyInterviewJobId);
                            startActivity(intent);
                        }
                    });
                }else{
                    helper.getView(R.id.rl_recommendcandidate).setVisibility(View.GONE);
                    helper.getView(R.id.ll_weichuli).setVisibility(View.VISIBLE);
                    helper.getView(R.id.rl_chengjie).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final NormalDialog dialog = new NormalDialog(getActivity());
                            dialog.content(getString(R.string.is_undertake_order))//
                                    .isTitleShow(true)
                                    .title(" ")
                                    .titleTextSize(0)
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
                                            undertakeOrder(item.JobOrderID,position);
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });
                    helper.getView(R.id.rl_refuse).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final NormalDialog dialog = new NormalDialog(getActivity());
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
                                            refuseOrder(item.JobOrderID,position);
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    });
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getOrder(pageindex);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==getActivity().RESULT_OK){
            switch (requestCode){
                case FILTER_REQUEST:
                    JobOrderStatus=data.getStringExtra("status");
                    if (TextUtils.isEmpty(data.getStringExtra("status"))){
                        JobOrderStatus= CommUtils.getOrderStatusCode("已承接")+","+CommUtils.getOrderStatusCode("未处理");
                    }
                    JobTypeCode=data.getStringExtra("position");
                    zhinenglist=(List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    refresh();
                    break;
            }
        }
    }

    @Event(value = R.id.tv_filter)
    private void orderfilter(View view){
        Intent intent =new Intent(getActivity(), OrderFilterActivity.class);
        intent.putExtra("zhineng", (Serializable)zhinenglist);
        intent.putExtra("status", JobOrderStatus);
        startActivityForResult(intent,FILTER_REQUEST);
    }

    private void getOrder(int pagenum){
        pagenum++;
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/GetJobOrderList");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(getActivity()));
        params.addBodyParameter("JobTypeCode",JobTypeCode);
        params.addBodyParameter("key", key);
        params.addBodyParameter("JobOrderStatus", JobOrderStatus);
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
                Toast.makeText(getActivity(),jobOrderListJson.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void undertakeOrder(String orderId,final int position){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/AcceptJobOrder");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(getActivity()));
        params.addBodyParameter("jobOrderId",orderId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AcceptJobOrder",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        jobList.get(position).JobOrderStatus=CommUtils.getOrderStatusCode("已承接");
                        Log.i("YYYY",CommUtils.getOrderStatus(jobList.get(position).JobOrderStatus));
                        Toast.makeText(getActivity(),"承接成功",Toast.LENGTH_SHORT).show();
                        if (adapter!=null) {
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(getActivity(),simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
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
    private void refuseOrder(String orderId,final int position){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/RejectJobOrder");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(getActivity()));
        params.addBodyParameter("jobOrderId",orderId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("RejectJobOrder",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        jobList.remove(position);
                        if (adapter!=null) {
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(getActivity(),simpleJson.msg,Toast.LENGTH_SHORT).show();
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
    private void refresh(){
        jobList.clear();
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
        mPtrFrame.autoRefresh();
    }

    private int  getSalaryBackground(String str){
        int res=R.drawable.img_blue;
        if (str.contains("20")){
            res=R.drawable.img_blue;
        }else if(str.contains("30")){
            res=R.drawable.img_yellow;
        }else if(str.contains("50")){
            res=R.drawable.img_green;
        }else if(str.contains("100")){
            res=R.drawable.img_red;
        }
        return res;
    }
}

package com.offer9191.boss.fragment.candidate;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.candidate.AddCandidateActivity;
import com.offer9191.boss.activity.candidate.CandidateDetailActivity;
import com.offer9191.boss.activity.candidate.ToBeRecommendedOrderActivity;
import com.offer9191.boss.adapter.baseadpater.CommonAdapter;
import com.offer9191.boss.adapter.baseadpater.ViewHolder;
import com.offer9191.boss.base.BaseFragment;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.fragment.CandidateFragment;
import com.offer9191.boss.jsonbean.CandidateListJson;
import com.offer9191.boss.jsonbean.CityJson;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by OfferJiShu01 on 2016/9/14.
 */
@ContentView(R.layout.fragment_candidate_sub)
public class CandidateSubFragment extends BaseFragment {
    private int tag=0;
    private int pageindex=0, pagesize=20;
    private String candidateStatus="",key="",candidateGender="",vocationCodes="",jobTypeCodes="",ageFrom="",ageTo="";
    private List<CityJson.DistrictsOne> zhinenglist;
    @ViewInject(R.id.rotate_header_list_view_frame)PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.rotate_header_list_view)LoadMoreListView lv;
    private List<CandidateListJson.CandidateOne> candidateList=new ArrayList<>();
    CommonAdapter<CandidateListJson.CandidateOne> adapter;
    public static CandidateSubFragment newInstance(int tag,String str){
        CandidateSubFragment candidateSubFragment =new CandidateSubFragment();
        Bundle args = new Bundle();
        args.putInt("tag", tag);
        args.putString("str", str);
        candidateSubFragment.setArguments(args);
        return candidateSubFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag= getArguments() != null ? getArguments().getInt("tag") : 0;
        candidateStatus=CommUtils.getCVStatusCode(getArguments()!=null?getArguments().getString("str"):"");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageindex=0;
                getCandidates(pageindex);
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

        adapter =new CommonAdapter<CandidateListJson.CandidateOne>(getActivity(),candidateList,R.layout.item_candidate) {
            @Override
            public void convert(ViewHolder helper, final CandidateListJson.CandidateOne item, final int position) {
                helper.setText(R.id.tv_name,item.CandidateName);
                if (item.CandidateGender.trim().equals("男")){
                    Drawable drawable= getResources().getDrawable(R.drawable.sex_m);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    ((TextView)helper.getView(R.id.tv_name)).setCompoundDrawables(null, null, drawable, null);
                }else{
                    Drawable drawable= getResources().getDrawable(R.drawable.sex_g);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    ((TextView)helper.getView(R.id.tv_name)).setCompoundDrawables(null, null, drawable, null);
                }

                helper.setText(R.id.tv_time,item.CreatedTime);
                helper.setText(R.id.tv_position,item.JobTypeCodeNames);
                helper.setText(R.id.tv_industry,item.VocationNames);
                helper.setText(R.id.tv_phone,item.CandidateMobile);
                helper.getView(R.id.ll_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(getActivity(), CandidateDetailActivity.class);
                        intent.putExtra("url",Constants.WEB_URL+"BossApp/detail/userdetail.html?userid="+item.CandidateID+"&sessionid="+ MyInfoManager.getSessionID(getActivity()));
                        intent.putExtra("shareurl",Constants.WEB_URL+"BossApp/share/userdetail.html?userid="+item.CandidateID+"&sessionid="+ MyInfoManager.getSessionID(getActivity()));
                        intent.putExtra("candidateId",item.CandidateID);
                        startActivity(intent);
                    }
                });
                switch (tag){
                    case 0:
                        helper.getView(R.id.v2).setVisibility(View.GONE);
                        helper.getView(R.id.rl_recommendcandidate).setVisibility(View.GONE);
                        break;
                    case 1:
                        break;
                    case 2:
                        helper.getView(R.id.v2).setVisibility(View.GONE);
                        helper.getView(R.id.v1).setVisibility(View.GONE);
                        helper.getView(R.id.rl_recommendcandidate).setVisibility(View.GONE);
                        helper.getView(R.id.rl_edit).setVisibility(View.GONE);
                        break;
                }
                helper.getView(R.id.rl_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final NormalDialog dialog = new NormalDialog(getActivity());
                        dialog.content(getString(R.string.is_delete_candidate))//
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
                                        deleteCandidate(item.CandidateID,position);
                                        dialog.dismiss();
                                    }
                                });

                    }
                });
                helper.getView(R.id.rl_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(getActivity(), AddCandidateActivity.class);
                        intent.putExtra("name",getResources().getString(R.string.edit_candidate));
                        intent.putExtra("candidateId",item.CandidateID);
                        startActivity(intent);
                    }
                });
                helper.getView(R.id.rl_recommendcandidate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(getActivity(), ToBeRecommendedOrderActivity.class);
                        intent.putExtra("CandidateID",item.CandidateID);
                        startActivity(intent);
                    }
                });
            }
        };
        lv.setAdapter(adapter);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getCandidates(pageindex);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    private void getCandidates(int pagenum){
        pagenum++;
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/GetMyCandidateList");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(getActivity()));
        params.addBodyParameter("candidateStatus",candidateStatus);
        params.addBodyParameter("key", key);
        params.addBodyParameter("candidateGender", candidateGender);
        params.addBodyParameter("vocationCodes", vocationCodes);
        params.addBodyParameter("jobTypeCodes", jobTypeCodes);
        params.addBodyParameter("ageTo", ageTo);
        params.addBodyParameter("ageFrom", ageFrom);
        params.addBodyParameter("pageindex", pagenum+"");
        params.addBodyParameter("pagesize", pagesize+"");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetMyCandidateList",result);
                processCandidates(result);
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
    private void processCandidates(String json){
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
                Toast.makeText(getActivity(),candidateListJson.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void deleteCandidate(String candidateId,final int position){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/DeleteCandidate");
        params.addBodyParameter("candidateId", candidateId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("DeleteCandidate",result);
                try {
                    SimpleJson simpleJson =GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        candidateList.remove(position);
                        if (adapter!=null) {
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getActivity(),getString(R.string.delete_success),Toast.LENGTH_SHORT).show();
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
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() { dismissProgressDialog(); }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==getActivity().RESULT_OK){
            switch (requestCode){
                case 1101:
                    Log.e("candidateGender",candidateGender);
                    key=data.getStringExtra("key");
                    candidateGender=data.getStringExtra("gender");
                    jobTypeCodes=data.getStringExtra("position");
                    ageFrom=data.getStringExtra("ageFrom");
                    ageTo=data.getStringExtra("ageTo");
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

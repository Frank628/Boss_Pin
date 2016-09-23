package com.offer9191.boss.activity.candidate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.IndustryActivity;
import com.offer9191.boss.activity.PositionActivity;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CandidateInfoJson;
import com.offer9191.boss.jsonbean.CandidateListJson;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.CityJsonUtils;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/8.
 */
@ContentView(R.layout.activity_addcandidate)
public class AddCandidateActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.edt_candidatename)EditText edt_candidatename;
    @ViewInject(R.id.edt_age)EditText edt_age;
    @ViewInject(R.id.edt_phone)EditText edt_phone;
    @ViewInject(R.id.edt_company)EditText edt_company;
    @ViewInject(R.id.edt_industry)EditText edt_industry;
    @ViewInject(R.id.edt_department)EditText edt_department;
    @ViewInject(R.id.edt_expectposition)EditText edt_expectposition;
    @ViewInject(R.id.edt_position)EditText edt_position;
    @ViewInject(R.id.edt_workcity)EditText edt_workcity;
    @ViewInject(R.id.edt_email)EditText edt_email;
    @ViewInject(R.id.edt_remark)EditText edt_remark;
    @ViewInject(R.id.rb_female)RadioButton rb_female;
    @ViewInject(R.id.rb_male)RadioButton rb_male;
    private String jobTypeCodes="",jobTypeCodeNames="",vocationCodes="",vocationNames="",DistrictCode="";
    private List<CityJson.CityOne> hangyelist;
    private List<CityJson.DistrictsOne> zhinenglist,citylist;
    private static final int INDUSTRY_REQUEST=1;
    private static final int POSITION_REQUEST=2;
    private static final int CITY_REQUEST=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getIntent().getStringExtra("name"));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (getIntent().getStringExtra("candidateId")!=null){
            getCandidate(getIntent().getStringExtra("candidateId"));
        }
    }
    @Event(value = {R.id.rl_industry,R.id.edt_industry})
    private void industryClick(View view){
        Intent intent =new Intent(this, IndustryActivity.class);
        intent.putExtra("hangye", (Serializable)hangyelist);
        startActivityForResult(intent, INDUSTRY_REQUEST);
    }
    @Event(value = {R.id.rl_expectposition,R.id.edt_expectposition})
    private void positionClick(View view){
        Intent intent =new Intent(this, PositionActivity.class);
        intent.putExtra("zhineng", (Serializable)zhinenglist);;
        intent.putExtra("isCity",false);
        startActivityForResult(intent, POSITION_REQUEST);
    }
    @Event(value = {R.id.rl_workcity,R.id.edt_workcity})
    private void cityClick(View view){
        Intent intent =new Intent(this, PositionActivity.class);
        intent.putExtra("zhineng", (Serializable)citylist);
        intent.putExtra("maxNum",1);
        intent.putExtra("isCity",true);
        startActivityForResult(intent, CITY_REQUEST);
    }
    @Event(value = R.id.btn_submit)
    private void addCandidateClick(View view){
        addCandidate();
    }
    private void addCandidate(){
        String candidateName=edt_candidatename.getText().toString().trim();
        String candidateGender=rb_male.isChecked()?"男":"女";
        String CurrentCompany=edt_company.getText().toString().trim();
        String CandidateEmail=edt_email.getText().toString().trim();
        String CandidatePosition=edt_position.getText().toString().trim();
        String CandidateMobile=edt_phone.getText().toString().trim();
        String CandidateDepartment=edt_department.getText().toString().trim();
        String Notes=edt_remark.getText().toString().trim();
        String CandidateAge=edt_age.getText().toString().trim();
        if (!CommUtils.isMobile(CandidateMobile)){
            Toast.makeText(AddCandidateActivity.this,getString(R.string.mobile_error),Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommUtils.isEmail(CandidateEmail)){
            Toast.makeText(AddCandidateActivity.this,getString(R.string.email_error),Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params=null;
        if (getIntent().getStringExtra("candidateId")!=null){
            params=new RequestParams(Constants.URL+"api/Candidate/EditCandidate");
            Log.i("candidateId",getIntent().getStringExtra("candidateId"));
            params.addBodyParameter("candidateId",getIntent().getStringExtra("candidateId"));
        }else{
             params=new RequestParams(Constants.URL+"api/Candidate/AddCandidate");
        }
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(AddCandidateActivity.this));
        params.addBodyParameter("candidateName",candidateName);
        params.addBodyParameter("candidateGender",candidateGender);
        params.addBodyParameter("jobTypeCodes",jobTypeCodes);
        params.addBodyParameter("jobTypeCodeNames",jobTypeCodeNames);
        params.addBodyParameter("vocationCodes",vocationCodes);
        params.addBodyParameter("vocationNames", vocationNames);
        params.addBodyParameter("DistrictCode", DistrictCode);
        params.addBodyParameter("CurrentCompany", CurrentCompany);
        params.addBodyParameter("CandidateEmail", CandidateEmail);
        params.addBodyParameter("CandidateMobile", CandidateMobile);
        params.addBodyParameter("CandidatePosition", CandidatePosition);
        params.addBodyParameter("CandidateDepartment", CandidateDepartment);
        params.addBodyParameter("Notes", Notes);
        params.addBodyParameter("CandidateAge", CandidateAge);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetJobOrderList",result);
                try {
                    SimpleJson simpleJson= GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){

                        if (getIntent().getStringExtra("candidateId")!=null){
                            Toast.makeText(AddCandidateActivity.this,getString(R.string.edit_success),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddCandidateActivity.this,getString(R.string.add_success),Toast.LENGTH_SHORT).show();
                        }
                        AddCandidateActivity.this.finish();
                    }else{
                        Toast.makeText(AddCandidateActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
    private void getCandidate(String candidateId){
        RequestParams params=new RequestParams(Constants.URL+"api/Candidate/GetCandidateInfo");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(AddCandidateActivity.this));
        params.addBodyParameter("candidateID",candidateId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetCandidateInfo",result);
                try {
                    CandidateInfoJson candidateInfoJson=GsonTools.changeGsonToBean(result, CandidateInfoJson.class);
                    if (candidateInfoJson.code==0){
                        edt_candidatename.setText(candidateInfoJson.data.CandidateName);
                        if (candidateInfoJson.data.CandidateGender.equals("男"))
                            rb_male.setChecked(true);
                        else
                            rb_female.setChecked(true);
                        edt_age.setText(candidateInfoJson.data.CandidateAge);
                        edt_phone.setText(candidateInfoJson.data.CandidateMobile);
                        edt_company.setText(candidateInfoJson.data.CurrentCompany);
                        edt_industry.setText(candidateInfoJson.data.VocationNames);
                        vocationNames=candidateInfoJson.data.VocationNames;
                        vocationCodes=candidateInfoJson.data.VocationCodes;
                        if (!TextUtils.isEmpty(candidateInfoJson.data.VocationCodes)){
                            hangyelist=new ArrayList<CityJson.CityOne>();
                            hangyelist.add(new CityJson.CityOne(candidateInfoJson.data.VocationCodes,candidateInfoJson.data.VocationNames));
                        }
                        edt_department.setText(candidateInfoJson.data.CandidateDepartment);
                        edt_position.setText(candidateInfoJson.data.CandidatePosition);
                        edt_expectposition.setText(candidateInfoJson.data.JobTypeCodeNames);
                        jobTypeCodeNames=candidateInfoJson.data.JobTypeCodeNames;
                        jobTypeCodes=candidateInfoJson.data.JobTypeCodes;
                        if (!TextUtils.isEmpty(candidateInfoJson.data.JobTypeCodes)){
                            zhinenglist=new ArrayList<CityJson.DistrictsOne>();
                            zhinenglist.add(new CityJson.DistrictsOne(candidateInfoJson.data.JobTypeCodes,candidateInfoJson.data.JobTypeCodeNames));
                        }
                        edt_workcity.setText(candidateInfoJson.data.DistrictName);
                        DistrictCode=candidateInfoJson.data.DistrictCode;
                        if (!TextUtils.isEmpty(candidateInfoJson.data.DistrictCode)){
                            citylist=new ArrayList<CityJson.DistrictsOne>();
                            citylist.add(new CityJson.DistrictsOne(candidateInfoJson.data.DistrictCode,candidateInfoJson.data.DistrictName));
                        }

                        edt_email.setText(candidateInfoJson.data.CandidateEmail);
                    }else{
                        Toast.makeText(AddCandidateActivity.this,candidateInfoJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {}
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case INDUSTRY_REQUEST:
                    hangyelist = (List<CityJson.CityOne>) data.getSerializableExtra("hangye");
                    vocationCodes= CityJsonUtils.listGetCITYCode(hangyelist);
                    vocationNames= CityJsonUtils.listGetValue(hangyelist);
                    edt_industry.setText(CityJsonUtils.listGetValue(hangyelist));
                    break;
                case POSITION_REQUEST:
                    zhinenglist = (List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    jobTypeCodes= CityJsonUtils.listGetDISCode(zhinenglist);
                    jobTypeCodeNames= CityJsonUtils.listGetZhiValue(zhinenglist);
                    edt_expectposition.setText(CityJsonUtils.listGetZhiValue(zhinenglist));;
                    break;
                case CITY_REQUEST:
                    citylist = (List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    jobTypeCodes= CityJsonUtils.listGetDISCode(citylist);
                    edt_workcity.setText(CityJsonUtils.listGetZhiValue(citylist));
                    break;
            }
        }
    }
}

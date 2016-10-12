package com.offer9191.boss.activity.candidate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.activity.PositionActivity;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.CityJsonUtils;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnOperItemClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.ActionSheetDialogWechat;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/8.
 */
@ContentView(R.layout.activity_cadidatefilter)
public class CandidateFilterActivity extends BaseActivity{
    private static final int POSITION_REQUEST=1;
    private List<CityJson.DistrictsOne> zhinenglist;
    private String ageFrom="",zhineng="",ageTo="",gender="",key="";
    @ViewInject(R.id.tv_gender)TextView tv_gender;
    @ViewInject(R.id.tv_age)TextView tv_age;
    @ViewInject(R.id.tv_position)TextView tv_position;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng")!=null) {
            zhinenglist=(List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng");
            zhineng= CityJsonUtils.listGetDISCode(zhinenglist);
            tv_position.setText(CityJsonUtils.listGetZhiValue(zhinenglist));
        }
        key=getIntent().getStringExtra("key");
        gender=getIntent().getStringExtra("gender");
        ageFrom=getIntent().getStringExtra("ageFrom");
        ageTo=getIntent().getStringExtra("ageTo");
        if (!TextUtils.isEmpty(gender)){
            tv_gender.setText(gender);
        }
        if (!TextUtils.isEmpty(ageFrom)){
            tv_age.setText(ageFrom+"-"+ageTo);
        }
        if (!TextUtils.isEmpty(key)){
            edt_content.setText(key);
        }
    }

    @Event(value = R.id.tv_back)
    private void back(View view){
        onBackPressed();
    }
    @Event(value = R.id.rl_gender)
    private void genderSelect(View view){
        final String[] stringItems = {"不限","男", "女"};
        final ActionSheetDialogWechat actionSheetDialogWechat=new ActionSheetDialogWechat(this,stringItems,null);
        actionSheetDialogWechat.isTitleShow(false).show();
        actionSheetDialogWechat.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_gender.setText(stringItems[position]);
                gender=stringItems[position].equals("不限")?"":stringItems[position];
                actionSheetDialogWechat.dismiss();
            }
        });
    }
    @Event(value = R.id.rl_age)
    private void ageSelect(View view){
        final String[] stringItems = {"不限","20-30","30-40", "40-50", "50-60", "60-70"};
        final ActionSheetDialogWechat actionSheetDialogWechat=new ActionSheetDialogWechat(this,stringItems,null);
        actionSheetDialogWechat.isTitleShow(false).show();
        actionSheetDialogWechat.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_age.setText(stringItems[position]);
                if (stringItems[position].equals(stringItems[0])){
                    ageFrom="";
                    ageTo="";
                }else{
                    String str[]=stringItems[position].split("-");
                    ageFrom=str[0];
                    ageTo=str[1];
                }
                actionSheetDialogWechat.dismiss();
            }
        });
    }
    @Event(value = R.id.rl_position)
    private void position(View view){
        Intent intent =new Intent();
        intent.setClass(this, PositionActivity.class);
        intent.putExtra("zhineng", (Serializable)zhinenglist);
        intent.putExtra("isCity", false);
        startActivityForResult(intent, POSITION_REQUEST);
    }
    @Event(value = R.id.btn_submit)
    private void search(View view){
        Intent intent =new Intent();
        intent.putExtra("key",edt_content.getText().toString().trim());
        intent.putExtra("position", zhineng);
        intent.putExtra("gender", gender);
        intent.putExtra("ageFrom", ageFrom);
        intent.putExtra("ageTo", ageTo);
        intent.putExtra("zhineng", (Serializable)zhinenglist);
        setResult(RESULT_OK, intent);
        CandidateFilterActivity.this.finish();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK && data != null) {
            switch (requestCode) {
                case POSITION_REQUEST:

                    zhinenglist = (List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    zhineng= CityJsonUtils.listGetDISCode(zhinenglist);
                    tv_position.setText(CityJsonUtils.listGetZhiValue(zhinenglist));
                    break;
                default:
                    break;
            }
        }
    }
}

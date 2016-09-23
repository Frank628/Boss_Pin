package com.offer9191.boss.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.LoginJson;
import com.offer9191.boss.main.MainActivity;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OfferJiShu01 on 2016/9/8.
 */
@ContentView(R.layout.activity_editpersoninfo)
public class EditPersonInfoActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.edt_content)EditText edt_content;
    String param="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title =getIntent().getStringExtra("name");
        String content =getIntent().getStringExtra("content");
        param=getIntent().getStringExtra("param");
        int inputTpye= getIntent().getIntExtra("inputtype", InputType.TYPE_CLASS_TEXT);
        navigationLayout.setCenterText(title);
        navigationLayout.setLeftText("",-1 ,new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        navigationLayout.setLeftText(getResources().getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditPersonInfoActivity.this.finish();
            }
        });
        navigationLayout.setRightText(getResources().getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(edt_content.getText().toString().trim());

            }
        });
        edt_content.setInputType(inputTpye);
        edt_content.setText(content);
        edt_content.setSelection(edt_content.getText().length());

    }
    @Event(value = R.id.btn_delete)
    private void delete(View view){
        edt_content.setText("");
    }
    private void save(String str){
        if (TextUtils.isEmpty(str)){
            Toast.makeText(EditPersonInfoActivity.this,getString(R.string.not_null),Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog("",getString(R.string.data_uploading));
        Map<String,String> paramsMap=new HashMap<>();
        paramsMap.put("myName","");
        paramsMap.put("sex","");
        paramsMap.put("mobilePhoneNumber","");
        paramsMap.put("partnerName","");
        RequestParams params=new RequestParams(Constants.URL+"api/User/UpdateUserInfo");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(this));
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (entry.getKey().equals(param)) {
                params.addBodyParameter(entry.getKey(), str);
            } else {
                params.addBodyParameter(entry.getKey(), "");
            }
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("api/User/UpdateUserInfo",result);
                try {
                    LoginJson loginJson= GsonTools.changeGsonToBean(result,LoginJson.class);
                    if (loginJson.code==0){
                        MyInfoManager.setUserName(EditPersonInfoActivity.this,loginJson.data.UserName);
                        MyInfoManager.setSEX(EditPersonInfoActivity.this,loginJson.data.Sex);
                        MyInfoManager.setPhone(EditPersonInfoActivity.this,loginJson.data.MobilePhoneNumber);
                        MyInfoManager.setCompanyName(EditPersonInfoActivity.this,loginJson.data.PartnersName);
                        MyInfoManager.setDisplayName(EditPersonInfoActivity.this,loginJson.data.DisplayName);
                        MyInfoManager.setUserPreview(EditPersonInfoActivity.this,loginJson.data.HDpic);
                        Intent intent = new Intent();
                        intent.putExtra("result", edt_content.getText().toString().trim());
                        EditPersonInfoActivity.this.setResult(RESULT_OK, intent);
                        EditPersonInfoActivity.this.finish();
                    }else{
                        Toast.makeText(EditPersonInfoActivity.this,loginJson.msg,Toast.LENGTH_SHORT).show();
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
            public void onFinished() {
                dismissProgressDialog();
            }
        });
    }
}

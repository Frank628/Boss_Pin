package com.offer9191.boss.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.jsonbean.SmsJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.TimerButton;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by OfferJiShu01 on 2016/9/7.
 */
@ContentView(R.layout.activity_forgetpassword)
public class ActivityForgetPassword extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.edt_phone)EditText edt_phone;
    @ViewInject(R.id.edt_verification)EditText edt_verification;
    @ViewInject(R.id.btn_getcode)TimerButton btn_getcode;
    private String SmsId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getResources().getString(R.string.forget_password));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Event(value = R.id.btn_getcode)
    private void getcodeClick(View view){
        String mobilePhoneNumber=edt_phone.getText().toString().trim();
        if (!CommUtils.isMobile(mobilePhoneNumber)){
            Toast.makeText(ActivityForgetPassword.this,getString(R.string.mobile_error),Toast.LENGTH_SHORT).show();
            return;
        }
        sendMessage(mobilePhoneNumber);
    }
    @Event(value = R.id.btn_submit)
    private void submitClick(View view){
        String mobilePhoneNumber=edt_phone.getText().toString().trim();
        String verifyCode=edt_verification.getText().toString().trim();
        if (TextUtils.isEmpty(verifyCode)){
            Toast.makeText(ActivityForgetPassword.this,getString(R.string.code_not_null),Toast.LENGTH_SHORT).show();
            return;
        }

        if (!CommUtils.isMobile(mobilePhoneNumber)){
            Toast.makeText(ActivityForgetPassword.this,getString(R.string.mobile_error),Toast.LENGTH_SHORT).show();
            return;
        }
        submit(SmsId,verifyCode,mobilePhoneNumber);
    }
    private void submit(String msgId,String verifyCode,String mobilePhoneNumber){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/User/ResetPassword");
        params.addBodyParameter("msgId", msgId);
        params.addBodyParameter("verifyCode", verifyCode);
        params.addBodyParameter("mobilePhoneNumber", mobilePhoneNumber);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("ResetPassword",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        Toast.makeText(ActivityForgetPassword.this,"密码将发送到您手机短信，注意查收！",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ActivityForgetPassword.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
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
    private void sendMessage(String mobilePhoneNumber){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/Common/SendVerificationCode");
        params.addBodyParameter("mobilePhoneNumber", mobilePhoneNumber);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("SendVerificationCode",result);
                try {

                    SmsJson smsJson = GsonTools.changeGsonToBean(result,SmsJson.class);
                    if (smsJson.code==0){
                        btn_getcode.start();
                        SmsId=smsJson.data.SmsId;
                    }else{
                        Toast.makeText(ActivityForgetPassword.this,smsJson.msg,Toast.LENGTH_SHORT).show();
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
}

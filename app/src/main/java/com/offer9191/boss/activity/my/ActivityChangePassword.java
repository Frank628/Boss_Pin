package com.offer9191.boss.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.SysApplication;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by OfferJiShu01 on 2016/9/7.
 */
@ContentView(R.layout.activity_changepassword)
public class ActivityChangePassword extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.edt_newpassword)EditText edt_newpassword;
    @ViewInject(R.id.edt_oldpassword)EditText edt_oldpassword;
    @ViewInject(R.id.edt_againnewpassword)EditText edt_againnewpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getResources().getString(R.string.change_password));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Event(value = R.id.btn_submit)
    private void submitClick(View view){
        String oldpass=edt_oldpassword.getText().toString().trim();
        String newpass=edt_newpassword.getText().toString().trim();
        String againnewpass=edt_againnewpassword.getText().toString().trim();
        if (!CommUtils.isPassword(newpass)){
            Toast.makeText(ActivityChangePassword.this,getString(R.string.password_error),Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newpass.equals(againnewpass)){
            Toast.makeText(ActivityChangePassword.this,getString(R.string.new_pass_noequal),Toast.LENGTH_SHORT).show();
            return;
        }
        submit(oldpass,newpass);
    }
    private void submit(String oldpassword,String newpassword){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/User/ChangePassword");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ActivityChangePassword.this));
        params.addBodyParameter("oldpassword", oldpassword);
        params.addBodyParameter("newpassword", newpassword);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("ChangePassword",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        Toast.makeText(ActivityChangePassword.this,getString(R.string.submit_success),Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(ActivityChangePassword.this, LoginActivity.class);
                        startActivity(intent);
                        SysApplication.getInstance().exit();
                    }else{
                        Toast.makeText(ActivityChangePassword.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
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

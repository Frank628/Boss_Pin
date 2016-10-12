package com.offer9191.boss.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.Dialog;
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

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@ContentView(R.layout.activity_regist)
public class RegistActivity extends BaseActivity{
    @ViewInject(R.id.atv_companyname)private AutoCompleteTextView atv_companyname;
    @ViewInject(R.id.edt_name) private EditText edt_name;
    @ViewInject(R.id.edt_phone) private EditText edt_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucent(this);


    }

    @Event(value = R.id.tv_back)
    private void back(View view){
        onBackPressed();
    }
    private void submit(String DisplayName,String MobileNumber,String CompanyName){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/User/PartnersApply");
        params.addBodyParameter("DisplayName", DisplayName);
        params.addBodyParameter("MobileNumber", MobileNumber);
        params.addBodyParameter("CompanyName", CompanyName);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("PartnersApply",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){

                        Dialog.showRadioDialog(RegistActivity.this, "提示", "正在为您审核，请耐心等待！", new Dialog.DialogClickListener() {
                            @Override
                            public void confirm() {
                                RegistActivity.this.finish();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    }else{
                        Toast.makeText(RegistActivity.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
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
    private void submitClick(View view){
        String CompanyName =atv_companyname.getText().toString().trim();
        String DisplayName=edt_name.getText().toString().trim();
        String MobileNumber=edt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(CompanyName)){
            Toast.makeText(RegistActivity.this,getString(R.string.company_not_null),Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(DisplayName)){
            Toast.makeText(RegistActivity.this,getString(R.string.name_not_null),Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommUtils.isMobile(MobileNumber)){
            Toast.makeText(RegistActivity.this,getString(R.string.mobile_error),Toast.LENGTH_SHORT).show();
            return;
        }
        submit(DisplayName,MobileNumber,CompanyName);

    }
    @Event(value = R.id.tv_qa)
    private void qaClick(View view){
        Intent intent =new Intent(this, WebActivityContainer.class);
        intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
        intent.putExtra("url",Constants.WEB_URL+"BossApp/common/qa.html");
        startActivity(intent);
    }
}


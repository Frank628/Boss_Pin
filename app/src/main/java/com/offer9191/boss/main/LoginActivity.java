package com.offer9191.boss.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.activity.my.ActivityForgetPassword;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.LoginJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.utils.SharePrefUtil;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnOperItemClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.ActionSheetDialog;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    private static String TAG="BINDALIAS";
    @ViewInject(R.id.atv_user)private EditText atv_user;
    @ViewInject(R.id.edt_password) private EditText mPasswordView;
    @ViewInject(R.id.cb_eye) private CheckBox cb_eye;
    @ViewInject(R.id.cb_rememberpsd)private CheckBox cb_rememberpsd;
    @ViewInject(R.id.ib_delete) private ImageButton ib_delete;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucent(this);
        atv_user.setText(MyInfoManager.getUserName(this));
        if(SharePrefUtil.getBoolean(this,Constants.IS_REMEMBER_PASSWORD,true)){
            mPasswordView.setText(MyInfoManager.getPassword(this));
        }
        cb_rememberpsd.setChecked(SharePrefUtil.getBoolean(this,Constants.IS_REMEMBER_PASSWORD,true));
        cb_rememberpsd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharePrefUtil.saveBoolean(LoginActivity.this,Constants.IS_REMEMBER_PASSWORD,b);
            }
        });
        cb_eye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mPasswordView.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = mPasswordView.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
        atv_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    if (atv_user.getText().toString().trim().length()>0){
                        ib_delete.setVisibility(View.VISIBLE);
                    }else{
                        ib_delete.setVisibility(View.GONE);
                    }

                }else{
                    ib_delete.setVisibility(View.GONE);
                }
            }
        });
        atv_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ib_delete.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (atv_user.getText().toString().toString().length()>0){

                }else{
                    ib_delete.setVisibility(View.GONE);
                }
            }
        });

    }
    @Event(value = R.id.ib_delete)
    private void deleteUser(View view){
        atv_user.setText("");
    }

    @Event(value = R.id.btn_regist)
    private void regist(View view){
        Intent intent =new Intent(this,RegistActivity.class);
        startActivity(intent);
    }
    @Event(value = R.id.btn_login)
    private void login(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
       login();
    }
    @Event(value = R.id.tv_forgetpassword)
    private void forgetpassword(View view){
        Intent intent =new Intent(this,ActivityForgetPassword.class);
        startActivity(intent);
    }

    private void login(){
        String username=atv_user.getText().toString().trim();
        final String password=mPasswordView.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(this,getString(R.string.username_empty),Toast.LENGTH_SHORT).show();
            return;
        }
        if (!CommUtils.isPassword(password)){
            Toast.makeText(this,getString(R.string.password_error),Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog =ProgressDialog.show(this,"",getString(R.string.progress_login),true,false);
        progressDialog.show();
        RequestParams params=new RequestParams(Constants.URL+"api/User/Login");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result,password);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                } else { // 其他错误
                    Toast.makeText(LoginActivity.this,"服务器异常，请稍后重试！", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {
                if (progressDialog!=null){
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void processData(String json,String pass){
        try {
            LoginJson loginJson= GsonTools.changeGsonToBean(json,LoginJson.class);
            if (loginJson.code==0){
                Log.i("login",json);
                MyInfoManager.setUserName(LoginActivity.this,loginJson.data.UserName);
                MyInfoManager.setPassword(LoginActivity.this,pass);
                MyInfoManager.setLoginJSON(LoginActivity.this,json);
                MyInfoManager.setSEX(LoginActivity.this,loginJson.data.Sex);
                MyInfoManager.setPhone(LoginActivity.this,loginJson.data.MobilePhoneNumber);
                MyInfoManager.setCompanyName(LoginActivity.this,loginJson.data.PartnersName);
                MyInfoManager.setDisplayName(LoginActivity.this,loginJson.data.DisplayName);
                MyInfoManager.setSessionID(LoginActivity.this,loginJson.data.SessionId);
                MyInfoManager.setUserPreview(LoginActivity.this,loginJson.data.HDpic);
                JPushInterface.setAliasAndTags(getApplicationContext(), loginJson.data.PartnersID, null,mAliasCallback);
                if (loginJson.data.UserType.equals("16032113522111478308d6c2b5439")){
                    Intent intent =new Intent(this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent =new Intent(this,WebActivityContainer.class);
                    intent.putExtra("url",Constants.WEB_URL+"BossApp/manager/index.html?sessionid="+loginJson.data.SessionId+"&pagehome=1");
                    startActivity(intent);
                }

                LoginActivity.this.finish();
            }else{
                Toast.makeText(LoginActivity.this,loginJson.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Event(value = R.id.tv_boss)
    private void bossdetailClick(View view){
        Intent intent =new Intent(LoginActivity.this, WebActivityContainer.class);
        intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
        intent.putExtra("url",Constants.WEB_URL+"BossApp/common/about.html");
        startActivity(intent);
    }
    @Event(value = R.id.tv_contactus)
    private void qaClick(View view){
        final String[] stringItems = {"18800311720"};
        final ActionSheetDialog dialog = new ActionSheetDialog(LoginActivity.this, stringItems, null);
        dialog.title("联系我们")//
                .titleTextSize_SP(14.5f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" +stringItems[position]);
                intent.setData(data);
                LoginActivity.this.startActivity(intent);

            }
        });
    }
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            Log.i("JPush alias", alias);
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again.";
                    Log.i(TAG, logs);
                    JPushInterface.setAliasAndTags(getApplicationContext(), alias, null,mAliasCallback);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }

    };
}


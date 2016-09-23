package com.offer9191.boss.activity.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.activity_feedback)
public class ActivityFeedback extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.edt_content)EditText edt_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getResources().getString(R.string.feedback));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Event(value = R.id.btn_submit)
    private void submitClick(View view){
        if (TextUtils.isEmpty(edt_content.getText().toString().trim())){
            Toast.makeText(ActivityFeedback.this,getString(R.string.not_null),Toast.LENGTH_SHORT).show();
            return;
        }else{
            submit(edt_content.getText().toString().trim());
        }

    }
    private void submit(String contents){
        showProgressDialog("",getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/User/AddSuggest");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(ActivityFeedback.this));
        params.addBodyParameter("contents", contents);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AddSuggest",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        edt_content.setText("");
                        Toast.makeText(ActivityFeedback.this,getString(R.string.submit_success),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ActivityFeedback.this,simpleJson.msg,Toast.LENGTH_SHORT).show();
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

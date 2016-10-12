package com.offer9191.boss.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.offer9191.boss.R;
import com.offer9191.boss.SysApplication;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.activity_setting)
public class ActivitySetting extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getResources().getString(R.string.setting));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    @Event(value = R.id.rl_feedback)
    private void feedback(View view){
        Intent intent =new Intent(this, ActivityFeedback.class);
        startActivity(intent);
    }
    @Event(value = R.id.rl_changepassword)
    private void changepassword(View view){
        Intent intent =new Intent(this, ActivityChangePassword.class);
        startActivity(intent);
    }

    @Event(value = R.id.tv_logout)
    private void logout(View view){
        final NormalDialog dialog = new NormalDialog(ActivitySetting.this);
        dialog.content("确认退出当前账号？")//
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(18)//
                .title("退出")
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
//                        dialog.dismiss();
                        Intent intent =new Intent(ActivitySetting.this, LoginActivity.class);
                        startActivity(intent);
                        SysApplication.getInstance().exitA();
                    }
                });

    }
    @Event(value = R.id.rl_about)
    private void aboutClick(View view){
        Intent intent =new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}

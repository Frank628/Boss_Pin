package com.offer9191.boss.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.utils.SharePrefUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by OfferJiShu01 on 2016/8/30.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runGUID();
            }
        }, 500);
    }
    private void runGUID(){
        if (SharePrefUtil.getBoolean(SplashActivity.this, Constants.IS_SHOW_GUID,true)){
            Intent intent =new Intent(SplashActivity.this, GuidActivity.class);
            startActivity(intent);
        }else{
            Intent intent =new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        SplashActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}

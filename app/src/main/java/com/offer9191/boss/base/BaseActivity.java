package com.offer9191.boss.base;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.offer9191.boss.R;
import com.offer9191.boss.SysApplication;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.network.NetWorkManager;
import com.offer9191.boss.widget.Dialog;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;

import org.xutils.x;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by OfferJiShu01 on 2016/8/30.
 */
@RuntimePermissions
public class BaseActivity extends FragmentActivity{
    public ProgressDialog progressDialog;
    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        SysApplication.getInstance().addActivity(this);

//        NetWorkManager.getInstance().regist(this);
    }
//    @Override
//    public void change(NetWorkManager.NetWorkInfo netWorkInfo) {
//        if (!netWorkInfo.isNetAvailable) {
//
//            Toast.makeText(BaseActivity.this,"当前网络不可用，请检查网络设置！",Toast.LENGTH_SHORT).show();
//
//
//        }
//
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        NetWorkManager.getInstance().unregist(this);
    }
    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    public static void setColor(Activity activity, int color,boolean isColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            View statusView = createStatusView(activity, color,isColor);
            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    private static View createStatusView(Activity activity, int color,boolean isColor) {
        // 获得状态栏高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,statusBarHeight);
        statusView.setLayoutParams(params);
        if (isColor)
            statusView.setBackgroundColor(color);
        else
            statusView.setBackgroundResource(color);
        return statusView;
    }
    public void showProgressDialog(String title,String toast){
        progressDialog =ProgressDialog.show(this,title,toast,true,false);
        progressDialog.show();
    }
    public void dismissProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}

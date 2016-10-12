package com.offer9191.boss.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.SysApplication;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.fragment.CandidateFragment;
import com.offer9191.boss.fragment.OrderFragment;
import com.offer9191.boss.fragment.WebFragmentContainer;
import com.offer9191.boss.fragment.MyFragment;
import com.offer9191.boss.jsonbean.AppVersionJson;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.service.DownloadService;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/8/30.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private static String TAG="MAINACTIVITY";
    private long mExitTime;
    private List<Fragment> fragments=new ArrayList<>();
    @ViewInject(R.id.rg)RadioGroup rg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColor(this, ContextCompat.getColor(this,R.color.main_nav_bg),true);
        fragments.add(WebFragmentContainer.newInstance(Constants.WEB_URL+"BossApp/message/index.html?sessionid="+ MyInfoManager.getSessionID(MainActivity.this),"通知"));
        fragments.add(new OrderFragment());
        fragments.add(new CandidateFragment());
        fragments.add(WebFragmentContainer.newInstance(Constants.WEB_URL+"BossApp/report/index.html?sessionid="+ MyInfoManager.getSessionID(MainActivity.this),"报表"));
        fragments.add(new MyFragment());

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i=0;i<radioGroup.getChildCount();i++){
                    if (radioGroup.getChildAt(i).getId()==checkedId){
                        changeFragment(i);
                    }
                }
            }
        });
        changeFragment(0);
        getAppVersion();
    }

    private void changeFragment(int index){
        if (index==4){
            setColor(this,R.drawable.img_my_statue,false);
        }else{
            setColor(this, ContextCompat.getColor(this,R.color.main_nav_bg),true);
        }
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();

        Fragment currentFragment = fragments.get(index);
        if (currentFragment.isAdded()){
            currentFragment.onStart();
            ft.show(currentFragment);
        }else{
            ft.add(R.id.container,currentFragment);
            ft.show(currentFragment);
        }
        for (int i = 0; i < fragments.size(); i++) {
            if (i!=index){
                if (fragments.get(i).isAdded()){
                    fragments.get(i).onStop();
                    ft.hide(fragments.get(i));
                }
            }
        }
        ft.commitAllowingStateLoss();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                SysApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void getAppVersion(){
        RequestParams params=new RequestParams(Constants.URL+"api/Common/GetAppVersion");
        params.addBodyParameter("AppType", "1");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("GetAppVersion",result);
                try {
                    final AppVersionJson appVersionJson = GsonTools.changeGsonToBean(result,AppVersionJson.class);
                    if (appVersionJson.code==0){
                        String[] str =appVersionJson.data.update.newVersion.trim().split("\\.");
                        String versionStr=str[0]+str[1]+str[2];
                        String[] str2 =CommUtils.getVersionName(MainActivity.this).trim().split("\\.");
                        String versionStr2=str2[0]+str2[1]+str2[2];
                        int new_version =Integer.parseInt(versionStr);
                        int curr_version =Integer.parseInt(versionStr2);
                        if (new_version>curr_version){
                            if(appVersionJson.data.update.isMaintenanceMode){
                                final NormalDialog dialog = new NormalDialog(MainActivity.this);
                                dialog.content(appVersionJson.data.update.content)//
                                        .style(NormalDialog.STYLE_TWO)//
                                        .titleTextSize(18)//
                                        .title("版本更新")
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
                                                Intent serviceDownload=new Intent(MainActivity.this,DownloadService.class);
                                                if (CommUtils.isServiceRunning(MainActivity.this, "com.offer9191.boss.service.DownloadService")) {
                                                    MainActivity.this.stopService(serviceDownload);
                                                }
                                                serviceDownload.putExtra("url", appVersionJson.data.update.updateURL[0]);
                                                MainActivity.this.startService(serviceDownload);
                                                dialog.dismiss();
                                            }
                                        });
                            }else{
                                final NormalDialog dialog = new NormalDialog(MainActivity.this);
                                dialog.content(appVersionJson.data.update.content)//
                                        .style(NormalDialog.STYLE_TWO)//
                                        .titleTextSize(18)//
                                        .title("版本更新")
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
                                                Intent serviceDownload=new Intent(MainActivity.this,DownloadService.class);
                                                if (CommUtils.isServiceRunning(MainActivity.this, "com.offer9191.boss.service.DownloadService")) {
                                                    MainActivity.this.stopService(serviceDownload);
                                                }
                                                serviceDownload.putExtra("url", appVersionJson.data.update.updateURL[0]);
                                                MainActivity.this.startService(serviceDownload);
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        }
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

package com.offer9191.boss.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.AppVersionJson;
import com.offer9191.boss.service.DownloadService;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.NavigationLayout;
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

/**
 * Created by OfferJiShu01 on 2016/9/23.
 */
@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigation;
    @ViewInject(R.id.tv_version)TextView tv_version;
    @ViewInject(R.id.tv_currentversion)TextView tv_currentversion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigation.setCenterText(getString(R.string.about));
        navigation.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_version.setText("v"+CommUtils.getVersionName(this));
        tv_currentversion.setText("v"+CommUtils.getVersionName(this));
    }
    @Event(value = R.id.rl_version)
    private void versionClick(View view){
       getAppVersion();
    }
    @Event(value = R.id.rl_boss)
    private void bossdetailClick(View view){
        Intent intent =new Intent(AboutActivity.this, WebActivityContainer.class);
        intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
        intent.putExtra("url",Constants.WEB_URL+"BossApp/common/about.html");
        startActivity(intent);
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
                        String[] str2 =CommUtils.getVersionName(AboutActivity.this).trim().split("\\.");
                        String versionStr2=str2[0]+str2[1]+str2[2];
                        int new_version =Integer.parseInt(versionStr);
                        int curr_version =Integer.parseInt(versionStr2);
                        if (new_version>curr_version){
                            if(appVersionJson.data.update.isMaintenanceMode){
                                final NormalDialog dialog = new NormalDialog(AboutActivity.this);
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
                                                Intent serviceDownload=new Intent(AboutActivity.this,DownloadService.class);
                                                if (CommUtils.isServiceRunning(AboutActivity.this, "com.offer9191.boss.service.DownloadService")) {
                                                    AboutActivity.this.stopService(serviceDownload);
                                                }
                                                serviceDownload.putExtra("url", appVersionJson.data.update.updateURL[0]);
                                                AboutActivity.this.startService(serviceDownload);
                                                dialog.dismiss();
                                            }
                                        });
                            }else{
                                final NormalDialog dialog = new NormalDialog(AboutActivity.this);
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
                                                Intent serviceDownload=new Intent(AboutActivity.this,DownloadService.class);
                                                if (CommUtils.isServiceRunning(AboutActivity.this, "com.offer9191.boss.service.DownloadService")) {
                                                    AboutActivity.this.stopService(serviceDownload);
                                                }
                                                serviceDownload.putExtra("url", appVersionJson.data.update.updateURL[0]);
                                                AboutActivity.this.startService(serviceDownload);
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



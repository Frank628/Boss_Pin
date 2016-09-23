package com.offer9191.boss.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.offer9191.boss.R;
import com.offer9191.boss.utils.SharePrefUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by OfferJiShu01 on 2016/9/12.
 */
public class DownloadService extends Service {
    private String url="";
    private ProgressDialog progressDialog;
    private Notification notification;
    private NotificationManager notificationManager;
    private static final int APK_DOWNLOAD_NOTIFICATION=1111;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags=START_REDELIVER_INTENT;
        if (intent!=null) {
            url=intent.getStringExtra("url");
        }else{
            url= SharePrefUtil.getString(this, "url", "");
        }
        download(url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void forceUpdateView(){
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setTitle("正在下载更新...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        progressDialog.show();
    }
    private void dissmissForceView(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    private void statusUpdateView(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Boss聘下载")
                .setSmallIcon(R.mipmap.app_icon).build();
//        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
//        notification.contentIntent=pendingintent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        RemoteViews remote = new RemoteViews(getPackageName(), R.layout.layout_download_notification);
        notification.contentView = remote;
        notification.contentView.setProgressBar(R.id.progress, 100, 0, false);
        notification.contentView.setTextViewText(R.id.tv_progress,  0 + "%");
        notificationManager.notify(APK_DOWNLOAD_NOTIFICATION,notification);
    }
    private void download(String url){
        statusUpdateView();
        RequestParams params =new RequestParams(url.trim());
        params.setSaveFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"BossPin.apk");
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Environment .getExternalStorageDirectory(), "BossPin.apk")),"application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DownloadService.this.startActivity(intent);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dissmissForceView();
            }
            @Override
            public void onCancelled(CancelledException cex) {
                dissmissForceView();
            }
            @Override
            public void onFinished() {
                dissmissForceView();
            }
            @Override
            public void onWaiting() { }
            @Override
            public void onStarted() {}
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                progressDialog.setProgress((int) (current*100/total));
                Log.i("download",(int)(current*100/total)+"");
                notification.contentView.setProgressBar(R.id.progress, 100,(int)(current*100/total), false);
                notification.contentView.setTextViewText(R.id.tv_progress,(int)(current*100/total) + "%");
                notificationManager.notify(APK_DOWNLOAD_NOTIFICATION,notification);
            }
        });
    }
}

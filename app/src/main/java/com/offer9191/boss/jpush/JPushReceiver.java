package com.offer9191.boss.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.offer9191.boss.R;
import com.offer9191.boss.jsonbean.JPushJson;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.GsonTools;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.Event;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by OfferJiShu01 on 2016/9/27.
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private static final int NOTIFICATION_ID = 0x123;
    private NotificationManager nm;

    public JPushReceiver() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            processCustomMessage(context, bundle);
        }
    }
    private void processCustomMessage(Context context, Bundle bundle) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG,message+"----"+extras);
        try {
            JPushJson jpushExtra = GsonTools.changeGsonToBean(extras, JPushJson.class);
            nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            if (!TextUtils.isEmpty(jpushExtra.CandidateID)){
                sendNotification(context,"您推荐的候选人有新的动态");
            }
            if (!TextUtils.isEmpty(jpushExtra.JobOrderID)){
                sendNotification(context,"您有新的订单消息");
            }
            EventBus.getDefault().post(jpushExtra);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendNotification(Context context,String msg){
        Intent intent = new Intent(context, LoginActivity.class);
        // 单击Notification 通知时将会启动Intent 对应的程序，实现页面的跳转
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notify = new Notification.Builder(context)
                // 设置打开该通知，该通知自动消失
                .setAutoCancel(true)
                // 设置显示在状态栏的通知提示信息
                .setTicker("Boss聘有新消息")
                // 设置通知的图标
                .setSmallIcon(R.mipmap.app_icon)
                // 设置通知内容的标题
                .setContentTitle("Boss聘消息")
                // 设置通知内容
                .setContentText(msg)
                // // 设置使用系统默认的声音、默认LED灯
                // .setDefaults(Notification.DEFAULT_SOUND
                // |Notification.DEFAULT_LIGHTS)
//                // 设置通知的自定义声音
//                .setSound(
//                        Uri.parse(android.resource://org.crazyit.ui/+ R.raw.msg))
                 .setWhen(System.currentTimeMillis())
                // 设改通知将要启动程序的Intent
                .setContentIntent(pi)
                .getNotification();
                 // 发送通知
                  nm.notify(NOTIFICATION_ID, notify);
    }
}

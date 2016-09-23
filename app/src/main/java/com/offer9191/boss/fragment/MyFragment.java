package com.offer9191.boss.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.activity.WebActivityContainer;
import com.offer9191.boss.activity.my.ActivityPersonInfo;
import com.offer9191.boss.activity.my.ActivitySetting;
import com.offer9191.boss.base.BaseFragment;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.LoginJson;
import com.offer9191.boss.main.LoginActivity;
import com.offer9191.boss.utils.GsonTools;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by OfferJiShu01 on 2016/9/6.
 */
@ContentView(R.layout.fragment_my)
public class MyFragment extends BaseFragment {
//    @ViewInject(R.id.imageView1) RoundedImageView avatar;
    @ViewInject(R.id.avatar)SimpleDraweeView avatar;
    @ViewInject(R.id.tv_companyname) TextView tv_companyname;
    @ViewInject(R.id.tv_name) TextView tv_name;
    @Override
    public void onStart() {
        super.onStart();
        avatar.setImageResource(R.drawable.img_head_default);
        if (!TextUtils.isEmpty(MyInfoManager.getLoginJSON(getActivity()))){
            try {
                LoginJson loginJson = GsonTools.changeGsonToBean(MyInfoManager.getLoginJSON(getActivity()),LoginJson.class);
                tv_name.setText(MyInfoManager.getDisplayName(getActivity()));
                tv_companyname.setText(MyInfoManager.getCompanyName(getActivity()));
                if (MyInfoManager.getSEX(getActivity()).equals("男")){
                    Drawable drawable= getResources().getDrawable(R.drawable.sex_m);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    tv_name.setCompoundDrawables(null, null, drawable, null);
                }else{
                    Drawable drawable= getResources().getDrawable(R.drawable.sex_g);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    tv_name.setCompoundDrawables(null, null, drawable, null);
                }
                if (!TextUtils.isEmpty(MyInfoManager.getUserPreview(getContext()))){
                    avatar.setImageURI(Uri.parse(MyInfoManager.getUserPreview(getContext())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Intent intent =new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Event(value = R.id.rl_personinfo)
    private void personinfo(View view){
        Intent intent =new Intent(getActivity(), ActivityPersonInfo.class);
        startActivity(intent);
    }
    @Event(value = R.id.rl_setting)
    private void setting(View view){
        Intent intent =new Intent(getActivity(), ActivitySetting.class);
        startActivity(intent);
    }
    @Event(value = R.id.rl_qa)
    private void qaClick(View view){
        Intent intent =new Intent(getActivity(), WebActivityContainer.class);
        intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
        intent.putExtra("url",Constants.WEB_URL+"BossApp/common/qa.html");
        startActivity(intent);
    }
    @Event(value = R.id.rl_protocol)
    private void protocolClick(View view){
        Intent intent =new Intent(getActivity(), WebActivityContainer.class);
        intent.putExtra(Constants.IS_NEED_NAVIGATION,true);
        intent.putExtra("url",Constants.WEB_URL+"BossApp/common/qa.html");
        startActivity(intent);
    }
}

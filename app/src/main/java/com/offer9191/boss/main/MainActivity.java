package com.offer9191.boss.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.RadioGroup;

import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.fragment.CandidateFragment;
import com.offer9191.boss.fragment.OrderFragment;
import com.offer9191.boss.fragment.WebFragmentContainer;
import com.offer9191.boss.fragment.MyFragment;
import com.offer9191.boss.service.DownloadService;
import com.offer9191.boss.widget.Dialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/8/30.
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    private static String TAG="MAINACTIVITY";
    private List<Fragment> fragments=new ArrayList<>();
    @ViewInject(R.id.rg)RadioGroup rg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColor(this, ContextCompat.getColor(this,R.color.main_nav_bg),true);
        fragments.add(WebFragmentContainer.newInstance(Constants.WEB_URL+"BossApp/message/index.html"));
        fragments.add(new OrderFragment());
        fragments.add(new CandidateFragment());
        fragments.add(WebFragmentContainer.newInstance(Constants.WEB_URL+"BossApp/report/index.html"));
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
        checkUpdate();
    }
    private void checkUpdate(){
        Dialog.showSelectDialog(this,"发现新版本", "1.增加视频功能以及会议功能；\n2.bug修复", new Dialog.DialogClickListener() {
            @Override
            public void confirm() {
                Intent intent =new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra("url","http://img.9191offer.com:8888/offer/VISS/AppSetting/Offer_Android.apk");
                startService(intent);
            }

            @Override
            public void cancel() {

            }
        });
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
}

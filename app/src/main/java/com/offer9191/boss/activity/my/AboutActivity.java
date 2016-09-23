package com.offer9191.boss.activity.my;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by OfferJiShu01 on 2016/9/23.
 */
@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigation;
    @ViewInject(R.id.tv_version)TextView tv_version;
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
    }
}



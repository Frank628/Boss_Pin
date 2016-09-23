package com.offer9191.boss.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.SharePrefUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/19.
 */
@ContentView(R.layout.activity_guid)
public class GuidActivity extends BaseActivity {
    @ViewInject(R.id.viewpager)ViewPager viewPager;
    @ViewInject(R.id.ll_bottom)LinearLayout ll_bottom;
    @ViewInject(R.id.btn_open)Button btn_open;
    private List<View> views=new ArrayList<>();
    private List<View> dots=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharePrefUtil.saveBoolean(this, Constants.IS_SHOW_GUID,false);
        int[] splashImage = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        for (int i = 0; i < splashImage.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(splashImage[i]);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(iv);
        }
        for (int i = 0; i < splashImage.length; i++) {
            View view = new ImageView(this);
            view.setBackgroundResource(R.drawable.dot_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommUtils.dip2px(this,8), CommUtils.dip2px(this,8));
            params.setMargins(CommUtils.dip2px(this,8),0,CommUtils.dip2px(this,8),0);
            view.setLayoutParams(params);
            dots.add(view);
            ll_bottom.addView(view);
        }
        viewPager.setAdapter(new PagerAdapter() {
                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    ((ViewPager) container).addView(views.get(position), 0);
                    return views.get(position);
                }
                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == (arg1);
                }

                @Override
                public int getCount() {
                    return views.size();
                }

                @Override
                public void destroyItem(View container, int position, Object object) {
                    ((ViewPager) container).removeView(views.get(position));
                }
        });
        dots.get(0).setBackgroundResource(R.drawable.dot_over);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0==(views.size()-1)) {
                    btn_open.setVisibility(View.VISIBLE);
                }else{
                    btn_open.setVisibility(View.GONE);
                }
                for (int i=0;i<dots.size();i++){
                    if (arg0==i){
                        dots.get(i).setBackgroundResource(R.drawable.dot_over);
                    }else{
                        dots.get(i).setBackgroundResource(R.drawable.dot_normal);
                    }
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
    @Event(value = R.id.btn_open)
    private void open(View view){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        GuidActivity.this.finish();
    }
}

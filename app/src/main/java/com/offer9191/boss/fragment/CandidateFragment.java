package com.offer9191.boss.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.offer9191.boss.R;
import com.offer9191.boss.activity.candidate.AddCandidateActivity;
import com.offer9191.boss.activity.candidate.CandidateFilterActivity;
import com.offer9191.boss.activity.my.ActivitySetting;
import com.offer9191.boss.activity.order.OrderFilterActivity;
import com.offer9191.boss.base.BaseFragment;
import com.offer9191.boss.fragment.candidate.CandidateSubFragment;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.PagerSlidingTabStrip;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/7.
 */
@ContentView(R.layout.fragment_cadidate)
public class CandidateFragment extends BaseFragment {
    public static final int FILTER_REQUEST=1101;
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.viewpager)ViewPager viewpager;
    @ViewInject(R.id.tabs)PagerSlidingTabStrip tabs;
    private static final String[] CONTENT = new String[] { "待审核", "已通过", "未通过" };
    private String key="",candidateGender="",ageFrom="",ageTo="";
    private List<CityJson.DistrictsOne> zhinenglist;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationLayout.setLeftText(getResources().getString(R.string.tab3), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        navigationLayout.setRightText(getResources().getString(R.string.add), R.drawable.candidate_btn_add, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(), AddCandidateActivity.class);
                intent.putExtra("name",getResources().getString(R.string.add_candidate));
                startActivity(intent);
            }
        });
        navigationLayout.setRight2Text("", R.drawable.candidate_btn_search, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(), CandidateFilterActivity.class);
                intent.putExtra("zhineng", (Serializable)zhinenglist);
                intent.putExtra("gender", candidateGender);
                intent.putExtra("key", key);
                intent.putExtra("ageFrom", ageFrom);
                intent.putExtra("ageTo", ageTo);
                startActivityForResult(intent,FILTER_REQUEST);
            }
        });
        viewpager.setAdapter(new CandidatesFragmentAdapter(getChildFragmentManager()));
        tabs.setViewPager(viewpager);
        viewpager.setCurrentItem(1);
    }
    @Event(value = R.id.rl_setting)
    private void setting(View view){

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    class CandidatesFragmentAdapter extends FragmentPagerAdapter{
        public CandidatesFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CandidateSubFragment.newInstance(position,CONTENT[position % CONTENT.length].toUpperCase());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==getActivity().RESULT_OK){
            for(Fragment fragment:getChildFragmentManager().getFragments()){
                fragment.onActivityResult(requestCode, resultCode, data);
            }
            switch (requestCode){
                case 1101:
                    key=data.getStringExtra("key");
                    candidateGender=data.getStringExtra("gender");
                    ageFrom=data.getStringExtra("ageFrom");
                    ageTo=data.getStringExtra("ageTo");
                    zhinenglist=(List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    break;
            }
        }
    }
}

package com.offer9191.boss.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.adapter.TwoStageAdapter;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.FileUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.FlowLayout;
import com.offer9191.boss.widget.NavigationLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/22.
 */
@ContentView(R.layout.activity_position)
public class IndustryActivity extends BaseActivity {
    @ViewInject(R.id.fl_selected) private FlowLayout fl_selected;
    @ViewInject(R.id.el_list)private ExpandableListView el_list;
    @ViewInject(R.id.navigation)private NavigationLayout navigationLayout;
    private TwoStageAdapter adapter;
    private List<CityJson.CityOne> hangyelist=new ArrayList<>();
    private int maxNum=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getString(R.string.posiotion_select));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IndustryActivity.this.finish();
            }
        });
        navigationLayout.setRightText(getString(R.string.ensure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.putExtra("hangye", (Serializable)hangyelist);
                setResult(RESULT_OK, intent);
                IndustryActivity.this.finish();
            }
        });
        initData();
    }

    private void initData(){
        if ((List<CityJson.CityOne>) getIntent().getSerializableExtra("hangye")!=null) {
            hangyelist=(List<CityJson.CityOne>) getIntent().getSerializableExtra("hangye");
        }
        try {
            CityJson cityBean =GsonTools.changeGsonToBean(FileUtils.getAssetsTxt(IndustryActivity.this,"industry.txt"),CityJson.class) ;
            if (cityBean.code==0) {
                adapter=new TwoStageAdapter(IndustryActivity.this, cityBean.data.list,maxNum,new TwoStageAdapter.OnChildSelectedListener() {
                    @Override
                    public void OnChildSelected(final List<CityJson.CityOne> list) {
                        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                        hangyelist=list;
                        fl_selected.removeAllViews();
                        for (int i = 0; i < list.size(); i++) {
                            View view = layoutInflater.inflate(R.layout.item_selected_text, null);
                            TextView tv = (TextView) view.findViewById(R.id.tv_name);
                            tv.setText(list.get(i).CodeValue);
                            final CityJson.CityOne cityOne =list.get(i);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    adapter.removeFilterItem(cityOne);
                                }
                            });
                            View newView = view;
                            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                            params.rightMargin = 10;
                            params.topMargin = 8;
                            params.leftMargin = 10;
                            params.bottomMargin = 8;
                            newView.setLayoutParams(params);
                            fl_selected.addView(newView);
                        }
                    }
                });
                adapter.setFilter(hangyelist);
                el_list.setAdapter(adapter);
                el_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        for (int i = 0; i < adapter.getGroupCount(); i++) {
                            if (groupPosition != i) {
                                el_list.collapseGroup(i);
                            }
                        }
                    }
                });
                el_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

                        return true;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

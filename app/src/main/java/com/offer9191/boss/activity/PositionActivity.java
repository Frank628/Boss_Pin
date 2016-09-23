package com.offer9191.boss.activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.R;
import com.offer9191.boss.adapter.PostionAdapter;
import com.offer9191.boss.adapter.baseadpater.CommonAdapter;
import com.offer9191.boss.adapter.baseadpater.ViewHolder;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.CityJsonUtils;
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
 * Created by OfferJiShu01 on 2016/9/20.
 */
@ContentView(R.layout.activity_position)
public class PositionActivity extends BaseActivity {
    @ViewInject(R.id.fl_selected) private FlowLayout fl_selected;
    @ViewInject(R.id.el_list)private ExpandableListView el_list;
    @ViewInject(R.id.navigation)private NavigationLayout navigationLayout;
    private PostionAdapter adapter;
    private PopupWindow dPopwin;
    private List<CityJson.DistrictsOne> zhinenglist=new ArrayList<>();
    private CityJson cityBean;
    private int maxNum=5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getString(R.string.posiotion_select));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PositionActivity.this.finish();
            }
        });
        navigationLayout.setRightText(getString(R.string.ensure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.putExtra("zhineng", (Serializable)zhinenglist);
                setResult(RESULT_OK, intent);
                PositionActivity.this.finish();
            }
        });
        maxNum=getIntent().getIntExtra("maxNum", 5);
        initData();
    }

    private void initData(){
        if ((List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng")!=null) {
            zhinenglist=(List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng");
            addFilterTag();
        }
        try {
            String cityString="";
            if (getIntent().getBooleanExtra("isCity",true)){
                cityString= FileUtils.getAssetsTxt(PositionActivity.this, "city.txt");
            }else{
                cityString= FileUtils.getAssetsTxt(PositionActivity.this, "position.txt");
            }
            cityBean = GsonTools.changeGsonToBean(cityString, CityJson.class);
            if (cityBean.code==0) {
                adapter=new PostionAdapter(this, cityBean.data.list);
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
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        initDistPop(cityBean.data.list.get(groupPosition).Citys.get(childPosition).Districts);
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initDistPop(List<CityJson.DistrictsOne> districtOnes){
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.pop_position, null);
        dPopwin = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        dPopwin.setBackgroundDrawable(new BitmapDrawable());
        dPopwin.showAtLocation(findViewById(R.id.root), Gravity.CENTER, 0, 0);
        dPopwin.update();
        ListView lv = (ListView) layout.findViewById(R.id.lv);
        ImageView iv_close=(ImageView) layout.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dPopwin.dismiss();
            }
        });
        CommonAdapter<CityJson.DistrictsOne> disAdapter=new CommonAdapter<CityJson.DistrictsOne>(this,districtOnes,R.layout.item_city_check_child) {
            @Override
            public void convert(ViewHolder helper, final CityJson.DistrictsOne item, int position) {
                helper.setText(R.id.tv_name, item.CodeValue);
                if (CityJsonUtils.containsDISObj(zhinenglist, item.CodeID)) {
                    helper.getView(R.id.iv_gou).setVisibility(View.VISIBLE);
                }else{
                    helper.getView(R.id.iv_gou).setVisibility(View.INVISIBLE);
                }
                helper.getView(R.id.rl_child).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CityJsonUtils.containsDISObj(zhinenglist, item.CodeID)) {
                            v.findViewById(R.id.iv_gou).setVisibility(View.INVISIBLE);
                            CityJsonUtils.removeDISFromList(zhinenglist, item.CodeID);
                            addFilterTag();
                        }else{
                            if (zhinenglist.size()>=maxNum) {
                                Toast.makeText(PositionActivity.this, "最多选择"+maxNum+"项！", Toast.LENGTH_SHORT).show();
                            }else{
                                v.findViewById(R.id.iv_gou).setVisibility(View.VISIBLE);
                                zhinenglist.add(item);
                                addFilterTag();
                            }
                        }
                    }
                });
            }
        };
        lv.setAdapter(disAdapter);
    }
    private void addFilterTag(){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        fl_selected.removeAllViews();
        for (int i = 0; i < zhinenglist.size(); i++) {
            View view = layoutInflater.inflate(R.layout.item_selected_text, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_name);
            tv.setText(zhinenglist.get(i).CodeValue);
            final CityJson.DistrictsOne districtOne =zhinenglist.get(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CityJsonUtils.removeDISFromList(zhinenglist, districtOne.CodeID);
                    addFilterTag();
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


}

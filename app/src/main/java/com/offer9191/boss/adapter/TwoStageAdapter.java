package com.offer9191.boss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.R;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.CityJsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/22.
 */
public class TwoStageAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CityJson.ProvinceOne> group;
    private LayoutInflater inflater;
    private int groupSelected=-1,childSelected=-1;
    private OnChildSelectedListener onChildSelectedListener;
    private List<CityJson.CityOne> mlist=new ArrayList<CityJson.CityOne>();
    private int maxNum;
    public TwoStageAdapter(Context context, List<CityJson.ProvinceOne> group,int maxNum,OnChildSelectedListener onChildSelectedListener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.group = group;
        this.onChildSelectedListener=onChildSelectedListener;
        this.maxNum=maxNum;
    }
    public void setSelectedPosition(int groupSelected,int childSelected){
        this.groupSelected=groupSelected;
        this.childSelected=childSelected;
    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return group.get(groupPosition).Citys.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_city_check_child, null);
            holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_gou=(ImageView) convertView.findViewById(R.id.iv_gou);
            holder.rl_child=(RelativeLayout) convertView.findViewById(R.id.rl_child);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(group.get(groupPosition).Citys.get(childPosition).CodeValue);
        holder.iv_gou.setVisibility(View.INVISIBLE);
        holder.tv_name.setTextColor(context.getResources().getColor(R.color.title2));
        if (CityJsonUtils.containsObj(mlist, group.get(groupPosition).Citys.get(childPosition).CodeID)) {
            holder.iv_gou.setVisibility(View.VISIBLE);
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.main_nav_bg));
        }
        holder.rl_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CityJsonUtils.containsObj(mlist, group.get(groupPosition).Citys.get(childPosition).CodeID)) {
                    CityJsonUtils.removeFromList(mlist, group.get(groupPosition).Citys.get(childPosition).CodeID);
                    v.findViewById(R.id.iv_gou).setVisibility(View.INVISIBLE);
                }else{
                    if (mlist.size()>=maxNum) {
                        Toast.makeText(context, "最多选择"+maxNum+"项！", Toast.LENGTH_SHORT).show();
                    }else{
                        mlist.add(group.get(groupPosition).Citys.get(childPosition));
                        v.findViewById(R.id.iv_gou).setVisibility(View.VISIBLE);
                    }
                }
                onChildSelectedListener.OnChildSelected(mlist);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//		return groupPosition==0?0:group.get(groupPosition).Citys.size();
        return group.get(groupPosition).Citys.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_city_group, null);
            holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_arrow=(ImageView) convertView.findViewById(R.id.iv_arrow);
            holder.group_rl=(RelativeLayout) convertView.findViewById(R.id.group_rl);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(group.get(groupPosition).CodeValue);
        if (isExpanded) {
            holder.iv_arrow.setImageResource(R.drawable.icon_arrow_down);
        }else{
            holder.iv_arrow.setImageResource(R.drawable.icon_arrow_next);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }
    public void setFilter(List<CityJson.CityOne> list){
        mlist=list;
        onChildSelectedListener.OnChildSelected(mlist);
        notifyDataSetChanged();
    }
    public void removeFilterItem(CityJson.CityOne cityOne){
        if (CityJsonUtils.containsObj(mlist, cityOne.CodeID)) {
            CityJsonUtils.removeFromList(mlist, cityOne.CodeID);
            notifyDataSetChanged();
            onChildSelectedListener.OnChildSelected(mlist);
        }
    }
    public interface OnChildSelectedListener{
        public void OnChildSelected(List<CityJson.CityOne> list);
    }
    private final class ViewHolder {
        private ImageView iv_arrow,iv_gou;
        private TextView tv_name;
        private RelativeLayout group_rl,rl_child;
    }
}

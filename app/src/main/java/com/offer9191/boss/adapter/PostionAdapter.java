package com.offer9191.boss.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.jsonbean.CityJson;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/20.
 */
public class PostionAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CityJson.ProvinceOne> group;
    private LayoutInflater inflater;

    public PostionAdapter(Context context, List<CityJson.ProvinceOne> group) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.group = group;
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
//		if (groupPosition==groupSelected&&childPosition==childSelected) {
//			holder.iv_gou.setVisibility(View.VISIBLE);
//			holder.tv_name.setTextColor(Color.RED);
//		}else{
//			holder.iv_gou.setVisibility(View.GONE);
//			holder.tv_name.setTextColor(Color.BLACK);
//		}
        holder.tv_name.setText(group.get(groupPosition).Citys.get(childPosition).CodeValue);
        holder.iv_gou.setVisibility(View.INVISIBLE);

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
//		if (groupPosition==0) {
//			holder.group_rl.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent =new Intent();
//					intent.putExtra("city",new CityOne("全国"));
//					context.setResult(context.RESULT_OK, intent);
//					context.finish();
//
//				}
//			});
//		}
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
    public interface OnChildSelectedListener{
        public void OnChildSelected(List<CityJson.CityOne> list);
    }
    private final class ViewHolder {
        private ImageView iv_arrow,iv_gou;
        private TextView tv_name;
        private RelativeLayout group_rl,rl_child;
    }
}

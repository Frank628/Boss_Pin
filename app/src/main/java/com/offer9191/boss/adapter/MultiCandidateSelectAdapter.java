package com.offer9191.boss.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.offer9191.boss.R;
import com.offer9191.boss.jsonbean.CandidateListJson;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/20.
 */
public class MultiCandidateSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CandidateListJson.CandidateOne> list;
    private Context context;
    private OnMultiSelected onMultiSelected;
    public List<String> listSelected=new ArrayList<String>();
    public interface OnMultiSelected {
        public void onSelected(List<String> list);
    }
    public MultiCandidateSelectAdapter(List<CandidateListJson.CandidateOne> list, Context context, OnMultiSelected onMultiSelected) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
        this.onMultiSelected=onMultiSelected;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder =null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_muticandidate_select, null);
            holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_position=(TextView) convertView.findViewById(R.id.tv_position);
            holder.tv_industry=(TextView) convertView.findViewById(R.id.tv_industry);
            holder.tv_phone=(TextView) convertView.findViewById(R.id.tv_phone);
            holder.checkbox=(CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(list.get(position).CandidateName);
        holder.tv_position.setText(list.get(position).JobTypeCodeNames);
        holder.tv_industry.setText(list.get(position).VocationNames);
        holder.tv_phone.setText(list.get(position).CandidateMobile);
        Drawable drawable=context.getResources().getDrawable(R.drawable.sex_m);
        if (list.get(position).CandidateGender.trim().equals("男")){
            drawable = context.getResources().getDrawable(R.drawable.sex_m);
        }else{
            drawable = context.getResources().getDrawable(R.drawable.sex_g);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
        holder.tv_name.setCompoundDrawables(null, null, drawable, null);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!listSelected.contains(list.get(position).CandidateID)) {
                        listSelected.add(list.get(position).CandidateID);
                    }
                }else{
                    if (listSelected.contains(list.get(position).CandidateID)) {
                        listSelected.remove(list.get(position).CandidateID);
                    }
                }
                notifyDataSetChanged();
                onMultiSelected.onSelected(listSelected);
            }
        });
        if (listSelected.contains(list.get(position).CandidateID)) {
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }
        return convertView;
    }
    private final class ViewHolder {
        private TextView tv_name,tv_position,tv_industry,tv_phone;
        private CheckBox checkbox;
    }
}

package com.offer9191.boss.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.offer9191.boss.MyInfoManager;
import com.offer9191.boss.R;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.config.Constants;
import com.offer9191.boss.jsonbean.CandidateListJson;
import com.offer9191.boss.jsonbean.JobOrderListJson;
import com.offer9191.boss.jsonbean.SimpleJson;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.utils.GsonTools;
import com.offer9191.boss.widget.mydialog.animation.BounceEnter.BounceTopEnter;
import com.offer9191.boss.widget.mydialog.animation.SlideExit.SlideBottomExit;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnBtnClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.NormalDialog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/20.
 */
public class MultiOrderSelectAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<JobOrderListJson.JobOrderOne> list;
    private Context context;
    private OnMultiSelected onMultiSelected;
    public List<String> listSelected=new ArrayList<String>();
    public List<String> listSelected_id=new ArrayList<String>();
    public interface OnMultiSelected {
        public void onSelected(List<String> list,List<String> list2);
    }
    public MultiOrderSelectAdapter(List<JobOrderListJson.JobOrderOne> list, Context context, OnMultiSelected onMultiSelected) {
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
            convertView=inflater.inflate(R.layout.item_mutiorder_select, null);
            holder.tv_position=(TextView) convertView.findViewById(R.id.tv_position);
            holder.tv_address=(TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_time=(TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_company=(TextView) convertView.findViewById(R.id.tv_company);
            holder.tv_status=(TextView) convertView.findViewById(R.id.tv_status);
            holder.tv_salary=(TextView) convertView.findViewById(R.id.tv_salary);
            holder.checkbox=(CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.tv_position.setText(list.get(position).JobTitle);
        holder.tv_address.setText(list.get(position).ProvinceName+"-"+list.get(position).CityName+" | 发布："+list.get(position).MyName);
        holder.tv_company.setText(list.get(position).CompanyName);
        holder.tv_status.setText( CommUtils.getOrderStatus(list.get(position).JobOrderStatus));
        holder.tv_salary.setText(list.get(position).PositionLevel);
        holder.tv_time.setText(list.get(position).CreatedTime);
        holder.tv_salary.setBackgroundResource(CommUtils.getSalaryBackground(list.get(position).PositionLevel));
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (CommUtils.getOrderStatus(list.get(position).JobOrderStatus).equals("未处理")){
                        buttonView.setChecked(false);
                        final NormalDialog dialog = new NormalDialog(context);
                        dialog.content(context.getString(R.string.is_undertake_order))//
                                .isTitleShow(true)
                                .title(context.getString(R.string.not_undertake_order))
                                .titleTextSize(17)
                                .titleTextColor(R.color.main_nav_bg)
                                .style(NormalDialog.STYLE_TWO)//
                                .showAnim(new BounceTopEnter())//
                                .dismissAnim(new SlideBottomExit())//
                                .show();

                        dialog.setOnBtnClickL(
                                new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        dialog.dismiss();
                                    }
                                },
                                new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        undertakeOrder(list.get(position).JobOrderID,position);
                                        dialog.dismiss();
                                    }
                                });
                        return;
                    }
                    if (!listSelected.contains(list.get(position).JobOrderID)) {
                        listSelected.add(list.get(position).JobOrderID);
                    }
                    if (!listSelected_id.contains(list.get(position).CompanyInterviewJobId)) {
                        listSelected_id.add(list.get(position).CompanyInterviewJobId);
                    }
                }else{
                    if (listSelected.contains(list.get(position).JobOrderID)) {
                        listSelected.remove(list.get(position).JobOrderID);
                    }
                    if (listSelected_id.contains(list.get(position).CompanyInterviewJobId)) {
                        listSelected_id.remove(list.get(position).CompanyInterviewJobId);
                    }
                }
                notifyDataSetChanged();
                onMultiSelected.onSelected(listSelected,listSelected_id);
            }
        });
        if (listSelected.contains(list.get(position).JobOrderID)) {
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }
        return convertView;
    }
    private final class ViewHolder {
        private TextView tv_position,tv_address,tv_status,tv_company,tv_time,tv_salary;
        private CheckBox checkbox;
    }
    private void undertakeOrder(String orderId,final int position){
        ((BaseActivity)context).showProgressDialog("",context.getString(R.string.data_uploading));
        RequestParams params=new RequestParams(Constants.URL+"api/JobOrder/AcceptJobOrder");
        params.addBodyParameter("sessionId", MyInfoManager.getSessionID(context));
        params.addBodyParameter("jobOrderId",orderId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("AcceptJobOrder",result);
                try {
                    SimpleJson simpleJson = GsonTools.changeGsonToBean(result,SimpleJson.class);
                    if (simpleJson.code==0){
                        list.get(position).JobOrderStatus=CommUtils.getOrderStatusCode("已承接");
                        MultiOrderSelectAdapter.this.notifyDataSetChanged();
                    }else{
                        Toast.makeText(context,simpleJson.msg,Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                ((BaseActivity)context).dismissProgressDialog();
            }
        });
    }
}

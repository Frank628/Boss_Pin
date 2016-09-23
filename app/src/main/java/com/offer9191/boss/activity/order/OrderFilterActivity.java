package com.offer9191.boss.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.offer9191.boss.R;
import com.offer9191.boss.activity.PositionActivity;
import com.offer9191.boss.base.BaseActivity;
import com.offer9191.boss.jsonbean.CityJson;
import com.offer9191.boss.utils.CityJsonUtils;
import com.offer9191.boss.utils.CommUtils;
import com.offer9191.boss.widget.NavigationLayout;
import com.offer9191.boss.widget.mydialog.dialog.listener.OnOperItemClickL;
import com.offer9191.boss.widget.mydialog.dialog.widget.ActionSheetDialogWechat;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/8.
 */
@ContentView(R.layout.activity_orderfilter)
public class OrderFilterActivity extends BaseActivity {
    @ViewInject(R.id.navigation)NavigationLayout navigationLayout;
    @ViewInject(R.id.tv_position)TextView tv_position;
    @ViewInject(R.id.tv_status)TextView tv_status;
    private static final int POSITION_REQUEST=1;
    private List<CityJson.DistrictsOne> zhinenglist;
    private String status="",zhineng="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationLayout.setCenterText(getResources().getString(R.string.order_filter));
        navigationLayout.setLeftText("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if ((List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng")!=null) {
            zhinenglist=(List<CityJson.DistrictsOne>) getIntent().getSerializableExtra("zhineng");
            zhineng= CityJsonUtils.listGetDISCode(zhinenglist);
            tv_position.setText(CityJsonUtils.listGetZhiValue(zhinenglist));
        }
        status=getIntent().getStringExtra("status");
        if (!TextUtils.isEmpty(status)){
            tv_status.setText(CommUtils.getOrderStatus(status));
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==RESULT_OK && data != null) {
            switch (requestCode) {
                case POSITION_REQUEST:
                    zhinenglist = (List<CityJson.DistrictsOne>) data.getSerializableExtra("zhineng");
                    zhineng= CityJsonUtils.listGetDISCode(zhinenglist);
                    tv_position.setText(CityJsonUtils.listGetZhiValue(zhinenglist));
                    break;
                default:
                    break;
            }
        }
    }
    @Event(value = R.id.rl_status)
    private void status(View view){
        final String[] stringItems = {"不限","已承接", "未处理"};
        final ActionSheetDialogWechat actionSheetDialogWechat=new ActionSheetDialogWechat(this,stringItems,null);
        actionSheetDialogWechat.isTitleShow(false).show();
        actionSheetDialogWechat.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_status.setText(stringItems[position]);
                status=stringItems[position].equals(stringItems[0])?"": CommUtils.getOrderStatusCode(stringItems[position]);
                actionSheetDialogWechat.dismiss();
            }
        });
    }
    @Event(value = R.id.rl_position)
    private void position(View view){
        Intent intent =new Intent();
        intent.setClass(this, PositionActivity.class);
        intent.putExtra("zhineng", (Serializable)zhinenglist);
        startActivityForResult(intent, POSITION_REQUEST);
    }
    @Event(value = R.id.btn_submit)
    private void search(View view){
        Intent intent =new Intent();
        intent.putExtra("position", zhineng);
        intent.putExtra("status", status);
        intent.putExtra("zhineng", (Serializable)zhinenglist);
        setResult(RESULT_OK, intent);
        OrderFilterActivity.this.finish();
    }
}

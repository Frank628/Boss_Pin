package com.offer9191.boss.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.offer9191.boss.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by OfferJiShu01 on 2016/9/2.
 */
public class CommUtils {
    public static void  share(String title, String comment, String site, String siteurl, String ImageUrl, Context context){
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(site);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(context);

    }

    public static boolean isPassword(String pass){
        if (pass==null)return false;
        if(pass.length()<6)
            return false;
        else
            return true;
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getOrderStatus(String value){
        Map<String,String> map =new HashMap<>();
        map.put("1603180944196283322f0f78f9c32","已承接");
        map.put("16031809442990191986f5295ec2a","未承接");
        map.put("16031809443853841384c6aaa2f40","已放弃");
        map.put("160407163145317364649f0b04ec1","未处理");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }
    public static String getOrderStatusCode(String value){
        Map<String,String> map =new HashMap<>();
        map.put("已承接","1603180944196283322f0f78f9c32");
        map.put("未承接","16031809442990191986f5295ec2a");
        map.put("已放弃","16031809443853841384c6aaa2f40");
        map.put("未处理","160407163145317364649f0b04ec1");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }
    public static String getCVStatus(String value){
        Map<String,String> map =new HashMap<>();
        map.put("1604111001102556828f262603084","待审核");
        map.put("1604111003082134296413e504208","已通过");
        map.put("1604111004430268526c50eb52d74","未通过");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }
    public static String getCVStatusCode(String value){
        Map<String,String> map =new HashMap<>();
        map.put("待审核","1604111001102556828f262603084");
        map.put("已通过","1604111003082134296413e504208");
        map.put("未通过","1604111004430268526c50eb52d74");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }

    public static String getStringfromList(List<String> list) {
        if (list==null) {
            return "";
        }
        if (list.size() == 0) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                str.append(list.get(i));
                if (i != list.size() - 1) {
                    str.append(",");
                }
            }
            return str.toString();
        }
    }
    public static String getCandidateStatus(String value){
        Map<String,String> map =new HashMap<>();
        map.put("16031713433853004518671a3c2e4","简历已审核");
        map.put("1603171343511797686fe87c9d6ec","已推荐面试");
        map.put("160317134406437641391ec449627","面试通过");
        map.put("160317134423720629948b513e82a","已上岗");
        map.put("1603171344402955779c0db32cf54","第一笔回款");
        map.put("16031713444849404687a71217f4c","第二笔回款");
        map.put("160317134505976046714a544596a","已完成");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }
    public static String getCandidateStatusCode(String value){
        Map<String,String> map =new HashMap<>();
        map.put("简历已审核","16031713433853004518671a3c2e4");
        map.put("已推荐面试","1603171343511797686fe87c9d6ec");
        map.put("面试通过","160317134406437641391ec449627");
        map.put("已上岗","160317134423720629948b513e82a");
        map.put("第一笔回款","1603171344402955779c0db32cf54");
        map.put("第二笔回款","16031713444849404687a71217f4c");
        map.put("已完成","160317134505976046714a544596a");
        if (map.get(value)!=null)
            return map.get(value);
        else
            return "";
    }

    public static int  getSalaryBackground(String str){
        int res= R.drawable.img_blue;
        if (str.contains("20")){
            res=R.drawable.img_blue;
        }else if(str.contains("30")){
            res=R.drawable.img_yellow;
        }else if(str.contains("50")){
            res=R.drawable.img_green;
        }else if(str.contains("100")){
            res=R.drawable.img_red;
        }
        return res;
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }
    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,0-9])|(17[0,0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

}

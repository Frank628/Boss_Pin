package com.offer9191.boss;

import android.content.Context;

import com.offer9191.boss.config.Constants;
import com.offer9191.boss.utils.SharePrefUtil;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class MyInfoManager {

    public static String getUserName(Context context){
        return SharePrefUtil.getString(context, Constants.USER_NAME,"");
    }
    public static void setUserName(Context context,String str){
         SharePrefUtil.saveString(context, Constants.USER_NAME,str);
    }
    public static String getDisplayName(Context context){
        return SharePrefUtil.getString(context, Constants.DISPLAY_NAME,"");
    }
    public static void setDisplayName(Context context,String str){
        SharePrefUtil.saveString(context, Constants.DISPLAY_NAME,str);
    }
    public static String getCompanyName(Context context){
        return SharePrefUtil.getString(context, Constants.COMPANY_NAME,"");
    }
    public static void setCompanyName(Context context,String str){
        SharePrefUtil.saveString(context, Constants.COMPANY_NAME,str);
    }
    public static String getSEX(Context context){
        return SharePrefUtil.getString(context, Constants.SEX,"");
    }
    public static void setSEX(Context context,String str){
        SharePrefUtil.saveString(context, Constants.SEX,str);
    }
    public static String getPhone(Context context){
        return SharePrefUtil.getString(context, Constants.MOBILE,"");
    }
    public static void setPhone(Context context,String str){
        SharePrefUtil.saveString(context, Constants.MOBILE,str);
    }
    public static String getPassword(Context context){
        return SharePrefUtil.getString(context, Constants.PASSWORD,"");
    }
    public static void setPassword(Context context,String str){
        SharePrefUtil.saveString(context, Constants.PASSWORD,str);
    }

    public static String getSessionID(Context context){
        return SharePrefUtil.getString(context, Constants.SESSION_ID,"");
    }
    public static void setSessionID(Context context,String str){
        SharePrefUtil.saveString(context, Constants.SESSION_ID,str);
    }

    public static String getLoginJSON(Context context){
        return SharePrefUtil.getString(context, Constants.LOGIN_JSON,"");
    }
    public static void setLoginJSON(Context context,String str){
        SharePrefUtil.saveString(context, Constants.LOGIN_JSON,str);
    }

    public static String getUserPreview(Context context){
        return SharePrefUtil.getString(context, Constants.USER_PREVIEW,"");
    }
    public static void setUserPreview(Context context,String str){
        SharePrefUtil.saveString(context, Constants.USER_PREVIEW,str);
    }
}

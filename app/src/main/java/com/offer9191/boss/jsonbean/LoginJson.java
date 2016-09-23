package com.offer9191.boss.jsonbean;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class LoginJson {
    public int code;
    public String msg="";
    public DataLogin data;
    public static class DataLogin{
        public String UserId="";
        public String UserName="";
        public String DisplayName="";
        public String MobilePhoneNumber="";
        public String UserType="";
        public String Sex="";
        public String HDpic="";
        public String SessionId="";
        public String PartnersName="";
    }
}

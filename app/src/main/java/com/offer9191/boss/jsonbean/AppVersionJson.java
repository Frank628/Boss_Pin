package com.offer9191.boss.jsonbean;

/**
 * Created by OfferJiShu01 on 2016/9/27.
 */
public class AppVersionJson {
    public int code;
    public String msg="";
    public Version data;
    public static class Version{
        public UpdateV update;
    }
    public static class UpdateV{
        public String content="";
        public String FileUrl ="";
        public String fileSize="";
        public String hasPushNotice="";
        public boolean isMaintenanceMode;
        public String minVersion="";
        public String newVersion="";
        public String pushNotice="";
        public String updateDate="";
        public String[] updateURL;
    }
}

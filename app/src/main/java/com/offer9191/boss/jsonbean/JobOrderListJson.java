package com.offer9191.boss.jsonbean;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class JobOrderListJson {
    public int code;
    public String msg="";
    public DataJobOrderList data;
    public static class DataJobOrderList{
        public List<JobOrderOne> jobList;
        public int pages;
        public int total;
    }
    public static class JobOrderOne{
        public String CityName="";
        public String CompanyInterviewJobId="";
        public String JobOrderID="";
        public String JobOrderStatus="";
        public String JobTitle="";
        public String JobTypeCode="";
        public String JobTypeCodeName="";
        public String MyName="";
        public String PartnersID="";
        public String ProvinceName="";
        public String PositionLevel="";
        public String CompanyName="";
        public String CreatedTime="";

    }
}

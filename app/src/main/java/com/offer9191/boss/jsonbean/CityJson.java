package com.offer9191.boss.jsonbean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class CityJson {
    public int code;
    public String msg="";
    public DataCity data;
    public static class DataCity{
        public List<ProvinceOne> list;
    }
    public static class ProvinceOne implements Serializable {
        public String Code="";
        public String CodeCategory="";
        public String CodeID="";
        public String CodeSeq="";
        public String CodeValue="";
        public List<CityOne> Citys;
    }
    public static class CityOne implements Serializable {
        public CityOne(String codeID, String codeValue) {
            CodeID = codeID;
            CodeValue = codeValue;
        }

        public String Code="";
        public String CodeCategory="";
        public String CodeID="";
        public String CodeSeq="";
        public String CodeValue="";
        public List<DistrictsOne> Districts;
    }
    public static class DistrictsOne implements Serializable {
        public DistrictsOne(String codeID, String codeValue) {
            CodeID = codeID;
            CodeValue = codeValue;
        }

        public String Code="";
        public String CodeCategory="";
        public String CodeID="";
        public String CodeSeq="";
        public String CodeValue="";
    }
}

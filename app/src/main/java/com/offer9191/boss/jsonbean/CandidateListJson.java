package com.offer9191.boss.jsonbean;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class CandidateListJson {
    public int code;
    public String msg="";
    public DataCandidateList data;
    public static class DataCandidateList{
        public List<CandidateOne> candidateList;
        public int pages;
        public int total;
    }
    public static class CandidateOne{
        public String CandidateGender="";
        public String CandidateID="";
        public String CandidateMobile="";
        public String CandidateName="";
        public String CandidateStatus="";
        public String CreatedTime="";
        public String JobTypeCodeNames="";
        public String JobTypeCodes="";
        public String VocationCodes="";
        public String VocationNames="";

    }
}

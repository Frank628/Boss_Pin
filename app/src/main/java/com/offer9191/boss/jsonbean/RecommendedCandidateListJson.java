package com.offer9191.boss.jsonbean;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class RecommendedCandidateListJson {
    public int code;
    public String msg="";
    public DataCandidateList data;
    public static class DataCandidateList{
        public List<RecommendedCandidateOne> candidateRecommendList;
        public int pages;
        public int total;
    }
//    "CandidateGender": "男",
//            "CandidateMobile": "13818984085",
//            "CandidateName": "杨峥峻  ",
//            "CandidatePosition": "电商经理",
//            "CandidateStatus": "1604111003082134296413e504208",
//            "VocationNames":
    public static class RecommendedCandidateOne{
        public String CandidateGender="";
        public String CandidateMobile="";
        public String CandidateName="";
        public String JobCandidateStatus="";
        public String VocationNames="";
        public String CandidatePosition="";

    }
}

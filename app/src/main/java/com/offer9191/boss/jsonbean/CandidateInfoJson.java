package com.offer9191.boss.jsonbean;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/18.
 */
public class CandidateInfoJson {
    public int code;
    public String msg="";
    public CandidateInfo data;
    public static class CandidateInfo{
        public String AddressDetails="";
        public String CandidateAge="";
        public String CandidateDepartment="";
        public String CandidateEmail="";
        public String CandidateGender="";
        public String CandidateID="";
        public String CandidateMobile="";
        public String CandidateName="";
        public String CandidatePosition="";
        public String CandidateStatus="";
        public String CreatedTime="";
        public String CurrentCompany="";

        public String ProvinceName="";
        public String ProvinceCode="";
        public String CityName="";
        public String CityCode="";
        public String DistrictName="";
        public String DistrictCode="";
        public String JobTypeCodeNames="";
        public String JobTypeCodes="";
        public String Notes="";
        public String VocationCodes="";
        public String VocationNames="";
    }

}

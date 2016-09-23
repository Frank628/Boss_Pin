package com.offer9191.boss.utils;

import com.offer9191.boss.jsonbean.CityJson;

import java.util.List;

/**
 * Created by OfferJiShu01 on 2016/9/20.
 */
public class CityJsonUtils {
    public static String listGetCITYCode(List<CityJson.CityOne> list) {
        if (list==null) {
            return "";
        }
        if (list.size() == 0) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                CityJson.CityOne c = list.get(i);
                str.append(c.CodeID);
                if (i != list.size() - 1) {
                    str.append(",");
                }
            }
            return str.toString();
        }
    }

    public static String listGetDISCode(List<CityJson.DistrictsOne> list) {
        if (list==null) {
            return "";
        }
        if (list.size() == 0) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                CityJson.DistrictsOne c = list.get(i);
                str.append(c.CodeID);
                if (i != list.size() - 1) {
                    str.append(",");
                }
            }
            return str.toString();
        }
    }
    public static boolean containsObj(List<CityJson.CityOne> list, String str) {
        for (CityJson.CityOne c : list) {
            if (str.equals(c.CodeID)) {
                return true;
            }
        }
        return false;
    }
    public static void removeFromList(List<CityJson.CityOne> list, String str) {
        for (int i = 0; i < list.size(); i++) {
            CityJson.CityOne c = list.get(i);
            if (str.equals(c.CodeID)) {
                list.remove(i);
                break;
            }
        }
    }
    public static boolean containsDISObj(List<CityJson.DistrictsOne> list, String str) {
        for (CityJson.DistrictsOne c : list) {
            if (str.equals(c.CodeID)) {
                return true;
            }
        }
        return false;
    }
    public static void removeDISFromList(List<CityJson.DistrictsOne> list, String str) {
        for (int i = 0; i < list.size(); i++) {
            CityJson.DistrictsOne c = list.get(i);
            if (str.equals(c.CodeID)) {
                list.remove(i);
                break;
            }
        }
    }
    public static String listGetValue(List<CityJson.CityOne> list) {
        if (list==null) {
            return "";
        }
        if (list.size() == 0) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                CityJson.CityOne c = list.get(i);
                str.append(c.CodeValue);
                if (i != list.size() - 1) {
                    str.append(",");
                }
            }
            return str.toString();
        }
    }
    public static String listGetZhiValue(List<CityJson.DistrictsOne> list) {
        if (list==null) {
            return "";
        }
        if (list.size() == 0) {
            return "";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                CityJson.DistrictsOne c = list.get(i);
                str.append(c.CodeValue);
                if (i != list.size() - 1) {
                    str.append(",");
                }
            }
            return str.toString();
        }
    }

}

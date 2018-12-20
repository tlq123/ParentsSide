package com.cszj.ps.activity.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {

    private final static String SHARE_NAME = "CHUANGSHIZHIJIA";

    /*
    用户名
     */
    public static void saveUserId(String params, Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId" , params);
        editor.commit();
    }

    public static String getUserId(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        String userId = sharedPreferences.getString("userId","");
        return userId;
    }

    /*
    wifi 名
     */
    public static void saveWifiName(String params, Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wifiName" , params);
        editor.commit();
    }

    public static String getWifiName(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        String wifiName = sharedPreferences.getString("wifiName","");
        return wifiName;
    }

    /*
    wifi 密码
     */
    public static void saveWifiPwd(String params, Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wifiPwd" , params);
        editor.commit();
    }

    public static String getWifiPwd(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        String wifiPwd = sharedPreferences.getString("wifiPwd","");
        return wifiPwd;
    }
}










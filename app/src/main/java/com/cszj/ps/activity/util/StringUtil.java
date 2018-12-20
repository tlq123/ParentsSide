
package com.cszj.ps.activity.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理的辅助方法
 *
 */
public class StringUtil {

    /**是否为空*/
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**是否为空*/
    public static boolean isEmptyCount(String str) {
        return str == null || "".equals(str.trim()) || ".".equalsIgnoreCase(str.trim());
    }
    /**是否为空*/
    public static boolean isEmptyString(String str){
        return str == null || "".equals(str) || "--".equalsIgnoreCase(str) || "暂无".equalsIgnoreCase(str);
    }

    /**字符串是否为空，为空返回“暂无”*/
    public static String isEmptyString(boolean defaultStr, String title, String str) {
        if(isEmpty(str)){
            return defaultStr ? "暂无" : "";
        }

        if("--".equalsIgnoreCase(str)){
            return defaultStr ? "暂无" : "";
        }

        return (title == null ? "" : title) + (str == null ? "" : str);
    }
    /**是否为空*/
    public static String isEmpty(String defaultStr, String str){
        return isEmpty(str) ? defaultStr : str;
    }

    // 比较是否相等
    public static boolean isEuqals(String str1, String str2) {
        if (str1 == null){
            return str2 == null;
        }else{
            return str1.equals(str2);
        }
    }

    // 安全地截断空白符号
    public static String safeTrim(String src) {
        if (src == null)
            return src;
        return src.trim();

    }

    // 如果字符串为null的话则转化为空
    public static String emptyIfNull(String src) {
        return src == null ? "" : src;
    }

    // 当字符串为Null时，设定默认值
    public static String setValueIfNull(String src, String def) {
        return src == null ? def : ("null".equals(src) ? null : src);
    }

    // 判断字符串是否为空
    public static boolean isBlank(StringBuffer value) {

        return isBlank(value == null ? null : value.toString());

    }

    // 判断字符串是否为空
    public static boolean isBlank(String value) {
        if (value == null || value.trim().length() == 0) {
            return true;
        }
        return false;
    }

    // 查找某个字符串在字符串数组中的位置
    public static int findIndex(String[] items, String item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean isStickBlank(String value) {
        if (isBlank(value) || "null".equals(value)) {
            return true;
        }

        return false;

    }

    public static String emptyIfStrickBlank(String value) {
        if (isStickBlank(value)) {
            return "";
        }

        return value;
    }

    public static String emptyIfBlank(String value) {
        if (isBlank(value))
            return "";

        return value;

    }

    // 是否标准的邮箱格式
    public static boolean isEmail(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.find();
    }

    // 是否标准的邮箱格式
    public static boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email))
            return false;
        return Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*").matcher(email).matches();
    }


}

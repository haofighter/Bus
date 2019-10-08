package com.szxb.buspay.db.sp;


import com.szxb.buspay.BuildConfig;

/**
 * 作者: Tangren on 2017/7/12
 * 包名：com.szxb.onlinbus.util
 * 邮箱：996489865@qq.com
 * TODO:获取全局的SP数据
 */

public class FetchAppConfig {

    //获取appId
    public static String getAppId() {
        return (String) CommonSharedPreferences.get("app_id", "0000000");
    }

    //车牌号
    public static String getBusNo() {
        return (String) CommonSharedPreferences.get("bus_no", "000000");
    }


    //获取票价
    public static int getBasePrice() {
        return (Integer) CommonSharedPreferences.get("base_price", 0);
    }

    //司机tac
    public static String getDriverNo() {
        String driverNo = "";
        if (BuildConfig.CITY == 1 || BuildConfig.CITY == 8) {
            driverNo = "00112234";
        } else {
            driverNo = "00000000";
        }
        return (String) CommonSharedPreferences.get("driver_no", driverNo);
    }

    //司机卡号
    public static String getEmpNo() {
        return (String) CommonSharedPreferences.get("emp_no", "00000000");
    }

    //线路名
    public static String getLineName() {
        return (String) CommonSharedPreferences.get("line_name", "0000");
    }


    public static String getLineNo() {
        return (String) CommonSharedPreferences.get("line_no", "0000");
    }

    //线路名称说明
    public static String chinese_name() {
        return (String) CommonSharedPreferences.get("chinese_name", "请先设置线路信息");
    }

    //折扣
    public static String coefficient() {
        return (String) CommonSharedPreferences.get("coefficient", "000000000000000000000000000000000000000000000000000000000000000000000000");
    }

    //公司号
    public static String unitno() {
        return (String) CommonSharedPreferences.get("unitno", "0");
    }


    public static String getBinVersion() {
        return (String) CommonSharedPreferences.get("bin", "taian.bin");
    }

    public static String getLastVersion() {
        return (String) CommonSharedPreferences.get("last_bin", "000000");
    }


    //数字流水00000000-99999999
    public static String getNumSeq() {
        return (String) CommonSharedPreferences.get("num_seq", "00000000");
    }

    //上一个黑名单版本
    public static String getLastBlackVersion() {
        return (String) CommonSharedPreferences.get("last_black_version", "0");
    }

    public static String getLastParamsFileName() {
        return (String) CommonSharedPreferences.get("last_params_file_name", "0");
    }

    public static String getZYmonthEnableTime() {
        return (String) CommonSharedPreferences.get("zy_month_enable_time", "0");
    }
}
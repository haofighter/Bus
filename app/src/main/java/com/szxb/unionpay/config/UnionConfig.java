package com.szxb.unionpay.config;

import com.szxb.buspay.db.sp.CommonSharedPreferences;

/**
 * 作者：Tangren on 2018-07-06
 * 包名：com.szxb.unionpay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class UnionConfig {

    /**
     * @return TPDU
     */
    public static String getTpdu() {
        return (String) CommonSharedPreferences.get("tp_du", "0000000000");
    }

    /**
     * @return 操作员编号
     */
    public static String operatorNumber() {
        return (String) CommonSharedPreferences.get("operator_number", "099");
    }

    public static String macKey() {
        return (String) CommonSharedPreferences.get("mac_key", String.format("%016X", 0));
    }

    /**
     * @return 批次号
     */
    public static String batchNum() {
        return (String) CommonSharedPreferences.get("batch_num", "005001");
    }

    public static String getUnionPayUrl() {
        return (String) CommonSharedPreferences.get("union_pay_url", "https://120.204.69.139:30000/mjc/webtrans/VPB_lb");
    }

    public static String getUnionPonSn() {
        return (String) CommonSharedPreferences.get("union_pos_sn", "00000000");
    }

    public static String getMch() {
        return (String) CommonSharedPreferences.get("mch", "000000000000000");
    }

    public static String key() {
        return (String) CommonSharedPreferences.get("key", "00000000000000000000000000000000");
    }


    public static int tradeSeq() {
        return (Integer) CommonSharedPreferences.get("trade_seq", 1);
    }

    public static String getAidIndexList() {
        return (String) CommonSharedPreferences.get("aid_index_list", String.format("%016X", 0));
    }


    public static long getLastUpdateTime() {
        return (Long) CommonSharedPreferences.get("last_update_time", 0L);
    }

    public static final int SIGN = 100000;
    public static final int PAY = 200000;

    //null
    public static final int NULL = -1;
    public static final int NULL2 = -2;
    public static final int NULL3 = -3;
    public static final int NULL4 = -4;
    public static final int NULL5 = -5;
    public static final int NULL6 = -6;
    public static final int NULL7 = -7;

    //invalid
    public static final int INVALID = -8;

    public static final int INVALID2 = -11;

    //exception
    public static final int EXCEPTION = -9;

    //repeat
    public static final int REPEAT = -10;


}

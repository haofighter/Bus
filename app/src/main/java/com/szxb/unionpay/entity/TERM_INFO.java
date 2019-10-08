package com.szxb.unionpay.entity;

import com.szxb.unionpay.unionutil.HexUtil;

/**
 * 作者：Tangren on 2018-07-07
 * 包名：com.szxb.unionpay.entity
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class TERM_INFO {
    public String terminal_country_code = "0156";
    public String TID = HexUtil.bytesToHexString("12345678".getBytes());
    public String IFD =HexUtil.bytesToHexString("00000000".getBytes());
    public String transaction_currency_code = "0156";
    public String terminal_capabilities = "E0E1C8";
    public String terminal_type = "22";
    public String transaction_currency_exponent = "02";
    public String additional_terminal_capabilities = "ff00103001";
    public String merchantName = HexUtil.bytesToHexString("xiaobing".getBytes());
    public String ttq = "a0800080";
    public String statusCheckSupport = "40";
}

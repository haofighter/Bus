package com.szxb.buspay.manager;

/**
 * 作者: TangRen on 2018/7/29
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:功能开关
 */

public interface ISwitch {

    boolean isSuppScanPay();

    boolean isSuppIcPay();

    boolean isSuppUnionPay();

    boolean isHalfPrices();

    void setHalfPrices(boolean half);

    boolean isSuppKeyBoard();
}

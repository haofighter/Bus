package com.szxb.buspay.manager;

import com.szxb.buspay.db.entity.card.LineInfoEntity;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public interface IPosManager {
    void loadFromPrefs(int city, String binName);

    String getLineName();

    void setLineName(String var1);

    String getLineNo();

    void setLineNo(String var1);

    int getBasePrice();

    void setBasePrice(int var1);

    int getWcPayPrice();

    void setWcPrice(int var1);

    void setUnionPayPrice(int price);

    int getUnionPayPrice();

    void setUnionScanPrice(int price);

    int getUnionScanPrice();

    String getMac(String keyId);

    String getPublicKey(String keyId);

    long getOrderTime();

    String getmchTrxId();

    String geCityCode();

    int getInStationId();

    String getInStationName();

    String getChinese_name();

    void setChinese_name(String name);

    byte[] getKey();

    void setKey(String privateKey);

    String getBusNo();

    void setBusNo(String bus_no);

    String getAppId();

    void setAppId(String app_id);

    String getCityCode();

    void setCityCode();

    void setUnitno(String unitno);

    String getUnitno();

    void setDriverNo(String no, String empCardNo);

    String getDriverNo();

    String getEmpNo();

    String getPosSN();

    LineInfoEntity getLineInfoEntity();

    void setLineInfoEntity();

    String getLastVersion();

    void setLastVersion(String version);

    String getBinVersion();

    String[] getCoefficent();

    void setCoefficent(String coefficent);

    void setBlackVersion(String version);

    String getBlackVersion();

    void setLastParamsFileName(String var);

    String getLastParamsFileName();

    //补采的flag
    void setFlag(String flag);

    String getFlag();

    void setSupMinTims(String[] times);

    String[] getTimes();

    void setSupplementaryMiningCnt(long cnt);

    long getSupplementaryMiningCnt();

    /**
     * @param str 招远月票卡允许使用的时间段
     */
    void setZYMonthEnableTime(String str);

    String getZYMonthEnableTime();

    void setZYMouthCardAllowUse(String rmk1);
}

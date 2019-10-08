package com.szxb.buspay.db.entity.scan.param;

/**
 * 作者：Tangren on 2018-07-21
 * 包名：com.szxb.buspay.db.entity.scan.param
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class UnionPayParam {

    /**
     * mch : 438153341310001
     * sn : 12345678
     * key : 11111111111111111111111111111111
     */

    private String mch;
    private String sn;
    private String key;

    public String getMch() {
        return mch;
    }

    public void setMch(String mch) {
        this.mch = mch;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "UnionPayParam{" +
                "mch='" + mch + '\'' +
                ", sn='" + sn + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

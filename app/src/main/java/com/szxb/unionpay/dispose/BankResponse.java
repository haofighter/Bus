package com.szxb.unionpay.dispose;

/**
 * 作者：Tangren on 2018-09-08
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class BankResponse {
    private int resCode = -999;
    private String mainCardNo = "0";
    private long lastTime = 0;
    private int type;
    private String msg = "未知错误[" + resCode + "]";

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMainCardNo() {
        return mainCardNo;
    }

    public void setMainCardNo(String mainCardNo) {
        this.mainCardNo = mainCardNo;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "BankICResponse{" +
                "resCode=" + resCode +
                ", mainCardNo='" + mainCardNo + '\'' +
                ", lastTime=" + lastTime +
                ", msg='" + msg + '\'' +
                '}';
    }
}

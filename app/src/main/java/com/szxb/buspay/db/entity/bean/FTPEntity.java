package com.szxb.buspay.db.entity.bean;

/**
 * 作者: TangRen on 2017/12/17
 * 包名：com.czgj.entity.scan
 * 邮箱：996489865@qq.com
 * TODO:FTP 信息
 */

public class FTPEntity {

    /**
     * i : ip
     * p : 端口
     * u : 账号
     * psw : 密码
     */

    private String i;
    private int p;
    private String u;
    private String psw;

    public FTPEntity(String i, int p, String u, String psw) {
        this.i = i;
        this.p = p;
        this.u = u;
        this.psw = psw;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    @Override
    public String toString() {
        return "FTPEntity{" +
                "i='" + i + '\'' +
                ", p=" + p +
                ", u='" + u + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }
}

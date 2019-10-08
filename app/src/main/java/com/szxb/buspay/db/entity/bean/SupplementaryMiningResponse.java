package com.szxb.buspay.db.entity.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * 作者：Tangren on 2018-09-05
 * 包名：com.szxb.buspay.db.entity.bean
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class SupplementaryMiningResponse {

    /**
     * result : success
     * datalist : [{"endtime":"2018-06-13 23:59:59","startime":"2018-06-10 00:00:00","upstatus":0,"upway":0}]
     * rescode : 00
     */

    private String result="UNK";
    private String rescode="99";
    private List<DatalistBean> datalist;
    private boolean isOk;

    public boolean isOk() {
        return TextUtils.equals(rescode, "00");
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRescode() {
        return rescode;
    }

    public void setRescode(String rescode) {
        this.rescode = rescode;
    }

    public List<DatalistBean> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<DatalistBean> datalist) {
        this.datalist = datalist;
    }

    public static class DatalistBean {
        /**
         * endtime : 2018-06-13 23:59:59
         * startime : 2018-06-10 00:00:00
         * upstatus : 0
         * upway : 0
         */

        private String endtime;
        private String startime;
        private String flag;
        private int upstatus;
        private int upway;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getStartime() {
            return startime;
        }

        public void setStartime(String startime) {
            this.startime = startime;
        }

        public int getUpstatus() {
            return upstatus;
        }

        public void setUpstatus(int upstatus) {
            this.upstatus = upstatus;
        }

        public int getUpway() {
            return upway;
        }

        public void setUpway(int upway) {
            this.upway = upway;
        }
    }
}

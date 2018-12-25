package com.szxb.buspay.db.entity.bean;

import java.util.List;

/**
 * 作者：Tangren on 2018-07-24
 * 包名：com.szxb.buspay.db.entity.bean
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
public class ConfigParam {


    private List<ConfigBean> config;

    public List<ConfigBean> getConfig() {
        return config;
    }

    public void setConfig(List<ConfigBean> config) {
        this.config = config;
    }

    public static class ConfigBean {
        /**
         * ip : ftp地址
         * port : ftp 端口号
         * user : ftp用户名
         * psw : ftp密码
         * mch_id : 微信商户号
         * city_code : 城市代码
         * url_ip : http ip
         * is_supp_scan_pay : true    是否支持扫码支付
         * is_supp_ic_pay : true      是否支持ic卡支付
         * is_supp_union_pay : false  是否支持银联卡支付
         * is_supp_key_board : false  是否票价键盘
         * city : 淄博[0]
         */

        private String ip;
        private int port;
        private String user;
        private String psw;
        private String mch_id;
        private String city_code;
        private String url_ip;
        private boolean is_supp_scan_pay;
        private boolean is_supp_ic_pay;
        private boolean is_supp_union_pay;

        public String getOrganization() {
            return organization;
        }

        public void setOrganization(String organization) {
            this.organization = organization;
        }

        private boolean is_supp_key_board;
        private String city;
        private String organization;//机构代码 用于互联互通

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPsw() {
            return psw;
        }

        public void setPsw(String psw) {
            this.psw = psw;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getCity_code() {
            return city_code;
        }

        public void setCity_code(String city_code) {
            this.city_code = city_code;
        }

        public String getUrl_ip() {
            return url_ip;
        }

        public void setUrl_ip(String url_ip) {
            this.url_ip = url_ip;
        }

        public boolean isIs_supp_scan_pay() {
            return is_supp_scan_pay;
        }

        public void setIs_supp_scan_pay(boolean is_supp_scan_pay) {
            this.is_supp_scan_pay = is_supp_scan_pay;
        }

        public boolean isIs_supp_ic_pay() {
            return is_supp_ic_pay;
        }

        public void setIs_supp_ic_pay(boolean is_supp_ic_pay) {
            this.is_supp_ic_pay = is_supp_ic_pay;
        }

        public boolean isIs_supp_union_pay() {
            return is_supp_union_pay;
        }

        public void setIs_supp_union_pay(boolean is_supp_union_pay) {
            this.is_supp_union_pay = is_supp_union_pay;
        }

        public boolean isIs_supp_key_board() {
            return is_supp_key_board;
        }

        public void setIs_supp_key_board(boolean is_supp_key_board) {
            this.is_supp_key_board = is_supp_key_board;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }
}

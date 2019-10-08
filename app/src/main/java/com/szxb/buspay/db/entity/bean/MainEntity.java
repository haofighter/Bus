package com.szxb.buspay.db.entity.bean;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.db.entity
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class MainEntity {
    private String card_id;
    private String card_money;
    private String pay_money;
    private String time;
    private String cardType;
    private int status;
    private int type;//0:刷卡，1：扫码，2：银联卡，3：银联二维码

    public MainEntity(String card_id) {
        this.card_id = card_id;
    }

    public MainEntity() {
    }


    public MainEntity(String card_id, String card_money,
                      String pay_money, String time) {
        this.card_id = card_id;
        this.card_money = card_money;
        this.pay_money = pay_money;
        this.time = time;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_money() {
        return card_money;
    }

    public void setCard_money(String card_money) {
        this.card_money = card_money;
    }

    public String getPay_money() {
        return pay_money;
    }

    public void setPay_money(String pay_money) {
        this.pay_money = pay_money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

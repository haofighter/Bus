package com.szxb.buspay.db.entity.scan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者: Tangren on 2017/7/19
 * 包名：com.szxb.buspay.entity
 * 邮箱：996489865@qq.com
 * TODO:扫码记录表
 * status:支付状态，默认false
 * biz_data_single:单个扫码记录的jsonObject
 */
@Entity
public class ScanInfoEntity {
    @Id(autoincrement = true)
    private Long id;
    //支付状态1:新订单，未支付，0:准实时扣款，2：批处理扣款成功 3：扣款失败后台处理
    private Integer status = 1;
    //一笔订单
    private String biz_data_single;
    //订单号
    private String mch_trx_id;
    //返回码
    private String result;
    //交易状态码
    private String tr_status;
    //保存的时间yyyy-MM-dd HH:mm:ss
    private String time;
    private String qrcode;
    private String openid;
    private Integer pay_fee;
    private String remark_1;
    private String remark_2;
    private String remark_3;
    private String remark_4;


    @Generated(hash = 883535376)
    public ScanInfoEntity(Long id, Integer status, String biz_data_single,
            String mch_trx_id, String result, String tr_status, String time,
            String qrcode, String openid, Integer pay_fee, String remark_1,
            String remark_2, String remark_3, String remark_4) {
        this.id = id;
        this.status = status;
        this.biz_data_single = biz_data_single;
        this.mch_trx_id = mch_trx_id;
        this.result = result;
        this.tr_status = tr_status;
        this.time = time;
        this.qrcode = qrcode;
        this.openid = openid;
        this.pay_fee = pay_fee;
        this.remark_1 = remark_1;
        this.remark_2 = remark_2;
        this.remark_3 = remark_3;
        this.remark_4 = remark_4;
    }

    @Generated(hash = 1829284389)
    public ScanInfoEntity() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBiz_data_single() {
        return this.biz_data_single;
    }

    public void setBiz_data_single(String biz_data_single) {
        this.biz_data_single = biz_data_single;
    }

    public String getMch_trx_id() {
        return this.mch_trx_id;
    }

    public void setMch_trx_id(String mch_trx_id) {
        this.mch_trx_id = mch_trx_id;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTr_status() {
        return this.tr_status;
    }

    public void setTr_status(String tr_status) {
        this.tr_status = tr_status;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark_1() {
        return this.remark_1;
    }

    public void setRemark_1(String remark_1) {
        this.remark_1 = remark_1;
    }

    public String getRemark_2() {
        return this.remark_2;
    }

    public void setRemark_2(String remark_2) {
        this.remark_2 = remark_2;
    }

    public String getRemark_3() {
        return this.remark_3;
    }

    public void setRemark_3(String remark_3) {
        this.remark_3 = remark_3;
    }

    public String getRemark_4() {
        return this.remark_4;
    }

    public void setRemark_4(String remark_4) {
        this.remark_4 = remark_4;
    }

    public String getQrcode() {
        return this.qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPay_fee() {
        return this.pay_fee;
    }

    public void setPay_fee(Integer pay_fee) {
        this.pay_fee = pay_fee;
    }

}

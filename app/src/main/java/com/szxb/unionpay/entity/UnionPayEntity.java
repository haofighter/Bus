package com.szxb.unionpay.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Tangren on 2018-07-06
 * 包名：com.szxb.unionpay.entity
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class UnionPayEntity {

    @Id(autoincrement = true)
    private Long id;

    //商户号
    private String mchId;

    //银联设备号
    private String unionPosSn;

    //设备号
    private String posSn;

    //车辆号
    private String busNo;

    //应付金额
    private String totalFee;

    //实际扣款金额
    private String payFee;

    //扣款返回码
    private String resCode = "408";

    //交易时间
    private String time;

    //交易流水
    private String tradeSeq;

    //主账号
    private String mainCardNo;

    //批次号
    private String batchNum;

    //线路名称
    private String bus_line_name;

    //线路号
    private String bus_line_no;

    //司机编号
    private String driverNum;

    //分公司号
    private String unitno;

    //上传状态
    private Integer upStatus;

    //唯一标示：批次号+流水号
    @Unique
    private String uniqueFlag;

    //55域数据
    private String tlv55;

    //单条支付数据
    private String singleData;

    //预留1(目前作为卡片序号cardNum)
    private String reserve_1;

    //预留2(目前作为银联卡与银联二维码的标志,null 表示银联卡)
    private String reserve_2;
    private String reserve_3;
    private String reserve_4;

    @Generated(hash = 1823348864)
    public UnionPayEntity(Long id, String mchId, String unionPosSn, String posSn,
                          String busNo, String totalFee, String payFee, String resCode,
                          String time, String tradeSeq, String mainCardNo, String batchNum,
                          String bus_line_name, String bus_line_no, String driverNum,
                          String unitno, Integer upStatus, String uniqueFlag, String tlv55,
                          String singleData, String reserve_1, String reserve_2, String reserve_3,
                          String reserve_4) {
        this.id = id;
        this.mchId = mchId;
        this.unionPosSn = unionPosSn;
        this.posSn = posSn;
        this.busNo = busNo;
        this.totalFee = totalFee;
        this.payFee = payFee;
        this.resCode = resCode;
        this.time = time;
        this.tradeSeq = tradeSeq;
        this.mainCardNo = mainCardNo;
        this.batchNum = batchNum;
        this.bus_line_name = bus_line_name;
        this.bus_line_no = bus_line_no;
        this.driverNum = driverNum;
        this.unitno = unitno;
        this.upStatus = upStatus;
        this.uniqueFlag = uniqueFlag;
        this.tlv55 = tlv55;
        this.singleData = singleData;
        this.reserve_1 = reserve_1;
        this.reserve_2 = reserve_2;
        this.reserve_3 = reserve_3;
        this.reserve_4 = reserve_4;
    }

    @Generated(hash = 14483869)
    public UnionPayEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMchId() {
        return this.mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getUnionPosSn() {
        return this.unionPosSn;
    }

    public void setUnionPosSn(String unionPosSn) {
        this.unionPosSn = unionPosSn;
    }

    public String getPosSn() {
        return this.posSn;
    }

    public void setPosSn(String posSn) {
        this.posSn = posSn;
    }

    public String getBusNo() {
        return this.busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getTotalFee() {
        return this.totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getPayFee() {
        return this.payFee;
    }

    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }

    public String getResCode() {
        return this.resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTradeSeq() {
        return this.tradeSeq;
    }

    public void setTradeSeq(String tradeSeq) {
        this.tradeSeq = tradeSeq;
    }

    public String getMainCardNo() {
        return this.mainCardNo;
    }

    public void setMainCardNo(String mainCardNo) {
        this.mainCardNo = mainCardNo;
    }

    public String getBatchNum() {
        return this.batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getBus_line_name() {
        return this.bus_line_name;
    }

    public void setBus_line_name(String bus_line_name) {
        this.bus_line_name = bus_line_name;
    }

    public String getBus_line_no() {
        return this.bus_line_no;
    }

    public void setBus_line_no(String bus_line_no) {
        this.bus_line_no = bus_line_no;
    }

    public String getDriverNum() {
        return this.driverNum;
    }

    public void setDriverNum(String driverNum) {
        this.driverNum = driverNum;
    }

    public String getUnitno() {
        return this.unitno;
    }

    public void setUnitno(String unitno) {
        this.unitno = unitno;
    }

    public Integer getUpStatus() {
        return this.upStatus;
    }

    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }

    public String getUniqueFlag() {
        return this.uniqueFlag;
    }

    public void setUniqueFlag(String uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    public String getTlv55() {
        return this.tlv55;
    }

    public void setTlv55(String tlv55) {
        this.tlv55 = tlv55;
    }

    public String getSingleData() {
        return this.singleData;
    }

    public void setSingleData(String singleData) {
        this.singleData = singleData;
    }

    public String getReserve_1() {
        return this.reserve_1;
    }

    public void setReserve_1(String reserve_1) {
        this.reserve_1 = reserve_1;
    }

    public String getReserve_2() {
        return this.reserve_2;
    }

    public void setReserve_2(String reserve_2) {
        this.reserve_2 = reserve_2;
    }

    public String getReserve_3() {
        return this.reserve_3;
    }

    public void setReserve_3(String reserve_3) {
        this.reserve_3 = reserve_3;
    }

    public String getReserve_4() {
        return this.reserve_4;
    }

    public void setReserve_4(String reserve_4) {
        this.reserve_4 = reserve_4;
    }

    @Override
    public String toString() {
        return "UnionPayEntity{" +
                "id=" + id +
                ", mchId='" + mchId + '\'' +
                ", unionPosSn='" + unionPosSn + '\'' +
                ", posSn='" + posSn + '\'' +
                ", busNo='" + busNo + '\'' +
                ", totalFee='" + totalFee + '\'' +
                ", payFee='" + payFee + '\'' +
                ", resCode='" + resCode + '\'' +
                ", time='" + time + '\'' +
                ", tradeSeq='" + tradeSeq + '\'' +
                ", mainCardNo='" + mainCardNo + '\'' +
                ", batchNum='" + batchNum + '\'' +
                ", bus_line_name='" + bus_line_name + '\'' +
                ", bus_line_no='" + bus_line_no + '\'' +
                ", driverNum='" + driverNum + '\'' +
                ", unitno='" + unitno + '\'' +
                ", upStatus=" + upStatus +
                ", uniqueFlag='" + uniqueFlag + '\'' +
                ", tlv55='" + tlv55 + '\'' +
                ", singleData='" + singleData + '\'' +
                ", reserve_1='" + reserve_1 + '\'' +
                ", reserve_2='" + reserve_2 + '\'' +
                ", reserve_3='" + reserve_3 + '\'' +
                ", reserve_4='" + reserve_4 + '\'' +
                '}';
    }
}

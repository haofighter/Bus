package com.szxb.buspay.db.entity.bean.card;

import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

import static java.lang.System.arraycopy;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.db.entity.bean.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class ConsumeCard {
    @Id(autoincrement = true)
    private Long id;
    //状态
    private String status;
    //卡类型
    private String cardType;
    //卡模块类型CPU/M1
    private String cardModuleType;
    //交易类型
    private String transType;
    //设备交易序号
    private String transNo;
    //卡号
    @Index
    private String cardNo;
    //卡余额
    private String cardBalance;
    //扣款金额
    private String payFee;
    //交易时间
    private String transTime;
    //用户卡脱机交易序号
    private String transNo2;
    //AC码
    private String tac;
    //线路号
    private String lineNo;
    //车号
    private String busNo;
    //当班司机号
    private String driverNo;
    //PSAM卡号
    private String pasmNo;
    //*****************淄博开始***********************/
    //行驶方向
    private String direction = "00";
    //站点号
    private String stationId = "00";
    //补票标志
    private String fareFlag = "00";

    //*****************淄博结束***********************/

    //*****************泰安开始***********************/
    //本机线路号
    private String localLineNo;
    //本机车号
    private String localBusNo;
    //本机司机号
    private String localDriverNo;
    //*****************泰安结束***********************/


    //*****************莱芜***********************/
    //算法表示
    private String algFlag = "00";
    //发卡机构标示
    private String issuerFlag = "00";
    //卡子类型
    private String cardChildType = "00";
    //CPU卡应用版本
    private String cpuVersion = "00";
    //*****************莱芜***********************/


    //唯一标示
    @Unique
    private String uniqueFlag;
    //上传状态
    private Integer upStatus;
    //单条记录
    private String singleRecord;

    //是否半价(招远使用)
    private String isHalfPrices = "0";

    //商户号
    private String mchId = "00000000";

    //公司号
    private String companyNo = "000000";

    private String reserve_1;
    private String reserve_2;
    private String reserve_3;
    private String reserve_4;
    private String reserve_5;

    private String cardRecordType;//交通部卡的记录类型为0xA0。莱芜本地卡和济南卡默认是0。
    private String cardMacVersion;// 密钥版本号
    private String cardMacIndex;//密钥索引

    public String getCardRecordType() {
        return cardRecordType;
    }

    public void setCardRecordType(String cardRecordType) {
        this.cardRecordType = cardRecordType;
    }

    public String getCardMacVersion() {
        return cardMacVersion;
    }

    public void setCardMacVersion(String cardMacVersion) {
        this.cardMacVersion = cardMacVersion;
    }

    public String getCardMacIndex() {
        return cardMacIndex;
    }

    public void setCardMacIndex(String cardMacIndex) {
        this.cardMacIndex = cardMacIndex;
    }

    public ConsumeCard(byte[] receDatas, boolean isSignOrBack, String city, String cardModuleType) {
        int index = 0;
        byte[] status_byte = new byte[0];
        status_byte = new byte[1];
        arraycopy(receDatas, index, status_byte, 0, status_byte.length);
        status = HexUtil.printHexBinary(status_byte);

        byte[] cardType_byte = new byte[1];
        arraycopy(receDatas, index += status_byte.length, cardType_byte, 0, cardType_byte.length);
        cardType = HexUtil.printHexBinary(cardType_byte);

        byte[] transType_byte = new byte[1];
        arraycopy(receDatas, index += cardType_byte.length, transType_byte, 0, transType_byte.length);
        transType = HexUtil.printHexBinary(transType_byte);

        byte[] transNo_byte = new byte[3];
        arraycopy(receDatas, index += transType_byte.length, transNo_byte, 0, transNo_byte.length);
        transNo = HexUtil.printHexBinary(transNo_byte);

        byte[] cardNo_byte = new byte[8];
        arraycopy(receDatas, index += transNo_byte.length, cardNo_byte, 0, cardNo_byte.length);
        cardNo = HexUtil.bcd2Str(cardNo_byte);

        byte[] cardBalance_byte = new byte[3];
        arraycopy(receDatas, index += cardNo_byte.length, cardBalance_byte, 0, cardBalance_byte.length);
        cardBalance = Util.hex2IntStr(HexUtil.printHexBinary(cardBalance_byte));

        byte[] payFee_byte = new byte[3];
        arraycopy(receDatas, index += cardBalance_byte.length, payFee_byte, 0, payFee_byte.length);
        payFee = Util.hex2IntStr(HexUtil.printHexBinary(payFee_byte));

        byte[] transTime_byte = new byte[7];
        arraycopy(receDatas, index += payFee_byte.length, transTime_byte, 0, transTime_byte.length);
        transTime = HexUtil.bcd2Str(transTime_byte);

        byte[] transNo2_byte = new byte[2];
        arraycopy(receDatas, index += transTime_byte.length, transNo2_byte, 0, transNo2_byte.length);
        transNo2 = HexUtil.printHexBinary(transNo2_byte);

        byte[] tac_byte = new byte[4];
        arraycopy(receDatas, index += transNo2_byte.length, tac_byte, 0, tac_byte.length);
        tac = isSignOrBack ? HexUtil.bcd2Str(tac_byte) : HexUtil.printHexBinary(tac_byte);

        byte[] lineNoe_byte = new byte[2];
        arraycopy(receDatas, index += tac_byte.length, lineNoe_byte, 0, lineNoe_byte.length);
        lineNo = HexUtil.printHexBinary(lineNoe_byte);

        byte[] busNo_byte = new byte[3];
        arraycopy(receDatas, index += lineNoe_byte.length, busNo_byte, 0, busNo_byte.length);
        busNo = HexUtil.bcd2Str(busNo_byte);

        byte[] driverNo_byte = new byte[4];
        arraycopy(receDatas, index += busNo_byte.length, driverNo_byte, 0, driverNo_byte.length);
        driverNo = HexUtil.bcd2Str(driverNo_byte);

        byte[] pasmNo_byte = new byte[6];
        arraycopy(receDatas, index += driverNo_byte.length, pasmNo_byte, 0, pasmNo_byte.length);
        pasmNo = HexUtil.bcd2Str(pasmNo_byte);

        this.cardModuleType = cardModuleType;
        if (TextUtils.equals(city, "zibo")) {
            getZibo(receDatas, index + pasmNo_byte.length);
        } else if (TextUtils.equals(city, "laiwu")) {
            getLw(receDatas, index + pasmNo_byte.length);
        }


        isHalfPrices = BusApp.getPosManager().isHalfPrices() ? "1" : "0";
        mchId = BusApp.getPosManager().getAppId();
        companyNo = BusApp.getPosManager().getUnitno();
        upStatus=1;
        uniqueFlag = pasmNo + transNo + transTime;

        reserve_1 = Util.Random(10);
    }

    /**
     * @param ziboDatas 淄博独有的数据
     * @param index     下标
     */
    private void getZibo(byte[] ziboDatas, int index) {
        byte[] direction_byte = new byte[1];
        arraycopy(ziboDatas, index, direction_byte, 0, direction_byte.length);
        direction = HexUtil.printHexBinary(direction_byte);

        byte[] stationId_byte = new byte[1];
        arraycopy(ziboDatas, index += direction_byte.length, stationId_byte, 0, stationId_byte.length);
        stationId = HexUtil.printHexBinary(stationId_byte);

        byte[] fareFlag_byte = new byte[1];
        arraycopy(ziboDatas, index + stationId_byte.length, fareFlag_byte, 0, fareFlag_byte.length);
        fareFlag = HexUtil.printHexBinary(fareFlag_byte);


        byte[] recordData = new byte[50];
        arraycopy(ziboDatas, 0, recordData, 0, recordData.length);
        singleRecord = HexUtil.printHexBinary(recordData);

    }


    private void getLw(byte[] lwDatas, int index) {
        Log.i("数据", HexUtil.bcd2Str(lwDatas));
        byte[] algFlag_bytes = new byte[1];
        arraycopy(lwDatas, index, lwDatas, 0, algFlag_bytes.length);
        algFlag = HexUtil.printHexBinary(algFlag_bytes);

        byte[] issuerFlag_bytes = new byte[8];
        arraycopy(lwDatas, index += algFlag_bytes.length, issuerFlag_bytes, 0, issuerFlag_bytes.length);
        issuerFlag = HexUtil.printHexBinary(issuerFlag_bytes);

        byte[] cardChildType_bytes = new byte[1];
        arraycopy(lwDatas, index += issuerFlag_bytes.length, cardChildType_bytes, 0, cardChildType_bytes.length);
        cardChildType = HexUtil.printHexBinary(cardChildType_bytes);

        byte[] cpuVersion_bytes = new byte[1];
        arraycopy(lwDatas, index + cardChildType_bytes.length, cpuVersion_bytes, 0, cpuVersion_bytes.length);
        cpuVersion = HexUtil.printHexBinary(cpuVersion_bytes);

        byte[] cardNumhead = new byte[2];
        arraycopy(lwDatas, index + cpuVersion_bytes.length, cardNumhead, 0, cardNumhead.length);
        cpuVersion = HexUtil.printHexBinary(cardNumhead);


        byte[] cardMacversion = new byte[1];
        arraycopy(lwDatas, index + cardNumhead.length, cardMacversion, 0, cardMacversion.length);
        cardMacVersion = HexUtil.printHexBinary(cardMacversion);

        byte[] cardMacindex = new byte[1];
        arraycopy(lwDatas, index + cardMacversion.length, cardMacindex, 0, cardMacindex.length);
        cardMacIndex = HexUtil.printHexBinary(cardMacindex);

    }

    @Generated(hash = 1184016689)
    public ConsumeCard(Long id, String status, String cardType, String cardModuleType, String transType, String transNo, String cardNo, String cardBalance, String payFee,
                       String transTime, String transNo2, String tac, String lineNo, String busNo, String driverNo, String pasmNo, String direction, String stationId, String fareFlag,
                       String localLineNo, String localBusNo, String localDriverNo, String algFlag, String issuerFlag, String cardChildType, String cpuVersion, String uniqueFlag,
                       Integer upStatus, String singleRecord, String isHalfPrices, String mchId, String companyNo, String reserve_1, String reserve_2, String reserve_3, String reserve_4,
                       String reserve_5, String cardRecordType, String cardMacVersion, String cardMacIndex) {
        this.id = id;
        this.status = status;
        this.cardType = cardType;
        this.cardModuleType = cardModuleType;
        this.transType = transType;
        this.transNo = transNo;
        this.cardNo = cardNo;
        this.cardBalance = cardBalance;
        this.payFee = payFee;
        this.transTime = transTime;
        this.transNo2 = transNo2;
        this.tac = tac;
        this.lineNo = lineNo;
        this.busNo = busNo;
        this.driverNo = driverNo;
        this.pasmNo = pasmNo;
        this.direction = direction;
        this.stationId = stationId;
        this.fareFlag = fareFlag;
        this.localLineNo = localLineNo;
        this.localBusNo = localBusNo;
        this.localDriverNo = localDriverNo;
        this.algFlag = algFlag;
        this.issuerFlag = issuerFlag;
        this.cardChildType = cardChildType;
        this.cpuVersion = cpuVersion;
        this.uniqueFlag = uniqueFlag;
        this.upStatus = upStatus;
        this.singleRecord = singleRecord;
        this.isHalfPrices = isHalfPrices;
        this.mchId = mchId;
        this.companyNo = companyNo;
        this.reserve_1 = reserve_1;
        this.reserve_2 = reserve_2;
        this.reserve_3 = reserve_3;
        this.reserve_4 = reserve_4;
        this.reserve_5 = reserve_5;
        this.cardRecordType = cardRecordType;
        this.cardMacVersion = cardMacVersion;
        this.cardMacIndex = cardMacIndex;
    }

    @Generated(hash = 476783687)
    public ConsumeCard() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTransType() {
        return this.transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransNo() {
        return this.transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardBalance() {
        return this.cardBalance;
    }

    public void setCardBalance(String cardBalance) {
        this.cardBalance = cardBalance;
    }

    public String getPayFee() {
        return this.payFee;
    }

    public void setPayFee(String payFee) {
        this.payFee = payFee;
    }

    public String getTransTime() {
        return this.transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getTransNo2() {
        return this.transNo2;
    }

    public void setTransNo2(String transNo2) {
        this.transNo2 = transNo2;
    }

    public String getTac() {
        return this.tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public String getLineNo() {
        return this.lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getBusNo() {
        return this.busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getDriverNo() {
        return this.driverNo;
    }

    public void setDriverNo(String driverNo) {
        this.driverNo = driverNo;
    }

    public String getPasmNo() {
        return this.pasmNo;
    }

    public void setPasmNo(String pasmNo) {
        this.pasmNo = pasmNo;
    }

    public String getDirection() {
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStationId() {
        return this.stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getFareFlag() {
        return this.fareFlag;
    }

    public void setFareFlag(String fareFlag) {
        this.fareFlag = fareFlag;
    }

    public Integer getUpStatus() {
        return this.upStatus;
    }

    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
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

    public String getUniqueFlag() {
        return this.uniqueFlag;
    }

    public void setUniqueFlag(String uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    public String getSingleRecord() {
        return this.singleRecord;
    }

    public void setSingleRecord(String singleRecord) {
        this.singleRecord = singleRecord;
    }

    public String getAlgFlag() {
        return this.algFlag;
    }

    public void setAlgFlag(String algFlag) {
        this.algFlag = algFlag;
    }

    public String getIssuerFlag() {
        return this.issuerFlag;
    }

    public void setIssuerFlag(String issuerFlag) {
        this.issuerFlag = issuerFlag;
    }

    public String getCardChildType() {
        return this.cardChildType;
    }

    public void setCardChildType(String cardChildType) {
        this.cardChildType = cardChildType;
    }

    public String getCpuVersion() {
        return this.cpuVersion;
    }

    public void setCpuVersion(String cpuVersion) {
        this.cpuVersion = cpuVersion;
    }

    @Override
    public String toString() {
        return "ConsumeCard{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", cardType='" + cardType + '\'' +
                ", transType='" + transType + '\'' +
                ", transNo='" + transNo + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", cardBalance='" + cardBalance + '\'' +
                ", payFee='" + payFee + '\'' +
                ", transTime='" + transTime + '\'' +
                ", transNo2='" + transNo2 + '\'' +
                ", tac='" + tac + '\'' +
                ", lineNo='" + lineNo + '\'' +
                ", busNo='" + busNo + '\'' +
                ", driverNo='" + driverNo + '\'' +
                ", pasmNo='" + pasmNo + '\'' +
                ", direction='" + direction + '\'' +
                ", stationId='" + stationId + '\'' +
                ", fareFlag='" + fareFlag + '\'' +
                ", algFlag='" + algFlag + '\'' +
                ", issuerFlag='" + issuerFlag + '\'' +
                ", cardChildType='" + cardChildType + '\'' +
                ", cpuVersion='" + cpuVersion + '\'' +
                ", uniqueFlag='" + uniqueFlag + '\'' +
                ", upStatus=" + upStatus +
                ", singleRecord='" + singleRecord + '\'' +
                ", reserve_1='" + reserve_1 + '\'' +
                ", reserve_2='" + reserve_2 + '\'' +
                ", reserve_3='" + reserve_3 + '\'' +
                '}';
    }

    public String getLocalLineNo() {
        return this.localLineNo;
    }

    public void setLocalLineNo(String localLineNo) {
        this.localLineNo = localLineNo;
    }

    public String getLocalBusNo() {
        return this.localBusNo;
    }

    public void setLocalBusNo(String localBusNo) {
        this.localBusNo = localBusNo;
    }

    public String getLocalDriverNo() {
        return this.localDriverNo;
    }

    public void setLocalDriverNo(String localDriverNo) {
        this.localDriverNo = localDriverNo;
    }

    public String getCardModuleType() {
        return this.cardModuleType;
    }

    public void setCardModuleType(String cardModuleType) {
        this.cardModuleType = cardModuleType;
    }

    public String getIsHalfPrices() {
        return this.isHalfPrices;
    }

    public void setIsHalfPrices(String isHalfPrices) {
        this.isHalfPrices = isHalfPrices;
    }

    public String getReserve_4() {
        return this.reserve_4;
    }

    public void setReserve_4(String reserve_4) {
        this.reserve_4 = reserve_4;
    }

    public String getReserve_5() {
        return this.reserve_5;
    }

    public void setReserve_5(String reserve_5) {
        this.reserve_5 = reserve_5;
    }

    public String getMchId() {
        return this.mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getCompanyNo() {
        return this.companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }


}

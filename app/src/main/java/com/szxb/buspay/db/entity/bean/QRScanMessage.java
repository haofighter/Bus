package com.szxb.buspay.db.entity.bean;

import com.szxb.buspay.db.entity.scan.PosRecord;

/**
 * 作者: Tangren on 2017-09-11
 * 包名：szxb.com.commonbus.entity
 * 邮箱：996489865@qq.com
 * TODO:扫码信息
 */

public class QRScanMessage {

    private PosRecord posRecord;
    private CntEntity cntEntity;
    private int result;
    private String key;
    private String message;
    private boolean isOk;

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public PosRecord getPosRecord() {
        return posRecord;
    }

    public void setPosRecord(PosRecord posRecord) {
        this.posRecord = posRecord;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public QRScanMessage(PosRecord posRecord, int result) {
        this.posRecord = posRecord;
        this.result = result;
    }

    public QRScanMessage(PosRecord posRecord, int result, String keyCode) {
        this.posRecord = posRecord;
        this.result = result;
        this.key = keyCode;
    }


    public QRScanMessage(CntEntity cntEntity, int result) {
        this.cntEntity = cntEntity;
        this.result = result;
    }

    public CntEntity getCntEntity() {
        return cntEntity;
    }

    public void setCntEntity(CntEntity cntEntity) {
        this.cntEntity = cntEntity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "QRScanMessage{" +
                "posRecord=" + posRecord +
                ", cntEntity=" + cntEntity +
                ", result=" + result +
                '}';
    }
}

package com.szxb.unionpay.dispose;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.module.BankScanPay;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.unionpay.entity.UnionPayEntity;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;
import static com.szxb.buspay.util.DateUtil.getCurrentDate;

/**
 * 作者：Tangren on 2018-09-11
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class BankQRParse {
    synchronized public BankResponse parseResponse(int amount, String qrCode) {
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.START_DIALOG));
        BusllPosManage.getPosManager().setTradeSeq();
        //同步保存记录
        saveQRUnionPayEntity(amount, qrCode);
        SyncSSLRequest syncSSLRequest = new SyncSSLRequest();
        Iso8583Message iso8583Message = BankScanPay.getInstance()
                .qrPayMessage(qrCode, amount, BusllPosManage.getPosManager().getTradeSeq(), BusllPosManage.getPosManager().getMacKey());
        BankResponse response = syncSSLRequest.request(Config.PAY_TYPE_BANK_QR, iso8583Message.getBytes());
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.STOP_DIALOG));
        return response;
    }

    private void saveQRUnionPayEntity(int amount, String qrCode) {
        UnionPayEntity payEntity = new UnionPayEntity();
        payEntity.setMchId(BusllPosManage.getPosManager().getMchId());
        payEntity.setUnionPosSn(BusllPosManage.getPosManager().getPosSn());
        payEntity.setPosSn(BusApp.getPosManager().getDriverNo());
        payEntity.setBusNo(BusApp.getPosManager().getBusNo());
        payEntity.setTotalFee(String.valueOf(amount));
        //注:支付金额记录存储需根据交易返回为准,未防止交易失败导致金额错误
        payEntity.setPayFee("0");
        payEntity.setTime(getCurrentDate());
        payEntity.setTradeSeq(String.format("%06d", BusllPosManage.getPosManager().getTradeSeq()));
        payEntity.setMainCardNo(qrCode);
        payEntity.setReserve_1(qrCode);
        payEntity.setBatchNum(BusllPosManage.getPosManager().getBatchNum());
        payEntity.setBus_line_name(BusApp.getPosManager().getChinese_name());
        payEntity.setBus_line_no(BusApp.getPosManager().getLineNo());
        payEntity.setDriverNum(BusApp.getPosManager().getDriverNo());
        payEntity.setUnitno(BusApp.getPosManager().getUnitno());
        payEntity.setUpStatus(1);//0已支付、1未支付
        payEntity.setUniqueFlag(String.format("%06d", BusllPosManage.getPosManager().getTradeSeq()) + BusllPosManage.getPosManager().getBatchNum());
        payEntity.setReserve_2("QR");
        //记录也同步保存
        getDaoSession().getUnionPayEntityDao().insertOrReplaceInTx(payEntity);
    }
}

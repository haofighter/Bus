package com.szxb.buspay.task.card;

import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.bean.card.SearchCard;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.dispose.BankCardParse;
import com.szxb.unionpay.dispose.BankResponse;

import static com.szxb.buspay.util.Util.fen2Yuan;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者：Tangren on 2018-07-27
 * 包名：com.szxb.buspay.task.card
 * 邮箱：996489865@qq.com
 * TODO:公用的
 */

public class CommonBase {


    /**
     * 签到
     *
     * @param searchCard .
     */
    public static void empSign(SearchCard searchCard) {
        //未签到
        if (TextUtils.equals(searchCard.cardType, "06")) {
            //只允许普通员工签到
            ConsumeCard response = CommonBase.response(0, 0, false, false, false, true, searchCard.cardModuleType);
            SLog.d("CommonBase(empSign.java:44)签到>>" + response);
            if (TextUtils.equals(response.getStatus(), "00") &&
                    TextUtils.equals(response.getTransType(), "12")) {
                //司机卡上班
                BusApp.getPosManager().setDriverNo(response.getTac(), response.getCardNo());
                notice(Config.IC_TO_WORK, "司机卡上班[" + response.getTac() + "]", true);
                saveRecord(response);
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
            } else {
                if (TextUtils.equals(response.getStatus(), "F2")) {
                    DateUtil.setK21Time();
                }
                BusToast.showToast(BusApp.getInstance(), "签到失败[" + response.getStatus() + "|" + response.getTransType() + "]", false);
            }
        } else {
            //如果不是普通员工签到卡,则不做任何提示
            SLog.d("CommonBase(empSign.java:57)不是普通员工签到卡>>>则不做任何提示");
        }
    }


    /**
     * 消费报文
     *
     * @param pay_fee    实际扣款
     * @param normal_pay 普通卡金额
     * @param isBlack    是否是黑名单：0x01黑名单锁卡
     * @param isWhite    是否是白名单,预留
     * @return 消费响应
     */
    public static ConsumeCard response(int pay_fee, int normal_pay, boolean isBlack, boolean isWhite,
                                       boolean workStatus, boolean isSign, String cardModuleType) {
        int total_fee = BusApp.getPosManager().getBasePrice();
        byte[] data = new byte[64];
        byte[] amount = HexUtil.int2Bytes(pay_fee, 3);
        byte[] baseAmount = HexUtil.int2Bytes(total_fee, 3);
        byte[] black = new byte[]{(byte) (isBlack ? 0x01 : 0x00)};
        byte[] white = new byte[]{0x01};
        byte[] busNo = HexUtil.str2Bcd(BusApp.getPosManager().getBusNo());
        byte[] lineNo = HexUtil.hex2byte(BusApp.getPosManager().getLineNo());
        byte[] workStatus_ = new byte[]{(byte) (workStatus ? 0x01 : 0x00)};
        byte[] driverNo = HexUtil.str2Bcd(BusApp.getPosManager().getDriverNo());
        byte[] direction = new byte[]{0x00};
        byte[] stationId = new byte[]{0x01};
        byte[] normalAmount = HexUtil.int2Bytes(normal_pay, 3);
        byte[] sendData = HexUtil.mergeByte(amount, baseAmount, black, white, busNo, lineNo,
                workStatus_, driverNo, direction, stationId, normalAmount, data);

        int ret = libszxb.qxcardprocess(sendData);
        return new ConsumeCard(sendData, isSign, "zibo", cardModuleType);
    }


    /**
     * 检查余额并做提示
     *
     * @param response .
     */
    public static void checkTheBalance(ConsumeCard response, int music) {
        checkTheBalance(response, music, true);
    }


    /**
     * @param response   .
     * @param music      .
     * @param saveRecord 是否报错记录,默认保存
     */
    public static void checkTheBalance(ConsumeCard response, int music, boolean saveRecord) {
        notice(music, "本次扣款:"
                + fen2Yuan(string2Int(response.getPayFee())) + "元\n余额:"
                + fen2Yuan(string2Int(response.getCardBalance())) + "元", true);

        if (saveRecord) {
            saveRecord(response);
        }
    }


    /**
     * @param response 0金额处理
     *                 扣款0元,但是底层返回却是应付金额
     */
    public static void zeroDis(ConsumeCard response) {
        if (TextUtils.equals(response.getTransType(), "07")) {
            response.setPayFee("0");
        }
    }


    /**
     * @param response 下班
     */
    public static void offWork(ConsumeCard response, int music) {
        BusApp.getPosManager().setDriverNo(String.format("%08d", 0), String.format("%08d", 0));
        notice(music, "司机卡下班[00]", true);
        saveRecord(response);
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
    }

    /**
     * @param response 下班
     */
    public static void offWork(ConsumeCard response) {
        offWork(response, Config.IC_OFF_WORK);
    }


    /**
     * @param music  .
     * @param tipVar .
     * @param isOk   .
     */
    public static void notice(int music, String tipVar, boolean isOk) {
        SoundPoolUtil.play(music);
        QRScanMessage message = new QRScanMessage(new PosRecord(), QRCode.TIP);
        message.setMessage(tipVar);
        message.setOk(isOk);
        RxBus.getInstance().send(message);

    }

    /**
     * 保存交易记录
     *
     * @param consumeCard .
     */
    public static void saveRecord(ConsumeCard consumeCard) {
        ThreadFactory.getScheduledPool().execute(new WorkThread("zibo", consumeCard));
    }


    /**
     * @param bankICResponse .
     * @param searchCard     .
     * @throws Exception .
     */
    public static BankResponse unionDispose(BankResponse bankICResponse, SearchCard searchCard) throws Exception {
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.START_DIALOG));
        boolean isNull = bankICResponse.getResCode() == -999;
        BankCardParse cardParse = new BankCardParse();

        bankICResponse = cardParse.parseResponse(bankICResponse,
                isNull ? "0" : bankICResponse.getMainCardNo(),
                isNull ? 0 : bankICResponse.getLastTime(), 1/*BusApp.getPosManager().getUnionPayPrice()*/, searchCard.cityCode + searchCard.cardNo);
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.STOP_DIALOG));

        if (bankICResponse.getResCode() > 0) {
            BusToast.showToast(BusApp.getInstance(), bankICResponse.getMsg(), true);
        } else {
            BusToast.showToast(BusApp.getInstance(), bankICResponse.getMsg() + "[" + bankICResponse.getResCode() + "]", false);
        }
        return bankICResponse;
    }
}

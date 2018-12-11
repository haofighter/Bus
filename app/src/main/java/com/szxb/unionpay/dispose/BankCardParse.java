package com.szxb.unionpay.dispose;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.Config;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.module.BankPay;
import com.szxb.java8583.module.BusCard;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.PassCode;
import com.szxb.unionpay.entity.TERM_INFO;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.HexUtil;
import com.szxb.unionpay.unionutil.TLV;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;
import static com.szxb.buspay.util.DateUtil.getCurrentDate;

/**
 * 作者：Tangren on 2018-09-08
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:银行卡解析
 */

public class BankCardParse {

    //金额超限
    public static final int ERROR_AMOUNT_OUT = -1;
    public static final int ERROR_2 = -2;
    public static final int ERROR_3 = -3;
    public static final int ERROR_4 = -4;
    public static final int ERROR_5 = -5;
    public static final int ERROR_6 = -6;
    public static final int ERROR_7 = -7;
    public static final int ERROR_8 = -8;
    public static final int ERROR_9 = -9;
    public static final int ERROR_10 = -10;
    //连续刷卡
    public static final int ERROR_REPEAT = -11;
    //网络超时
    public static final int ERROR_NET = -12;
    //支付成功
    public static final int SUCCESS = 200;
    //重新签到
    public static final int ERROR_RE_SIGN = -302;
    //其他错误
    public static final int ERROR_ELSE = -400;


    private List<String[]> listTLV;
    private Map<String, String> mapTLV;
    private TERM_INFO term_info = new TERM_INFO();
    private PassCode retPassCode = new PassCode();

    synchronized public BankResponse parseResponse(BankResponse icResponse, String lastMainCardNo, long lastTime, int amount, String aid) throws Exception {
        SLog.d("BankCardParse(parseResponse.java:68)aid=" + aid);
        icResponse.setMsg("参数错误");
        if (TextUtils.isEmpty(aid)) {
            icResponse.setResCode(ERROR_9);
            icResponse.setMsg("AID参数错误");
            return icResponse;
        }

        if (amount > 1500) {
            icResponse.setResCode(ERROR_AMOUNT_OUT);
            icResponse.setMsg("金额超出最大限制[" + amount + "]");
            return icResponse;
        }

        String sAID = "00A40400" + String.format("%02X", aid.length() / 2) + aid;
        String[] retStr = libszxb.RFID_APDU(sAID);

        if (retStr == null || retStr[0] == null) {
            SLog.d("UnionCard(run.java:110)RFID_APDU>>NULL>>aid=" + aid);
            icResponse.setResCode(ERROR_2);
            return icResponse;
        }

        if (!retStr[0].equals("9000")) {
            SLog.d("run(LoopThread.java:134)>> -2>>>[0]=" + retStr[0]);
            icResponse.setResCode(ERROR_3);
            return icResponse;
        }

        if (TextUtils.isEmpty(retStr[1])) {
            SLog.d("UnionCard(run.java:123)retStr[1]==NULL");
            icResponse.setResCode(ERROR_4);
            return icResponse;
        }

        listTLV = TLV.decodingTLV(retStr[1]);
        mapTLV = TLV.decodingTLV(listTLV);

        listTLV = TLV.decodingPDOL(mapTLV.get("9f38"));

        SLog.d("BankCardParse(parseResponse.java:107)9f38=" + mapTLV.get("9f38"));

        mapTLV = TLV.decodingTLV(listTLV);

        int len = 0;
        StringBuilder pDOLBuilder = new StringBuilder();

        for (String key : mapTLV.keySet()) {
            len += Integer.parseInt(mapTLV.get(key));
            SLog.d("BankCardParse(parseResponse.java:116)key=" + key);
            switch (key) {
                case "9f66"://终端交易属性,是否支持CDCVM
                    pDOLBuilder.append(term_info.ttq);
                    break;
                case "9f02"://授权金额（支付金额）
                    String payMoney = String.format("%012d", amount);
                    pDOLBuilder.append(payMoney);
                    retPassCode.setTAG9F02(payMoney);
                    break;
                case "9f03"://返现金额，0
                    String str9f03 = String.format("%012d", 0);
                    pDOLBuilder.append(str9f03);
                    retPassCode.setTAG9F03(str9f03);
                    break;
                case "9f1a"://国家代码
                    pDOLBuilder.append(term_info.terminal_country_code);
                    retPassCode.setTAG9F1A(term_info.terminal_country_code);
                    break;
                case "95"://终端验证结果
                    String str95 = String.format("%010d", 0);
                    pDOLBuilder.append(str95);
                    retPassCode.setTAG95(str95);
                    break;
                case "5f2a"://交易货币代码
                    pDOLBuilder.append(term_info.transaction_currency_code);
                    retPassCode.setTAG5F2A(term_info.transaction_currency_code);
                    break;
                case "9a"://交易日期yyMMdd
                    String transDate = getCurrentDate("yyMMdd");
                    pDOLBuilder.append(transDate);
                    retPassCode.setTAG9A(transDate);
                    break;
                case "9c"://交易类型
                    pDOLBuilder.append("00");
                    retPassCode.setTAG9C("00");
                    break;
                case "9f37"://不可预知数
                    Random random = new Random();
                    String randomStr = String.format("%08x",
                            random.nextInt());
                    pDOLBuilder.append(randomStr);
                    retPassCode.setTAG9F37(randomStr);
                    break;
                case "df60"://
                    pDOLBuilder.append("00");
                    break;
                case "9f21"://时间HHmmss
                    String transTime = getCurrentDate("HHmmss");
                    pDOLBuilder.append(transTime);
                    break;
                case "df69"://
                    pDOLBuilder.append("01");
                    break;
            }
        }

        String GPO = "80a80000"
                + Integer.toHexString(len + 2) + "83"
                + Integer.toHexString(len) + pDOLBuilder.toString();

        SLog.d("UnionCard(run.java:191)GPO=" + GPO);

        retStr = libszxb.RFID_APDU(GPO);
        if (null == retStr) {
            SLog.d("UnionCard(run.java:192)RFID_APDU>>NULL");
            icResponse.setResCode(ERROR_5);
            return icResponse;
        }

        if (TextUtils.isEmpty(retStr[0])) {
            SLog.d("UnionCard(run.java:206)retStr[0]==NULL");
            icResponse.setResCode(ERROR_6);
            return icResponse;
        }

        if (!retStr[0].equalsIgnoreCase("9000")) {
            SLog.d("UnionCard(run.java:198)>>>无效>>retStr[0]=" + retStr[0]);
            icResponse.setResCode(ERROR_7);
            return icResponse;
        }

        if (TextUtils.isEmpty(retStr[1])) {
            SLog.d("UnionCard(run.java:218)retStr[1]==NULL");
            icResponse.setResCode(ERROR_8);
            return icResponse;
        }


        listTLV = TLV.decodingTLV(retStr[1]);
        mapTLV = TLV.decodingTLV(listTLV);


        if (mapTLV.containsKey("9f36")) {
            retPassCode.setTAG9F36(mapTLV.get("9f36"));
        }

        if (mapTLV.containsKey("5f34")) {
            retPassCode.setTAG5F34(mapTLV.get("5f34"));
        }

        if (mapTLV.containsKey("9f10")) {
            retPassCode.setTAG9F10(mapTLV.get("9f10"));
        }

        if (mapTLV.containsKey("57")) {
            retPassCode.setTAG57(mapTLV.get("57"));
        }

        if (mapTLV.containsKey("9f27")) {
            retPassCode.setTAG9F27(mapTLV.get("9f27"));
            Log.d("9F27", retPassCode.getTAG9F27());
        }

        if (mapTLV.containsKey("9f26")) {
            retPassCode.setTAG9F26(mapTLV.get("9f26"));
        }

        if (mapTLV.containsKey("82")) {
            retPassCode.setTAG82(mapTLV.get("82"));
        }

        BusllPosManage.getPosManager().setTradeSeq();

        int index = retPassCode.getTAG57().indexOf("d");

        SLog.d("UnionCard(run.java:263)getTAG57=" + retPassCode.getTAG57() + "<<index=" + index);

        String mainCardNo = retPassCode.getTAG57();
        if (index > 0) {
            mainCardNo = retPassCode.getTAG57().substring(0, index);
        }
        String cardNum = retPassCode.getTAG5F34();
        String cardData = retPassCode.getTAG57();
        String tlv = retPassCode.toString();


        if (BuildConfig.BIN_NAME.contains("zhaoyuan")) {
            //如果是招远则做卡限制
            if (cardNum.indexOf("62232006") != 0 || cardNum.indexOf("62231906") != 0) {
                icResponse.setResCode(ERROR_10);
                icResponse.setMsg("暂不支持此银联卡");
                return icResponse;
            }
        }

        //重刷拦截
        if (TextUtils.equals(mainCardNo, lastMainCardNo)) {
            if (SystemClock.elapsedRealtime() - lastTime < 10000) {
                icResponse.setResCode(ERROR_REPEAT);
                icResponse.setMsg("禁止连续刷卡");
                return icResponse;
            }
        }


        BusCard busCard = new BusCard();
        busCard.setMainCardNo(mainCardNo);
        busCard.setCardNum(cardNum);
        busCard.setMagTrackData(cardData);
        busCard.setTlv55(tlv);
        busCard.setMacKey(BusllPosManage.getPosManager().getMacKey());
        busCard.setMoney(amount);
        busCard.setTradeSeq(BusllPosManage.getPosManager().getTradeSeq());

        Iso8583Message iso8583Message = BankPay.getInstance().payMessage(busCard);

        byte[] sendData = iso8583Message.getBytes();

        saveUnionPayEntity(amount, mainCardNo, tlv, sendData, cardNum);

        SyncSSLRequest syncSSLRequest = new SyncSSLRequest();
        icResponse = syncSSLRequest.request(Config.PAY_TYPE_BANK_IC, sendData);

        return icResponse;
    }

    /**
     * @param amount     金额
     * @param mainCardNo 主账号
     * @param tlv        tlv
     * @param sendData   单笔记录
     * @return 记录
     */
    @NonNull
    private void saveUnionPayEntity(int amount, String mainCardNo, String tlv, byte[] sendData, String cardNum) {
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
        payEntity.setMainCardNo(mainCardNo);
        //Reserve_1 cardNum
        payEntity.setReserve_1(cardNum);
        payEntity.setBatchNum(BusllPosManage.getPosManager().getBatchNum());
        payEntity.setBus_line_name(BusApp.getPosManager().getChinese_name());
        payEntity.setBus_line_no(BusApp.getPosManager().getLineNo());
        payEntity.setDriverNum(BusApp.getPosManager().getDriverNo());
        payEntity.setUnitno(BusApp.getPosManager().getUnitno());
        payEntity.setUpStatus(1);//0已支付、1未支付
        payEntity.setUniqueFlag(String.format("%06d", BusllPosManage.getPosManager().getTradeSeq()) + BusllPosManage.getPosManager().getBatchNum());
        payEntity.setTlv55(tlv);
        payEntity.setSingleData(HexUtil.bytesToHexString(sendData));
        //记录也同步保存
        getDaoSession().getUnionPayEntityDao().insertOrReplaceInTx(payEntity);
    }


}

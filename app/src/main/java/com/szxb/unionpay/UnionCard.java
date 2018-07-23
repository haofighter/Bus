package com.szxb.unionpay;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.UnionAidEntityDao;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.module.BankPay;
import com.szxb.java8583.module.BusCard;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.config.UnionConfig;
import com.szxb.unionpay.entity.PassCode;
import com.szxb.unionpay.entity.TERM_INFO;
import com.szxb.unionpay.entity.UnionAidEntity;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.HexUtil;
import com.szxb.unionpay.unionutil.TLV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.szxb.buspay.util.DateUtil.getCurrentDate;
import static com.szxb.unionpay.config.UnionConfig.EXCEPTION;
import static com.szxb.unionpay.config.UnionConfig.INVALID;
import static com.szxb.unionpay.config.UnionConfig.NULL;
import static com.szxb.unionpay.config.UnionConfig.REPEAT;

/**
 * 作者：Tangren on 2018-07-07
 * 包名：com.szxb.unionpay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class UnionCard {

    private List<String[]> listTLV;

    private Map<String, String> mapTLV;

    private TERM_INFO term_info = new TERM_INFO();

    private int money = 1;

    private ArrayList<Map<String, String>> AIDiameters;

    private PassCode retPassCode = new PassCode();

    private Map<String, String> aid = new HashMap<>();

    private UnionCard() {
        AIDiameters = new ArrayList<>();
        UnionAidEntityDao dao = DBCore.getDaoSession().getUnionAidEntityDao();
        List<UnionAidEntity> list = dao.queryBuilder().build().list();
        for (int i = 0; i < list.size(); i++) {
            String useaidparameter = list.get(i).getIcParam().substring(2, list.get(i).getIcParam().length());
            Log.d("Field AID", useaidparameter);
            List<String[]> listaidparameters = TLV.decodingTLV(useaidparameter);
            Map<String, String> aidparametersMap = TLV
                    .decodingTLV(listaidparameters);
            AIDiameters.add(aidparametersMap);
        }

    }

    private volatile static UnionCard instance = null;

    public static UnionCard getInstance() {
        if (instance == null) {
            synchronized (UnionCard.class) {
                if (instance == null) {
                    instance = new UnionCard();
                }
            }
        }
        return instance;
    }


    private String tempStr;
    private long lastScanTime2 = 0;
    private int ret = 0;

    public void run(String aid) {
        try {
//            money = BusApp.getPosManager().getUnionPayPrice();
            money = 1;
            do {

                String sAID = "00A40400" + String.format("%02X", aid.length() / 2) + aid;
                String[] retStr = libszxb.RFID_APDU(sAID);

                if (retStr == null || retStr[0] == null) {
                    ret = NULL;
                    break;
                }

                if (!retStr[0].equals("9000")) {
                    SLog.d("run(LoopThread.java:134)>> -2>>>[0]=" + retStr[0]);
                    ret = INVALID;
                    break;
                }

                listTLV = TLV.decodingTLV(retStr[1]);
                mapTLV = TLV.decodingTLV(listTLV);

                listTLV = TLV.decodingPDOL(mapTLV.get("9f38"));
                mapTLV = TLV.decodingTLV(listTLV);

                int len = 0;
                StringBuilder pDOLBuilder = new StringBuilder();

                for (String key : mapTLV.keySet()) {
                    len += Integer.parseInt(mapTLV.get(key));
                    switch (key) {
                        case "9f66"://终端交易属性,是否支持CDCVM
                            pDOLBuilder.append(term_info.ttq);
                            break;
                        case "9f02"://授权金额（支付金额）
                            String payMoney = String.format("%012d", money);
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
                    }
                }

                String GPO = "80a80000"
                        + Integer.toHexString(len + 2) + "83"
                        + Integer.toHexString(len) + pDOLBuilder.toString();

                retStr = libszxb.RFID_APDU(GPO);
                if (null == retStr) {
                    ret = NULL;
                    break;
                }

                if (!retStr[0].equalsIgnoreCase("9000")) {
                    ret = INVALID;
                    break;
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
                String mainCardNo = retPassCode.getTAG57().substring(0, retPassCode.getTAG57().indexOf("d"));
                String cardNum = retPassCode.getTAG5F34();
                String cardData = retPassCode.getTAG57();
                String tlv = retPassCode.toString();

                if (filterCheck(mainCardNo)) {
                    ret = REPEAT;
                    if (checkQR(SystemClock.elapsedRealtime(), lastScanTime2) && TextUtils.equals(mainCardNo, tempStr)) {
                        BusToast.showToast(BusApp.getInstance().getApplicationContext(), "此卡刚刚刷过", false);
                    }
                    break;
                }

                SLog.d("run(LoopThread.java:233)mainCardNo=" + mainCardNo + ",cardNum=" + cardNum + ",cardData=" + cardData + ",tlv=" + tlv);

                SLog.d("run(LoopThread.java:235)mackey=" + BusllPosManage.getPosManager().getMacKey());

                BusCard busCard = new BusCard();
                busCard.setMainCardNo(mainCardNo);
                busCard.setCardNum(cardNum);
                busCard.setMagTrackData(cardData);
                busCard.setTlv55(tlv);
                busCard.setMacKey(BusllPosManage.getPosManager().getMacKey());
                busCard.setMoney(1);
                busCard.setTradeSeq(BusllPosManage.getPosManager().getTradeSeq());

                Iso8583Message iso8583Message = BankPay.getInstance().payMessage(busCard);
                byte[] sendData = iso8583Message.getBytes();

                SLog.d("LoopCard(run.java:242)" + iso8583Message.toFormatString());

                UnionPayEntity payEntity = new UnionPayEntity();
                payEntity.setMchId(BusllPosManage.getPosManager().getMchId());
                payEntity.setUnionPosSn(BusllPosManage.getPosManager().getPosSn());
                payEntity.setPosSn(BusApp.getPosManager().getDriverNo());
                payEntity.setBusNo(BusApp.getPosManager().getBusNo());
                payEntity.setTotalFee(String.valueOf(money));
                //注:支付金额记录存储需根据交易返回为准,未防止交易失败导致金额错误
                payEntity.setPayFee("0");
                payEntity.setTime(DateUtil.getCurrentDate());
                payEntity.setTradeSeq(String.format("%06d", BusllPosManage.getPosManager().getTradeSeq()));
                payEntity.setMainCardNo(mainCardNo);
                payEntity.setBatchNum(BusllPosManage.getPosManager().getBatchNum());
                payEntity.setBus_line_name(BusApp.getPosManager().getChinese_name());
                payEntity.setBus_line_no(BusApp.getPosManager().getLineName());
                payEntity.setDriverNum(BusApp.getPosManager().getDriverNo());
                payEntity.setUnitno(BusApp.getPosManager().getUnitno());
                payEntity.setUpStatus(1);//0已支付、1未支付
                payEntity.setUniqueFlag(String.format("%06d", BusllPosManage.getPosManager().getTradeSeq()) + BusllPosManage.getPosManager().getBatchNum());
                payEntity.setTlv55(tlv);
                payEntity.setSingleData(HexUtil.bytesToHexString(sendData));

                ThreadScheduledExecutorUtil.getInstance().getService().submit(new WorkThread("union", payEntity));

                SLog.d("LoopCard(run.java:278)" + payEntity);
                UnionPay.getInstance().exeSSL(UnionConfig.PAY, sendData);

                ret = 0;
                BusToast.showToast(BusApp.getInstance(), "刷卡成功:正在向银联发起请求", true);
                tempStr = mainCardNo;
                SLog.d("LoopCard(run.java:309)ret=" + ret);
                break;
            } while (true);

        } catch (Exception e) {
            e.printStackTrace();
            ret = EXCEPTION;
            SLog.d("run(LoopThread.java:258)哇哦……出现了异常,请及时处理>>>" + e.toString());
        }
    }

    /**
     * @param result .
     * @return 检查是否拦截
     */
    private boolean filterCheck(String result) {
        SLog.d("LoopCard(filterCheck.java:296)result=" + result + ",tempStr=" + tempStr);
        if (TextUtils.equals(result, tempStr) &&
                !checkQR2(SystemClock.elapsedRealtime(), lastScanTime2)) {
            return true;
        }
        lastScanTime2 = SystemClock.elapsedRealtime();
        return false;
    }

    /**
     * @param currentTime .
     * @param lastTime    .
     * @return .
     */
    private boolean checkQR2(long currentTime, long lastTime) {
        return currentTime - lastTime > 5000;
    }

    /**
     * @param currentTime .
     * @param lastTime    .
     * @return .
     */
    private boolean checkQR(long currentTime, long lastTime) {
        return currentTime - lastTime > 2000;
    }


}

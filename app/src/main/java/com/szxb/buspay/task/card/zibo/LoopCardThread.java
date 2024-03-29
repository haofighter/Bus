package com.szxb.buspay.task.card.zibo;

import android.os.SystemClock;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.bean.card.SearchCard;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.task.card.CommonBase;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.dispose.BankResponse;

import static com.szxb.buspay.task.card.CommonBase.checkTheBalance;
import static com.szxb.buspay.task.card.CommonBase.empSign;
import static com.szxb.buspay.task.card.CommonBase.notice;
import static com.szxb.buspay.task.card.CommonBase.offWork;
import static com.szxb.buspay.task.card.CommonBase.zeroDis;
import static com.szxb.buspay.util.Util.filter;
import static com.szxb.buspay.util.Util.hex2Int;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.task.card
 * 邮箱：996489865@qq.com
 * TODO:淄博
 */

public class LoopCardThread extends Thread {

    private boolean isBlack = false;
    private boolean isWhite = false;

    private String cardNoTemp = "0";
    private long lastTime = 0;
    private SearchCard searchCard;
    private BankResponse bankICResponse = new BankResponse();

    @Override
    public void run() {
        super.run();
        try {
            byte[] searchBytes = new byte[120];
            int status = libszxb.MifareGetSNR(searchBytes);
            if (status < 0) {
                if (status == -2) {
                    //重启K21
                    SLog.d("LoopCardThread(run.java:55)status==2>>尝试重启K21");
                    libszxb.deviceReset();
                }
                SLog.e("LoopCardThread(run.java:19)寻卡状态=" + status);
                searchCard = null;
                return;
            }

            if (searchBytes[0] != (byte) 0x00) {
                //如果寻卡状态不等于00..无法处理此卡
                searchCard = null;
                return;
            }

            searchCard = new SearchCard(searchBytes);

            //拦截黑名单
            //do
            isBlack = DBManager.queryBlack(searchCard.cardNo);

            //1S防抖动
            if (!filter(SystemClock.elapsedRealtime(), lastTime)) {
                return;
            }

            String driverNo = BusApp.getPosManager().getDriverNo();
            if (TextUtils.equals(driverNo, String.format("%08d", 0))
                    && !TextUtils.equals(searchCard.cardType, "06")) {
                return;
            }

            if (TextUtils.equals(searchCard.cardType, "02")
                    || TextUtils.equals(searchCard.cardType, "03")
                    || TextUtils.equals(searchCard.cardType, "04")) {
                //如果属于上述卡类型,如果票价小于普通卡金额做1分钟去重
                if (filterOneMinute(searchCard)) {
                    return;
                }
            }

            //防止重复刷卡
            //去重刷,同一个卡号1.5S内不提示
            if (!Util.check(cardNoTemp, searchCard.cardNo, lastTime)) {
//                BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", false);
                return;
            }

            //1.判断是否已签到
            //2.未签到
            //3.已签到
            if (TextUtils.equals(driverNo, String.format("%08d", 0))) {
                //未签到
                empSign(searchCard);
            } else {
                //判断,线路是否存在
                if (checkLine()) {
                    return;
                }
                //06员工卡要做特殊处理,此时员工卡要么是下班要么是正常消费
                //此员工卡卡号等于当前司机卡号时>>下班，否则正常消费
                if (TextUtils.equals(searchCard.cardType, "06")) {
                    String empNo = BusApp.getPosManager().getEmpNo();
                    if (!TextUtils.equals(searchCard.cityCode + searchCard.cardNo, empNo)) {
                        if (filterOneMinute(searchCard)) {
                            return;
                        }
                    }
                }
                //已签到
                elseCardControl(searchCard);
            }

            cardNoTemp = searchCard.cardNo;
            lastTime = SystemClock.elapsedRealtime();
            searchCard = null;
        } catch (Exception e) {
            e.printStackTrace();
            BusToast.showToast(BusApp.getInstance(), "系统异常", false);
            SLog.e("LoopCardThread(run.java:132)LoopCardThread出现异常>>>" + Util.getExectionStr(e));
        }
    }

    private boolean filterOneMinute(SearchCard searchCard) {
        int normalAmount = payFee(CardTypeZiBo.CARD_NORMAL);
        int currentAmount = payFee(searchCard.cardType);
        SLog.d("LoopCardThread(run.java:148)普通卡金额=" + normalAmount + ",当前卡金额=" + currentAmount);
        if (currentAmount < normalAmount) {
            if (DBManager.filterBrush(searchCard.cityCode + searchCard.cardNo, searchCard.cardType)) {
                BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", false);
                lastTime = SystemClock.elapsedRealtime();
                return true;
            }
        }
        return false;
    }

    /**
     * 检查线路是否存在
     *
     * @return .
     */
    private boolean checkLine() {
        if (BusApp.getPosManager().getLineInfoEntity() == null) {
            BusToast.showToast(BusApp.getInstance(), "请先配置线路信息", false);
            lastTime = SystemClock.elapsedRealtime();
            return true;
        }
        return false;
    }


    /**
     * @param searchCard 其他卡操作
     */
    private void elseCardControl(SearchCard searchCard) {

        if (TextUtils.equals(searchCard.cardModuleType, "F0")) {
            //银联卡
            try {

                if (!AppUtil.checkNetStatus()) {
                    BusToast.showToast(BusApp.getInstance(), "网络异常\n请选择其他方式乘车", false);
                    return;
                }

                bankICResponse = CommonBase.unionDispose(bankICResponse, searchCard);

            } catch (Exception e) {
                e.printStackTrace();
                BusToast.showToast(BusApp.getInstance(), "银联模块异常\n" + e.toString(), false);
            }
        } else {
            int pay_fee = payFee(searchCard.cardType);
            int normal_pay = payFee("01");
            ConsumeCard response = CommonBase.response(pay_fee, normal_pay, isBlack, isWhite, true, false, searchCard.cardModuleType);

            String status = response.getStatus();
            String balance = response.getCardBalance();
            String cardType = response.getCardType();
            if (TextUtils.equals(status, "00")) {
                if (TextUtils.equals(response.getTransType(), "11")) {
                    //黑名单卡锁卡交易
                    notice(Config.IC_INVALID, "黑名单卡[" + searchCard.cardType + "]", false);
                } else {
                    //正常消费
                    switch (cardType) {
                        case "01"://普通卡和CPU福利卡
                        case "05"://纪念卡
                            checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                            break;
                        case "02"://学生卡
                            zeroDis(response);
                            checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_STUDENT : Config.IC_RECHARGE);
                            break;
                        case "03"://老年卡
                            if (TextUtils.equals(response.getTransType(), "06")) {
                                //免费卡交易类型为06时判断余额是否小于5元
                                checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_OLD : Config.IC_RECHARGE);
                            } else {
                                zeroDis(response);
                                checkTheBalance(response, Config.IC_OLD);
                            }
                            break;
                        case "04"://免费卡
                            if (TextUtils.equals(response.getTransType(), "06")) {
                                //免费卡交易类型为06时判断余额是否小于5元
                                checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_HONOR : Config.IC_RECHARGE);
                            } else {
                                zeroDis(response);
                                checkTheBalance(response, Config.IC_HONOR);
                            }
                            break;
                        case "06"://员工卡
                            if (TextUtils.equals(response.getTransType(), "13")) {
                                //下班
                                offWork(response);
                            } else {
                                //员工卡正常消费
                                zeroDis(response);
                                checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE2 : Config.IC_RECHARGE);
                            }
                            break;
                        case "10"://线路票价设置卡(只做签退用)
                        case "11"://数据采集卡(只做签退用)
                            offWork(response);
                            break;
                        case "12"://签点卡
                            notice(Config.IC_BASE, "签点卡", true);
                            break;
                        case "13"://检测卡
                            notice(Config.IC_BASE, "检测卡", true);
                            break;
                        case "18"://稽查卡
                            notice(Config.IC_BASE, "稽查卡", true);
                            break;
                        default://其他卡类型
                            zeroDis(response);
                            checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                            break;
                    }
                }
            } else if (TextUtils.equals(status, "10")) {
                //淄博公交，语音提示“无偿献血卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_BLOOD);

            } else if (TextUtils.equals(status, "11")) {
                //淄博公交，语音提示“爱心卡”
                zeroDis(response);
                checkTheBalance(response, Config.IC_LOVE);

            } else if (status.equalsIgnoreCase("F1")) {
                //卡片未启用
                notice(Config.IC_ERROR, "卡片未启用[F1]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F2")) {
                //卡片过期
                notice(Config.IC_ERROR, "卡片过期[F2]", false);
                DateUtil.setK21Time();
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F3")) {
                //卡内余额不足
                notice(Config.IC_PUSH_MONEY, "卡内余额不足[F3]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F4")) {
                //此卡为黑名单卡(已经锁了)
                notice(Config.IC_LLLEGAL, "黑名单卡[F4]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F5")) {
                //不是本系统卡
                notice(Config.IC_PUSH_MONEY, "不是本系统卡[F5]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F6")) {
                //月票卡不能乘坐本线路
                notice(Config.IC_PUSH_MONEY, "月票卡不能乘坐本线路[F6]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("FE")
                    || status.equalsIgnoreCase("FF")) {
                //消费异常(重新刷卡)
                notice(Config.IC_RE, "重新刷卡[" + status + "]", false);
                this.searchCard.cardNo = "0";
            }

        }

    }

    /**
     * @param cardType 卡类型
     * @return .
     */
    private int payFee(String cardType) {
        String[] coefficent = BusApp.getPosManager().getCoefficent();
        int basePrices = BusApp.getPosManager().getBasePrice();
        switch (cardType) {
            case CardTypeZiBo.CARD_NORMAL://普通卡 01
            case CardTypeZiBo.CARD_MEMORY://纪念卡
                return string2Int(coefficent[0]) * basePrices / 100;
            case CardTypeZiBo.CARD_STUDENT://学生卡
                return string2Int(coefficent[1]) * basePrices / 100;
            case CardTypeZiBo.CARD_OLD://老年卡
                return string2Int(coefficent[2]) * basePrices / 100;
            case CardTypeZiBo.CARD_FREE://免费卡
                return string2Int(coefficent[3]) * basePrices / 100;
            case CardTypeZiBo.CARD_EMP://员工卡
                return string2Int(coefficent[4]) * basePrices / 100;
            case CardTypeZiBo.CARD_GATHER:
            case CardTypeZiBo.CARD_SIGNED:
            case CardTypeZiBo.CARD_CHECKED:
            case CardTypeZiBo.CARD_CHECK:
                return 0;
            default:
                return string2Int(coefficent[0]) * basePrices / 100;
        }
    }

}

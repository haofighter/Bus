package com.szxb.buspay.task.card.lw;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.ConsumeCardDao;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.bean.card.SearchCard;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionCard;

import java.util.List;

import static com.szxb.buspay.task.card.lw.CardTypeGJ.CARD_NORMAL;
import static com.szxb.buspay.util.Util.fen2Yuan;
import static com.szxb.buspay.util.Util.filter;
import static com.szxb.buspay.util.Util.hex2Int;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者：Tangren on 2018-07-23
 * 包名：com.szxb.buspay.task.card
 * 邮箱：996489865@qq.com
 * TODO:莱芜公交
 */

public class LoopCardThread_GJ extends Thread {

    private boolean isBlack = false;
    private boolean isWhite = false;

    private String cardNoTemp = "0";
    private long lastTime = 0;
    private SearchCard searchCard;


    @Override
    public void run() {
        super.run();
        try {
            byte[] searchBytes = new byte[120];
            int status = libszxb.MifareGetSNR(searchBytes);
            if (status < 0) {
                if (status == -2) {
                    //重启K21
                    SLog.d("LoopCardThread_GJ(run.java:55)status==2>>尝试重启K21");
                    libszxb.deviceReset();
                }
                SLog.e("LoopCardThread_GJ(run.java:19)寻卡状态=" + status);
                searchCard = null;
                return;
            }

            //1S防抖动
            if (!filter(SystemClock.elapsedRealtime(), lastTime)) {
                return;
            }

            //通信部的卡寻卡  问题卡返回
            if (searchBytes[0] == (byte) 0xA0) {
                //如果寻卡状态不等于00..无法处理此卡
                searchCard = new SearchCard(searchBytes);
                tranErrAll(searchCard.errorCode);
                lastTime = SystemClock.elapsedRealtime();
                return;
            }

            if (searchBytes[0] != (byte) 0x00) {
                //如果寻卡状态不等于00..无法处理此卡
                Log.i("获取到卡状态GJ", "   " + searchBytes[0]);
                return;
            }

            Log.i("刷卡", " searchCard  ");
            searchCard = new SearchCard(searchBytes);
            String cardNo = searchCard.cityCode + searchCard.cardNo;//"8000000102164414"
            ConsumeCard consumeCard = DBCore.getDaoSession().getConsumeCardDao().queryBuilder().where(ConsumeCardDao.Properties.CardNo.eq(cardNo)).orderDesc(ConsumeCardDao.Properties.TransTime).limit(1).unique();
            String[] coefficent = BusApp.getPosManager().getCoefficent();
            int basePrices = BusApp.getPosManager().getBasePrice();
            int price = string2Int(coefficent[0]) * basePrices / 100;

            if (consumeCard != null && Integer.parseInt(consumeCard.getPayFee()) < price && !searchCard.cardType.equals("46")&&!searchCard.cardType.equals("41")) {
                Log.i("test", "上一次时间" + DateUtil.String2Date(consumeCard.getTransTime()).getTime() + "   当前时间：" + System.currentTimeMillis());
                if (Math.abs(DateUtil.String2Date(consumeCard.getTransTime()).getTime() - System.currentTimeMillis()) < 60000) {//特殊卡1分钟间隔
                    BusToast.showToast(BusApp.getInstance(), "重复刷卡", false);
                    lastTime = SystemClock.elapsedRealtime();
                    return;
                }
            }

            Log.i("刷卡", " 时间间隔  ");
            String driverNo = BusApp.getPosManager().getDriverNo();
            if (TextUtils.equals(driverNo, String.format("%08d", 0))
                    && !TextUtils.equals(searchCard.cardType, "46") && !TextUtils.equals(searchCard.cardType, "06") && !TextUtils.equals(searchCard.cardType, "40")) {
                return;
            }

            Log.i("刷卡", " 时间间隔  ");
            if (TextUtils.equals(searchCard.cardType, "02")
                    || TextUtils.equals(searchCard.cardType, "03")
                    || TextUtils.equals(searchCard.cardType, "04")) {
                //如果属于上述卡类型,如果票价小于普通卡金额做1分钟去重
                int normalAmount = payFee(CARD_NORMAL);
                int currentAmount = payFee(searchCard.cardType);
                SLog.d("LoopCardThread(run.java:89)普通卡金额=" + normalAmount + ",当前卡金额=" + currentAmount);
                if (currentAmount < normalAmount) {
                    if (DBManager.filterBrush(searchCard.cityCode + searchCard.cardNo, searchCard.cardType)) {
                        BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", false);
                        lastTime = SystemClock.elapsedRealtime();
                        return;
                    }
                }
            }
            Log.i("刷卡", " 卡类型判断  ");
            //防止重复刷卡
            //去重刷,同一个卡号3S内不提示
            if (!Util.check(cardNoTemp, searchCard.cardNo, lastTime)) {
//                lastTime = SystemClock.elapsedRealtime();
//                BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", false);
                return;
            }

            Log.i("刷卡", " 3秒去重  ");

            //1.判断是否已签到
            //2.未签到
            //3.已签到
            if (TextUtils.equals(driverNo, String.format("%08d", 0))) {
                //未签到
                if (TextUtils.equals(searchCard.cardType, "46") || TextUtils.equals(searchCard.cardType, "06")) {
                    //只允许普通员工签到
                    ConsumeCard response = response(0, false, false, false, true);
                    SLog.d("LoopCardThread(run.java:67)签到>>" + response);
                    if (TextUtils.equals(response.getStatus(), "00") &&
                            TextUtils.equals(response.getTransType(), "12")) {
                        //司机卡上班
                        BusApp.getPosManager().setDriverNo(response.getTac(), response.getCardNo());
                        notice(Config.IC_TO_WORK, "司机卡上班[" + response.getTac() + "]", true);
                        saveRecord(response);
                        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
                    } else {
                        BusToast.showToast(BusApp.getInstance(), "签到失败[" + response.getStatus() + "|" + response.getTransType() + "]", false);
                    }
                } else {
                    //如果不是普通员工签到卡,则不做任何提示
                    SLog.d("LoopCardThread(run.java:77)不是普通员工签到卡>>>则不做任何提示");
                }

                if (TextUtils.equals(searchCard.cardType, "40")) {
                    elseCardControl(searchCard, false);
                }

            } else {
                //判断,线路是否存在
                if (checkLine()) {
                    return;
                }
                if (TextUtils.equals(searchCard.cardType, "40")) {//如果是40设置卡 则进行下班操作
                    offWork(null);
                    return;
                }
                Log.i("time", "checkLine");

                // 白名单校验
                if (DBManager.checkedWhiteList(searchCard) && searchCard.cardModuleType.equals("A0") && !searchCard.company.startsWith(BusApp.getPosManager().getOrganization())) {
                    notice(Config.IC_ERROR, "不在白名单中[5469]", false);
                    lastTime = SystemClock.elapsedRealtime();
                    return;
                }

                Log.i("刷卡", " 卡消费检测  ");

                //06员工卡要做特殊处理,此时员工卡要么是下班要么是正常消费
                //此员工卡卡号等于当前司机卡号时>>下班，否则正常消费
                if (TextUtils.equals(searchCard.cardType, "06")) {
                    String empNo = BusApp.getPosManager().getEmpNo();
                    if (!TextUtils.equals(searchCard.cityCode + searchCard.cardNo, empNo)) {
                        int normalAmount = payFee(CARD_NORMAL);
                        int currentAmount = payFee(searchCard.cardType);
                        if (currentAmount < normalAmount
                                &&
                                DBManager.filterBrush(searchCard.cityCode + searchCard.cardNo, searchCard.cardType)) {
                            BusToast.showToast(BusApp.getInstance(), "您已刷过[" + searchCard.cardType + "]", false);
                            lastTime = SystemClock.elapsedRealtime();
                            return;
                        }
                    }
                }

                Log.i("刷卡", " 准备消费  ");
                //已签到
                elseCardControl(searchCard, true);
            }

            cardNoTemp = searchCard.cardNo;
            lastTime = SystemClock.elapsedRealtime();
            searchCard = null;
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("LoopCardThread(run.java:60)LoopCardThread出现异常>>>" + e.toString());
        }
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
    private void elseCardControl(SearchCard searchCard, boolean isWrok) {

        if (TextUtils.equals(searchCard.cardModuleType, "F0")) {
            if (!BusApp.isConnection(BusApp.getInstance())) {
                notice(Config.IC_ERROR, "错误[2709]", false);
                return;
            }

            Log.i("刷卡", " 银联卡消费  ");
            //银联卡
            UnionCard.getInstance().run(searchCard.cityCode + searchCard.cardNo);

        } else {
            int pay_fee = payFee(searchCard);
            ConsumeCard response = response(pay_fee, isBlack, isWhite, isWrok, false);

            Log.i("刷卡", " 消费完成  解析数据  ");

            String status = response.getStatus();
            String cardModuleType = response.getCardModuleType();
            String balance = response.getCardBalance();
            if (response.getTransType().equals("16")) {//设置卡设置线路
                BusToast.showToast(BusApp.getInstance(), "线路正在更新", true);
                String fileName = HexUtil.fileName(response.getTransNo().substring(2, 6));
                List<BaseRequest> taskList = AppUtil.getDownloadAppointFileList(fileName, BusApp.getPosManager().getBusNo());
                AppUtil.run(taskList, null);
                return;
            }
            if (TextUtils.equals(status, "00")) {
                switch (cardModuleType) {
                    case "20"://一卡通CPU
                        CPU(response);
                        break;
                    case "08"://本地Mi
                        MI(response);
                        break;
                }
            } else if (TextUtils.equals(status, "02")) {//交通部

                if (response.getCardType().equals("01")) {//普通卡
                    checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                } else if (response.getCardType().equals("02")) {//学生卡
                    checkTheBalance(response, Config.IC_STUDENT);
                } else if (response.getCardType().equals("03")) {//老人卡
                    checkTheBalance(response, Config.LAORENKA_T);
                } else if (response.getCardType().equals("04")) {//关爱卡
                    checkTheBalance(response, Config.GUANAIKA_T);
                } else if (response.getCardType().equals("05")) {//军人卡
                    checkTheBalance(response, Config.JUNRENKA_T);
                } else {
                    checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                }
            } else if (TextUtils.equals(status, "10")) {
                //淄博公交，语音提示“无偿献血卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_BLOOD);

            } else if (TextUtils.equals(status, "11")) {
                //淄博公交，语音提示“爱心卡”
                zeroDis(response);
                checkTheBalance(response, Config.IC_YEARLY);

            } else if (TextUtils.equals(status, "12")) {//请年检
                checkTheBalance(response, Config.IC_LOVE);

            } else if (status.equalsIgnoreCase("F1")) {
                //卡片未启用
                notice(Config.IC_PUSH_MONEY, "卡片未启用[F1]", false);
            } else if (status.equalsIgnoreCase("F2")) {
                //卡片过期
                notice(Config.IC_PUSH_MONEY, "卡片过期[F2]", false);
                DateUtil.setK21Time();

            } else if (status.equalsIgnoreCase("F3")) {
                //卡内余额不足
                notice(Config.IC_PUSH_MONEY, "卡内余额不足[F3]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F4")) {
                //此卡为黑名单卡(已经锁了)
                notice(Config.IC_PUSH_MONEY, "黑名单卡[F4]", false);
            } else if (status.equalsIgnoreCase("F5")) {
                //不是本系统卡
                notice(Config.IC_PUSH_MONEY, "不是本系统卡[F5]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("F6")) {
                //月票卡不能乘坐本线路
                notice(Config.IC_PUSH_MONEY, "月票卡不能乘坐本线路[F6]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equalsIgnoreCase("FE")) {
//                    || status.equalsIgnoreCase("FF")
                //消费异常(重新刷卡)
                notice(Config.IC_PUSH_MONEY, "重新刷卡[" + status + "]", false);
                this.searchCard.cardNo = "0";
            } else if (status.equals("A0")) {
                Log.i("time 消费处理完毕", "提示==" + response.getCardType() + response.getTransType());
                tranErrAll(response.getCardType() + response.getTransType());
            }

            Log.i("刷卡", " 消费完成  保存记录  ");
            saveRecord(response);
        }

    }


    //M1卡处理
    public ConsumeCard MI(ConsumeCard response) {
        String balance = response.getCardBalance();
        String cardType = response.getCardType();
        switch (cardType) {
            case "41"://普通卡和CPU福利卡
                checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                break;
            case "02"://学生卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_STUDENT);
                break;
            case "03"://老年卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_OLD);
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

            case "45"://残疾人卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_DIS);
                break;
            case "46"://员工卡
                if (TextUtils.equals(response.getTransType(), "13")) {
                    //下班
                    offWork(response);
                } else {
                    //员工卡正常消费
                    zeroDis(response);
                    checkTheBalance(response, Config.IC_EMP);
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
        return response;

    }

    //CPU卡处理
    public ConsumeCard CPU(ConsumeCard response) {
        String balance = response.getCardBalance();
        String cardType = response.getCardType();
        switch (cardType) {
            case "41"://普通卡和CPU福利卡
            case "04"://济南卡
                checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_BASE : Config.IC_RECHARGE);
                break;
            case "42"://学生卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_STUDENT);
                break;
            case "43"://老年卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_OLD);
                break;
            case "44"://免费卡
                if (TextUtils.equals(response.getTransType(), "06")) {
                    //免费卡交易类型为06时判断余额是否小于5元
                    checkTheBalance(response, hex2Int(balance) > 500 ? Config.IC_FREE : Config.IC_RECHARGE);
                } else {
                    zeroDis(response);
                    checkTheBalance(response, Config.IC_FREE);
                }
                break;
            case "45"://残疾人卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_DIS);
                break;
            case "88"://特殊卡
                zeroDis(response);
                checkTheBalance(response, Config.IC_BASE);
                break;
            case "46"://员工卡
                if (TextUtils.equals(response.getTransType(), "13")) {
                    //下班
                    offWork(response);
                } else {
                    //员工卡正常消费
                    zeroDis(response);
                    checkTheBalance(response, Config.IC_EMP);
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

        return response;
    }

    //交通部卡处理
    public void tranErrAll(String string) {
        Log.i("time", "tranErrAll");
        switch (string) {
//            case "FFFF": //不做提示
//                return;
            case "FF00"://读取时间失败
                notice(Config.IC_ERROR, "读取时间失败[" + string + "]", false);
                return;
            case "2698"://请投币
            case "2699"://请投币
            case "2701"://请投币
            case "2702"://请投币
            case "2703"://请投币
            case "2705"://请投币
            case "2706"://请投币
            case "2707"://请投币
                notice(Config.IC_PUSH_MONEY, "请投币[" + string + "]", false);
                return;
            case "2708"://
                notice(Config.IC_ERROR, "错误[" + string + "]", false);
                return;
            case "2712"://卡片未启用
                notice(Config.IC_INVALID, "卡片未启用[" + string + "]", false);
                return;
            case "2718"://
                notice(Config.IC_ERROR, "错误[" + string + "]", false);
                return;
            case "2731"://余额不足
                notice(Config.EC_BALANCE, "余额不足[" + string + "]", false);
                return;
            case "2735"://余额过大
                notice(Config.EC_FEE, "余额过大[" + string + "]", false);
                return;
            case "2771"://秘钥版本不对
                notice(Config.IC_ERROR, "秘钥版本不对[" + string + "]", false);
                return;
            case "2459"://卡片未启用
                notice(Config.IC_ERROR, "卡片未启用[" + string + "]", false);
                return;
            case "2460"://卡片过期
                notice(Config.IC_INVALID, "卡片过期[" + string + "]", false);
                return;
            case "2461"://日期格式错误
                notice(Config.IC_ERROR, "日期格式错误[" + string + "]", false);
                return;
            case "2463"://终端使用不支持01算法的psam
                notice(Config.IC_ERROR, "终端使用不支持00算法的psam[" + string + "]", false);
                return;
            case "5488"://终端使用不支持00算法的psam
                notice(Config.IC_ERROR, "终端使用不支持00算法的psam[" + string + "]", false);
                return;
            case "5489"://终端使用不支持01算法的psam
                notice(Config.IC_ERROR, "终端使用不支持00算法的psam[" + string + "]", false);
                return;
            case "2473"://无效卡
                notice(Config.WU_XIAO_KA, "无效卡[" + string + "]", false);
                return;

        }


    }

    /**
     * @param response 0金额处理
     *                 扣款0元,但是底层返回却是应付金额
     */
    private void zeroDis(ConsumeCard response) {
        if (TextUtils.equals(response.getTransType(), "07")) {
            response.setPayFee("0");
        }
    }

    /**
     * @param cardType 城市当地卡卡类型
     * @return .
     */
    private int payFee(String cardType) {
        String[] coefficent = BusApp.getPosManager().getCoefficent();
        int basePrices = BusApp.getPosManager().getBasePrice();
        try {
            switch (cardType) {
                case CARD_NORMAL://普通卡
                    return string2Int(coefficent[0]) * basePrices / 100;
                case CardTypeGJ.CARD_STUDENT://学生卡
                    return string2Int(coefficent[1]) * basePrices / 100;
                case CardTypeGJ.CARD_OLD://老年卡
                    return string2Int(coefficent[2]) * basePrices / 100;
                case CardTypeGJ.CARD_FREE://CPU免费卡
                    return string2Int(coefficent[3]) * basePrices / 100;
                case CardTypeGJ.CARD_MEMORY://残疾人卡
                    return string2Int(coefficent[4]) * basePrices / 100;
                case CardTypeGJ.CARD_YUANGONG://员工卡
                    return string2Int(coefficent[5]) * basePrices / 100;
                case CardTypeGJ.CARD_JINAN://济南
                    return string2Int(coefficent[9]) * basePrices / 100;
                default:
                    return string2Int(coefficent[0]) * basePrices / 100;
            }
        } catch (Exception e) {
            BusToast.showToast(BusApp.getInstance(), "折扣获取失败", false);
            return string2Int(coefficent[0]) * basePrices / 100;
        }
    }

    /**
     * @param
     * @return .
     */
    private int payFee(SearchCard searchCard) {
        switch (searchCard.cardModuleType) {
            case "A0"://交通部刷卡金额  目前全部以普通卡价格为标准
                String[] coefficent = BusApp.getPosManager().getCoefficent();
                int basePrices = BusApp.getPosManager().getBasePrice();
                return string2Int(coefficent[0]) * basePrices / 100;
            default://其他卡
                return payFee(searchCard.cardType);
        }
    }


    /**
     * @param cardType       卡类型
     * @param cardModuleType 卡物理类型
     * @return .
     */
    private int payFee(String cardType, String cardModuleType) {
        String[] coefficent = BusApp.getPosManager().getCoefficent();
        int basePrices = BusApp.getPosManager().getBasePrice();

        if (TextUtils.equals(cardType, "04")) {
            //有可能是M1免费卡或者CPU济南卡
            if (TextUtils.equals(cardModuleType, "08")) {
                //M1
                //免费卡
                return string2Int(coefficent[3]) * basePrices / 100;
            } else {
                //CPU
                //济南卡
                return string2Int(coefficent[9]) * basePrices / 100;
            }

        }
        switch (cardType) {
            case CARD_NORMAL://普通卡
                return string2Int(coefficent[0]) * basePrices / 100;
            case CardTypeGJ.CARD_STUDENT://学生卡
                return string2Int(coefficent[1]) * basePrices / 100;
            case CardTypeGJ.CARD_OLD://老年卡
                return string2Int(coefficent[2]) * basePrices / 100;
            case CardTypeGJ.CARD_FREE://CPU免费卡
                return string2Int(coefficent[3]) * basePrices / 100;
            case CardTypeGJ.CARD_MEMORY://残疾人卡
                return string2Int(coefficent[4]) * basePrices / 100;
            case CardTypeGJ.CARD_YUANGONG://员工卡
                return string2Int(coefficent[5]) * basePrices / 100;
            default:
                return string2Int(coefficent[0]) * basePrices / 100;
        }
    }


    /**
     * 消费报文
     *
     * @param pay_fee 实际扣款
     * @param isBlack 是否是黑名单：0x01黑名单锁卡
     * @param isWhite 是否是白名单,预留
     * @return 消费响应
     */
    private ConsumeCard response(int pay_fee, boolean isBlack, boolean isWhite,
                                 boolean workStatus, boolean isSign) {
        int total_fee = BusApp.getPosManager().getBasePrice();
        byte[] data = new byte[128];
        byte[] amount = HexUtil.int2Bytes(pay_fee, 3);//消费金额 正常消费金额
        byte[] baseAmount = HexUtil.int2Bytes(total_fee, 3);//基础票价
        byte[] black = new byte[]{(byte) (isBlack ? 0x01 : 0x00)};//是否黑名单
        byte[] white = new byte[]{0x01};//是否白名单
        byte[] busNo = HexUtil.str2Bcd(BusApp.getPosManager().getBusNo());//车号
        byte[] lineNo = HexUtil.hex2byte(BusApp.getPosManager().getLineNo());//线路号
        byte[] workStatus_ = new byte[]{(byte) (workStatus ? 0x01 : 0x00)};//上下班状态
        byte[] driverNo = HexUtil.str2Bcd(BusApp.getPosManager().getDriverNo());//司机号
        byte[] direction = new byte[]{0x00};//方向
        byte[] stationId = new byte[]{0x01};//站点
        String[] coefficent = BusApp.getPosManager().getCoefficent();
        SLog.d("金额:" + string2Int(coefficent[0]) * total_fee / 100);
        byte[] singlePrice = HexUtil.int2Bytes(string2Int(coefficent[0]) * total_fee / 100, 3);// 单票制普通卡金额
        byte[] sendData = HexUtil.mergeByte(amount, baseAmount, black, white, busNo, lineNo,
                workStatus_, driverNo, direction, stationId, singlePrice, data);

        SLog.d("LoopCardThread(response.java:279)发送的报文:" + HexUtil.printHexBinary(sendData));

        int ret = libszxb.qxcardprocess(sendData);
        return new ConsumeCard(sendData, isSign, "zibo", searchCard.cardModuleType);
    }


    /**
     * @param response 下班
     */
    private void offWork(ConsumeCard response) {
        BusApp.getPosManager().setDriverNo(String.format("%08d", 0), String.format("%08d", 0));
        notice(Config.IC_OFF_WORK, "司机卡下班[00]", true);
        if (response != null) {
            saveRecord(response);
        }
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.SIGN));
    }


    /**
     * 检查余额并做提示
     *
     * @param response .
     */
    private void checkTheBalance(ConsumeCard response, int music) {
        notice(music, "本次扣款:"
                + fen2Yuan(string2Int(response.getPayFee())) + "元\n余额:"
                + fen2Yuan(string2Int(response.getCardBalance())) + "元", true);


        saveRecord(response);
    }

    /**
     * @param music  .
     * @param tipVar .
     * @param isOk   .
     */
    private void notice(int music, String tipVar, boolean isOk) {
        SoundPoolUtil.play(music);
        BusToast.showToast(BusApp.getInstance(), tipVar, isOk);
    }

    /**
     * 保存交易记录
     *
     * @param consumeCard .
     */
    private void saveRecord(ConsumeCard consumeCard) {
        ThreadFactory.getScheduledPool().execute(new WorkThread("zibo", consumeCard));
        SLog.d("LoopCardThread(saveRecord.java:345)保存成功");
    }

}

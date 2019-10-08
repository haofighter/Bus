package com.szxb.buspay.task.key;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.interfaces.OnKeyListener;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.CountTime;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.tip.MainLooper;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;

import java.util.concurrent.TimeUnit;

import static com.szxb.buspay.util.HexUtil.printHexBinary;
import static com.szxb.buspay.util.HexUtil.sendBackToKeyBoard;
import static com.szxb.buspay.util.Util.checkEnterKey;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者: Tangren on 2017-12-08
 * 包名：szxb.com.commonbus.task.scan
 * 邮箱：996489865@qq.com
 * TODO:按键/票价键盘监听
 */

public class LoopKeyTask {

    private long lastTime = 0;
    private OnKeyListener listener;

    private volatile static LoopKeyTask instance = null;

    private LoopKeyTask() {
    }

    public static LoopKeyTask getInstance() {
        if (instance == null) {
            synchronized (LoopKeyTask.class) {
                if (instance == null) {
                    instance = new LoopKeyTask();
                }
            }
        }
        return instance;
    }

    public void startLoopKey() {
        libszxb.deviceSerialSetBaudrate(3, 115200);
        ThreadFactory.getScheduledPool().executeCycle(new Runnable() {
            @Override
            public void run() {
                try {
                    if (BusApp.getPosManager().isSuppKeyBoard()) {
                        keyBord();
                    }
                    enterKey();
                } catch (Exception e) {
                    e.printStackTrace();
                    SLog.d("LoopKeyTask(run.java:58)按键出现异常>>" + e.toString());
                }
            }
        }, 500, 200, "key", TimeUnit.MILLISECONDS);
    }


    private String tempPrices;
    private byte[] recv = new byte[512];

    private void keyBord() {
        int i = libszxb.deviceSerialRecv(3, recv, 50);
        String keycode = HexUtil.printHexBinary(recv).substring(12, 14);

        if (TextUtils.isEmpty(keycode) ||
                TextUtils.equals(keycode, "00")) {
            return;
        }


        switch (keycode) {
            case "2E"://.
            case "23"://#
            case "18"://退格
                break;
            case "0D"://归位键
                tempPrices = null;
                sendBackToKeyBoard("\b\b\b");
                break;
            case "2A":
                if (!TextUtils.isEmpty(tempPrices)) {
                    //当前票价的半价
                    SLog.d("LoopKeyTask(keyBord.java:106)接收到半价通知>>");
                    int basePrices = BusApp.getPosManager().getBasePrice();
                    int halfPrices = basePrices / 2;
                    if (halfPrices > 450) {
                        BusToast.showToast(BusApp.getInstance(), "按键金额超出限制\n请重试[" + halfPrices + "]", false);
                        return;
                    }
                    BusApp.getPosManager().setBasePrice(halfPrices);
                    String[] coefficient = BusApp.getPosManager().getCoefficent();
                    BusApp.getPosManager().setWcPrice(string2Int(coefficient[8]) * halfPrices / 100);
                    BusApp.getPosManager().setUnionPayPrice(string2Int(coefficient[9]) * halfPrices / 100);
                    BusApp.getPosManager().setHalfPrices(true);
                    RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.KEY_CODE));

                    SLog.d("LoopKeyTask(keyBord.java:116)微信：" + BusApp.getPosManager().getWcPayPrice()
                            + ",基础金额=" + BusApp.getPosManager().getBasePrice()
                            + "微信折扣：" + BusApp.getPosManager().getCoefficent()[8]
                            + "银联折扣:" + BusApp.getPosManager().getCoefficent()[9]);
                    tempPrices = null;
                }
                break;
            case "30":
            case "31":
            case "32":
            case "33":
            case "34":
            case "35":
            case "36":
            case "37":
            case "38":
            case "39":
                if (BusApp.getPosManager().getLineInfoEntity() == null) {
                    BusToast.showToast(BusApp.getInstance(), "请先配置线路信息", false);
                    return;
                }
                String s = HexUtil.convertHexToString(keycode);
                SLog.d("LoopKeyTask(keyBord.java:122)接收到键盘命令>>开始更新票价>>票价=" + s);
                int code = Util.string2Int(s);
                if (code * 100 > 900) {
                    BusToast.showToast(BusApp.getInstance(), "按键金额超出限制\n请重试[" + s + "]", false);
                    return;
                }
                int basePrice = code * 100;
                BusApp.getPosManager().setBasePrice(basePrice);
                String[] coefficient = BusApp.getPosManager().getCoefficent();
                BusApp.getPosManager().setWcPrice(string2Int(coefficient[8]) * code);
                BusApp.getPosManager().setUnionPayPrice(string2Int(coefficient[9]) * code);
                BusApp.getPosManager().setHalfPrices(false);
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.KEY_CODE));

                SLog.d("LoopKeyTask(keyBord.java:138)"
                        + "微信：" + BusApp.getPosManager().getWcPayPrice()
                        + "银联：" + BusApp.getPosManager().getUnionPayPrice()
                        + ",基础金额=" + BusApp.getPosManager().getBasePrice()
                        + ",微信折扣：" + BusApp.getPosManager().getCoefficent()[8]
                        + ",银联折扣:" + BusApp.getPosManager().getCoefficent()[9]);
                tempPrices = s;
                break;
            default:
                break;
        }
    }

    private void enterKey() {
        byte[] b = new byte[5];
        int device = libszxb.devicekey(b);
        if (device == -2) {
            libszxb.deviceReset();
        }

        final String resultCode = printHexBinary(b);
        if (!resultCode.equals("0000000000")) {
            MainLooper.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (resultCode) {
                        case "0100000000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键1");
                                //上
                                listener.onKeyUp();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        case "0001000000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键2");
                                //确定
                                listener.onKeyOk();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        case "0000010000":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:66)按键3");
                                //取消
                                listener.onKeyCancel();
                                CountTime.getInstance().stopCnt();
                            }
                            break;
                        case "0000000100":
                            if (filterRE()) return;
                            if (listener != null) {
                                Log.d("LoopKeyTask",
                                        "enterKey(LoopKeyTask.java:60)按键4");
                                //下
                                listener.onKeyDown();
                                CountTime.getInstance().startCnt();
                            }
                            break;
                        default:
                            break;
                    }
                    lastTime = SystemClock.elapsedRealtime();
                }
            });

        }
    }

    //取消
    public void cancel() {

    }

    private boolean filterRE() {
        return !checkEnterKey(SystemClock.elapsedRealtime(), lastTime);
    }

    public void setOnKeyListener(OnKeyListener listener) {
        this.listener = listener;
    }
}

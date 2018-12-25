package com.szxb.buspay.task.scan;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.dispose.BankQRParse;
import com.szxb.unionpay.dispose.BankResponse;

import static com.szxb.buspay.util.Util.checkQR;
import static com.szxb.buspay.util.Util.checkQRMy;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.task.scan
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class LoopScanThread extends Thread {
    //临时变量存储上次刷卡记录,为了防止重复刷卡
    private String tem = "0";
    //每次扫码后的时间
    private long lastTime = 0;

    private BankResponse response = new BankResponse();

    @Override
    public void run() {
        super.run();
        try {
            byte[] recs = new byte[1024];
            int barcode = libszxb.getBarcode(recs);
            Log.i("扫码", "状态：" + barcode);
            if (barcode > 0) {
                String result = new String(recs, 0, barcode);
                Log.i("扫码", result);
                if (PosScanManager.isTenQRcode(result)) {
                    if (!BusApp.getPosManager().isSuppScanPay()) {
                        //本线路不支持扫码
                        BusToast.showToast(BusApp.getInstance(), "本线路暂不支持扫码乘车", false);
                        return;
                    }
                    if (filter(result)) {
                        return;
                    }

                    PosScanManager.getInstance().txposScan(result);
                } else if (PosScanManager.isMyQRcode(result)) {
                    if (!checkQRMy(SystemClock.elapsedRealtime(), lastTime)) {
                        return;
                    }
                    PosScanManager.getInstance().xbposScan(result);
                } else if (Util.isAllNum(result)) {

                    if (!BusApp.getPosManager().isSuppUnionPay()) {
                        BusToast.showToast(BusApp.getInstance(), "本线路暂不支持银联二维码乘车", false);
                        return;
                    }
                    if (filter(result)) {
                        return;
                    }

                    BankQRParse qrParse = new BankQRParse();
                    response = qrParse.parseResponse(BusApp.getPosManager().getUnionScanPrice(), result);

                    if (response.getResCode() > 0) {
                        BusToast.showToast(BusApp.getInstance(), response.getMsg(), true);
                    } else {
                        BusToast.showToast(BusApp.getInstance(), response.getMsg() + "[" + response.getResCode() + "]", false);
                    }
                } else {
//                    BusToast.showToast(BusApp.getInstance(), "二维码有误", false);
                }
                tem = result;
                lastTime = SystemClock.elapsedRealtime();
            }
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("LoopScanThread(run.java:58)" + e.toString());
        }
    }

    private boolean filter(String result) {
        return filterCheck(result) || checkLine();
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
        String driverNo = BusApp.getPosManager().getDriverNo();
        return TextUtils.equals(driverNo, String.format("%08d", 0));
    }


    private boolean filterCheck(String result) {
        if (!checkQR(SystemClock.elapsedRealtime(), lastTime)) return true;
        if (TextUtils.equals(result, tem)) {
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_QR_CODE));
            lastTime = SystemClock.elapsedRealtime();
            return true;
        }
        return false;
    }
}

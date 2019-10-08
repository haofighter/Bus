package com.szxb.buspay.util;

import android.os.CountDownTimer;

import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.MainLooper;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class CountTime {

    private volatile static CountTime instance = null;
    private volatile static long cnt = 0;

    private CountTime() {
    }

    public static CountTime getInstance() {
        if (instance == null) {
            synchronized (CountTime.class) {
                if (instance == null) {
                    instance = new CountTime();
                }
            }
        }
        return instance;
    }

    /**
     * 启动计时
     */
    public void startCnt() {
        if (cnt > 6000) {
            return;
        }
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timeCount != null) {
                    timeCount.cancel();
                    timeCount = null;
                    canceled = true;
                }
                if (canceled) {
                    timeCount = new TimeTask(10000, 1000);
                    timeCount.start();
                    canceled = false;
                }
            }
        });
    }

    /**
     * 停止计时
     */
    public void stopCnt() {
        cnt = 0;
        if (canceled) {
            return;
        }
        if (timeCount != null) {
            timeCount.cancel();
        }
        canceled = true;
        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.STOP_CNT));
    }

    private TimeTask timeCount;
    public boolean canceled = true;

    private class TimeTask extends CountDownTimer {

        public TimeTask(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            cnt = l;
        }

        @Override
        public void onFinish() {
            stopCnt();
        }
    }


}

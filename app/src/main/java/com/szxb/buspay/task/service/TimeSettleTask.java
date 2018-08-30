package com.szxb.buspay.task.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.task.thread.ThreadScheduledExecutorUtil;

import java.util.concurrent.TimeUnit;


/**
 * 作者: Tangren on 2017/8/16
 * 包名：szxb.com.commonbus.task
 * 邮箱：996489865@qq.com
 * TODO:定时处理未按时结算的订单
 */

public class TimeSettleTask extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TimeSettleTask() {
        super("TimeSettleTask");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (BusApp.getPosManager().isSuppScanPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("scan"), 30, 60, TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppIcPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("ic"), 30, 140, TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppUnionPay()) {
            ThreadScheduledExecutorUtil.getInstance().getService().scheduleAtFixedRate(new RecordThread("union"), 30, 160, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

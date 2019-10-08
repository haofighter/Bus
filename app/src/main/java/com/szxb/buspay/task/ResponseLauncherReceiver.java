package com.szxb.buspay.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.rx.RxBus;

/**
 * 作者：Tangren on 2018-08-01
 * 包名：com.szxb.buspay.task
 * 邮箱：996489865@qq.com
 * TODO:接收launcher发送的数据
 */

public class ResponseLauncherReceiver extends BroadcastReceiver {
    private static final String ACTION = "com.szxb.launcher.receiverbus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(ACTION, intent.getAction())) {
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.RES_LAUNCHER));
        }
    }
}

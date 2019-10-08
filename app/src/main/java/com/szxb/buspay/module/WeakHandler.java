package com.szxb.buspay.module;

import android.os.Handler;
import android.os.Message;

import com.szxb.buspay.interfaces.OnReceiverMessageListener;

import java.lang.ref.WeakReference;


/**
 * 作者：Tangren on 2018-04-06
 * 包名：com.szxb.jcbus.message
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public final class WeakHandler {

    private WeakHandler() {
    }

    public static class MyHandler extends Handler {

        WeakReference<OnReceiverMessageListener> listenerWeakReference;

        public MyHandler(OnReceiverMessageListener listener) {
            listenerWeakReference = new WeakReference<OnReceiverMessageListener>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listenerWeakReference != null && listenerWeakReference.get() != null) {
                listenerWeakReference.get().handlerMessage(msg);
            }
        }
    }


}

package com.szxb.buspay.interfaces;

import android.os.Message;

/**
 * 作者：Tangren on 2018-04-06
 * 包名：com.szxb.jcbus.interfaces
 * 邮箱：996489865@qq.com
 * TODO:消息回调接口
 */

public interface OnReceiverMessageListener {
    void handlerMessage(Message message);
}

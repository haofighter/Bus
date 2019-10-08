package com.szxb.buspay.util.rx;

import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.mlog.SLog;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 作者: Tangren on 2017/7/31
 * 包名：com.szxb.utils.rx
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class RxBus {

    private static volatile RxBus instance;
    private final FlowableProcessor<Object> bus;

    private RxBus() {
        bus = PublishProcessor.create().toSerialized();
        SLog.d("RxBus(RxBus.java:24)RxBus是否订阅成功>>" + bus.hasSubscribers());
    }

    /**
     * 单例RxBus
     *
     * @return RxBus
     */
    public static RxBus getInstance() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    /**
     * 发送一个新事件(失败了继续发送)
     *
     * @param o
     */
    public void send(QRScanMessage o) {
        bus.onNext(o);
    }

    /**
     * 发送一个新事件(仅仅发送一次)
     *
     * @param o
     */
    public void sendSingle(QRScanMessage o) {
        bus.onNext(o);
    }

    /**
     * 返回特定类型的被观察者
     *
     * @param eventType eventType
     * @param <T>
     * @return
     */
    public <T> Flowable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    public Flowable<Object> toObservable() {
        return bus;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return bus.hasSubscribers();
    }

}

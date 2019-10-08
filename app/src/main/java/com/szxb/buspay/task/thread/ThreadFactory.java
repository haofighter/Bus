package com.szxb.buspay.task.thread;

/**
 * 作者：Tangren on 2018-09-05
 * 包名：com.szxb.buspay.task.thread
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class ThreadFactory {

    private volatile static ThreadFactory instance = null;

    private static ScheduledThreadPool mScheduledPool;

    /**
     * 创建一个执行周期性任务的线程池
     */
    public static ScheduledThreadPool getScheduledPool() {
        if (mScheduledPool == null) {
            synchronized (ScheduledThreadPool.class) {
                if (mScheduledPool == null) {
                    mScheduledPool = new ScheduledThreadPool(20);
                }
            }
        }
        return mScheduledPool;
    }
}

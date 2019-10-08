package com.szxb.buspay.db.entity.bean;

import java.util.concurrent.TimeUnit;

/**
 * 作者：Tangren on 2018-09-10
 * 包名：com.szxb.buspay.db.entity.bean
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class ThreadConfig {
    private Runnable runnable;
    private int delay = 10;
    private int period = 10;
    private String tag = "TAG";
    TimeUnit unit = TimeUnit.SECONDS;

    public ThreadConfig(Runnable runnable, int delay, int period, String tag, TimeUnit unit) {
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
        this.tag = tag;
        this.unit = unit;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}

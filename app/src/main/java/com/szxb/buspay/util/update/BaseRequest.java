package com.szxb.buspay.util.update;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.mlog.SLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者：Tangren on 2018-08-21
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public abstract class BaseRequest {

    public static ResponseMessage response = new ResponseMessage();

    public Disposable getDisposable() {
        return getObservable().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseMessage>() {
                    @Override
                    public void accept(@NonNull ResponseMessage t) throws Exception {
                        doAccept(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        doThrowable(throwable);
                    }
                });
    }

    protected abstract void doSubscribe(ObservableEmitter<ResponseMessage> e);

    private void doAccept(ResponseMessage t) {
        if (onResponse != null) {
            onResponse.response(true, t);
        } else {
            SLog.e("BaseRequest(doAccept.java:49)response==null >>>>");
            BusToast.showToast(BusApp.getInstance().getApplicationContext(), response.getMsg(),
                    response.getStatus()
                            == ResponseMessage.SUCCESSFUL || response.getStatus()
                            == ResponseMessage.SUCCESS || response.getStatus()
                            == ResponseMessage.NOUPDATE);
        }
    }

    private void doThrowable(Throwable t) {
        if (onResponse != null) {
            response.setThrowable(t);
            onResponse.response(false, response);
        } else {
            SLog.e("BaseRequest(doThrowable.java:61)onResponse==null 异常>>>");
            BusToast.showToast(BusApp.getInstance(), "任务异常\n" + t.toString(), false);
        }
    }


    public Observable<ResponseMessage> getObservable() {
        return Observable.create(new ObservableOnSubscribe<ResponseMessage>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ResponseMessage> e) throws Exception {
                doSubscribe(e);
            }
        });
    }


    public OnResponse onResponse;

    public void setOnResponse(OnResponse onResponse) {
        this.onResponse = onResponse;
    }
}

package com.szxb.buspay.util.update;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.scan.MacKeyEntity;
import com.szxb.buspay.db.entity.scan.PublicKeyEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.param.ParamsUtil;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function4;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;

/**
 * 作者：Tangren on 2018-08-22
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DownloadScanRequest extends BaseRequest {

    @Override
    public Observable<ResponseMessage> getObservable() {
        return Observable.zip(macKey, publicKey, alipublicKey, blackList, new Function4<Boolean, Boolean, Boolean, Boolean, ResponseMessage>() {
            @Override
            public ResponseMessage apply(@NonNull Boolean aBoolean, @NonNull Boolean aBoolean2, @NonNull Boolean aBoolean3, @NonNull Boolean aBoolean4) throws Exception {
                response.setWhat(ResponseMessage.WHAT_SCAN);
                if (aBoolean && aBoolean2 && aBoolean3) {
                    response.setMsg("微信参数更新成功");
                    response.setStatus(ResponseMessage.SUCCESSFUL);
                    if (aBoolean4) {
                        response.setMsg("微信参数更新成功,支付宝参数获取失败");
                    }
                } else if (aBoolean || aBoolean2 || aBoolean3) {
                    response.setMsg("微信参数部分更新成功macKey=" + aBoolean + ">>publicKey=" + aBoolean2 + ">>>blackList=" + aBoolean3);
                    response.setStatus(ResponseMessage.SUCCESS);
                }

                return response;
            }
        });
    }

    @Override
    protected void doSubscribe(ObservableEmitter<ResponseMessage> e) {

    }


    private Observable<Boolean> macKey = Observable.create(new ObservableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
            JsonRequest macRequest = new JsonRequest(Config.MAC_KEY);
            Log.i("下载微信", "请求" + ParamsUtil.getkeyMap().toString());
            macRequest.set(ParamsUtil.getkeyMap());
            Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(macRequest);
            if (execute.isSucceed()) {

                String macMsg = execute.get().getString("retmsg");
                if (!TextUtils.isEmpty(macMsg) && TextUtils.equals(macMsg, "success")) {
                    final JSONArray array = execute.get().getJSONArray("mackey_list");
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MacKeyEntity macKeyEntity = new MacKeyEntity();
                        macKeyEntity.setTime(DateUtil.getCurrentDate());
                        macKeyEntity.setKey_id(object.getString("key_id"));
                        macKeyEntity.setPubkey(object.getString("mackey"));
                        getDaoSession().insertOrReplace(macKeyEntity);
                        Log.i("下载的秘钥", array.toString());
                    }
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
            } else {
                subscriber.onNext(false);
            }
        }
    });

    private Observable<Boolean> publicKey = Observable.create(new ObservableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
            JsonRequest publicKeyRequest = new JsonRequest(Config.PUBLIC_KEY);
            publicKeyRequest.set(ParamsUtil.getkeyMap());
            Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(publicKeyRequest);
            if (execute.isSucceed()) {
                String pubMsg = execute.get().getString("retmsg");
                if (!TextUtils.isEmpty(pubMsg) && TextUtils.equals(pubMsg, "success")) {
                    JSONArray pKearney = execute.get().getJSONArray("pubkey_list");
                    for (int i = 0; i < pKearney.size(); i++) {
                        JSONObject object = pKearney.getJSONObject(i);
                        PublicKeyEntity entity = new PublicKeyEntity();
                        entity.setKey_id(object.getString("key_id"));
                        entity.setPubkey(object.getString("pubkey"));
                        getDaoSession().insertOrReplace(entity);
                    }
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
            } else {
                subscriber.onNext(false);
            }
        }
    });

    private Observable<Boolean> alipublicKey = Observable.create(new ObservableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
            Log.i("下载的公钥  支付宝", "下载");
            JsonRequest publicKeyRequest = new JsonRequest(Config.ALI_PUBLIC_KEY);
            Map<String, Object> map = new HashMap<>();
            map.put("merchant_id", BusApp.getPosManager().getAppId());
            Log.i("下载的公钥  支付宝", "请求");
            Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(publicKeyRequest);
            Log.i("下载的公钥  支付宝", "请求完成");
            if (execute.isSucceed()) {
                String pubMsg = execute.get().toString();
                Log.i("下载的公钥  支付宝", pubMsg);
                if (!TextUtils.isEmpty(pubMsg) && TextUtils.equals(pubMsg, "success")) {
                    JSONArray pKearney = execute.get().getJSONArray("pubkey_list");
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
            } else {
                Log.i("下载的公钥  支付宝", "请求完成" + execute.getException());
                subscriber.onNext(false);
            }
        }
    });

    private Observable<Boolean> blackList = Observable.create(new ObservableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
            JsonRequest blackListRequest = new JsonRequest(Config.BLACK_LIST);
            blackListRequest.set(ParamsUtil.getBlackListMap());
            Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(blackListRequest);
            if (execute.isSucceed()) {
                String bLMsg = execute.get().getString("retmsg");
                if (!TextUtils.isEmpty(bLMsg) && TextUtils.equals(bLMsg, "ok")) {
                    final JSONArray array = execute.get().getJSONArray("black_list");
                    if (array == null || array.isEmpty()) {
                        subscriber.onNext(true);
                        return;
                    }
                    DBManager.addBlackList(array);
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
            } else {
                subscriber.onNext(false);
            }
        }
    });

}

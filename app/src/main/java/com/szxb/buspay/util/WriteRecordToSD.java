package com.szxb.buspay.util;

import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * 作者：Tangren on 2018-01-18
 * 包名：com.czgj.task
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class WriteRecordToSD {

    private volatile static WriteRecordToSD instance = null;

    private WriteRecordToSD() {
    }

    public static WriteRecordToSD getInstance() {
        if (instance == null) {
            synchronized (WriteRecordToSD.class) {
                if (instance == null) {
                    instance = new WriteRecordToSD();
                }
            }
        }
        return instance;
    }

    public void writer(final int day, final String scanPath, final String cardPath, final String unionPath) {

        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> subscriber) throws Exception {
                boolean success = false;
                try {
                    List<ScanInfoEntity> scanRecord = DBManager.getScanRecordList(day);
                    List<ConsumeCard> cardRecord = DBManager.getConsumeCardList(day);
                    List<UnionPayEntity> unionPayEntityList = DBManager.getUnionPayList(day);

                    File scanFile = new File(scanPath);
                    File cardFile = new File(cardPath);
                    File unionFile = new File(unionPath);

                    if (!scanFile.exists()) {
                        boolean mkdir = scanFile.mkdirs();
                        if (!mkdir) {
                            subscriber.onNext(false);
                            return;
                        }
                    }

                    if (!cardFile.exists()) {
                        boolean mkdir = cardFile.mkdirs();
                        if (!mkdir) {
                            subscriber.onNext(false);
                            return;
                        }
                    }

                    if (!unionFile.exists()) {
                        boolean mkdir = unionFile.mkdirs();
                        if (!mkdir) {
                            subscriber.onNext(false);
                            return;
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < scanRecord.size(); i++) {
                        sb.append(scanRecord.get(i).getBiz_data_single())
                                .append("status=").append(scanRecord.get(i).getStatus());
                        sb.append("\r\n");
                    }
                    byte2File(sb.toString().getBytes(), scanPath, "scan" + "_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + "_" + BusApp.getPosManager().getBusNo() + ".txt");

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < cardRecord.size(); i++) {
                        stringBuilder.append(new Gson().toJson(cardRecord.get(i)));
                        stringBuilder.append("\r\n");
                    }
                    byte2File(stringBuilder.toString().getBytes(), cardPath, "card" + "_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + "_" + BusApp.getPosManager().getBusNo() + ".txt");

                    StringBuilder unionBuilder = new StringBuilder();
                    for (int i = 0; i < unionPayEntityList.size(); i++) {
                        unionBuilder.append(new Gson().toJson(unionPayEntityList.get(i)));
                        unionBuilder.append("\r\n");
                    }
                    byte2File(unionBuilder.toString().getBytes(), unionPath, "union" + "_" + DateUtil.getCurrentDate("yyyyMMddHHmmss") + "_" + BusApp.getPosManager().getBusNo() + ".txt");

                    success = true;


                } catch (Exception e) {
                    e.printStackTrace();
                    SLog.e("WriteRecordToSD(call.java:108)导出记录异常>>" + e.toString());
                    success = false;
                }
                subscriber.onNext(success);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            BusToast.showToast(BusApp.getInstance(), "导出成功", true);
                        } else {
                            BusToast.showToast(BusApp.getInstance(), "导出失败\n请检查SD卡", false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        SLog.e("WriteRecordToSD(call.java:133)导出失败>>" + throwable.toString());
                        BusToast.showToast(BusApp.getInstance(), "导出失败\n" + throwable.toString(), false);
                    }
                });
    }


    /**
     * 根据byte数组，生成文件
     *
     * @param bfile    文件数组
     * @param filePath 文件存放路径
     * @param fileName 文件名称
     */
    private void byte2File(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath, fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            bos.flush();
            fos.flush();
        } catch (Exception e) {
            SLog.e("WriteRecordToSD(byte2File.java:162)流异常>>" + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                SLog.e("WriteRecordToSD(byte2File.java:174)导出数据异常>>" + e.toString());
            }

        }
    }

}

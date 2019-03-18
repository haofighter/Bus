package com.szxb.unionpay.unionutil;

import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.UnionAidEntityDao;
import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.core.Iso8583MessageFactory;
import com.szxb.java8583.module.ParamDownload;
import com.szxb.java8583.module.SignIn;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.java8583.quickstart.SingletonFactory;
import com.szxb.java8583.quickstart.special.SpecialField62;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.UnionPay;
import com.szxb.unionpay.entity.UnionAidEntity;
import com.szxb.unionpay.entity.UnionPayEntity;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static com.szxb.java8583.util.EncodeUtil.hex2byte;
import static com.szxb.java8583.util.MacEcbUtils.bytesToHexString;
import static java.lang.System.arraycopy;

/**
 * 作者：Tangren on 2018-07-06
 * 包名：com.szxb.unionpay.unionutil
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class ParseUtil {

    public static void parseMackey(String value, boolean isTip) {
        byte[] field_60_data = hex2byte(value);
        int i = 0;
        byte[] pinKey = new byte[16];
        arraycopy(field_60_data, i, pinKey, 0, pinKey.length);

        byte[] pinKeyCrc = new byte[4];
        arraycopy(field_60_data, i += pinKey.length, pinKeyCrc, 0, pinKeyCrc.length);

        byte[] macKey = new byte[16];
        arraycopy(field_60_data, i += pinKeyCrc.length, macKey, 0, macKey.length);

        byte[] macKeyCrc = new byte[4];
        arraycopy(field_60_data, i + macKey.length, macKeyCrc, 0, macKeyCrc.length);

        String key = BusllPosManage.getPosManager().getKey();
        byte[] des = ThreeDes.Union3DesDecrypt(hex2byte(key), macKey);

        String macKeyHex = bytesToHexString(des);
        String Mac = macKeyHex.substring(0, 16);
        byte[] crc = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] crcdata = ThreeDes.encrypt(crc, Mac);

        if (crcdata != null) {
            byte[] macCrc = new byte[4];
            arraycopy(crcdata, 0, macCrc, 0, macCrc.length);
            if (Arrays.equals(macCrc, macKeyCrc)) {
                BusllPosManage.getPosManager().setMacKey(macKeyHex);
                if (isTip) {
                    BusToast.showToast(BusApp.getInstance(), "银联签到成功", true);
                }
                SLog.d("ParseUtil(parseMackey.java:74)银联Mac 保存成功");
            } else {
                BusToast.showToast(BusApp.getInstance(), "银联签到失败[NEQ]", false);
                SLog.d("ParseUtil(parseMackey.java:77)银联签到失败[NEQ]");
            }

            SLog.d("ParseUtil(parseMackey.java:80)商户号=" + BusllPosManage.getPosManager().getMchId() + ",终端号=" + BusllPosManage.getPosManager().getPosSn() + ",秘钥=" + BusllPosManage.getPosManager().getKey());
        }
    }

    /**
     * 保存tlv
     *
     * @param field_62
     */
    public static void setParmaInfo(String field_62) {
        byte[] posInfo = hex2byte(field_62);
        byte[] index_1 = new byte[1];
        arraycopy(posInfo, 0, index_1, 0, index_1.length);
        String state = bytesToHexString(index_1);
        Log.d("ParseUtil",
                "setParmaInfo(ParseUtil.java:75)" + state);
        if (state.equals("30")) {
            //
        } else if (state.equals("31")) {
            //31 9F06
            byte[] data = new byte[posInfo.length - 1];
            arraycopy(posInfo, 1, data, 0, data.length);

            StringBuilder sBuilder = new StringBuilder();
            int index = 0;
            for (int i = 0; i < data.length; i += 11) {
                byte[] rid = new byte[2];
                arraycopy(data, index, rid, 0, rid.length);

                byte[] len = new byte[1];
                arraycopy(data, index += rid.length, len, 0, len.length);

                byte[] value = new byte[8];
                arraycopy(data, index += len.length, value, 0, value.length);

                sBuilder.append(bytesToHexString(rid)).append(bytesToHexString(len)).append(bytesToHexString(value)).append(",");
                index += value.length;
            }
            SLog.d("ParseUtil(setParmaInfo.java:95)" + sBuilder.toString());
            BusllPosManage.getPosManager().setAidIndexList(sBuilder.toString());
        }

    }

    private static int aidCnt = 0;

    public static void initUnionPay() {
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<byte[]> subscriber) throws Exception {
                //1.签到
                BusllPosManage.getPosManager().setTradeSeq();
                Iso8583Message message = SignIn.getInstance().message(BusllPosManage.getPosManager().getTradeSeq());
                byte[] exe = UnionPay.getInstance().exeSyncSSL(message.getBytes());
                if (exe == null) {
                    SLog.d("InitZipActivity(call.java:83)签到失败>>");
                    BusToast.showToast(BusApp.getInstance(), "银联签到失败", true);
                } else {
                    Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                    factory.setSpecialFieldHandle(62, new SpecialField62());
                    Iso8583Message message0810 = factory.parse(exe);

                    if (message0810.getValue(39).getValue().equals("00")) {
                        String batchNum = message0810.getValue(60).getValue().substring(2, 8);
                        BusllPosManage.getPosManager().setBatchNum(batchNum);
                        parseMackey(message0810.getValue(62).getValue(), true);
                    }
                    SLog.d("InitZipActivity(call.java:89)签到\n" + message0810.toFormatString());
                }
                if (ParseUtil.isUpdateParams()) {
                    subscriber.onNext(exe);
                } else {
                    subscriber.isDisposed();
                }
            }
        }).flatMap(new Function<byte[], ObservableSource<byte[]>>() {
            @Override
            public ObservableSource<byte[]> apply(@NonNull byte[] bytes) throws Exception {
                //2.查询需要下载的参数
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().aidMessage().getBytes());
                return Observable.just(exe);
            }
        }).flatMap(new Function<byte[], ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull byte[] bytes) throws Exception {
                if (bytes == null) {
                    SLog.d("InitZipActivity(call.java:118)参数查询失败>>");
                } else {
                    SLog.d("InitZipActivity(call.java:120)查询参数成功,开始下载参数>>");
                    Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                    factory.setSpecialFieldHandle(62, new SpecialField62());
                    Iso8583Message message0810 = factory.parse(bytes);
                    if (message0810.getValue(39).getValue().equals("00")) {
                        ParseUtil.setParmaInfo(message0810.getValue(62).getValue());
                    }

                }
                String adiList = BusllPosManage.getPosManager().aidIndexList();
                SLog.d("InitZipActivity(call.java:125)indexList:" + adiList);
                String macs[] = adiList.split(",");
                aidCnt = macs.length;
                return Observable.fromArray(macs);
            }
        }).flatMap(new Function<String, ObservableSource<byte[]>>() {
            @Override
            public ObservableSource<byte[]> apply(@NonNull String s) throws Exception {
                //3.依次下载参数
                SLog.d("InitZipActivity(call.java:140)下载参数中>>");
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().messageAID(s).getBytes());
                return Observable.just(exe);
            }
        }).flatMap(new Function<byte[], ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(@NonNull byte[] bytes) throws Exception {
                Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                factory.setSpecialFieldHandle(62, new SpecialField62());
                Iso8583Message message0810 = factory.parse(bytes);
                if (message0810.getValue(39).getValue().equals("00")) {
                    ParseUtil.save_ic_params(message0810.getValue(62).getValue());
                }
                SLog.d("InitZipActivity(call.java:153)参数剩余" + aidCnt + "个\n" + message0810.toFormatString());
                aidCnt -= 1;
                return Observable.just(aidCnt);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(@NonNull Integer integer) throws Exception {
                return integer <= 0;
            }
        }).flatMap(new Function<Integer, ObservableSource<byte[]>>() {
            @Override
            public ObservableSource<byte[]> apply(@NonNull Integer integer) throws Exception {
                //4.下载参数结束
                byte[] exe = UnionPay.getInstance().exeSyncSSL(ParamDownload.getInstance().messageAIDEnd().getBytes());
                return Observable.just(exe);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(@NonNull byte[] bytes) throws Exception {
                        Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
                        factory.setSpecialFieldHandle(62, new SpecialField62());
                        Iso8583Message message0810 = factory.parse(bytes);
                        if (message0810.getValue(39).getValue().equals("00")) {
                            SLog.d("InitZipActivity(call.java:176)参数下载结束>>");
                            BusllPosManage.getPosManager().setCurrentUpdateTime(System.currentTimeMillis() / 1000);
                        }
                        SLog.d("InitZipActivity(call.java:178)" + message0810.toFormatString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        SLog.d("InitZipActivity(call.java:183)更新失败:" + throwable.toString());
                    }
                });
    }


    /**
     * 保存IC卡交易参数
     *
     * @param var aid
     */
    public static void save_ic_params(String var) {
        UnionAidEntityDao icParams_380Dao = DBCore.getDaoSession().getUnionAidEntityDao();
        UnionAidEntity params = new UnionAidEntity();
        params.setIcParam(var);
        icParams_380Dao.insertOrReplaceInTx(params);
    }

    /**
     * @return 是否需要更新藏
     */
    public static boolean isUpdateParams() {
        long last = BusllPosManage.getPosManager().getLastUpdateTime();
        long current = System.currentTimeMillis() / 1000;
        //2018,5,1
        return current >= 1525142850L && current - last > 7 * 86400;
    }

    public static List<UnionPayEntity> unUpList() {
        UnionPayEntityDao dao = DBCore.getDaoSession().getUnionPayEntityDao();
        return dao.queryBuilder().where(UnionPayEntityDao.Properties.UpStatus.eq("1"))
                .orderDesc(UnionPayEntityDao.Properties.Id)
                .limit(15).build().list();
    }

    public static void updateUnionUpState(String uniqueFlag) {
        UnionPayEntityDao dao = DBCore.getDaoSession().getUnionPayEntityDao();
        List<UnionPayEntity> list = dao.queryBuilder().where(UnionPayEntityDao.Properties.UniqueFlag.eq(uniqueFlag)).build().list();
        for (UnionPayEntity entity : list) {
            entity.setUpStatus(0);
            dao.update(entity);
            SLog.d("ParseUtil(updateUnionUpState.java:276)上传成功,状态修改成功");
        }
    }
}

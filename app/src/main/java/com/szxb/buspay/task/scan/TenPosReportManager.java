package com.szxb.buspay.task.scan;

import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.tencent.wlxsdk.WlxSdk;


/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.util.report
 * 邮箱：996489865@qq.com
 * TODO:腾讯
 */

public class TenPosReportManager {

    private static TenPosReportManager instance = null;
    private WlxSdk wxSdk;

    private TenPosReportManager() {
        wxSdk = new WlxSdk();
    }

    public static TenPosReportManager getInstance() {
        if (instance == null) {
            synchronized (TenPosReportManager.class) {
                if (instance == null) {
                    instance = new TenPosReportManager();
                }
            }
        }
        return instance;
    }

    public void posScan(String qrcode) {
        if (wxSdk == null) wxSdk = new WlxSdk();
        int init = wxSdk.init(qrcode);
        int key_id = wxSdk.get_key_id();
        String open_id = wxSdk.get_open_id();
        String mac_root_id = wxSdk.get_mac_root_id();
        int verify = 0;
        if (!TextUtils.isEmpty(open_id)) {
            if (DBManager.filterOpenID(open_id)) {
                BusToast.showToast(BusApp.getInstance(), "禁止频繁刷码", false);
            } else if (DBManager.filterSameQR(qrcode)) {
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_QR_CODE));
            } else if (DBManager.filterBlackName(open_id)) {
                //是黑名单里面的成员
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.QR_ERROR));
            } else {
                if (init == 0 && key_id > 0) {
                    //String open_id, String pub_key, int payfee, byte scene, byte scantype, String pos_id, String pos_trx_id, String aes_mac_root
                    verify = wxSdk.verify(open_id
                            , BusApp.getPosManager().getPublicKey(String.valueOf(key_id))
                            , BusApp.getPosManager().getWcPayPrice()//金额
                            , (byte) 1
                            , (byte) 1
                            , BusApp.getPosManager().getPosSN()
                            , Util.Random(8)
                            , BusApp.getPosManager().getMac(mac_root_id));

                    String record = wxSdk.get_record();

                    PosRecord posRecord = new PosRecord();
                    posRecord.setOpen_id(open_id);
                    posRecord.setQr_code(qrcode);
                    posRecord.setOrder_time(BusApp.getPosManager().getOrderTime());
                    posRecord.setTotal_fee(BusApp.getPosManager().getBasePrice());//金额，上线修改为posRecord.setTotal_fee(App.getPosManager().getMarkedPrice());

                    posRecord.setPay_fee(BusApp.getPosManager().getWcPayPrice());//实际扣款金额，上线修改为posRecord.setTotal_fee(App.getPosManager().getPayMarkPrice());

                    posRecord.setCity_code(BusApp.getPosManager().getCityCode());
                    posRecord.setOrder_desc(BusApp.getPosManager().getLineName());//线路名
                    posRecord.setRecord(record);
                    posRecord.setBus_no(BusApp.getPosManager().getBusNo());
                    posRecord.setUnitno(BusApp.getPosManager().getUnitno());
                    posRecord.setDriveno(BusApp.getPosManager().getDriverNo());

                    //line_name作为line_no处理
                    posRecord.setBus_line_name(BusApp.getPosManager().getLineNo());

                    posRecord.setPos_no(BusApp.getPosManager().getPosSN());
                    posRecord.setBus_line_no(BusApp.getPosManager().getLineNo());
                    posRecord.setIn_station_id(1);
                    posRecord.setIn_station_name(BusApp.getPosManager().getLineName());

                    PosRequest.getInstance().request(new QRScanMessage(posRecord, verify));
                } else {
                    SoundPoolUtil.play(Config.VERIFY_FAIL);
                    BusToast.showToast(BusApp.getInstance(), "验码失败\n[SDK初始化失败]", false);
                }

            }
        }
    }


}

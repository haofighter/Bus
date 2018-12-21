package com.szxb.buspay.task.scan;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.util.tip.BusToast;

/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.util.comm
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PosScanManager {

    private static PosScanManager instance = null;

    private PosScanManager() {
    }

    public static PosScanManager getInstance() {
        if (instance == null) {
            synchronized (PosScanManager.class) {
                if (instance == null) {
                    instance = new PosScanManager();
                }
            }
        }
        return instance;
    }


    public static boolean isMyQRcode(String qrcode) {
        return qrcode != null && qrcode.indexOf("szxb") == 0;
    }

    public static boolean isConfigQRcode(String qrcode) {
        return qrcode != null && qrcode.indexOf("szxbcfg") == 0;
    }

    public static boolean isTenQRcode(String qrcode) {
        return qrcode != null && qrcode.indexOf("TX") == 0;
    }

    //腾讯
    public void txposScan(String qrcode) {
        //当为配置参数时提示
        if (BusApp.getPosManager().getLineInfoEntity() == null) {
            BusToast.showToast(BusApp.getInstance(), "请先配置线路信息", false);
        } else {
            TenPosReportManager.getInstance().posScan(qrcode);
        }
    }

    //银联
    public void ylposScan(String qrcode) {
        //当为配置参数时提示
        if (BusApp.getPosManager().getLineInfoEntity() == null) {
            BusToast.showToast(BusApp.getInstance(), "请先配置线路信息", false);
        } else {
            TenPosReportManager.getInstance().posScan(qrcode);
        }
    }

    public void xbposScan(String qrcode) {
        int codeType;
        if (qrcode.indexOf("szxb_ftp") == 0) {
            //如果是ftp设置
            codeType = QRCode.CONFIG_CODE_FTP;
        } else if (qrcode.indexOf("szxb_mch") == 0) {
            //商户设置
            codeType = QRCode.CONFIG_CODE_MCH;
        } else if (qrcode.indexOf("szxb_ip") == 0) {
            //服务器IP设置
            codeType = QRCode.CONFIG_CODE_IP;
        } else if (qrcode.indexOf("szxb_union") == 0) {
            //银联参数
            codeType = QRCode.CONFIG_CODE_UNIONPAY;
        } else if (qrcode.indexOf("szxb_more") == 0) {
            //更多功能扫码
            codeType = QRCode.QR_MOREN;
        } else if (qrcode.indexOf("szxb_time") == 0) {
            //扫码设置时间
            codeType = QRCode.CONFIG_TIMER;
        } else {
            //线路设置
            codeType = QRCode.CONFIG_CODE_LINE;
        }
        XBPosReportManager.getInstance().posScan(codeType, qrcode);
    }

}

package com.szxb.buspay.task.scan;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.LINEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.scan.param.UnionPayParam;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.mlog.SLog;

import java.util.List;

/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.util.report
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class XBPosReportManager {


    private static XBPosReportManager instance = null;

    private XBPosReportManager() {

    }

    public static XBPosReportManager getInstance() {
        synchronized (XBPosReportManager.class) {
            if (instance == null) {
                instance = new XBPosReportManager();
            }
        }
        return instance;
    }


    void posScan(int codeType, String qrcode) {
        try {
            if (qrcode.length() < 8) {
                BusToast.showToast(BusApp.getInstance(), "二维码参数有误", false);
                return;
            }
            switch (codeType) {
                case QRCode.CONFIG_CODE_FTP:

                    break;
                case QRCode.CONFIG_CODE_MCH:

                    break;
                case QRCode.CONFIG_CODE_IP:

                    break;
                case QRCode.CONFIG_TIMER:
                    String time = qrcode.substring(9, qrcode.length());
                    JSONObject timeObject = JSONObject.parseObject(time);
                    DateUtil.setTime(timeObject.getString("t"), true);

                    break;
                case QRCode.CONFIG_CODE_UNIONPAY:
                    String resultUnion = qrcode.substring(10, qrcode.length());
                    UnionPayParam unionPayParam = new Gson().fromJson(resultUnion, UnionPayParam.class);
                    Util.updateUnionParam(unionPayParam);
                    break;
                case QRCode.QR_MOREN:

                    break;
                default:
                    BusToast.showToast(BusApp.getInstance(), "线路正在更新", true);
                    String resultLine = qrcode.substring(4, qrcode.length());
                    LINEntity linEntity = new Gson().fromJson(resultLine, LINEntity.class);
                    if (linEntity != null) {
                        String fileName = HexUtil.fileName(linEntity.getL());
                        SLog.d("XBPosReportManager(posScan.java:70)需要下载的文件名：" + fileName);

                        List<BaseRequest> taskList = AppUtil.getDownloadAppointFileList(fileName, linEntity.getN());
                        AppUtil.run(taskList, null);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("XBPosReportManager(posScan.java:76)设置码出现异常>>" + e.toString());
        }
    }

}

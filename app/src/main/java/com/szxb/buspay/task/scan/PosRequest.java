package com.szxb.buspay.task.scan;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sqlite.insert;
import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.MainActivity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.db.sp.CommonSharedPreferences;
import com.szxb.buspay.http.CallServer;
import com.szxb.buspay.http.HttpListener;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.param.ParamsUtil;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.tip.MyToast;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.mlog.SLog;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.szxb.buspay.util.Config.QR_ERROR;
import static com.szxb.buspay.util.Config.SCAN_SUCCESS;
import static com.szxb.buspay.util.Config.ZHIFU_SUC;

/**
 * 作者: Tangren on 2017-09-27
 * 包名：com.szxb.buspay.manager.report
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PosRequest {

    private volatile static PosRequest instance = null;
   // private int cishu = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor  editor;
    private PosRequest() {
    }

    public static PosRequest getInstance() {
        if (instance == null) {
            synchronized (PosRequest.class) {
                if (instance == null) {
                    instance = new PosRequest();
                }
            }
        }
        return instance;
    }

    public void request(final QRScanMessage message) {
        switch (message.getResult()) {
            case QRCode.EC_SUCCESS:
                //判断是哪个城市的app
                if (BuildConfig.FLAVOR.equals("laiwu_app")) {
                    //BusApp.setBusNumber(5);
                    SoundPoolUtil.play(ZHIFU_SUC);  //莱芜城市语音播报支付成功
                    int cishu = (int) CommonSharedPreferences.get("infonumber",0);
                    int a = (cishu+1);
                    //TODO:刷卡计数
                    BusApp.setBusNumber(a);
                    CommonSharedPreferences.put("infonumber",a);
                } else {
                    SoundPoolUtil.play(SCAN_SUCCESS); //其他城市语音播报扫码成功
                }
                MyToast.showToast(BusApp.getInstance(), "扫码成功", true);


                if (BuildConfig.FLAVOR.equals("jinan_app")) {
                    message.getPosRecord().setMch_trx_id(BusApp.getPosManager().getmchTrxId());
                    Map<String, Object> map = ParamsUtil.requestMapForJiNan(message.getPosRecord());
                    requestTXFroJN(1000, Config.XBPAY_JINA_WX, map);
                } else {
                    message.getPosRecord().setMch_trx_id(BusApp.getPosManager().getmchTrxId());
                    Map<String, Object> map = ParamsUtil.requestMap(message.getPosRecord());
                    requestTX(100, Config.XBPAY, map);
                }
                break;
            case QRCode.QR_ERROR://非腾讯或者小兵二维码
            case QRCode.EC_CARD_CERT_SIGN_ALG_NOT_SUPPORT://卡证书签名算法不支持
            case QRCode.EC_MAC_ROOT_KEY_DECRYPT_ERR://加密的mac根密钥解密失败
            case QRCode.EC_QRCODE_SIGN_ALG_NOT_SUPPORT://二维码签名算法不支持
            case QRCode.EC_OPEN_ID://输入的openid不符
            case QRCode.EC_CARD_CERT://卡证书签名错误
            case QRCode.EC_FAIL://系统异常
                SoundPoolUtil.play(QR_ERROR);
                BusToast.showToast(BusApp.getInstance(), "二维码有误[" + message.getResult() + "]", false);
                break;
            case QRCode.EC_FEE://超出最大金额
                SoundPoolUtil.play(Config.EC_FEE);
                BusToast.showToast(BusApp.getInstance(), "超出最大金额[" + QRCode.EC_FEE + "]", false);
                break;
            case QRCode.EC_BALANCE://余额不足
                SoundPoolUtil.play(Config.EC_BALANCE);
                BusToast.showToast(BusApp.getInstance(), "余额不足[" + QRCode.EC_BALANCE + "]", false);
                break;
            case QRCode.EC_CODE_TIME://二维码过期
                String noticeStr;
                //检查当前日期是否正常(>=2018)
                if (Calendar.getInstance().get(Calendar.YEAR) < 2018) {
                    noticeStr = "正在校准时间[请重试]";
                    SLog.d("PosRequest(request.java:83)二维码过期[10006]>>>并且当前时间小于2018>>开始校准时间");
                    ThreadFactory.getScheduledPool().execute(new WorkThread("reg_time"));
                } else {
                    noticeStr = "二维码过期[10006]";
                    SoundPoolUtil.play(Config.EC_RE_QR_CODE);
                }
                BusToast.showToast(BusApp.getInstance(), noticeStr, false);
                break;
            case QRCode.REFRESH_QR_CODE://请刷新二维码
            case QRCode.EC_MAC_SIGN_ERR://mac校验失败
            case QRCode.EC_USER_SIGN://二维码签名错误
            case QRCode.EC_FORMAT://二维码格式错误
            case QRCode.EC_USER_PUBLIC_KEY://卡证书用户公钥错误
            case QRCode.EC_CARD_PUBLIC_KEY://卡证书公钥错误
            case QRCode.EC_PARAM_ERR://参数错误
            case QRCode.EC_CARD_CERT_TIME://卡证书过期，提示用户联网刷新二维码
                SoundPoolUtil.play(Config.EC_RE_QR_CODE);
                BusToast.showToast(BusApp.getInstance(), "请刷新二维码[" + message.getResult() + "]", false);
                break;
            default:
                SoundPoolUtil.play(Config.VERIFY_FAIL);
                BusToast.showToast(BusApp.getInstance(), "验码失败[" + message.getResult() + "]", false);
                if (message.getResult() == -1) {
                    List<BaseRequest> taskList = AppUtil.getScanInit();
                    AppUtil.run(taskList, null);
                }
                break;
        }
    }

    private void requestTX(int what, String url, final Map<String, Object> map) {
        JsonRequest request = new JsonRequest(url);
        request.set(map);
        CallServer.getHttpclient().add(what, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                try {
                    JSONObject result = response.get();
                    parseOneJson(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    SLog.d("PosRequest(success.java:127)解析异常," + e.toString());
                }
            }


            @Override
            public void fail(int what, String e) {
                SLog.d("PosRequest(fail.java:134)网络超时");
            }
        });
    }

    /**
     * 单票解析
     *
     * @param result .
     */
    public static void parseOneJson(JSONObject result) {
        Log.i("上传数据返回", result.toString());
        String retcode = result.getString("retcode");
        if (retcode.equals("0")) {
            String retmsg = result.getString("retmsg");
            if (retmsg.equals("ok")) {
                JSONArray result_list = result.getJSONArray("result_list");
                JSONObject resultObject = result_list.getJSONObject(0);
                String tr_result = resultObject.getString("result");
                String mch_trx_id = resultObject.getString("mch_trx_id");

                if (tr_result.equals("0")) {
                    if (resultObject.getString("status").equals("00")
                            || resultObject.getString("status").equals("91")
                            || resultObject.getString("status").equals("11")) {
                        DBManager.updateTransInfo(mch_trx_id, 0, "0", resultObject.getString("status"));
                    } else if (resultObject.getString("status").equals("99")) {
                        DBManager.updateTransInfo(mch_trx_id, 4, tr_result, resultObject.getString("status"));
                    } else {
                        DBManager.updateTransInfo(mch_trx_id, 3, tr_result, resultObject.getString("status"));
                    }
                } else {
                    //准实时扣款失败
                    DBManager.updateTransInfo(mch_trx_id, 3, tr_result, null);
                }
            } else {
                //准实时扣款失败
                SLog.d("PosRequest(parseOneJson.java:170)实时扣款失败,json数据" + result.toJSONString());
            }
        }
    }


    private void requestTXFroJN(int what, String url, final Map<String, Object> map) {
        JsonRequest request = new JsonRequest(url);
        request.set(map);
        CallServer.getHttpclient().add(what, request, new HttpListener<JSONObject>() {
            @Override
            public void success(int what, Response<JSONObject> response) {
                try {
                    //TODO  记录上传成功  需要做后续操作

                    SLog.d("PosRequest(success.java:127)数据返回" + response.get());
//                    JSONObject result = response.get();
//                    parseOneJson(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    SLog.d("PosRequest(success.java:127)解析异常," + e.toString());
                }
            }
            @Override
            public void fail(int what, String e) {
                SLog.d("PosRequest(fail.java:134)网络超时" + e);
            }
        });
    }
}

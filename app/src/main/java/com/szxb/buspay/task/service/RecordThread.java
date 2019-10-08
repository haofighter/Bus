package com.szxb.buspay.task.service;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.ScanInfoEntityDao;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.http.JsonRequest;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.param.ParamsUtil;
import com.szxb.buspay.util.param.sign.ParamSingUtil;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.ParseUtil;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Tangren on 2018-07-26
 * 包名：com.szxb.buspay.task.service
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class RecordThread extends Thread {

    public RecordThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        super.run();
        try {
            if (!AppUtil.checkNetStatus()) {
                //如果无网络,停止本次上传
                return;
            }
            if (TextUtils.equals(getName(), "scan")) {
                scanRecordTask();

            } else if (TextUtils.equals(getName(), "ic")) {
                List<ConsumeCard> icList = DBManager.getICList();
                icRecordTask(0, icList);

            } else if (TextUtils.equals(getName(), "sup_min")) {
                //补采
                List<ConsumeCard> icList = DBManager.getICSupMinList();
                icRecordTask(1, icList);

            } else if (TextUtils.equals(getName(), "union")) {
                unionRecordTask();
            }
        } catch (Exception e) {
            SLog.d("RecordThread(run.java:53)" + getName() + "任务异常>>>" + e.toString());
        }

    }

    /**
     * 刷卡
     */
    private void icRecordTask(final int type, List<ConsumeCard> icList) {
        if (icList.size() == 0) {
            if (type == 1) {
                stopSupMin();
            }
            return;
        }
        JSONArray array = new JSONArray();
        Map<String, Object> map = new HashMap<>();
        for (ConsumeCard cardRecord : icList) {
            JSONObject object = new JSONObject();
            //卡类型
            object.put("cardType", cardRecord.getCardType());
            //交易类型
            object.put("tradeType", cardRecord.getTransType());
            //设备交易序号
            object.put("psamTradeCount", cardRecord.getTransNo());
            //卡号
            object.put("cardNo", cardRecord.getCardNo());
            //卡金额
            object.put("cardAmt", Util.str2Hex(cardRecord.getCardBalance(), 6));
            //交易金额
            object.put("fareAmt", Util.str2Hex(cardRecord.getPayFee(), 6));
            //交易时间
            object.put("tradeDate", cardRecord.getTransTime());
            //
            object.put("tradeTime", cardRecord.getTransTime());
            //用户卡脱机交易序号
            object.put("cardTradeCount", cardRecord.getTransNo2());
            //TAC
            object.put("tACCode", cardRecord.getTac());
            //
            object.put("companyNo", cardRecord.getCompanyNo());
            //
            object.put("lineNo", cardRecord.getLineNo());
            //
            object.put("busNo", cardRecord.getBusNo());
            //
            object.put("driverNo", cardRecord.getDriverNo());
            //
            object.put("pasmNumber", cardRecord.getPasmNo());
            //
            object.put("direction", cardRecord.getDirection());
            //
            object.put("currenStation", cardRecord.getStationId());
            //
            object.put("ticketMore", cardRecord.getFareFlag());
            //
            object.put("currentLineNo", cardRecord.getLineNo());
            //
            object.put("currentBusNo", cardRecord.getBusNo());
            //
            object.put("currentDriverNo", cardRecord.getDriverNo());
            //
            object.put("backup", "0000000000");

            //
            object.put("termid", BusApp.getPosManager().getPosSN());
            object.put("termseq", TextUtils.isEmpty(cardRecord.getReserve_1()) ? Util.Random(10) : cardRecord.getReserve_1());
            object.put("mchid", cardRecord.getMchId());

            object.put("halfprice", cardRecord.getIsHalfPrices());
            object.put("keytype", TextUtils.equals(cardRecord.getCardModuleType(), "08") ? "0" : "31");
            object.put("citycode", cardRecord.getCardNo().substring(0, 4));
            object.put("internalcode", cardRecord.getCardNo().substring(4, 8));
            object.put("branchcode", BusApp.getPosManager().getUnitno());
            object.put("keyver", cardRecord.getCardMacVersion());
            object.put("keyindex", cardRecord.getCardMacIndex());

            array.add(object);
        }
        map.put("data", array.toJSONString());
        String net = Config.IC_CARD_RECORD;
        Log.i("数据上传地址", "数据地址：" + Config.IC_CARD_RECORD);
        JsonRequest request = new JsonRequest(net, RequestMethod.POST);
        request.add(map);
        Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            try {
                JSONObject object = execute.get();
                SLog.d("RecordThread(icRecordTask.java:158)" + object.toJSONString());
                String rescode = object.getString("rescode");
                if (TextUtils.equals(rescode, "0000")) {
                    JSONArray list = object.getJSONArray("dataList");
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject ob = list.getJSONObject(i);
                        String tradeDate = ob.getString("tradeDate");
                        String pasmNumber = ob.getString("pasmNumber");
                        String cardTradeCount = ob.getString("cardTradeCount");
                        String busNo = ob.getString("busNo");
                        String cardNo = ob.getString("cardNo");
                        DBManager.updateCardInfo(type, tradeDate, pasmNumber, cardTradeCount, busNo, cardNo);
                        SLog.d("RecordThread(icRecordTask.java:114)IC卡上传成功   时间:" + tradeDate + " cardNo：" + cardNo + ",busNo" + busNo + ",上传方式=" + (type == 0 ? "正常上传" : "补采上传"));
                    }
                    if (type == 1) {
                        RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.FILL_PUSH_ING));
                    }
                }
            } catch (Exception e) {
                SLog.d("RecordThread(icRecordTask.java:114)IC卡上传异常>>" + e.toString());
            }
        } else {
            SLog.d("RecordThread(unionRecordTask.java:187)IC卡上传网络异常");
            SLog.d("RecordThread(unionRecordTask.java:187)IC卡上传网络异常" + execute.getException().toString() + Config.IC_CARD_RECORD);
        }
    }

    /**
     * 停止补采
     */
    private void stopSupMin() {
        //补采结束通知后台
        SLog.d("RecordThread(icRecordTask.java:87)停止补采任务>>>>>通知后台>>>");
        String url = "http://112.74.102.125/bipbus/interaction/uploadterm";
        Map<String, Object> params = new HashMap<>();
        params.put("termid", BusApp.getPosManager().getPosSN());
        params.put("appid", BusApp.getPosManager().getAppId());
        JsonRequest request = new JsonRequest(url);
        request.add(params);
        Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            JSONObject jsonObject = execute.get();
            SLog.d("RecordThread(stopSupMin.java:201)" + jsonObject.toJSONString());
            if (TextUtils.equals(jsonObject.getString("rescode"), "00")
                    || TextUtils.equals(jsonObject.getString("rescode"), "02")) {
                SLog.d("RecordThread(icRecordTask.java:101)通知后台补采结束成功>>>>>>");
                ThreadFactory.getScheduledPool().stopTask("sup_min");
                RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.FILL_PUSH_END));
            }
        } else {
            SLog.d("RecordThread(stopSupMin.java:207)" + execute.getException().toString());
        }
    }

    /**
     * 银联卡
     */
    private void unionRecordTask() {
        List<UnionPayEntity> unUpList = ParseUtil.unUpList();
        if (unUpList.size() == 0) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        final JSONArray array = new JSONArray();
        for (UnionPayEntity payEntity : unUpList) {
            JSONObject object = new JSONObject();
            object.put("mchId", payEntity.getMchId());
            object.put("unionPosSn", payEntity.getUnionPosSn());
            object.put("posSn", payEntity.getPosSn());
            object.put("busNo", payEntity.getBusNo());
            object.put("totalFee", payEntity.getTotalFee());
            object.put("payFee", payEntity.getPayFee());
            object.put("resCode", payEntity.getResCode());
            object.put("time", payEntity.getTime());
            object.put("tradeSeq", payEntity.getTradeSeq());
            object.put("mainCardNo", payEntity.getMainCardNo());
            object.put("batchNum", payEntity.getBatchNum());
            object.put("bus_line_name", payEntity.getBus_line_name());
            object.put("bus_line_no", payEntity.getBus_line_no());
            object.put("driverNum", payEntity.getDriverNum());
            object.put("unitno", payEntity.getUnitno());
            object.put("upStatus", payEntity.getUpStatus());
            object.put("uniqueFlag", payEntity.getUniqueFlag());
            //卡填1，二维码填2
            object.put("tranType", TextUtils.isEmpty(payEntity.getReserve_2()) ? "1" : "2");
            array.add(object);
        }
        map.put("data", array.toJSONString());
        JsonRequest request = new JsonRequest(Config.UNION_CARD_RECORD);
        request.add(map);
        Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            try {
                JSONObject object = execute.get();
                String rescode = object.getString("rescode");
                if (TextUtils.equals(rescode, "0000")) {
                    JSONArray list = object.getJSONArray("datalist");
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject ob = list.getJSONObject(i);
                        String uniqueFlag = ob.getString("uniqueFlag");
                        ParseUtil.updateUnionUpState(uniqueFlag);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                SLog.d("RecordThread(unionRecordTask.java:116)银联卡上传异常>>" + e.toString());
            }
        } else {
            SLog.d("RecordThread(unionRecordTask.java:187)银联卡上传网络异常" + execute.getException().toString() + Config.IC_CARD_RECORD);
        }
    }


    /**
     * 扫码
     */
    private void scanRecordTask() {
        List<ScanInfoEntity> swipeList = DBManager.getSwipeList();
        if (swipeList.size() == 0) {
            return;
        }
        JsonRequest request = new JsonRequest(Config.XBPAY, RequestMethod.POST);
        JSONObject order_list = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0; i < swipeList.size(); i++) {
            array.add(JSON.parse(swipeList.get(i).getBiz_data_single()));
        }
        order_list.put("order_list", array);
        String timestamp = DateUtil.getCurrentDate();
        Map<String, Object> debitMap = ParamsUtil.commonMap(BusApp.getPosManager().getAppId(), timestamp);
        debitMap.put("sign", ParamSingUtil.getSign(BusApp.getPosManager().getAppId(), timestamp, order_list, Config.private_key));
        debitMap.put("biz_data", order_list);
        request.add(debitMap);
        request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
        Response<JSONObject> execute = SyncRequestExecutor.INSTANCE.execute(request);
        if (execute.isSucceed()) {
            try {
                JSONObject response = execute.get();
                SLog.d("RecordThread(scanRecordTask.java:79)延迟扣款请求响应>>\n" + response.toJSONString());
                String retcode = response.getString("retcode");
                if (retcode.equals("0")) {
                    ScanInfoEntityDao dao = DBCore.getDaoSession().getScanInfoEntityDao();
                    String retmsg = response.getString("retmsg");
                    if (retmsg.equals("ok")) {
                        JSONArray result_list = response.getJSONArray("result_list");
                        for (int i = 0; i < result_list.size(); i++) {
                            JSONObject resultObject = result_list.getJSONObject(i);
                            if (resultObject.getString("status").equals("00")
                                    || resultObject.getString("status").equals("91")
                                    || resultObject.getString("status").equals("11")) {
                                ScanInfoEntity entity = swipeList.get(i);
                                entity.setStatus(2);
                                dao.update(entity);
                                SLog.d("TimeSettleTask(success.java:97)延迟扣款>>扣款成功-修改成功");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                SLog.d("RecordThread(scanRecordTask.java:171)扫码延迟扣款异常>>" + e.toString());
            }
        } else {
            SLog.d("RecordThread(scanRecordTask.java:244)扫码延迟扣款网络异常");
        }
    }
}

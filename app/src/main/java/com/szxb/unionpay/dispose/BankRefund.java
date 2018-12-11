package com.szxb.unionpay.dispose;

import android.text.TextUtils;

import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.http.BaseByteRequest;
import com.szxb.buspay.http.CallServer;
import com.szxb.buspay.http.HttpListener;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.java8583.core.Iso8583Message;
import com.szxb.java8583.core.Iso8583MessageFactory;
import com.szxb.java8583.module.PosRefund;
import com.szxb.java8583.module.PosScanRefund;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.java8583.quickstart.SingletonFactory;
import com.szxb.java8583.quickstart.special.SpecialField62;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者：Tangren on 2018-09-10
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:银联冲正（刷卡、二维码）
 */

public class BankRefund extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            if (!AppUtil.checkNetStatus()) {
                //如果无网络,停止本次上传
                return;
            }

            String[] time = DateUtil.time(1);
            UnionPayEntityDao unionPayEntityDao = DBCore.getDaoSession().getUnionPayEntityDao();
            List<UnionPayEntity> list = unionPayEntityDao.queryBuilder()
                    .where(UnionPayEntityDao.Properties.Time.between(time[0], time[1]))
                    .where(UnionPayEntityDao.Properties.ResCode.eq("408"))
                    .where(UnionPayEntityDao.Properties.Reserve_1.isNotNull())
                    .orderDesc(UnionPayEntityDao.Properties.Id).limit(5).build().list();

            AtomicInteger what = new AtomicInteger(111);
            for (UnionPayEntity payEntity : list) {
                Iso8583Message refund = getIso8583Message(payEntity);
                requestRefund(what.get(), refund.getBytes(), payEntity);
                what.getAndDecrement();
            }

        } catch (Exception e) {
            e.printStackTrace();
            SLog.e("BankRefund(run.java:76)" + e.toString());
        }
    }


    private Iso8583Message getIso8583Message(UnionPayEntity payEntity) {
        return TextUtils.isEmpty(payEntity.getReserve_2())
                ? PosRefund.getInstance().refund(payEntity.getMainCardNo(), payEntity.getReserve_1(), Util.string2Int(payEntity.getTradeSeq()), BusllPosManage.getPosManager().getBatchNum(), "00", Util.string2Int(payEntity.getTotalFee()))
                : PosScanRefund.getInstance().refun(Util.string2Int(payEntity.getTradeSeq()), payEntity.getMainCardNo(), "00", Util.string2Int(payEntity.getTotalFee()));
    }

    private void requestRefund(int what, byte[] sendData, UnionPayEntity entity) {
        String url = BusllPosManage.getPosManager().getUnionPayUrl();
        BaseByteRequest request = new BaseByteRequest(url, RequestMethod.POST);
        InputStream stream = new ByteArrayInputStream(sendData);
        request.setDefineRequestBody(stream, "x-ISO-TPDU/x-auth");
        CallServer.getHttpclient().add(what, request, new HttpResponseListener(entity));
    }

    private class HttpResponseListener implements HttpListener<byte[]> {

        private UnionPayEntity payEntity;

        public HttpResponseListener(UnionPayEntity payEntity) {
            this.payEntity = payEntity;
        }

        @Override
        public void success(int what, Response<byte[]> response) {
            Iso8583MessageFactory factory = SingletonFactory.forQuickStart();
            factory.setSpecialFieldHandle(62, new SpecialField62());
            Iso8583Message message0810 = factory.parse(response.get());
            SLog.d("HttpResponseListener(success.java:97)" + message0810.toFormatString());
            String value = message0810.getValue(39).getValue();
            if (value.equals("00") || value.equals("25") || value.equals("12")) {
                //冲正成功
                payEntity.setResCode("444");
                payEntity.setUpStatus(1);
                ThreadFactory.getScheduledPool().execute(new WorkThread("update_union", payEntity));
            } else {
                payEntity.setResCode(value + "[ERROR]");
                ThreadFactory.getScheduledPool().execute(new WorkThread("update_union", payEntity));
            }
        }

        @Override
        public void fail(int what, String e) {
            SLog.d("HttpResponseListener(fail.java:122)" + e);
        }
    }
}

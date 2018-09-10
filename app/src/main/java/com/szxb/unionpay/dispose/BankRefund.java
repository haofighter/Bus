package com.szxb.unionpay.dispose;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.buspay.db.manager.DBCore;
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
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.java8583.quickstart.SingletonFactory;
import com.szxb.java8583.quickstart.special.SpecialField62;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;
import com.szxb.unionpay.unionutil.SSLContextUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * 作者：Tangren on 2018-09-10
 * 包名：com.szxb.unionpay.dispose
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
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
                Iso8583Message refund = PosRefund.getInstance().refund(
                        payEntity.getMainCardNo(), payEntity.getReserve_1(),
                        Util.string2Int(payEntity.getTradeSeq()), payEntity.getBatchNum(), "00");
                requestRefund(what.get(), refund.getBytes(), payEntity);
                what.getAndDecrement();
            }

        } catch (Exception e) {
            e.printStackTrace();
            SLog.e("BankRefund(run.java:76)"+e.toString());
        }
    }

    private void requestRefund(int what, byte[] sendData, UnionPayEntity entity) {
        String url = BusllPosManage.getPosManager().getUnionPayUrl();
        final Request<byte[]> request = NoHttp.createByteArrayRequest(url, RequestMethod.POST);
        request.setHeader("User-Agent", "Donjin Http 0.1");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Accept", "*/*");
        request.setHeader("Accept-Encoding", "*");
        request.setHeader("Connection", "close");
        request.setHeader("HOST", "120.204.69.139:30000");

        InputStream stream = new ByteArrayInputStream(sendData);
        request.setDefineRequestBody(stream, "x-ISO-TPDU/x-auth");
        SSLContext sslContext = SSLContextUtil.getSSLContext(BusApp.getInstance().getApplicationContext());
        request.setHostnameVerifier(SSLContextUtil.getHostnameVerifier());
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        request.setSSLSocketFactory(socketFactory);

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
            if (value.equals("00")||value.equals("25")||value.equals("12")) {
                //冲正成功
                payEntity.setResCode("444");
                ThreadFactory.getScheduledPool().execute(new WorkThread("update_union", payEntity));
            } else {
                payEntity.setResCode(value + "[冲正失败]");
                ThreadFactory.getScheduledPool().execute(new WorkThread("update_union", payEntity));
            }
        }

        @Override
        public void fail(int what, String e) {
            SLog.d("HttpResponseListener(fail.java:122)" + e);
        }
    }
}

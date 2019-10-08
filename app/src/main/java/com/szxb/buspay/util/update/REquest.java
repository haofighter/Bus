package com.szxb.buspay.util.update;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Tangren on 2018-08-22
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class REquest {

    private volatile int progress = 1;

    public void fatch() {

        List<BaseRequest> list = new ArrayList<>();

        DownloadUnionPayRequest payRequest = new DownloadUnionPayRequest();
        payRequest.setForceUpdate(false);
        list.add(payRequest);

        DownloadBlackRequest blackRequest = new DownloadBlackRequest();
        list.add(blackRequest);

        DownloadLineRequest lineRequest = new DownloadLineRequest();
        lineRequest.setBusNo("123456");
        lineRequest.setFileName("3,5.json");
        list.add(lineRequest);

        DownloadScanRequest scanRequest = new DownloadScanRequest();
        list.add(scanRequest);

        PosInitTask service = new PosInitTask();
        service.run();
    }

}

package com.szxb.buspay.util.update;

import com.szxb.mlog.SLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Tangren on 2018-08-22
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PosInitTask implements OnResponse {

    private List<BaseRequest> list = new ArrayList<>();

    public PosInitTask() {

    }

    public void setTask(List<BaseRequest> list) {
        this.list = list;
    }

    private volatile int taskSize = 0;

    public void run() {
        taskSize = list.size();
        for (Object object : list) {
            if (object instanceof DownloadBlackRequest) {
                DownloadBlackRequest blackRequest = (DownloadBlackRequest) object;
                blackRequest.getDisposable();
                blackRequest.setOnResponse(this);
            } else if (object instanceof DownloadLineRequest) {
                DownloadLineRequest lineRequest = (DownloadLineRequest) object;
                lineRequest.getDisposable();
                lineRequest.setOnResponse(this);
            } else if (object instanceof DownloadScanRequest) {
                DownloadScanRequest scanRequest = (DownloadScanRequest) object;
                scanRequest.getDisposable();
                scanRequest.setOnResponse(this);
            } else if (object instanceof DownloadUnionPayRequest) {
                DownloadUnionPayRequest unionRequest = (DownloadUnionPayRequest) object;
                unionRequest.getDisposable();
                unionRequest.setOnResponse(this);
            }
        }
    }

    @Override
    public void response(boolean success, ResponseMessage response) {
        taskSize -= 1;
        SLog.d("CallService(response.java:56)" + response + ">>剩余=" + taskSize);
        if (taskSize == 0) {
            SLog.d("CallService(response.java:53)任务已经全部执行完毕>>>>");
        }

    }
}

package com.szxb.buspay.util.update;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.scan.param.UnionPayParam;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.param.sign.FileByte;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.unionutil.ParseUtil;

import io.reactivex.ObservableEmitter;

/**
 * 作者：Tangren on 2018-08-22
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DownloadUnionPayRequest extends BaseRequest {

    private boolean forceUpdate;

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    protected void doSubscribe(ObservableEmitter<ResponseMessage> e) {
        response.setWhat(ResponseMessage.WHAT_UNION);
//        UnionPayParam unionParam1 = new UnionPayParam();
//        unionParam1.setSn("06340705");
//        unionParam1.setMch("914371241110001");
//        unionParam1.setKey("A21F43164A31866BD513E304AB76B0C4");
//        Util.updateUnionParam(unionParam1);

        int res = Util.downUnionPayParasFile(forceUpdate, "unionpay/", "银联参数检查更新");
        if (res == 1) {
            //下载好了更新
            String lastParamsFileName = BusApp.getPosManager().getLastParamsFileName();
            SLog.d("DownloadUnionPayRequest(doSubscribe.java:40)lastParamsFileName=" + lastParamsFileName);
            byte[] params = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + lastParamsFileName);
            JSONObject object = HexUtil.parseObject(params);
            if (object != null) {
                UnionPayParam unionParam = new Gson().fromJson(object.toJSONString(), UnionPayParam.class);
                Util.updateUnionParam(unionParam);
                //Log.e("银联商户号：",unionParam.getSn()+";"+unionParam.getMch()+";"+unionParam.getSn());
                response.setStatus(ResponseMessage.SUCCESSFUL);
                response.setMsg("银联参数更新成功");
                ParseUtil.initUnionPay();
//                SLog.d("DownloadUnionPayRequest(doSubscribe.java:47)unionParam=" + unionParam);
            }
        } else if (res == 2) {
            response.setStatus(ResponseMessage.NOUPDATE);
            response.setMsg("银联参数无需更新");
        } else {
            response.setMsg("银联参数不存在");
        }
        e.onNext(response);
    }


}

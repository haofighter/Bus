package com.szxb.buspay.util.update;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.param.sign.FileByte;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.mlog.SLog;

import io.reactivex.ObservableEmitter;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;
import static com.szxb.buspay.util.Util.download;

/**
 * 作者：Tangren on 2018-08-22
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DownloadLineRequest extends BaseRequest {

    private String fileName;
    private String busNo;
    private boolean forceUpdate;

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void doSubscribe(ObservableEmitter<ResponseMessage> e) {
        response.setWhat(ResponseMessage.WHAT_LINE);
        if (forceUpdate) {
            downloadFile();
        } else {
            downloadPoint();
        }
        e.onNext(response);
        SLog.d("DownloadLineRequest(doSubscribe.java:55)线路响应");
    }

    /**
     * 指定下载
     */
    private void downloadPoint() {
        LineInfoEntity infoEntity = BusApp.getPosManager().getLineInfoEntity();
        if (infoEntity == null) {
            //线路文件不存在
            //不更新任何
            SLog.d("DownloadLineRequest(doSubscribe.java:52)线路文件不存在>>>>");
            response.setStatus(ResponseMessage.FILE_NO_EXIT);
            response.setMsg("线路文件不存在");
        } else {
            boolean res = download("allline.json", "pram/allline.json", "版本文件下载");
            if (res) {
                //版本文件下载成功
                SLog.d("DownloadLineRequest(doSubscribe.java:61)版本文件下载成功");
                byte[] pramVersion = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/allline.json");
                JSONObject object = HexUtil.parseObject(pramVersion);
                if (object != null) {
                    JSONArray jsonArray = object.getJSONArray("allline");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        String acnt = ob.getString("acnt");
                        String routeno = ob.getString("routeno");
                        String routeversion = ob.getString("routeversion");
                        String fileName_ = acnt + "," + routeno + ".json";
                        if (TextUtils.equals(fileName_, infoEntity.getFileName())) {
                            if (TextUtils.equals(routeversion, infoEntity.getVersion())
                                    && !TextUtils.isEmpty(infoEntity.getRmk1())) {
                                SLog.d("DownloadLineRequest(doSubscribe.java:76)版本相同无需更新");
                                BusToast.showToast(BusApp.getInstance(), "线路信息初始化成功[EQ]", true);
                                response.setStatus(ResponseMessage.NOUPDATE);
                                response.setMsg("版本相同无需更新");
                            }
                        }

                    }

                    if (response.getStatus() != ResponseMessage.NOUPDATE) {
                        //如果不是无需更新则更新
                        SLog.d("DownloadLineRequest(doSubscribe.java:83)线路版本不相同>>>>>>");
                        downloadFile();
                    }
                }
            } else {
                //版本文件下载失败>>直接更新
                SLog.d("DownloadLineRequest(doSubscribe.java:88)版本文件下载失败>>直接更新");
                downloadFile();
            }
        }
    }

    /**
     * 强制更新(扫码更新)
     */
    private void downloadFile() {
        boolean res = download(fileName, "pram/" + fileName, "下载指定文件");
        if (res) {
            byte[] line = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + fileName);
            JSONObject object = HexUtil.parseObject(line);
            if (object != null) {
                SLog.d("DownloadLineRequest(doSubscribe.java:40)线路信息>>" + object.toJSONString());
                LineInfoEntityDao dao = getDaoSession().getLineInfoEntityDao();
                LineInfoEntity onLineInfo = new LineInfoEntity();
                onLineInfo.setLine(object.getString("line"));
                onLineInfo.setVersion(object.getString("version"));
                onLineInfo.setUp_station(object.getString("up_station"));
                onLineInfo.setDown_station(object.getString("down_station"));
                onLineInfo.setChinese_name(object.getString("chinese_name"));
                onLineInfo.setIs_fixed_price(object.getString("is_fixed_price"));
                onLineInfo.setIs_keyboard(object.getString("is_keyboard"));
                onLineInfo.setFixed_price(object.getString("fixed_price"));
                onLineInfo.setCoefficient(object.getString("coefficient"));
                onLineInfo.setShortcut_price(object.getString("shortcut_price"));
                if (object.containsKey("month_enable_time")) {
                    onLineInfo.setRmk1(object.getString("month_enable_time").replace(":", ""));
                } else {
                    onLineInfo.setRmk1("0");
                }
                onLineInfo.setFileName(fileName);
                //先删除所有
                dao.deleteAll();
                dao.insertOrReplace(onLineInfo);
                HexUtil.parseLine(onLineInfo, busNo);
                BusApp.getPosManager().setLineInfoEntity();
                response.setStatus(ResponseMessage.SUCCESSFUL);
                response.setMsg("线路文件更新成功");
            } else {
                response.setMsg("解析异常");
            }
        } else {
            response.setMsg("线路文件同步失败");
        }
    }

}

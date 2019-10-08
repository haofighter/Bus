package com.szxb.buspay.util.update;

import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.dao.BlackListCardDao;
import com.szxb.buspay.db.entity.bean.BlackList;
import com.szxb.buspay.db.entity.card.BlackListCard;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.HexUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.param.sign.FileByte;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;

import static com.szxb.buspay.db.manager.DBCore.getDaoSession;

/**
 * 作者：Tangren on 2018-08-21
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class DownloadBlackRequest extends BaseRequest {

    @Override
    protected void doSubscribe(ObservableEmitter<ResponseMessage> e) {
        response.setWhat(ResponseMessage.WHAT_BLACK);
        String fileName = "blackversion.json";
        boolean versionRes = Util.download(fileName, "black/blackversion.json", "黑名单版本下载");
        if (versionRes) {
            byte[] blackVersion = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + fileName);
            JSONObject jsonObject = HexUtil.parseObject(blackVersion);
            if (jsonObject != null) {
                String version = jsonObject.getString("blackversion");
                boolean b = HexUtil.checkBlackVersion(version);
                if (b) {
                    //需要更新黑名单
                    fileName = "blacklist.json";
                    boolean download = Util.download(fileName, "black/blacklist.json", "下载黑名单文件");
                    if (download) {
                        byte[] blackList = FileByte.File2byte(Environment.getExternalStorageDirectory() + "/" + fileName);
                        JSONObject object = HexUtil.parseObject(blackList);
                        if (object != null) {
                            BlackList black = new Gson().fromJson(object.toJSONString(), BlackList.class);
                            if (black != null) {
                                BlackListCardDao dao = getDaoSession().getBlackListCardDao();
                                dao.deleteAll();
                                List<String> blacklist = black.getBlacklist();
                                List<BlackListCard> bl = new ArrayList<BlackListCard>();
                                for (String cardNo : blacklist) {
                                    BlackListCard blackListCard = new BlackListCard();
                                    blackListCard.setCard_id(cardNo);
                                    bl.add(blackListCard);
                                }
                                BusApp.getPosManager().setBlackVersion(version);
                                ThreadFactory.getScheduledPool().execute(new WorkThread("black_list", bl));
                                response.setStatus(ResponseMessage.SUCCESSFUL);
                                response.setMsg("黑名单更新成功");
                            }
                        }
                    }
                } else {
                    //无需更新
                    response.setStatus(ResponseMessage.NOUPDATE);
                    response.setMsg("黑名单无需更新");
                }
            }
        }

        e.onNext(response);
    }

}

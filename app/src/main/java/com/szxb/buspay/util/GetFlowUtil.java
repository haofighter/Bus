package com.szxb.buspay.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.TextUtils;

import java.util.List;

/**
 * 作者：Tangren on 2018-08-20
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class GetFlowUtil {
    public static class FlowInfo {
        long upKb;
        long downKb;
        long appname;

        public long getUpKb() {
            return upKb;
        }

        public void setUpKb(long upKb) {
            this.upKb = upKb;
        }

        public long getDownKb() {
            return downKb;
        }

        public void setDownKb(long downKb) {
            this.downKb = downKb;
        }

        public long getAppname() {
            return appname;
        }

        public void setAppname(long appname) {
            this.appname = appname;
        }
    }

    public static FlowInfo getAppFlowInfo(String pakageName, Context context) {
        //获取到配置权限信息的应用程序
        PackageManager pms = context.getPackageManager();
        List<PackageInfo> packinfos = pms
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        //存放具有Internet权限信息的应用
        FlowInfo flowInfo = new FlowInfo();
        for (PackageInfo packinfo : packinfos) {
            String appName = packinfo.packageName;
            if (!TextUtils.isEmpty(appName)) {
                if (appName.equals(pakageName)) {
                    //获取到应用的uid（user id）
                    int uid = packinfo.applicationInfo.uid;
                    //TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息
                    //发送的 上传的流量byte
                    flowInfo.setUpKb(TrafficStats.getUidRxBytes(uid));
                    //下载的流量 byte
                    flowInfo.setDownKb(TrafficStats.getUidTxBytes(uid));
                    break;
                }
            }
        }
        return flowInfo;
    }
}

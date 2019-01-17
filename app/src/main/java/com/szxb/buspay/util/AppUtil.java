package com.szxb.buspay.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.buspay.util.update.DownloadBlackRequest;
import com.szxb.buspay.util.update.DownloadLineRequest;
import com.szxb.buspay.util.update.DownloadScanRequest;
import com.szxb.buspay.util.update.DownloadUnionPayRequest;
import com.szxb.buspay.util.update.OnResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: Tangren on 2017/8/7
 * 包名：com.szxb.utils
 * 邮箱：996489865@qq.com
 * TODO:判断是否是DEBUG模式
 */

public class AppUtil {

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug;
    }

    public static void syncISDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     *
     * @param className 类名
     * @return
     */
    public static boolean isForeground(String className) {
        if (TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) BusApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否有网络
     *
     * @return boolean
     */
    public static boolean checkNetStatus() {
        ConnectivityManager cm = (ConnectivityManager) BusApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Boolean isWifiConn = networkInfo.isConnected();
        NetworkInfo networkInfo_ = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isMobileConn = networkInfo_.isConnected();
        return isWifiConn || isMobileConn;
    }

    /**
     * @param context 。
     * @return 是否有网络
     */
    public static boolean getNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return true;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue .
     * @return .
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * @return 任务队列
     */
    public static List<BaseRequest> getRequestList() {
        List<BaseRequest> taskList = new ArrayList<>();
        if (BusApp.getPosManager().isSuppUnionPay()) {
            DownloadUnionPayRequest payRequest = new DownloadUnionPayRequest();
            payRequest.setForceUpdate(false);
            taskList.add(payRequest);
        }

        LineInfoEntity lineInfoEntity = BusApp.getPosManager().getLineInfoEntity();
        if (lineInfoEntity != null) {
            DownloadLineRequest lineRequest = new DownloadLineRequest();
            lineRequest.setFileName(lineInfoEntity.getFileName());
            lineRequest.setForceUpdate(false);
            taskList.add(lineRequest);
        } else {
            DownloadLineRequest lineRequest = new DownloadLineRequest();
            lineRequest.setForceUpdate(false);
            taskList.add(lineRequest);
        }

        DownloadBlackRequest blackRequest = new DownloadBlackRequest();
        taskList.add(blackRequest);

        DownloadScanRequest scanRequest = new DownloadScanRequest();
        taskList.add(scanRequest);

        return taskList;
    }

    /**
     * @param fileName 线路文件名
     * @param busNo    车辆号
     * @return 指定更新线路
     */
    public static List<BaseRequest> getDownloadAppointFileList(String fileName, final String busNo) {
        List<BaseRequest> taskList = new ArrayList<>();
        DownloadLineRequest lineRequest = new DownloadLineRequest();
        lineRequest.setFileName(fileName);
        if (busNo != null)
            lineRequest.setBusNo(busNo);
        lineRequest.setForceUpdate(true);
        taskList.add(lineRequest);
        return taskList;
    }

    /**
     * @return 二维码任务
     */
    public static List<BaseRequest> getScanInit() {
        List<BaseRequest> taskList = new ArrayList<>();
        DownloadScanRequest scanRequest = new DownloadScanRequest();
        taskList.add(scanRequest);
        return taskList;
    }

    /**
     * 执行更新任务
     *
     * @param taskList   任务队列
     * @param onResponse 回调
     */
    public static void run(List<BaseRequest> taskList, OnResponse onResponse) {
        for (Object object : taskList) {
            if (object instanceof DownloadBlackRequest) {
                DownloadBlackRequest blackRequest = (DownloadBlackRequest) object;
                blackRequest.getDisposable();
                blackRequest.setOnResponse(onResponse);
            } else if (object instanceof DownloadLineRequest) {
                DownloadLineRequest lineRequest = (DownloadLineRequest) object;
                lineRequest.getDisposable();
                lineRequest.setOnResponse(onResponse);
            } else if (object instanceof DownloadScanRequest) {
                DownloadScanRequest scanRequest = (DownloadScanRequest) object;
                scanRequest.getDisposable();
                scanRequest.setOnResponse(onResponse);
            } else if (object instanceof DownloadUnionPayRequest) {
                DownloadUnionPayRequest unionRequest = (DownloadUnionPayRequest) object;
                unionRequest.getDisposable();
                unionRequest.setOnResponse(onResponse);
            }
        }
    }
}

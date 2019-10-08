package com.szxb.buspay.task;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szxb.buspay.module.init.InitActivity;
import com.szxb.mlog.SLog;

import java.util.List;


/**
 * 作者: Tangren on 2017/7/12
 * 包名：com.szxb.onlinbus.task
 * 邮箱：996489865@qq.com
 * TODO:开机自起
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String PACKAGE_NAME = "com.szxb.buspay";
    private boolean isAppRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION) ||
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo info : runningTasks) {
                if (info.topActivity.getPackageName().equals(PACKAGE_NAME) && info.baseActivity.getPackageName().equals(PACKAGE_NAME)) {
                    isAppRunning = true;
                    break;
                }
            }

            if (!isAppRunning) {
                Intent mainActivityIntent = new Intent(context, InitActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
            } else {
                SLog.d("BootBroadcastReceiver(onReceive.java:44)Activity已经启动>>>");
            }
        }

    }
}

package com.szxb.buspay.task;

import android.os.Environment;

import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.DateUtil;
import com.szxb.mlog.SLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者: Tangren on 2018-05-05
 * 包名：com.szxb.buspay.task
 * 邮箱：996489865@qq.com
 * TODO:删除过期文件>1周的log
 */

public class TaskDelFile {

    public void del() {
        ThreadFactory.getScheduledPool().execute(new FileRunnable());
    }

    private static class FileRunnable implements Runnable {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("zh", "CN"));

        @Override
        public void run() {
            try {
                String path = Environment.getExternalStorageDirectory() + "/BJLogger";
                File file = new File(path);
                if (!file.exists()) return;
                File[] files = file.listFiles();
                Pattern pattern = Pattern.compile("[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}");
                for (File file1 : files) {
                    Matcher matcher = pattern.matcher(file1.getName());
                    String dateStr = null;
                    if (matcher.find()) {
                        dateStr = matcher.group(0);
                    }
                    Date lastTime = format.parse(dateStr);
                    boolean delFile = DateUtil.isDelFile(lastTime);
                    if (delFile) {
                        boolean delete = file1.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                SLog.d("TaskDelFile(del.java:43)删除过期log出现异常," + e.toString());
            }
        }
    }

}

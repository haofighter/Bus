package com.szxb.buspay.module.init;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.MainActivity;
import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.bean.whitelist;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.tip.MainLooper;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.buspay.util.update.DownloadUnionPayRequest;
import com.szxb.buspay.util.update.OnResponse;
import com.szxb.buspay.util.update.ResponseMessage;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.unionutil.ParseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 作者：Tangren on 2018-08-03
 * 包名：com.szxb.buspay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class InitActivity extends AppCompatActivity implements OnResponse {

    private boolean updateOk = false;
    private boolean binOk = false;
    private boolean whiteListOK = false;

    private TextView update_info; //具体消息
    private RelativeLayout init_layout;
    private AtomicInteger taskSize;

    private AnimationDrawable drawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        update_info = findViewById(R.id.update_info);
        init_layout = findViewById(R.id.init_layout);
        ImageView progress = findViewById(R.id.progress); //加载的小图片
        drawable = (AnimationDrawable) progress.getBackground();
        drawable.start();
        TextView tip_info = findViewById(R.id.tip_info); //界面底部的温馨提示
        tip_info.setText(String.format("温馨提示:\n\t\t\t\t%1$s", Config.tip()));
        update_info.setText("微信同步中\n");
        update_info.append("bin初始化中\n");
        update_info.append("线路文件同步中\n");
        initBin();
        setTaskList();
        initUnionPay();
        initWhiteList();
        initView();
    }

    private void initView() {
        if (BuildConfig.CITY == 1) {
            init_layout.setBackgroundResource(R.mipmap.taian__bg);
        } else if (BuildConfig.CITY == 2) {
            init_layout.setBackgroundResource(R.mipmap.laiwu_gj);
        } else if (BuildConfig.CITY == 7) {
            init_layout.setBackgroundResource(R.mipmap.laiwu_cy);
        } else {
            init_layout.setBackgroundResource(R.mipmap.bzb);
        }
    }

    private void setTaskList() {
        List<BaseRequest> taskList = AppUtil.getRequestList();
        taskSize = new AtomicInteger(taskList.size());
        AppUtil.run(taskList, this);
    }


    private void initBin() {
        ThreadFactory.getScheduledPool().execute(new Runnable() {
            @Override
            public void run() {
                String lastVersion = BusApp.getPosManager().getLastVersion();  //上个bin版本
                Log.e("lastVersion",lastVersion);
                String binName = BuildConfig.BIN_NAME;
                Log.e("binName",binName);
                if (!TextUtils.equals(lastVersion, binName)) {
                    AssetManager ass = BusApp.getInstance().getAssets();
                    int k = libszxb.ymodemUpdate(ass, binName);
                    if (k == 0) {
                        BusApp.getPosManager().setLastVersion(binName);
                    }
                    BusToast.showToast(BusApp.getInstance(), "固件更新成功", true);
                    Log.i("sfsfasfsafsf", "固件更新成功  状态：" + k);
                }
                binOk = true;
                MainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update_info.append("bin更新完成\n");
                    }
                });

                if (updateOk && whiteListOK) {
                    startActivity(new Intent(InitActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    public void initUnionPay() {
        boolean isSuppUnionPay = BusApp.getPosManager().isSuppUnionPay();
        if (!isSuppUnionPay) {
            return;
        }
        String posSn = BusllPosManage.getPosManager().getPosSn();
        if (TextUtils.equals(posSn, "00000000")) {
            DownloadUnionPayRequest payRequest = new DownloadUnionPayRequest();
            payRequest.setForceUpdate(true);
            payRequest.getDisposable();
            SLog.d("InitActivity(initUnionPay.java:120)银联参数暂未配置>>");
            return;
        }
        update_info.append("银联签到中\n");
        ParseUtil.initUnionPay();
        update_info.append("银联签到完成\n");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (drawable != null) {
            drawable.stop();
        }
    }

    @Override
    public void response(boolean success, ResponseMessage response) {
        SLog.d("InitActivity(response.java:179)" + response);
        taskSize.getAndDecrement();
        update_info.append(response.getMsg() + "\n");
        if (taskSize.get() <= 0) {
            updateOk = true;
            if (binOk && whiteListOK) {
                startActivity(new Intent(InitActivity.this, MainActivity.class));
                finish();
            }
        }
    }


    public void initWhiteList() {
        update_info.setText("导入白名单\n");
        InputStream is = null;
        try {
            is = getAssets().open("whitelist.dat.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int lenght = 0;
        try {
            lenght = is.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[lenght];
        try {
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = null;
        try {
            result = new String(buffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] sresult = result.split("\r\n");
        for (int i = 0; i < sresult.length; i++) {

            System.out.println(sresult[i]);

            if (i > 1) {
                String[] sitem = sresult[i].split("\\s+");

                if (sitem.length > 2) {
                    System.out.println(sitem[0] + sitem[1] + sitem[2]);

                    whitelist addlist = new whitelist(null, sitem[0], sitem[1], sitem[2]);
                    DBCore.getDaoSession().getWhitelistDao().insertOrReplace(addlist);
                } else {
                    System.out.println(sitem[0] + sitem[1]);

                    whitelist addlist = new whitelist(null, sitem[0], sitem[1], "no word");
                    DBCore.getDaoSession().getWhitelistDao().insertOrReplace(addlist);
                }
            }
        }
        List<whitelist> list = DBCore.getDaoSession().getWhitelistDao().loadAll();

        System.out.println(list.size() + "");
        whiteListOK = true;
        update_info.append("白名单导入成功\n");
        if (taskSize.get() <= 0) {
            if (binOk && updateOk) {
                startActivity(new Intent(InitActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}

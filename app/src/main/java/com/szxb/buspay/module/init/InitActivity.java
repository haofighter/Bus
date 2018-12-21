package com.szxb.buspay.module.init;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.MainActivity;
import com.szxb.buspay.R;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
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

    private TextView update_info;
    private AtomicInteger taskSize;

    private AnimationDrawable drawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        update_info = (TextView) findViewById(R.id.update_info);
        ImageView progress = (ImageView) findViewById(R.id.progress);
        drawable = (AnimationDrawable) progress.getBackground();
        drawable.start();
        TextView tip_info = (TextView) findViewById(R.id.tip_info);
        tip_info.setText(String.format("温馨提示:\n\t\t\t\t%1$s", Config.tip()));
        update_info.setText("微信同步中\n");
        update_info.append("bin初始化中\n");
        update_info.append("线路文件同步中\n");
        initBin();
        setTaskList();
        initUnionPay();
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
                String lastVersion = BusApp.getPosManager().getLastVersion();
                String binName = BuildConfig.BIN_NAME;
                if (!TextUtils.equals(lastVersion, binName)) {
                    AssetManager ass = BusApp.getInstance().getAssets();
                    int k = libszxb.ymodemUpdate(ass, binName);
                    BusApp.getPosManager().setLastVersion(binName);

                    BusToast.showToast(BusApp.getInstance(), "固件更新成功", true);
                }
                binOk = true;
                MainLooper.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        update_info.append("bin更新完成\n");
                    }
                });

                if (updateOk) {
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
            if (binOk) {
                startActivity(new Intent(InitActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}

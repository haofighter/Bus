package com.szxb.buspay;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.lilei.tool.tool.IToolInterface;
import com.szxb.buspay.db.manager.DBCore;
import com.szxb.buspay.manager.PosManager;
import com.szxb.buspay.task.TaskDelFile;
import com.szxb.buspay.task.service.RecordThread;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.mlog.AndroidLogAdapter;
import com.szxb.mlog.CsvFormatStrategy;
import com.szxb.mlog.DiskLogAdapter;
import com.szxb.mlog.FormatStrategy;
import com.szxb.mlog.PrettyFormatStrategy;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.config.UnionPayManager;
import com.szxb.unionpay.dispose.BankRefund;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class BusApp extends Application {
    private static BusApp instance;
    private static PosManager manager;
    //服务操作
    private IToolInterface mService;

    /**
     * 0：淄博
     * 1：泰安
     * 2：莱芜长运
     * 3：招远
     * 4：荣成
     * 5：潍坊
     */
    private static int city = BuildConfig.CITY;

    /**
     * taian.bin 泰安
     * zibo.bin 淄博
     * laiwu_cy.bin 莱芜长运
     * zhaoyuan.bin 招远
     */
    private static String binName = BuildConfig.BIN_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DBCore.init(this, "databases_bus.db");

        UnionPayManager unionPayManager = new UnionPayManager();
        BusllPosManage.init(unionPayManager);

        manager = new PosManager();
        manager.loadFromPrefs(city, binName);


        initLog();

        SoundPoolUtil.init(this);

        NoHttp.initialize(InitializationConfig.newBuilder(this)
                .networkExecutor(new OkHttpNetworkExecutor())
                .connectionTimeout(15 * 1000)
                .build());

        SophixManager.getInstance().queryAndLoadNewPatch();
        crashReport();
        initTask();

        initService();
    }

    //连接服务
    private void initService() {
        Intent i = new Intent();
        i.setAction("com.lypeer.aidl");
        i.setPackage("com.lilei.tool.tool");
        boolean ret = bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public IToolInterface getmService() {
        return mService;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IToolInterface.Stub.asInterface(service);
        }
    };


    public static BusApp getInstance() {
        return instance;
    }

    public static PosManager getPosManager() {
        if (manager == null) {
            manager = new PosManager();
            manager.loadFromPrefs(city, binName);
        }
        return manager;
    }

    private void initLog() {
        FormatStrategy format = PrettyFormatStrategy.newBuilder()
                .tag(BuildConfig.LOG_TAG)
                .showThreadInfo(false)
                .methodCount(1)
                .build();
        SLog.addLogAdapter(new AndroidLogAdapter(format));
        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag(BuildConfig.LOG_TAG)
                .fileName(BusApp.getPosManager().getBusNo())
                .build();
        SLog.addLogAdapter(new DiskLogAdapter(formatStrategy));
        new TaskDelFile().del();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initSophi();
    }

    private void initSophi() {
        String appVersion = AppUtil.getVersionName(this);
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.d("BusApp",
                                    "onLoad(BusApp.java:169)补丁加载成功>>handlePatchVersion=" + handlePatchVersion);
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            Log.d("BusApp",
                                    "onLoad(BusApp.java:175)补丁重启生效>>handlePatchVersion=" + handlePatchVersion);
                        } else {
                            Log.d("BusApp",
                                    "onLoad(BusApp.java:176)code=" + code + ">>其他错误>>handlePatchVersion=" + handlePatchVersion + ">>info=" + info);
                        }
                    }
                }).initialize();

    }

    private void initTask() {
        //状态上报
        ThreadFactory.getScheduledPool().executeDelay(new WorkThread("pos_status_push"), 30, TimeUnit.SECONDS);

        //校准时间
        ThreadFactory.getScheduledPool().executeDelay(new WorkThread("app_reg_time"), 1, TimeUnit.MINUTES);

        //检查补采
        ThreadFactory.getScheduledPool().executeDelay(new WorkThread("check_fill", 0), 40, TimeUnit.SECONDS);

        if (BusApp.getPosManager().isSuppScanPay()) {
            ThreadFactory.getScheduledPool().executeCycle(new RecordThread("scan"), 13, 30, "scan", TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppIcPay()) {
            ThreadFactory.getScheduledPool().executeCycle(new RecordThread("ic"), 30, 35, "ic", TimeUnit.SECONDS);
            ThreadFactory.getScheduledPool().executeCycle(new BankRefund(), 60, 37, "union_refund", TimeUnit.SECONDS);
        }

        if (BusApp.getPosManager().isSuppUnionPay()) {
            ThreadFactory.getScheduledPool().executeCycle(new RecordThread("union"), 45, 30, "union", TimeUnit.SECONDS);
        }
    }


    private void crashReport() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            @Override
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                map.put("device_no", BusApp.getPosManager().getAppId());
                map.put("line_no", BusApp.getPosManager().getLineNo());
                map.put("line_name", BusApp.getPosManager().getLineName());
                map.put("bus_no", BusApp.getPosManager().getBusNo());
                map.put("driver_no", BusApp.getPosManager().getPosSN());
                map.put("time", DateUtil.getCurrentDate());
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });
        CrashReport.initCrashReport(this, "e95522befa", true, strategy);
    }

}

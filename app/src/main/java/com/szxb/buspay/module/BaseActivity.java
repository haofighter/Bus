package com.szxb.buspay.module;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.szxb.buspay.BuildConfig;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.bean.CntEntity;
import com.szxb.buspay.db.entity.bean.MainEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.bean.card.ConsumeCard;
import com.szxb.buspay.db.entity.scan.ScanInfoEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.interfaces.OnKeyListener;
import com.szxb.buspay.task.key.LoopKeyTask;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.task.thread.WorkThread;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.WaitDialog;
import com.szxb.buspay.util.WriteRecordToSD;
import com.szxb.buspay.util.adapter.HomeParentAdapter;
import com.szxb.buspay.util.adapter.RecordAdapter;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.tip.BusToast;
import com.szxb.buspay.util.tip.MyToast;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.buspay.util.update.DownloadUnionPayRequest;
import com.szxb.buspay.util.update.OnResponse;
import com.szxb.buspay.util.update.ResponseMessage;
import com.szxb.java8583.module.manager.BusllPosManage;
import com.szxb.mlog.SLog;
import com.szxb.unionpay.entity.UnionPayEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.szxb.buspay.db.manager.DBManager.queryScanRecord;
import static com.szxb.buspay.util.Config.POSITION_BUS_RECORD;
import static com.szxb.buspay.util.Config.POSITION_CHECK_NET;
import static com.szxb.buspay.util.Config.POSITION_CNT;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_1_M;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_3_M;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_7;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_DB;
import static com.szxb.buspay.util.Config.POSITION_EXPORT_LOG;
import static com.szxb.buspay.util.Config.POSITION_PUSH_FILL;
import static com.szxb.buspay.util.Config.POSITION_READ_PARAM;
import static com.szxb.buspay.util.Config.POSITION_SCAN_RECORD;
import static com.szxb.buspay.util.Config.POSITION_TIME;
import static com.szxb.buspay.util.Config.POSITION_UNION_RECORD;
import static com.szxb.buspay.util.Config.POSITION_UPDATE_PARAMS;
import static com.szxb.buspay.util.Util.fen2Yuan;
import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者: Tangren on 2017/7/17
 * 包名：com.szxb.buspay.base
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public abstract class BaseActivity extends AppCompatActivity implements OnKeyListener, OnResponse {

    //菜单view
    protected RecyclerView recyclerView;

    //网络状态
    private TextView net_status;
    //补采状态
    private TextView fill_status;

    //记录view
    protected RecyclerView recycler_view_record;
    protected View main_record;
    protected TextView record_type;

    //汇总view
    protected TextView record_type_cnt;
    protected View main_cnt;
    protected TextView ic_card_swipe_cnt, ic_card_amount_cnt, ic_card_up_status;
    protected TextView scan_cnt, scan_amount_cnt, scan_up_status;
    protected TextView union_cnt, union_amount_cnt, union_un_status;
    protected TextView sum_cnt, sum_amount_cnt, sum_up_status;
    protected TextView time_cnt, time_amount_cnt;

    //参数信息
    protected View main_info;
    protected TextView main_info_type;
    protected TextView city_code, ten_mach_id, union_mch_id;
    protected TextView line_name, driver_no, union_pos_sn;
    protected TextView pos_sn, black_version, black_cnt;
    protected TextView bin_name, app_version, sn;

    protected View main_main;

    private void initCntView() {
        ic_card_swipe_cnt = findViewById(R.id.ic_card_swipe_cnt);
        ic_card_amount_cnt = findViewById(R.id.ic_card_amount_cnt);
        ic_card_up_status = findViewById(R.id.ic_card_up_status);

        scan_cnt = findViewById(R.id.scan_cnt);
        scan_amount_cnt = findViewById(R.id.scan_amount_cnt);
        scan_up_status = findViewById(R.id.scan_up_status);

        union_cnt = findViewById(R.id.union_cnt);
        union_amount_cnt = findViewById(R.id.union_amount_cnt);
        union_un_status = findViewById(R.id.union_un_status);

        sum_cnt = findViewById(R.id.sum_cnt);
        sum_amount_cnt = findViewById(R.id.sum_amount_cnt);
        sum_up_status = findViewById(R.id.sum_up_status);

        time_cnt = findViewById(R.id.time_cnt);
        time_amount_cnt = findViewById(R.id.time_amount_cnt);
    }

    private void initMainInfoView() {
        main_info = findViewById(R.id.main_info);
        main_info_type = findViewById(R.id.main_info_type);

        city_code = findViewById(R.id.city_code);
        ten_mach_id = findViewById(R.id.ten_mach_id);
        union_mch_id = findViewById(R.id.union_mch_id);

        line_name = findViewById(R.id.line_name);
        driver_no = findViewById(R.id.driver_no);
        union_pos_sn = findViewById(R.id.union_pos_sn);

        pos_sn = findViewById(R.id.pos_sn);
        black_version = findViewById(R.id.black_version);
        black_cnt = findViewById(R.id.black_cnt);

        bin_name = findViewById(R.id.bin_name);
        app_version = findViewById(R.id.app_version);
        sn = findViewById(R.id.sn);
    }

    //签到view
    protected View main_sign;

    protected HomeParentAdapter mAdapter;
    protected List<MainEntity> mList = new ArrayList<>();

    protected abstract int rootView();

    protected TranslateAnimation mShowAnimation;
    protected TranslateAnimation mHiddenAnimation;


    //是否在查询记录界面
    private boolean childViewShow = false;
    private RecordAdapter recordAdapter;
    private List<MainEntity> mRecordList = new ArrayList<>();

    //dialog
    private WaitDialog waitDialog;

    protected void initView() {
        net_status = findViewById(R.id.net_status);
        fill_status = findViewById(R.id.fill_status);
        recyclerView = findViewById(R.id.recycler_view);
        recycler_view_record = findViewById(R.id.recycler_view_record);
        main_record = findViewById(R.id.main_record);
        record_type = findViewById(R.id.record_type);
        record_type_cnt = findViewById(R.id.record_type_cnt);
        main_cnt = findViewById(R.id.main_cnt);
        main_sign = findViewById(R.id.main_sign);
        main_main = findViewById(R.id.main_main);

        if (BuildConfig.CITY == 1) {
            main_sign.setBackgroundResource(R.mipmap.taian__bg);
            main_main.setBackgroundResource(R.mipmap.taian__bg);
        } else if (BuildConfig.CITY == 2) {
            main_sign.setBackgroundResource(R.mipmap.laiwu_gj);
            main_main.setBackgroundResource(R.mipmap.laiwu_gj);
        } else {
            main_sign.setBackgroundResource(R.mipmap.bzb);
            main_main.setBackgroundResource(R.mipmap.bzb);
        }

        initCntView();
        initMainInfoView();
    }

    protected abstract void initData();

    protected abstract void message(QRScanMessage message);

    private LoopKeyTask instance;

    private Disposable subscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 21) {
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(rootView());
        initView();

        initRx();

        initData();

        initList();

        instance = LoopKeyTask.getInstance();
        instance.setOnKeyListener(this);
        instance.startLoopKey();

        initRecyclerData();

        initAnimation();

        waitDialog = new WaitDialog();

    }

    private void initRx() {
        subscribe = RxBus.getInstance().toObservable(QRScanMessage.class)
                .onBackpressureDrop()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QRScanMessage>() {
                    @Override
                    public void accept(@NonNull QRScanMessage qrScanMessage) {
                        try {
                            if (qrScanMessage.getResult() == QRCode.STOP_CNT) {
                                //关闭菜单页
                                onKeyCancel();
                            } else if (qrScanMessage.getResult() == QRCode.CNT) {
                                //汇总
                                showCnt(qrScanMessage.getCntEntity());
                            } else if (qrScanMessage.getResult() == QRCode.NET_STATUS) {
                                //网络状态发送改变
                                hasNetWork(AppUtil.getNetWorkState(getApplicationContext()));
                            } else if (qrScanMessage.getResult() == QRCode.FILL_PUSH_ING
                                    || qrScanMessage.getResult() == QRCode.FILL_PUSH_END) {
                                //补采通知
                                fillPush(qrScanMessage.getResult());
                            } else if (qrScanMessage.getResult() == QRCode.TIP) {
                                //刷卡提示
                                MyToast.showToast2(BusApp.getInstance(), qrScanMessage.getMessage(), qrScanMessage.isOk());
                            } else if (qrScanMessage.getResult() == QRCode.START_DIALOG) {
                                //启动Dialog
                                showDialog();
                            } else if (qrScanMessage.getResult() == QRCode.STOP_DIALOG) {
                                //关闭Dialog
                                closeDialog();
                            } else {
                                message(qrScanMessage);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            SLog.d("BaseActivity(accept.java:271)Rx异常>" + Util.getExectionStr(e));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        SLog.d("BaseActivity(call.java:115)Rx异常>>" + Util.getExectionStr((Exception) throwable));
                    }
                });
    }


    protected abstract void initList();

    //初始化MENU数据
    private void initRecyclerData() {
        mAdapter = new HomeParentAdapter(getApplicationContext(), mList, recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter.setItemChecked(0);

        recordAdapter = new RecordAdapter(getApplicationContext(), mRecordList, recycler_view_record);
        recycler_view_record.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view_record.setAdapter(recordAdapter);
        recycler_view_record.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    }

    //菜单初始化动画
    private void initAnimation() {
        mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnimation.setDuration(500);

        mHiddenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAnimation.setDuration(500);
    }


    @Override
    public void onKeyDown() {
        if (main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!childViewShow) {
            //如果不是交易查询界面
            if (recyclerView.getVisibility() == View.GONE) {
                setViewStatus(recyclerView, true);
            } else {
                mAdapter.downKey();
            }
        } else {
            recordAdapter.downKey();
        }
    }

    @Override
    public void onKeyUp() {
        if (main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!childViewShow) {
            if (recyclerView.getVisibility() == View.GONE) {
                setViewStatus(recyclerView, true);
            } else {
                mAdapter.upKey();
            }
        } else {
            recordAdapter.upKey();
        }
    }

    @Override
    public void onKeyOk() {
        if (childViewShow || main_sign.getVisibility() == View.VISIBLE) {
            return;
        }
        int position = mAdapter.getCurrentItemPosition();
        //刷卡记录
        if (position == POSITION_BUS_RECORD) {
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("刷卡记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("卡号", "余额", "金额", "交易时间"));
            List<ConsumeCard> zibo = DBManager.queryCardRecord("zibo");
            if (zibo != null) {
                for (ConsumeCard consumeCard : zibo) {
                    MainEntity mainEntity = new MainEntity();
                    mainEntity.setCardType(consumeCard.getCardType());
                    mainEntity.setType(0);
                    mainEntity.setStatus(consumeCard.getUpStatus());
                    mainEntity.setTime(consumeCard.getTransTime());
                    mainEntity.setCard_id(consumeCard.getCardNo());
                    if (TextUtils.equals(consumeCard.getCardType(), "0A")) {
                        //次数卡
                        mainEntity.setCard_money(string2Int(consumeCard.getCardBalance()) + "");
                        mainEntity.setPay_money(string2Int(consumeCard.getPayFee()) + "");
                    } else {
                        mainEntity.setCard_money(fen2Yuan(string2Int(consumeCard.getCardBalance())));
                        mainEntity.setPay_money(fen2Yuan(string2Int(consumeCard.getPayFee())));
                    }
                    mRecordList.add(mainEntity);
                }
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_SCAN_RECORD) {//扫码记录
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("扫码记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("用户ID", "交易状态", "金额", "交易时间"));
            List<ScanInfoEntity> scanInfoEntityList = queryScanRecord();
            for (ScanInfoEntity scanInfo : scanInfoEntityList) {
                MainEntity mainEntity = new MainEntity();
                mainEntity.setType(1);
                mainEntity.setStatus(scanInfo.getStatus());
                mainEntity.setTime(scanInfo.getTime());
                mainEntity.setCard_id(scanInfo.getOpenid());
                //扫码无法获取用户余额此,处为上传跟支付状态
                mainEntity.setCard_money(Util.scanStatus(scanInfo.getStatus()));
                mainEntity.setPay_money(fen2Yuan(scanInfo.getPay_fee()));
                mRecordList.add(mainEntity);
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_UNION_RECORD) {//银联卡记录
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_record, true);
            record_type.setText("银联卡记录");
            mRecordList.clear();
            mRecordList.add(new MainEntity("卡号", "交易状态", "金额", "交易时间"));
            List<UnionPayEntity> unionPayEntityList = DBManager.queryUnionPayRecord();
            for (UnionPayEntity unionPayEntity : unionPayEntityList) {
                MainEntity mainEntity = new MainEntity();
                mainEntity.setType(TextUtils.isEmpty(unionPayEntity.getReserve_2()) ? 2 : 3);
                mainEntity.setStatus(unionPayEntity.getUpStatus());
                mainEntity.setTime(unionPayEntity.getTime());
                mainEntity.setCard_id(unionPayEntity.getMainCardNo());
                //银联卡无法获取用户余额此,处为扣款状态
                mainEntity.setCard_money(Util.unionPayStatus(unionPayEntity.getResCode()));
                mainEntity.setPay_money(fen2Yuan(string2Int(unionPayEntity.getPayFee())));
                mRecordList.add(mainEntity);
            }
            recordAdapter.position = 0;
            recordAdapter.setItemChecked(0);
            recordAdapter.refreshData(mRecordList);
        } else if (position == POSITION_CNT) {//当天汇总
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_cnt, true);
            record_type_cnt.setText("当天汇总");
            ThreadFactory.getScheduledPool().execute(new WorkThread("cnt"));
        } else if (position == POSITION_UPDATE_PARAMS) {//手动更新参数
            BusToast.showToast(BusApp.getInstance(), "开始更新请稍后", true);
            List<BaseRequest> taskList = AppUtil.getRequestList();
            AppUtil.run(taskList, this);
            onKeyCancel();
        } else if (position == POSITION_EXPORT_DB) {//数据库导出
            ThreadFactory.getScheduledPool().execute(new WorkThread("export_db"));
        } else if (position == POSITION_EXPORT_LOG) {//日志导出
            ThreadFactory.getScheduledPool().execute(new WorkThread("export_log"));
        } else if (position == POSITION_CHECK_NET) {//检测网络
            boolean b = AppUtil.getNetWorkState(getApplicationContext());
            BusToast.showToast(getApplicationContext(), b ? "网络已连接" : "未检测到网络", b);
            onKeyCancel();
        } else if (position == POSITION_TIME) {//校准时间
            BusToast.showToast(getApplicationContext(), "开始校准时间", true);
            ThreadFactory.getScheduledPool().execute(new WorkThread("reg_time"));
            onKeyCancel();
        } else if (position == POSITION_EXPORT_7) {//导出7天记录
            export(7);
        } else if (position == POSITION_EXPORT_1_M) {//导出1个月记录
            export(30);
        } else if (position == POSITION_EXPORT_3_M) {//导出3个月记录
            export(90);
        } else if (position == POSITION_READ_PARAM) {//参数信息
            onKeyCancel();
            childViewShow = true;
            setViewStatus(main_info, true);
            main_info_type.setText("参数查询");

            city_code.setText(String.format("城市代码\n%1$s", BusApp.getPosManager().getCityCode()));
            ten_mach_id.setText(String.format("腾讯商户号\n%1$s", BusApp.getPosManager().getAppId()));
            union_mch_id.setText(String.format("银联商户号\n%1$s", BusllPosManage.getPosManager().getMchId()));
            line_name.setText(String.format("线路名\n%1$s", BusApp.getPosManager().getChinese_name()));
            driver_no.setText(String.format("司机号\n%1$s", BusApp.getPosManager().getDriverNo()));
            union_pos_sn.setText(String.format("银联POS编号\n%1$s", BusllPosManage.getPosManager().getPosSn()));
            pos_sn.setText(String.format("车辆编号\n%1$s", BusApp.getPosManager().getBusNo()));
            black_version.setText(String.format("黑名单版本\n%1$s", BusApp.getPosManager().getBlackVersion()));
            long[] longs = DBManager.queryBlackListCnt();
            black_cnt.setText(String.format("黑名单数量\n%1$d|%2$d", longs[0], longs[1]));
            bin_name.setText(String.format("BIN版本\n%1$s", BuildConfig.BIN_NAME));
            app_version.setText(String.format("软件版本\n%1$s", AppUtil.getVersionName(this)));
            sn.setText(String.format("SN编号\n%1$s", BusApp.getPosManager().getPosSN()));
        } else if (position == POSITION_PUSH_FILL) {
            if (ThreadFactory.getScheduledPool().isRunningInPool("sup_min")) {
                BusToast.showToast(BusApp.getInstance().getApplicationContext(), "请勿重复启动补采任务", false);
            } else {
                BusToast.showToast(BusApp.getInstance().getApplicationContext(), "检查补采\n请稍后", true);
                ThreadFactory.getScheduledPool().executeDelay(new WorkThread("check_fill", 1), 0, TimeUnit.SECONDS);
            }
            onKeyCancel();
        } else if (position == Config.POSITION_DN_UNION_PARAMS) {
            BusToast.showToast(BusApp.getInstance().getApplicationContext(), "正在更新请稍后", true);
            DownloadUnionPayRequest payRequest = new DownloadUnionPayRequest();
            payRequest.setForceUpdate(true);
            payRequest.getDisposable();
            onKeyCancel();
        } else if (position == Config.HISTROY) {

            ThreadFactory.getScheduledPool().execute(new WorkThread("history"));
        }
    }

    /**
     * 导出数据
     *
     * @param day 天数
     */
    private void export(int day) {
        String scanPath = "/storage/sdcard1/scan";
        String cardPath = "/storage/sdcard1/card";
        String unionPath = "/storage/sdcard1/union";
        WriteRecordToSD.getInstance().writer(day, scanPath, cardPath, unionPath);
    }

    @Override
    public void onKeyCancel() {

        if (recyclerView.getVisibility() == View.VISIBLE) {
            setViewStatus(recyclerView, false);
        }

        if (main_record.getVisibility() == View.VISIBLE) {
            setViewStatus(main_record, false);
            childViewShow = false;
        }

        if (main_cnt.getVisibility() == View.VISIBLE) {
            setViewStatus(main_cnt, false);
            childViewShow = false;
        }

        if (main_info.getVisibility() == View.VISIBLE) {
            setViewStatus(main_info, false);
            childViewShow = false;
        }
    }

    private void showCnt(CntEntity cntEntity) {
        ic_card_swipe_cnt.setText(cntEntity.ic_card_swipe_cnt[0]);
        ic_card_amount_cnt.setText(cntEntity.ic_card_swipe_cnt[1]);
        ic_card_up_status.setText(cntEntity.ic_card_swipe_cnt[2]);

        scan_cnt.setText(cntEntity.scan_cnt[0]);
        scan_amount_cnt.setText(cntEntity.scan_cnt[1]);
        scan_up_status.setText(cntEntity.scan_cnt[2]);

        union_cnt.setText(cntEntity.union_cnt[0]);
        union_amount_cnt.setText(cntEntity.union_cnt[1]);
        union_un_status.setText(cntEntity.union_cnt[2]);

        sum_cnt.setText(cntEntity.cnt[0]);
        sum_amount_cnt.setText(cntEntity.cnt[1]);
        sum_up_status.setText(cntEntity.cnt[2]);

        time_cnt.setText(cntEntity.time_cnt[0]);
        time_amount_cnt.setText(cntEntity.time_cnt[1]);
    }


    /**
     * @param view   view
     * @param isShow 是否显示
     */
    private void setViewStatus(View view, boolean isShow) {
        if (isShow) {
            //显示
            view.setVisibility(View.VISIBLE);
            view.startAnimation(mShowAnimation);
        } else {
            //隐藏
            view.setVisibility(View.GONE);
            view.startAnimation(mHiddenAnimation);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hasNetWork(AppUtil.getNetWorkState(getApplicationContext()));
    }

    private void hasNetWork(boolean b) {
        net_status.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    /**
     * 补采通知
     *
     * @param code .
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void fillPush(int code) {
        long supplementaryMiningCnt = BusApp.getPosManager().getSupplementaryMiningCnt();
        if (code == QRCode.FILL_PUSH_ING) {
            fill_status.setVisibility(View.VISIBLE);
            long supplementaryMining = DBManager.getSupplementaryMining();
            fill_status.setText(supplementaryMiningCnt + "/" + supplementaryMining);
        } else {
            fill_status.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.fill_end, 0, 0, 0);
            fill_status.setText(String.format("%1$d/补采结束", supplementaryMiningCnt));
        }
    }

    /**
     * 显示dialog
     */
    private void showDialog() {
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("tip");
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        waitDialog.show(mFragTransaction, "tip");
        waitDialog.start();
    }

    /**
     * 关闭dialog
     */
    private void closeDialog() {
        if (waitDialog != null && waitDialog.isAdded()) {
            waitDialog.disDialog();
            Log.d("BaseActivity",
                    "closeDialog(BaseActivity.java:599)关闭");
        }
        Log.d("BaseActivity",
                "closeDialog(BaseActivity.java:603)=============");
    }


    @Override
    public void response(boolean success, ResponseMessage response) {
        BusToast.showToast(getApplicationContext(), response.getMsg(),
                response.getStatus()
                        == ResponseMessage.SUCCESSFUL || response.getStatus()
                        == ResponseMessage.SUCCESS || response.getStatus()
                        == ResponseMessage.NOUPDATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.cancel();
        onKeyCancel();
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }
}

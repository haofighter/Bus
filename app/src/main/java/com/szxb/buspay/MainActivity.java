package com.szxb.buspay;

import android.graphics.Color;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.szxb.buspay.db.entity.bean.MainEntity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.interfaces.OnReceiverMessageListener;
import com.szxb.buspay.module.BaseActivity;
import com.szxb.buspay.module.WeakHandler;
import com.szxb.buspay.task.card.lw.LoopCardThread_CY;
import com.szxb.buspay.task.card.taian.LoopCardThread_TA;
import com.szxb.buspay.task.card.zhaoyuan.LoopCardThread_ZY;
import com.szxb.buspay.task.card.zibo.LoopCardThread;
import com.szxb.buspay.task.scan.LoopScanThread;
import com.szxb.buspay.task.thread.ThreadFactory;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.Config;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.buspay.util.tip.BusToast;

import java.util.concurrent.TimeUnit;

import static com.szxb.buspay.util.AppUtil.sp2px;
import static com.szxb.buspay.util.Util.fen2Yuan;

public class MainActivity extends BaseActivity implements OnReceiverMessageListener {

    private WeakHandler.MyHandler mHandler;
    private TextView time, station_name, prices, version_name, bus_no;
    private TextView sign_time, sign_version, sign_bus_no;

    @Override
    protected int rootView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        time = (TextView) findViewById(R.id.currentTime);
        station_name = (TextView) findViewById(R.id.station_name);
        prices = (TextView) findViewById(R.id.prices);
        version_name = (TextView) findViewById(R.id.version_name);
        bus_no = (TextView) findViewById(R.id.bus_no);
        sign_time = (TextView) findViewById(R.id.sign_time);
        sign_version = (TextView) findViewById(R.id.sign_version);
        sign_bus_no = (TextView) findViewById(R.id.sign_bus_no);

    }

    @Override
    protected void initData() {
        mHandler = new WeakHandler.MyHandler(this);
        initDate();
        initDatas();
        ThreadFactory.getScheduledPool().executeCycle(new LoopScanThread(), 1000, 200, "loop_scan", TimeUnit.MILLISECONDS);
        boolean isSuppIC = BusApp.getPosManager().isSuppIcPay();
        if (isSuppIC) {
            String appId = BusApp.getPosManager().getAppId();
            ThreadFactory.getScheduledPool().executeCycle(
                    TextUtils.equals(appId, "10000009") ? new LoopCardThread()//淄博
                            : TextUtils.equals(appId, "10000010") ? new LoopCardThread_CY()//莱芜长运
                            : TextUtils.equals(appId, "10000098") ? new LoopCardThread_TA()//泰安
                            : TextUtils.equals(appId, "10000011") ? new LoopCardThread_ZY() ://招远
                            new LoopCardThread()
                    , 1000, 200, "loop_ic", TimeUnit.MILLISECONDS);
        }

    }

    private void initDatas() {
        String driverNo = BusApp.getPosManager().getDriverNo();
        if (TextUtils.equals(driverNo, String.format("%08d", 0))) {
            main_sign.setVisibility(View.VISIBLE);
        }
        setPrices();
        station_name.setText(BusApp.getPosManager().getChinese_name());
        sign_time.setText(DateUtil.getCurrentDate("yyyy-MM-dd"));
        version_name.setText(String.format("[%1$s]", AppUtil.getVersionName(getApplicationContext())));
        sign_version.setText(String.format("[%1$s]\n%2$s", AppUtil.getVersionName(getApplicationContext()), BuildConfig.BIN_NAME));
        sign_bus_no.setText(BusApp.getPosManager().getBusNo());
        bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                BusApp.getPosManager().getBusNo(), BusApp.getPosManager().getDriverNo()));
    }

    /**
     * 设置票价
     */
    private void setPrices() {
        //票价回显
        Util.echo();
        String text = "票价:";
        String pricesStr = String.format("%1$s", fen2Yuan(BusApp.getPosManager().getBasePrice()));
        String text2 = "元";
        SpannableString ss = new SpannableString(text + pricesStr + text2);
        ss.setSpan(new AbsoluteSizeSpan(sp2px(getApplicationContext(), 35)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#EE4000")), text.length(), text.length() + pricesStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(sp2px(getApplicationContext(), 70)), text.length(), text.length() + pricesStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(sp2px(getApplicationContext(), 35)), text.length() + pricesStr.length(), text.length() + pricesStr.length() + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        prices.setText(ss);
    }

    @Override
    protected void message(QRScanMessage message) {
        switch (message.getResult()) {
            case QRCode.REFRESH_VIEW:
                setPrices();
                station_name.setText(BusApp.getPosManager().getChinese_name());
                bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                        BusApp.getPosManager().getBusNo(), BusApp.getPosManager().getDriverNo()));
                sign_bus_no.setText(BusApp.getPosManager().getBusNo());
                break;
            case QRCode.SIGN:
                String driverNo = BusApp.getPosManager().getDriverNo();
                main_sign.setVisibility(TextUtils.equals(driverNo, String.format("%08d", 0)) ? View.VISIBLE : View.GONE);
                main_sign.startAnimation(TextUtils.equals(driverNo, String.format("%08d", 0)) ? mShowAnimation : mHiddenAnimation);
                bus_no.setText(String.format("车辆号:%1$s\n司机号:%2$s",
                        BusApp.getPosManager().getBusNo(), driverNo));
                break;
            case QRCode.RES_LAUNCHER:
                version_name.setTextColor(getApplicationContext().getResources().getColor(R.color.colorAccent));
                version_name.setText(String.format("%1$s!", version_name.getText().toString()));
                break;
            case QRCode.REFRESH_QR_CODE:
                SoundPoolUtil.play(Config.EC_RE_QR_CODE);
                BusToast.showToast(getApplicationContext(), "请刷新二维码[" + QRCode.REFRESH_QR_CODE + "]", false);
                break;
            case QRCode.QR_ERROR:
                SoundPoolUtil.play(Config.QR_ERROR);
                BusToast.showToast(getApplicationContext(), "二维码有误[" + QRCode.QR_ERROR + "]", false);
                break;
            case QRCode.KEY_CODE:
                setPrices();
                break;
            case QRCode.UPDATE_UNION_PARAMS:

                break;
            default:
                break;
        }
    }

    @Override
    protected void initList() {
        mList.add(new MainEntity("查看刷卡记录"));
        mList.add(new MainEntity("查看扫码记录"));
        mList.add(new MainEntity("查看银联卡记录"));
        mList.add(new MainEntity("查询当天汇总"));
        mList.add(new MainEntity("手动更新参数"));
        mList.add(new MainEntity("数据库导出"));
        mList.add(new MainEntity("日志导出"));
        mList.add(new MainEntity("检测网络"));
        mList.add(new MainEntity("校准时间"));
        mList.add(new MainEntity("导出7天记录"));
        mList.add(new MainEntity("导出1个月记录"));
        mList.add(new MainEntity("导出3个月记录"));
        mList.add(new MainEntity("查看基础信息"));
        mList.add(new MainEntity("手动补传"));
        mList.add(new MainEntity("更新银联参数"));
    }


    private void initDate() {
        DateUtil.setK21Time();
        ThreadFactory.getScheduledPool().executeCycle(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(QRCode.TIMER);
            }
        }, 0, 1, "time", TimeUnit.SECONDS);

    }


    @Override
    public void handlerMessage(Message message) {
        switch (message.what) {
            case QRCode.TIMER:
                String currentTime = String.format("%1$s", DateUtil.getMainTime());
                time.setText(currentTime);
                sign_time.setText(currentTime);
                break;
            default:

                break;
        }
    }

}

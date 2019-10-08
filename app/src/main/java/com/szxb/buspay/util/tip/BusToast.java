package com.szxb.buspay.util.tip;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.R;


/**
 * 作者: Tangren on 2017/7/17
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:自定义Toast
 */

public class BusToast extends Toast {

    private static LayoutInflater mInflater;

    private static Toast mToast;

    private static long firstTime = 0;

    private static long secondTime = 0;

    private static String temStr;

    private TextView textView;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public BusToast(Context context) {
        super(context);
    }

    private static Toast showTopay(Context context, CharSequence text, boolean isOk) {
        Toast toast = new Toast(context);
        mInflater = LayoutInflater.from(context);
        View view = getView(isOk, text);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(LENGTH_SHORT);
        return toast;
    }

    private static View getView(boolean isOk, CharSequence text) {
        View view;
        if (isOk) {
            view = mInflater.inflate(R.layout.view_toast_success, null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(text);
            return view;
        } else {
            view = mInflater.inflate(R.layout.view_toast_fali, null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(text);
            return view;
        }
    }

    /**
     * @param context 上下文
     * @param text    tip文本
     * @param isOk    更新/检测/扣款成功或者失败
     */
    public static void showToast(final Context context, final CharSequence text, final boolean isOk) {
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast = showTopay(BusApp.getInstance(), text, isOk);
                mToast.show();
//                if (mToast == null) {
//                    mToast = showTopay(context, text, isOk);
//                    mToast.show();
//                    firstTime = SystemClock.elapsedRealtime();
//                    Log.d("BusToast",
//                            "run(BusToast.java:88)mToast == null>>>提示" + text);
//                } else {
//                    secondTime = System.currentTimeMillis();
//                    if (text.equals(temStr)) {
//                        if (secondTime - firstTime > Toast.LENGTH_SHORT) {
//                            mToast.show();
//                            Log.d("BusToast",
//                                "run(BusToast.java:95)text.equals(temStr)>>>提示" + text);
//                        }
//                    } else {
//                        temStr = (String) text;
//                        mToast.setView(getView(isOk, text));
//                        mToast.show();
//                        Log.d("BusToast",
//                            "run(BusToast.java:102) temStr = (String) text>>>>"+text);
//                    }
//                }
//                firstTime = secondTime;
            }
        });
    }


}

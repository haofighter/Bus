package com.szxb.buspay.util.tip;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.szxb.buspay.R;

/**
 * 作者：Tangren on 2018-08-30
 * 包名：com.szxb.buspay.util.tip
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class MyToast extends Toast {

    private static MyToast toast;

    private static LayoutInflater mInflater;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public MyToast(Context context) {
        super(context);
    }


    /**
     * 初始化Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     */
    public static void showToast(final Context context, final CharSequence text, final boolean isOk) {
        MainLooper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast2(context,text,isOk);
            }
        });

    }


    /**
     * 初始化Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     */
    public static void showToast2(final Context context, final CharSequence text, final boolean isOk) {
        cancelToast();
        toast = new MyToast(context);
        mInflater = LayoutInflater.from(context);
        View view = getView(isOk, text);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(LENGTH_SHORT);
        toast.setView(view);
        toast.show();
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
     * 隐藏当前Toast
     */
    private static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }


}

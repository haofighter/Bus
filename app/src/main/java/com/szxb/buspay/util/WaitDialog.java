package com.szxb.buspay.util;

import android.annotation.TargetApi;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.szxb.buspay.R;

/**
 * 作者：Tangren on 2018-09-08
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class WaitDialog extends DialogFragment {

    private AnimationDrawable drawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.color.transparent);
        }
        View view = inflater.inflate(R.layout.view_dialog, container, false);
        ImageView progressView = (ImageView) view.findViewById(R.id.progress);
        drawable = (AnimationDrawable) progressView.getBackground();
        drawable.start();
        return view;
    }

    public void start() {
        if (drawable != null && !drawable.isRunning()) {
            drawable.start();
        }
    }

    public void disDialog() {
        if (getDialog() != null && getDialog().isShowing()) {
            getDialog().dismiss();
            if (drawable!=null&&drawable.isRunning()){
                drawable.stop();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStart() {
        super.onStart();
        hideBottom();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void hideBottom() {
        Window window = this.getDialog().getWindow();
        if (window == null) {
            return;
        }
        window.getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}

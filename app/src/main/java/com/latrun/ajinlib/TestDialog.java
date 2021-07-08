package com.latrun.ajinlib;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.LayoutRes;

import com.xy.baselib.ui.dialog.BaseDialog;

import org.jetbrains.annotations.NotNull;

public class TestDialog extends BaseDialog {

    public TestDialog(@NotNull Context context) {
        super(context);
    }

    @Override
    public void initView() {

    }

    @Override
    public int LayoutRes() {
        return R.layout.test_dialog;
    }

    @Override
    public double proportion() {
        return 0.8;
    }

    @Override
    public int Gravity() {
        return Gravity.CENTER;
    }
}

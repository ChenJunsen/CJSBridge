package com.cjs.cjsbridge_ui.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cjs.cjsbridge_ui.R;

/**
 * 自定义顶部Toast弹窗
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/17 0017 11:51
 */
public class TopToast {
    private Toast toast;
    private String text;
    private int duration;
    private TextView toast_tv;

    public TopToast(Context context) {
        init(context);
    }

    private void init(Context context) {
        toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 0);
        View toastView = View.inflate(context, R.layout.layout_top_toast, null);
        toast_tv = toastView.findViewById(R.id.toastText);
        toast.setView(toastView);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (toast_tv != null) {
            toast_tv.setText(text);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        if (toast != null) {
            toast.setDuration(duration);
        }
    }

    public void show() {
        if (toast != null) {
            toast.show();
        }
    }

    public static void showToast(Context context, String msg, int duration) {
        TopToast toast = new TopToast(context);
        toast.setText(msg);
        toast.setDuration(duration);
        toast.show();
    }

    public static void showToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_LONG);
    }
}

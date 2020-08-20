package com.cjs.cjsbridge.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;


/**
 * 快捷显示系统对话框
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/17 0017 16:59
 */
public class MsgDialog {

    /**
     * 但按钮对话框
     *
     * @param activity
     * @param msg
     * @param title
     * @param submitText
     * @param dialogListener
     */
    public static void show1(Activity activity, String msg, String title, final String submitText, final DialogListenerSimple dialogListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(submitText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogListener != null) {
                            dialogListener.onSubmit(dialog);
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }

    public static void show1(Activity activity, String msg, final DialogListenerSimple dialogListener) {
        show1(activity, msg, "提示", "确认", dialogListener);
    }

    public static void show1(Activity activity, String msg) {
        show1(activity, msg, "提示", "确认", null);
    }

    /**
     * 双按钮显示消息
     *
     * @param activity       上下文
     * @param msg            信息文字
     * @param title          标题
     * @param submitText     确认文字
     * @param cancelText     取消文字
     * @param dialogListener 回调监听
     */
    public static void show2(Activity activity, String msg, String title, String submitText, String cancelText, final DialogListener dialogListener) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(submitText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogListener != null) {
                            dialogListener.onSubmit(dialog);
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogListener != null) {
                            dialogListener.onCancel(dialog);
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .create()
                .show();
    }

    /**
     * 双按钮显示消息
     *
     * @param activity       上下文
     * @param msg            信息文字
     * @param dialogListener 回调监听
     */
    public static void show2(Activity activity, String msg, final DialogListener dialogListener) {
        show2(activity, msg, "提示", "确定", "取消", dialogListener);
    }

    public static void show2(Activity activity, String msg) {
        show2(activity, msg, "提示", "确定", "取消", null);
    }

    /**
     * 对话框回调监听器
     *
     * @author JasonChen
     * @email chenjunsen@outlook.com
     * @createTime 2020/8/17 0017 17:02
     */
    public interface DialogListener {
        /**
         * 确认
         *
         * @param dialog
         */
        void onSubmit(DialogInterface dialog);

        /**
         * 取消
         *
         * @param dialog
         */
        void onCancel(DialogInterface dialog);
    }

    /**
     * 简易版回调监听
     *
     * @author JasonChen
     * @email chenjunsen@outlook.com
     * @createTime 2020/8/17 0017 17:03
     */
    public static abstract class DialogListenerSimple implements DialogListener {

        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
        }
    }
}

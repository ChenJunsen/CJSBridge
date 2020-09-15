package com.cjs.cjsbridge.jsi;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.model.Student;
import com.cjs.cjsbridge_common.tools.L;

/**
 * JS交互接口，适用于原生自带的{@link android.webkit.WebView#addJavascriptInterface(Object, String)}方法
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/13 0013 14:18
 */
public class CJSInterface {
    private Activity activity;

    public CJSInterface(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void toast(String params) {
        String msg = "";
        if (!TextUtils.isEmpty(params)) {
            try {
                JSONObject jsonp = JSON.parseObject(params);
                msg = jsonp.getString("msg");
            } catch (Exception e) {
                L.e("参数转换失败:" + e.getMessage());
                e.printStackTrace();
            }
        }
        L.d(String.format("h5 call toast method:[%1$s]", msg));
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void dialog(String params) {
        String title = "";
        String msg = "";
        try {
            JSONObject jsonp = JSON.parseObject(params);
            title = jsonp.getString("title");
            msg = jsonp.getString("msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        L.d(String.format("h5 call dialog method:[%1$s][%2$s]", title, msg));
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    @JavascriptInterface
    public void printStudentInfo(String params) {
        if (!TextUtils.isEmpty(params)) {
            try {
                JSONObject studentObj = JSON.parseObject(params);
                Student student = JSON.toJavaObject(studentObj, Student.class);
                L.d("学生信息打印", student.toString());
                Toast.makeText(activity, "模拟打印学生信息:" + student, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

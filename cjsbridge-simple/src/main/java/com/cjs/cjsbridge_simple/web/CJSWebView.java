package com.cjs.cjsbridge_simple.web;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_common.tools.L;


/**
 * 自定义webView addJavascriptInterface版
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 16:35
 */
public class CJSWebView extends WebView {

    public CJSWebView(Context context) {
        super(context);
        init(context);
    }

    public CJSWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CJSWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CJSWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public CJSWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init(context);
    }

    private void init(Context context) {
        L.i(">>>>>>>>>>>>>>>>> CJSWebView initializing <<<<<<<<<<<<<<<<<");

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭浏览器缓存
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            webSettings.setAllowFileAccessFromFileURLs(true);
//            webSettings.setAllowUniversalAccessFromFileURLs(true);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);//开启内嵌日志调试工具
        }*/
        setWebViewClient(new CJSWebClient((Activity) getContext()));
        setWebChromeClient(new CJSWebChromeClient((Activity) getContext()));
    }

    /**
     * 原生调用H5方法
     *
     * @param methodName 方法名字
     * @param params     附带参数
     */
    public void evaluateJavascript(String methodName, JSONObject params,ValueCallback callback) {
        String jsFmt = "javascript:%1$s(%2$s)";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(String.format(jsFmt, methodName, params.toJSONString()), callback);
        }
    }
}

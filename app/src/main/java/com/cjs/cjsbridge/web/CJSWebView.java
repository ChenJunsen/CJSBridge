package com.cjs.cjsbridge.web;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.cjs.cjsbridge.jsi.CJSInterface;
import com.cjs.cjsbridge.tools.L;

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
        //注入JS插件，并且给这个插件取一个别名CJSI,在H5端通过这个别名来调用相关方法
        addJavascriptInterface(new CJSInterface((Activity) context), "CJSI");
        L.d("注入JS插件:CJSI");

        WebSettings webSettings=getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭浏览器缓存
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            webSettings.setAllowFileAccessFromFileURLs(true);
//            webSettings.setAllowUniversalAccessFromFileURLs(true);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webSettings.setJavaScriptEnabled(true);
        setWebViewClient(new CJSWebClient());
    }
}

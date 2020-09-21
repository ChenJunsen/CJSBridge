package com.cjs.cjsbridge_prompt.web;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_common.scheme.CJScheme;
import com.cjs.cjsbridge_common.tools.L;
import com.cjs.cjsbridge_common.tools.SystemUtil;
import com.cjs.cjsbridge_prompt.core.CJSBridge;
import com.cjs.cjsbridge_prompt.core.exception.CJSBException;
import com.cjs.cjsbridge_prompt.dispatch.CJSActionDispatcher;


/**
 * 自定义WebView 重写onJsPrompt版
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 15:26
 */
public class CJSWebView extends WebView implements CJSActionDispatcher {
    private CJSWebChromeClient cjsWebChromeClient;

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
//        addJavascriptInterface(new CJSInterface((Activity) context), "CJSI");
//        L.d("注入JS插件:CJSI");

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

        cjsWebChromeClient = new CJSWebChromeClient((Activity) getContext());
        cjsWebChromeClient.setCjsActionDispatcher(this);
        setWebViewClient(new CJSWebClient((Activity) getContext()));
        setWebChromeClient(cjsWebChromeClient);

        //可选操作 增加设备UA，方便H5判断当前设备型号
        String uaStr = webSettings.getUserAgentString();
        webSettings.setUserAgentString(uaStr + "/" + SystemUtil.getSystemModel());
    }

    /**
     * 原生调用H5方法
     *
     * @param methodName 方法名字
     * @param params     附带参数
     */
    public void evaluateJavascript(String methodName, JSONObject params, ValueCallback callback) {
        String jsFmt = "javascript:%1$s(%2$s)";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(String.format(jsFmt, methodName, params.toJSONString()), callback);
        }
    }


    /**
     * 分发处理接收到的H5指令和参数
     *
     * @param webView
     * @param cjScheme
     */
    @Override
    public void dispatchH5Action(WebView webView, CJScheme cjScheme) {
        try {
            CJSBridge.getInstance().callNative(webView, cjScheme);
        } catch (CJSBException e) {
            e.printStackTrace();
        }
    }
}

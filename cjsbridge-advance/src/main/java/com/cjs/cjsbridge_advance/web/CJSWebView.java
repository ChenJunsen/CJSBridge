package com.cjs.cjsbridge_advance.web;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.cjs.cjsbridge_advance.core.CJSBridge2;
import com.cjs.cjsbridge_advance.core.exception.CJSBException;
import com.cjs.cjsbridge_advance.dispatch.CJSActionDispatcher;
import com.cjs.cjsbridge_common.scheme.CJScheme;
import com.cjs.cjsbridge_common.tools.SystemUtil;

/**
 * 进阶版WebView
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 17:59
 */
public class CJSWebView extends WebView implements CJSActionDispatcher {

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
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CJSWebViewClient webViewClient = new CJSWebViewClient();
        webViewClient.setCjsActionDispatcher(this);
        setWebViewClient(webViewClient);
        setWebChromeClient(new CJSWebChromeClient((Activity) context));
        //可选操作 增加设备UA，方便H5判断当前设备型号
        String uaStr = webSettings.getUserAgentString();
        webSettings.setUserAgentString(uaStr + "/" + SystemUtil.getSystemModel());
    }


    @Override
    public void dispatchH5Action(WebView webView, CJScheme cjScheme) {
        try {
            CJSBridge2.getInstance().callNative(webView, cjScheme);
        } catch (CJSBException e) {
            e.printStackTrace();
        }
    }
}

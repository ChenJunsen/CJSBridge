package com.cjs.cjsbridge.web;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cjs.cjsbridge.tools.L;

/**
 * 自定义WebViewClient
 * WebViewClient帮助WebView处理各种通知、请求事件的，具体来说包括：
 * <ul>
 *     <li>onLoadResource()</li>
 *     <li>onPageStarted()</li>
 *     <li>onPageFinished()</li>
 *     <li>onReceiveError()</li>
 *     <li>onReceivedHttpAuthRequest()</li>
 * </ul>
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/12 0012 18:03
 */
public class CJSWebClient extends WebViewClient {
    Activity activity;

    public CJSWebClient(Activity activity) {
        this.activity=activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        L.d("拦截到请求地址1:" + url);
        //重写该方法，只需返回false（官方用法），就可以阻止webView在加载url时调用系统浏览器的问题
        return false;
    }

}

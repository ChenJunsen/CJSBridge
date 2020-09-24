package com.cjs.cjsbridge_advance.dispatch;

import android.webkit.JsPromptResult;
import android.webkit.WebView;

import com.cjs.cjsbridge_common.scheme.CJScheme;


/**
 * JS交互事件分发器 适用于通过重写{@link android.webkit.WebChromeClient#onJsPrompt(WebView, String, String, String, JsPromptResult)}来调用方法。
 * 用于将H5的处理逻辑从ChromeClient中剥离出来
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 15:22
 */
public interface CJSActionDispatcher {

    void onPageStarted(WebView webView,String url);

    /**
     * CJSB注入成功后
     * @param webView
     */
    void onJSBridgeInitialized(WebView webView);

    /**
     * 分发处理H5的请求事件
     * @param webView 当前H5页面所在的WebView
     * @param cjScheme 解析好的url信息
     */
    void dispatchH5Action(WebView webView, CJScheme cjScheme);

    void onPageFinished(WebView webView,String url);


}

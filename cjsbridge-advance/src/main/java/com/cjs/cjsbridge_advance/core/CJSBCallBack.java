package com.cjs.cjsbridge_advance.core;

import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_common.tools.L;

import java.lang.ref.WeakReference;

/**
 * 原生回调给H5
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/3 0003 9:40
 */
public class CJSBCallBack {
    private WeakReference<WebView> webViewRef;
    private WebView webView;
    private String sid;

    /**
     * 原生调用H5方法实现回调的字符串模板。
     * 该模板包含两个参数，第一个是sid，第二个是回调的数据(JSON String类型)
     * sid是从解析的自定义url里面拿到的端口号，用于保证异步回调一致。
     * 该方法与CJSBridge.js里面的桥方法对应
     */
    private static final String CALL_BACK_STRING = "javascript:CJSBridge.callBack(%1$s,%2$s)";
    /**
     * 回调成功状态码
     */
    private static final int STATUS_OK = 1;
    /**
     * 回调失败状态码
     */
    private static final int STATUS_FAIL = -1;

    public CJSBCallBack(String sid, WeakReference<WebView> webViewRef) {
        this.webViewRef = webViewRef;
        this.sid = sid;
        if (webViewRef != null) {
            webView = webViewRef.get();
        }
    }

    /**
     * 回调操作
     *
     * @param isSuccess 是否成功 true-成功  false-失败
     * @param message   回调提示信息
     * @param params    回调参数
     */
    public void apply(boolean isSuccess, String message, JSONObject params) {
        //因为回调是个异步操作，所以不确定在达到回调期间，webView是否销毁，所以用的是WeakReference
        if (webView == null) {
            L.e("callBack", "回调给H5的的webView获取失败,回调操作取消");
            return;
        }
        JSONObject callBackParams = new JSONObject();
        if (params == null) {
            params = new JSONObject();
        }
        if (TextUtils.isEmpty(message)) {
            message = isSuccess ? "成功" : "失败";
        }
        callBackParams.put("params", params);
        callBackParams.put("cjsb_status", isSuccess ? STATUS_OK : STATUS_FAIL);
        callBackParams.put("cjsb_msg", message);

        String callBackStr = callBackParams.toJSONString();
        L.d("callBack", "原生回调H5-->SID:" + sid + "     callBackParams:" + callBackStr);
        String url = String.format(CALL_BACK_STRING, sid, callBackStr);
        L.d("callBack", "原生回调H5-->执行脚本:" + url);
        webView.loadUrl(url);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(url,null);
        }*/
    }

    public void apply(boolean isSuccess, JSONObject params) {
        apply(isSuccess, null, params);
    }

    /**
     * 回调操作
     *
     * @param isSuccess 是否成功 true-成功  false-失败
     */
    public void apply(boolean isSuccess) {
        apply(isSuccess, "", null);
    }
}

package com.cjs.cjsbridge_advance.core;

import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_advance.dispatch.CJSActionDispatcher;

import java.util.ArrayList;

/**
 * 内置API插件
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/24 0024 16:41
 */
public class CJSBInnerPlugin implements CJSBH5Plugin {
    /**
     * CJSB初始化
     */
    public static final String ACTION_INIT="cjsb_init";

    @Override
    public ArrayList<String> registeredAction() {
        return new ArrayList<String>(){{
            add(ACTION_INIT);
        }};
    }

    @Override
    public boolean interceptAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        return false;
    }

    @Override
    public void dispatchAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        if(TextUtils.equals(action,ACTION_INIT)){
            if(webView instanceof CJSActionDispatcher){
                ((CJSActionDispatcher) webView).onJSBridgeInitialized(webView);
            }
            cjsbCallBack.apply(true);
        }
    }
}

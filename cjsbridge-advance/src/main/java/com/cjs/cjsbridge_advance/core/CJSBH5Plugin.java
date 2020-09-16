package com.cjs.cjsbridge_advance.core;


import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * JS插件接口
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 15:47
 */
public interface CJSBH5Plugin {

    /**
     * 注册指令
     *
     * @return
     */
    ArrayList<String> registeredAction();

    /**
     * 拦截H5的指令
     *
     * @param webView      当前所在的webView
     * @param action       执行指令
     * @param params       请求参数
     * @param cjsbCallBack 回调
     * @return 返回‘true’表示该指令最多运行到当前插件，之后的插件即使有同名方法也不会走
     */
    boolean interceptAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack);

    /**
     * 分发处理H5的指令
     *
     * @param webView      当前所在的webView
     * @param action       执行指令
     * @param params       请求参数
     * @param cjsbCallBack 回调
     */
    void dispatchAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack);

}

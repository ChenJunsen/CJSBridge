package com.cjs.cjsbridge.jsi.prompt;

import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.tools.SPStoreUtil;
import com.cjs.cjsbridge_prompt.core.CJSBCallBack;
import com.cjs.cjsbridge_prompt.core.CJSBH5Plugin;
import com.cjs.cjsbridge_prompt.core.annotation.JSI;

/**
 * 文件读写插件
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 10:43
 */
public class IOPlugin implements CJSBH5Plugin {

    /**
     * 数据存储
     *
     * @param view
     * @param params
     * @param cjsbCallBack
     */
    @JSI
    public void setStorage(WebView view, JSONObject params, CJSBCallBack cjsbCallBack) {
        SPStoreUtil spStoreUtil = new SPStoreUtil(view.getContext());
        if (params != null) {
            for (String key : params.keySet()) {
                spStoreUtil.put(key, params.get(key));
            }
            spStoreUtil.commit();
            cjsbCallBack.apply(true);
        } else {
            cjsbCallBack.apply(false, "没有接收到需要存储的参数", null);
        }
    }

    /**
     * 数据获取
     *
     * @param view
     * @param params
     * @param cjsbCallBack
     */
    @JSI
    public void getStorage(WebView view, JSONObject params, CJSBCallBack cjsbCallBack) {
        SPStoreUtil spStoreUtil = new SPStoreUtil(view.getContext());
        if (params != null) {
            String key = params.getString("key");
            JSONObject callback = new JSONObject();
            callback.put("value", spStoreUtil.get(key));
            cjsbCallBack.apply(true, callback);
        }else {
            cjsbCallBack.apply(false, "没有接收到需要查询的key", null);
        }
    }
}

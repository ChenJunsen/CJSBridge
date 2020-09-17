package com.cjs.cjsbridge.jsi.adv;

import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.tools.SPStoreUtil;
import com.cjs.cjsbridge_advance.core.CJSBCallBack;
import com.cjs.cjsbridge_advance.core.CJSBH5Plugin;

import java.util.ArrayList;

/**
 * AdvIOPlugin
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/17 0017 11:52
 */
public class AdvIOPlugin implements CJSBH5Plugin {
    @Override
    public ArrayList<String> registeredAction() {
        ArrayList<String> list = new ArrayList<>();
        list.add(CJSH5Action.SET_STORAGE);
        list.add(CJSH5Action.GET_STORAGE);
        return list;
    }

    @Override
    public boolean interceptAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        return false;
    }

    @Override
    public void dispatchAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        if(TextUtils.equals(action,CJSH5Action.SET_STORAGE)){
            SPStoreUtil spStoreUtil = new SPStoreUtil(webView.getContext());
            if (params != null) {
                for (String key : params.keySet()) {
                    spStoreUtil.put(key, params.get(key));
                }
                spStoreUtil.commit();
                cjsbCallBack.apply(true);
            } else {
                cjsbCallBack.apply(false, "没有接收到需要存储的参数", null);
            }
        }else if(TextUtils.equals(action,CJSH5Action.GET_STORAGE)){
            SPStoreUtil spStoreUtil = new SPStoreUtil(webView.getContext());
            if (params != null) {
                String key = params.getString("key");
                JSONObject callback = new JSONObject();
                callback.put("value", spStoreUtil.get(key));
                cjsbCallBack.apply(true, callback);
            } else {
                cjsbCallBack.apply(false, "没有接收到需要查询的key", null);
            }
        }
    }
}

package com.cjs.cjsbridge.jsi.adv;

import android.text.TextUtils;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_advance.core.CJSBCallBack;
import com.cjs.cjsbridge_advance.core.CJSBH5Plugin;
import com.cjs.cjsbridge_ui.toast.TopToast;

import java.util.ArrayList;


/**
 * AdvRemindPlugin
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/17 0017 14:51
 */
public class AdvRemindPlugin implements CJSBH5Plugin {

    @Override
    public ArrayList<String> registeredAction() {
        ArrayList<String> list = new ArrayList<>();
        list.add(CJSH5Action.TOAST);
        return list;
    }

    @Override
    public boolean interceptAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        if (TextUtils.equals(action, CJSH5Action.TOAST)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void dispatchAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        if (TextUtils.equals(action, CJSH5Action.TOAST)) {
            TopToast.showToast(webView.getContext(), params.getString("msg"));
            cjsbCallBack.apply(true,"TopToast",null);
        }
    }
}

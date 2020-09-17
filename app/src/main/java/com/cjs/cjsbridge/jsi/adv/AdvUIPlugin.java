package com.cjs.cjsbridge.jsi.adv;

import android.app.Activity;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_advance.core.CJSBCallBack;
import com.cjs.cjsbridge_advance.core.CJSBH5Plugin;
import com.cjs.cjsbridge_ui.dialog.MsgDialog;

import java.util.ArrayList;

/**
 * 进阶版H5交互指令插件
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 17:12
 */
public class AdvUIPlugin implements CJSBH5Plugin {

    @Override
    public ArrayList<String> registeredAction() {
        ArrayList<String> actions = new ArrayList<>();
        actions.add(CJSH5Action.TOAST);
        actions.add(CJSH5Action.DIALOG);
        return actions;
    }

    @Override
    public boolean interceptAction(WebView webView, String action, JSONObject params, CJSBCallBack cjsbCallBack) {
        return false;
    }

    @Override
    public void dispatchAction(WebView webView, String action, JSONObject params, final CJSBCallBack cjsbCallBack) {

        switch (action) {
            case CJSH5Action.TOAST:
                String msg0 = params.getString("msg");
                Toast.makeText(webView.getContext(), msg0, Toast.LENGTH_LONG).show();
//                MsgDialog.show1((Activity) webView.getContext(),msg0);
                cjsbCallBack.apply(true, "normalToast", null);
                break;
            case CJSH5Action.DIALOG:
                String title = "提示";
                if (params.containsKey("title")) {
                    title = params.getString("title");
                }
                String submitText = "确定";
                if (params.containsKey("submitText")) {
                    submitText = params.getString("submitText");
                }
                String cancelText = "取消";
                if (params.containsKey("cancelText")) {
                    cancelText = params.getString("cancelText");
                }
                String msg = params.getString("msg");
                int buttons = 1;
                if (params.containsKey("buttons")) {
                    buttons = params.getIntValue("buttons");
                }
                if (buttons == 2) {
                    MsgDialog.show2(
                            (Activity) webView.getContext(),
                            msg,
                            title,
                            submitText,
                            cancelText,
                            new MsgDialog.DialogListener() {

                                @Override
                                public void onSubmit(DialogInterface dialog) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("click", "submit");
                                    cjsbCallBack.apply(true, jsonObject);
                                }

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("click", "cancel");
                                    cjsbCallBack.apply(true, jsonObject);
                                }
                            }
                    );
                } else {
                    MsgDialog.show1(
                            (Activity) webView.getContext(),
                            msg, title, submitText,
                            new MsgDialog.DialogListenerSimple() {
                                @Override
                                public void onSubmit(DialogInterface dialog) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("click", "submit");
                                    cjsbCallBack.apply(true, jsonObject);
                                }
                            }
                    );
                }
                break;
        }
    }
}

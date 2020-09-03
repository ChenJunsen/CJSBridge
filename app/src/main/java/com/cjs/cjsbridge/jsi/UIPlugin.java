package com.cjs.cjsbridge.jsi;

import android.app.Activity;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_prompt.core.CJSBCallBack;
import com.cjs.cjsbridge_prompt.core.CJSBH5Plugin;
import com.cjs.cjsbridge_prompt.core.annotation.JSI;
import com.cjs.cjsbridge_ui.dialog.MsgDialog;

/**
 * H5交互插件(UI模块)(适用于{@link com.cjs.cjsbridge_prompt.core.CJSBridge})
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/3 0003 10:50
 */
public class UIPlugin implements CJSBH5Plugin {
    /**
     * 吐司
     *
     * @param view
     * @param params
     * @param callBack
     */
    @JSI
    public void toast(WebView view, JSONObject params, CJSBCallBack callBack) {
        String msg = params.getString("msg");
        String duration = params.getString("duration");
        int dur = Toast.LENGTH_SHORT;
        if ("long".equalsIgnoreCase(duration)) {
            dur = Toast.LENGTH_LONG;
        }
        Toast.makeText(view.getContext(), msg, dur).show();
        callBack.apply(true);
    }

    /**
     * 对话框
     *
     * @param view
     * @param params
     * @param callBack
     */
    @JSI
    public void dialog(WebView view, JSONObject params, final CJSBCallBack callBack) {
        String title="提示";
        if(params.containsKey("title")){
            title=params.getString("title");
        }
        String submitText="确定";
        if(params.containsKey("submitText")){
            submitText=params.getString("submitText");
        }
        String cancelText="取消";
        if(params.containsKey("cancelText")){
            cancelText=params.getString("cancelText");
        }
        String msg=params.getString("msg");
        int buttons = 1;
        if(params.containsKey("buttons")){
            buttons=params.getIntValue("buttons");
        }
        if(buttons==2){
            MsgDialog.show2(
                    (Activity) view.getContext(),
                    msg,
                    title,
                    submitText,
                    cancelText,
                    new MsgDialog.DialogListener(){

                        @Override
                        public void onSubmit(DialogInterface dialog) {
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("click","submit");
                            callBack.apply(true,jsonObject);
                        }

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("click","submit");
                            callBack.apply(true,jsonObject);
                        }
                    }
            );
        }else{
            MsgDialog.show1(
                    (Activity) view.getContext(),
                    msg, title, submitText,
                    new MsgDialog.DialogListenerSimple() {
                        @Override
                        public void onSubmit(DialogInterface dialog) {
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("click","submit");
                            callBack.apply(true,jsonObject);
                        }
                    }
            );
        }
    }
}

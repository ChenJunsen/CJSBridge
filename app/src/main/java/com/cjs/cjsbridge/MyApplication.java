package com.cjs.cjsbridge;

import android.app.Application;

import com.cjs.cjsbridge.jsi.adv.AdvUIPlugin;
import com.cjs.cjsbridge.jsi.prompt.IOPlugin;
import com.cjs.cjsbridge.jsi.prompt.UIPlugin;
import com.cjs.cjsbridge_advance.core.CJSBridge2;
import com.cjs.cjsbridge_prompt.core.CJSBridge;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化桥，注册插件
        CJSBridge.getInstance().addJSInterface("CJSBUI", UIPlugin.class);
        CJSBridge.getInstance().addJSInterface("CJSBIO", IOPlugin.class);


        //进阶版插件注册
        CJSBridge2.getInstance().addJSInterface(new AdvUIPlugin());
    }
}

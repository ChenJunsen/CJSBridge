package com.cjs.cjsbridge;

import android.app.Application;

import com.cjs.cjsbridge.core.CJSBridge;
import com.cjs.cjsbridge.jsi.UIPlugin;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化桥，注册插件
        CJSBridge.getInstance().addJSInterface("CJSBUI", UIPlugin.class);
    }
}

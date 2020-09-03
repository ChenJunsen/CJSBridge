package com.cjs.cjsbridge_prompt.core;


import com.cjs.cjsbridge_prompt.core.annotation.JSI;

/**
 * JS插件接口。
 * 方法格式规定(可自定义，需要与CJSBridge里面的解析保持一致):
 * 1、需要{@link JSI}注解
 * 2、方法名格式为 f(WebView view,JSONObject params,CallBack callback)
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 17:59
 */
public interface CJSBH5Plugin {

}

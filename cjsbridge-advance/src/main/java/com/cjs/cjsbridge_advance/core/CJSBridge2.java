package com.cjs.cjsbridge_advance.core;

import android.webkit.WebView;

import androidx.collection.ArraySet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge_advance.core.exception.CJSBException;
import com.cjs.cjsbridge_common.scheme.CJScheme;
import com.cjs.cjsbridge_common.tools.L;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * 进阶版JSBridge
 * 1、注册、注销插件
 * 2、插件拦截器
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 17:10
 */
public class CJSBridge2 {

    private static volatile CJSBridge2 instance;

    private ArraySet<CJSBH5Plugin> injectedPlugins;


    /**
     * 单例模式获取一个桥实例
     *
     * @return
     */
    public static CJSBridge2 getInstance() {
        if (instance == null) {
            synchronized (CJSBridge2.class) {
                if (instance == null) {
                    instance = new CJSBridge2();
                }
            }
        }
        return instance;
    }

    private CJSBridge2() {
        //初始化插件集合
        injectedPlugins = new ArraySet<>();
    }

    /**
     * 注册插件
     *
     * @param plugin
     */
    public void addJSInterface(CJSBH5Plugin plugin) {
        if (plugin != null) {
            injectedPlugins.add(plugin);
            L.d("InjectPlugin", "成功注入H5插件：" + plugin.getClass().getName());
        } else {
            L.e("InjectPlugin", "注入失败,插件不能为空");
        }
    }

    /**
     * 注销插件
     *
     * @param plugin
     */
    public void removeJSInterface(CJSBH5Plugin plugin) {
        if (plugin != null) {
            boolean res = injectedPlugins.remove(plugin);
            if (res) {
                L.d("RemovePlugin", "成功注销名为" + plugin.getClass().getName() + "的插件");
            } else {
                L.e("RemovePlugin", "注销失败:" + plugin.getClass().getName());
            }
        } else {
            L.e("RemovePlugin", "注销失败:插件不能为空");
        }
    }


    /**
     * H5调用原生方法
     *
     * @param webView
     * @param cjScheme
     */
    public void callNative(WebView webView, CJScheme cjScheme) throws CJSBException {
        String methodName = cjScheme.getMethodName();
        if (injectedPlugins != null && injectedPlugins.size() > 0) {
            boolean hasFoundMethods = false;
            //倒序遍历，实现拦截功能
            for (int i = injectedPlugins.size() - 1; i >= 0; i--) {
                CJSBH5Plugin h5plugin = injectedPlugins.valueAt(i);
                List<String> registerActionList = h5plugin.registeredAction();
                if (registerActionList != null && registerActionList.size() > 0) {
                    if (registerActionList.contains(methodName)) {
                        hasFoundMethods = true;
                        try {
                            JSONObject jsonP = JSON.parseObject(cjScheme.getParams());
                            CJSBCallBack callBack = new CJSBCallBack(cjScheme.getSid() + "", new WeakReference<>(webView));
                            //查询是否需要拦截此插件的该方法
                            boolean isIntercept = h5plugin.interceptAction(webView, methodName, jsonP, callBack);
                            //将具体的执行操作分发到具体插件里面
                            h5plugin.dispatchAction(webView, methodName, jsonP, callBack);
                            if (isIntercept) {
                                //如果需要拦截，那么之后的所有插件里面的同名方法都不会走了
                                L.d(methodName + "在" + h5plugin.getClass().getName() + "中被拦截了");
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new CJSBException("H5调用原生方法失败:" + e.getMessage());
                        }
                    }
                }
            }
            if (!hasFoundMethods) {
                L.w("没有找到名为:" + methodName + "的方法");
            }
        } else {
            L.e("调用H5插件失败，还没有注册插件，请先调用addJSInterface方法初始化插件");
        }
    }
}

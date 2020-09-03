package com.cjs.cjsbridge.core;


import android.text.TextUtils;
import android.webkit.WebView;

import androidx.collection.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.core.annotation.JSI;
import com.cjs.cjsbridge.core.exception.CJSBException;
import com.cjs.cjsbridge.core.exception.CJSBNoSuchMethodException;
import com.cjs.cjsbridge.scheme.CJScheme;
import com.cjs.cjsbridge.tools.L;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;


/**
 * 原生JS桥核心库
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 17:59
 */
public class CJSBridge {

    private static CJSBridge instance;

    /**
     * 注册的H5插件集合，这里采用安卓独有的数据结构ArrayMap,提升内存使用效率。
     * 但是注意这个类如果以androidX编译会存在androidX版本和android.util版本。
     * 后者只支持4.4以上的编译环境，低于该环境会报警告。前者不会。
     */
    private ArrayMap<String, Class<? extends CJSBH5Plugin>> injectedPlugins;

    /**
     * 所有注入的H5原生交互方法
     */
    private ArrayMap<String, ArrayMap<String, Method>> injectedMethods;

    /**
     * 单例模式获取一个桥实例
     *
     * @return
     */
    public static CJSBridge getInstance() {
        if (instance == null) {
            synchronized (CJSBridge.class) {
                if (instance == null) {
                    instance = new CJSBridge();
                }
            }
        }
        return instance;
    }

    private CJSBridge() {
        //初始化插件集合
        injectedPlugins = new ArrayMap<>();
        injectedMethods = new ArrayMap<>();
    }

    /**
     * 添加注入一个H5交互插件
     *
     * @param bridgeName 插件别名，提供给H5端调用
     * @param cls        原生对应插件类
     */
    public void addJSInterface(String bridgeName, Class<? extends CJSBH5Plugin> cls) {
        if (!TextUtils.isEmpty(bridgeName)) {
            if (!injectedPlugins.containsKey(bridgeName)) {
                injectedPlugins.put(bridgeName, cls);
                injectedMethods.put(bridgeName, getMethodsInCls(cls));
                L.d("InjectPlugin", "成功注入H5插件：" + bridgeName + "   " + cls.getName());
            } else {
                L.i("InjectPlugin", "已存在名为" + bridgeName + "的插件，注入操作取消");
            }
        } else {
            L.e("InjectPlugin", "注入失败,bridgeName不能为空");
        }
    }

    /**
     * 移除一个H5交互插件
     *
     * @param bridgeName 插件别名
     */
    public void removeJSInterface(String bridgeName) {
        if (!TextUtils.isEmpty(bridgeName)) {
            Class cls = injectedPlugins.remove(bridgeName);
            injectedMethods.remove(bridgeName);
            if (cls == null) {
                L.w("RemovePlugin", "移除失败,不存在名为" + bridgeName + "的插件");
            } else {
                L.d("RemovePlugin", "成功移除名为" + bridgeName + "的插件");
            }
        } else {
            L.e("RemovePlugin", "移除失败,bridgeName不能为空");
        }
    }

    /**
     * 解析cls里面符合要求的方法集合
     *
     * @param cls
     * @return
     */
    private ArrayMap<String, Method> getMethodsInCls(Class<? extends CJSBH5Plugin> cls) {
        ArrayMap<String, Method> methods = new ArrayMap<>();
        Method[] declaredMethods = cls.getDeclaredMethods();//所有自身(不带父类)的private、protected、public方法
        L.d("---------------------开始进行方法装包:" + cls.getName() + "----------------------");
        for (Method declaredMethod : declaredMethods) {
            //declaredMethod.getModifiers() & Modifier.PUBLIC) == 1
            String mName = declaredMethod.getName();
            if (!TextUtils.isEmpty(mName) && (declaredMethod.getAnnotation(JSI.class) != null)) {//只筛选JSI方法
                Class[] pTypes = declaredMethod.getParameterTypes();
                //只筛选三个参数，并且前三个参数为WebView,JSONObject,CallBack类型的方法
                if (pTypes.length == 3 && pTypes[0] == WebView.class && pTypes[1] == JSONObject.class && pTypes[2] == CJSBCallBack.class) {
                    methods.put(mName, declaredMethod);
                    L.d("装包:" + mName);
                } else {
                    L.w("发现JSI注解方法，但是格式异常，取消装包:" + mName);
                }
            }
        }
        L.d("---------------------方法装包结束:" + cls.getName() + "-------------------------");
        return methods;
    }


    /**
     * H5调用原生方法
     *
     * @param webView
     * @param cjScheme
     */
    public void callNative(WebView webView, CJScheme cjScheme) throws CJSBException {
        String bridgeName = cjScheme.getBridgeName();
        String methodName = cjScheme.getMethodName();
        if (!TextUtils.isEmpty(bridgeName) && !TextUtils.isEmpty(methodName)) {
            if (!injectedPlugins.containsKey(bridgeName)) {
                throw new CJSBNoSuchMethodException("没有找到" + bridgeName + "这个桥");
            }
            ArrayMap<String, Method> bridgeMethods = injectedMethods.get(bridgeName);
            if (bridgeMethods.containsKey(methodName)) {
                Method method = bridgeMethods.get(methodName);
                Class<? extends CJSBH5Plugin> cls = injectedPlugins.get(bridgeName);
                try {
                    method.setAccessible(true);
                    JSONObject jsonP = new JSONObject();
                    try {
                        jsonP = JSON.parseObject(cjScheme.getParams());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    method.invoke(cls.newInstance(), webView, jsonP, new CJSBCallBack(cjScheme.getSid() + "", new WeakReference<>(webView)));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CJSBException("反射调用" + methodName + "失败:" + e.getMessage());
                }
            } else {
                throw new CJSBNoSuchMethodException(bridgeName + "里面没有名为" + methodName + "的方法");
            }
        } else {
            L.e("执行方法名字不能为空!");
        }
    }

}

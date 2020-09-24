package com.cjs.cjsbridge_advance.web;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cjs.cjsbridge_advance.core.CJSBridge2;
import com.cjs.cjsbridge_advance.core.exception.CJSBException;
import com.cjs.cjsbridge_advance.dispatch.CJSActionDispatcher;
import com.cjs.cjsbridge_common.scheme.CJScheme;
import com.cjs.cjsbridge_common.scheme.CJSchemeParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * 进阶版WebClient
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 17:54
 */
public class CJSWebViewClient extends WebViewClient {

    private CJSActionDispatcher cjsActionDispatcher;

    public void setCjsActionDispatcher(CJSActionDispatcher cjsActionDispatcher) {
        this.cjsActionDispatcher = cjsActionDispatcher;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        CJScheme cjScheme = CJSchemeParser.parse(url);
        //只拦截指定地scheme
        if (TextUtils.equals(cjScheme.getScheme(), CJSchemeParser.CJSB_BRIDGE_SCHEME)) {
            if (cjsActionDispatcher != null) {
                //分发逻辑
                cjsActionDispatcher.dispatchH5Action(view, cjScheme);
            }
            //返回true,表示拦截这个url,这样不会走浏览器打开页面地行为了
            return true;
        } else {
            //return super.shouldOverrideUrlLoading(view, url);
            //官方用法，只需简单地返回false，可以阻止浏览器默认行为，如唤醒系统浏览器加载页面
            return false;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //从asset文件夹中读出对应的JS桥文件，然后注入webView当中
        //这样做，可以使得H5端在创建页面的时候不用手动引入相关的js文件，实现对H5端的透明化
        try {
            InputStream is = view.getContext().getAssets()
                    .open("advance/CJSBridge2.js");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String js = new String(buffer);
            executeJavascript(view, js);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CJSBException e) {
            e.printStackTrace();
        }
        super.onPageStarted(view, url, favicon);
        if(cjsActionDispatcher!=null){
            cjsActionDispatcher.onPageStarted(view,url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(cjsActionDispatcher!=null){
            cjsActionDispatcher.onPageFinished(view, url);
        }
    }


    private void executeJavascript(WebView webView, String js) throws CJSBException {
        CJSBridge2.callH5(webView, js);
    }
}

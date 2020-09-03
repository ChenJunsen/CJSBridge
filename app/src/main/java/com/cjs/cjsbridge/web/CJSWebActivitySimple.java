package com.cjs.cjsbridge.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.R;
import com.cjs.cjsbridge.jsi.CJSInterface;
import com.cjs.cjsbridge_common.tools.L;
import com.cjs.cjsbridge_simple.web.CJSWebView;
import com.cjs.cjsbridge_ui.head.CJSHeadBar;

/**
 * 采用最原始的addJavascriptInterface进行交互的Web页面
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/3 0003 16:10
 */
public class CJSWebActivitySimple extends AppCompatActivity {

    private CJSHeadBar headBar;//标题栏
    private CJSWebView webView;//Web容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjs_web);
        headBar = findViewById(R.id.headBar);
        webView = findViewById(R.id.webView);

        //注入JS插件，并且给这个插件取一个别名CJSI,在H5端通过这个别名来调用相关方法
        webView.addJavascriptInterface(new CJSInterface(this), "CJSI");
        L.d("注入JS插件:CJSI");

        Intent i = getIntent();
        if (i != null) {
            headBar.setTitle(i.getStringExtra("title"));
            webView.loadUrl(i.getStringExtra("url"));
        }

        //重写返回键方法
        headBar.setOnBackClickListener(new CJSHeadBar.OnBackClickListener() {
            @Override
            public void onBackClick(View v) {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });

        //设置标题栏右键刷新
        headBar.setOnRightClickListener(new CJSHeadBar.OnRightClickListener() {
            @Override
            public void onRightClick(View v) {
                if (webView != null) {
                    webView.reload();
                    Toast.makeText(CJSWebActivitySimple.this, "刷新", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(webView!=null){
            JSONObject params=new JSONObject();
            params.put("msg","原生页面唤醒");
            /*webView.evaluateJavascript("consoleErr", params, new ValueCallback() {
                @Override
                public void onReceiveValue(Object value) {

                }
            });*/
        }
    }
}

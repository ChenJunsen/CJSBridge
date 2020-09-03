package com.cjs.cjsbridge.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.cjs.cjsbridge.R;
import com.cjs.widgets.cjsheadbar.CJSHeadBar;

/**
 * H5容器页面
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/2 0002 15:03
 */
public class CJSWebActivity extends AppCompatActivity {
    private CJSHeadBar headBar;//标题栏
    private CJSWebView2 webView;//Web容器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cjs_web);
        headBar = findViewById(R.id.headBar);
        webView = findViewById(R.id.webView);

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
                    Toast.makeText(CJSWebActivity.this, "刷新", Toast.LENGTH_SHORT).show();
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

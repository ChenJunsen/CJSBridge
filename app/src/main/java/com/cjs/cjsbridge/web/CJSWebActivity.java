package com.cjs.cjsbridge.web;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cjs.cjsbridge.R;
import com.cjs.widgets.cjsheadbar.CJSHeadBar;

public class CJSWebActivity extends AppCompatActivity {
    private CJSHeadBar headBar;
    private CJSWebView webView;

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
    }
}

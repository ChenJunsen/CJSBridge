package com.cjs.cjsbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cjs.cjsbridge.web.CJSWebActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btn_go_to_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_go_to_1 = findViewById(R.id.btn_go_to_1);

        btn_go_to_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CJSWebActivity.class);
                i.putExtra("title", "测试的网页1");
//                i.putExtra("url", "http://www.baidu.com");
//                i.putExtra("url", "http://192.168.43.108:8080/cjsb/func.html?h="+new Random(99999).nextInt());
                i.putExtra("url", "http://172.1.2.62:8080/cjsb/func.html?h="+new Random(999).nextInt());
                startActivity(i);
            }
        });
    }
}

package com.cjs.cjsbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cjs.cjsbridge.tools.UriLogger;
import com.cjs.cjsbridge.web.CJSWebActivityAdvanced;
import com.cjs.cjsbridge.web.CJSWebActivityPrompt;
import com.cjs.cjsbridge.web.CJSWebActivitySimple;
import com.cjs.cjsbridge_ui.toast.TopToast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_go_to_simple, btn_go_to_prompt, btn_uri_print;
    private Button btn_go_to_simple_local, btn_go_to_prompt_local;
    private Button btn_go_to_adv_local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_go_to_simple = findViewById(R.id.btn_go_to_1);
        btn_go_to_prompt = findViewById(R.id.btn_go_to_2);
        btn_uri_print = findViewById(R.id.btn_uri_print);
        btn_go_to_simple_local = findViewById(R.id.btn_go_to_1_local);
        btn_go_to_prompt_local = findViewById(R.id.btn_go_to_2_local);
        btn_go_to_adv_local=findViewById(R.id.btn_go_to_3_local);

        btn_go_to_simple.setOnClickListener(this);
        btn_go_to_prompt.setOnClickListener(this);
        btn_uri_print.setOnClickListener(this);
        btn_go_to_prompt_local.setOnClickListener(this);
        btn_go_to_simple_local.setOnClickListener(this);
        btn_go_to_adv_local.setOnClickListener(this);


        TopToast.showToast(this,"hello", Toast.LENGTH_LONG);
    }


    @Override
    public void onClick(View v) {
        if (v == btn_go_to_simple) {
            Intent i = new Intent(MainActivity.this, CJSWebActivitySimple.class);
            i.putExtra("title", "测试的网页Simple");
            i.putExtra("url", "http://192.168.42.2:8080/cjsb/func1.html?h=" + new Random(100).nextInt(88));
            startActivity(i);
        } else if (v == btn_go_to_prompt) {
            Intent i = new Intent(MainActivity.this, CJSWebActivityPrompt.class);
            i.putExtra("title", "测试的网页Prompt");
            i.putExtra("url", "http://192.168.42.2:8080/cjsb/func2.html?h=" + new Random(100).nextInt(88));
            startActivity(i);
        } else if (v == btn_uri_print) {
            UriLogger.print("ejb0://qq.boom.fest@io:1211/gift/image/total.jpeg?sid=11ff456&ser=F&flag=0019&name=刘昂&msg=今天天气不错");
            UriLogger.print("https://www.baidu.com:8801/src/view/afc?key=&1168-klio89879");
            UriLogger.print("msdn://www.baidu.com/src_key=&1168-klio89879");
        } else if (v == btn_go_to_simple_local) {
            Intent i = new Intent(MainActivity.this, CJSWebActivitySimple.class);
            i.putExtra("title", "测试的网页Simple");
            i.putExtra("url", "file:///android_asset/html/func1.html");
            startActivity(i);
        } else if (v == btn_go_to_prompt_local) {
            Intent i = new Intent(MainActivity.this, CJSWebActivityPrompt.class);
            i.putExtra("title", "测试的网页Prompt");
            i.putExtra("url", "file:///android_asset/html/func2.html");
            startActivity(i);
        }else if(v == btn_go_to_adv_local){
            Intent i = new Intent(MainActivity.this, CJSWebActivityAdvanced.class);
            i.putExtra("title", "测试的网页Advanced");
            i.putExtra("url", "file:///android_asset/html/func3.html");
            startActivity(i);
        }
    }
}

package com.cjs.cjsbridge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cjs.cjsbridge.dialog.MsgDialog;
import com.cjs.cjsbridge.tools.L;
import com.cjs.cjsbridge.web.CJSWebActivity;
import com.cjs.sdk.cjsnfc.NFCScanningActivity;
import com.cjs.sdk.cjsnfc.nt.NfcMainNewActivity;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btn_go_to_1;
    private Button btn_read_nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_go_to_1 = findViewById(R.id.btn_go_to_1);
        btn_read_nfc = findViewById(R.id.btn_read_nfc);

        btn_go_to_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CJSWebActivity.class);
                i.putExtra("title", "测试的网页1");
//                i.putExtra("url", "http://www.baidu.com");
//                i.putExtra("url", "http://192.168.43.108:8080/cjsb/func.html?h="+new Random(99999).nextInt());
                i.putExtra("url", "http://172.1.2.62:8080/cjsb/func.html?h=" + new Random(100).nextInt(88));
//                i.putExtra("url","http://zhidao.baidu.com/question/283844212.html");
                startActivity(i);
            }
        });

        btn_read_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNFC();
            }
        });


        uriTest("ejb0://qq.boom.fest@io:1211/gift/image/total.jpeg?sid=11ff456&ser=F&flag=0019&name=刘昂&msg=今天天气不错");
        uriTest("https://www.baidu.com:8801/src/view/afc?key=&1168-klio89879");
        uriTest("msdn://www.baidu.com/src_key=&1168-klio89879");


        TextView aId=findViewById(R.id.androidID);
        aId.setText(Settings.System.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    private void uriTest(String url) {
        Uri uri = Uri.parse(url);
        L.d("URI-PARSE-url", url);
        L.d("URI-PARSE-scheme", uri.getScheme());
        L.d("URI-PARSE-host", uri.getHost());
        L.d("URI-PARSE-auth", uri.getAuthority());
        L.d("URI-PARSE-auth-encoded", uri.getEncodedAuthority());
        L.d("URI-PARSE-path", uri.getPath());
        L.d("URI-PARSE-path-encoded", uri.getEncodedPath());
        L.d("URI-PARSE-fragment", uri.getFragment());
        L.d("URI-PARSE-fragment-encoded", uri.getEncodedFragment());
        L.d("URI-PARSE-port", uri.getPort() + "");
        L.d("URI-PARSE-query", uri.getQuery());
        L.d("URI-PARSE-query-encoded", uri.getEncodedQuery());
        L.d("URI-PARSE-user", uri.getUserInfo());
        L.d("URI-PARSE-user-encoded", uri.getEncodedUserInfo());
        L.d("URI-PARSE-spec-part", uri.getSchemeSpecificPart());
        L.d("URI-PARSE-path-segments", list2String(uri.getPathSegments()));
    }

    private String list2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i));
                if (i != list.size() - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void startNFC() {
        Intent i = new Intent(this, NfcMainNewActivity.class);
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == NFCScanningActivity.RESULT_CODE_COMPLETE_READ) {
            MsgDialog.show1(this, "NFC读卡结果:" + (data == null ? "" : data.getStringExtra("result")));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

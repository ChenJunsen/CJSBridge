package com.cjs.sdk.cjsnfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * NFC扫描界面
 *
 * @author JasonChen
 * @email chenjunsen@outlook.coms
 * @createTime 2020/9/1 0001 10:03
 */
public class NFCScanningActivity extends AppCompatActivity {
    public static final int RESULT_CODE_COMPLETE_READ = 200;
    private TextView tv_tag;

    private NfcAdapter adapter;
    PendingIntent mPendingIntent;
    IntentFilter[] mIntentFilters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_scanning);
        tv_tag = findViewById(R.id.tv_tag);
        tv_tag.setText(getString(R.string.default_nfc_read_tag));
        init();
    }

    private void init() {
        adapter = NfcAdapter.getDefaultAdapter(this);
        if (null == adapter) {
            Toast.makeText(this, getString(R.string.default_warning_not_support_nfc), Toast.LENGTH_LONG).show();
            finish();
        } else if (!adapter.isEnabled()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.default_warning_not_open_nfc))
                        .setTitle("提示")
                        .setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                                // 根据包名打开对应的设置界面
                                startActivity(intent);
                            }
                        })
                        .create()
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.default_warning_not_open_nfc))
                        .setTitle("提示")
                        .setPositiveButton("知道了", null)
                        .create()
                        .show();
            }
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
            Toast.makeText(this, "oops!" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        mIntentFilters = new IntentFilter[]{filter, filter2};
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String res = NFCUtil2.processIntent(intent);
        Toast.makeText(this,res,Toast.LENGTH_LONG).show();
        Intent data = new Intent();
        data.putExtra("result", res);
        setResult(RESULT_CODE_COMPLETE_READ, data);
        finish();
    }


}

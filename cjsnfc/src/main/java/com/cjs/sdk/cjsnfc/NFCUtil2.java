package com.cjs.sdk.cjsnfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

public class NFCUtil2 {
    //解析
    public static String processIntent(Intent intent) {
        //取出封装在intent中的TAG
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        return ByteArrayToHexString(tagFromIntent.getId());
//        return tagFromIntent.toString();
        StringBuilder sb=new StringBuilder("[");
        sb.append(tagFromIntent.describeContents());
        sb.append(",");
        sb.append(bytes2String(tagFromIntent.getId()));
        sb.append(",");
        sb.append(strings2String(tagFromIntent.getTechList()));
        sb.append("]&");
        return sb.toString();
    }

    //转为16进制字符串
    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F"};
        String out = "";


        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private static String bytes2String(byte[] bytes){
        StringBuilder s= new StringBuilder();
        for(byte b:bytes){
            s.append(b);
            s.append("-");
        }
        return s.toString();
    }

    private static String strings2String(String[] ss){
        StringBuilder s= new StringBuilder();
        for(String b:ss){
            s.append(b);
            s.append("-");
        }
        return s.toString();
    }
}

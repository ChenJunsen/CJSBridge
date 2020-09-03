package com.cjs.sdk.cjsnfc;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;
import java.nio.charset.Charset;

public class WriteTagBack {//几种不同数据格式的nfc标签写入数据

	public void writeTagUltralight(Tag tag) {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            // 从第五页开始写，因为从0-3前四页是存储系统数据的。
            ultralight.writePage(4, "中国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(5, "美国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(6, "英国".getBytes(Charset.forName("GB2312")));
            ultralight.writePage(7, "法国".getBytes(Charset.forName("GB2312")));
           
            System.out.println("成功写入MifareUltralight格式数据!");
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                ultralight.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
	
	public void writeTagClassic(Tag tag) {
		 
        MifareClassic mfc = MifareClassic.get(tag);
 
        try {
            mfc.connect();
            boolean auth = false;
            short sectorAddress = 1;
            auth = mfc.authenticateSectorWithKeyA(sectorAddress,
                    MifareClassic.KEY_NFC_FORUM);
            if (auth) {
                // the last block of the sector is used for KeyA and KeyB cannot be overwritted
                mfc.writeBlock(4, "1313838438000000".getBytes());
                mfc.writeBlock(5, "1322676888000000".getBytes());
                mfc.close();
                System.out.println("成功写入");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                mfc.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
	
	// 调用writeTag(tagFromIntent, "dwtedx");
	boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                	System.out.println("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                	System.out.println("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                System.out.println("写入数据成功.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        System.out.println("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                    	System.out.println("Failed to format tag.");
                        return false;
                    }
                } else {
                	System.out.println("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
        	System.out.println("写入数据失败");
        }

        return false;
    }
	
	//Ndef ndefTag = Ndef.get(tag);
	//ndefTag.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
 
}

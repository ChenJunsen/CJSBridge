/*
 * Copyright 2011, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cjs.sdk.cjsnfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cjs.sdk.cjsnfc.tools.Converter;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class NfcMainActivity extends Activity {
	private static final String TAG = "stickynotes";
	// private boolean mResumed = false;
	private boolean mWriteMode = false;
	NfcAdapter mNfcAdapter;
	EditText mNoteRead;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
	private static final String SELECT_APDU_HEADER = "00A40400";
	IntentFilter[] mNdefExchangeFilters;
	private String[][] techListsArray;// ++
	private Tag tagFromIntent;// ++
    String ex_id="",types="";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_main);
		mNoteRead = ((EditText) findViewById(R.id.noteRead));

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);// 设备注册
		if (mNfcAdapter == null) {// 判断设备是否可用
			toast("设备不支持nfc!");
			return;
		}
		if (!mNfcAdapter.isEnabled()) {
			Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
			finish();
			return;
		}
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("*/*");// text/plain
		} catch (MalformedMimeTypeException e) {
		}

		IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter ttech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected, ttech, td };
		techListsArray = new String[][] {
				new String[] { NfcF.class.getName() },
				new String[] { NfcA.class.getName() },
				new String[] { NfcB.class.getName() },
				new String[] { NfcV.class.getName() },
				new String[] { Ndef.class.getName() },
				new String[] { NdefFormatable.class.getName() },
				new String[] { IsoDep.class.getName() },
				new String[] { MifareClassic.class.getName() },
				new String[] { MifareUltralight.class.getName() } };

	}

	@Override
	public void onResume() {
		super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				null, null);// ++
		resolvIntent(getIntent());
		/*if (mNfcAdapter != null) {
			enableNdefExchangeMode();
		}*/
	}

	void setUpWebView(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0)
			return;
		for (int i = 0; i < msgs.length; i++) {
			int lenth = msgs[i].getRecords().length;
			NdefRecord[] records = msgs[i].getRecords();
			for (int j = 0; j < lenth; j++) {
				for (NdefRecord record : records) {
					if (isUri(record))
						{types="URI";parseUriRecord(record);}
					else
						{types="TEXT";parseRecord(record);}
				}
			}
		}
	}

	public static boolean isUri(NdefRecord record) {
		if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
			if (Arrays.equals(record.getType(), NdefRecord.RTD_URI))
				{return true;}
			else
				return false;
		} else if (record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI) {
			return true;
		} else
			return false;
	}

	private void parseUriRecord(NdefRecord record) {
		short tnf = record.getTnf();
		if (tnf == NdefRecord.TNF_WELL_KNOWN) {
			parseWellKnownUriRecode(record);
		} else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
			parseAbsoluteUriRecode(record);
		} else {
			toast("不知名的uri");
		}
	}

	private void parseRecord(NdefRecord record) {
		short tnf = record.getTnf();
		if (tnf == NdefRecord.TNF_WELL_KNOWN) {
			parseWellKnownTextRecode(record);
		} else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
			parseOtherRecode(record);
		} else if (tnf == NdefRecord.TNF_EXTERNAL_TYPE) {
			parseOtherRecode(record);
		}
	}

	private void parseAbsoluteUriRecode(NdefRecord record) {
		byte[] payload = record.getPayload();
		Uri uri = Uri.parse(new String(payload, Charset.forName("utf-8")));
		setNoteBody(new String(payload, Charset.forName("utf-8")));
	}

	private void parseWellKnownUriRecode(NdefRecord record) {
		Preconditions.checkArgument(Arrays.equals(record.getType(),
				NdefRecord.RTD_URI));
		byte[] payload = record.getPayload();
		String prefix = URI_PREFIX_MAP.get(payload[0]);
		byte[] fulluri = Bytes.concat(
				prefix.getBytes(Charset.forName("utf-8")),
				Arrays.copyOfRange(payload, 1, payload.length));
		Uri uri = Uri.parse(new String(fulluri, Charset.forName("utf-8")));
		setNoteBody(new String(fulluri, Charset.forName("utf-8")));
	}

	private void parseWellKnownTextRecode(NdefRecord record) {
		Preconditions.checkArgument(Arrays.equals(record.getType(),
				NdefRecord.RTD_TEXT));
		String payloadStr = "";
		byte[] payload = record.getPayload();
		Byte statusByte = record.getPayload()[0];
		String textEncoding = "";
		textEncoding = ((statusByte & 0200) == 0) ? "utf-8" : "utf-16";
		int languageCodeLength = 0;
		languageCodeLength = statusByte & 0077;
		try {
			payloadStr = new String(payload, languageCodeLength + 1,
					payload.length - languageCodeLength - 1, textEncoding);
		} catch (UnsupportedEncodingException e) {
		}
		setNoteBody(payloadStr);
	}

	private void parseOtherRecode(NdefRecord record) {
		byte[] payload = record.getPayload();
		Uri uri = Uri.parse(new String(payload, Charset.forName("utf-8")));
		setNoteBody(new String(payload, Charset.forName("utf-8")));
	}

	private void setNoteBody(String body) {
		Editable text = mNoteRead.getText();
		text.clear();
		text.append("content:"+body+"\n"+"EXTRA_ID:"+ex_id+"\n"+"type："+types);
	}

	private static final BiMap<Byte, String> URI_PREFIX_MAP = ImmutableBiMap
			.<Byte, String> builder().put((byte) 0x00, "")
			.put((byte) 0x02, "https://www.").put((byte) 0x03, "http://")
			.put((byte) 0x04, "https://").put((byte) 0x05, "tel:")
			.put((byte) 0x06, "mailto:")
			.put((byte) 0x07, "ftp://anonymous:anonymous@")
			.put((byte) 0x08, "ftp://ftp.").put((byte) 0x09, "ftps://")
			.put((byte) 0x0A, "sftp://").put((byte) 0x0B, "smb://")
			.put((byte) 0x0C, "nfs://").put((byte) 0x0D, "ftp://")
			.put((byte) 0x0E, "dav://").put((byte) 0x0F, "news:")
			.put((byte) 0x10, "telnet://").put((byte) 0x11, "imap:")
			.put((byte) 0x12, "rtsp://").put((byte) 0x13, "urn:")
			.put((byte) 0x14, "pop:").put((byte) 0x15, "sip:")
			.put((byte) 0x16, "sips:").put((byte) 0x17, "tftp:")
			.put((byte) 0x18, "btspp://").put((byte) 0x19, "btl2cap://")
			.put((byte) 0x1A, "btgoep://").put((byte) 0x1B, "tcpobex://")
			.put((byte) 0x1C, "irdaobex://").put((byte) 0x1D, "file://")
			.put((byte) 0x1E, "urn:epc:id:").put((byte) 0x1F, "urn:epc:tag:")
			.put((byte) 0x20, "urn:epc:pat:").put((byte) 0x21, "urn:epc:raw:")
			.put((byte) 0x22, "urn:epc:").put((byte) 0x23, "urn:nfc:").build();

	void resolvIntent(Intent intent) {
		String action = intent.getAction();
		toast(action);
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			setUpWebView(msgs);
			
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// 处理该intent
			tagFromIntent = getIntent()
					.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			getresult(tagFromIntent);

		}else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
			types="Tag";
			tagFromIntent = getIntent()
					.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			getresult(tagFromIntent);
		}

	}
	
	 private String bytesToHexString(byte[] src) {
         StringBuilder stringBuilder = new StringBuilder("0x");
         if (src == null || src.length <= 0) {
             return null;
         }
         char[] buffer = new char[2];
         for (int i = 0; i < src.length; i++) {
             buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
             buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
             System.out.println(buffer);
             stringBuilder.append(buffer);
         }
         return stringBuilder.toString();
     }
	 private NfcB nfcbTag;
	void getresult(Tag tag) {
		ArrayList<String> list = new ArrayList<String>();
		types="";
		for (String string : tag.getTechList()) {
			list.add(string);
			types+=string.substring(string.lastIndexOf(".")+1, string.length())+",";
		}
		types=types.substring(0, types.length()-1);
		if (list.contains("android.nfc.tech.MifareUltralight")) {
			String str = readTagUltralight(tag);
			setNoteBody(str);
		} else if (list.contains("android.nfc.tech.MifareClassic")) {
			String str = readTagClassic(tag);
			setNoteBody(str);
		}
		else if (list.contains("android.nfc.tech.IsoDep")) {
	    	   try  
	           {  
	               //Get an instance of the type A card from this TAG  
	               /*IsoDep isodep = IsoDep.get(tag);  
	               isodep.connect();  
	               if (isodep.isConnected()) {
	               //select the card manager applet      
	            	  byte[] cmd = { (byte) 0x00, // CLA Class
                (byte) 0xB4, // INS Instruction
                (byte) 0x04, // P1 Parameter 1
                (byte) 0x00, // P2 Parameter 2
                (byte) 0x00, // Le
                };
	            	byte[] command = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
	               byte[] balanceRsp = isodep.transceive(command);  
	               isodep.close(); */
	    		   IsoDep isodep = IsoDep.get(tagFromIntent);
					isodep.connect();
					//select the card manager applet
					byte[] mf = { (byte) '1', (byte) 'P',
							(byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
							(byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F',
							(byte) '0', (byte) '1', };
	                String result="";
					byte[] mfRsp = isodep.transceive(getSelectCommand(mf));
					Log.d(TAG, "mfRsp:" + HexToString(mfRsp));
					//select Main Application
					byte[] wht = { (byte) 0x41, (byte) 0x50,//此处以武汉通为例，其它的卡片参考对应的命令，网上可以查到
							(byte) 0x31, (byte) 0x2E, (byte) 0x57, (byte) 0x48, (byte) 0x43,
							(byte) 0x54, (byte) 0x43, };
		            byte[] sztRsp = isodep.transceive(getSelectCommand(wht));

		            byte[] balance = { (byte) 0x80, (byte) 0x5C, 0x00, 0x02, 0x04};
		            byte[] balanceRsp = isodep.transceive(balance);
		            Log.d(TAG, "balanceRsp:" + HexToString(balanceRsp));
		            if(balanceRsp!=null && balanceRsp.length>4)
		            {
		            	int cash = byteToInt(balanceRsp, 4);	            	
		            	float ba = cash / 100.0f;
		            	result+="  余额："+String.valueOf(ba);
		            	 
		            }
		            setNoteBody(result); 
		            isodep.close();               
	                   
	               
	           }catch(Exception e)  
	           {  
	               Log.e(TAG, "ERROR:" + e.getMessage());  
	           }  
	       }
		else if (list.contains("android.nfc.tech.NfcB")) {
			nfcbTag = NfcB.get(tag);
			try {
				nfcbTag.connect();
				if (nfcbTag.isConnected()) {
					System.out.println("已连接");
					Toast.makeText(NfcMainActivity.this, "身份证已连接",
							Toast.LENGTH_SHORT).show();
					new CommandAsyncTask().execute();

				}
				// nfcbTag.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       else if (list.contains("android.nfc.tech.NfcA")) {
    	   toast("NfcA");
    	   NfcA nfca = NfcA.get(tagFromIntent);
           try{
               nfca.connect();
               if (nfca.isConnected()) {//NTAG216的芯片
               byte[] SELECT = {
                       (byte) 0x30,
                       (byte) 5 & 0x0ff,//0x05
                   };
               byte[] response = nfca.transceive(SELECT);
               nfca.close();
               if(response!=null){
               setNoteBody(new String(response, Charset.forName("utf-8")));
               }
               }
           }
           catch(Exception e){
           }
		}
       else if (list.contains("android.nfc.tech.NfcF")) {//此处代码没有标签测试
    	   NfcF nfc = NfcF.get(tag);
           try {
               nfc.connect();
               byte[] felicaIDm = new byte[]{0};
               byte[] req = readWithoutEncryption(felicaIDm, 10);
               byte[] res = nfc.transceive(req);
               nfc.close();
               setNoteBody(ByteArrayToHexString(res));
           } catch (Exception e) {
               Log.e(TAG, e.getMessage() , e);
           }
       }
       else if (list.contains("android.nfc.tech.NfcV")) {//完成
    	   NfcV tech = NfcV.get(tag);
    	    if (tech != null) {
    	    	try {
    	    		tech.connect();
                    if (tech.isConnected()) {
                    	byte[] tagUid = tag.getId();  // store tag UID for use in addressed commands

                    	int blockAddress = 0;
                    	int blocknum = 4;
                    	byte[] cmd = new byte[] {
                    	        (byte)0x22,  // FLAGS
                    	        (byte)0x23,  // 20-READ_SINGLE_BLOCK,23-所有块
                    	        0, 0, 0, 0, 0, 0, 0,0,
                    	        (byte)(blockAddress & 0x0ff),(byte)(blocknum-1 & 0x0ff)
                    	};
                    	System.arraycopy(tagUid, 0, cmd, 2, tagUid.length);  // paste tag UID into command
                    	
                    	byte[] response = tech.transceive(cmd);
                        tech.close();
                        if(response!=null){
                        setNoteBody(new String(response, Charset.forName("utf-8")));
                        }
                    }
                } catch (IOException e) {
                   
                }
    	    }
       }
       
       else if (list.contains("android.nfc.tech.Ndef")) {
    	   NdefMessage[] messages = getNdefMessages(getIntent());
           byte[] payload = messages[0].getRecords()[0].getPayload();
           setNoteBody(new String(payload));
       }
       else if (list.contains("android.nfc.tech.NdefFormatable")) {
    	   NdefMessage[] messages = getNdefMessages(getIntent());
           byte[] payload = messages[0].getRecords()[0].getPayload();
           setNoteBody(new String(payload));
       }
	}
	
	public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }
	
	public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	
	public String readTagClassic(Tag tag) {
		boolean auth = false;
		MifareClassic mfc = MifareClassic.get(tag);
		// 读取TAG
		try {
			String metaInfo = "";
			int type = mfc.getType();// 获取TAG的类型
			int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
			String typeS = "";
			switch (type) {
			case MifareClassic.TYPE_CLASSIC:
				typeS = "TYPE_CLASSIC";
				break;
			case MifareClassic.TYPE_PLUS:
				typeS = "TYPE_PLUS";
				break;
			case MifareClassic.TYPE_PRO:
				typeS = "TYPE_PRO";
				break;
			case MifareClassic.TYPE_UNKNOWN:
				typeS = "TYPE_UNKNOWN";
				break;
			}
			metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
					+ mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
					+ "B\n";
			for (int j = 0; j < sectorCount; j++) {
				// Authenticate a sector with key A.

				auth = mfc.authenticateSectorWithKeyA(j,
						MifareClassic.KEY_DEFAULT);
				int bCount;
				int bIndex;
				if (auth) {
					metaInfo += "Sector " + j + ":验证成功\n";
					// 读取扇区中的块
					bCount = mfc.getBlockCountInSector(j);
					bIndex = mfc.sectorToBlock(j);
					for (int i = 0; i < bCount; i++) {
						byte[] data = mfc.readBlock(bIndex);
						metaInfo += "Block " + bIndex + " : "
								+ ByteArrayToHexString(data) + "\n";
						bIndex++;
					}
				} else {
					metaInfo += "Sector " + j + ":验证失败\n";
				}
			}
			return metaInfo;
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} finally {
			if (mfc != null) {
				try {
					mfc.close();
				} catch (IOException e) {
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
		return null;

	}

	private String ByteArrayToHexString(byte[] inarray) { // converts byte
		// arrays to string
		int i, j, in;
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F" };
		String out = "";

		for (j = 0; j < inarray.length; ++j) {
			in = inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	public String readTagUltralight(Tag tag) {
		MifareUltralight mifare = MifareUltralight.get(tag);
		try {
			mifare.connect();
			int size=mifare.PAGE_SIZE;
			byte[] payload = mifare.readPages(0);
			String result="page1："+ByteArrayToHexString(payload)+"\n"+"总容量："+String.valueOf(size)+"\n";

			//这里只读取了其中几个page、
			byte[] payload1 = mifare.readPages(4);
			byte[] payload2 = mifare.readPages(8);
			byte[] payload3 = mifare.readPages(12);
			result+="page4:"+ByteArrayToHexString(payload1)+"\npage8:"+ByteArrayToHexString(payload2)+"\npage12："+ByteArrayToHexString(payload3)+"\n";
			
			//byte[] payload4 = mifare.readPages(16);
			//byte[] payload5 = mifare.readPages(20);
			return result;
					//+ new String(payload4, Charset.forName("US-ASCII"));
					//+ new String(payload5, Charset.forName("US-ASCII"));
		} catch (IOException e) {
			Log.e(TAG, "IOException while writing MifareUltralight message...",
					e);
			return "读取失败！";
		} catch (Exception ee) {
			Log.e(TAG, "IOException while writing MifareUltralight message...",
					ee);
			return "读取失败！";
		} finally {
			if (mifare != null) {
				try {
					mifare.close();
				} catch (IOException e) {
					Log.e(TAG, "Error closing tag...", e);
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// mResumed = false;
		mNfcAdapter.disableForegroundDispatch(this);
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundNdefPush(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		// 读取uidgetIntent()
		byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		ex_id= Converter.getHexString(myNFCID, myNFCID.length);
		/*Toast.makeText(getApplication(),
				"EXTRA_ID:" + Converter.getHexString(myNFCID, myNFCID.length),
				Toast.LENGTH_LONG).show();*/
		// 读取uidgetIntent()
		setIntent(intent);
	}

	private View.OnClickListener mTagRead = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {

		}
	};

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	private void disableNdefExchangeMode() {
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void dialog(String str) {
		new AlertDialog.Builder(this).setTitle("提示信息")
				.setIcon(android.R.drawable.btn_star).setMessage(str)
				.setPositiveButton("确定", null).show();
	}
	
	private byte[] getSelectCommand(byte[] aid)
	{
		final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
        cmd_pse.put((byte) 0x00) // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x04) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) aid.length) // Lc
                .put(aid).put((byte) 0x00); // Le
		return cmd_pse.array();
	}
	private String HexToString(byte[] data) {
		String temp = "";
		for (byte d : data) {
			temp += String.format("%02x", d);
		}
		return temp;
	}

	public static byte byteToHex(byte arg) {
		byte hex = 0;
		if (arg >= 48 && arg <= 57) {
			hex = (byte) (arg - 48);
		} else if (arg >= 65 && arg <= 70) {
			hex = (byte) (arg - 55);
		} else if (arg >= 97 && arg <= 102) {
			hex = (byte) (arg - 87);
		}
		return hex;
	}

	private byte[] StringToHex(String data)
	{
		byte temp[] = data.getBytes();
		byte result[] = new byte[temp.length/2];
		for(int i=0; i<result.length ; i++)
		{
			result[i] = (byte) (byteToHex(temp[i*2])<<4 | byteToHex(temp[i*2+1]));
		}
		return result;
	}
	
	private int byteToInt(byte[] b, int n) {
		int ret = 0;
		for (int i = 0; i < n; i++) {
			ret = ret<<8;
			ret |= b[i] & 0x00FF;
		}
		if (ret > 100000 || ret < -100000)
			ret -= 0x80000000;
		return ret;
	}
	
	class CommandAsyncTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			byte[] search = new byte[] { 0x05, 0x00, 0x00 };
			search = new byte[] { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x60,
					0x02 };
			search = new byte[] { 0x1D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
					0x01, 0x08 };
			byte[] result = new byte[] {};
			StringBuffer sb = new StringBuffer();
			try {
				byte[] cmd = new byte[] { 0x05, 0x00, 0x00 };
				;
				result = nfcbTag.transceive(cmd);
				sb.append("寻卡指令:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				cmd = new byte[] { 0x1D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08,
						0x01, 0x08 };
				result = nfcbTag.transceive(cmd);
				sb.append("选卡指令:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				sb.append("读固定信息指令\n");

				cmd = new byte[] { 0x00, (byte) 0xA4, 0x00, 0x00, 0x02, 0x60,
						0x02 };
				result = nfcbTag.transceive(cmd);
				sb.append("发:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				cmd = new byte[] { (byte) 0x80, (byte) 0xB0, 0x00, 0x00, 0x20 };
				result = nfcbTag.transceive(cmd);
				sb.append("发:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				cmd = new byte[] { 0x00, (byte) 0x88, 0x00, 0x52, 0x0A,
						(byte) 0xF0, 0x00, 0x0E, 0x0C, (byte) 0x89, 0x53,
						(byte) 0xC3, 0x09, (byte) 0xD7, 0x3D };
				result = nfcbTag.transceive(cmd);
				sb.append("发:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				cmd = new byte[] { 0x00, (byte) 0x88, 0x00, 0x52, 0x0A,
						(byte) 0xF0, 0x00, };
				result = nfcbTag.transceive(cmd);
				sb.append("发:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
				cmd = new byte[] { 0x00, (byte) 0x84, 0x00, 0x00, 0x08 };
				result = nfcbTag.transceive(cmd);
				sb.append("发:" + ByteArrayToHexString(cmd) + "\n");
				sb.append("收:" + ByteArrayToHexString(result) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setNoteBody(result);
			try {
				nfcbTag.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	NdefMessage[] getNdefMessages(Intent intent) {//读取nfc数据
        // Parse the intent
    	
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                    record
                });
                msgs = new NdefMessage[] {
                    msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }
	
	 private byte[] readWithoutEncryption(byte[] idm, int size)
	            throws IOException {
	        ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

	        bout.write(0);           
	        bout.write(0x06);       
	        bout.write(idm);        
	        bout.write(1);          
	        bout.write(0x0f);       
	        bout.write(0x09);        
	        bout.write(size);        
	        for (int i = 0; i < size; i++) {
	            bout.write(0x80);    
	            bout.write(i);      
	        }

	        byte[] msg = bout.toByteArray();
	        msg[0] = (byte) msg.length; 
	        return msg;
	    }

}
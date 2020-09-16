package com.cjs.cjsbridge.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference辅助工具类
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/16 0016 10:54
 */
public class SPStoreUtil {

    /**
     * H5数据储存文件名
     */
    private static final String FILE_NAME_H5 = "sp.store.h5";

    private Context context;
    private String fileName;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SPStoreUtil(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
        sp = context.getSharedPreferences(fileName == null ? FILE_NAME_H5 : fileName, Context.MODE_PRIVATE);
    }

    public SPStoreUtil(Context context) {
        this(context, null);
    }

    public void putString(String key, String value) {
        if (editor == null) {
            editor = sp.edit();
        }
        editor.putString(key, value);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public String getString(String key, String def) {
        return sp.getString(key, def);
    }

    public void putBoolean(String key, boolean value) {
        if (editor == null) {
            editor = sp.edit();
        }
        editor.putBoolean(key, value);
    }

    public void getBoolean(String key) {
        sp.getBoolean(key, false);
    }

    public void getBoolean(String key, boolean def) {
        sp.getBoolean(key, def);
    }

    public void put(String key, Object value) {
        if (editor == null) {
            editor = sp.edit();
        }
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {//统一按照String处理
            editor.putString(key, value + "");
        }
    }

    public Object get(String key){
        return sp.getAll().get(key);
    }

    public void clear() {
        if (editor != null) {
            editor.clear();
        }
    }

    public void commit() {
        if (editor != null) {
            editor.commit();
        }
    }
}

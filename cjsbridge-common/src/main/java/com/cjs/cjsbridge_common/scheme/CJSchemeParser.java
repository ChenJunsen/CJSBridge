package com.cjs.cjsbridge_common.scheme;

import android.net.Uri;
import android.text.TextUtils;

import com.cjs.cjsbridge_common.tools.L;

import java.util.List;

/**
 * 协议解析器
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 14:33
 */
public class CJSchemeParser {
    /**
     * 与H5端商定好的scheme
     */
    public static final String CJSB_BRIDGE_SCHEME = "cjsb";

    /**
     * <h1>解析自定义协议链接</h1>
     * 一个完整的url链接格式为:<br>
     * http://abcd@1211:9099/img/sun.jpg?tid=edf667&date=20190090<br>
     * http                                 scheme
     * abcd@1211:9099                       authority
     * abcd                                 userInfo
     * 1211                                 host
     * 9099                                 port
     * /img/sun.jpg                         path
     * tid=edf667&date=20190090             query
     * <p>
     * 上述方法是以{@link Uri#parse(String)}方式解析分割的部分
     * 从端口号到第一个问号之间的部分称为path,path之间用/分割为多个path-segment
     * query为携带的参数部分，以问号为开始标志，中间的参数间隔以&号分割，每组参数以key=value组成键值对
     * authority部分包含了user,host和端口号三部分，
     * 如果没有@符号，就没有user部分，没有冒号就没有端口号部分
     *
     * @param url
     * @return 一个封装好的，适配CJSBridge的对象
     */
    public static CJScheme parse(String url) {
        L.d("开始解析scheme原始URL:" + url);
        CJScheme cjScheme = new CJScheme();
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                cjScheme.setScheme(uri.getScheme());
                cjScheme.setBridgeName(uri.getHost());
                cjScheme.setSid(uri.getPort());
                cjScheme.setParams(uri.getQueryParameter("params"));
                List<String> pathList = uri.getPathSegments();
                cjScheme.setMethodName(pathList == null ? "" : pathList.get(0));
            }
        }
        L.d("解析URL结束:" + cjScheme);
        return cjScheme;
    }
}

package com.cjs.cjsbridge.tools;

import android.net.Uri;

import com.cjs.cjsbridge_common.tools.L;

import java.util.List;

/**
 * Uri详情打印工具
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/9/15 0015 16:53
 */
public class UriLogger {

    public static void print(String url) {
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

    private static String list2String(List<String> list) {
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

}

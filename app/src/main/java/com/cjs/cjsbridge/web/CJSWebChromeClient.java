package com.cjs.cjsbridge.web;

import android.webkit.WebChromeClient;

/**
 * 自定义WebChromeClient
 * <ul>
 *     <li>WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等</li>
 *     <li>onCloseWindow(关闭WebView)</li>
 *     <li>onCreateWindow()</li>
 *     <li>onJsAlert (WebView上alert是弹不出来东西的，需要定制你的WebChromeClient处理弹出)</li>
 *     <li>onJsPrompt()</li>
 *     <li>onJsConfirm()</li>
 *     <li>onProgressChanged()</li>
 *     <li>onReceivedIcon()</li>
 *     <li>onReceivedTitle()</li>
 *     <li><strong>在JS和原生交互时候，WebChromeClient非常重要。</strong></li>
 * </ul>
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/12 0012 18:04
 */
public class CJSWebChromeClient extends WebChromeClient {
    public CJSWebChromeClient() {
        super();
    }
}

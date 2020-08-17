package com.cjs.cjsbridge.web;

import android.app.Activity;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.cjs.cjsbridge.tools.L;

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
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/12 0012 18:04
 */
public class CJSWebChromeClient extends WebChromeClient {
    private static final String H5ConTag = "H5Console";
    private static final String H5AlertTag = "H5Alert";
    private static final String H5ConfirmTag = "H5Confirm";

    Activity activity;

    private boolean enableH5ConsoleLog = true;
    private boolean enableH5AlertLog = true;
    private boolean enableH5PromptLog = true;
    private boolean enableH5ConfirmLog = true;

    public CJSWebChromeClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * 拦截处理H5端的console
     *
     * @param consoleMessage
     * @return
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (enableH5ConsoleLog) {
            String logFmt = "%2$s[lineNum:%1$s]-%3$s";//转换成logCat的日志格式
            ConsoleMessage.MessageLevel level = consoleMessage.messageLevel();
            int lineNumber = consoleMessage.lineNumber();
            String msg = consoleMessage.message();
            switch (level) {
                case WARNING:
                    L.w(H5ConTag, String.format(logFmt, lineNumber, level, msg));
                    break;
                case ERROR:
                    L.e(H5ConTag, String.format(logFmt, lineNumber, level, msg));
                    break;
                case TIP:
                    L.i(H5ConTag, String.format(logFmt, lineNumber, level, msg));
                    break;
                default:
                    L.d(H5ConTag, String.format(logFmt, lineNumber, level, msg));
                    break;
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }

    /**
     * 拦截H5的alert弹窗
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        if (enableH5AlertLog) {
            String fmt = "[源:%1$s";
            String fmt2 = "msg:%1$s]";
            L.d(H5AlertTag, String.format(fmt, url));
            L.d(H5AlertTag, String.format(fmt2, message));
        }
        return super.onJsAlert(view, url, message, result);
    }


    /**
     * 拦截H5的confirm弹窗
     *
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        if (enableH5ConfirmLog) {
            String fmt = "[源:%1$s";
            String fmt2 = "msg:%1$s]";
            L.d(H5ConfirmTag, String.format(fmt, url));
            L.d(H5ConfirmTag, String.format(fmt2, message));
        }
        return super.onJsConfirm(view, url, message, result);
    }

    /**
     * 拦截H5的prompt弹窗
     * 该弹窗为一个可输入的确认弹窗，默认样式比较丑，而且使用度和alert,confirm比起来低，所以可以采用它进行混合交互
     * @param view
     * @param url
     * @param message 对话框文本信息
     * @param defaultValue 默认设置的输入值
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}

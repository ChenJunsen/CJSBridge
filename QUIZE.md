# 一些开发中遇到的问题
## 0.如果不能加载页面，看看有没有赋予网络权限
## 1.WebChromeClient和WebViewClient
* WebViewClient帮助WebView处理各种通知、请求事件的，具体来说包括：

    onLoadResource()

    onPageStarted()

    onPageFinished()

    onReceiveError()

    onReceivedHttpAuthRequest()

* WebChromeClient是辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等

    onCloseWindow(关闭WebView)

    onCreateWindow()

    onJsAlert (WebView上alert是弹不出来东西的，需要定制你的WebChromeClient处理弹出)

    onJsPrompt()

    onJsConfirm()

    onProgressChanged()

    onReceivedIcon()

    onReceivedTitle()

### 在JS和原生交互时候，WebChromeClient非常重要。

## 2.WebView.loadUrl时跳转系统的浏览器
重写WebViewClient的shouldOverrideUrlLoading(WebView view, String url)使用view.loadUrl(url);加载url  
网上的写法:
```
WebView webView= (WebView) findViewById(R.id.webView);
webView.getSettings().setJavaScriptEnabled(true);
webView.loadUrl("http://www.baidu.com");
webView.setWebViewClient(new WebViewClient(){
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    view.loadUrl(url);
    return super.shouldOverrideUrlLoading(view, url);
    }
});
```
实际官方有注解，view.loadUrl(url)这一步完全不用调用，只需要返回false就行:
```
webView.setWebViewClient(new WebViewClient(){
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    return false;
    }
});
```

## 3.加载的页面不完整
#### 原因之一:JavaScript没有允许
解决办法:
```
webSettings.setJavaScriptEnabled(true);
```

## 4.加载页面报
```
net::ERR_CLEARTEXT_NOT_PERMITTED
```
出现这个错，用电脑浏览器访问同样地址的页面是没有问题，唯独安卓手机这边会有问题。  
原因:
```
从Android 9.0（API级别28）开始，默认情况下限制了明文流量的网络请求，对未加密流量不再信任，直接放弃请求，因此http的url均无法在webview中加载，https 不受影响。
```
解决办法:
* 在AndroidManifest.xml里面的application节点设置一个属性
```
<manifest ...>
    <application
        ...
        android:usesCleartextTraffic="true"
        ...>
        ...
    </application>
</manifest>
```

* 也是在application节点里面设置一个属性，首先要在res目录下建立一个xml文件夹，新建一个xml配置文件，名字自定义(注意命名规范)：
```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```
然后在application的节点里面增加属性,值是引用到上面配置的文件:
```
<manifest ...>
    <application
        ...
        android:networkSecurityConfig="@xml/network_security_config"
        ...>
        ...
    </application>
</manifest>
```
* 服务端和客户端都采用Https通讯，这也是谷歌官方建议这样做的

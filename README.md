# CJSBridge是一个探索性研究项目
本项目的研究的问题:
* Android WebView的一些漏洞及解决办法
* Android WebView如何实现原生及H5之间的交互
* 如何自定义一套混合交互框架

### 在搭建项目时遇到的一些问题及解决办法，参考
```
/doc/QUIZE.md
```

### WebView已知的一些漏洞，如何钻洞及填洞方法，参考
```
/doc/安卓WebView的一些漏洞及解决办法.pdf
```

### H5的模板代码和JS代码参考
```
app/src/main/assets
```

### WebView和H5交互的方式
* @JavascriptInterface方式实现  
主体思路：  
    1.新建一个JSInterface类(名字随意),内部定义一些需要与H5交互的public方法(H5调用原生),
    方法可以带参数，但是不能直接是对象类型，一般是String,并且可以是个JSONString,这样H5那边
    可以打包传参。  
    2.自己新建一个Activity,插入一个webView，调用该webView的addJavascriptInterface方法
    ```
    webView.addJavascriptInterface(new JSInterface(), "JSI");
    ```
    其中new的对象是前面建立的JSInterface类，‘JSI’等于是给这个插件取个别名，然后h5那边可以调用
    3.h5端，通过，比如我们要调用前面public方法里面的toast方法
    ```
    function callNativeXX(){
          javascript:JSI.toast('hello')
    }    
    ```
    h5的方法名字可以自己定义，内容格式就是:
    ```
    javascript:插件别名.具体方法名(参数)  
    ```
    上面的代码在安卓4.2以前是可以运行的，4.2后就不行了，因为可以通过JS注入，反射获取app内部隐私信息。  
    因此4.2后，需要在前面的那些public方法上面加入@JavascriptInterface的注解，例如:
    ```
    @JavascriptInterface
    public void toast(String msg){
        ....      
    }              
    ```
* 通过拦截WebViewChromeClient的onPrompt方式实现
    ChromeClient里面可以重写onAlert,onConfirm和onPrompt三个方法，这三个方法分别对应H5端直接调用alert(),confirm()和
    prompt()进行对应的对话框显示。每个方法都可以传入一个msg参数，作为对话框的显示的内容。因此，这种实现思想就是先通过H5端调用
    其中一种，将这个msg传到原生，然后在chromeClient中拦截到这个信息，进行解析，得到需要的指令。
    虽说三种方法都可以，但是实际不是都可以。从实际使用频率上看，prompt无非是使用最小的。prompt是个能输入的对话框，一般来讲，
    项目上遇到这种对话框都要重写，因此实用性较低。可以选择prompt入手。

* 通过H5的ifame控件实现
  这种方式实现的思想和prompt的核心思想是一致的。只不过，msg的传入改为iframe传入。iframe可以理解为H5页面的内嵌浏览器。我们可以在
  每次请求指令的时候，用js代码生成一个看不见的iframe,通过iframe.src=msg的方式将其传输到原生。原生可以在WebClient的shouldOverrideUrlLoading
  方法中拦截到这个msg,进行解析，实现相关逻辑。

* 原生调用H5的方法  
  1.核心方法是安卓webView里的
  ```
  evaluateJavascript(javascript, new ValueCallback<String>() {
      @Override
      public void onReceiveValue(String value) {
          
      }
  });
  ```
  但是该方法只在api-19以上有效,因此通用写法为:
  ```
    webView.loadUrl("javascript:具体的js代码")
  ```
  二者的区别在于前者有回调，后者没有

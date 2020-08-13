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

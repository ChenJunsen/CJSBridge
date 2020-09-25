/**
 * JS桥 v2.0 created by cjs at 2020.09.16
 * 采用iframe进行传输协议
 * 1、自定义协议格式
 * <pre>
 *     bridge_scheme://bridgeName:sid/methodName?‘jsp’=params&...
 * </pre>
 * 2、该函数设置为自执行函数
 */
!(function cjs_bridge() {
    /**
     * 代码-解析参数失败
     * @type {number}
     */
    const CJSB_ERR_PARSE_PARAMS = -777
    /**
     * 代码-非法SID
     * @type {number}
     */
    const CJSB_ERR_INVALID_SID = -999
    /**
     * js桥的scheme自定义协议名字
     * @type {string}
     */
    const CJSB_BRIDGE_SCHEME = 'cjsb'
    const CJSB_BRIDGE_NAME = 'cjsbridge'
    const CJSB_CALL_BACK_PREFIX = 'cjsb_callBack_'
    const CJSB_TAG_PREFIX = '[CJSB]'
    let enbale_cjsb_log = true

    /**
     * 日志工具类
     * @type {{d: d, e: e, w: w}}
     */
    let L = {
        e: function (msg) {
            if (enbale_cjsb_log) {
                console.error(CJSB_TAG_PREFIX + msg)
            }
        },
        d: function (msg) {
            if (enbale_cjsb_log) {
                console.log(CJSB_TAG_PREFIX + msg)
            }
        },
        w: function (msg) {
            if (enbale_cjsb_log) {
                console.warn(CJSB_TAG_PREFIX + msg)
            }
        },
        i: function (msg) {
            if (enbale_cjsb_log) {
                console.info(CJSB_TAG_PREFIX + msg)
            }
        }
    }

    L.i('WebView ua is:' + navigator.userAgent)

    let sidSeed = 1//指令ID的种子，逐渐递增
    let callBacks = {}//回调集合
    let CJSBridge = window.CJSBridge || (window.CJSBridge = {})//初始化桥实体
    //上面的表达式可以翻译为下面这句话
    /*if(!window.CJSBridge){
        window.CJSBridge={}
    }
    let CJSBridge=window.CJSBridge*/
    let events = {}//自定义事件集合


    /**
     * 生成转换原生可接受的参数
     * @param oriP
     * @returns {string|*|string}
     */
    function generateParams(oriP) {
        if (typeof oriP === 'object') {
            return JSON.stringify(oriP)
        } else {
            return oriP || ''
        }
    }

    /**
     * 解析原生传到H5的参数
     * @param pStr
     */
    function parseParams(pStr) {
        let params = pStr
        try {
            if (typeof pStr === 'string') {
                params = JSON.parse(pStr)
            }
            if (!hasOwnProperty(params, 'cjsb_status')) {
                params['cjsb_status'] = '1'
                params['cjsb_msg'] = '成功'
            }
        } catch (e) {
            L.e("解析原生传参失败:")
            L.e(e)
            params = {
                cjsb_status: CJSB_ERR_PARSE_PARAMS,
                cjsb_msg: '参数解析失败'
            }
        }
        return params
    }

    /**
     * 生成指令ID
     * 思想:随机数+变量->确保唯一
     * @returns {number}
     */
    function generateSID() {
        return Math.floor(Math.random() * (1 << 50)) + sidSeed++
    }

    /**
     * 判断obj是否含有prop的属性
     * @param obj
     * @param prop
     * @returns {boolean}
     */
    function hasOwnProperty(obj, prop) {
        //采用原型的call来判断，防止自定义同名方法覆盖
        return Object.prototype.hasOwnProperty.call(obj, prop)
    }

    /**
     * 注册一个请求
     * @param sid
     * @param callBack
     */
    function registerCall(sid, callBack) {
        if (sid) {
            if (typeof callBack === 'function') {
                callBacks[CJSB_CALL_BACK_PREFIX + sid] = callBack
            } else {
                callBacks[CJSB_CALL_BACK_PREFIX + sid] = null
                L.i('register callback is not a function,that means there is no callback for the method with sid:' + sid)
            }
        } else {
            L.e('invalid register call sid!')
        }
    }

    /**
     * 注销一个请求，释放内存
     * @param sid
     * @returns {*} 返回注销掉的请求
     */
    function unRegisterCall(sid) {
        if (sid) {
            let callBackId = CJSB_CALL_BACK_PREFIX + sid
            let call = callBacks[callBackId]
            delete callBacks[callBackId]
            return call
        } else {
            L.e('invalid unregister call sid!')
            return {}
        }
    }

    function callNative(methodName, params, sid) {
        let url = CJSB_BRIDGE_SCHEME + "://" + CJSB_BRIDGE_NAME + ":" + sid + '/' + methodName + '?params=' + generateParams(params)
        //核心 -------->真正传输给原生数据的入口<------
        L.d("H5 call native url:" + url)
        iframeCall(url)
    }

    function iframeCall(url) {
        //创建一个看不见的iframe,用于传输url给原生
        let iframe = document.createElement('iframe')
        iframe.src = url
        iframe.style.display = 'none'
        document.documentElement.appendChild(iframe)
        setTimeout(() => {
            document.documentElement.removeChild(iframe)
        }, 0)
    }

    /**
     * 注册自定义事件
     * @param evName 事件名字
     * @param params 事件所要携带的参数
     * @param cancancel 事件是否能取消
     * @param canBubble 事件是否能冒泡
     */
    function registerEvent(evName, params, cancancel, canBubble) {
        L.d('Register event：' + evName)
        if (evName && typeof evName === 'string') {
            let p = {};
            if (typeof params === 'string') {
                try {
                    p = JSON.parse(params);
                } catch (e) {
                    L.w('convert event params failed!')
                }
            } else {
                p = params || {}
            }
            let event = new CustomEvent(evName, {
                detail: p,
                cancelable: cancancel,
                bubbles: canBubble
            })
            events[evName] = event
        } else {
            L.e('Register event failed! Invalid event name：' + evName);
        }
    }

    /**
     * 注销自定义事件
     * @param evName 事件名字
     */
    function unRegisterEvent(evName) {
        L.d('UnRegister event：' + evName)
        if (evName) {
            delete events[evName]
        } else {
            L.e('UnRegister event failed! Invalid event name：' + evName);
        }
    }

    /**
     * 触发自定义事件
     * @param evName 事件名字
     * @param params 触发事件时附带的参数
     */
    function triggerEvent(evName, params) {
        L.d('Trigger event：' + evName + '  携带参数:' + JSON.stringify(params))
        if (evName) {
            let ev = events[evName]
            let p = {}
            if (typeof params === 'string') {
                try {
                    p = JSON.parse(params)
                } catch (e) {
                    L.w('convert event params failed!')
                }
            } else {
                p = params || {}
            }
            //将触发时的携带参数与初始化的参数进行合并
            for (let key in p) {
                ev.detail[key]=p[key]
            }
            document.dispatchEvent(ev)
        } else {
            L.e('trigger event failed! Invalid event name：' + evName);
        }
    }

    //设置桥实体
    let core = {
        call: function (methodName, params, callBack) {
            let sid = generateSID()
            registerCall(sid, callBack)
            callNative(methodName, params, sid)
        },
        callBack: function (sid, data) {
            let callB = unRegisterCall(sid)
            if (typeof callB === 'function') {
                callB(parseParams(data))
            } else {
                L.w('The call back with sid=' + sid + ' is no longer exist!')
            }
        },
        registerEvent: function (evName, params) {
            registerEvent(evName, params, false, false)
        },
        triggerEvent: function (evName, params) {
            triggerEvent(evName, params)
        }
    }

    //拷贝core里面的属性进入桥
    for (let key in core) {
        if (!hasOwnProperty(CJSBridge, key)) {
            CJSBridge[key] = core[key]
        }
    }

    L.i('CJSBridge2注入完毕')
    CJSBridge.call('cjsb_init')
}())

function CJSBReady(readyCallback) {
    function ready(callback) {
        // 如果 jsbridge 已经注入则直接调用
        if (window.CJSBridge) {
            callback && callback();
        } else {
            // 如果没有注入则监听注入的事件
            document.addEventListener('CJSBridgeReady', callback, false);
        }
    }

    ready(readyCallback);
}

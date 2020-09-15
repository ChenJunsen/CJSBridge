/**
 * JS桥
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
    const CJSB_CALL_BACK_PREFIX = 'cjsb_callBack_'
    const CJSB_TAG_PREFIX = '[CJSB]'
    var enbale_cjsb_log = true

    /**
     * 日志工具类
     * @type {{d: d, e: e, w: w}}
     */
    var L = {
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

    var sidSeed = 1//指令ID的种子，逐渐递增
    var callBacks = {}//回调集合
    var CJSBridge = window.CJSBridge || (window.CJSBridge = {})//初始化桥实体
    //上面的表达式可以翻译为下面这句话
    /*if(!window.CJSBridge){
        window.CJSBridge={}
    }
    var CJSBridge=window.CJSBridge*/

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
        var params = {}
        try {
            if(typeof pStr === 'string'){
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
                L.i('register call is not a function,that means there is no callback for this method')
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
            var callBackId = CJSB_CALL_BACK_PREFIX + sid
            var call = callBacks[callBackId]
            delete callBacks[callBackId]
            return call
        } else {
            L.e('invalid unregister call sid!')
            return {}
        }
    }

    function callNative(bridgeName, methodName, params, sid) {
        var url = CJSB_BRIDGE_SCHEME + "://" + bridgeName + ":" + sid + '/' + methodName + '?params=' + generateParams(params)
        //核心 -------->真正传输给原生数据的入口<------
        L.d("H5 call native url:" + url)
        prompt(url)
    }

    //设置桥实体
    var core = {
        call: function (bridgeName, methodName, params, callBack) {
            var sid = generateSID()
            registerCall(sid, callBack)
            callNative(bridgeName, methodName, params, sid)
        },
        callBack: function (sid, data) {
            var callB = unRegisterCall(sid)
            if (typeof callB === 'function') {
                callB(parseParams(data))
            }
        }
    }

    //拷贝core里面的属性进入桥
    for (var key in core) {
        if (!hasOwnProperty(CJSBridge, key)) {
            CJSBridge[key] = core[key]
        }
    }

}())

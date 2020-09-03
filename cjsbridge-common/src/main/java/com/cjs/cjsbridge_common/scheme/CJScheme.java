package com.cjs.cjsbridge_common.scheme;

/**
 * 协议解析后封装的对象
 *
 * @author JasonChen
 * @email chenjunsen@outlook.com
 * @createTime 2020/8/31 0031 14:35
 */
public class CJScheme {

    /**
     * 解析失败过后的scheme代码,这里取值-1还有另一层含义，就是{@link android.net.Uri#parse(String)}这个方法没有解析到port也会返回-1
     */
    public static final int INVALID_PARSE_SID = -1;

    private String scheme;
    private String methodName;
    private String bridgeName;
    private String params;
    private int sid = INVALID_PARSE_SID;

    public CJScheme() {
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBridgeName() {
        return bridgeName;
    }

    public void setBridgeName(String bridgeName) {
        this.bridgeName = bridgeName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    @Override
    public String toString() {
        return "CJScheme{" +
                "scheme='" + scheme + '\'' +
                ", methodName='" + methodName + '\'' +
                ", bridgeName='" + bridgeName + '\'' +
                ", params='" + params + '\'' +
                ", sid=" + sid +
                '}';
    }
}

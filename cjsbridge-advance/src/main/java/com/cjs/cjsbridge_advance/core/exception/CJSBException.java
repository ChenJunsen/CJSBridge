package com.cjs.cjsbridge_advance.core.exception;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.cjs.cjsbridge_common.tools.L;


public class CJSBException extends Exception {

    public CJSBException() {
    }

    public CJSBException(String message) {
        super(message);
    }

    public CJSBException(String message, Throwable cause) {
        super(message, cause);
    }

    public CJSBException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CJSBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        L.e("exception", message);
    }
}

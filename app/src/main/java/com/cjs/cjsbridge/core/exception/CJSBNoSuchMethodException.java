package com.cjs.cjsbridge.core.exception;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class CJSBNoSuchMethodException extends CJSBException {
    public CJSBNoSuchMethodException() {
    }

    public CJSBNoSuchMethodException(String message) {
        super(message);
    }

    public CJSBNoSuchMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public CJSBNoSuchMethodException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CJSBNoSuchMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

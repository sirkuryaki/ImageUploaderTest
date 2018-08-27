package com.yopdev.imageuploadertest.util;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */

public class WSResult {
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;
    public static final int OFFLINE = 3;

    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SUCCESS, FAILURE, OFFLINE
    })

    public @interface WSResultInterface {
    }
}

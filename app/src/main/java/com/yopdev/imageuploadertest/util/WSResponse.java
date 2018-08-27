package com.yopdev.imageuploadertest.util;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */
public class WSResponse<T> {

    public final MutableLiveData<Boolean> executed = new MutableLiveData<>();
    public T data;
    private int httpCode;
    private String body;


    public WSResponse() {
        executed.postValue(false);
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return httpCode >= 200 && httpCode < 300;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Response:%d %s", httpCode, body);
    }

    @NonNull
    @WSResult.WSResultInterface
    public Integer getResult() {

        if (httpCode == 408) {
            return WSResult.OFFLINE;
        }

        return httpCode >= 200 && httpCode < 300 ? WSResult.SUCCESS : WSResult.FAILURE;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

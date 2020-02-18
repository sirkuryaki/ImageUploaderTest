package com.yopdev.imageuploadertest.util;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.yopdev.imageuploadertest.Config;

import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

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
        return httpCode >= HTTP_OK && httpCode < HTTP_MULT_CHOICE;
    }

    public boolean isOffline() {
        return !isNetworkAvailable();
    }

    public boolean isTimeout() {
        return httpCode == HTTP_CLIENT_TIMEOUT;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Response:%d %s", httpCode, body);
    }

    @NonNull
    @WSResult.WSResultInterface
    public Integer getResult() {
        if (isSuccess()) {
            return WSResult.SUCCESS;
        } else if (isOffline()) {
            return WSResult.OFFLINE;
        } else if (isTimeout()) {
            return WSResult.TIMEOUT;
        } else {
            return WSResult.FAILURE;
        }
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

//    https://stackoverflow.com/a/30343108/5279996
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) Config.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }
}

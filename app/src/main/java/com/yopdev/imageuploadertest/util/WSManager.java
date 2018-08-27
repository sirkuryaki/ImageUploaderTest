package com.yopdev.imageuploadertest.util;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */
public class WSManager {

    private static final MediaType JPG = MediaType.parse("image/jpeg");
    private final String mAcceptLanguage;
    private final Executor networkIO;

    public WSManager(Executor networkIO) {
        this.networkIO = networkIO;
        mAcceptLanguage = Locale.getDefault().toString().replace("_", "-");
    }

    private OkHttpClient getNewHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false);

        return builder.build();
    }

    @NonNull
    public LiveData<WSResponse<String>> postImage(@NonNull String url,
                                                  @NonNull String applicationToken,
                                                  @NonNull String accessToken,
                                                  @NonNull String fileUri) {
        return new LiveData<WSResponse<String>>() {

            final AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {

                super.onActive();
                if (started.compareAndSet(false, true)) {
                    networkIO.execute(() -> postValue(executeImageRequest(url, applicationToken, accessToken, fileUri)));
                }
            }
        };
    }

    @NonNull
    private WSResponse<String> executeImageRequest(@NonNull String url,
                                                   @NonNull String applicationToken,
                                                   @NonNull String accessToken,
                                                   @NonNull String fileUri) {

        OkHttpClient httpclient = getNewHttpClient();

        WSResponse<String> response = new WSResponse<>();

        File file = new File(fileUri);

        try {
            RequestBody bodyImage = RequestBody.create(JPG, file);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("content", "android_foto", bodyImage)
                    .build();

            Request.Builder request = new Request.Builder()
                    .url(url)
                    .post(requestBody);

            request.addHeader("Accept-Language", mAcceptLanguage);
            request.addHeader("Authorization", "Bearer " + applicationToken);
            request.addHeader("Authorizationpostulante", accessToken);

            try {
                Response executed = httpclient.newCall(request.build()).execute();
                response.setHttpCode(executed.code());
                response.setBody(response.getHttpCode() + ": " + executed.body().string());
            } catch (IOException e) {
                response.setBody(e.toString());
                response.setHttpCode(408);
            }

        } catch (OutOfMemoryError error) {
            response.setBody("OutOfMemoryError");
            response.setHttpCode(408);
        }

        return response;
    }
}

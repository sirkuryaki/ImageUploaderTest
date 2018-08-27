package com.yopdev.imageuploadertest.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */
public abstract class NetworkResource<RequestType> {

    private final MediatorLiveData<WSResource<RequestType>> result;

    @MainThread
    public NetworkResource() {
        result = new MediatorLiveData<>();
        result.setValue(WSResource.loading(null));

        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        LiveData<WSResponse<RequestType>> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);

            //noinspection ConstantConditions
            if (response.isSuccess()) {
                result.postValue(WSResource.success(response.getData(), null));
            } else {
                result.postValue(WSResource.error(response.getBody(), response.data, null));
            }
        });
    }


    public LiveData<WSResource<RequestType>> asLiveData() {
        return result;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<WSResponse<RequestType>> createCall();


    @WorkerThread
    @SuppressWarnings("unused")
    protected void saveData(@NonNull RequestType data) {
    }
}
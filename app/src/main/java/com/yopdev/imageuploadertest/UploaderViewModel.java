package com.yopdev.imageuploadertest;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yopdev.imageuploadertest.util.NetworkResource;
import com.yopdev.imageuploadertest.util.WSResource;
import com.yopdev.imageuploadertest.util.WSResponse;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */
public class UploaderViewModel extends AndroidViewModel {

    private final MutableLiveData<String> photoPath;

    public UploaderViewModel(@NonNull Application application) {
        super(application);

        photoPath = new MutableLiveData<>();
    }

    @Nullable
    String getPhotoPath() {
        return photoPath.getValue();
    }

    void setPhotoPath(@Nullable String path) {
        photoPath.setValue(path);
    }

    @Nullable
    LiveData<WSResource<String>> uploadImage(@NonNull String url,
                                             @NonNull String applicationToken,
                                             @NonNull String accessToken,
                                             @NonNull String formDataPartName,
                                             @NonNull String formDataFilename) {

        String fileUri = photoPath.getValue();

        if (fileUri == null) {
            return null;
        }

        UploaderApplication application = getApplication();

        return new NetworkResource<String>() {
            @NonNull
            @Override
            protected LiveData<WSResponse<String>> createCall() {
                return application.getWsManager().postImage(url, applicationToken, accessToken, fileUri, formDataPartName, formDataFilename);
            }
        }.asLiveData();
    }

}

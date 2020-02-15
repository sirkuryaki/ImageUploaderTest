/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yopdev.imageuploadertest.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 * Based on https://github.com/googlesamples/android-architecture-components/
 *
 * @param <T> </T>
 */
public class WSResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String errorMessage;

    public final String debugUrl;

    private WSResource(@NonNull Status status, @Nullable T data, @Nullable String errorMessage, @Nullable String debugUrl) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
        this.debugUrl = debugUrl;
    }

    public static <T> WSResource<T> success(@NonNull T data, @Nullable String debugUrl) {
        return new WSResource<>(Status.SUCCESS, data, null, debugUrl);
    }

    public static <T> WSResource<T> error(String msg, @Nullable T data, @Nullable String debugUrl) {
        return new WSResource<>(Status.ERROR, data, msg, debugUrl);
    }

    public static <T> WSResource<T> loading(@Nullable T data) {
        return new WSResource<>(Status.LOADING, data, null, null);
    }

    public static <T> WSResource<T> timeout(String msg) {
        return new WSResource<>(Status.TIMEOUT, null, msg, null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WSResource<?> resource = (WSResource<?>) o;

        return status == resource.status && (errorMessage != null ? errorMessage.equals(resource.errorMessage) : resource.errorMessage == null) && (data != null ? data.equals(resource.data) : resource.data == null);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WSResource{" +
                "status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                '}';
    }
}

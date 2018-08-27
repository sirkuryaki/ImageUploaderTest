package com.yopdev.imageuploadertest;

import android.app.Application;

import com.yopdev.imageuploadertest.util.WSManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by sirkuryaki on 27/08/2018.
 * YOPdev.com
 */
public class UploaderApplication extends Application {

    private Executor networkExecutor;
    private WSManager wsManager;

    @Override
    public void onCreate() {
        super.onCreate();

        networkExecutor = Executors.newFixedThreadPool(3);
    }


    public WSManager getWsManager() {
        if (wsManager == null) {
            wsManager = new WSManager(networkExecutor);
        }
        return wsManager;
    }
}

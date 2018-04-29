package hefu.pad.sdk.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import hefu.pad.sdk.http.RokidServer;

/**
 * Created by zc on 2018/4/20.
 * 这个Service运行在rokid 硬件上
 *
 *
 */

public class RokidServerService extends Service {
    private String TAG="RokidServerService";
    RokidServer server;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        server= RokidServer.getInstance();
        server.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");

    }
}

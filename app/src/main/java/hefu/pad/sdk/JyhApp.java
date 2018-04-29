package hefu.pad.sdk;

import android.app.Application;
import android.content.Context;

import hefu.pad.sdk.utils.CrashHandler;

/**
 * Created by zc on 2018/4/18.
 */

public class JyhApp extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        mContext=this;
    }
    /**
     * 全局上下文
     */
    public static Context getContext() {
        return mContext;
    }
}

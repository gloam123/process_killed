package cn.yiming.pkilled;

import android.app.Application;
import android.content.Context;

import cn.yiming.pkilled.utils.ContextUtils;

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ContextUtils.initApplicationContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        boolean isBrowserProcess = !ContextUtils.getProcessName().contains(":");
        if (isBrowserProcess) {
        } else {
        }

    }
}
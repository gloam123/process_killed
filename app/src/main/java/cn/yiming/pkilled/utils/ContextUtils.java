package cn.yiming.pkilled.utils;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

public class ContextUtils {
    private static final String TAG = "ContextUtils";
    private static Context sApplicationContext;

    public static int dip2px(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }

    /**
     * Get the Android application context.
     *
     * Under normal circumstances there is only one application context in a process, so it's safe
     * to treat this as a global. In WebView it's possible for more than one app using WebView to be
     * running in a single process, but this mechanism is rarely used and this is not the only
     * problem in that scenario, so we don't currently forbid using it as a global.
     *
     * Do not downcast the context returned by this method to Application (or any subclass). It may
     * not be an Application object; it may be wrapped in a ContextWrapper. The only assumption you
     * may make is that it is a Context whose lifetime is the same as the lifetime of the process.
     */
    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    /**
     * Initializes the java application context.
     *
     * This should be called exactly once early on during startup, before native is loaded and
     * before any other clients make use of the application context through this class.
     *
     * @param appContext The application context.
     */
    public static void initApplicationContext(Context appContext) {
        // Conceding that occasionally in tests, native is loaded before the browser process is
        // started, in which case the browser process re-sets the application context.
        assert sApplicationContext == null || sApplicationContext == appContext
                || ((ContextWrapper) sApplicationContext).getBaseContext() == appContext;
        sApplicationContext = appContext;
    }

    /** @return The name of the current process. E.g. "org.chromium.chrome:privileged_process0". */
    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        }
        try {
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
            return (String) activityThreadClazz.getMethod("currentProcessName").invoke(null);
        } catch (Exception e) {
            // If fallback logic is ever needed, refer to:
            // https://chromium-review.googlesource.com/c/chromium/src/+/905563/1
            throw new RuntimeException(e);
        }
    }

    public static String getPackageName() {
        return sApplicationContext.getPackageName();
    }

}
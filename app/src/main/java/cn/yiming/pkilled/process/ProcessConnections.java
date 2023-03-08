package cn.yiming.pkilled.process;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class ProcessConnections {
    final static String BIND_IMPORTANT_CLS = "cn.yiming.pkilled.process.ImportantService";
    final static String BIND_WAIVE_CLS = "cn.yiming.pkilled.process.WaiveService";
    final static String BIND_MISC_CLS = "cn.yiming.pkilled.process.MiscService";

    public interface RemoteService {
        String getData();
    }

    public interface ProcessObserver {
        void onConnected(RemoteService service);
    }

    public static void startImportantProcess(Context context, ProcessObserver observer) {
        startBindProcess(context, observer, BIND_IMPORTANT_CLS, Context.BIND_IMPORTANT);
    }

    public static void startWaiveProcess(Context context, ProcessObserver observer) {
        startBindProcess(context, observer, BIND_WAIVE_CLS, Context.BIND_WAIVE_PRIORITY);
    }

    public static void startMiscProcess(Context context, ProcessObserver observer, int flags) {
        startBindProcess(context, observer, BIND_MISC_CLS, flags);
    }

    private static void startBindProcess(Context context, ProcessObserver observer, String cls, int extrasFlags) {
        Intent intent = new Intent();
        ComponentName bindServiceComponent = new ComponentName(context.getPackageName(), cls);
        intent.setComponent(bindServiceComponent);
        int defaultFlags = Context.BIND_AUTO_CREATE;

        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IBindProcessService iService = IBindProcessService.Stub.asInterface(service);
                observer.onConnected(() -> {
                    try {
                        return iService.getServiceData();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return "0";
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                observer.onConnected(null);
            }
        }, defaultFlags | extrasFlags);
    }


}

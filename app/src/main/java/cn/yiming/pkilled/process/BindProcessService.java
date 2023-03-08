package cn.yiming.pkilled.process;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;


public class BindProcessService extends Service {
    private int mCount;

    // Binder object used by clients for this service.
    private final IBindProcessService.Stub mBinder = new IBindProcessService.Stub() {
        @Override
        public void sendData(String aString) throws RemoteException {
            android.util.Log.e("gqg5", "BindProcessService.sendData aString=" + aString);
        }

        @Override
        public String getServiceData() throws RemoteException {
            return String.valueOf(++mCount);
        }

    };

    public BindProcessService() {}

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}

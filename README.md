# process_killed
## 小米UI14, 特定Bind标志创建的进程高概率被killed复现Demo


### Redmi K40 Pro MIUI14.0.3(android13) 异常系统: 宿主进程切后台后子进程高概率被killed.
![Image text](https://github.com/gloam123/process_killed/blob/main/docs/redmi_k40_miui14.webp)


### Oneplus 7t android 10.0 正常系统: 宿主进程切后台子进程不会被killed.
![Image text](https://github.com/gloam123/process_killed/blob/main/docs/oneplus_7t_android10.webp)


## 核心代码如下:
```
// implement
private void startBindProcess(Context context, ProcessObserver observer, String cls, int extrasFlags) {
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


// usage
// case 1: 高概率被killed
startBindProcess(context, observer, BIND_IMPORTANT_CLS, Context.BIND_IMPORTANT);

// case 2: 不会被killed
startBindProcess(context, observer, BIND_WAIVE_CLS, Context.BIND_WAIVE_PRIORITY);
```

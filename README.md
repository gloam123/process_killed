# MIUI14上子进程高概率被killed 复现Demo.
### MIUI14上, 宿主进程切后台进入其他程序后, 子进程高概率被killed. 下图分别展示了Redmi K40 MIUI14.0.3和Oneplus 7t android 10.0上的不同行为.
![Redmi K40 Pro MIUI14.0.3](https://github.com/gloam123/process_killed/blob/master/docs/redmi_k40_miui14.webp) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Oneplus 7t android 10.0](https://github.com/gloam123/process_killed/blob/master/docs/oneplus_7t_android10.webp)
<br>
&ensp;&ensp;点击"Start Process"会根据上面不同的标致调用bindService()启动不同的进程.
<br>
&ensp;&ensp;在MIUI14(android13)上, 宿主进程切后台后再启动其他APP, BIND_IMPORTANT等标志启动的子进程会被kill, 而BIND_WAIVE_PRIORITY标志启动的进程则不会被kill. 和MIUI其他版本或其他系统行为不一致.

### 下面LOG显示了ImportantService和MiscService进程被kill的相关日志
```
2023-03-08 19:03:47.214  1872-2272  Boost                    D  hostingType=service, hostingName={cn.yiming.pkilled/cn.yiming.pkilled.process.ImportantService}, callerPackage=cn.yiming.pkilled, isSystem=false, isBoostNeeded=false.
2023-03-08 19:03:47.214  1872-2272  ActivityManager          I  Start proc 23411:cn.yiming.pkilled:ImportantService/u0a110 for service {cn.yiming.pkilled/cn.yiming.pkilled.process.ImportantService} caller=cn.yiming.pkilled
2023-03-08 19:03:48.883  1872-2514  ActivityManager          I  Killing 23411:cn.yiming.pkilled:ImportantService/u0a110 (adj 700): MiuiMemoryService(service)
2023-03-08 19:03:48.883  1872-2514  SmartPower...110(23411)  I  background->died(885ms) R(process died ) adj=700.
2023-03-08 19:03:48.883 16098-16193 IdProviderImpl           D  getOAID from com.miui.analytics
2023-03-08 19:03:48.883  1872-2273  libprocessgroup          I  Successfully killed process cgroup uid 10110 pid 23442 in 5ms
2023-03-08 19:03:47.977  1872-2247  SchedBoost               D  cn.yiming.pkilled:ImportantService boosting, skip reset priority
2023-03-08 19:03:47.239  1872-2272  Boost                    D  hostingType=service, hostingName={cn.yiming.pkilled/cn.yiming.pkilled.process.MiscService}, callerPackage=cn.yiming.pkilled, isSystem=false, isBoostNeeded=false.
2023-03-08 19:03:47.239 23430-23430 ed:WaiveService          E  Not starting debugger since process cannot load the jdwp agent.
2023-03-08 19:03:47.239  1872-2272  ActivityManager          I  Start proc 23442:cn.yiming.pkilled:MiscService/u0a110 for service {cn.yiming.pkilled/cn.yiming.pkilled.process.MiscService} caller=cn.yiming.pkilled
2023-03-08 19:03:48.877  3596-11045 RecentsModel             W  getRunningTask
2023-03-08 19:03:48.877  1872-2514  ActivityManager          I  Killing 23442:cn.yiming.pkilled:MiscService/u0a110 (adj 700): MiuiMemoryService(service)
2023-03-08 19:03:48.878  1872-2514  SmartPower...110(23442)  I  background->died(880ms) R(process died ) adj=700.
```

### 核心代码如下:
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

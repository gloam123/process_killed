// IBindProcessService.aidl
package cn.yiming.pkilled.process;

// Declare any non-default types here with import statements

interface IBindProcessService {
    oneway void sendData(String data);
    String getServiceData();
}
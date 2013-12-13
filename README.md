AIDL_COMMUNICATE-CALLBACK
=========================

    本项目主要在于学习Android中通过AIDL完成进程间通信与回调，项目组成：AIDL_SERVICE_LIB[库项目]、AIDL_CLIENT。
    ---------------------------------------------------------------------------------------------------------
    
    ### 那么主要的需要实现的功能通过几个AIDL文件就可以获知:
    
    ###IAIDLService.aidl:
    package cn.dreamtobe.aidl.service;
    import cn.dreamtobe.aidl.service.Book;
    import cn.dreamtobe.aidl.service.IServiceCallback;
    interface IAIDLService{
    int getCount();
    Book getBook();
    boolean startTestTask();
    void stopTestTask();
    void registerCallback(IServiceCallback paramIServiceCallback);
    void unregisterCallback(IServiceCallback paramIServiceCallback);
    }
    
    ###IServiceCallback.aidl:
    package cn.dreamtobe.aidl.service;
    import cn.dreamtobe.aidl.service.Book;
    interface IServiceCallback {
    void handlerCommEvent(int msgId, int param);
  	void callbackBookEvent(int cmd, in Book book);
    }
    
    

![image](https://github.com/Jacksgong/AIDL_COMMUNICATE-CALLBACK/raw/master/aidl_readme/raw/com_task.png)
![image](https://github.com/Jacksgong/AIDL_COMMUNICATE-CALLBACK/raw/master/aidl_readme/raw/bind_succeed.png)
![image](https://github.com/Jacksgong/AIDL_COMMUNICATE-CALLBACK/raw/master/aidl_readme/raw/unbind.png)
![image](https://github.com/Jacksgong/AIDL_COMMUNICATE-CALLBACK/raw/master/aidl_readme/raw/unbind_request.png)

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
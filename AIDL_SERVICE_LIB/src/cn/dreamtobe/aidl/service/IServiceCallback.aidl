package cn.dreamtobe.aidl.service;

import cn.dreamtobe.aidl.service.Book;

interface IServiceCallback {

	void handlerCommEvent(int msgId, int param);
	
	void callbackBookEvent(int cmd, in Book book);
}
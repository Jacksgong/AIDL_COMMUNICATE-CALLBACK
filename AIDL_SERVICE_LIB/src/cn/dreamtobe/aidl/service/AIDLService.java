package cn.dreamtobe.aidl.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author jacksgong
 *         http://blog.dreamtobe.cn
 *         2013. 12. 14
 * 
 */
public class AIDLService extends Service {

	private static final String TAG = "cn.dreamtobe.aidl.service";

	public static final String AIDL_SERVICE_ACTION = "cn.dreamtobe.aidl.service";

	public static final int CMD_COMMENT = 1;
	public static final int CMD_REQUEST_DISCONNECT = 2;

	private static final int INTERVAL_SEC = 1;
	public static final int MAX_TEST_VALUE = 100;

	private Timer mTimer = null;
	private int mTestIteValue = 0;
	private volatile boolean mIsStartTask = false;

	private final RemoteCallbackList<IServiceCallback> mCallbackList = new RemoteCallbackList<IServiceCallback>();

	private AIDLServerBinder serviceBinder = new AIDLServerBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "AIDLServer.onCreate()...");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "AIDLServer.onDestroy()...");
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		callBack(CMD_REQUEST_DISCONNECT, 1);

		mCallbackList.kill();
	}

	private void callBack(int cmd, int param) {
		int N = mCallbackList.beginBroadcast();
		try {
			for (int i = 0; i < N; i++) {
				mCallbackList.getBroadcastItem(i).handlerCommEvent(cmd, param);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		mCallbackList.finishBroadcast();
	}

	private void callBack(int cmd, Book book) {
		int N = mCallbackList.beginBroadcast();
		try {
			for (int i = 0; i < N; i++) {
				mCallbackList.getBroadcastItem(i).callbackBookEvent(cmd, book);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		mCallbackList.finishBroadcast();

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "AIDLServer.onBind()...");
		return serviceBinder;
	}

	private class AIDLServerBinder extends IAIDLService.Stub {

		@Override
		public Book getBook() throws RemoteException {
			Book b = new Book();
			b.setBookName("aidlBook : " + String.valueOf(1 + mTestIteValue));
			b.setBookPrice(mTestIteValue);
			return b;
		}

		@Override
		public int getCount() throws RemoteException {
			return mTestIteValue;
		}

		@Override
		public boolean startTestTask() throws RemoteException {
			if (mIsStartTask) {
				return false;
			}

			mIsStartTask = true;
			mTimer = new Timer();
			mTimer.schedule(new MyTimerTask(), 0L, INTERVAL_SEC * 1000L);

			return true;
		}

		@Override
		public void stopTestTask() throws RemoteException {
			if (mIsStartTask) {
				mTimer.cancel();
				mTimer = null;
			}

			mIsStartTask = false;
		}

		@Override
		public void registerCallback(IServiceCallback paramIServiceCallback) throws RemoteException {
			if (paramIServiceCallback != null) {
				mCallbackList.register(paramIServiceCallback);
			}
		}

		@Override
		public void unregisterCallback(IServiceCallback paramIServiceCallback) throws RemoteException {
			if (paramIServiceCallback != null) {
				mCallbackList.unregister(paramIServiceCallback);
			}
		}

	}

	class MyTimerTask extends TimerTask {

		public void run() {
			if (mTestIteValue == MAX_TEST_VALUE) {
				mTestIteValue = 0;
			}

			mTestIteValue++;
			callBack(CMD_COMMENT, mTestIteValue);
			if (mTestIteValue % 4 == 0)
				callBack(CMD_COMMENT, new Book("book_test" + String.valueOf(mTestIteValue), 4 * mTestIteValue));
		}
	}
}
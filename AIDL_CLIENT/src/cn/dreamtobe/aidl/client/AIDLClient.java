package cn.dreamtobe.aidl.client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.dreamtobe.aidl.service.AIDLService;
import cn.dreamtobe.aidl.service.Book;
import cn.dreamtobe.aidl.service.IAIDLService;
import cn.dreamtobe.aidl.service.IServiceCallback;

/**
 * 
 * @author jacksgong
 *         http://blog.dreamtobe.cn
 *         2013. 12. 14
 * 
 */
public class AIDLClient extends Activity implements OnClickListener, ServiceConnection {
	private static final String TAG = "cn.dreamtobe.aidl.client";

	private ProgressBar mProgressBar;
	private TextView mReceiveValueTv;
	private TextView mCallBackTv;

	private Button mStartBtn;
	private Button mStartTestTaskBtn;
	private Button mStopBtn;
	private Button mStopTestTaskBtn;
	private Button mGetSvrValueBtn;

	private boolean mIsBind;
	private Intent mServiceIntent;
	private IAIDLService mAIDLService;
	private ServiceCallBack mCallBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initService();
		initView();
	}

	private void initService() {
		mServiceIntent = new Intent(AIDLService.AIDL_SERVICE_ACTION);
		mCallBack = new ServiceCallBack();
	}

	private void initView() {
		setContentView(R.layout.main);

		setTitle(getString(R.string.app_name) + getString(R.string.tips));

		mReceiveValueTv = (TextView) findViewById(R.id.receive_Tv);
		mCallBackTv = (TextView) findViewById(R.id.callBack_Tv);

		mProgressBar = (ProgressBar) findViewById(R.id.progress);
		mProgressBar.setMax(AIDLService.MAX_TEST_VALUE);

		mStartBtn = (Button) findViewById(R.id.start_Btn);
		mStopBtn = (Button) findViewById(R.id.stop_Btn);
		mGetSvrValueBtn = (Button) findViewById(R.id.get_Btn);
		mStartTestTaskBtn = (Button) findViewById(R.id.start_task_Btn);
		mStopTestTaskBtn = (Button) findViewById(R.id.stop_task_Btn);

		mStartBtn.setOnClickListener(this);
		mStopBtn.setOnClickListener(this);
		mGetSvrValueBtn.setOnClickListener(this);
		mStartTestTaskBtn.setOnClickListener(this);
		mStopTestTaskBtn.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {

		if (mAIDLService != null) {
			try {
				mAIDLService.unregisterCallback(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			if (mIsBind) {
				unbindService(this);
			}
		}
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.start_Btn:
				Log.i(TAG, "bind button click.");
				if (isReadyService()) {
					Log.d(TAG, "already bind.");
					return;
				}

				mIsBind = bindService(mServiceIntent, this, BIND_AUTO_CREATE);

				break;

			case R.id.stop_Btn:
				Log.i(TAG, "unbind button click.");

				if (!checkServiceState()) {
					return;
				}

				mAIDLService.unregisterCallback(mCallBack);
				unbindService(this);
				mIsBind = false;
				mAIDLService = null;
				mReceiveValueTv.setText(getString(R.string.unbind_tip));
				break;

			case R.id.get_Btn:
				Log.i(TAG, "get button click.");
				if (!checkServiceState()) {
					return;
				}

				if (mAIDLService.getBook() != null) {
					mReceiveValueTv.setText(mAIDLService.getBook().getBookName());
				}
				break;

			case R.id.start_task_Btn:
				Log.i(TAG, "start task button click.");
				if (!checkServiceState()) {
					return;
				}

				if (!mAIDLService.startTestTask()) {
					mReceiveValueTv.setText(getString(R.string.already_start_task));
				}
				break;

			case R.id.stop_task_Btn:
				Log.i(TAG, "stop task button click.");

				if (!checkServiceState()) {
					return;
				}

				mAIDLService.stopTestTask();
				break;

			default:
				break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private boolean isReadyService() {
		return mAIDLService != null && mIsBind;
	}

	private boolean checkServiceState() {
		if (!isReadyService()) {
			Log.d(TAG, "Not Yet bind.");
			if (mReceiveValueTv != null) {
				mReceiveValueTv.setText(getString(R.string.not_bind_tip));
			}
			return false;
		}
		return true;
	}

	@Override
	public void onServiceConnected(ComponentName comName, IBinder service) {
		Log.i(TAG, "AIDLCient.onServiceConnected()...");
		mAIDLService = IAIDLService.Stub.asInterface(service);

		try {
			mAIDLService.registerCallback(mCallBack);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		new Handler(getMainLooper()).post(new Runnable() {
			public void run() {
				mReceiveValueTv.setText(AIDLClient.this.getString(R.string.bind_tip));
			}
		});

	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		Log.i(TAG, "AIDLCient.onServiceDisconnected()...");
		mIsBind = false;
	}

	private class ServiceCallBack extends IServiceCallback.Stub {

		@Override
		public void handlerCommEvent(int msgId, final int param) throws RemoteException {
			switch (msgId) {
			case AIDLService.CMD_COMMENT:
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						mCallBackTv.setText(String.valueOf(param));
					}
				});

				break;
			case AIDLService.CMD_REQUEST_DISCONNECT:
				if (mAIDLService != null) {
					mAIDLService.unregisterCallback(mCallBack);
					mIsBind = false;
					mAIDLService = null;
				}
				break;

			default:
				break;
			}
		}

		@Override
		public void callbackBookEvent(int cmd, final Book book) throws RemoteException {

			switch (cmd) {
			case AIDLService.CMD_COMMENT:
				new Handler(getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						if (book != null) {
							mReceiveValueTv.setText(book.getBookName());
						}
					}
				});
				break;

			default:
				break;
			}
		}
	}

}
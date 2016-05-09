/*******************************************************************************
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.tencent.wetest.service;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.activity.LoadingActivity;
import com.tencent.wetest.activity.MainActivity;
import com.tencent.wetest.activity.TestResultsActivity;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.common.util.ReportUtil;
import com.tencent.wetest.common.util.ToolUtil;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class FloatViewService extends Service {

	private WindowManager mWmManager ;
	private LayoutParams mWMParas ;
	private View mView;

	private AlertDialog timeout_d;
	private static final int MSG_DISMISS_DIALOG = 0;
	private final static int ONGOING_NOTIFICATION = 1;

	private final int SCREEN_VERTICAL = 1;
	private final int SCREEN_HORIZONTALLY = 0;

	private ImageView floatBtn;
	private float x , y , startX ,startY ,mTouchStartX ,mTouchStartY;

	private ApkInfo apkinfo;
	private boolean monitorFlag = true;
	private long sceneStartClock;
	private long reportStartClock;
	private boolean isStaStop = true;
	private TextView timeView;
	private final int delaytime = 1000;
	private int over_ratio;
	private final int TEST_TIMEOUT = 2 * 60 * 60 * 1000;
	private int dismissCount = -1;
	private AlertDialog.Builder timeout_b;
	private AlertDialog.Builder start_scene_b;
	private AlertDialog start_scene_d;
	private View textEntryView;
	private AutoCompleteTextView E_scene;
	private List<String> scene_list;
	private ArrayAdapter<String> scene_adapter;
	private String test_scene;
	public static boolean is_scene_runing;
	public  boolean flag;
	public  static  TextView fps;

	//private SdkFPSRecevier fpsRecevier;

	@Override
	public void onCreate() {


		((WTApplication)WTApplication.getContext()).setMonitorFlag(false);
		mView = LayoutInflater.from(this).inflate(R.layout.floating, null);
		createView();
		apkinfo = ((WTApplication)WTApplication.getContext()).getApkinfo();

		Logger.info("apkInfo " + apkinfo);
		((WTApplication)WTApplication.getmContext()).setTestStart(true);

		//gameMonitorHandler.postDelayed(new Thread(gameMonitorTask), 500);

		Intent fpsWindow = new Intent(FloatViewService.this,FloatExitService.class);
		startService(fpsWindow);

		//fpsRecevier = new SdkFPSRecevier();

		//registerReceiver(fpsRecevier,new IntentFilter(EventConstant.Stastic.SDK_FPS));
		startTest();
	}

	@SuppressLint({ "SimpleDateFormat", "ClickableViewAccessibility" })
	private void createView() {

		timeView = (TextView)mView.findViewById(R.id.time);

		LayoutInflater factory = LayoutInflater.from(FloatViewService.this);

		mWmManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

		mWMParas = ((WTApplication) getApplication()).getWMParams();

		mWMParas.type = 2002;
		mWMParas.flags = 8  ;

//		mWMParas.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//		        | LayoutParams.FLAG_NOT_FOCUSABLE
//		        | LayoutParams.FLAG_NOT_TOUCHABLE;


		mWMParas.gravity = Gravity.LEFT | Gravity.TOP;

		mWMParas.x = 0;
		mWMParas.y = 0;

		mWMParas.width = LayoutParams.WRAP_CONTENT;
		mWMParas.height = LayoutParams.WRAP_CONTENT;
		mWMParas.format = 1;

		mWmManager.addView(mView, mWMParas);

		floatBtn = (ImageView)mView.findViewById(R.id.floatBtn);


		floatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!flag) {

					if (is_scene_runing) {

						is_scene_runing = false;

						floatBtn.setImageResource(R.drawable.float_scene_start);

						Toast.makeText(
								FloatViewService.this,
								getResources().getString(R.string.scene) + "  - "
										+ test_scene
										+ " - " + getResources().getString(R.string.end), Toast.LENGTH_SHORT).show();


						((WTApplication) getApplication()).setTag(getResources().getString(R.string.end) + (new Date()).getTime());

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}

					} else {

						start_scene_d.show();


					}

				}

			}
		});
		floatBtn.setOnTouchListener(flowOnTouchListener);

		textEntryView = factory.inflate(R.layout.scene, null);
		E_scene = (AutoCompleteTextView) textEntryView.findViewById(R.id.E_scene);

		scene_list = new ArrayList<String>();

		scene_adapter = new ArrayAdapter<String>(
				FloatViewService.this,
				android.R.layout.simple_dropdown_item_1line,
				scene_list);
		E_scene.setAdapter(scene_adapter);

		start_scene_b = new AlertDialog.Builder((WTApplication) getApplication());
		start_scene_b.setView(textEntryView);
		start_scene_b.setTitle(getResources().getString(R.string.confirm));
		start_scene_b.setMessage(getResources().getString(R.string.scene_confirm));
		start_scene_b.setPositiveButton(getResources().getString(R.string.btnOK), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				if (!E_scene.getText().toString().equals("")) {
					if (!scene_list.contains(E_scene.getText().toString()))
						scene_adapter.add(E_scene.getText().toString());
					test_scene = E_scene.getText().toString();

					start_scene_b.setView(textEntryView);

					sceneStartClock = SystemClock.uptimeMillis();

					floatBtn.setImageResource(R.drawable.float_scene_close);
//					Drawable drawable = getResources().getDrawable(R.drawable.flow_ctr_start);
//					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//					scene.setCompoundDrawables(null, drawable, null, null);

					((WTApplication) getApplication()).setTag(E_scene.getText().toString());

					Toast.makeText(FloatViewService.this,
							getResources().getString(R.string.scene) + " - " + test_scene + " - " + getResources().getString(R.string.start),
							Toast.LENGTH_SHORT).show();

					E_scene.setText("");
					is_scene_runing = true;
				}
			}

		});
		start_scene_b.setNegativeButton(getResources().getString(R.string.cancel), null);
		start_scene_d = start_scene_b.create();
		start_scene_d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		timeout_b = new AlertDialog.Builder((WTApplication) getApplication());

		timeout_b.setTitle(getResources().getString(R.string.confirm));
		timeout_b.setMessage(getResources().getString(R.string.timeout_confirm));
		timeout_b.setPositiveButton(getResources().getString(R.string.end) + "(30)", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				((WTApplication) WTApplication.getContext()).setMonitorFlag(true);
				dismissCount = -1;
			}

		});
		timeout_b.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				dismissCount = -1;
			}

		});

		timeout_d = timeout_b.create();
		timeout_d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		fps = (TextView)mView.findViewById(R.id.fps);

		SharedPreferences setting = ((WTApplication)WTApplication.getContext()).getmSetting();

		if(((WTApplication)WTApplication.getContext()).isRoot() && setting.getBoolean("setting_fps",true))  {

			fps.setVisibility(View.VISIBLE);

		}else {

			fps.setVisibility(View.GONE);
		}


	}

	private OnTouchListener flowOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			x = event.getRawX();

			flag = false;
			if (SCREEN_VERTICAL == checkScreenOrientation()) {
				y = event.getRawY()
						- getStatusBarHeight(WTApplication.getContext());
			} else {
				y = event.getRawY();
			}

			switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					startX = x;
					startY = y;

					mTouchStartX = event.getX();
					mTouchStartY = event.getY();

					flag = false;
					break;

				case MotionEvent.ACTION_MOVE:

					flag = updateViewPosition();

					break;
				case MotionEvent.ACTION_UP:

					flag = updateViewPosition();

					mTouchStartX = mTouchStartY = 0;


					break;

			}

			return flag;
		}
	};

	private boolean updateViewPosition() {

		if (Math.abs(x - startX) > 10 || Math.abs(y - startY) > 10) {
			mWMParas.x = (int) (x - mTouchStartX);
			mWMParas.y = (int) (y - mTouchStartY);
			mWmManager.updateViewLayout(mView, mWMParas);

		}


		if(Math.abs(x - startX) > 10 || Math.abs(y - startY) > 10){
			Logger.info("update return true x : " + Math.abs(x - mTouchStartX)  + " y : " + Math.abs(y - mTouchStartY) );
			return true;
		}

		Logger.info("update return false");
		return  false;

	}

	@SuppressWarnings("deprecation")
	private int checkScreenOrientation() {

		WindowManager wm = (WindowManager) WTApplication.getContext().getSystemService(Context.WINDOW_SERVICE);

		int dev_width = 480;
		if (null != wm.getDefaultDisplay()) {
			dev_width = wm.getDefaultDisplay().getWidth();
		}

		int dev_height = 800;
		if (null != wm.getDefaultDisplay()) {
			dev_height = wm.getDefaultDisplay().getHeight();
		}
		int orientation = SCREEN_VERTICAL;

		if (dev_width > dev_height) {
			orientation = SCREEN_HORIZONTALLY;
		}

		return orientation;
	}

	public int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		int sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbar;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		showNotification(true);
		super.onStart(intent, startId);
	}

	public void showNotification(boolean running) {

		Notification ni = new Notification();
		ni.tickerText = "wetest";
		ni.icon = R.drawable.logo;
		ni.when = System.currentTimeMillis();

		Context ctx = this.getApplicationContext();

		Intent i = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(ctx, 0, i, 0);
		ni.setLatestEventInfo(ctx, "wetest", running ? "service is running" : "service is stopped", pi);

		startForeground(ONGOING_NOTIFICATION, ni);

	}

	@Override
	public void onDestroy() {

		Logger.info("FLoatView onDestory");

		Intent stopFps = new Intent(FloatViewService.this,FloatExitService.class);
		stopService(stopFps);


		mWmManager.removeView(mView);
		monitorFlag = false;

		handler.removeCallbacks(task);
		gameMonitorHandler.removeCallbacks(gameMonitorTask);

		((WTApplication)WTApplication.getmContext()).setTestStart(false);

		//unregisterReceiver(fpsRecevier);

		super.onDestroy();


	}

	@Override
	public IBinder onBind(Intent intent) {
		return new FLBinder();
	}


	@Override
	public boolean onUnbind(Intent intent)
	{
		Logger.info("FloatViewService onUnbind");
		return super.onUnbind(intent);
	}

	class GameMonitorRunnable implements Runnable {

		public void run() {

			new Thread(new Runnable(){

				@Override
				public void run() {

					while(monitorFlag){
						monitorGameProcess();;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}

			}).start();

		}

	}

	private Handler gameMonitorHandler = new Handler();

	private Runnable gameMonitorTask = new Runnable() {
		public void run() {


			monitorGameProcess();
			gameMonitorHandler.postDelayed(new Thread(this), 500);

		}
	};

	public void monitorGameProcess() {

		// String pidInfo =   apkinfo.getPackagename() == null ? "-1" : "" + ApkManager.getPidByPackageName(apkinfo.getPackagename())[0];

		//Logger.info("current game is: "+ apkinfo.getPackagename() + "pid is:" + ApkManager.getPidByPackageName(apkinfo.getPackagename())[0] + "flag : " + monitorFlag);

		if( (apkinfo.getPackagename() == null || ApkManager.getPidByPackageName(apkinfo.getPackagename())[0] == -1) && monitorFlag ){

			Logger.info("stopTest in monitorGameProcess");
			stopTestHandler.post(new Thread(new StopTestRunnable()));
			monitorFlag = false;


		}

	}

	private Handler handler = new Handler();

	private Runnable task = new Runnable() {

		public void run() {

			dataRefresh();
			handler.postDelayed(this, delaytime);

		}

	};

	public void dataRefresh() {

		long testtime = (SystemClock.uptimeMillis()  - reportStartClock ) ;

		timeView.setText(testtime/1000 + "s");

		if(testtime > over_ratio * TEST_TIMEOUT){

			if(over_ratio == 2){

				Logger.info("stop Test in dataRefresh");
				stopTestHandler.post(new Thread(new StopTestRunnable()));

			}else{

				dismissCount = 15;
				if (timeout_d != null)
					timeout_d.show();
				over_ratio++;

			}

		}

		if(dismissCount >= 0){

			Message m = dismissHandler.obtainMessage();
			m.what = dismissCount ;
			dismissHandler.sendMessage(m);

		}

	}

	private Handler dismissHandler = new DismissHandler(this);
	static class DismissHandler extends Handler{
		private final WeakReference<FloatViewService> mService;
		private final FloatViewService mContext;
		public DismissHandler(FloatViewService service) {
			mService = new WeakReference<FloatViewService>(service);
			mContext = service;
		}
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case MSG_DISMISS_DIALOG:

					if (mContext.timeout_d != null && mContext.timeout_d.isShowing()) {

						mContext.timeout_d.dismiss();

						Logger.info("stopTest in dissmisshandler");
						mContext.stopTest();;

					}

					break;

				default:
					mContext.timeout_d.getButton(AlertDialog.BUTTON_POSITIVE).setText("����("+ msg.what +")");
					break;

			}

			mContext.dismissCount--;

		}
	}

	public void startTest() {
		handler.postDelayed(task, delaytime);

		((WTApplication) WTApplication.getContext()).getReport().getDatalist().clear();

		Intent service = new Intent( WTApplication.getContext(), StasticService.class );
		startService(service);

		isStaStop = false;

		reportStartClock = SystemClock.uptimeMillis();

		over_ratio = 1;

		((WTApplication) WTApplication.getContext()).getReport().setTimeStart(SystemClock.uptimeMillis());

		Date now = new Date();
		String name = "wt" + now.getTime();
		String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wetest";
		ToolUtil.createDir(fileDir);
		File f = new File( fileDir + "/" + name);
		((WTApplication)WTApplication.getContext()).setCurrTestFile(f);

		Toast.makeText(FloatViewService.this, getResources().getString(R.string.test_start),Toast.LENGTH_SHORT).show();

	}

	private Handler stopTestHandler = new StopTestHandler(this);
	static class StopTestHandler extends Handler{

		private final WeakReference<FloatViewService> mService;
		private final FloatViewService mContext;

		public StopTestHandler(FloatViewService service) {
			mService = new WeakReference<FloatViewService>(service);
			mContext = service;
		}

		@Override
		public void handleMessage(Message msg) {


			Intent serviceStop = new Intent();
			serviceStop.setClass(mContext, FloatViewService.class);
			mContext.stopService(serviceStop);


			Logger.info("stopTestHandler FloatExitService stop service");
			Intent stopIntent = new Intent(mContext,StasticService.class);
			mContext.stopService(stopIntent);

			((WTApplication) WTApplication.getContext()).getReport().setTimeTEnd(SystemClock.uptimeMillis());

			((WTApplication) WTApplication.getContext()).getReport().getDatalist().clear();

			Intent backIntent = null;

			if(mContext.apkinfo.getPackagename() != null)
				backIntent = new Intent(mContext,TestResultsActivity.class);
			else
				backIntent = new Intent(mContext,LoadingActivity.class);

			backIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			backIntent.putExtra("unexpectStop", true);
			backIntent.putExtra("filename", ((WTApplication) WTApplication.getContext()).getCurrTestFile().getName());

			((WTApplication) WTApplication.getContext()).setTestStart(false);

			mContext.startActivity(backIntent);


		}
	}

	class StopTestRunnable implements Runnable {

		public void run() {

			Logger.info("StopTestRunnable running");

			new Thread(new Runnable() {
				@Override
				public void run() {
					ReportUtil.saveReport();

					Message msg = new Message();
					stopTestHandler.handleMessage(msg);
				}
			}).start();


		}

	}

	public void stopTest(){
		stopTestHandler.post(new Thread(new StopTestRunnable()));
	}

	class FLBinder extends Binder implements IService
	{

		@Override
		public void invokeServiceMethod()
		{

			stopTest();
		}

	}

}

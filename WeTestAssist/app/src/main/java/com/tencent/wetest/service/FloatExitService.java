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


import java.lang.reflect.Field;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.common.util.SuUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class FloatExitService extends Service {

	private WindowManager mWmManager ;
	private LayoutParams mWMParas ;
	private View mView;

	public static ImageView btnExit;

	private final int SCREEN_VERTICAL = 1;
	private final int SCREEN_HORIZONTALLY = 0;

	private float mTouchStartX;
	private float mTouchStartY;

	private float startX;
	private float startY;

	private float x;
	private float y;

	private IService iService = null;
	private FLServiceConnection conn;

	private boolean flag;
	private ApkInfo apkinfo;
	private boolean monitorFlag;

	private AlertDialog.Builder exit_b;
	private AlertDialog exit_d;


	@Override
	public void onCreate() {

		mView = LayoutInflater.from(this).inflate(R.layout.fps_window, null);

		btnExit = (ImageView) mView.findViewById(R.id.floatBtnExit);

		mWmManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

		mWMParas = new LayoutParams();

		mWMParas.type = 2002;

		mWMParas.flags |= 8;
		mWMParas.gravity = Gravity.RIGHT | Gravity.TOP;

		mWMParas.x = 0;
		mWMParas.y = 0;

		mWMParas.width = LayoutParams.WRAP_CONTENT;
		mWMParas.height = LayoutParams.WRAP_CONTENT;
		mWMParas.format = 1;

		mWmManager.addView(mView, mWMParas);

		btnExit.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				x = mWmManager.getDefaultDisplay().getWidth() - event.getRawX();

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

						break;

					case MotionEvent.ACTION_MOVE:

						flag = updateViewPosition();
						break;

					case MotionEvent.ACTION_UP:

						flag = updateViewPosition();
						mTouchStartX = mTouchStartY = 0;
						break;
				}
				return false;
			}
		});

		conn = new FLServiceConnection();

		Intent bindIntent = new Intent(this,FloatViewService.class);
		this.bindService(bindIntent, conn, BIND_AUTO_CREATE);


		monitorFlag = true;

		apkinfo = ((WTApplication)WTApplication.getContext()).getApkinfo();

		Logger.info("GameMonitorRunnable start ");
		new Thread(new GameMonitorRunnable()).start();

		exit_b = new AlertDialog.Builder((WTApplication) getApplication());
		exit_b.setTitle(getResources().getString(R.string.confirm));
		exit_b.setMessage(getResources().getString(R.string.exit_test_confirm));
		exit_b.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {


					if(!FloatViewService.is_scene_runing){

						if (iService != null) {
							monitorFlag = false;
							iService.invokeServiceMethod();

							unbindService(conn);

						}
					}else{

						Toast.makeText(getApplicationContext(),getResources().getString(R.string.scene_is_not_end),Toast.LENGTH_SHORT).show();
					}




			}

		});
		exit_b.setNegativeButton(getResources().getString(R.string.continue_test), null);
		exit_d = exit_b.create();
		exit_d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

		btnExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!flag) {
					exit_d.show();
				}

			}
		});

	}

	class GameMonitorRunnable implements Runnable {

		public void run() {

			new Thread(new Runnable(){

				@Override
				public void run() {


					Logger.info("GameMonitorRunnable started");
					while(monitorFlag){
						monitorGameProcess();
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

	public void monitorGameProcess() {

		// String pidInfo =   apkinfo.getPackagename() == null ? "-1" : "" + ApkManager.getPidByPackageName(apkinfo.getPackagename())[0];

		//Logger.info("current game is: "+ apkinfo.getPackagename() + "pid is:" + ApkManager.getPidByPackageName(apkinfo.getPackagename())[0] + "flag : " + monitorFlag);

		if( (apkinfo.getPackagename() == null || ApkManager.getPidByPackageName(apkinfo.getPackagename())[0] == -1|| ((WTApplication)WTApplication.getContext()).isMonitorFlag()) && monitorFlag ){

			if (iService != null) {

				iService.invokeServiceMethod();

				try {
					unbindService(conn);
				}catch (Exception e){
					Logger.error("FloatExitService Exception : " + e.toString());
				}


			}

			((WTApplication)WTApplication.getContext()).setMonitorFlag(false);
			monitorFlag = false;

		}

	}
	class FLServiceConnection implements ServiceConnection
	{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{

			iService = (IService)service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{

		}

	}

	private boolean updateViewPosition() {

		if (Math.abs(x - startX) > 10 || Math.abs(y - startY) > 10) {
			mWMParas.x = (int) (x - mTouchStartX);
			mWMParas.y = (int) (y - mTouchStartY);
			mWmManager.updateViewLayout(mView, mWMParas);
		}


		if (Math.abs(x - startX) > 10 || Math.abs(y - startY) > 10) {

			Logger.info("update exit return true x : " + Math.abs(x - mTouchStartX)  + " y : " + Math.abs(y - mTouchStartY) );
			return true;

		}
		return false;

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
		int orientation = SCREEN_VERTICAL; // 0:???? 1:????

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
	public void onDestroy() {

		Logger.info("unbindserice inf exit service ");
		mWmManager.removeView(mView);

		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}



}

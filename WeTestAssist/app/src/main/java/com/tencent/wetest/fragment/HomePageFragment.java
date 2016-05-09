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

package com.tencent.wetest.fragment;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.activity.AppListActivity;
import com.tencent.wetest.activity.MainActivity;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.service.FloatViewService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Date;

public class HomePageFragment extends Fragment {

	private LinearLayout app_select_layout , root_alert_layout;
	private TextView appName;
	private ApkInfo mApkInfo;
	private Button btnstart;
	private ProgressDialog mProDialog;
	private Intent mIntent;
	private TextView isRoot , avail_para;
	private SharedPreferences mSetting;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.home, container, false);

		app_select_layout = (LinearLayout)mView.findViewById(R.id.app_select_layout);
		app_select_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AppListActivity.class);
				getActivity().startActivity(intent);

			}
		});

		root_alert_layout = (LinearLayout)mView.findViewById(R.id.root_alert_layout);
		root_alert_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!((WTApplication)WTApplication.getContext()).isRoot()) {
					AlertDialog.Builder root_b = new AlertDialog.Builder(getActivity());
					root_b.setTitle(getResources().getString(R.string.explain));
					root_b.setMessage(getResources().getString(R.string.explain_content));
					root_b.setPositiveButton(getResources().getString(R.string.i_know), null);

					AlertDialog root_d = root_b.create();
					root_d.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);

					if(((MainActivity)getActivity()).isRunning())
						root_d.show();
				}

			}
		});


		appName = (TextView)mView.findViewById(R.id.appName);

		btnstart = (Button) mView.findViewById(R.id.btn_test_start);
		btnstart.setOnClickListener(onStartClick);
		
		isRoot = (TextView)mView.findViewById(R.id.isRoot);
		isRoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(!((WTApplication)WTApplication.getContext()).isRoot()) {
					AlertDialog.Builder root_b = new AlertDialog.Builder(getActivity());
					root_b.setTitle(getResources().getString(R.string.explain));
					root_b.setMessage(getResources().getString(R.string.explain_content));
					root_b.setPositiveButton(getResources().getString(R.string.i_know), null);

					AlertDialog root_d = root_b.create();
					root_d.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);

					if(((MainActivity)getActivity()).isRunning())
							root_d.show();
				}
			}
		});

		avail_para = (TextView)mView.findViewById(R.id.avail_para);

		ImageView img_alert = (ImageView)mView.findViewById(R.id.img_alert);

		if (((WTApplication)WTApplication.getContext()).isRoot()) {
			avail_para.setText(getResources().getString(R.string.home_bottom_title) + " FPS");
			img_alert.setVisibility(View.GONE);
		}else {

			avail_para.setText(getResources().getString(R.string.home_bottom_title));
			img_alert.setVisibility(View.VISIBLE);
		}

//		avail_para.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				if(!((WTApplication)WTApplication.getContext()).isRoot()) {
//					AlertDialog.Builder root_b = new AlertDialog.Builder(getActivity());
//					root_b.setTitle(getResources().getString(R.string.explain));
//					root_b.setMessage(getResources().getString(R.string.explain_content));
//					root_b.setPositiveButton(getResources().getString(R.string.i_know), null);
//
//					AlertDialog root_d = root_b.create();
//					root_d.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);
//
//					if(((MainActivity)getActivity()).isRunning())
//						root_d.show();
//				}
//			}
//		});

		if(((WTApplication)WTApplication.getContext()).isRoot()){
			Logger.info("Homepage is root");
			isRoot.setText(getResources().getString(R.string.phone_is_root));

		}

		mSetting =  ((WTApplication)WTApplication.getContext()).getmSetting();
		if(!((WTApplication)WTApplication.getContext()).isRoot() && mSetting.getBoolean("isFirstSetup",true)) {
			mSetting.edit().putBoolean("isFirstSetup", false).apply();
			isRoot.performClick();
		}
		return mView;
	}

	private View.OnClickListener onStartClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if(mApkInfo.getPackagename() == null){
				Toast.makeText(WTApplication.getContext(), getResources().getString(R.string.select_valid_app), Toast.LENGTH_SHORT).show();
				return ;
			}


			if (mProDialog == null)

				mProDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.suggest),
						getResources().getString(R.string.pullup_to_load) + "....", true, true);
			else {
				mProDialog.show();
			}



			mIntent = ApkManager.launchAppByPackageName(mApkInfo.getPackagename(), getActivity());

			Thread launchAppThread = new Thread(new ShowappHandler());
			launchAppThread.start();

		}
	};


	class ShowappHandler implements Runnable {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void run() {

			int launchFlag = 0;

			if (mIntent != null) {

				Date start = new Date();
				Date end = new Date();

				//mIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

				startActivity(mIntent);

				long res = -1;
				final int MAX_TRY = 5;
				boolean appStart = false;

				for(int i=0;i < MAX_TRY; i++){


					String pkgName = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?ApkManager.GetTopPackageName(getActivity()) : ApkManager.GetTopActivity(getActivity()).getPackageName();


					if( pkgName.equals(mApkInfo.getPackagename())){
						end = new Date();
						long endTime = end.getTime();
						res = endTime - start.getTime();
						appStart = true;
						break;
					}

					try {

						Thread.sleep(1000);

					} catch (InterruptedException e) {

						e.printStackTrace();

					}

				}


				if(appStart){

					try{

						long baseTime = (new Date()).getTime();

						if(baseTime != -1){

							((WTApplication) WTApplication.getContext()).getReport().setBaseTime(baseTime);
							((WTApplication) WTApplication.getContext()).getReport().setBaseColock(SystemClock.uptimeMillis());

						}

						((WTApplication) WTApplication.getContext()).getTestReports().clear();



						//RealTimeTestManager.getInstance().startRealTimeTest(getActivity());

						launchFlag = 1;

						((WTApplication) WTApplication.getContext()).getReport().setTimeTStart(SystemClock.uptimeMillis());

						Intent startIntent = new Intent( WTApplication.getContext(),FloatViewService.class);
						getActivity().startService(startIntent);

						getActivity().moveTaskToBack(true);


					}catch (Exception e){

						Logger.error("Homepage Exception : " + e.toString());
					}


				}

			}

			Message message = new Message();
			message.what = launchFlag;
			launchHandler.sendMessage(message);

		}

	}

	private Handler launchHandler = new LaunchAppHandler(this);
	static class LaunchAppHandler extends Handler{
		private final WeakReference<HomePageFragment> mActivity;
		private final HomePageFragment mContext;
		public LaunchAppHandler(HomePageFragment fragment) {
			mActivity = new WeakReference<HomePageFragment>(fragment);
			mContext = fragment;
		}
		@Override
		public void handleMessage(Message msg) {

			if(msg.what == 0)

				Toast.makeText(mContext.getActivity(), mContext.getResources().getString(R.string.invalid_app), Toast.LENGTH_SHORT).show();

			else{

				mContext.btnstart.setEnabled(false);
				mContext.app_select_layout.setEnabled(false);

			}

			if (mContext.mProDialog != null) {
				mContext.mProDialog.dismiss();
			}

		}
	}

	@Override
	public void onResume() {

		super.onResume();
		//Logger.info("HomePageFragment onResume");

		mApkInfo = ((WTApplication)WTApplication.getContext()).getApkInfo();
		if ( mApkInfo.getAppname() != null && appName != null) {
			appName.setText(mApkInfo.getAppname());

		}
		else
			appName.setText("");

		if(!((WTApplication)WTApplication.getContext()).isTestStart() ){

			btnstart.setEnabled(true);
			app_select_layout.setEnabled(true);


		}else{

			btnstart.setEnabled(false);
			app_select_layout.setEnabled(false);


		}

	}
}

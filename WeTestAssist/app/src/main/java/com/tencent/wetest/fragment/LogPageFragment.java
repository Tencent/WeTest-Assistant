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
import com.tencent.wetest.common.adapter.LogListAdapter;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.model.LogGroup;
import com.tencent.wetest.common.model.LogItem;
import com.tencent.wetest.common.util.ReportUtil;
import com.tencent.wetest.common.util.SuUtil;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class LogPageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{

	private ExpandableListView loglist;
	private ProgressDialog mClearDialog;
	private List<HashMap<String, Object>> items;
	private String mUin;
	private List<LogGroup> groups;
	private LogListAdapter adapter;
	private boolean mExpandFlag = false;
	private RapidFloatingActionButton oneKeyCommit;
	private boolean isRefresh;
	private SwipeRefreshLayout swipeLayout;

	private RapidFloatingActionLayout rfaLayout;
	private RapidFloatingActionHelper rfabHelper;
	private AlertDialog.Builder clearBuilder;
	private AlertDialog clearDialog;

	private View mView;

	private List<LogItem> logitems_today;
	private List<LogItem> logitems_lastday;
	private List<LogItem> logitems_other;

	private List<LogGroup> adpLogGroups;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mView = inflater.inflate(R.layout.log, container, false);

		clearBuilder = new AlertDialog.Builder(getActivity());
		clearBuilder.setTitle(getResources().getString(R.string.confirm));
		clearBuilder.setMessage(getResources().getString(R.string.clear_record_confirm));
		clearBuilder.setPositiveButton(getResources().getString(R.string.btnOK), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				clearLoglist();
			}
		});

		clearBuilder.setNegativeButton(getResources().getString(R.string.cancel), null);
		clearDialog = clearBuilder.create();
		clearDialog.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);

		mUin = ((WTApplication) WTApplication.getContext()).getmSetting()
				.getString("uin", "");

		loglist = (ExpandableListView) mView.findViewById(R.id.content_view);
		loglist.setGroupIndicator(null);

		oneKeyCommit = (RapidFloatingActionButton) mView.findViewById(R.id.oneKeyCommit);
		RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(getActivity());
		rfaLayout = (RapidFloatingActionLayout)mView.findViewById(R.id.activity_main_rfal);


		List<RFACLabelItem> items = new ArrayList<>();

		items.add(new RFACLabelItem<Integer>()
						.setLabel(getResources().getString(R.string.clear_record))
						.setResId(R.drawable.clear)
						.setIconNormalColor(0xff4e342e)
						.setIconPressedColor(0xff3e2723)
						.setLabelColor(Color.WHITE)
						.setLabelSizeSp(14)
						.setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(getActivity(), 4)))
						.setWrapper(1)
		);

		rfaContent
				.setItems(items)
				.setIconShadowRadius(ABTextUtil.dip2px(getActivity(), 5))
				.setIconShadowColor(0xff888888)
				.setIconShadowDy(ABTextUtil.dip2px(getActivity(), 5));


		rfaContent.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
			@Override
			public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {

				if(rfacLabelItem.getLabel().equals(getResources().getString(R.string.clear_record))){

					clearDialog.show();
				}


				rfabHelper.toggleContent();
			}

			@Override
			public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {

				if(rfacLabelItem.getLabel().equals(getResources().getString(R.string.clear_record))){

					clearDialog.show();
				}
				rfabHelper.toggleContent();
			}
		});


		rfabHelper = new RapidFloatingActionHelper(
				getActivity(),
				rfaLayout,
				oneKeyCommit,
				rfaContent
		).build();
		;

		init();


		return mView;
	}

	private void init()
	{

		swipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(android.R.color.holo_green_light,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);


		adpLogGroups = new ArrayList<LogGroup>();


		LogGroup adpGroup_today = new LogGroup();
		adpGroup_today.setGroupName(getResources().getString(R.string.today));

		LogGroup adpGroup_lastday = new LogGroup();
		adpGroup_lastday.setGroupName(getResources().getString(R.string.yesterday));

		LogGroup adpGroup_other = new LogGroup();
		adpGroup_other.setGroupName(getResources().getString(R.string.otherday));

		List<LogItem> adpItems_today = new ArrayList<LogItem>();
		adpGroup_today.setGroupChild(adpItems_today);

		List<LogItem> adpItems_lastday = new ArrayList<LogItem>();
		adpGroup_lastday.setGroupChild(adpItems_lastday);

		List<LogItem> adpItems_other = new ArrayList<LogItem>();
		adpGroup_other.setGroupChild(adpItems_other);

		adpLogGroups.add(adpGroup_today);
		adpLogGroups.add(adpGroup_lastday);
		adpLogGroups.add(adpGroup_other);

		if(adapter == null)
			adapter = new LogListAdapter(getActivity(), adpLogGroups);
		else
			adapter.setGroups(adpLogGroups);

		loglist.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {

	}

	@Override
	public void onRefresh() {
		if(!isRefresh){ isRefresh = true;
			new Handler().postDelayed(new Runnable() {
				public void run() {
					loadMoreLogList();
					swipeLayout.setRefreshing(false);
					//list.add(new SoftwareClassificationInfo(2, "ass"));
					isRefresh= false;
				}
			}, 1000); }

	}

	@Override
	public void onResume(){
		super.onResume();
		clearAdpGroups();
		updateLoglist();
	}

	private void clearAdpGroups() {

		for (LogGroup group : adpLogGroups){
			group.getGroupChild().clear();
		}
	}

	public void updateLoglist() {


		try {

			new Thread(new Runnable() {

				public void run() {
					try {

						items = ReportUtil.readReportList();
						//applist = ReportUtil.readReportAppnameList(mUin);

						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -1);
						Date lastday = cal.getTime();

						Date today = new Date();

						if( ((WTApplication) WTApplication.getContext()).getReport().getBaseTime() != -1){

							today = new Date(((WTApplication) WTApplication.getContext()).getReport().getBaseTime() + SystemClock.uptimeMillis()
									- ((WTApplication) WTApplication.getContext()).getReport().getBaseColock());

						}

						SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");

						groups = new ArrayList<LogGroup>();
						LogGroup logGroup_today = new LogGroup();
						logGroup_today.setGroupName(getResources().getString(R.string.today));
						LogGroup logGroup_lastday = new LogGroup();
						logGroup_lastday.setGroupName(getResources().getString(R.string.yesterday));
						LogGroup logGroup_other = new LogGroup();
						logGroup_other.setGroupName(getResources().getString(R.string.otherday));

						logitems_today = new ArrayList<LogItem>();
						logitems_lastday = new ArrayList<LogItem>();
						logitems_other = new ArrayList<LogItem>();

						if (items != null) {

							for (HashMap<String, Object> item : items) {

								LogItem temp = new LogItem();
								temp.setImg((Drawable) item.get("icon"));
								temp.setTime(((CharSequence) item.get("appName")).toString());
								temp.setFilename((String) item.get("filename"));
								temp.setAppname((String) item.get("packageName"));


								if (temp.getTime().split(" ")[0].equals(sp.format(today)))
									logitems_today.add(temp);
								else if (temp.getTime().split(" ")[0].equals(sp.format(lastday)))
									logitems_lastday.add(temp);
								else
									logitems_other.add(temp);
							}

						}

						Collections.reverse(logitems_today);
						logGroup_today.setGroupChild(logitems_today);
						groups.add(logGroup_today);

						Collections.reverse(logitems_lastday);
						logGroup_lastday.setGroupChild(logitems_lastday);
						groups.add(logGroup_lastday);

						Collections.reverse(logitems_other);
						logGroup_other.setGroupChild(logitems_other);
						groups.add(logGroup_other);

						loadMoreLogList();

					} catch (Exception e) {

						Logger.error("read logexception" + e.toString());


						String cmd = "logcat -d -v time > /data/data/com.tencent.wefpmonitor/files/crash.log";
						SuUtil.executeCommand(1, cmd);

						e.printStackTrace();
					}

				}
			}).start();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void loadMoreLogList(){


		int curr_0_Size = adpLogGroups.get(0).getChildSize();
		int curr_1_Size = adpLogGroups.get(1).getChildSize();
		int curr_2_Size = adpLogGroups.get(2).getChildSize();

		for(int i = curr_0_Size ; i < groups.get(0).getChildSize() && i < curr_0_Size + 10;i++){

			adpLogGroups.get(0).add(groups.get(0).getChild(i));
		}


		for(int i = curr_1_Size ; i < groups.get(1).getChildSize() && i < curr_1_Size + 10;i++){

			adpLogGroups.get(1).add(groups.get(1).getChild(i));
		}

		for(int i = curr_2_Size ; i < groups.get(2).getChildSize() && i < curr_2_Size + 10;i++){

			adpLogGroups.get(2).add(groups.get(2).getChild(i));
		}

		Message m = updateHandler.obtainMessage();
		updateHandler.sendMessage(m);

	}
	private Handler updateHandler = new UpdateHandler(this);
	static class UpdateHandler extends Handler{
		private final WeakReference<LogPageFragment> mFragment;
		private final LogPageFragment mContext;
		public UpdateHandler(LogPageFragment fragment) {
			mFragment = new WeakReference<LogPageFragment>(fragment);
			mContext = fragment;
		}
		@Override
		public void handleMessage(Message msg) {


			if(!mContext.mExpandFlag){

				int groupCount = mContext.adapter.getGroupCount();

				Logger.info("groupCount : " + groupCount + "adpGroups Count : " + mContext.adpLogGroups.size() + "adapterCount : " + mContext.adapter.getGroupCount());
				for (int i = 0 ; i < groupCount ; i++) {

					mContext.loglist.expandGroup(i);

				};
			}

			mContext.adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	}


	public void clearLoglist(){
		if (mClearDialog == null)

			mClearDialog = ProgressDialog.show(getActivity(),getResources().getString(R.string.suggest), getResources().getString(R.string.pullup_to_load),
					true, true);
		else {
			mClearDialog.show();
		}
		new Thread(new Runnable() {
			public void run() {

				try {
					ReportUtil.clear(mUin);

					items = ReportUtil.readReportList();

					if (items == null)
						items = new ArrayList<HashMap<String, Object>>();

					groups = new ArrayList<LogGroup>();
					LogGroup logGroup_today = new LogGroup();
					logGroup_today.setGroupName(getResources().getString(R.string.today));

					LogGroup logGroup_lastday = new LogGroup();
					logGroup_lastday.setGroupName(getResources().getString(R.string.yesterday));

					LogGroup logGroup_other = new LogGroup();
					logGroup_other.setGroupName(getResources().getString(R.string.otherday));

					List<LogItem> logitems = new ArrayList<LogItem>();

					logGroup_today.setGroupChild(logitems);
					logGroup_lastday.setGroupChild(logitems);
					logGroup_other.setGroupChild(logitems);

					groups.add(logGroup_today);
					groups.add(logGroup_lastday);
					groups.add(logGroup_other);

					clearAdpGroups();

				} catch (Exception e) {

					e.printStackTrace();
				}

				Message m = clearHandler.obtainMessage();
				clearHandler.sendMessage(m);
			}
		}).start();
	}

	private Handler clearHandler = new ClearHandler(this);
	static class ClearHandler extends Handler{
		private final WeakReference<LogPageFragment> mActivity;
		private final LogPageFragment mContext;
		public ClearHandler(LogPageFragment activity) {
			mActivity = new WeakReference<LogPageFragment>(activity);
			mContext = activity;
		}
		@Override
		public void handleMessage(Message msg) {

			if(mContext.mClearDialog != null )
				mContext.mClearDialog.dismiss();

			mContext.adapter.notifyDataSetChanged();
			super.handleMessage(msg);

		}
	}

}

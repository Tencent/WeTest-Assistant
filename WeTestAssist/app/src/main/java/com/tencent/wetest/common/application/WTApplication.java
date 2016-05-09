package com.tencent.wetest.common.application;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.common.model.FPSInfo;
import com.tencent.wetest.common.model.Report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.WindowManager.LayoutParams;

import  android.support.multidex.MultiDexApplication;
public class WTApplication extends MultiDexApplication {

	private static Context mContext;
	private SharedPreferences mSetting;
	private LayoutParams mWMParas;

	private Report report;
	private ApkInfo apkinfo;
	private ProgressDialog proDialog;
	private Report logreport;

	private boolean testStart;

	private String tag;
	private boolean root = false;
	private File currTestFile;
	private int LoginType;
	private FPSInfo fps;

	private Set<String> testReports;


	private boolean monitorFlag;

	private int ctrHeight;


	public void onCreate() {

		super.onCreate();

		if(mWMParas == null)
			mWMParas = new LayoutParams();

		if(mContext == null)
			mContext = getApplicationContext();
		if(report == null){

			report = new Report();
			report.setBaseTime(-1);

		}

		if(apkinfo == null)
			apkinfo = new ApkInfo();

		if(mSetting == null)
			mSetting = getSharedPreferences("wetest_setting",  MODE_PRIVATE);


		if(testReports == null || testReports.size() == 0 )
			testReports = new HashSet<String>();

		this.tag = "";

		testStart = false;

	}

	public LayoutParams getWMParams(){
		return mWMParas;
	}


	public static Context getContext(){
		return mContext;
	}

	public Report getReport(){
		return report;
	}

	public ApkInfo getApkInfo(){
		return apkinfo;
	}


	public static Context getmContext() {
		return mContext;
	}


	public static void setmContext(Context mContext) {
		WTApplication.mContext = mContext;
	}

	public ApkInfo getApkinfo() {
		return apkinfo;
	}


	public void setApkinfo(ApkInfo apkinfo) {
		this.apkinfo = apkinfo;
	}


	public ProgressDialog getProDialog() {
		return proDialog;
	}


	public void setProDialog(ProgressDialog proDialog) {
		this.proDialog = proDialog;
	}


	public Report getLogreport() {
		return logreport;
	}


	public void setLogreport(Report logreport) {
		this.logreport = logreport;
	}


	public void setReport(Report report) {
		this.report = report;
	}




	public SharedPreferences getmSetting() {
		return mSetting;
	}


	public void setmSetting(SharedPreferences mSetting) {
		this.mSetting = mSetting;
	}

	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public boolean isRoot() {
		return root;
	}


	public void setRoot(boolean root) {
		this.root = root;
	}


	public boolean isTestStart() {
		return testStart;
	}


	public void setTestStart(boolean testStart) {
		this.testStart = testStart;
	}


	public File getCurrTestFile() {
		return currTestFile;
	}


	public void setCurrTestFile(File currTestFile) {
		this.currTestFile = currTestFile;
	}

	public int getLoginType() {
		return LoginType;
	}

	public void setLoginType(int loginType) {
		LoginType = loginType;
	}

	public FPSInfo getFps() {
		return fps;
	}

	public void setFps(FPSInfo fps) {
		this.fps = fps;
	}

	public Set<String> getTestReports() {
		return testReports;
	}

	public void setTestReports(Set<String> testReports) {
		this.testReports = testReports;
	}

	public boolean isMonitorFlag() {
		return monitorFlag;
	}

	public void setMonitorFlag(boolean monitorFlag) {
		this.monitorFlag = monitorFlag;
	}

	public int getCtrHeight() {
		return ctrHeight;
	}

	public void setCtrHeight(int ctrHeight) {
		this.ctrHeight = ctrHeight;
	}
}

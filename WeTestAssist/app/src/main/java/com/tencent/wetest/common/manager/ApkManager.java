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

package com.tencent.wetest.common.manager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.model.APPInfo;
import com.tencent.wetest.common.util.SuUtil;

public class ApkManager {

	private static ActivityManager activityManager;
	private static PackageManager packageManager;
	private static List<APPInfo> items ;


	public static List<APPInfo> getInstalledApp(){

		if(packageManager == null)
			packageManager = (PackageManager) WTApplication.getContext().getPackageManager();

		items = new ArrayList<APPInfo>();

		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

		List<PackageInfo> myPackageInfos = new ArrayList<PackageInfo>();

		for (int i = 0; i < packageInfos.size(); i++)
		{
			PackageInfo packageInfo = packageInfos.get(i);

			if ((packageInfo.applicationInfo.flags
					& ApplicationInfo.FLAG_SYSTEM) == 0)
				myPackageInfos.add(packageInfo);
		}

		for(PackageInfo pi:myPackageInfos){

			if(!(pi.applicationInfo.packageName.equals("com.tencent.wefpmonitor")||pi.applicationInfo.packageName.equals("com.tencent.wetest"))){

				APPInfo info = new APPInfo();
				info.setIcon(pi.applicationInfo.loadIcon(packageManager));
				info.setAppName(pi.applicationInfo.loadLabel(packageManager));
				info.setPackageName(pi.applicationInfo.packageName);


				items.add(info);
			}

		}

		Collections.sort(items);
		return items;
	}


	public static int[] getPidByPackageName(String packageName) {

		int[] ids = new int[]{-1,-1};

		try{

			String processName = getProcessNameByPackageName(packageName);

			if(activityManager == null)
				activityManager = (ActivityManager)WTApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);

			List<RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();

			for (RunningAppProcessInfo info : runningAppProcessInfos) {

				//Logger.info("curr process:" + info.processName + "processName:" + processName);

				if (info.processName.equals(processName)) {

					ids[0] = info.pid;
					ids[1] = info.uid;

					break;

				}

			}

			if(ids[0] == -1){

				List<RunningServiceInfo> runingInfo = activityManager.getRunningServices(500);

				for(RunningServiceInfo info : runingInfo){

					//Logger.info("process is: "+info.process);

					if(info.process.equals(processName)){

						ids[0] = info.pid;
						ids[1] = info.uid;

						break;
					}

				}

			}

		}catch(Exception e){

			Logger.error("getPidByPackageNameException : " + e.toString());

		}

		return ids;

	}

	public static String getProcessNameByPackageName(String packageName) {

		PackageManager pManager = WTApplication.getContext().getPackageManager();
		ApplicationInfo aInfo = null;
		String processName = null;

		try {

			aInfo = pManager.getApplicationInfo(packageName, 0);
			processName = aInfo.processName;

		} catch (Exception e) {

			Logger.error(e.toString()+"name is:"+packageName);

		}

		return processName;
	}

	public static PackageInfo getPackageInfoByPackageName(String packageName) {
		if(packageManager == null)
			packageManager = (PackageManager)WTApplication.getContext().getPackageManager();

		List<PackageInfo> packs = packageManager.getInstalledPackages(0);

		for(PackageInfo pi:packs){
			if(packageName.equals(pi.applicationInfo.packageName))
				return pi;
		}
		return null;
	}

	public static Intent launchAppByPackageName(String packageName,Context ctx){

		try {

			PackageInfo pi = packageManager.getPackageInfo(packageName, 0);

			Intent resIntent = new Intent(Intent.ACTION_MAIN, null);
			resIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resIntent.setPackage(pi.packageName);

			List<ResolveInfo> apps = packageManager.queryIntentActivities(resIntent,0);

			ResolveInfo ri = apps.iterator().next();

			if (ri != null) {
				String startappName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(startappName, className);

				intent.setComponent(cn);

				return intent;
			}

		} catch (Exception e) {

			Logger.error("launchAppByPackageName Exception : " + e.toString());

		}

//		//try {
//
//		if(packageManager == null)
//			packageManager = (PackageManager)WTApplication.getContext().getPackageManager();
//
//		PackageInfo pi = null;
//		try {
//			pi = packageManager.getPackageInfo(packageName, 0);
//		} catch (PackageManager.NameNotFoundException e) {
//			Logger.error("launchAppByPackageName exception : " + e.toString());
//			e.printStackTrace();
//		}
//
//		    Intent resIntent = new Intent(Intent.ACTION_MAIN, null);
//			resIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//			resIntent.setPackage(pi.packageName);
//
//			List<ResolveInfo> apps = packageManager.queryIntentActivities(resIntent,0);
//
//			ResolveInfo ri = apps.iterator().next();
//
//			if (ri != null) {
//				String startappName = ri.activityInfo.packageName;
//
//				String className = ri.activityInfo.packageName;
//
//				Logger.info("pkg: " + startappName + " service: " + className );
//
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				intent.addCategory(Intent.CATEGORY_LAUNCHER);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				ComponentName cn = new ComponentName(startappName, className);
//
//				intent.setComponent(cn);
//
//				return intent;
//			}
////
////		} catch (Exception e) {
////
////			Logger.error("launchAppByPackageName Exception : " + e.toString());
////
////		}

		return null;

	}


	public static void closeAppByPackageName(String packageName){
		if(activityManager == null)
			activityManager = (ActivityManager)WTApplication.getContext().
					getSystemService(Context.ACTIVITY_SERVICE);

//		String cmd = "kill "+getPidByPackageName(packageName)[0];
//		Logger.info(cmd);
//		try {
//			Process p = Runtime.getRuntime().exec(cmd);
//			p.waitFor();
//			Logger.info("kill end:"+cmd);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		while(isServiceRunning(WeFPApplication.getContext(),"com.tencent.wefpmonitor.service.StasticService")){
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
		//activityManager.killBackgroundProcesses(packageName);

		SuUtil.kill(packageName);

//		Method forceStopPackage;
//		try {
//			forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
//			forceStopPackage.setAccessible(true);
//			forceStopPackage.invoke(activityManager, packageName); 
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Logger.info("kill success");


	}

	public static void unpack(String filename) {
		AssetManager assetManager = WTApplication.getContext().getAssets();
		String targetPath = WTApplication.getContext().getFilesDir().getPath();
		try {

			InputStream inputStream ;

			if(filename.equals("wetested") || filename.equals("inject"))
				inputStream = assetManager.open(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? "assets_for_L/"+filename : filename);
			else
				inputStream = assetManager.open(filename);

			File f = new File(targetPath.concat("/").concat(filename));

			if ( f.exists() ){
				f.delete();
			}

			FileOutputStream outputStream = new FileOutputStream(f);

			int read = 0;
			byte[] bytes = new byte[16384];
			while ((read = inputStream.read(bytes)) != -1)
				outputStream.write(bytes, 0, read);

			outputStream.flush();
			outputStream.close();
			inputStream.close();

			String cmd = "chmod 775 " + targetPath + "/" + filename;
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean isServiceRunning(Context mContext,String className) {

		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static ComponentName GetTopActivity(Context ctx) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
		ActivityManager.RunningTaskInfo info = runningTasks.get(0);
		ComponentName component = info.topActivity;
		return component;
	}


	public static String GetTopPackageName(Context ctx) {


		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for(RunningAppProcessInfo appProcess : appProcesses){
			if(appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
				return appProcess.pkgList.length > 0 ? appProcess.pkgList[0] : "";
			}
		}

		return "";
	}


	private static final String SCHEME = "package";

	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

	private static final String APP_PKG_NAME_22 = "pkg";

	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	public static void openFloatWindow(Context context, String packageName) {

		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) {
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else {
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);

		if(Integer.parseInt(android.os.Build.VERSION.SDK) >=19)
			Toast.makeText(WTApplication.getContext(), context.getResources().getString(R.string.open_window_up_19), Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(WTApplication.getContext(), context.getResources().getString(R.string.open_window_down_19), Toast.LENGTH_SHORT).show();

	}
}




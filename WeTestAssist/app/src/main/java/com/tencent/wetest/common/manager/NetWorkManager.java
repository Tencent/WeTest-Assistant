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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import android.content.Context;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkManager {



	public void onCreate() {

	}

	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}


	public static boolean is3rd(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}


	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = lm.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		//return cm.getActiveNetworkInfo().isAvailable();
		if (cm == null) {
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED && info[i].isAvailable() && info[i].isConnected()) {

						return true;
					}
				}
			}
		}

		return false;
	}


	public static boolean isNetWorkAvaliable2(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isNetAvaliable = false;

		if (cm == null) {
			isNetAvaliable = true;
		} else {

			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED && info[i].isAvailable() && info[i].isConnected()) {

						isNetAvaliable = true;
					}
				}
			}
		}

		if(isNetAvaliable){

			try {

				URL url = new URL("http://www.baidu.com");
				HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setRequestMethod("GET");
				//urlConn.setRequestMethod("POST");
				urlConn.setReadTimeout(30000);
				urlConn.setConnectTimeout(30000);

				urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				//urlConn.setRequestProperty("Connection", "close");
				urlConn.connect();

				return true;

			}catch (Exception e ){

				return false;

			}
		}

		return false;

	}




}

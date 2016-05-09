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

package com.tencent.wetest.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.tencent.wetest.common.log.Logger;

/**
 * 设备信息管理类
 *
 *
 */
@SuppressLint("NewApi")
public class DeviceUtil {

	/**
	 * 获取设备内存大小值
	 * @param ctx 上下文
	 * @return 内存大小,单位MB
	 */

	public static String getTotalMemory(Context ctx) {

		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {

			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
			localBufferedReader.close();

		}catch (Exception e) {

			Logger.error("getTotalMemory Exception : " + e.toString());
		}

		return String.valueOf(initial_memory);
	}

	/**
	 * 获取设备CPU核数
	 * @return CPU核数
	 */

	public static int getCpuCoreNum()
	{
		class CpuFilter implements FileFilter
		{
			public boolean accept(File pathname)
			{
				if (Pattern.matches("cpu[0-9]", pathname.getName()))
				{
					return true;
				}
				return false;
			}
		}

		try
		{
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		}
		catch (Exception e)
		{
			return 1;
		}
	}

	/**
	 * 获取设备屏幕分辨率
	 * @param cx 上下文
	 * @return 屏幕分辨率 ( width x height )
	 */

	public static String getDisplayMetrics(Context cx) {

		WindowManager mWindowManager = (WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
		Display display = mWindowManager.getDefaultDisplay();
		DisplayMetrics metric = new DisplayMetrics();

		Point size = new Point();

		String str = "";

		try {

			if(Build.VERSION.SDK_INT >= 11){

				display.getRealSize(size);

				str += String.valueOf(size.x) + " x " + String.valueOf(size.y);

			}else{

				Method method = Class.forName("android.view.Display").getMethod("getRealMetrics",DisplayMetrics.class);
				method.invoke(display, metric);

				str += String.valueOf(metric.widthPixels) + " x " + String.valueOf(metric.heightPixels);


			}



		}catch(Exception e){

			display.getMetrics(metric);
			str += String.valueOf(metric.widthPixels) + " x " + String.valueOf(metric.heightPixels);
			Logger.error("getDeviceRealMetric Exception : " + e.toString());

		}

		return str ;

	}


	/**
	 * 获取设备品牌
	 * @param cx 上下文
	 * @return BRAND ( MODEL )
	 */
	public static String getManu(Context cx) {

		return android.os.Build.BRAND + "("+android.os.Build.MODEL+")";
	}

	/**
	 * 获取系统版本
	 * @param cx 上下文
	 * @return VERSION.RELEASE
	 */
	public static String getVersion(Context cx) {

		// return "Android" + "("+android.os.Build.VERSION.RELEASE+")";
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取设备信息，用于测试报告上传
	 * @param context 上下文
	 * @return DeviceInfo的JSON格式
	 */
	public static String getDeviceInfo(Context context){

		try {

			JSONArray content_f_Device = new JSONArray();
			JSONObject manu = new JSONObject();
			manu.put("manu", android.os.Build.BRAND);
			content_f_Device.put(manu);

			JSONObject model = new JSONObject();
			model.put("model", android.os.Build.MODEL);
			content_f_Device.put(model);

			JSONObject version  = new JSONObject();
			version.put("version", getVersion(context));
			content_f_Device.put(version);

			JSONObject cpu   = new JSONObject();
			cpu.put("cpu", ""+getCpuCoreNum());
			content_f_Device.put(cpu);

			JSONObject mem    = new JSONObject();
			mem .put("mem", ""+getTotalMemory(context));
			content_f_Device.put(mem);

			JSONObject resolution    = new JSONObject();
			resolution .put("resolution", ""+getDisplayMetrics(context));
			content_f_Device.put(resolution);

			JSONObject cpufreq    = new JSONObject();
			cpufreq .put("cpufreq", getCpuMaxFreq());
			content_f_Device.put(cpufreq);
			//+","+getCpuMinFreq()

			JSONObject cpuname    = new JSONObject();
			cpuname .put("cpuname", getCpuName());
			content_f_Device.put(cpuname);

			return content_f_Device.toString();

		} catch (JSONException e) {

			Logger.error("getDeviceInfo Exception : " + e.toString());

		}

		return null;
	}

	//获得CPU最小赫兹 
	public static String getCpuMinFreq()
	{
		String result = "--";
		double cpuMinFreq = 0;
		try
		{
			BufferedReader br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
			String text = "";
			while ((text = br.readLine()) != null)
			{
				text  = text.trim();
				if (!"".equals(text.trim()))
				{
					cpuMinFreq = Double.parseDouble(text.trim()) / (1000*1000);

					BigDecimal bg = new BigDecimal(cpuMinFreq).setScale(2, RoundingMode.UP);

					result = "" + bg.doubleValue();

					// DeviceUtil.cpuMinFreq = cpuMinFreq;
				}
				break;
			}
			br.close();
			br = null;
		}
		catch(Exception e)
		{
			Logger.error("getCpuMinFreqException" + e.toString());
		}
		return result;
	}

	//获得CPU最大赫兹 
	public static String getCpuMaxFreq()
	{
		String result = "--";
		double cpuMaxFreq = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));
			String text = "";
			while ((text = br.readLine()) != null)
			{
				text  = text.trim();
				if (!"".equals(text.trim()))
				{
					cpuMaxFreq = Double.parseDouble(text.trim()) / (1000*1000);
					BigDecimal bg = new BigDecimal(cpuMaxFreq).setScale(1, RoundingMode.UP);

					result = "" + bg.doubleValue();

				}
				break;
			}
			br.close();
			br = null;

		} catch(Exception e)  {

			Logger.error("getCpuMaxFreq Exception:" + e.toString());

		}

		return result;
	}

	//获得CPU架构信息
	public static String getCpuName() {

		String result = "--";

		try {

			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);

			result = array[1];

			br.close();

		} catch (Exception e) {

			Logger.error("getCpuName Exception: " + e.toString());
		}

		return result;
	}

	/**
	 * 获取手机MAC地址用于注册
	 * @param ctx
	 * @return
	 */
	public static String getMacAddr(Context ctx){

		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Service.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		return wifiInfo.getMacAddress();

	}

}

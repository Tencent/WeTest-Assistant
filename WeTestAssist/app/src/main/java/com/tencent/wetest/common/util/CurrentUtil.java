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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Locale;
;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.tencent.wetest.common.log.Logger;

/**
 * 获取温度和电量
 *
 */
public class CurrentUtil {

	static final String BUILD_MODEL = Build.MODEL.toLowerCase(Locale.ENGLISH);

	@TargetApi(4)
	static public Long getValue() {

		File f = null;

		// Huawei a199
		if (CurrentUtil.BUILD_MODEL.contains("a199")) {
			f = new File("/sys/class/power_supply/Battery/current_now");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		// Galaxy S4
		if (CurrentUtil.BUILD_MODEL.contains("sgh-i337")
				|| CurrentUtil.BUILD_MODEL.contains("gt-i9505")
				|| CurrentUtil.BUILD_MODEL.contains("gt-i9500")
				|| CurrentUtil.BUILD_MODEL.contains("sch-i545")
				|| CurrentUtil.BUILD_MODEL.contains("find 5")
				|| CurrentUtil.BUILD_MODEL.contains("sgh-m919")
				|| CurrentUtil.BUILD_MODEL.contains("sgh-i537")) {
			f = new File("/sys/class/power_supply/battery/current_now");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		if (CurrentUtil.BUILD_MODEL.contains("cynus")) {
			f = new File(
					"/sys/devices/platform/mt6329-battery/FG_Battery_CurrentConsumption");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}
		// Zopo Zp900, etc.
		if (CurrentUtil.BUILD_MODEL.contains("zp900")
				|| CurrentUtil.BUILD_MODEL.contains("jy-g3")
				|| CurrentUtil.BUILD_MODEL.contains("zp800")
				|| CurrentUtil.BUILD_MODEL.contains("zp800h")
				|| CurrentUtil.BUILD_MODEL.contains("zp810")
				|| CurrentUtil.BUILD_MODEL.contains("w100")
				|| CurrentUtil.BUILD_MODEL.contains("zte v987")) {
			f = new File(
					"/sys/class/power_supply/battery/BatteryAverageCurrent");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		// Samsung Galaxy Tab 2
		if (CurrentUtil.BUILD_MODEL.contains("gt-p31")
				|| CurrentUtil.BUILD_MODEL.contains("gt-p51")) {
			f = new File("/sys/class/power_supply/battery/current_avg");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		// HTC One X
		if (CurrentUtil.BUILD_MODEL.contains("htc one x")) {
			f = new File("/sys/class/power_supply/battery/batt_attr_text");
			if (f.exists()) {
				Long value = getBattAttrTextLongValue(f, "I_MBAT", "I_MBAT");
				if (value != null)
					return value;
			}
		}

		// HTC One
		if (CurrentUtil.BUILD_MODEL.contains("htc one")) {
			f = new File("/sys/class/power_supply/battery/batt_current_now");
			if (f.exists()) {
				return getLongValue(f, true);
			}
		}

		// HTC butterfly
		if (CurrentUtil.BUILD_MODEL.contains("htc x920e")) {
			f = new File("/sys/class/power_supply/battery/batt_current_now");
			if (f.exists()) {
				return getLongValue(f, true);
			}
		}

		// wildfire S
		if (CurrentUtil.BUILD_MODEL.contains("wildfire s")) {
			f = new File("/sys/class/power_supply/battery/smem_text");
			if (f.exists()) {
				Long value = getBattAttrTextLongValue(f, "eval_current",
						"batt_current");
				if (value != null)
					return value;
			}
		}

		// trimuph with cm7, lg ls670, galaxy s3, galaxy note 2
		if (CurrentUtil.BUILD_MODEL.contains("triumph")
				|| CurrentUtil.BUILD_MODEL.contains("ls670")
				|| CurrentUtil.BUILD_MODEL.contains("gt-i9300")
				|| CurrentUtil.BUILD_MODEL.contains("gt-n7100")
				|| CurrentUtil.BUILD_MODEL.contains("sgh-i317")) {
			f = new File("/sys/class/power_supply/battery/current_now");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		// htc desire hd / desire z / inspire?
		// htc evo view tablet
		if (CurrentUtil.BUILD_MODEL.contains("desire hd")
				|| CurrentUtil.BUILD_MODEL.contains("desire z")
				|| CurrentUtil.BUILD_MODEL.contains("inspire")
				|| CurrentUtil.BUILD_MODEL.contains("pg41200")) {
			f = new File("/sys/class/power_supply/battery/batt_current");
			if (f.exists()) {
				return getLongValue(f, false);
			}
		}

		// nexus one cyangoenmod
		f = new File("/sys/devices/platform/ds2784-battery/getcurrent");
		if (f.exists()) {
			return getLongValue(f, true);
		}

		// sony ericsson xperia x1
		f = new File(
				"/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// xdandroid
		/* if (Build.MODEL.equalsIgnoreCase("MSM")) { */
		f = new File(
				"/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}
		/* } */

		// droid eris
		f = new File("/sys/class/power_supply/battery/smem_text");
		if (f.exists()) {
			Long value = getSMemTextLongValue();
			if (value != null)
				return value;
		}

		// htc sensation / evo 3d
		f = new File("/sys/class/power_supply/battery/batt_attr_text");
		if (f.exists()) {
			Long value = getBattAttrTextLongValue(f, "batt_discharge_current",
					"batt_current");
			if (value != null)
				return value;
		}

		// some htc devices
		f = new File("/sys/class/power_supply/battery/batt_current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// nexus one
		f = new File("/sys/class/power_supply/battery/current_now");
		if (f.exists()) {
			return getLongValue(f, true);
		}

		// samsung galaxy vibrant
		f = new File("/sys/class/power_supply/battery/batt_chg_current");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// sony ericsson x10
		f = new File("/sys/class/power_supply/battery/charger_current");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// Nook Color
		f = new File("/sys/class/power_supply/max17042-0/current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// Xperia Arc
		f = new File("/sys/class/power_supply/bq27520/current_now");
		if (f.exists()) {
			return getLongValue(f, true);
		}

		// Motorola Atrix
		/*
		 * f = new File(
		 * "/sys/devices/platform/cpcap_battery/power_supply/usb/current_now");
		 * if (f.exists()) { return getLongValue(f, false); }
		 */
		// Acer Iconia Tab A500
		f = new File("/sys/EcControl/BatCurrent");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// charge current only, Samsung Note
		f = new File("/sys/class/power_supply/battery/batt_current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// galaxy note, galaxy s2
		f = new File("/sys/class/power_supply/battery/batt_current_adc");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		// intel
		f = new File("/sys/class/power_supply/max170xx_battery/current_now");
		if (f.exists()) {
			return getLongValue(f, true);
		}

		// Sony Xperia U
		f = new File("/sys/class/power_supply/ab8500_fg/current_now");
		if (f.exists()) {
			return getLongValue(f, true);
		}

		f = new File("/sys/class/power_supply/android-battery/current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		f = new File("/sys/class/power_supply/android-battery/current_now");
		if (f.exists()) {
			return getLongValue(f, false);
		}

		return null;
	}

	public static Long getLongValue(File _f, boolean _convertToMillis) {

		//Logger.debug("handle getLongValue : " + _f.getName());

		String text = null;

		try {
			FileInputStream fs = new FileInputStream(_f);
			InputStreamReader sr = new InputStreamReader(fs);
			BufferedReader br = new BufferedReader(sr);

			text = br.readLine();

			br.close();
			sr.close();
			fs.close();
		} catch (Exception ex) {
			Log.e("RemoteCall", ex.getMessage());
			ex.printStackTrace();
		}

		Long value = null;

		//Logger.debug("batt text is " + text);

		if (text != null) {
			try {
				value = Long.parseLong(text);
			} catch (NumberFormatException nfe) {
				Log.e("RemoteCall", nfe.getMessage());
				value = null;
			}

			if (_convertToMillis && value != null)
				value = value / 1000; // convert to milliampere

		}

		return value;
	}

	public static Long getBattAttrTextLongValue(File f, String dischargeField,
												String chargeField) {

		String text = null;
		Long value = null;

		try {

			// @@@ debug
			// StringReader fr = new
			// StringReader("vref: 1248\r\nbatt_id: 3\r\nbatt_vol: 4068\r\nbatt_current: 0\r\nbatt_discharge_current: 123\r\nbatt_temperature: 329\r\nbatt_temp_protection:normal\r\nPd_M:0\r\nI_MBAT:-313\r\npercent_last(RP): 94\r\npercent_update: 71\r\nlevel: 71\r\nfirst_level: 100\r\nfull_level:100\r\ncapacity:1580\r\ncharging_source: USB\r\ncharging_enabled: Slow\r\n");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();

			final String chargeFieldHead = chargeField + ": ";
			final String dischargeFieldHead = dischargeField + ": ";

			while (line != null) {
				if (line.contains(chargeField)) {
					text = line.substring(line.indexOf(chargeFieldHead)
							+ chargeFieldHead.length());
					try {
						value = Long.parseLong(text);
						if (value != 0)
							break;
					} catch (NumberFormatException nfe) {
						Log.e("wetest", nfe.getMessage(), nfe);
					}
				}

				// "batt_discharge_current:"
				if (line.contains(dischargeField)) {
					text = line.substring(line.indexOf(dischargeFieldHead)
							+ dischargeFieldHead.length());
					try {
						value = (-1) * Math.abs(Long.parseLong(text));
					} catch (NumberFormatException nfe) {
						Log.e("wetest", nfe.getMessage(), nfe);
					}
					break;
				}

				line = br.readLine();
			}

			br.close();
			fr.close();
		} catch (Exception ex) {
			Log.e("wetest", ex.getMessage(), ex);
		}

		return value;
	}

	public static Long getSMemTextLongValue() {

		boolean success = false;
		String text = null;

		try {

			// @@@ debug StringReader fr = new
			// StringReader("batt_id: 1\r\nbatt_vol: 3840\r\nbatt_vol_last: 0\r\nbatt_temp: 1072\r\nbatt_current: 1\r\nbatt_current_last: 0\r\nbatt_discharge_current: 112\r\nVREF_2: 0\r\nVREF: 1243\r\nADC4096_VREF: 4073\r\nRtemp: 70\r\nTemp: 324\r\nTemp_last: 0\r\npd_M: 20\r\nMBAT_pd: 3860\r\nI_MBAT: -114\r\npd_temp: 0\r\npercent_last: 57\r\npercent_update: 58\r\ndis_percent: 64\r\nvbus: 0\r\nusbid: 1\r\ncharging_source: 0\r\nMBAT_IN: 1\r\nfull_bat: 1300000\r\neval_current: 115\r\neval_current_last: 0\r\ncharging_enabled: 0\r\ntimeout: 30\r\nfullcharge: 0\r\nlevel: 58\r\ndelta: 1\r\nchg_time: 0\r\nlevel_change: 0\r\nsleep_timer_count: 11\r\nOT_led_on: 0\r\noverloading_charge: 0\r\na2m_cable_type: 0\r\nover_vchg: 0\r\n");
			FileReader fr = new FileReader(
					"/sys/class/power_supply/battery/smem_text");
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();

			while (line != null) {
				if (line.contains("I_MBAT")) {
					text = line.substring(line.indexOf("I_MBAT: ") + 8);
					success = true;
					break;
				}
				line = br.readLine();
			}

			br.close();
			fr.close();
		} catch (Exception ex) {
			Log.e("wetest", ex.getMessage());
			ex.printStackTrace();
		}

		Long value = null;

		if (success) {

			try {
				value = Long.parseLong(text);
			} catch (NumberFormatException nfe) {
				Log.e("wetest", nfe.getMessage());
				value = null;
			}

		}

		return value;
	}




	static public Double getTemprature() {

		Double value = null;

		//normal
		value = getTempValue("/sys/class/power_supply/battery/");

		if(value != 0)
			return value;

		// Huawei a199
		value = getTempValue("/sys/class/power_supply/Battery/");
		if(value != 0)
			return value;

		// cynus
		value = getTempValue("/sys/devices/platform/mt6329-battery/");
		if(value != 0)
			return value;

		// nexus one cyangoenmod
		value = getTempValue("/sys/devices/platform/ds2784-battery/");
		if(value != 0)
			return value;

		// sony ericsson xperia x1
		value = getTempValue("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/ds2746-battery/");
		if(value != -1)
			return value;

		// xdandroid
		value = getTempValue("/sys/devices/platform/i2c-adapter/i2c-0/0-0036/power_supply/battery/");
		if(value != 0)
			return value;

		// Nook Color/sys/devices/platform/bq_bci_battery.1/power_supply/Battery
		value = getTempValue("/sys/class/power_supply/max17042-0/");
		if(value != 0)
			return value;

		// Nook Color
		value = getTempValue("/sys/class/power_supply/bq27520/");
		if(value != 0)
			return value;

		// Acer Iconia Tab A500
		value = getTempValue("/sys/EcControl/");
		if(value != 0)
			return value;

		// intel
		value = getTempValue("/sys/class/power_supply/max170xx_battery/");
		if(value != 0)
			return value;

		// intel
		value = getTempValue("/sys/class/power_supply/ab8500_fg/");
		if(value != 0)
			return value;

		// intel
		value = getTempValue("/sys/class/power_supply/android-battery/");
		if(value != 0)
			return value;

		//HUAWEI Hn3-u01
		value = getTempValue("/sys/devices/platform/bq_bci_battery.1/power_supply/Battery/");
		if(value != 0)
			return value;

		return null;
	}

	/**
	 * 读取温度文件
	 * @param prefix 不同系统的温度文件目录不同
	 * @return 当前电池温度
	 */
	private static Double getTempValue(String prefix) {

		File f1 = null  , f2 = null ;

		f1 = new File(prefix + "temp");
		f2 = new File(prefix + "batt_temp");

		String text = null;
		Double value = 0D;


		if(f1.exists()){

			try {
				FileInputStream fs = new FileInputStream(f1);
				InputStreamReader sr = new InputStreamReader(fs);
				BufferedReader br = new BufferedReader(sr);

				text = br.readLine();

				br.close();
				sr.close();
				fs.close();
			} catch (Exception ex) {

				Logger.error("getTempValue Exception " + ex.toString());

				ex.printStackTrace();
			}


			if (text != null) {

				try {

					value = Double.parseDouble(text);

					return value/10;

				} catch (NumberFormatException nfe) {

					Logger.error("getTempValue Exception " + nfe.toString());
					value = null;
				}

			}


		}else{

			Logger.error("File " + f1.getName() + " is not exist");
		}




		if(f2.exists()){

			try {
				FileInputStream fs = new FileInputStream(f2);
				InputStreamReader sr = new InputStreamReader(fs);
				BufferedReader br = new BufferedReader(sr);

				text = br.readLine();

				br.close();
				sr.close();
				fs.close();
			} catch (Exception ex) {

				Logger.error("getTempValue Exception " + ex.toString());
				ex.printStackTrace();
			}

			if (text != null) {
				try {

					value = Double.parseDouble(text);

					return value/10;

				} catch (NumberFormatException nfe) {

					Logger.error("getTempValue Exception " + nfe.toString());
					value = null;
				}

			}


		}else{

			Logger.error("File " + f2.getName() + " is not exist");
		}

		return value/10;

	}

	public static int getBattChargingStatus(String filePath){

		File f = new File(filePath + "charging_enabled");;


		String text = null;
		int res = 0;

		if(f.exists()){

			try {
				FileInputStream fs = new FileInputStream(f);
				InputStreamReader sr = new InputStreamReader(fs);
				BufferedReader br = new BufferedReader(sr);

				text = br.readLine();


				br.close();
				sr.close();
				fs.close();
			} catch (Exception ex) {

				Logger.error("getTempValue Exception " + ex.toString());

				ex.printStackTrace();
			}


			if (text != null) {

				try {

					res = Integer.parseInt(text);

					return res;

				} catch (NumberFormatException nfe) {

					Logger.error("getBattChargingStatus Exception " + nfe.toString());

				}

			}


		}else{

			Logger.error("File " + f.getName() + " is not exist");
		}


		return res;
	}

}

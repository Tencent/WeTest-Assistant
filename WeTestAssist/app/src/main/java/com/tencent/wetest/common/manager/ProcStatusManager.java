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
import java.io.BufferedReader;
import java.io.FileReader;

import android.annotation.SuppressLint;
import android.net.TrafficStats;

import com.tencent.wetest.common.log.Logger;


public class ProcStatusManager {

	static{
		System.loadLibrary("WeTestForAndroid");
	}

	private static BufferedReader br;


	public static native long  getTotalCpuTime();

	public static native long  getProcessCpuTime(int pid);


	public static native long[]  getNativeNetworkUsage(int uid,long[] arr);



	@SuppressLint("NewApi")
	public static long[]  getNetworkUsage(int uid)
	{

		//Logger.debug("uid : " + uid);

		long[] res = {0,0};

		try{

			res = getNetworkUsageJava(uid);


//			Logger.info("uid is : " + uid);
//
//			Logger.info("res 0 is : " + res[0] + " res 1 is : " + res[1]);

			if( res[0] == -1 || res[0] == 0){

				res[0] = TrafficStats.getUidRxBytes(uid);

				res[1] = TrafficStats.getUidTxBytes(uid);

				//res = getNativeNetworkUsage(uid, res);

			}

		}catch(Exception e){

			Logger.error("getNetworkUsageException:" + e.toString());
			//res = getNativeNetworkUsage( uid,res);

		}

		return res;

	}


	public static long[]  getNetworkUsageJava(int uid)
	{

		long[] res = {-1,-1};//res[0]:tcp_rcv res_[1]:tcp_snd
		String sysPath = "/proc/uid_stat/"+uid+"/tcp_rcv";

		BufferedReader br = null;

		try {

			br = new BufferedReader(new FileReader(sysPath));

			res[0] = Long.parseLong(br.readLine());

			if (br != null) {
				br.close();
			}

		} catch(Exception e){

			e.printStackTrace();
		}


		sysPath = "/proc/uid_stat/"+uid+"/tcp_snd";
		try {

			br = new BufferedReader(new FileReader(sysPath));

			res[1] = Long.parseLong(br.readLine());

			if (br != null) {
				br.close();
			}

		} catch(Exception e){

			e.printStackTrace();
		}

		return res;
	}
}

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
import java.io.InputStream;
import java.io.OutputStream;

import android.app.ActivityManager;
import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Debug.MemoryInfo;

import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.model.ProcMemInfo;
import com.tencent.wetest.common.util.SuUtil;

/**
 * ProcMemManager
 * 用于获取进程内存和FPS
 *
 */
public class ProcParamManager {

	private static String sysPath;
	private static BufferedReader br1 = null;
	private static BufferedReader br2 = null;
	private static BufferedReader br3 = null;
	private static String[] content;
	private static int pagesSize;

	private static int PM_MAP_READ = 1;
	private static int PM_MAP_WRITE = 2;
	private static int PM_MAP_EXEC = 4;

	private static final int RETRYTIME = 3;
	private static int retrytimes = 0;

	private static int fd = -1;

	private static final int ABSTRACTMODE = 0;
	private static final int CUSTOMMODE = 1;
	private static final String FILESYSTEM_SOCKET_PREFIX  = "/tmp/";
	private static final String ROOT_SERVER_NAME  = "com.tencent.wetested";

	private static String getRootData(int pids ,int fd,int mode){

		String res = null;

		try {

			LocalSocket client =null;
			LocalSocketAddress addr;
			client = new LocalSocket();

			switch(mode){

				case ABSTRACTMODE:

					addr = new LocalSocketAddress(ROOT_SERVER_NAME,LocalSocketAddress.Namespace.ABSTRACT);
					break;

				case CUSTOMMODE:

					addr = new LocalSocketAddress(ROOT_SERVER_NAME,LocalSocketAddress.Namespace.FILESYSTEM);
					break;

				default:

					Logger.error("mode error!");
					client.close();

					return null;

			}

			client.connect(addr);

			String pid = ""+pids;
			OutputStream writer =  client.getOutputStream();
			writer.write( pid.getBytes());
			writer.flush();

			byte[] buffer = new byte[500];

			InputStream reader =  client.getInputStream();
			int readlen = reader.read(buffer);

			byte[] recv = new byte[readlen];
			System.arraycopy(buffer, 0, recv, 0, readlen);

			res = new String(recv);

			// Logger.info("response is:"+res+"readlen is "+readlen);

			reader.close();

			writer.close();

			client.close();


		} catch (Exception e) {

			Logger.error("getRootDataException:" + e.toString());

			SuUtil.startWetestd2();

		}

		return res;
	}

	/**
	 * 获取进程内存信息
	 * @param pid
	 * @param info
	 */
	private static void getProcessMemInfo(int pid, ProcMemInfo info) {

		if (pid >= 0) {
			int[] pids = new int[1];
			pids[0] = pid;

			ActivityManager mAm = (ActivityManager) WTApplication
					.getContext().getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo[] memoryInfoArray = mAm.getProcessMemoryInfo(pids);
			MemoryInfo  pidMemoryInfo = memoryInfoArray[0];

			info.setNatived(pidMemoryInfo.nativePss);
			info.setDalvik(pidMemoryInfo.dalvikPss);
			info.setTotal(pidMemoryInfo.getTotalPss());
		}
	}

	public static ProcMemInfo getProcMem(int pid) {

		if( pid == -5){

			ProcMemInfo info = new ProcMemInfo();
			getProcessMemInfo(pid, info);
			return null;
		}

		ProcMemInfo info = new ProcMemInfo();

		try {

			getProcessMemInfo(pid, info);

			if (((WTApplication) WTApplication.getContext()).isRoot()) {


				String tmp = getRootData(pid, fd,ABSTRACTMODE);

				int j = 0;


				while (tmp == null || tmp.split("\\|").length != 5) {

					if (j++ == 3)
						break;
					if(j % 2 != 0){

						SuUtil.startWetestd();

					}
					else{

						SuUtil.startWeTestedWithShell();

					}

					tmp = getRootData(pid, fd,ABSTRACTMODE);

					Thread.sleep(1000);

				}

				if (j >= 3) {

					Logger.error("get data failed after try 3 times!");

					info.setVss("0");
					info.setRss("0");
					info.setUss("0");
					info.setPss("0");
					info.setFps(0);

				} else {

					String[] res = tmp.split("\\|");

					info.setVss(res[0]);
					info.setRss(res[1]);
					info.setUss(res[2]);
					info.setPss(res[3]);
					try {

						int fps = Integer.parseInt(res[4]);

						if(fps <= 0){

							SuUtil.startWeTestedWithShell();

						}

						info.setFps(fps);

					} catch (NumberFormatException e) {

						SuUtil.startWeTestedWithShell();

					}
				}
			}
		} catch (Exception e) {

			Logger.error("procMemException:" + e.toString());

		}
		return info;
	}
}

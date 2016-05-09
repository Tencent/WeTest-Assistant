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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.os.Build;

import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.Shell;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;

/**
 * 用于执行root权限操作
 *
 */
public class SuUtil {

	private static Process process;
	private static String wtPid = "-1";

	/**
	 * 结束进程,执行操作调用即可
	 */
	public static void kill(String packageName) {
		initProcess();
		killProcess(packageName);
		close();
	}

	public static void change1(int pid) {
		initProcess();
		changePk(pid);
		close();
	}


	public static void test(String pid) {
		initProcess();
		tests(pid);
		close();
	}

	public static String tests(String cmd){
		OutputStream out = process.getOutputStream();

		try {

			out.write(cmd.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static void changePk(int pid) {
		OutputStream out = process.getOutputStream();
		String cmd = "chmod 777 proc/" + pid + "/maps \n";
		try {

			out.write(cmd.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void change2(int pid) {
		initProcess();
		changePk2(pid);
		close();
	}


	private static void changePk2(int pid) {
		OutputStream out = process.getOutputStream();
		String cmd = "chmod 777 proc/" + pid + "/pagemap \n";

		try {

			out.write(cmd.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 初始化进程
	 */
	private static void initProcess() {
		if (process == null)
			try {
				process = Runtime.getRuntime().exec("su");

			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * 结束进程
	 */
	private static void killProcess(String packageName) {
		OutputStream out = process.getOutputStream();
		String cmd = "am force-stop " + packageName + " \n";

		try {

			out.write(cmd.getBytes());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭输出流
	 */
	private static void close() {
		if (process != null)
			try {
				process.getOutputStream().close();
				process = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@SuppressLint("SdCardPath")
	public static void startWetestd(){

		executeCommand(1,"service call SurfaceFlinger 1008 i32 1");

		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			executeCommand(1,"setenforce 0");
		}

		boolean hasWetestd = false;
		boolean hasSurfaceFlinger = false;

		String fps_process = "";

		try {
			Process pbd = Runtime.getRuntime().exec("ps");
			//pbd.waitFor();
			InputStream is = pbd.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String br = null;
			while((br = reader.readLine())!= null){
				String[] process = br.split("\\s+");
				//ogger.info("size _ process:"+process.length);

				if(process.length == 9){
					//Logger.info(" process is:"+process[8]);
					if(process[8].equals("/data/data/com.tencent.wefpmonitor/files/wetested")){
						hasWetestd = true;
						wtPid = process[1];
						//break;
					}else if (process[8].equals("/system/bin/surfaceflinger")){

						hasSurfaceFlinger = true;

					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		if(hasSurfaceFlinger){

			fps_process = "/system/bin/surfaceflinger";

		}else{

			fps_process = "system_server";
		}

		Logger.debug("process is " + fps_process);

		if(!hasWetestd){

			String commandPath = WTApplication.getContext().getFilesDir() + "/wetested '" + fps_process+"'";
			ProcessBuilder pb = null;
			String command = commandPath + "&";

			pb = new ProcessBuilder("su","-c", command);

			pb.redirectErrorStream(true);

			try {

				pb.start();

			} catch (Exception e) {

				Logger.error("startWetestd Exception : " + e.toString());

			}

			boolean hasWeTested = false;

			try {
				Process pbd = Runtime.getRuntime().exec("ps");
				//pbd.waitFor();
				InputStream is = pbd.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String br = null;
				while((br = reader.readLine())!= null){
					String[] process = br.split("\\s+");
					//ogger.info("size _ process:"+process.length);

					if(process.length == 9){
						//Logger.info(" process is:"+process[8]);
						if(process[8].equals("/data/data/com.tencent.wefpmonitor/files/wetested")){
							hasWeTested = true;
							wtPid = process[1];
							break;
						}

					}
				}

				if(!hasWeTested){

					executeCommand(1, command);

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

		}else{
			Logger.info("wetested is already started");
		}

		//startWeTestedWithShell();

	}


	public static void startWetestd2(){


		boolean hasWetestd = false;

		try {
			Process pbd = Runtime.getRuntime().exec("ps");
			//pbd.waitFor();
			InputStream is = pbd.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String br = null;
			while((br = reader.readLine())!= null){
				String[] process = br.split("\\s+");
				//ogger.info("size _ process:"+process.length);

				if(process.length == 9){
					//Logger.info(" process is:"+process[8]);
					if(process[8].equals("/data/data/com.tencent.wefpmonitor/files/wetestd")){
						hasWetestd = true;
						break;
					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		if(!hasWetestd){
			String commandPath = WTApplication.getContext().getFilesDir() + "/wetestd";
			ProcessBuilder pb = null;
			String command = commandPath + "&";

			//executeCommand(1, command);

			pb = new ProcessBuilder("su","-c", command);

			Process exec = null;

			pb.redirectErrorStream(true);
			try {

				Logger.info("start wetestd is started");
				exec = pb.start();

				Thread.sleep(1000);

			} catch (Exception e) {

				e.printStackTrace();

			}
		}else{
			Logger.info("wetestd is already started");
		}



	}

	public static void executeCommand(int id, String cmd){


		if(((WTApplication) WTApplication.getContext()).isRoot()){


			try {

				StackTraceElement[] ste = new Throwable().getStackTrace();
				for(StackTraceElement s : ste){
					Logger.info(s.toString());
				}

				Shell.runRootCommand(new Command(id, cmd){

					StringBuilder sb = new StringBuilder();

					private void output(String label, Object arg0, Object arg1){
						sb.setLength(0);
						sb.append(label);
						sb.append(" > ");
						sb.append("arg0: ");
						sb.append(arg0);
						sb.append("; arg1: ");
						sb.append(arg1);
						Logger.info(sb.toString());
					}

					@Override
					public void commandCompleted(int arg0, int arg1) {
						output("commandCompleted", arg0, arg1);
					}

					@Override
					public void commandOutput(int arg0, String arg1) {
						output("commandOutput", arg0, arg1);
					}

					@Override
					public void commandTerminated(int arg0, String arg1) {
						output("commandTerminated", arg0, arg1);
					}});

			} catch (Exception e) {
				Logger.error("run command fail.", e);
			}

		}


	}

	public static void execSuCmd(){

		if(((WTApplication) WTApplication.getContext()).isRoot()){


			executeCommand(1,"service call SurfaceFlinger 1008 i32 1");

			String sopath = "/data/data/com.tencent.wefpmonitor/files/libfps0.so";

			String injCmd = "/data/data/com.tencent.wefpmonitor/files/inject install /system/bin/surfaceflinger "+sopath+"" +
					" Y > /data/local/tmp/inj.log 2>&1";

			Logger.error(injCmd);

			String commandPath = WTApplication.getContext().getFilesDir() + "/wetestd";
			ProcessBuilder pb = null;
			String command = commandPath + "&";

			//executeCommand(1, command);

			pb = new ProcessBuilder("su","-c", injCmd);

			Process exec = null;

			pb.redirectErrorStream(true);
			try {

				exec = pb.start();

				Thread.sleep(1000);

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

	}


	public static void startWeTestedWithShell(){

		//executeCommand(1, "kill -9 " + wtPid);

		boolean hasSurfaceFlinger = false;

		String fps_process = "";

		try {
			Process pbd = Runtime.getRuntime().exec("ps");
			//pbd.waitFor();
			InputStream is = pbd.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String br = null;
			while((br = reader.readLine())!= null){
				String[] process = br.split("\\s+");
				//ogger.info("size _ process:"+process.length);

				if(process.length == 9){

					if (process[8].equals("/system/bin/surfaceflinger")){

						hasSurfaceFlinger = true;

					}

					if (process[8].contains("wetested")){

						executeCommand(1, "kill -9 " + process[1]);


					}

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		if(hasSurfaceFlinger){

			fps_process = "/system/bin/surfaceflinger";

		}else{

			fps_process = "system_server";
		}

		//fps_process = "system_server";

		String command = "cp " + WTApplication.getContext().getFilesDir()+ "/wetested /system/xbin";

		Logger.info(command);

		executeCommand(1, "mount -o remount /system");
		executeCommand(1, command);
		executeCommand(1, "chmod 755 /system/xbin/wetested");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		command = "cp " + WTApplication.getContext().getFilesDir()+ "/inject /system/xbin";

		Logger.info(command);

		executeCommand(1, "mount -o remount /system");
		executeCommand(1, command);
		executeCommand(1, "chmod 755 /system/xbin/inject");

		executeCommand(1, "/system/xbin/wetested '" + fps_process+"'&");

		executeCommand(1, "/system/xbin/inject install "+fps_process+" /data/data/com.tencent.wefpmonitor/files/libfps0.so Y > /data/local/tmp/inj1.log 2>&1");


	}

}  
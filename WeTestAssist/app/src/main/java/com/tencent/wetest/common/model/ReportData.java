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

package com.tencent.wetest.common.model;

/**
 * 测试报告的上报信息非JSON格式
 *
 * 用于数据统计时，Report中dataList中的元素
 *
 */
public class ReportData {

	private double cpu; //进程消耗的cpu时间

	private double pNetworUsagekIn; //进程接收流量
	private double pNetworUsagekOut; //进程发送流量

	private double pNative; //进程所占native内存
	private double pDalvik; //进程所占dalvik内存
	private double pTotal; //进程所占总内存

	private String pTemperature; // 电池温度
	private String pCurrent; // 电池电量

	private long time; //时间戳
	private String day;
	private int fps; //屏幕刷新率
	private String tag; // 场景标签

	private SceneInfo sceneInfo;

	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	public double getpNetworUsagekIn() {
		return pNetworUsagekIn;
	}
	public void setpNetworUsagekIn(double pNetworUsagekIn) {
		this.pNetworUsagekIn = pNetworUsagekIn;
	}
	public double getpNetworUsagekOut() {
		return pNetworUsagekOut;
	}
	public void setpNetworUsagekOut(double pNetworUsagekOut) {
		this.pNetworUsagekOut = pNetworUsagekOut;
	}

	public double getpNative() {
		return pNative;
	}
	public void setpNative(double pNative) {
		this.pNative = pNative;
	}
	public double getpDalvik() {
		return pDalvik;
	}
	public void setpDalvik(double pDalvik) {
		this.pDalvik = pDalvik;
	}
	public double getpTotal() {
		return pTotal;
	}
	public void setpTotal(double pTotal) {
		this.pTotal = pTotal;
	}

	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getpTemperature() {
		return pTemperature;
	}
	public void setpTemperature(String pTemperature) {
		this.pTemperature = pTemperature;
	}
	public String getpCurrent() {
		return pCurrent;
	}
	public void setpCurrent(String pCurrent) {
		this.pCurrent = pCurrent;
	}
	public SceneInfo getSceneInfo() {
		return sceneInfo;
	}
	public void setSceneInfo(SceneInfo sceneInfo) {
		this.sceneInfo = sceneInfo;
	}

}

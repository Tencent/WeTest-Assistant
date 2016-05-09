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

import java.util.ArrayList;
import java.util.List;

/**
 * 测试报告信息
 * 用于测试记录页面显示的详细信息
 * 和性能数据集合
 */
public class Report {


	/**
	 *
	 * duration : 测试持续时间
	 * phone : 手机品牌
	 * timeStart : 悬浮窗中点击开始的时间
	 * timeEnd : 悬浮窗中点击暂停的时间
	 * timeTStart : 开始测试时间
	 * timeTEnd : 结束测试时间
	 * baseTime : 基准时间（服务器获取的时间）
	 * baseColock : 基准系统时钟
	 * datalist : 性能数据结合
	 * testid : 测试id
	 *
	 */
	private String dateTime;
	private double duration;
	private String phone;
	private double consume;
	private long timeStart;
	private long timeEnd;
	private long timeTStart;
	private long timeTEnd;
	private long baseTime;
	private long baseColock;
	private String testid;

	private List<ReportData> datalist;

	public Report() {

		datalist = new ArrayList<ReportData>();

	}

	/**
	 * getter setter 方法
	 *
	 */
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getConsume() {
		return consume;
	}

	public void setConsume(double consume) {
		this.consume = consume;
	}

	public long getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}

	public long getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(long timeEnd) {
		this.timeEnd = timeEnd;
	}

	public long getTimeTStart() {
		return timeTStart;
	}

	public void setTimeTStart(long timeTStart) {
		this.timeTStart = timeTStart;
	}

	public long getTimeTEnd() {
		return timeTEnd;
	}

	public void setTimeTEnd(long timeTEnd) {
		this.timeTEnd = timeTEnd;
	}

	public List<ReportData> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<ReportData> datalist) {
		this.datalist = datalist;
	}

	public long getBaseTime() {
		return baseTime;
	}

	public void setBaseTime(long baseTime) {
		this.baseTime = baseTime;
	}

	public long getBaseColock() {
		return baseColock;
	}

	public void setBaseColock(long baseColock) {
		this.baseColock = baseColock;
	}

	public String getTestid() {
		return testid;
	}

	public void setTestid(String testid) {
		this.testid = testid;
	}

}

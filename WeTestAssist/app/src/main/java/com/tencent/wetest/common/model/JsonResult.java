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

import org.json.JSONArray;

/**
 *	性能JSON数据解析
 *  性能数据以JSON格式存储 
 *  将性能数据转换成JSON数据
 *  方便直接存储和读取
 *
 */

public class JsonResult {

	private JSONArray content_f_cpu; //进程消耗的cpu时间

	private JSONArray content_f_native; //进程所占native内存
	private JSONArray content_f_dalvik; //进程所占dalvik内存
	private JSONArray content_f_total;  //进程所占总内存 

	private JSONArray content_f_networkIn; //进程接收流量
	private JSONArray content_f_networkOut; //进程发送流量

	private JSONArray content_f_Fps; //屏幕刷新率

	private JSONArray content_f_time; //时间戳

	private JSONArray content_f_Tag; // 场景标签

	private JSONArray content_f_temperature; //电池温度

	private JSONArray content_f_current;//电池电量

	public JsonResult() {

		content_f_cpu = new JSONArray();

		content_f_native = new JSONArray();
		content_f_dalvik = new JSONArray();
		content_f_total = new JSONArray();

		content_f_networkIn = new JSONArray();
		content_f_networkOut = new JSONArray();

		content_f_Fps = new JSONArray();

		content_f_time = new JSONArray();

		content_f_Tag = new JSONArray();

		content_f_temperature = new JSONArray();

		content_f_current = new JSONArray();
	}

	public JSONArray getContent_f_cpu() {
		return content_f_cpu;
	}

	public void setContent_f_cpu(JSONArray content_f_cpu) {
		this.content_f_cpu = content_f_cpu;
	}


	public JSONArray getContent_f_native() {
		return content_f_native;
	}

	public void setContent_f_native(JSONArray content_f_native) {
		this.content_f_native = content_f_native;
	}

	public JSONArray getContent_f_dalvik() {
		return content_f_dalvik;
	}

	public void setContent_f_dalvik(JSONArray content_f_dalvik) {
		this.content_f_dalvik = content_f_dalvik;
	}

	public JSONArray getContent_f_total() {
		return content_f_total;
	}

	public void setContent_f_total(JSONArray content_f_total) {
		this.content_f_total = content_f_total;
	}

	public JSONArray getContent_f_networkIn() {
		return content_f_networkIn;
	}

	public void setContent_f_networkIn(JSONArray content_f_networkIn) {
		this.content_f_networkIn = content_f_networkIn;
	}

	public JSONArray getContent_f_networkOut() {
		return content_f_networkOut;
	}

	public void setContent_f_networkOut(JSONArray content_f_networkOut) {
		this.content_f_networkOut = content_f_networkOut;
	}

	public JSONArray getContent_f_Fps() {
		return content_f_Fps;
	}

	public void setContent_f_Fps(JSONArray content_f_Fps) {
		this.content_f_Fps = content_f_Fps;
	}

	public JSONArray getContent_f_time() {
		return content_f_time;
	}

	public void setContent_f_time(JSONArray content_f_time) {
		this.content_f_time = content_f_time;
	}

	public JSONArray getContent_f_Tag() {
		return content_f_Tag;
	}

	public void setContent_f_Tag(JSONArray content_f_Tag) {
		this.content_f_Tag = content_f_Tag;
	}

	public JSONArray getContent_f_temperature() {
		return content_f_temperature;
	}

	public void setContent_f_temperature(JSONArray content_f_temperature) {
		this.content_f_temperature = content_f_temperature;
	}

	public JSONArray getContent_f_current() {
		return content_f_current;
	}

	public void setContent_f_current(JSONArray content_f_current) {
		this.content_f_current = content_f_current;
	}

}

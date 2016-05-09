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
 * 内存信息
 * vss rss pss uss 需root后获取
 *
 */
public class ProcMemInfo {

	/**
	 * vss : Virtual Set Size 虚拟耗用内存（包含共享库占用的内存）
	 * rss : Resident Set Size 实际使用物理内存（包含共享库占用的内存）
	 * pss : Proportional Set Size 实际使用的物理内存（比例分配共享库占用的内存）
	 * uss : Unique Set Size 进程独自占用的物理内存（不包含共享库占用的内存）
	 */
	private String vss;
	private String  rss;
	private String pss;
	private String uss;


	/**
	 * natived : 进程所占native内存
	 * dalvik : 进程所占dalvik内存
	 * total : total
	 */
	private int natived;
	private int dalvik;
	private int total;

	/**
	 * fps : 屏幕刷新率
	 */
	private int fps;


	/**
	 * getter setter 方法
	 *
	 */
	public String getVss() {
		return vss;
	}
	public void setVss(String vss) {
		this.vss = vss;
	}
	public String getRss() {
		return rss;
	}
	public void setRss(String rss) {
		this.rss = rss;
	}
	public String getPss() {
		return pss;
	}
	public void setPss(String pss) {
		this.pss = pss;
	}
	public String getUss() {
		return uss;
	}
	public void setUss(String uss) {
		this.uss = uss;
	}
	public int getNatived() {
		return natived;
	}
	public void setNatived(int natived) {
		this.natived = natived;
	}
	public int getDalvik() {
		return dalvik;
	}
	public void setDalvik(int dalvik) {
		this.dalvik = dalvik;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}


}

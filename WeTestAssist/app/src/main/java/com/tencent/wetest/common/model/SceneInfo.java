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
 * 记录当前场景信息
 *
 */
public class SceneInfo {

	private String startTagName;
	private long startTagTime;
	private String endTagName;
	private long endTagTime;


	public String getStartTagName() {
		return startTagName;
	}
	public void setStartTagName(String startTagName) {
		this.startTagName = startTagName;
	}
	public long getStartTagTime() {
		return startTagTime;
	}
	public void setStartTagTime(long startTagTime) {
		this.startTagTime = startTagTime;
	}
	public String getEndTagName() {
		return endTagName;
	}
	public void setEndTagName(String endTagName) {
		this.endTagName = endTagName;
	}
	public long getEndTagTime() {
		return endTagTime;
	}
	public void setEndTagTime(long endTagTime) {
		this.endTagTime = endTagTime;
	}


}

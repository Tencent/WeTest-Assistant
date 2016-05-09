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


public class FPSInfo {

	private int fps;
	private long time;
	private int type;


	public int getFps() {
		return fps;
	}
	public void setFps(int fps) {
		this.fps = fps;
	}

	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}


}

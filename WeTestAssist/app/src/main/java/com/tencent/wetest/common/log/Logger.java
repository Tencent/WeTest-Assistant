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

package  com.tencent.wetest.common.log;

import android.util.Log;

/**
 * 统一LOG显示内容
 *
 */
public final class Logger {
	protected static String tag = "wetest";

	public static void info(String t){
		Log.i(tag, t);
	}

	public static void error(String t){
		Log.e(tag, t);
	}

	public static void error(String t, Throwable e){
		Log.e(tag, t, e);
	}

	public static void debug(String t){
		Log.d(tag, t);
	}
}

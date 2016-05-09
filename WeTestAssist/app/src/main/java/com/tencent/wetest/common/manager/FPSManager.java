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

public class FPSManager {

	 static{
	    	System.loadLibrary("WeTestForAndroid");
	 }

	 public static int injectFps(){
		 
		 return injectNFps();

	 }
	 
	 public static native int injectNFps();
	
	 public static  int disinjectFps(){
		 
		 return disinjectNFps();
		 
	 }

    private static native int disinjectNFps();


}

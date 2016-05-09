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

#import <Foundation/Foundation.h>

/**
 *  测试记录信息
 */

@interface LogInfo: NSObject{
    
    NSString *filePath;//测试记录文件路径
    NSString *saveTime;//记录保存时间
    NSString *appPkgName;//被测应用包名
    
    NSString *testTime;//测试时间
    NSString *appName;//测试应用版本
 
    NSString *timeStart;//测试开始时间
    NSString *timeEnd;//测试结束时间
    
    NSString *appVersion;//应用版本名称
    
    
    
}

//for upload
-(void) setFilePath: (NSString*) _filePath;
-(void) setSaveTime: (NSString *) _saveTime;
-(void) setAppPkgName: (NSString *) _appPkgName;
-(void) setTimeStart: (NSString *) _timeStart;
-(void) setTimeEnd: (NSString *) _timeEnd;
-(void) setAppVersion: (NSString *) _appVersion;

-(NSString*) filePath;
-(NSString *) saveTime;
-(NSString *) appPkgName;
-(NSString *) toString;
-(NSString *) timeStart;
-(NSString *) timeEnd;
-(NSString *) appVersion;


//for show
-(void) setTestTime: (NSString*) _testTime;
-(void) setAppName: (NSString *) _appName;

-(NSString*) testTime;
-(NSString*) appName;



@end
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

#import "LogInfo.h"

/**
 *  测试记录信息
 */
@implementation LogInfo


-(void) setFilePath: (NSString*) _filePath{

    filePath = _filePath;
}
-(void) setSaveTime: (NSString *) _saveTime{
    saveTime = _saveTime;
}
-(void) setAppPkgName: (NSString *) _appPkgName{

    appPkgName = _appPkgName;
}


-(void) setTimeStart:(NSString *)_timeStart{
    
    timeStart = _timeStart;
}

-(void) setTimeEnd:(NSString *)_timeEnd{
    
    timeEnd = _timeEnd;
}

-(void) setAppVersion: (NSString *) _appVersion{

    appVersion = _appVersion;
    
}

/**
 *  测试记录文件路径
 *
 *  @return filePath
 */
-(NSString*) filePath{

    return filePath;
}

/**
 *  记录保存时间
 *
 *  @return save time
 */
-(NSString *) saveTime{
    
    return saveTime;
}

/**
 *  被测应用包名
 *
 *  @return app pkg name
 */
-(NSString *) appPkgName{

    return appPkgName;

}



/**
 *  测试开始时间
 *
 *  @return start time
 */
-(NSString *) timeStart{
    
    
    return  timeStart;
    
}

/**
 *  测试结束时间
 *
 *  @return end time
 */
-(NSString *) timeEnd{
    
    
    return  timeEnd;
    
}

/**
 *  测试应用版本
 *
 *  @return app verion name
 */

-(NSString *) appVersion{
    
    
    return  appVersion;
    
}

/**
 *  用于显示测试记录格式
 *
 *  @return format stirng
 */
-(NSString *) toString{


    return  [NSString stringWithFormat:@"%@  %@",appName,saveTime];

}


//for show
-(void) setTestTime: (NSString*) _testTime{

    testTime = _testTime;
    
}
-(void) setAppName: (NSString *) _appName{

    appName = _appName;
    
}

/**
 *  测试时间
 *
 *  @return test time
 */
-(NSString*) testTime{

    return testTime;
    
}

/**
 *  测试应用名称
 *
 *  @return app name
 */
-(NSString*) appName{
    
    return appName;

}




@end
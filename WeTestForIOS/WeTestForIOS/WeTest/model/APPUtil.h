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

#include "LogInfo.h"
#import "JsonResult.h"
#include "APPInfo.h"

/**
 *  方法工具类 实现所有后台逻辑
 */
@interface APPUtil : NSObject{

    
    int startWiFiFlow,startWiFiReceived,startWiFiSent,startWWANFlow,startWWANReceived,startWWANSend,wififlow,wifisend,wifireceive,wwanFlow,wwanSend,wwanReceive,fps;

    
}

-(void)setStartFlow;

+ (APPUtil *)sharedInstance;

-(NSString *) getDeviceInfo;

- (NSUInteger) getSysInfo: (uint) typeSpecifier;


-(BOOL) autoLogin;

-(void)paraCapture;

-( LogInfo * )reportSave;

-(void) fileAppend:(NSString *) content fileName:(NSString *) name;

-(void) addRecord;

-(UIColor *)colorWithHexColorString:(NSString *)hexColorString;

-(UIImage*)  OriginImage:(UIImage *)image   scaleToSize:(CGSize)size;

-(NSString *)  formatLongTime: (long long) tip;

-(void) logClear;

-(void) logout;

-(void) delLog:(NSString * ) fileName;

-(void) updateLog:(NSString * ) fileName content:(NSString *) content;

-(JsonResult *) readRecordContent:(NSString * ) fileName;

-(BOOL)CompareVersionFromOldVersion : (NSString *)oldVersion
                         newVersion : (NSString *)newVersion;

- (NSMutableDictionary*) deviceName;

- (void) touchEvent;

-(NSMutableArray * ) getApplistInfo;

-(APPInfo * ) getApplistInfoByPkgName :(NSString * ) pkgName;

-(int )compareDate:(NSString *)strDate;

- (UIImage *)imageWithColor:(UIColor *)color;

@end
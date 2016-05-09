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

#import <UIKit/UIKit.h>
#import "model/APPInfo.h"
#import <AVFoundation/AVFoundation.h>
#include <Security/Security.h>
#import "model/LogInfo.h"
#include <sys/sysctl.h>
#import "ViewController.h"
/**
 *  应用代理
 */

@interface AppDelegate : UIResponder <UIApplicationDelegate,UIAlertViewDelegate>{
    
    
}


@property (strong, nonatomic) UIWindow *window;

@property (assign, nonatomic) UIBackgroundTaskIdentifier bgTask;

@property (strong, nonatomic) dispatch_block_t expirationHandler;
@property (assign, nonatomic) BOOL jobExpired;
@property (assign, nonatomic) BOOL background;

@property UIBackgroundTaskIdentifier newtaskID;
@property UIBackgroundTaskIdentifier oldtaskID;

@property (strong,nonatomic) APPInfo *app;

@property (nonatomic) dispatch_source_t timer;
@property (nonatomic) dispatch_queue_t queue;
@property (nonatomic) BOOL flag;
@property (strong,nonatomic) NSMutableArray *reportList;
@property (strong,nonatomic)  AVAudioPlayer* player;

@property (strong,nonatomic)  NSString* fileName;

//@property (strong,nonatomic)  CLLocationManager * locationManager;

@property (assign) int fps;

@property (assign) long long baseTime ;

@property (assign) NSTimeInterval baseColock ;
@property (assign) NSTimeInterval timeStart ;
@property (assign) NSTimeInterval timeEnd ;

@property (strong,nonatomic)  NSLock* lock;


@property (assign) int loginType;

@property (strong,nonatomic) IBOutlet UIAlertView * capAlert;
@property (retain,nonatomic) IBOutlet UIAlertView * tipAlert;

@property (retain,nonatomic) IBOutlet UIButton *testStartButton;

@property (retain,nonatomic) IBOutlet LogInfo * nLog;

//@property (strong, nonatomic) UINavigationController *navController;


/**
 *  QQ网页登陆回调
 *
 *  @param skey
 *  @param strUin
 */
- (void)loginByWebQQ:(NSString *) skey uin:(NSString *)strUin;
/**
 *  OA网页登陆回调
 */
- (void)loginByWebOA;

@end

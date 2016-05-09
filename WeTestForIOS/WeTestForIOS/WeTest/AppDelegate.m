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

#import "AppDelegate.h"

#import <CoreLocation/CoreLocation.h>
#import "APPListViewController.h"

#include <dlfcn.h>
#include <objc/runtime.h>
#include <mach/mach_traps.h>
#include <mach/mach_init.h>
#include "ViewController.h"
#import "model/APPUtil.h"
#import <AVFoundation/AVFoundation.h>
#include <OpenAL/al.h>
#include <OpenAL/alc.h>
#import "Constants.h"

#import "model/CHKeychain.h"
#include <sys/sysctl.h>
#import <UIKit/UIKit.h>
#include <mach/mach.h>
#import "Constants.h"


bool isLogined = true;
UIAlertView * myAlert;

@implementation AppDelegate



- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
   
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    NSFileManager * manager = [NSFileManager defaultManager];
    
    NSString * appPath = [documentPaths objectAtIndex:0];
    
    BOOL isDir = true;
    
    BOOL isDirExist = [manager fileExistsAtPath:appPath
                                        isDirectory:&isDir];
    
    NSBundle * mainBundle = [NSBundle mainBundle];
    NSURL * bundleFullURL = [mainBundle bundleURL];
    NSString * s_bundleURL = [bundleFullURL absoluteString];
   
//    if([s_bundleURL hasPrefix:@"file:///private/"])
//        
//    {
//        NSString * message = [NSString stringWithFormat:@"WeTest安装不正确:%@",s_bundleURL ];
//        
//        
//        UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"请重新安装"
//                                                         message:message
//                                                        delegate: self
//                                               cancelButtonTitle: @"确定"
//                                               otherButtonTitles: nil];
//        [alert show];
//        
//    }else
        if(!(isDirExist && isDir)){
    
        UIAlertView * alert2 = [[UIAlertView alloc] initWithTitle:@"请重新安装"
                                                         message: [NSString stringWithFormat:@"WeTest未能正确安装:%@",appPath]
                                                        delegate: self
                                               cancelButtonTitle: @"确定"
                                               otherButtonTitles: nil];
        [alert2 show];

    }
    
    
    
    self.capAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                         message: @"结束测试?"
                                        delegate: self
                               cancelButtonTitle: @"继续测试"
                               otherButtonTitles: @"结束", nil];
    
    self.capAlert.tag = 15;
    
    UIActivityIndicatorView *progress= [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(125, 50, 30, 30)];
    progress.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    //progress.backgroundColor = [UIColor blackColor];
    //[loginProgress_ startAnimating];
    
    myAlert = [[UIAlertView alloc] initWithTitle:nil
                                         message: @"正在登录..."
                                        delegate: self
                               cancelButtonTitle: nil
                               otherButtonTitles: nil];
    
    myAlert.tag = 1;
    [myAlert setAlertViewStyle:UIAlertViewStyleDefault];
    UIColor *testColor1= [UIColor colorWithRed:240/255.0 green:240/255.0 blue:240/255.0 alpha:0.5];
    [[myAlert layer] setContents:testColor1];
    
    
    [myAlert setValue:progress forKey:@"accessoryView"];
    
    
    [progress startAnimating];

    
    UInt32 doChangeDefaultRoute = 1;
    AudioSessionSetProperty(kAudioSessionProperty_OverrideCategoryDefaultToSpeaker, sizeof(doChangeDefaultRoute), &doChangeDefaultRoute);
    
    self.reportList = [[NSMutableArray alloc] init];

    //[[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
    
    [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback error:nil];
    
    if (!self.lock) {
        
        self.lock = [[NSLock alloc] init];
        
    }
    
    [[AVAudioSession sharedInstance] setActive:YES  withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:nil];
     UIApplication* app = [UIApplication sharedApplication];
    
    __weak AppDelegate* this = self;

    self.expirationHandler = ^{
        
        [[UIApplication sharedApplication] endBackgroundTask:this.bgTask];
        
        this.bgTask = UIBackgroundTaskInvalid;
        this.bgTask = [app beginBackgroundTaskWithExpirationHandler:this.expirationHandler];
        
        {
            [[AVAudioSession sharedInstance] setActive:YES error:nil];
            [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
            
            NSString * soundFilePath = [[NSBundle mainBundle] pathForResource: @"resource/Bulletin"        ofType: @"m4r"];
          
            NSURL *  fileURL = [[NSURL alloc] initFileURLWithPath: soundFilePath];
            
            NSError * error ;
            
            
            this.player = [[AVAudioPlayer alloc] initWithContentsOfURL: fileURL  error: &error];
            
            this.player.volume = 0;
            
            this.player.numberOfLoops = -1;
            
            [this.player play];
        
        }
        
    };
    
    [self monitorBatteryStateInBackground];
  
    return YES;
}


- (void)monitorBatteryStateInBackground
{
    self.background = YES;
    [self startBackgroundTask];
}


- (void)applicationWillResignActive:(UIApplication *)application
{
    
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    
    if (self.flag) {//测试开始
        
        self.bgTask = [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:self.expirationHandler];
        [[AVAudioSession sharedInstance] setActive:YES error:nil];
        [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
        
        NSString * soundFilePath = [[NSBundle mainBundle] pathForResource: @"resource/Bulletin"        ofType: @"m4r"];
        
        NSURL *  fileURL = [[NSURL alloc] initFileURLWithPath: soundFilePath];
        
        NSError * error ;
        
        self.player = [[AVAudioPlayer alloc] initWithContentsOfURL: fileURL  error: &error];
        
        self.player.volume = 0;
        self.player.numberOfLoops = -1;
        
        [self.player play];
        
        
    }else{
              [[UIApplication sharedApplication] setKeepAliveTimeout:600 handler:^{
            
                  
        }];
        
    }
   
    

   

}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    
    if (self.flag) {
       
        dispatch_suspend(self.timer);
        [self.capAlert show];
        
    }

    [UIApplication sharedApplication].applicationIconBadgeNumber=0;//取消应用程序通知脚标
   
    self.background = NO;
    
    [self.player stop];
  
}

/**
 *  app 将要终止时调用
 *
 *  @param application 全局实例
 */
- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error//当定位服务不可用出错时，系统会自动调用该函数
{
    
    if([error code]==kCLErrorDenied)//通过error的code来判断错误类型
    {
        //Access denied by user
        // [InterfaceFuncation ShowAlertWithMessage:@"错误" AlertMessage:@"未开启定位服务\n客户端保持后台功能需要调用系统的位置服务\n请到设置中打开位置服务" ButtonTitle:@"好"];
    }
}

/**
 *  bak for solution2
 */
- (void)startBackgroundTask
{
    
    
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
          
            NSInteger count=0;
            BOOL NoticeNoBackground=false;
            BOOL FlushBackgroundTime=false;
           
            while(self.background && !self.jobExpired)
            {
                
                
                NSTimeInterval backgroundTimeRemaining = [[UIApplication sharedApplication] backgroundTimeRemaining];
                
                [NSThread sleepForTimeInterval:1];
                
                count++;
                
                
                if(count>120)
                {
                    count=0;
                 
                    [NSThread sleepForTimeInterval:1];
                    
                    FlushBackgroundTime=false;
                   
                    [[AVAudioSession sharedInstance] setActive:YES error:nil];
                    [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
                    
                    count=0;
                
                    NSString * soundFilePath = [[NSBundle mainBundle] pathForResource: @"resource/Bulletin"        ofType: @"m4r"];
                    
                    NSURL *  fileURL = [[NSURL alloc] initFileURLWithPath: soundFilePath];
                    
                    
                    
                    NSError * error ;
                    
                    self.player = [[AVAudioPlayer alloc] initWithContentsOfURL: fileURL  error: &error];
                    
                    self.player.volume = 0;
                    
                    [self.player play];
                   
                 
                     [[AVAudioSession sharedInstance] setActive:NO  withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:nil];
               
                    [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:nil];
                    
                    NSTimeInterval backgroundTimeRemaining = [[UIApplication sharedApplication] backgroundTimeRemaining];
                   
                    if(backgroundTimeRemaining<200&&NoticeNoBackground==false)
                    {
                        
                        [[AVAudioSession sharedInstance] setActive:YES error:nil];
                         [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback withOptions:AVAudioSessionCategoryOptionMixWithOthers error:nil];
                        
                        NSString * soundFilePath = [[NSBundle mainBundle] pathForResource: @"resource/Bulletin" ofType: @"m4r"];
                       
                        NSURL *  fileURL = [[NSURL alloc] initFileURLWithPath: soundFilePath];
                        
                        
                        NSError * error ;
                        
                       self.player = [[AVAudioPlayer alloc] initWithContentsOfURL: fileURL  error: &error];
                        
                        self.player.volume = 0;
                        
                        [self.player play];
                        
                        [[AVAudioSession sharedInstance] setActive:NO  withOptions:AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation error:nil];
                      
                        [[UIApplication sharedApplication] beginBackgroundTaskWithExpirationHandler:nil];
                    
                    }
                    
                }
                
                
            }
            self.jobExpired = NO;
        });
    
}

-(AudioFileID)openAudioFile:(NSString*)filePath
{
    AudioFileID outAFID;
   
    NSURL * afUrl = [NSURL fileURLWithPath:filePath];
    OSStatus result = AudioFileOpenURL((__bridge CFURLRef)afUrl, kAudioFileReadPermission, 0, &outAFID);
    if (result != 0) NSLog(@"cannot openf file: %@",filePath);
    return outAFID;
}

-(UInt32)audioFileSize:(AudioFileID)fileDescriptor
{
    UInt64 outDataSize = 0;
    UInt32 thePropSize = sizeof(UInt64);
    OSStatus result = AudioFileGetProperty(fileDescriptor, kAudioFilePropertyAudioDataByteCount, &thePropSize, &outDataSize);
    if(result != 0) NSLog(@"cannot find file size");
    return (UInt32)outDataSize;
}

//------------for UIAlert -------------------------------------------------------------

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
    if ([alertView tag] == 15) {
        
        switch (buttonIndex) {
            case 0:
                
                dispatch_resume(self.timer);
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    [myAlert setMessage:@"继续测试"];
                    
                    [myAlert show];
                    
                });
                
                
                void* sbServices = dlopen("/System/Library/PrivateFrameworks/SpringBoardServices.framework/SpringBoardServices", RTLD_LAZY);
                
                int (*SBSLaunchApplicationWithIdentifier)(CFStringRef identifier, Boolean suspended) = dlsym(sbServices, "SBSLaunchApplicationWithIdentifier");
                
                int result = SBSLaunchApplicationWithIdentifier((__bridge CFStringRef)[self.app appPkgName], false);
                
                if (result) {
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        
                        [myAlert dismissWithClickedButtonIndex:0 animated:true];
                        
                        UIAlertView * launchAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                                               message:@"应用打开失败，请手动回到应用"
                                                                              delegate:self
                                                                     cancelButtonTitle:@"确定"
                                                                     otherButtonTitles:nil, nil ];
                        
                        [launchAlert show];
                        
                    });
                    
                }else{
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        
                        [myAlert dismissWithClickedButtonIndex:0 animated:true];
                        
                    });
                    
                            
                }
                
                break;
                
            case 1:
                
                self.flag = false;
                
                dispatch_queue_t queue = dispatch_queue_create("wetest.saveReport", DISPATCH_QUEUE_SERIAL);
                
                [self.testStartButton setTitle:@"开始测试" forState:UIControlStateNormal];
                dispatch_async(queue, ^{//异步，后台运行
                    
                    self.nLog = [[APPUtil sharedInstance] reportSave];
                    
                   
                    
                });
                
                break;
                
        }

    }
    
}

//------------for UIAlert End-------------------------------------------------------------

/**
 *  禁止横屏
 *
 *  @param application <#application description#>
 *  @param window      <#window description#>
 *
 *  @return <#return value description#>
 */
- (NSUInteger)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
    return UIInterfaceOrientationMaskPortrait;
}

@end

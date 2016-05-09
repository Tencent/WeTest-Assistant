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

#import "ViewController.h"
#include <dlfcn.h>
#include <objc/runtime.h>
#include <mach/mach_traps.h>
#include <mach/mach_init.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/sysctl.h>
#include <mach/mach.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/in.h>
#import <QuartzCore/CADisplayLink.h>
#include <ifaddrs.h>
#include <arpa/inet.h>
#include <net/if_dl.h>
#import <net/if.h>
#import <AVFoundation/AVFoundation.h>
#include <objc/runtime.h>  
#import "model/ReportData.h"
#import <QuartzCore/QuartzCore.h>
#include <GLKit/GLKit.h>
#import <UIKit/UIKit.h>
#import "Constants.h"
#import "APPUtil.h"
#import "CHKeychain.h"

#define SBSERVPATH  "/System/Library/PrivateFrameworks/SpringBoardServices.framework/SpringBoardServices"



@interface ViewController ()

@end

@interface UIApplication (private)
- (void) launchApplicationWithIdentifier: (NSString*)identifier suspended: (BOOL)suspended;
@end

@implementation ViewController

@synthesize decisionText = decisionText_;
@synthesize testStartButton = testStartButton_;
@synthesize captureButton = captureButton_;
@synthesize horizonal = horizonal_;
@synthesize appListButton = appListButton_;
@synthesize projLabel = projLabel_;

@synthesize verButton = verButton_;

@synthesize appPkgName = appPkgName_;
@synthesize appName = appName_;
@synthesize appIcon = appIcon_;
@synthesize delegate = delegate_;
@synthesize list = _list;


int count = 0 ;
int currentCount = 0;

int test = 0;
CFTimeInterval lastTime = 0;
CFTimeInterval nowTime;
long currUp = -1;
dispatch_queue_t paraQueue;
dispatch_queue_t captureQueue;
int cmd = 1;
int timeTicker = 0;
int remainTime = 15;
CADisplayLink* _link;

enum
{
    kIOSurfaceLockReadOnly  = 0x00000001,
    kIOSurfaceLockAvoidSync = 0x00000002
};

- (void) countTotalFrames{
    
    count += _link.frameInterval;
    
    CFTimeInterval timeUpdateInterval = _link.timestamp - lastTime;
    
    if (timeUpdateInterval > 1) {
        
        lastTime = _link.timestamp;
     
        ((AppDelegate *)delegate_).fps = count / timeUpdateInterval;
        
        count = 0;
        
     }
    
    
}


- (void)buttonHighlight{
    
    
    UIColor * color = [[APPUtil sharedInstance] colorWithHexColorString:@"#BABABA"];
    
    appListButton_.backgroundColor = color;
    
}


- (void)buttonNormal{
    
    UIColor * color = [[APPUtil sharedInstance] colorWithHexColorString:@"#F2F2F2"];
    
    appListButton_.backgroundColor = color;
    
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    delegate_ = (AppDelegate *)[[UIApplication sharedApplication] delegate];
  
    //     _link = [CADisplayLink displayLinkWithTarget:self selector:@selector(countTotalFrames)];
//
//     _link.frameInterval = 1;
//    
//     [_link addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
//    

    NSString * appVersion =  [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    NSString * verTitle = [NSString stringWithFormat:@"V%@",appVersion];
    [verButton_ setTitle:verTitle forState:UIControlStateNormal];
    
    
    remainTime = 20;
    
    UIActivityIndicatorView *progress= [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(125, 50, 30, 30)];
    progress.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    //progress.backgroundColor = [UIColor blackColor];
    //[loginProgress_ startAnimating];
    
    self.myAlert = [[UIAlertView alloc] initWithTitle:nil
                                         message: @"正在上传..."
                                        delegate: self
                               cancelButtonTitle: nil
                               otherButtonTitles: nil];
    
    [self.myAlert setAlertViewStyle:UIAlertViewStyleDefault];
    UIColor *testColor1= [UIColor colorWithRed:240/255.0 green:240/255.0 blue:240/255.0 alpha:0.5];
    [[self.myAlert layer] setContents:testColor1];
    
    
    [self.myAlert setValue:progress forKey:@"accessoryView"];
    
    self.myAlert.tag = 3;
    
    [progress startAnimating];
    
    
    //UIImage * tab_bk_image = [UIImage imageNamed:@"resource/menu_tab_bk.png"];
    
    UIImage * tab_bk_image = [[APPUtil sharedInstance] imageWithColor:[[APPUtil sharedInstance] colorWithHexColorString:@"#4285f4" ]];
   
    [self.navigationController.navigationBar setBackgroundImage:tab_bk_image forBarMetrics:UIBarMetricsDefault];
    
  
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, 0 , 300, 44)];
    titleLabel.backgroundColor = [UIColor clearColor];  //设置Label背景透明
    titleLabel.font = [UIFont boldSystemFontOfSize:20];  //设置文本字体与大小
    titleLabel.textColor = [UIColor colorWithRed:(255.0/255.0) green:(255.0 / 255.0) blue:(255.0 / 255.0) alpha:1];  //设置文本颜色
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = @"前台性能";  //设置标题
    self.navigationItem.titleView = titleLabel;

    
    [appListButton_ addTarget:self action:@selector(buttonHighlight) forControlEvents:UIControlEventTouchDown];
    [appListButton_ addTarget:self action:@selector(buttonNormal) forControlEvents:UIControlEventTouchUpInside];
    
    [appListButton_ addTarget:self action:@selector(buttonNormal) forControlEvents:UIControlEventTouchUpOutside];
    
    UIColor * bakColor = [[APPUtil sharedInstance] colorWithHexColorString:@"#F2F2F2"];
    appListButton_.backgroundColor = bakColor;
    
    projLabel_.backgroundColor = bakColor;
    
    testStartButton_.layer.cornerRadius = 10.0;//设置圆角
    
    self.view.backgroundColor = [UIColor colorWithWhite:1.0 alpha:1.0];
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    delegate.testStartButton = self.testStartButton;
    
    //delegate.testStartView = self;
   
    
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
  
    
    self.succAlert = [[UIAlertView alloc] initWithTitle:@"超时提醒"
                                                message:@"已超过2小时（最多4小时）"
                                               delegate:self
                                      cancelButtonTitle:@"结束"
                                      otherButtonTitles:@"继续测试", nil ];
    
    self.succAlert.tag = 15;
    
  
    
    self.tipAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                message:@""
                                               delegate:self
                                      cancelButtonTitle:@"确定"
                                      otherButtonTitles:nil, nil ];
    
    self.tipAlert.tag = 4;
    
    
    paraQueue = dispatch_queue_create("com.WeTest.paraCapture2", DISPATCH_QUEUE_SERIAL);
    
    if(!delegate.timer){
    
         delegate.timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, paraQueue);
    }
    
   
    
    dispatch_source_set_timer(delegate.timer, DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC, 0);
    
    dispatch_source_set_event_handler(delegate.timer, ^{
        
        [[APPUtil sharedInstance] paraCapture];
        
        timeTicker ++ ;
        
        
        if (timeTicker % 60 == 0) {
            
            [[APPUtil sharedInstance] reportSave];
            
            
        }
        
        
        if (timeTicker == 7200) {
            
            remainTime = 15;
            
        }
        
        if (remainTime <=15 && remainTime >=0) {
            
            dispatch_sync(dispatch_get_main_queue(), ^{
                
                NSString * message = [NSString stringWithFormat:@"%d秒后结束",remainTime];
                
                [self.succAlert setMessage:message];
                
                [self.succAlert message];
                
                [self.succAlert show];
                
            });
            
        }
        
        if (remainTime <= 15) {
            
            remainTime--;
            
        }
        
        
        
        if (remainTime <= 0 || timeTicker == 14400) {
            
            if (remainTime <= 15 ) {
                
                
                [self.succAlert dismissWithClickedButtonIndex:0 animated:false];
                
            }
            
            ((AppDelegate *)delegate_).flag = false;
            
            dispatch_suspend(((AppDelegate *)delegate_).timer);
            
            dispatch_sync(dispatch_get_main_queue(), ^{
                
                [testStartButton_ setTitle:@"开始测试" forState:UIControlStateNormal];
            });
            
            
            
            dispatch_queue_t sQueue = dispatch_queue_create("wetest.saveFileInTimer", DISPATCH_QUEUE_SERIAL);
            
            dispatch_async(sQueue, ^{//异步，后台运行
                
                [[APPUtil sharedInstance] reportSave];
                
            });
            
        }
        
        
    });

}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.list = nil;
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *TableSampleIdentifier = @"TableSampleIdentifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:
                             TableSampleIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]
                initWithStyle:UITableViewCellStyleDefault
                reuseIdentifier:TableSampleIdentifier];
    }
    
    NSUInteger row = [indexPath row];
    cell.textLabel.text = [self.list objectAtIndex:row];
    return cell;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return [self.list count];
}

-(IBAction)buttonTestStartPressed:(id)sender{
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    
    if ( [testStartButton_.titleLabel.text isEqualToString: @"开始测试"]) {
        
        APPInfo * info = [[APPUtil sharedInstance] getApplistInfoByPkgName:[delegate.app appPkgName]];
        
        if ([info icon]) {
            
            delegate.app = info;
            
            if ([((AppDelegate *)delegate_).app appPkgName]) {
                
                appPkgName_.text = [((AppDelegate *)delegate_).app appPkgName];
                appName_.text = [((AppDelegate *)delegate_).app appName];
                [appIcon_ setImage:[((AppDelegate *)delegate_).app icon]];
                
            }
        }
        
        //[self.myAlert setMessage:[NSString stringWithFormat:@"测试开始 %@ %@" , [delegate_.app appPkgName] ,[delegate_.app version] ]];
        
        [self.myAlert setMessage:@"测试开始"];
        
        [self.myAlert show];
        
        dispatch_queue_t queue = dispatch_queue_create("wetest.testStart", DISPATCH_QUEUE_SERIAL);
        
        [delegate.reportList removeAllObjects];
        
        dispatch_async(queue, ^{
                       
            //创建测试报告文件
            NSDate *datenow = [NSDate date];
            NSTimeZone *zone = [NSTimeZone systemTimeZone];
            NSInteger interval = [zone secondsFromGMTForDate:datenow];
            NSDate *localeDate = [datenow  dateByAddingTimeInterval: interval];
            
            NSString *timeSp = [NSString stringWithFormat:@"%ld", (long)[localeDate timeIntervalSince1970]];
            
            NSString * fileName = [NSString stringWithFormat:@"wt%@",timeSp];
            
            delegate.fileName = fileName;
            
            delegate.timeStart = [[NSProcessInfo processInfo] systemUptime];
            
            
            [ [APPUtil sharedInstance ] setStartFlow ];
        
            
            //拉起app
            //NSInteger ret =  [[UIApplication sharedApplication] launchApplicationWithIdentifier:[delegate.app appPkgName] suspended:NO];
            
            
            void* sbServices = dlopen("/System/Library/PrivateFrameworks/SpringBoardServices.framework/SpringBoardServices", RTLD_LAZY);
            
            int (*SBSLaunchApplicationWithIdentifier)(CFStringRef identifier, Boolean suspended) = dlsym(sbServices, "SBSLaunchApplicationWithIdentifier");
            
            int result = SBSLaunchApplicationWithIdentifier((__bridge CFStringRef)[delegate.app appPkgName], false);
            
            if (result) {
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    [self.myAlert dismissWithClickedButtonIndex:0 animated:true];
                    
                    UIAlertView * launchAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                                           message:@"请选择正确应用"
                                                                          delegate:self
                                                                 cancelButtonTitle:@"确定"
                                                                 otherButtonTitles:nil, nil ];
                    
                    [launchAlert show];
                    
                });
                
                
                
            }else{
                
                
                delegate.flag = true;
                
                timeTicker = 0;
                
                remainTime = 20;
                
                dispatch_resume(delegate.timer);
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self.myAlert dismissWithClickedButtonIndex:0 animated:true];
                    
                    [testStartButton_ setTitle:@"停止测试" forState:UIControlStateNormal];
                    
                    
                });
                
            }
            
            dlclose(sbServices);
            
        });
        
    }else{
    
        delegate.flag = false;

        dispatch_suspend(delegate.timer);
        
        [testStartButton_ setTitle:@"开始测试" forState:UIControlStateNormal];
        
        dispatch_queue_t queue = dispatch_queue_create("wetest.saveFile", DISPATCH_QUEUE_SERIAL);
        
            dispatch_async(queue, ^{//异步，后台运行
                
                delegate.nLog = [[APPUtil sharedInstance] reportSave];
                
            });
        
        
        
    }
    
}

- (void *) init_backup {

    return nil;
}

-(IBAction)buttonTestPressed:(id)sender{

    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSString *FileName=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"test"];
    
    
    
    NSString* date;
    NSDateFormatter* formatter = [[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"YYYY-MM-dd hh:mm:ss"];
    date = [formatter stringFromDate:[NSDate date]];
    
   
    
    NSData* testData = [date dataUsingEncoding:NSUTF8StringEncoding];
    
    [testData writeToFile:FileName atomically:YES];

    
}



-(IBAction)buttonLoginPressed:(id)sender{
    
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView{
    return 1;
}

- (void) viewWillAppear:(BOOL)animated
{
    self.tabBarController.tabBar.hidden = false;
    
    [testStartButton_ setBackgroundColor:[[APPUtil sharedInstance] colorWithHexColorString:@"#4285f4" ]];
    
    delegate_ = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    if ([((AppDelegate *)delegate_).app appPkgName]) {
        
        appPkgName_.text = [((AppDelegate *)delegate_).app appPkgName];
        appName_.text = [((AppDelegate *)delegate_).app appName];
        [appIcon_ setImage:[((AppDelegate *)delegate_).app icon]];

    }

}

-(IBAction)buttonLogoutPressed:(id)sender{

    [[APPUtil sharedInstance] logout];
   
    [self.tabBarController.navigationController popToRootViewControllerAnimated:TRUE];
    
}


//------------for UIAlert -------------------------------------------------------------
//根据被点击按钮的索引处理点击事件
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (alertView.tag) {
            
        case 15:
            if (buttonIndex == 0) {
                
                ((AppDelegate *)delegate_).flag = false;
                               dispatch_suspend(((AppDelegate *)delegate_).timer);
                
                [testStartButton_ setTitle:@"开始测试" forState:UIControlStateNormal];
                
                dispatch_queue_t queue = dispatch_queue_create("wetest.saveFileInAlert", DISPATCH_QUEUE_SERIAL);
                
                dispatch_async(queue, ^{//异步，后台运行
                    
                    ((AppDelegate *)delegate_).nLog = [[APPUtil sharedInstance] reportSave];
                    
                    
                });
            }else{
                
                remainTime = 20;
                
               
            }

            break;
            
        default:
            break;
            
    }
    
}

@end

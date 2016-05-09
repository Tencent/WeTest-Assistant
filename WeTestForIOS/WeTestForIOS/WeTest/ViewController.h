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
#include <Foundation/Foundation.h>
#import "model/APPInfo.h"
#import "AppDelegate.h"
#import "model/LogInfo.h"

/**
 *  前台性能展示页面
 */
@interface ViewController :  UIViewController<UITableViewDelegate, UITableViewDataSource,UIAlertViewDelegate >  {
    
    UILabel *decisionText;
    UIButton *testStartButton;
    UIButton *captureButton;
    
}

extern NSConstantString * kNStatSrcKeyPID;
extern NSConstantString * kNStatSrcKeyTxBytes;
extern NSConstantString * kNStatSrcKeyRxBytes;

typedef void * IOMobileFramebufferConnection;
typedef void * IOSurfaceRef;
typedef void * IOSurfaceAcceleratorRef;

struct __NStatSource{
    
};

struct __NStatManager{
    
};

@property (retain,nonatomic) IBOutlet UILabel *decisionText;
@property (retain,nonatomic) IBOutlet UILabel *projLabel;
@property (retain,nonatomic) IBOutlet UIButton *testStartButton;
@property (retain,nonatomic) IBOutlet UIButton *captureButton;
@property (retain,nonatomic) IBOutlet UIButton *appListButton;
@property (retain,nonatomic) IBOutlet UIButton *verButton;

@property (retain,nonatomic) IBOutlet UILabel *appPkgName;
@property (retain,nonatomic) IBOutlet UILabel *appName;
@property (strong, nonatomic) IBOutlet UIImageView * appIcon;

@property (strong, nonatomic) IBOutlet UIView * horizonal;

@property (nonatomic) dispatch_source_t captureTimer;
@property (nonatomic) struct __NStatManager * manager ;

//@property (nonatomic) AppDelegate *   delegate;
@property (weak, nonatomic) id <UIApplicationDelegate>   delegate;

@property (retain,nonatomic) IBOutlet UIAlertView * succAlert;

@property (retain,nonatomic) IBOutlet UIAlertView * myAlert;

@property (retain,nonatomic) IBOutlet UIAlertView * tipAlert;

@property (strong,nonatomic) NSArray *list;

/**
 *  开始测试
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonTestStartPressed:(id)sender;
-(IBAction)buttonTestPressed:(id)sender;
-(IBAction)buttonLoginPressed:(id)sender;
-(IBAction)buttonRefresh:(id)sender;

/**
 *  注销登陆
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonLogoutPressed:(id)sender;

- (void)buttonHighlight;
- (void)buttonNormal;

@end

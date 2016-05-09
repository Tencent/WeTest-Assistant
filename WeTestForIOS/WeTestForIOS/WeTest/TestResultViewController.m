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

#import "TestResultViewController.h"
#import "AppDelegate.h"
#import "model/APPUtil.h"
#import "CHKeychain.h"
#import "Constants.h"
#import "DataDetailViewController.h"

/**
 *  用于显示单条测试记录概况
 */
@interface TestResultViewController ()

@end


@implementation TestResultViewController

@synthesize testResultText = testResultText_;

@synthesize resTimeLabel = resTimeLabel_;
@synthesize resNameLabel = resNameLabel_;
@synthesize resProjLabel = resProjLabel_;
@synthesize resTesterLabel = resTesterLabel_;
@synthesize resTitleLabel = resTitleLabel_;
@synthesize btnDatatDetail = btnDatatDetail_;

@synthesize resTimeText = resTimeText_;
@synthesize resNameText = resNameText_;
@synthesize resTitleText = resTitleText_;

float orginTextY = 0;

- (void)viewDidLoad {
    
    [super viewDidLoad];
    
    resTimeText_.text = [NSString stringWithFormat:@"%@秒",[self.log testTime]];
    resNameText_.text = [self.log appName];
    
    [[APPUtil sharedInstance] readRecordContent:[self.log filePath]];
    
    self.delConfirmAlert =  [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                       message:@"确定删除?"
                                                      delegate:self
                                             cancelButtonTitle:@"取消"
                                             otherButtonTitles:@"确定",nil];
    
    self.delConfirmAlert.tag = 15;
    
    
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
  
}

- (void)viewWillAppear:(BOOL)animated{
    
    self.tabBarController.tabBar.hidden = true;
    
    NSFileManager * fileManager = [NSFileManager defaultManager];
    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSString *filePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[self.log filePath] ];
    
    if ([fileManager fileExistsAtPath:filePath isDirectory:false]) {
        
        [btnDatatDetail_ setEnabled:TRUE];
        
        
        [btnDatatDetail_ setTitle:@"查看数据" forState:UIControlStateNormal];
        
         btnDatatDetail_.layer.cornerRadius = 10.0f;
        
         [btnDatatDetail_ setBackgroundColor:[[APPUtil sharedInstance] colorWithHexColorString:@"#4285f4" ]];
        
    }else{
        
        [btnDatatDetail_ setTitle:@"无数据" forState:UIControlStateNormal];
        [btnDatatDetail_ setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
        
        [btnDatatDetail_ setEnabled:false];
        
        btnDatatDetail_.layer.cornerRadius = 10.0f;
        btnDatatDetail_.layer.borderWidth = 0.0f;
        
        [btnDatatDetail_ setBackgroundColor:[[APPUtil sharedInstance] colorWithHexColorString:@"#0f4285f4" ]];
        
    }
    
    

    
}

-(IBAction)buttonDelLogPressed:(id)sender{

    
    [self.delConfirmAlert show];
    
}

-(IBAction)buttonDataDetailPressed:(id)sender{
    
    LogInfo * logSender = self.log;
    
    [self performSegueWithIdentifier:@"DataDetail" sender:logSender];
}

//------------for UIAlert -------------------------------------------------------------
//根据被点击按钮的索引处理点击事件
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (alertView.tag) {
        case 15:
            
            if (buttonIndex == 1) {
                
                [[APPUtil sharedInstance] delLog:[self.log filePath]];
                
                [self.navigationController popToRootViewControllerAnimated:TRUE];
              
            }
            
            break;
            
        default:
            
            break;
            
    }
    
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    if ([segue.identifier isEqualToString: @"DataDetail"]) {
        
        DataDetailViewController * dataDetail = segue.destinationViewController;
        
        dataDetail.log = sender;
        
        
    }
}

@end

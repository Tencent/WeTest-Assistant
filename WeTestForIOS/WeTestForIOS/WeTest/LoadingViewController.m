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

#import "LoadingViewController.h"
#import "AppDelegate.h"
#import "APPUtil.h"

@interface LoadingViewController ()

@end

@implementation LoadingViewController

@synthesize logo = _logo;
@synthesize logo_title = _logo_title;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[UIApplication
      sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
    // Do any additional setup after loading the view.
   
    UIImage * image = [UIImage imageNamed:@"resource/mainbk.png"];
    
    self.view.backgroundColor = [UIColor colorWithPatternImage:image];
   
     UIImage * image_logo = [UIImage imageNamed:@"resource/about_icon.png"];
     _logo.image = image_logo;
    
    UIImage * image_title = [UIImage imageNamed:@"resource/about_logo.png"];
    _logo_title.image = image_title;
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}






- (void) viewWillAppear:(BOOL)animated
{
    
    dispatch_queue_t queue = dispatch_queue_create("com.tencent.wetest.Login", DISPATCH_QUEUE_SERIAL);
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        
        dispatch_async(
                       dispatch_get_main_queue(),
                       ^{
                           
                           [self performSegueWithIdentifier:@"loadingLoginSuccess" sender:nil];
                           
                       });
        
    });
    
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

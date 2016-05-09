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

/**
 *  测试结果显示页面
 */
#import <UIKit/UIKit.h>
#import "model/LogInfo.h"
@interface TestResultViewController : UIViewController<UIAlertViewDelegate , UITextFieldDelegate>{

    UITextView *testResultText;
}

@property (retain,nonatomic) IBOutlet UITextView *testResultText;

@property (retain,nonatomic) IBOutlet UILabel * resTimeLabel;
@property (retain,nonatomic) IBOutlet UILabel * resNameLabel;
@property (retain,nonatomic) IBOutlet UILabel * resProjLabel;
@property (retain,nonatomic) IBOutlet UILabel * resTesterLabel;
@property (retain,nonatomic) IBOutlet UILabel * resTitleLabel;

@property (retain,nonatomic) IBOutlet UITextView * resTimeText;
@property (retain,nonatomic) IBOutlet UITextView * resNameText;
@property (retain,nonatomic) IBOutlet UITextView * resTitleText;

@property (retain,nonatomic) IBOutlet UIButton *btnDatatDetail;

@property (retain,nonatomic) IBOutlet UIAlertView * delConfirmAlert;
@property (strong,nonatomic) LogInfo *log;

/**
 *  上传测试记录
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonUploadPressed:(id)sender;
/**
 *  删除测试记录
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonDelLogPressed:(id)sender;
-(IBAction)buttonDataDetailPressed:(id)sender;
-(IBAction)buttonRefresh:(id)sender;

@end

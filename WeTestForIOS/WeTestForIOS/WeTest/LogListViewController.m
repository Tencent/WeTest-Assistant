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
#import "LogListViewController.h"
#import "TestResultViewController.h"
#import "AppDelegate.h"
#import "model/LogInfo.h"
#import "model/APPUtil.h"
#import "Constants.h"
#import "model/LogTableViewCell.h"
#import "APPUtil.h"
#import "CHKeychain.h"

@interface LogListViewController ()

@end
/**
 *  用于显示测试记录
 */
@implementation LogListViewController

@synthesize searchBar = searchBar_;

@synthesize today_list = _today_list;
@synthesize yesterday_list = _yesterday_list;
@synthesize other_list = _other_list;

UITapGestureRecognizer * tapGestrue;

- (void)viewDidLoad {
    [super viewDidLoad];
    
//    UIImage * tab_bk_image = [UIImage imageNamed:@"resource/menu_tab_bk.png"];
//    
//    [self.navigationController.navigationBar setBackgroundImage:tab_bk_image forBarMetrics:UIBarMetricsDefault];

    UIImage * tab_bk_image = [[APPUtil sharedInstance] imageWithColor:[[APPUtil sharedInstance] colorWithHexColorString:@"#4285f4" ]];
    
    [self.navigationController.navigationBar setBackgroundImage:tab_bk_image forBarMetrics:UIBarMetricsDefault];
    
    UILabel *titleLabel = [[UILabel alloc]initWithFrame:CGRectMake(50, 50 , 300, 44)];
    titleLabel.backgroundColor = [UIColor clearColor];  //设置Label背景透明
    titleLabel.font = [UIFont boldSystemFontOfSize:20];  //设置文本字体与大小
    titleLabel.textColor = [UIColor colorWithRed:(255.0/255.0) green:(255.0 / 255.0) blue:(255.0 / 255.0) alpha:1];  //设置文本颜色
    titleLabel.textAlignment = NSTextAlignmentCenter;
    titleLabel.text = @"测试记录";  //设置标题
    self.navigationItem.titleView = titleLabel;
    
    /*UISearchBar *oneSearchBar = [[UISearchBar alloc] init];
    oneSearchBar.frame = CGRectMake(0, 0, 320, 44); // 设置位置和大小
    oneSearchBar.keyboardType = UIKeyboardTypeEmailAddress; // 设置弹出键盘的类型
    oneSearchBar.barStyle = UIBarStyleDefault; // 设置UISearchBar的样式
    oneSearchBar.tintColor = [UIColor colorWithWhite:1 alpha:1]; // 设置UISearchBar的颜色 使用clearColor就是去掉背景
    
    oneSearchBar.placeholder = @"应用名称"; // 设置提示文字
    oneSearchBar.text = @""; // 设置默认的文字
    //oneSearchBar.prompt = @"提示信息"; // 设置提示
    oneSearchBar.delegate = self; // 设置代理
    
    oneSearchBar.showsCancelButton = NO; // 设置时候显示关闭按钮
    
    for(UIView *view in  [[[oneSearchBar subviews] objectAtIndex:0] subviews]) {
        
        if([view isKindOfClass:[NSClassFromString(@"UINavigationButton") class]]) {
            UIButton * cancel =(UIButton *)view;
            [cancel setTitle:@"全部" forState:UIControlStateNormal];
            [cancel setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [cancel  setTintColor:[UIColor whiteColor]];
            [cancel.titleLabel setTextColor:[UIColor whiteColor]];
        }
    }*/
  
    // oneSearchBar.showsScopeBar = YES; // 设置显示范围框
    // oneSearchBar.showsSearchResultsButton = YES; // 设置显示搜索结果
    // oneSearchBar.showsBookmarkButton = YES; // 设置显示书签按钮
    
    //self.tableView.tableHeaderView = oneSearchBar;
    
    UILabel * labelText = [[UILabel alloc] initWithFrame:CGRectMake(30, -10, 90, 40)];
    
    [labelText setText:@"正在登录..."];
    
    // Create and add the activity indicator
    
    //[loginProgress_ addSubview:labelText];
    
    UIActivityIndicatorView *progress= [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(125, 50, 30, 30)];
    
    progress.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    
    //progress.backgroundColor = [UIColor blackColor];
    //[loginProgress_ startAnimating];
    
    
    
    [progress startAnimating];
    
    
    
    //初始化AlertView
    self.tipAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                    message:@"message"
                                                   delegate:self
                                          cancelButtonTitle:@"取消"
                                          otherButtonTitles:@"确定",nil];
   
    self.succAlert = [[UIAlertView alloc] initWithTitle:@"温馨提示"
                                                         message:@"上传成功"
                                                        delegate:self
                                               cancelButtonTitle:@"确定"
                                               otherButtonTitles:nil];
    
    tapGestrue = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissKeyBoard)];

}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    
    self.logList = nil;
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *TableSampleIdentifier = @"LogCell";
    
    LogTableViewCell * cell = (LogTableViewCell *)[tableView dequeueReusableCellWithIdentifier:
                             TableSampleIdentifier];
    if (cell == nil) {
        cell = [[LogTableViewCell alloc]
                initWithStyle:UITableViewCellStyleDefault
                reuseIdentifier:TableSampleIdentifier];
    }
    
    NSUInteger row = [indexPath row];
    
    NSFileManager * fileManager = [NSFileManager defaultManager];

    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSString *filePath = nil;
   
    APPInfo * cellInfo = nil;
    
    switch (indexPath.section) {
        case 0:
            
            if ([_today_list count] > row) {
                filePath = [[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[[_today_list objectAtIndex:row] filePath] ];
                
                cell.cellName.text = [[_today_list objectAtIndex:row] appName] ;
                cell.cellTime.text = [[_today_list objectAtIndex:row] saveTime];
              
                cellInfo = [[APPUtil sharedInstance] getApplistInfoByPkgName:[[_today_list objectAtIndex:row] appPkgName]];
                
                cell.cellImage.image = [cellInfo icon];
                
            }
           
            
            break;
            
        case 1:
            
            if ([_yesterday_list count] > row) {

                filePath = [[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[[_yesterday_list objectAtIndex:row] filePath] ];
                
                cell.cellName.text = [[_yesterday_list objectAtIndex:row] appName] ;
                cell.cellTime.text = [[_yesterday_list objectAtIndex:row] saveTime];

                cellInfo = [[APPUtil sharedInstance] getApplistInfoByPkgName:[[_yesterday_list objectAtIndex:row] appPkgName]];
                
                cell.cellImage.image = [cellInfo icon];

                
            }
          
            
            break;
            
        case 2:
            
            if ([_other_list count] > row) {

                filePath = [[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[[_other_list objectAtIndex:row] filePath] ];
                
                cell.cellName.text = [[_other_list objectAtIndex:row] appName] ;
                cell.cellTime.text = [[_other_list objectAtIndex:row] saveTime];
                
                cellInfo = [[APPUtil sharedInstance] getApplistInfoByPkgName:[[_other_list objectAtIndex:row] appPkgName]];
                
                cell.cellImage.image = [cellInfo icon];

            }
            break;
            
        default:
            
            cell.cellName.text = [NSString stringWithFormat:@"Unknown : %d" , indexPath.section];
            
            break;
    }
   
    UIColor * baseColor = [[APPUtil sharedInstance] colorWithHexColorString:@"#006699"];
    UIColor * grayColor = [[APPUtil sharedInstance] colorWithHexColorString:@"#8E8E8E"];
    
   
    if ([fileManager fileExistsAtPath:filePath isDirectory:false]) {
        
        cell.cellName.textColor = baseColor;
        cell.cellTime.textColor = baseColor;
        
    }else{
        
        cell.cellName.textColor = grayColor;
        cell.cellTime.textColor = grayColor;

    }
    
    //cell.textLabel.text = [self.logList objectAtIndex:row] ;
    //cell.imageView.image = [(APPInfo*)[self.list objectAtIndex:row] icon];
    //cell.textLabel.text = [self.list objectAtIndex:row];
    
    return cell;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    
    switch (section) {
            
        case 0:
            
            return  [_today_list count];
            
            break;
            
            
        case 1:
            
            return  [_yesterday_list count];
            
            break;
            

            
        case 2:
            
            return  [_other_list count];
            
            break;
            
        default:
            
            return 0;  
            
            break;  
            
    }
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = [indexPath row];

    LogInfo * sender = [self.logList objectAtIndex:row];
    
    [self performSegueWithIdentifier:@"LogDetail" sender:sender];

}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    if ([segue.identifier isEqualToString: @"LogDetail"]) {
        
        TestResultViewController * logDetail = segue.destinationViewController;
        
        logDetail.log = sender;
        
    }
}

- (void) viewWillAppear:(BOOL)animated
{

    self.tabBarController.tabBar.hidden = false;
    
    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];

    NSString *FileName=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
    
    NSData *data=[NSData dataWithContentsOfFile:FileName options:0 error:NULL];
    
    NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
    
    NSArray * strLogList = [result componentsSeparatedByString:@"\n"];
    
    NSMutableArray * arr = [[NSMutableArray alloc] init];
    
    NSMutableArray * todayList = [[NSMutableArray alloc] init];
    
    NSMutableArray * yesterdayList = [[NSMutableArray alloc] init];
    
    NSMutableArray * otherDayList = [[NSMutableArray alloc] init];
    
    for (int i = 0 ; i < strLogList.count; i++) {
        
        NSArray * strLogContent = [strLogList[i] componentsSeparatedByString:@"|"];
        
        //record file : 文件名 保存时间点 应用名称 测试人员 测试时长 开始时间     结束时间 应用包名 应用版本
        
        if (!(strLogContent.count<=3)) {
            
            LogInfo * log = [[LogInfo alloc] init];
            [log setFilePath:strLogContent[0]];
            [log setSaveTime:strLogContent[1]];
            [log setAppName:strLogContent[2]];
            [log setTestTime:strLogContent[3]];
            [log setTimeStart:strLogContent[4]];
            [log setTimeEnd:strLogContent[5]];
            [log setAppPkgName:strLogContent[6]];
            [log setAppVersion:strLogContent[7]];
            
            [arr addObject:log];
            
            switch ([[APPUtil sharedInstance] compareDate:[log saveTime]] ) {
                    
                case 0:
                    
                    [todayList addObject:log];
                    break;
                
                case -1:
                    
                    [yesterdayList addObject:log];

                    break;
                    
                case 1:
                    
                    [otherDayList addObject:log];

                    break;
            }
          
            
        }
        
    }
   
    _today_list = [NSMutableArray arrayWithArray:[[todayList reverseObjectEnumerator] allObjects]];
    _yesterday_list = [NSMutableArray arrayWithArray:[[yesterdayList reverseObjectEnumerator] allObjects]];
    _other_list = [NSMutableArray arrayWithArray:[[otherDayList reverseObjectEnumerator] allObjects]];;
    
    
    self.baseList = arr;
    
    //显示最近的测试
    self.logList = [NSMutableArray arrayWithArray:[[self.baseList reverseObjectEnumerator] allObjects]];
    
    
    titleArray = [[NSMutableArray alloc] initWithObjects:@"    今天", @"    昨天", @"    最近" , nil];
    
    [self.tableView reloadData];
    
}


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation

{

    // Return YES for supported orientations
    
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
    
}  

//指定有多少个分区(Section)，默认为1

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    

    return [titleArray count];//返回标题数组中元素的个数来确定分区的个数
    
}

//每个section显示的标题

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    

    
    switch (section) {
            
        case 0:
            
            return [titleArray objectAtIndex:section];
        case 1:
            
            return [titleArray objectAtIndex:section];
            
        case 2:
            
            return [titleArray objectAtIndex:section];
            
        default:
            
            return @"Unknown";  
            
    }  
    
    
    
}  


-(IBAction)buttonLogClearPressed:(id)sender{
    
    [self.tipAlert setMessage:@"是否清空?"];
    
    [self.tipAlert show];
    
}

-(IBAction)buttonLogOneKeyCommitPressed:(id)sender{
    
   
    [self.tipAlert setMessage:@"是否上传?"];
    
    [self.tipAlert show];
    
}


//取消按钮被点击的时候

-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    
    [searchBar resignFirstResponder];
    
    self.logList = [NSMutableArray arrayWithArray:[[self.baseList reverseObjectEnumerator] allObjects]];
    
    [self.tableView reloadData];
    
}

//搜索按钮被点击的时候


-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    
    
    
    NSMutableArray * arr = [[NSMutableArray alloc] init];
                            
    for(int i = 0 ;i < self.baseList.count ; i++){
        
        
        if([[self.baseList[i] appName] rangeOfString:searchBar.text options:NSCaseInsensitiveSearch].location != NSNotFound){
        
            [arr addObject:self.baseList[i]];
            
        }
    }
    
    self.logList = [NSMutableArray arrayWithArray:[[arr reverseObjectEnumerator] allObjects]];

    [self.tableView reloadData];
    
    [searchBar resignFirstResponder];
    
    
}



-(void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{

     [searchBar_ resignFirstResponder];
    
}

- (BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar{

    [self.tableView addGestureRecognizer:tapGestrue];

    
    return  YES;
    
}
//搜索内容改变的时候，在这个方法里面实现实时显示结果

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText{

    
    if ([searchBar.text isEqualToString:@""]) {
        
        self.logList = [NSMutableArray arrayWithArray:[[self.baseList reverseObjectEnumerator] allObjects]];
    }
    else{
    
        
        NSMutableArray * arr = [[NSMutableArray alloc] init];
        
        for(int i = 0 ;i < self.baseList.count ; i++){
            
            
            if([[self.baseList[i] appName] rangeOfString:searchBar.text options:NSCaseInsensitiveSearch].location != NSNotFound){
                
                [arr addObject:self.baseList[i]];
                
            }
        }
        
        self.logList = [NSMutableArray arrayWithArray:[[arr reverseObjectEnumerator] allObjects]];
        
    }
  
    
    [self.tableView reloadData];
    
}

//------------for UIAlert-------------------------------------------------------------
//根据被点击按钮的索引处理点击事件
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
 
    if ([[alertView message] isEqualToString:@"是否清空?"] && buttonIndex == 1) {
        
        dispatch_queue_t queue = dispatch_queue_create("wetest.logClear", DISPATCH_QUEUE_SERIAL);
        
        dispatch_async(queue, ^{
            
            [[APPUtil sharedInstance] logClear];
        
            dispatch_async(dispatch_get_main_queue(), ^{
                
                
                NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
                AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
                NSString *FileName=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
                
                NSData *data=[NSData dataWithContentsOfFile:FileName options:0 error:NULL];
                
                
                NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
                
                NSArray * strLogList = [result componentsSeparatedByString:@"\n"];
                
                NSMutableArray * arr = [[NSMutableArray alloc] init];
                
                NSMutableArray * todayList = [[NSMutableArray alloc] init];
                
                NSMutableArray * yesterdayList = [[NSMutableArray alloc] init];
                
                NSMutableArray * otherDayList = [[NSMutableArray alloc] init];
                
                for (int i = 0 ; i < strLogList.count; i++) {
                    
                    NSArray * strLogContent = [strLogList[i] componentsSeparatedByString:@"|"];
                    
                    if (!(strLogContent.count<=3)) {
                        
                        LogInfo * log = [[LogInfo alloc] init];
                        [log setFilePath:strLogContent[0]];
                        [log setSaveTime:strLogContent[1]];
                        [log setAppName:strLogContent[2]];
                        [log setTestTime:strLogContent[3]];
                        [log setTimeStart:strLogContent[4]];
                        [log setTimeEnd:strLogContent[5]];
                        [log setAppPkgName:strLogContent[6]];
                        [log setAppVersion:strLogContent[7]];
                        
                        [arr addObject:log];
                        
                        
                        switch ([[APPUtil sharedInstance] compareDate:[log saveTime]] ) {
                                
                            case 0:
                                
                                [todayList addObject:log];
                                break;
                                
                            case -1:
                                
                                [yesterdayList addObject:log];
                                
                                break;
                                
                            case 1:
                                
                                [otherDayList addObject:log];
                                
                                break;
                        }

                        
                    }
                    
                }
                
                _today_list = todayList;
                _yesterday_list = yesterdayList;
                _other_list = otherDayList;

               
                self.baseList = arr;
                
                self.logList =  [NSMutableArray arrayWithArray:[[self.baseList reverseObjectEnumerator] allObjects]];
                
                [self.tableView reloadData];
                
                
            });
            
        });
        
    }
    
}

//------------for UIAlert End-------------------------------------------------------------


-(void)dismissKeyBoard{
    [searchBar_ resignFirstResponder];
    
    [[UIApplication sharedApplication].keyWindow endEditing:YES];
    
    [self.tableView removeGestureRecognizer:tapGestrue];

}



//加入如下代码
-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if ([tableView respondsToSelector:@selector(setSeparatorInset:)]) {
        [tableView setSeparatorInset:UIEdgeInsetsZero];
    }
    
    if ([tableView respondsToSelector:@selector(setLayoutMargins:)]) {
        [tableView setLayoutMargins:UIEdgeInsetsZero];
    }
    
    if ([cell respondsToSelector:@selector(setLayoutMargins:)]) {
        [cell setLayoutMargins:UIEdgeInsetsZero];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{

    return 30;
}

@end

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
/**
 *  测试记录显示页面
 */
@interface LogListViewController : UITableViewController<UITableViewDelegate, UITableViewDataSource,UISearchBarDelegate,UIAlertViewDelegate,UIScrollViewDelegate>{

    //UITableView *DataTable;
    
//    NSMutableArray *dataArray1;
//    
//    NSMutableArray *dataArray2;
    
    NSMutableArray *titleArray;
    
}

@property (strong,nonatomic) NSMutableArray * logList;
@property (strong,nonatomic) NSMutableArray * baseList;
@property (strong,nonatomic) NSMutableArray * searchList;

@property (strong,nonatomic) NSMutableArray * title_list;
@property (strong,nonatomic) NSMutableArray * today_list;
@property (strong,nonatomic) NSMutableArray * yesterday_list;
@property (strong,nonatomic) NSMutableArray * other_list;

@property (retain,nonatomic) IBOutlet UISearchBar * searchBar;
@property (retain,nonatomic) IBOutlet UIAlertView * tipAlert;
@property (retain,nonatomic) IBOutlet UIAlertView * succAlert;

/**
 *  清空测试记录
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonLogClearPressed:(id)sender;

/**
 *  测试记录一键上传
 *
 *  @param sender <#sender description#>
 */
-(IBAction)buttonLogOneKeyCommitPressed:(id)sender;

//取消按钮被点击的时候

-(void)searchBarCancelButtonClicked:(UISearchBar *)searchBar;

//搜索按钮被点击的时候

-(void)searchBarSearchButtonClicked:(UISearchBar *)searchBar;

//搜索内容改变的时候，在这个方法里面实现实时显示结果

-(void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText;

-(IBAction)buttonRefresh:(id)sender;

@end

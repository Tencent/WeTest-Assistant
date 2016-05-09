//
//  APPListViewController.m
//  IStudy
//
//  Created by caorobin on 15-3-11.
//  Copyright (c) 2015年 caorobin. All rights reserved.
//

#import "APPListViewController.h"
#import "wetest/model/APPInfo.h"
#import "ViewController.h"
#import "APPUtil.h"

@interface APPListViewController ()

@end
/**
 *  应用列表显示界面
 */
@implementation APPListViewController

@synthesize list = _list;

/**
 *  获取应用信息
 */
- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.list = [[APPUtil sharedInstance ] getApplistInfo];
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    
    self.list = nil;
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

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
    cell.textLabel.text = [(APPInfo*)[self.list objectAtIndex:row] appName];
    //cell.textLabel.text = [(APPInfo*)[self.list objectAtIndex:row] executable];
    
    
    UIImage
    *icon = [(APPInfo*)[self.list objectAtIndex:row] icon];
    
    CGSize
    itemSize = CGSizeMake(40, 40);
    
    UIGraphicsBeginImageContextWithOptions(itemSize,
                                           NO,0.0);
    
    CGRect
    imageRect = CGRectMake(0.0, 0.0, itemSize.width, itemSize.height);
    
    [icon
     drawInRect:imageRect];
    
    cell.imageView.image
    = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();

    return cell;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    
    return [self.list count];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = [indexPath row];
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    delegate.app = [self.list objectAtIndex:row];
    
    ViewController * mainView = [self.navigationController.viewControllers objectAtIndex:0];
    
    [self.navigationController popToViewController:mainView animated:YES];
    
}

- (void) viewWillAppear:(BOOL)animated
{
    self.tabBarController.tabBar.hidden = true;
    
    [self.navigationController setNavigationBarHidden:NO animated:animated];
    [super viewWillAppear:animated];
}

@end

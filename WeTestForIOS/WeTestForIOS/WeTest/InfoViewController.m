//
//  InfoViewController.m
//  WeTestForIOS
//
//  Created by caorobin on 15-8-24.
//  Copyright (c) 2015年 caorobin. All rights reserved.
//

#import "InfoViewController.h"
#import "AppDelegate.h"
@interface InfoViewController ()

@end

@implementation InfoViewController

@synthesize userIcon = _userIcon;
@synthesize userName = _userName;
@synthesize uin = _uin;


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
    titleLabel.text = @"我";  //设置标题
    self.navigationItem.titleView = titleLabel;
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    
    NSString * url = [[delegate user] faceURL];
    NSLog(@"userUrl is %@" , url);
    NSURL *imgURL = [NSURL URLWithString:url];
    UIImage *image = [UIImage imageWithData:[NSData dataWithContentsOfURL:imgURL]];
    
    NSLog(@"image is %@" ,image);
    
    //self.userIcon.image = image;
    
    [_userName setText:delegate.user.name];
    [_uin setText:delegate.user.uin];
    
    
    // Do any additional setup after loading the view.
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

-(IBAction)buttonLogoutPressed:(id)sender{
    
    [[APPUtil sharedInstance] logout];
    
    [self.tabBarController.navigationController popToRootViewControllerAnimated:TRUE];
    
}

-(IBAction)buttonRecommandPressed:(id)sender{
    
   /* SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.text = @"欢迎使用WeTest助手";
    req.bText = YES;
    req.scene = WXSceneTimeline;
    
    if ([WXApi sendReq:req]) {
        NSLog(@"can wx res "    );
        
    }else
        NSLog(@"can not wx res");*/
    
    [self sendLinkContent];
    
}


-(void) onReq:(BaseReq*)req
{
    
    NSLog(@"onReq");
    
    if([req isKindOfClass:[GetMessageFromWXReq class]])
    {
        // 微信请求App提供内容， 需要app提供内容后使用sendRsp返回
        NSString *strTitle = [NSString stringWithFormat:@"微信请求App提供内容"];
        NSString *strMsg = @"微信请求App提供内容，App要调用sendResp:GetMessageFromWXResp返回给微信";
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strMsg delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        alert.tag = 1000;
        [alert show];
    }
    else if([req isKindOfClass:[ShowMessageFromWXReq class]])
    {
        ShowMessageFromWXReq* temp = (ShowMessageFromWXReq*)req;
        WXMediaMessage *msg = temp.message;
        
        //显示微信传过来的内容
        WXAppExtendObject *obj = msg.mediaObject;
        
        NSString *strTitle = [NSString stringWithFormat:@"微信请求App显示内容"];
        NSString *strMsg = [NSString stringWithFormat:@"标题：%@ \n内容：%@ \n附带信息：%@ \n缩略图:%u bytes\n\n", msg.title, msg.description, obj.extInfo, msg.thumbData.length];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strMsg delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alert show];
    }
    else if([req isKindOfClass:[LaunchFromWXReq class]])
    {
        //从微信启动App
        NSString *strTitle = [NSString stringWithFormat:@"从微信启动"];
        NSString *strMsg = @"这是从微信启动的消息";
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strMsg delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alert show];
        
    }
}

-(void) onResp:(BaseResp*)resp
{
    
    NSLog(@"onResp");
    if([resp isKindOfClass:[SendMessageToWXResp class]])
    {
        NSString *strTitle = [NSString stringWithFormat:@"发送媒体消息结果"];
        NSString *strMsg = [NSString stringWithFormat:@"errcode:%d", resp.errCode];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strMsg delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
        [alert show];
        
    }
}

- (void) sendLinkContent
{
    WXMediaMessage *message = [WXMediaMessage message];
    message.title = @"欢迎使用WeTest助手";
    message.description = @"欢迎使用WeTest助手";
    [message setThumbImage:[UIImage imageNamed:@"resource/logo.png"]];
    
    WXWebpageObject *ext = [WXWebpageObject object];
    ext.webpageUrl = @"http://wetest.qq.com";
    
    message.mediaObject = ext;
    
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = WXSceneTimeline;
    
    if ([WXApi sendReq:req]) {
        NSLog(@"can wx res "    );
        
    }else
        NSLog(@"can not wx res");
}

- (void) viewWillAppear:(BOOL)animated
{
    self.tabBarController.tabBar.hidden = false;
    
}

@end

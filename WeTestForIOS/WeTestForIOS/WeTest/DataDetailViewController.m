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

#import "DataDetailViewController.h"

@interface DataDetailViewController ()

@end

@implementation DataDetailViewController

@synthesize filePath = filePath_;
@synthesize dataContentLabel = dataContentLabel_;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
  
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)viewWillAppear:(BOOL)animated{
    
    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSFileManager * manager = [NSFileManager defaultManager];
    
    
    NSString * filePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[self.log filePath]];
    
    [filePath_ setText:filePath];
    
    
    
    if ([manager fileExistsAtPath:filePath]) {
        
        NSData *data=[NSData dataWithContentsOfFile:filePath options:0 error:NULL];
        
        if (data) {
            
            NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
            [dataContentLabel_ setText: result];
        }

    }
    
  
        
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

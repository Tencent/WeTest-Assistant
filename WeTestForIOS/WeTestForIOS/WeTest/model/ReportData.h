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

#import <Foundation/Foundation.h>

/**
 *  测试报告生成需要的数据信息
 */
@interface ReportData : NSObject{
    
    double cpu;//被测应用所占cpu
    double mem;//被测应用所占内存
    double fps;//被测应用fps（备用）
    double netIn;//被测应用接收流量
    double netOut;//被测应用发送流量
    long time;//时间戳
    
}

-(void) setCpu: (double) cpu;
-(void) setMem: (double) mem;
-(void) setFps:(double) fps;
-(void) setNetIn:(double) netIn;
-(void) setNetOut:(double) netOut;
-(void) setTime:(long) time;

-(double) cpu;
-(double) mem;
-(double) fps;
-(double) netIn;
-(double) netOut;
-(NSTimeInterval) time;

@end
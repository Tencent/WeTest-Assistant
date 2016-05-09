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

#import "ReportData.h"

/**
 *  测试报告生成需要的数据信息
 */
@implementation ReportData



-(void) setCpu:(double)_cpu
{
    cpu = _cpu;
}
-(void) setFps:(double)_fps
{
    
    fps = _fps;
}

-(void) setMem:(double)_mem
{
    
    mem = _mem;
}

-(void) setNetIn:(double)_netIn
{
    
    netIn = _netIn;
}

-(void) setNetOut:(double)_netOut
{
    
    netOut = _netOut;
}

-(void) setTime:(long) _time{

    time = _time;
}

/**
 *   被测应用所占cpu
 *
 *  @return cpu
 */
-(double) cpu{
    
    return cpu;
}

/**
 *  被测应用所占内存
 *
 *  @return mem
 */
-(double) mem
{
    return mem;
}

/**
 *  被测应用fps（备用）
 *
 *  @return fps
 */
-(double) fps;
{
    return fps;
}

/**
 *  被测应用接收流量
 *
 *  @return netIn
 */
-(double) netIn;
{
    return netIn;
}

/**
 *  被测应用发送流量
 *
 *  @return netOut
 */
-(double) netOut;
{
    return netOut;
}

/**
 *  时间戳
 *
 *  @return time
 */
-(NSTimeInterval) time;
{
    return time;
}

@end

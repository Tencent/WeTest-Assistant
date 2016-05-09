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
 *  测试记录的JSON格式
 */

@interface JsonResult : NSObject{
    
    NSMutableArray * cpuContent ;
    
    NSMutableArray * vssContent ;
    
    NSMutableArray * rssContent ;
    
    NSMutableArray * pssContent ;
    
    NSMutableArray * ussContent ;
    
    NSMutableArray * nativeContent ;
    
    NSMutableArray * dalvikContent ;
    
    NSMutableArray * totalContent ;
    
    NSMutableArray * networkInContent ;
    
    NSMutableArray * networkOutContent ;
    
    NSMutableArray * timeContent ;
    
    NSMutableArray * fpsContent ;
    
    NSMutableArray * tagContent ;

}

-(void) setCpuContent: (NSMutableArray *) _cpuContent;
-(void) setVssContent: (NSMutableArray *) _vssContent;
-(void) setRssContent: (NSMutableArray *) _rssContent;
-(void) setPssContent: (NSMutableArray *) _pssContent;
-(void) setUssContent: (NSMutableArray *) _ussContent;
-(void) setNativeContent: (NSMutableArray *) _nativeContent;
-(void) setDalvikContent: (NSMutableArray *) _dalvikContent;
-(void) setTotalContent: (NSMutableArray *) _totalContent;
-(void) setNetworkInContent: (NSMutableArray *) _networkInContent;
-(void) setNetworkOutContent: (NSMutableArray *) _networkOutContent;
-(void) setTimeContent: (NSMutableArray *) _timeContent;
-(void) setFpsContent: (NSMutableArray *) _fpsContent;
-(void) setTagContent: (NSMutableArray *) _tagContent;


-(NSMutableArray *) cpuContent ;

-(NSMutableArray *) vssContent ;

-(NSMutableArray *) rssContent ;

-(NSMutableArray *) pssContent ;

-(NSMutableArray *) ussContent ;

-(NSMutableArray *) nativeContent ;

-(NSMutableArray *) dalvikContent ;

-(NSMutableArray *) totalContent ;

-(NSMutableArray *) networkInContent ;

-(NSMutableArray *) networkOutContent ;

-(NSMutableArray *) timeContent ;

-(NSMutableArray *) fpsContent ;

-(NSMutableArray *) tagContent ;

@end
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

#import "JsonResult.h"

/**
 *  测试记录的JSON格式
 */
@implementation JsonResult

-(void) setCpuContent: (NSMutableArray *) _cpuContent{

    cpuContent = _cpuContent;
    
}
-(void) setVssContent: (NSMutableArray *) _vssContent{

    vssContent=_vssContent;

}
-(void) setRssContent: (NSMutableArray *) _rssContent{

    rssContent = _rssContent;
    
}
-(void) setPssContent: (NSMutableArray *) _pssContent{

    pssContent = _pssContent;
    
}
-(void) setUssContent: (NSMutableArray *) _ussContent{

    ussContent = _ussContent;
    
}
-(void) setNativeContent: (NSMutableArray *) _nativeContent{

    nativeContent = _nativeContent;
    
}
-(void) setDalvikContent: (NSMutableArray *) _dalvikContent{

    dalvikContent = _dalvikContent;
    
}
-(void) setTotalContent: (NSMutableArray *) _totalContent{

    totalContent = _totalContent;
    
}
-(void) setNetworkInContent: (NSMutableArray *) _networkInContent{

    networkInContent = _networkInContent;
    
}
-(void) setNetworkOutContent: (NSMutableArray *) _networkOutContent{

    networkOutContent = _networkOutContent;
    
}
-(void) setTimeContent: (NSMutableArray *) _timeContent{

    timeContent = _timeContent;
    
}
-(void) setFpsContent: (NSMutableArray *) _fpsContent{

    fpsContent = _fpsContent;
    
}
-(void) setTagContent: (NSMutableArray *) _tagContent{

    tagContent = _tagContent;
    
}


-(NSMutableArray *) cpuContent {

    return  cpuContent;
    
}

-(NSMutableArray *) vssContent {

    return vssContent;
}

-(NSMutableArray *) rssContent {

    return  rssContent;
    
}

-(NSMutableArray *) pssContent {

    return  pssContent;
    
}

-(NSMutableArray *) ussContent {

    return  ussContent;
    
}

-(NSMutableArray *) nativeContent {

    return  nativeContent;
    
}

-(NSMutableArray *) dalvikContent {

    return dalvikContent;
    
}

-(NSMutableArray *) totalContent {

    return totalContent;
    
}

-(NSMutableArray *) networkInContent {

    return  networkInContent;
    
}

-(NSMutableArray *) networkOutContent {

    return  networkOutContent;
    
}

-(NSMutableArray *) timeContent {

    return timeContent;
    
}

-(NSMutableArray *) fpsContent {

    return  fpsContent;
    
}

-(NSMutableArray *) tagContent {

    return  tagContent;
    
}


@end

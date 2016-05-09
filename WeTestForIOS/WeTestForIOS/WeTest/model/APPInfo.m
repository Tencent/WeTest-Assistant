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


#import "APPInfo.h"

/**
 *  被测应用信息
 */
@implementation APPInfo

-(void) setAppName: (NSString*) _name
{
    appName = _name;
}
-(void) setIcon: (UIImage *) _icon
{
    
    icon = _icon;
}

-(void) setExecutable:(NSString *) _executable;
{
    
    executable = _executable;
}

-(void) setAppPkgName: (NSString*) _pkgName{

    appPkgName = _pkgName;
}

-(void) setVersion:(NSString *) _version{

    version = _version;
    
}

/**
 *  应用包名
 *
 *  @return app bundle id
 */
-(NSString*) appPkgName{


    return appPkgName;
}

/**
 *  应用图标
 *
 *  @return app icon
 */
-(UIImage *) icon{

    return icon;
}

/**
 *  应用名称
 *
 *  @return app name
 */
-(NSString*) appName
{
    return appName;
}

/**
 *  应用进程名称
 *
 *  @return app executable
 */
-(NSString *) executable;
{
    return executable;
}

/**
 *  应用版本号
 *
 *  @return app version name
 */
-(NSString *) version;
{
    return version;
}



@end

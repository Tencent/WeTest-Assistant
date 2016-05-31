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

#import "APPUtil.h"
#include <mach/mach_traps.h>
#include <mach/mach_init.h>
#include <mach/mach.h>
#import <UIKit/UIKit.h>
#include <sys/types.h>
#include <sys/sysctl.h>
#import <Foundation/Foundation.h>
#import "AppDelegate.h"
#include <ifaddrs.h>
#include <arpa/inet.h>
#include <net/if_dl.h>
#import <net/if.h>
#import "ReportData.h"
#import "CHKeychain.h"
#import <sys/utsname.h>  
//#import <GraphicsServices/GSEvent.h>
#include <mach/mach_time.h> 

@implementation APPUtil

+ (APPUtil *)sharedInstance{
    static APPUtil *singleton;
    static dispatch_once_t token;
    dispatch_once(&token,^{
        //这里调用私有的initSingle方法
        singleton = [[APPUtil alloc]initSingle];
    });
    return singleton;
}

/**
 *  初始化单例
 *
 *  @return AppUtil 单例
 */
- (id)initSingle{
    self = [super init];
    if(self){
        
    }
    return self;
}

- (id)init{
        return [APPUtil sharedInstance];
}

/**
 *  获取iphone设备信息
 *
 *  @return 设备信息字符串
 */
-(NSString *) getDeviceInfo{
    
    NSString *strName = [[UIDevice currentDevice] model];
   
    NSMutableDictionary * dicInfo = [self deviceName];
    
    NSString *strModel = [dicInfo objectForKey:@"type"];
   
    NSString *version = [[UIDevice currentDevice] systemVersion];
    
    host_basic_info_data_t hostInfo;
    mach_msg_type_number_t infoCount;
    
    infoCount = HOST_BASIC_INFO_COUNT;
    host_info( mach_host_self(), HOST_BASIC_INFO, (host_info_t)&hostInfo, &infoCount ) ;
    NSString * cpuType ;
    
    switch (hostInfo.cpu_type) {
        case CPU_TYPE_ANY:
            cpuType = @"any";
            break;
            
        case CPU_TYPE_ARM:
            cpuType = @"arm";
            break;
            
        case CPU_TYPE_ARM64:
            cpuType = @"arm64";
            break;
            
        case CPU_TYPE_HPPA:
            cpuType = @"HPPA";
            break;
            
        case CPU_TYPE_I386:
            cpuType = @"I386";
            break;
            
        case CPU_TYPE_I860:
            cpuType = @"I860";
            break;
            
        case CPU_TYPE_MC680x0:
            cpuType = @"MC680x0";
            break;
            
        case CPU_TYPE_MC88000:
            cpuType = @"MC88000";
            break;
            
        case CPU_TYPE_MC98000:
            cpuType = @"MC98000";
            break;
            
        case CPU_TYPE_POWERPC:
            cpuType = @"POWERPC";
            break;
            
        case CPU_TYPE_POWERPC64:
            cpuType = @"POWERPC64";
            break;
            
        case CPU_TYPE_SPARC:
            cpuType = @"SPARC";
            break;
            
            
        case CPU_TYPE_VAX:
            cpuType = @"VAX";
            break;
            
            
        case CPU_TYPE_X86_64:
            cpuType = @"X86_64";
            break;
            
        default:
            cpuType = @"UNKNOWN";
            break;
    }
  
    int result;

    int mib[2];
    
    mib[0] = CTL_HW;
    mib[1] = HW_CPU_FREQ;
    unsigned long length = sizeof(result);
    if (sysctl(mib, 2, &result, &length, NULL, 0) < 0)
    {
        perror("getting cpu frequency");
    }
   
    NSMutableArray * deviceInfo = [[NSMutableArray alloc] init];
    
    NSString * mem = [[NSString alloc] initWithFormat:@"%d",(int)round((double_t)hostInfo.memory_size/(1024 * 1024))];
    NSString * cpuCount = [[NSString alloc] initWithFormat:@"%u",hostInfo.max_cpus];
    
    CGRect rect_screen = [[UIScreen mainScreen]bounds];
    int scale = [UIScreen mainScreen].scale;
    CGSize size_screen = rect_screen.size;
    NSString * screen = [[NSString alloc] initWithFormat:@"%.0f x %.0f",size_screen.width * scale,size_screen.height * scale];
    
    
    NSMutableDictionary * manu = [[NSMutableDictionary alloc] init];
    [manu setValue:strName forKey:@"manu"];
    [deviceInfo addObject: manu];
    
    NSMutableDictionary * model = [[NSMutableDictionary alloc] init];
    [model setValue:strModel forKey:@"model"];
    [deviceInfo addObject: model];
    
    NSMutableDictionary * mVersion = [[NSMutableDictionary alloc] init];
    [mVersion setValue:version forKey:@"version"];
    [deviceInfo addObject: mVersion];
    
    NSMutableDictionary * cpu = [[NSMutableDictionary alloc] init];
    
    [cpu setValue:cpuCount forKey:@"cpu"];
    [deviceInfo addObject: cpu];
    
   
    NSMutableDictionary * up_mem = [[NSMutableDictionary alloc] init];
    [up_mem setValue:mem forKey:@"mem"];
    [deviceInfo addObject: up_mem];
    
    NSMutableDictionary * resolution = [[NSMutableDictionary alloc] init];
    [resolution setValue:screen forKey:@"resolution"];
    [deviceInfo addObject: resolution];
    
    NSMutableDictionary * freq = [[NSMutableDictionary alloc] init];
    
    [freq setValue:[dicInfo objectForKey:@"cpu"] forKey:@"cpufreq"];
     [deviceInfo addObject: freq];
    
    NSMutableDictionary * cpuname = [[NSMutableDictionary alloc] init];
    [cpuname setValue:cpuType forKey:@"cpuname"];
    [deviceInfo addObject: cpuname];
    
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:deviceInfo options:NSJSONWritingPrettyPrinted error:nil];
    
    NSString * strDeviceInfo = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strDeviceInfo = [strDeviceInfo stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strDeviceInfo = [strDeviceInfo stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    return strDeviceInfo;
}


/**
 *  获取系统信息
 *
 *  @param typeSpecifier 系统信息类型
 *
 *  @return value of typeSpecifier
 */
- (NSUInteger) getSysInfo: (uint) typeSpecifier
{
    size_t size = sizeof(int);
    int results;
    int mib[2] = {CTL_HW, typeSpecifier};
    sysctl(mib, 2, &results, &size, NULL, 0);
    return (NSUInteger) results;
}

/**
 *  设置启动流量
 */
-(void)setStartFlow
{
    BOOL success;
    struct ifaddrs *addrs;
    const struct ifaddrs *cursor;
    const struct if_data *networkStatisc;
    
    NSString *name = [[NSString alloc] init];
    success = getifaddrs(&addrs) == 0;
    if (success)
    {
        cursor = addrs;
        while (cursor != NULL)
        {
            if (cursor->ifa_data == 0)
            {
                cursor = cursor->ifa_next;
                continue;
            }
            if (!(cursor->ifa_flags & IFF_UP) && !(cursor->ifa_flags & IFF_RUNNING)) {
                cursor = cursor->ifa_next;
                continue;
            }
            name = [NSString stringWithFormat:@"%s",cursor->ifa_name];
            if (cursor->ifa_addr->sa_family == AF_LINK)
            {
                //names of interfaces: en0 is WiFi ,pdp_ip0 is WWAN
                if ([name hasPrefix:@"en0"])
                {
                    networkStatisc = (const struct if_data*)cursor->ifa_data;
                    startWiFiSent += networkStatisc->ifi_obytes;
                    startWiFiReceived += networkStatisc->ifi_ibytes;
                    startWiFiFlow = startWiFiSent + startWiFiReceived;
                }
                if([name hasPrefix:@"pdp_ip0"])
                {
                    networkStatisc = (const struct if_data*)cursor->ifa_data;
                    startWWANSend += networkStatisc->ifi_obytes;
                    startWWANReceived += networkStatisc->ifi_ibytes;
                    startWWANFlow = startWWANSend + startWWANReceived;
                }
            }
            cursor = cursor->ifa_next;
        }
        freeifaddrs(addrs);
    }
    
}

/**
 *  获取 3g/gprs 流量
 *
 *  @return 流量值
 */
-(double)getAppWWANFlow;
{
    BOOL success;
    struct ifaddrs *addrs;
    const struct ifaddrs *cursor;
    const struct if_data *networkStatisc;
    
    int WWANSent = 0;
    int WWANReceived = 0;
    int WWANFlow = 0;
    
    NSString *name = [[NSString alloc] init] ;
    success = getifaddrs(&addrs) == 0;
    if (success)
    {
        cursor = addrs;
        while (cursor != NULL)
        {
            if (cursor->ifa_data == 0)
            {
                cursor = cursor->ifa_next;
                continue;
            }
            if (!(cursor->ifa_flags & IFF_UP) && !(cursor->ifa_flags & IFF_RUNNING)) {
                cursor = cursor->ifa_next;
                continue;
            }
            name = [NSString stringWithFormat:@"%s",cursor->ifa_name];
            if (cursor->ifa_addr->sa_family == AF_LINK)
            {
                //names of interfaces: en0 is WiFi ,pdp_ip0 is WWAN
                if([name hasPrefix:@"pdp_ip0"])
                {
                    networkStatisc = (const struct if_data*)cursor->ifa_data;
                    WWANSent += networkStatisc->ifi_obytes;
                    WWANReceived += networkStatisc->ifi_ibytes;
                    WWANFlow = WWANSent+WWANReceived;
                }
            }
            cursor = cursor->ifa_next;
        }
        freeifaddrs(addrs);
    }
    
    
    float res = (WWANFlow - startWWANFlow);
    
        
    wwanFlow = (WWANFlow - startWWANFlow);
    
    startWWANFlow = WWANFlow;
    
    wwanSend = (WWANSent - startWWANSend);
    startWWANSend = WWANSent;
    
    wwanReceive = (WWANReceived - startWWANReceived);
    startWWANReceived = WWANReceived;

    return res;
}

/**
 *  获取wifi流量
 *
 *  @return 流量值
 */
-(double)getAppWIFIFlow;
{
    BOOL success;
    struct ifaddrs *addrs;
    const struct ifaddrs *cursor;
    const struct if_data *networkStatisc;
    
    int WIFISent = 0;
    int WIFIReceived = 0;
    int WIFIFlow = 0;
    
    NSString *name = [[NSString alloc] init];
    success = getifaddrs(&addrs) == 0;
    if (success)
    {
        cursor = addrs;
        while (cursor != NULL)
        {
            if (cursor->ifa_data == 0)
            {
                cursor = cursor->ifa_next;
                continue;
            }
            if (!(cursor->ifa_flags & IFF_UP) && !(cursor->ifa_flags & IFF_RUNNING)) {
                cursor = cursor->ifa_next;
                continue;
            }
            name = [NSString stringWithFormat:@"%s",cursor->ifa_name];
            if (cursor->ifa_addr->sa_family == AF_LINK)
            {
                //names of interfaces: en0 is WiFi ,pdp_ip0 is WWAN
                if([name hasPrefix:@"en0"])
                {
                    networkStatisc = (const struct if_data*)cursor->ifa_data;
                    WIFISent += networkStatisc->ifi_obytes;
                    WIFIReceived += networkStatisc->ifi_ibytes;
                    WIFIFlow = WIFISent+WIFIReceived;
                }
            }
            cursor = cursor->ifa_next;
        }
        freeifaddrs(addrs);
    }
    
    wififlow = (WIFIFlow - startWiFiFlow);
    startWiFiFlow = WIFIFlow;
    
    wifisend = (WIFISent - startWiFiSent);
    startWiFiSent = WIFISent;
    
    wifireceive = (WIFIReceived - startWiFiReceived);
    startWiFiReceived = WIFIReceived;
    
    return wififlow;
}


/**
 *  抓取性能数据
 */
-(void)paraCapture{
    
    AppDelegate * delegate_ = (AppDelegate *)[[UIApplication sharedApplication] delegate];

    
    int mib[4] = {CTL_KERN,KERN_PROC,KERN_PROC_ALL,0};
    
    size_t miblen = 4;
    
    size_t size;
    
    task_t task;
    int st = sysctl(mib, miblen, NULL, &size , NULL, 0);
    
    struct kinfo_proc * process = NULL;
    struct kinfo_proc * newprocess = NULL;
    
    do{
        size += size/10;
        newprocess = realloc(process, size);
        
        if(!newprocess){
            if(process){
                free(process);
                process = NULL;
            }
        }
        
        process = newprocess;
        st = sysctl(mib, miblen, process, &size, NULL, 0);
    }while (st == -1 && errno == ENOMEM) ;
    
    if(st == 0){
        
        if(size % sizeof(struct kinfo_proc) == 0){
            
            
            int nprocess = size/sizeof(struct kinfo_proc);
            
            if (nprocess) {
                
                NSMutableArray * array = [[NSMutableArray alloc] init];
                
                for (int i = nprocess - 1; i>=0; i--) {
                    
                    
                    
                    NSString * processName = [[NSString alloc]initWithFormat:@"%s",process[i].kp_proc.p_comm];
                   
                    if ([[delegate_.app executable] isEqualToString:processName] || [[delegate_.app executable] hasPrefix:processName]) {
                        
                        
                        NSString * processID = [[NSString alloc]initWithFormat:@"%d",process[i].kp_proc.p_pid];
                        
                        int pid =  [processID intValue];
                        
                        int error = task_for_pid(mach_task_self(), pid, &task);
                        
                        task_info_data_t tinfo;
                        mach_msg_type_number_t task_info_count;
                        
                        task_info_count = TASK_INFO_MAX;
                        
                        int kr = task_info(task, TASK_BASIC_INFO, (task_info_t)tinfo, &task_info_count);
                        
                        task_basic_info_t        basic_info;
                        thread_array_t         thread_list;
                        mach_msg_type_number_t thread_count;
                        
                        thread_info_data_t     thinfo;
                        mach_msg_type_number_t thread_info_count;
                        
                        thread_basic_info_t basic_info_th;
                        uint32_t stat_thread = 0; // Mach threads
                        
                        basic_info = (task_basic_info_t)tinfo;
                        
                        kr = task_threads(task, &thread_list, &thread_count);
                        
                        if (kr != KERN_SUCCESS) {
                            
                            continue;
                            
                        }
                        
                        if (thread_count > 0)
                            stat_thread += thread_count;
                        
                        float tot_cpu = 0;
                        
                        for (int j = 0; j < thread_count; j++) {
                            thread_info_count = THREAD_INFO_MAX;
                            kr = thread_info(thread_list[j], THREAD_BASIC_INFO,
                                             (thread_info_t)thinfo, &thread_info_count);
                            if (kr != KERN_SUCCESS) {
                                
                                continue;
                                
                            }
                            basic_info_th = (thread_basic_info_t)thinfo;
                            
                            
                            if (!(basic_info_th->flags & TH_FLAGS_IDLE)) {
                                
                                tot_cpu = tot_cpu + basic_info_th->cpu_usage / (float)TH_USAGE_SCALE * 100.0;
                            }
                            
                        }
                        
                        if(kr != KERN_SUCCESS){
                            
                            continue;
                            
                        }
                        
                        if (error)
                        {
                            
                            // //NSLog(@"task_for_pid return error:\n %s\n", mach_error_string(error));
                            
                        } else {
                            
                            // //NSLog(@"Get the process %d's task port : %x\n", pid, task);
                            
                        }
                        
                        char buffer[1024];
                        
                        bzero(buffer, 1024);
                        
                        [self getAppWIFIFlow];
                        [self getAppWWANFlow];
                        
                        if(wififlow <0){
                            sprintf(buffer, "%.2f%%|%.2uM|0K|0K|0K|%d", tot_cpu,basic_info->resident_size/(1024),(int)fps);
                        }else
                            sprintf(buffer, "%.2f%%|%.2uM|%.2d|%.2d|%.2d|%d", tot_cpu,basic_info->resident_size/(1024),wififlow,wifisend,wifireceive,(int)fps);
                        
                        ReportData *data = [[ReportData alloc] init];
                        
                        
                        [data setCpu:[[NSString stringWithFormat:@"%.2f",tot_cpu] doubleValue]];
                        
                        
                        [data setMem:[[NSString stringWithFormat:@"%.2u",basic_info->resident_size/1024] doubleValue]];
                        
                        
                        [data setFps:[[NSString stringWithFormat:@"%d", 0] doubleValue]];
                        
                        if(wififlow <0){
                            
                            [data setNetIn:0];
                            [data setNetOut:0];
                            
                        }else{
                            
                            [data setNetIn:[[NSString stringWithFormat:@"%.2d",(wifireceive + wwanReceive)] doubleValue]];
                            [data setNetOut:[[NSString stringWithFormat:@"%.2d",(wifisend + wwanSend)] doubleValue]];
                            
                        }
                        
                        [data setFps:0];
                        
                        [data setTime:[[NSProcessInfo processInfo] systemUptime]];
                        
                        [delegate_.reportList addObject:data];
                        
                        
                        NSString * process_CPU = [[NSString alloc]initWithFormat:@"%d",process[i].kp_proc.p_estcpu];
                        double t = [[NSDate date] timeIntervalSince1970] - process[i].kp_proc.p_un.__p_starttime.tv_sec;
                        NSString * proc_useTiem = [[NSString alloc] initWithFormat:@"%f",t];
                        
                        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
                        [dic setValue:processID forKey:@"ProcessID"];
                        [dic setValue:processName forKey:@"ProcessName"];
                        [dic setValue:process_CPU forKey:@"ProcessCPU"];
                        [dic setValue:proc_useTiem forKey:@"ProcessUseTime"];
                        [array addObject:dic];
                        
                        
                    }
                    
                }
                
                free(process);
                process = NULL;
                
                
            }
        }
        
    }
    
    
}

/**
 *  保存测试记录
 *
 *  @return 测试记录信息
 */
-( LogInfo * )reportSave{

    
    LogInfo * nLog = [[LogInfo alloc ] init];
    
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    [delegate.lock lock];
    
    JsonResult * fileContent = [[APPUtil sharedInstance] readRecordContent:[delegate fileName]];
    
    NSString *res = @"";
    
    NSMutableDictionary * cpu = [[NSMutableDictionary alloc] init];
   
    NSMutableDictionary * native = [[NSMutableDictionary alloc] init];
    NSMutableDictionary * dalvik = [[NSMutableDictionary alloc] init];
    NSMutableDictionary * total = [[NSMutableDictionary alloc] init];
    NSMutableDictionary * time = [[NSMutableDictionary alloc] init];
    
    NSMutableDictionary * networkIn = [[NSMutableDictionary alloc] init];
    NSMutableDictionary * networkOut = [[NSMutableDictionary alloc] init];
    

    NSMutableArray * cpu_content,*native_content , *dalvik_content , *total_content,  *networkIn_content, *networkOut_content , *time_content ;
    
    if (fileContent) {
        
        
        cpu_content = [fileContent cpuContent];
        
        native_content = [fileContent nativeContent];
        
        
        dalvik_content =[fileContent dalvikContent];
    
        
        
        total_content = [fileContent totalContent];

        
        networkIn_content = [fileContent networkInContent];
        
        networkOut_content = [fileContent networkOutContent];
        
        time_content = [fileContent timeContent];
        
        
    }else{
        
        
                cpu_content = [[NSMutableArray alloc] init];
        
        
                native_content = [[NSMutableArray alloc] init];
                
        
                dalvik_content = [[NSMutableArray alloc] init];
                
        
                total_content = [[NSMutableArray alloc] init];
                
                networkIn_content = [[NSMutableArray alloc] init];
        
                networkOut_content = [[NSMutableArray alloc] init];
        
                time_content = [[NSMutableArray alloc] init];
        
    }
        delegate.timeEnd = [[NSProcessInfo processInfo] systemUptime];
    
    long long testTime = [[NSNumber numberWithDouble:delegate.timeEnd - delegate.timeStart] longLongValue];
    
    long long timeStart , timeEnd;
   
    NSDate * now = [NSDate date];
    NSTimeInterval gap = [now timeIntervalSince1970];
    timeEnd = [[NSNumber numberWithDouble:gap ] longLongValue];
    timeStart = timeEnd + delegate.timeEnd - delegate.timeStart;
    
    
    for( int i=0; i< delegate.reportList.count; i++){
        
        ReportData *temp = [delegate.reportList objectAtIndex:i];
      
        [cpu_content addObject:[NSNumber numberWithDouble:[temp cpu]]];
              [native_content addObject:[NSNumber numberWithInt:0]];
        [dalvik_content addObject:[NSNumber numberWithInt:0]];
        [total_content addObject:[NSNumber numberWithDouble:[temp mem]]];
        
        [networkIn_content addObject:[NSNumber numberWithDouble:[temp netIn]]];
        [networkOut_content addObject:[NSNumber numberWithDouble:[temp netOut]]];
    
        long long dataTime = [temp time] - delegate.timeStart + timeStart;
        
        [time_content addObject:[NSNumber numberWithLongLong:dataTime]];
        
        
    }
    
    [delegate.reportList removeAllObjects];
    
    [cpu setValue:cpu_content forKey:@"cpu"];
  
    [native setValue:native_content forKey:@"native"];
    [dalvik setValue:dalvik_content forKey:@"dalvik"];
    [total setValue:total_content forKey:@"total"];
    [networkIn setValue:networkIn_content forKey:@"networkIn"];
    [networkOut setValue:networkOut_content forKey:@"networkOut"];
    [time setValue:time_content forKey:@"time"];
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:cpu options:NSJSONWritingPrettyPrinted error:nil];
    
    NSString * strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//cpu
    
    jsonData = [NSJSONSerialization dataWithJSONObject:native options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//native
    
    jsonData = [NSJSONSerialization dataWithJSONObject:dalvik options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//dalvik
    
    jsonData = [NSJSONSerialization dataWithJSONObject:total options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//total
    
    jsonData = [NSJSONSerialization dataWithJSONObject:networkIn options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//networkIn
    
    jsonData = [NSJSONSerialization dataWithJSONObject:networkOut options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//netowrkOut
    
    jsonData = [NSJSONSerialization dataWithJSONObject:time options:NSJSONWritingPrettyPrinted error:nil];
    
    strData = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    strData = [strData stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    strData = [strData stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    
    res = [res stringByAppendingFormat:@"%@\n",strData];//time

    //文件保存
    
    NSFileManager * manager = [NSFileManager defaultManager];
    
    //uploadFile
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSString * filePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:[delegate fileName]];
    
    NSData* testData = [res dataUsingEncoding:NSUTF8StringEncoding];
    
    [testData writeToFile:filePath atomically:YES];
    
    //userFile
    
    NSString * userFilePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
    
    //record file : 文件名 保存时间点 应用名称 项目id 项目名称 测试时长 开始时间     结束时间 应用包名 应用版本
    NSString * record = [NSString stringWithFormat:@"%@|%@|%@|%lld|%lld|%lld|%@|%@",[delegate fileName],[[APPUtil sharedInstance] formatLongTime:timeEnd],[delegate.app appName],testTime,timeStart,timeEnd,[delegate.app appPkgName],[delegate.app version]];
    
    
    [nLog setFilePath:[delegate fileName]];
    [nLog setSaveTime:[[APPUtil sharedInstance] formatLongTime:timeEnd]];
    [nLog setAppName:[delegate.app appName]];
    [nLog setTestTime:[NSString stringWithFormat:@"%lld",testTime]];
    [nLog setTimeStart:[NSString stringWithFormat:@"%lld",timeStart]];
    [nLog setTimeEnd:[NSString stringWithFormat:@"%lld",timeEnd]];
    [nLog setAppPkgName:[delegate.app appPkgName]];
    [nLog setAppVersion:[delegate.app version]];
    
    NSData *data=[NSData dataWithContentsOfFile:userFilePath options:0 error:NULL];
    
    if (data) {
        
        NSLog(@"update data to file %@" , userFilePath);
        [self updateLog:[delegate fileName] content:record];
        
    }else{
        
        NSLog(@"write data to file %@" , userFilePath);
        NSData* userData = [record dataUsingEncoding:NSUTF8StringEncoding];
        
        if ([manager fileExistsAtPath:userFilePath isDirectory:false]) {
            
            [userData writeToFile:userFilePath atomically:YES];
            
        }else{
        
            [manager createFileAtPath:userFilePath contents:userData attributes:nil];
            
        }
      
        
    }
        
    [delegate.lock unlock];
    
    
    
    
    NSLog(@"new record saved end ");
    
    return nLog;
    
}

/**
 *  添加测试记录
 */
-(void) addRecord{

    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSDate *datenow = [NSDate date];
    
    NSString * userFilePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
    
    NSDateFormatter* fmt = [[NSDateFormatter alloc] init];
    NSTimeZone* GTMzone = [NSTimeZone timeZoneForSecondsFromGMT:0];
    [fmt setTimeZone:GTMzone];
    //fmt.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"zh_CN"];
    fmt.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    
    NSString* dateString = [fmt stringFromDate:datenow];
    
    NSString * record = [NSString stringWithFormat:@"%@|%@|%@|0",delegate.fileName,dateString,[delegate.app appName]];
    
    NSData *data=[NSData dataWithContentsOfFile:userFilePath options:0 error:NULL];
    
    if (data) {
        
        NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
        
        result = [result stringByAppendingFormat:@"\n%@",record];
        
        NSData* userData = [result dataUsingEncoding:NSUTF8StringEncoding];
        
        [userData writeToFile:userFilePath atomically:YES];
        
    }else{
        
        
        NSData* userData = [record dataUsingEncoding:NSUTF8StringEncoding];
        
        [userData writeToFile:userFilePath atomically:YES];
        
    }
    
}

/**
 *  向测试记录文件中添加性能数据
 *
 *  @param content 性能数据
 *  @param name    文件名称
 */
-(void) fileAppend:(NSString *) content fileName:(NSString *) name
{
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    NSString * filePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:name];
    
    if(![fileManager fileExistsAtPath:filePath]) {
        
        [content writeToFile:name atomically:YES encoding:NSUTF8StringEncoding error:nil];
        
    }
    
    NSFileHandle  *outFile;
    NSData *buffer;
    
    outFile = [NSFileHandle fileHandleForWritingAtPath:filePath];
    
    if(outFile == nil)
    {
        //NSLog(@"Open of file for writing failed");
    }
    
    //找到并定位到outFile的末尾位置(在此后追加文件)
    [outFile seekToEndOfFile];
    
    //读取inFile并且将其内容写到outFile中
    NSString *bs = [NSString stringWithFormat:@"%@",content];
    buffer = [bs dataUsingEncoding:NSUTF8StringEncoding];
    
    [outFile writeData:buffer];
    
    //关闭读写文件
    [outFile closeFile];
    
}

/**
 *  将颜色代码值转换成UIColor
 *
 *  @param hexColorString 颜色代码值
 *
 *  @return 转换后的UIColor
 */
-(UIColor *)colorWithHexColorString:(NSString *)hexColorString{
   
    if ([hexColorString length] <6){//长度不合法
        return nil;
    }
    NSString *tempString=[hexColorString lowercaseString];
    
    if ([tempString hasPrefix:@"0x"]){//检查开头是0x
        tempString = [tempString substringFromIndex:2];
    }else if ([tempString hasPrefix:@"#"]){//检查开头是#
        tempString = [tempString substringFromIndex:1];
    }
    if ([tempString length] !=6){
        return nil;
    }
    //分解三种颜色的值
    NSRange range;
    range.location =0;
    range.length =2;
    NSString *rString = [tempString substringWithRange:range];
    range.location =2;
    NSString *gString = [tempString substringWithRange:range];
    range.location =4;
    NSString *bString = [tempString substringWithRange:range];
    //取三种颜色值
    unsigned int r, g, b;
    [[NSScanner scannerWithString:rString]scanHexInt:&r];
    [[NSScanner scannerWithString:gString]scanHexInt:&g];
    [[NSScanner scannerWithString:bString]scanHexInt:&b];
    
    return [UIColor colorWithRed:((float) r /255.0f)
                          green:((float) g /255.0f)
                           blue:((float) b /255.0f)
                          alpha:1.0f];
}

/**
 *  将图片转换成固定大小
 *
 *  @param image 源图片
 *  @param size  需要的size
 *
 *  @return 转换后的图片
 */
-(UIImage*)  OriginImage:(UIImage *)image   scaleToSize:(CGSize)size
{
    // 创建一个bitmap的context
    // 并把它设置成为当前正在使用的context
    UIGraphicsBeginImageContext(size);
    
    // 绘制改变大小的图片
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    
    // 从当前context中创建一个改变大小后的图片
    UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();
    
    // 返回新的改变大小后的图片
    return scaledImage;
}
/**
 *  将时间戳格式化
 *
 *  @param tip 时间戳
 *
 *  @return format string “yyyy-MM-dd HH:mm:ss"
 */
-(NSString *)  formatLongTime: (long long) tip{

    NSNumber *time = [NSNumber numberWithLongLong:tip];
    NSTimeInterval nsTimeInterval = [time longValue];
    NSDate *date = [[NSDate alloc] initWithTimeIntervalSince1970:nsTimeInterval];
    
    NSDateFormatter *format=[[NSDateFormatter alloc] init];
    [format setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *dateString = [format stringFromDate:date];
    
    return  dateString;
    
}

/**
 *  清空测试记录
 */
-(void) logClear{

    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    NSString *FileName=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
    
    NSFileManager * fileManager = [NSFileManager defaultManager];
    

    if ([fileManager fileExistsAtPath:FileName isDirectory:false]) {
        
        NSData *data=[NSData dataWithContentsOfFile:FileName options:0 error:NULL];
        
        
        NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
        
        NSArray * strLogList = [result componentsSeparatedByString:@"\n"];
        
        
        for (int i = 0 ; i < strLogList.count; i++) {
            
            NSArray * strLogContent = [strLogList[i] componentsSeparatedByString:@"|"];
            
            NSString * path=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:strLogContent[0]];
            
            if ([fileManager fileExistsAtPath:path isDirectory:false]) {
                
                [fileManager removeItemAtPath:path error:nil];
                
            }
            
            
        }
        
        
        [fileManager removeItemAtPath:FileName error:nil];
        
        

        BOOL isDir = true;
        
        BOOL isDirExist = [fileManager fileExistsAtPath:[documentPaths objectAtIndex:0]
                                        isDirectory:&isDir];
        
      
        if(!(isDirExist && isDir)){
            
            [fileManager createDirectoryAtPath:[documentPaths objectAtIndex:0] withIntermediateDirectories:YES attributes:nil error:nil];
        }

    }
    
    
    
}
/**
 *  删除测试记录
 *
 *  @param fileName 测试记录文件名称
 */
-(void) delLog:(NSString * ) fileName{
  
        NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
        AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        
        NSString * userFilePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
        

        NSData *data=[NSData dataWithContentsOfFile:userFilePath options:0 error:NULL];
        
        
        NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
        
        NSArray * strLogList = [result componentsSeparatedByString:@"\n"];
        
        NSString * reStrData = @"";

        for (int i = 0 ; i < strLogList.count; i++) {
            
            NSArray * strLogContent = [strLogList[i] componentsSeparatedByString:@"|"];
            
            if (![fileName isEqualToString:strLogContent[0]]) {
                
                reStrData = [reStrData stringByAppendingFormat:@"%@\n",strLogList[i]];
            
            }
           
        }
        
        NSData* userData = [reStrData dataUsingEncoding:NSUTF8StringEncoding];
        
        [userData writeToFile:userFilePath atomically:YES];
    
         NSFileManager * fileManager = [NSFileManager defaultManager];
    
         NSString * delPath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:fileName];
    
        if ([fileManager fileExistsAtPath:delPath]) {
        
            [fileManager removeItemAtPath:delPath error:nil];
        

        }

    
    
        BOOL isDir = true;
    
        BOOL isDirExist = [fileManager fileExistsAtPath:[documentPaths objectAtIndex:0]
                                        isDirectory:&isDir];
    
    
        if(!(isDirExist && isDir)){
        
            [fileManager createDirectoryAtPath:[documentPaths objectAtIndex:0] withIntermediateDirectories:YES attributes:nil error:nil];
        }
 //   }
    
}

/**
 *  读取测试记录
 *
 *  @param fileName 测试记录文件名称
 *
 *  @return 测试记录json格式数据
 */
-(JsonResult *) readRecordContent:(NSString * ) fileName{

    JsonResult * res = [[JsonResult alloc ] init];
    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
   
    NSString * recordFilePath =[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:fileName];

    
    NSFileManager * manager = [NSFileManager defaultManager];
    
    if (![manager fileExistsAtPath:recordFilePath]) {
        
        return nil;
        
    }
    
    NSData *data=[NSData dataWithContentsOfFile:recordFilePath options:0 error:NULL];
    
    if (data) {
        
        NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
        
        NSArray * content = [result componentsSeparatedByString:@"\n"];

        if (content.count < 13) {
            return  nil;
        }
        
        //cpu
        NSData* jsonData = [content[0] dataUsingEncoding:NSUTF8StringEncoding];
        
        NSMutableDictionary * dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * cpuContent = [[NSMutableArray alloc] init];
        
        [cpuContent addObjectsFromArray:[dict objectForKey:@"cpu"]];
        
        if (cpuContent == 0) {
            return nil;
        }
        
        [res setCpuContent:cpuContent];
        
        
        jsonData = [content[1] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * vssContent = [[NSMutableArray alloc] init];
        [vssContent addObjectsFromArray:[dict objectForKey:@"vss"]] ;
        
        [res setVssContent:vssContent];

        jsonData = [content[2] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * rssContent = [[NSMutableArray alloc] init];
        [rssContent addObjectsFromArray:[dict objectForKey:@"rss"]];
        
        
        [res setRssContent:rssContent];
        
        jsonData = [content[3] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * pssContent = [[NSMutableArray alloc] init];
        [pssContent addObjectsFromArray:[dict objectForKey:@"pss"]];
        
        [res setPssContent:pssContent];
        
        jsonData = [content[4] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * ussContent = [[NSMutableArray alloc] init];
        [ussContent addObjectsFromArray:[dict objectForKey:@"uss"]];
        
        [res setUssContent:ussContent];
        
        jsonData = [content[5] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * nativeContent = [[NSMutableArray alloc] init];
        [nativeContent addObjectsFromArray:[dict objectForKey:@"native"]];
        
        [res setNativeContent:nativeContent];
        
        
        jsonData = [content[6] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * dalvikContent = [[NSMutableArray alloc] init];
        [dalvikContent addObjectsFromArray:[dict objectForKey:@"dalvik"]];
        
        [res setDalvikContent:dalvikContent];

        jsonData = [content[7] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * totalContent = [[NSMutableArray alloc] init];
        [totalContent addObjectsFromArray:[dict objectForKey:@"total"]];
        
        [res setTotalContent:totalContent];
        
        
        jsonData = [content[8] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * networkInContent = [[NSMutableArray alloc] init];
        [networkInContent addObjectsFromArray:[dict objectForKey:@"networkIn"]];
        
        [res setNetworkInContent:networkInContent];
        
        jsonData = [content[9] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * networkOutContent = [[NSMutableArray alloc] init];
        [networkInContent addObjectsFromArray:[dict objectForKey:@"networkOut"]];
        
        [res setNetworkOutContent:networkOutContent];
        
        
        jsonData = [content[10] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * timeContent = [[NSMutableArray alloc] init];
        [timeContent addObjectsFromArray:[dict objectForKey:@"time"]];
        
        [res setTimeContent:timeContent];
        
        jsonData = [content[11] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * fpsContent = [[NSMutableArray alloc] init];
        [fpsContent addObjectsFromArray:[dict objectForKey:@"fps"]];
        
        [res setFpsContent:fpsContent];
        
        jsonData = [content[12] dataUsingEncoding:NSUTF8StringEncoding];
        
        dict = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableLeaves error:nil ];
        
        NSMutableArray * tagContent = [[NSMutableArray alloc] init];
        [tagContent addObjectsFromArray:[dict objectForKey:@"tag"]];
        
        [res setTagContent:tagContent];
        
        
    }else{
        
        return nil;
        
    }

    
    return res;
    
}

/**
 *  更新测试记录数据
 *
 *  @param fileName 测试记录文件名称
 *  @param content  需要更新的内容
 */
-(void) updateLog:(NSString * ) fileName content:(NSString *) content{

    NSArray *documentPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,  NSUserDomainMask,YES);
    
    AppDelegate * delegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    NSString * userFilePath=[[documentPaths objectAtIndex:0] stringByAppendingPathComponent:@"wetest"];
    
    
    NSData *data=[NSData dataWithContentsOfFile:userFilePath options:0 error:NULL];
    
    
    NSString *result = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
    
    NSArray * strLogList = [result componentsSeparatedByString:@"\n"];
        
    NSString * reStrData = @"";
    
    BOOL hasFile = false;
    NSLog(@"loglist size = %d userfilepath = %@" , strLogList.count , userFilePath);
    for (int i = 0 ; i < strLogList.count; i++) {
        
        NSLog(@"loglist content = %@" , strLogList[i]);
            NSArray * strLogContent = [strLogList[i] componentsSeparatedByString:@"|"];
        
            if (strLogContent.count > 2) {
                
                if (![fileName isEqualToString:strLogContent[0]]) {
                
                    NSLog(@"content0 = %@" ,  strLogContent[0] );
                    reStrData = [reStrData stringByAppendingFormat:@"%@\n",strLogList[i]];
                
                }else{
                
                    NSLog(@"fileName = %@ , content0 = %@" , fileName , strLogContent[0] );
                    reStrData = [reStrData stringByAppendingFormat:@"%@\n",content];
                    hasFile = true;
                }

            }
        
    }
    
    if (!hasFile) {
        
        NSLog(@"has not file");
        reStrData = [reStrData stringByAppendingFormat:@"%@\n",content];
        
    }else{
        
        NSLog(@"has file");
        
    }
    
    NSData* userData = [reStrData dataUsingEncoding:NSUTF8StringEncoding];
    
    [userData writeToFile:userFilePath atomically:YES];
}

/**
 *  根据版本名称判断wetest是否需要更新
 *
 *  @param oldVersion 当前wetest版本
 *  @param newVersion 最新wetest版本
 *
 *  @return true 更新 false 不更新
 */
-(BOOL)CompareVersionFromOldVersion : (NSString *)oldVersion
                         newVersion : (NSString *)newVersion
{
    
    NSArray*oldV = [oldVersion componentsSeparatedByString:@"."];
    NSArray*newV = [newVersion componentsSeparatedByString:@"."];
    
    if (oldV.count == newV.count) {
        for (NSInteger i = 0; i < oldV.count; i++) {
            NSInteger old = [(NSString *)[oldV objectAtIndex:i] integerValue];
            NSInteger new = [(NSString *)[newV objectAtIndex:i] integerValue];
            if (old < new) {
                return YES;
            }
        }
        return NO;
    } else {
        return NO;
    }
}

/**
 *  通过系统名称判断手机名称 (需定期维护)
 *
 *  @return 手机名称
 */
- (NSMutableDictionary*) deviceName
{
    struct utsname systemInfo;
    
    uname(&systemInfo);
    
    NSString* code = [NSString stringWithCString:systemInfo.machine
                                        encoding:NSUTF8StringEncoding];
    
    static NSDictionary* deviceNamesByCode = nil;
    
    if (!deviceNamesByCode) {
        
        deviceNamesByCode = @{@"i386"      :@{@"type":@"Simulator",@"cpu":@""},
                              @"iPod1,1"   :@{@"type":@"iPod Touch",@"cpu":@"0.8"},      // (Original)
                              @"iPod2,1"   :@{@"type":@"iPod Touch",@"cpu":@"0.8"},      // (Second Generation)
                              @"iPod3,1"   :@{@"type":@"iPod Touch",@"cpu":@"0.8"},      // (Third Generation)
                              @"iPod4,1"   :@{@"type":@"iPod Touch",@"cpu":@"0.8"},     // (Fourth Generation)
                              @"iPhone1,1" :@{@"type":@"iPhone",@"cpu":@"0.6"},          // (Original)
                              @"iPhone1,2" :@{@"type":@"iPhone",@"cpu":@"0.6"},          // (3G)
                              @"iPhone2,1" :@{@"type":@"iPhone",@"cpu":@"0.6"},          // (3GS)
                              @"iPad1,1"   :@{@"type":@"iPad",@"cpu":@"1.0"},            // (Original)
                              @"iPad2,1"   :@{@"type":@"iPad 2",@"cpu":@"1.0"},          //
                              @"iPad3,1"   :@{@"type":@"iPad",@"cpu":@"1.0"},            // (3rd Generation)
                              @"iPhone3,1" :@{@"type":@"iPhone 4",@"cpu":@"1.0"},        //
                              @"iPhone4,1" :@{@"type":@"iPhone 4S",@"cpu":@"0.8"},       //
                              @"iPhone5,1" :@{@"type":@"iPhone 5",@"cpu":@"1.3"},        // (model A1428, AT&T/Canada)
                              @"iPhone5,2" :@{@"type":@"iPhone 5",@"cpu":@"1.3"},        // (model A1429, everything else)
                              @"iPad3,4"   :@{@"type":@"iPad",@"cpu":@"1.0"},            // (4th Generation)
                              @"iPad2,5"   :@{@"type":@"iPad Mini",@"cpu":@"1.0"},       // (Original)
                              @"iPhone5,3" :@{@"type":@"iPhone 5c",@"cpu":@"1.3"},       // (model A1456, A1532 | GSM)
                              @"iPhone5,4" :@{@"type":@"iPhone 5c",@"cpu":@"1.3"},       // (model A1507, A1516, A1526 (China), A1529 | Global)
                              @"iPhone6,1" :@{@"type":@"iPhone 5s",@"cpu":@"1.3"},       // (model A1433, A1533 | GSM)
                              @"iPhone6,2" :@{@"type":@"iPhone 5s",@"cpu":@"1.3"},       // (model A1457, A1518, A1528 (China), A1530 | Global)
                              @"iPad4,1"   :@{@"type":@"iPad Air",@"cpu":@"1.4"},        // 5th Generation iPad (iPad Air) - Wifi
                              @"iPad4,2"   :@{@"type":@"iPad Air",@"cpu":@"1.4"},        // 5th Generation iPad (iPad Air) - Cellular
                              @"iPad4,4"   :@{@"type":@"iPad Mini",@"cpu":@"1.0"},       // (2nd Generation iPad Mini - Wifi)
                              @"iPad4,5"   :@{@"type":@"iPad Mini",@"cpu":@"1.0"},        // (2nd Generation iPad Mini - Cellular)
                              @"iphone7,2" :@{@"type":@"iphone 6",@"cpu":@"1.4"},
                              @"iphone7,1" :@{@"type":@"iphone 6 plus",@"cpu":@"1.4"}
                              };
    }
    
    NSMutableDictionary * dictInfo = [deviceNamesByCode objectForKey:code];

    
    if (!dictInfo) {
        dictInfo = [[NSMutableDictionary alloc] init];
        [dictInfo setValue:@"iPhone" forKey:@"type"];
        [dictInfo setValue:@"0.6" forKey:@"cpu"];
    }
   
 
    return dictInfo;
}


/*- (void) touchEvent{

    mach_port_t thePortOfApp = GSCopyPurpleNamedPort("tencent.WeTest");

    /*mach_port_t thePortOfApp = GSCopyPurpleNamedPort("tencent.WeTest");
    
    GSEventRecord header;
    GSHandInfo click;
    GSPathInfo pathInfo = {2,2,2,1,1,{50,50},NULL};
    
    bzero(&header, sizeof(header));
    bzero(&click, sizeof(click));
    
    header.type = kGSEventHand;
    header.subtype = kGSEventSubTypeUnknown;
    header.location.x = 50;
    header.location.y = 50;
    
    header.windowLocation.x = 50;
    header.windowLocation.y = 50;
    
    header.infoSize = sizeof(GSHandInfo) + sizeof(GSPathInfo);
    header.timestamp = mach_absolute_time();
    
    click.type = kGSHandInfoTypeTouchDown;
    click.deltaX = 1;
    click.deltaY = 1;
    click.pathInfosCount = 1;
    
    struct
    {
        GSEventRecord record;
        GSHandInfo hand;
        GSHandInfo path;
    }  record = {header, click ,pathInfo};
    
    GSSendEvent(&record, thePortOfApp);
    
    
    CGPoint location = CGPointMake(160, 400);
    
    uint8_t touchEvent[sizeof(GSEventRecord) + sizeof(GSHandInfo) + sizeof(GSPathInfo)];
    
    // structure of touch GSEvent
    struct GSTouchEvent {
        GSEventRecord record;
        GSHandInfo    handInfo;
    } * event = (struct GSTouchEvent*) &touchEvent;
    bzero(touchEvent, sizeof(touchEvent));
    
    // set up GSEvent
    event->record.type = kGSEventHand;
    event->record.windowLocation = location;
    event->record.timestamp = GSCurrentEventTimestamp();
    event->record.infoSize = sizeof(GSHandInfo) + sizeof(GSPathInfo);
    event->handInfo.type = kGSHandInfoTypeTouchDown;
    
    event->handInfo.pathInfosCount = 1;

    /*if (is_50_or_higher){
        event->handInfo.x52 = 1;
    } else {
        event->handInfo.pathInfosCount = 1;
    }
    
    
    bzero(&event->handInfo.pathInfos[0], sizeof(GSPathInfo));
    event->handInfo.pathInfos[0].pathIndex     = 1;
    event->handInfo.pathInfos[0].pathIdentity  = 2;
    event->handInfo.pathInfos[0].pathProximity = 1 ? 0x03 : 0x00;;
    event->handInfo.pathInfos[0].pathLocation  = location;
    
    //mach_port_t port = (mach_port_t)getFrontMostAppPort();
    GSSendEvent((GSEventRecord *)event, thePortOfApp);
    /*GSPathInfo pathInfo = {2,2,2,1,1,{50,50}, NULL};
    
    struct
    {
        GSEventRecord record;
        GSHandInfo hand;
        GSPathInfo path;
    } record = {pathInfo};
    
    
}*/

-(NSMutableArray * ) getApplistInfo{

    NSMutableArray *array = [[NSMutableArray alloc] init];
    
    Class LSApplicationWorkspace_class = objc_getClass("LSApplicationWorkspace");
    
    NSObject* workspace = [LSApplicationWorkspace_class performSelector:@selector(defaultWorkspace)];
    
    NSArray *apps = [workspace performSelector:@selector(allApplications)];
    
    Class LSApplicationProxy_class = objc_getClass("LSApplicationProxy");
    
    for (LSApplicationProxy_class  in apps)
    {
        NSString * res = [LSApplicationProxy_class performSelector:@selector(applicationIdentifier)];
        
        if (TRUE) {
            
            NSURL * bundleFullURL = [LSApplicationProxy_class performSelector:@selector(bundleURL)];
            NSString * s_bundleURL = [bundleFullURL absoluteString];
            
            if ([s_bundleURL hasPrefix:@"file:///private/"]){
                
                
                //if(true){
                APPInfo * appInfo = [[APPInfo alloc]init];
                
                [appInfo setIcon:nil];
                
                [appInfo setAppPkgName:res];
                
                NSString *prefix = @"file://";
                NSRange needleRange = NSMakeRange(prefix.length,
                                                  s_bundleURL.length - prefix.length );
                
                NSString *needle = [s_bundleURL substringWithRange:needleRange];
                
                NSBundle *bundle = [NSBundle bundleWithPath:needle];
                
                NSLog(@"bundle dict : %@" ,  [bundle infoDictionary]);
                
                NSArray* allIcons = [[bundle infoDictionary] valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconFiles"];
                
                if (!allIcons) {
                    allIcons = [[bundle infoDictionary] objectForKey:@"CFBundleIconFiles"];
                    if (!allIcons) {
                        allIcons = [[bundle infoDictionary] valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon"];
                        if (!allIcons) {
                            allIcons = [[bundle infoDictionary] objectForKey:@"CFBundleIcons"];
                        }
                    }
                    
                }
                
                NSLog(@"allIcons : %@" ,  allIcons);
                
                NSString* executable = [[bundle infoDictionary] valueForKeyPath:@"CFBundleExecutable"];
                
                
                NSString *filePath = nil;
                
                [appInfo setExecutable:executable];
                
                
                NSString* appName = [[bundle infoDictionary] valueForKeyPath:@"CFBundleDisplayName"];
                
                
                [appInfo setAppName:appName];
                
                NSString* appVersion = [[bundle infoDictionary] valueForKeyPath:@"CFBundleShortVersionString"];
                
                if (!appVersion) {
                    appVersion = [[bundle infoDictionary] valueForKeyPath:@"CFBundleVersion"];
                    
                }
                
                [appInfo setVersion:appVersion];
                
                NSFileManager * manager = [NSFileManager defaultManager];
                
                UIImage* appIcon = nil;
                
                prefix = @"/private";
                
                needleRange = NSMakeRange(prefix.length,
                                          needle.length - prefix.length );

                NSString *icon_path = [needle substringWithRange:needleRange];
                
                if (allIcons) {
                    
                    NSString *fileName = nil;
                    
                    
                    for (int i = 0 ; i < allIcons.count; i++) {
                        
                        fileName = allIcons[i];
                        
                        if (![fileName isEqualToString:@""]) {
                            
                            filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                            
                            if ([manager fileExistsAtPath:filePath]) {
                                
                                if (([fileName rangeOfString:@"2x"].location == NSNotFound) && ([fileName rangeOfString:@"3x"].location == NSNotFound)) {
                                    
                                    NSString * header = [fileName substringToIndex:[fileName rangeOfString:@"."].location];
                                    
                                    NSString * suffix = [fileName substringFromIndex:[fileName rangeOfString:@"."].location];
                                    
                                    fileName = [NSString stringWithFormat:@"%@@2x%@",header , suffix];
                                    
                                    filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                    
                                    if ([manager fileExistsAtPath:filePath]) {
                                        
                                        appIcon = [UIImage imageWithContentsOfFile:filePath];
                                        
                                        [appInfo setIcon:appIcon];
                                        break;
                                        
                                    }else{
                                        
                                        fileName = [NSString stringWithFormat:@"%@@3x%@",header , suffix];
                                        
                                        filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                    
                                        if ([manager fileExistsAtPath:filePath]) {
                                            
                                            appIcon = [UIImage imageWithContentsOfFile:filePath];
                                            
                                            
                                            [appInfo setIcon:appIcon];
                                          
                                            break;
                                            
                                        }else{
                                            
                                            fileName = [NSString stringWithFormat:@"%@%@",header , suffix];
                                            filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                            if ([manager fileExistsAtPath:filePath]) {
                                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                                [appInfo setIcon:appIcon];
                                                break;
                                            }
                                        }
                                        
                                    }
                                    
                                }else{
                                   
                                    appIcon = [UIImage imageWithContentsOfFile:filePath];
                                    
                                    [appInfo setIcon:appIcon];
                                    
                                    break;
                                    
                                }
                                
                                
                            }else{
                                
                                if ([allIcons[i] rangeOfString:@"."].location == NSNotFound) {
                                    
                                    NSString *fileNamePng = [NSString stringWithFormat:@"%@@2x.png",allIcons[i]];
                                    NSString *fileNameJpg = [NSString stringWithFormat:@"%@@2x.jpg",allIcons[i]];
                                    
                                    prefix = @"/private";
                                    needleRange = NSMakeRange(prefix.length,
                                                              needle.length - prefix.length );
                                    
                                    icon_path = [needle substringWithRange:needleRange];
                                    
                                    NSString *file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                    
                                    NSString *file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                    
                                    
                                    if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]) {
                                        
                                        if ([manager fileExistsAtPath:file_png_path]) {
                                            
                                             appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                            
                                        }else{
                                            
                                             appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                            
                                        }
                                       
                                        
                                        [appInfo setIcon:appIcon];
                                        
                                        break;
                                        
                                    }else{
                                        
                                        fileNamePng = [NSString stringWithFormat:@"%@@3x.png",allIcons[i]];
                                        fileNameJpg = [NSString stringWithFormat:@"%@@3x.jpg",allIcons[i]];
                                        
                                        file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                        file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                        
                                        if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]) {
                                            
                                            if ([manager fileExistsAtPath:file_png_path]) {
                                                
                                                appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                                
                                            }else{
                                                
                                                appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                                
                                            }
                                            
                                            [appInfo setIcon:appIcon];
                                            
                                            break;
                                            
                                        }else{
                                            
                                            fileNamePng = [NSString stringWithFormat:@"%@.png",allIcons[i]];
                                            fileNameJpg = [NSString stringWithFormat:@"%@.jpg",allIcons[i]];

                                            file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                            file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                            
                                            NSLog(@"file png path = %@ , file jpg path = %@" , file_png_path , file_jpg_path);
                                            
                                            if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]){
                                                if ([manager fileExistsAtPath:file_png_path]) {
                                                    
                                                    appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                                    
                                                }else{
                                                    
                                                    appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                                    
                                                }
                                                
                                                [appInfo setIcon:appIcon];

                                                break;
                                            }
                                        }
                                    }
                                    
                                }
                                
                            }
                            
                        }
                        
                    }
                    
                    
                    if (![appInfo icon]) {
                        
                        filePath = [[NSString alloc]  initWithFormat:@"%@/Icon.png",icon_path];
                        
                        if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                            
                            appIcon = [UIImage imageWithContentsOfFile:filePath];
                            
                            [appInfo setIcon:appIcon];
                            
                            
                        }else{
                            
                            
                            filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@2x.png",icon_path];
                            
                            
                            if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                
                                [appInfo setIcon:appIcon];
                                
                                
                            }else{
                                
                                filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@3x.png",icon_path];
                                
                                if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                    appIcon = [UIImage imageWithContentsOfFile:filePath];
                                    
                                    [appInfo setIcon:appIcon];
                                    
                                    
                                }
                            }
                            
                        }
                        
                        
                    }
                    
                    
                }else{
                    
                    
                    filePath = [[NSString alloc]  initWithFormat:@"%@/Icon.png",icon_path];
                    
                    if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                        
                        appIcon = [UIImage imageWithContentsOfFile:filePath];
                        
                        [appInfo setIcon:appIcon];
                        
                        
                        
                    }else{
                        
                        filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@2x.png",icon_path];
                        
                        if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                            appIcon = [UIImage imageWithContentsOfFile:filePath];
                            
                            [appInfo setIcon:appIcon];
                            
                            
                        }else{
                            
                            filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@3x.png",icon_path];
                            
                            if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                
                                [appInfo setIcon:appIcon];
                                
                                
                            }
                        }
                        
                    }
                    
                    
                    
                    
                }
                
                
                if (![appInfo icon]) {
                    
                    NSLog(@"no icon of the %@" , [appInfo appPkgName]);
                }else{
                    
                    [array addObject:appInfo];
                    
                }
                
                
                
                
            }
            
        }
        
        
    }
    

    return array;
    
}


-(APPInfo * ) getApplistInfoByPkgName :(NSString * ) pkgName{
    
    
    APPInfo * appInfo = [[APPInfo alloc]init];

    [appInfo setIcon:nil];
    
    if (pkgName == nil || [pkgName isEqualToString:@""]) {
        
        return appInfo;
    }
    
    Class LSApplicationWorkspace_class = objc_getClass("LSApplicationWorkspace");
    
    NSObject* workspace = [LSApplicationWorkspace_class performSelector:@selector(defaultWorkspace)];
    
    NSArray *apps = [workspace performSelector:@selector(allApplications)];
    
    Class LSApplicationProxy_class = objc_getClass("LSApplicationProxy");
    
    for (LSApplicationProxy_class  in apps)
    {
        NSString * res = [LSApplicationProxy_class performSelector:@selector(applicationIdentifier)];
        
        if ([res isEqualToString:pkgName]) {
            
            if (TRUE) {
                
                NSURL * bundleFullURL = [LSApplicationProxy_class performSelector:@selector(bundleURL)];
                NSString * s_bundleURL = [bundleFullURL absoluteString];
                
                if ([s_bundleURL hasPrefix:@"file:///private/"]){
                    
                    
                    [appInfo setAppPkgName:res];
                    
                    NSString *prefix = @"file://";
                    NSRange needleRange = NSMakeRange(prefix.length,
                                                      s_bundleURL.length - prefix.length );
                    
                    NSString *needle = [s_bundleURL substringWithRange:needleRange];
                    
                    NSBundle *bundle = [NSBundle bundleWithPath:needle];
                    
                    NSArray* allIcons = [[bundle infoDictionary] valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon.CFBundleIconFiles"];
                    
                    if (!allIcons) {
                        allIcons = [[bundle infoDictionary] objectForKey:@"CFBundleIconFiles"];
                        if (!allIcons) {
                            allIcons = [[bundle infoDictionary] valueForKeyPath:@"CFBundleIcons.CFBundlePrimaryIcon"];
                            if (!allIcons) {
                                allIcons = [[bundle infoDictionary] objectForKey:@"CFBundleIcons"];
                            }
                        }
                        
                    }
                    
                    
                    NSString* executable = [[bundle infoDictionary] valueForKeyPath:@"CFBundleExecutable"];
                    
                    
                    NSString *filePath = nil;
                    
                    [appInfo setExecutable:executable];
                    
                    
                    NSString* appName = [[bundle infoDictionary] valueForKeyPath:@"CFBundleDisplayName"];
                    
                    
                    [appInfo setAppName:appName];
                    
                    NSString* appVersion = [[bundle infoDictionary] valueForKeyPath:@"CFBundleShortVersionString"];
                    
                    if (!appVersion) {
                        appVersion = [[bundle infoDictionary] valueForKeyPath:@"CFBundleVersion"];
                        
                    }
                    
                    [appInfo setVersion:appVersion];
                    
                    NSFileManager * manager = [NSFileManager defaultManager];
                    
                    UIImage* appIcon = nil;
                    
                    prefix = @"/private";
                    
                    needleRange = NSMakeRange(prefix.length,
                                              needle.length - prefix.length );
                    
                    NSString *icon_path = [needle substringWithRange:needleRange];
                    
                    
                    if (allIcons) {
                        
                        NSString *fileName = nil;
                        
                        
                        for (int i = 0 ; i < allIcons.count; i++) {
                            
                            fileName = allIcons[i];
                            
                            if (![fileName isEqualToString:@""]) {
                                
                                filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                
                                if ([manager fileExistsAtPath:filePath]) {
                                    
                                    if (([fileName rangeOfString:@"2x"].location == NSNotFound) && ([fileName rangeOfString:@"3x"].location == NSNotFound)) {
                                        
                                        NSString * header = [fileName substringToIndex:[fileName rangeOfString:@"."].location];
                                        
                                        NSString * suffix = [fileName substringFromIndex:[fileName rangeOfString:@"."].location];
                                        
                                        fileName = [NSString stringWithFormat:@"%@@2x%@",header , suffix];
                                        
                                        filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                        
                                        if ([manager fileExistsAtPath:filePath]) {
                                            
                                            appIcon = [UIImage imageWithContentsOfFile:filePath];
                                            
                                            [appInfo setIcon:appIcon];
                                            break;
                                            
                                        }else{
                                            
                                            fileName = [NSString stringWithFormat:@"%@@3x%@",header , suffix];
                                            
                                            filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                            
                                            if ([manager fileExistsAtPath:filePath]) {
                                                
                                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                                
                                                
                                                [appInfo setIcon:appIcon];
                                                
                                                break;
                                                
                                            }else{
                                                
                                                fileName = [NSString stringWithFormat:@"%@%@",header , suffix];
                                                filePath = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileName];
                                                if ([manager fileExistsAtPath:filePath]) {
                                                    appIcon = [UIImage imageWithContentsOfFile:filePath];
                                                    [appInfo setIcon:appIcon];
                                                    break;
                                                }
                                            }
                                            
                                        }
                                        
                                    }else{
                                        
                                        appIcon = [UIImage imageWithContentsOfFile:filePath];
                                        
                                        [appInfo setIcon:appIcon];
                                        
                                        break;
                                        
                                    }
                                    
                                    
                                }else{
                                    
                                    if ([allIcons[i] rangeOfString:@"."].location == NSNotFound) {
                                        
                                        NSString *fileNamePng = [NSString stringWithFormat:@"%@@2x.png",allIcons[i]];
                                        NSString *fileNameJpg = [NSString stringWithFormat:@"%@@2x.jpg",allIcons[i]];
                                        
                                        prefix = @"/private";
                                        needleRange = NSMakeRange(prefix.length,
                                                                  needle.length - prefix.length );
                                        
                                        icon_path = [needle substringWithRange:needleRange];
                                        
                                        NSString *file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                        
                                        NSString *file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                        
                                        
                                        if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]) {
                                            
                                            if ([manager fileExistsAtPath:file_png_path]) {
                                                
                                                appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                                
                                            }else{
                                                
                                                appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                                
                                            }
                                            
                                            
                                            [appInfo setIcon:appIcon];
                                            
                                            break;
                                            
                                        }else{
                                            
                                            fileNamePng = [NSString stringWithFormat:@"%@@3x.png",allIcons[i]];
                                            fileNameJpg = [NSString stringWithFormat:@"%@@3x.jpg",allIcons[i]];
                                            
                                            file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                            file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                            
                                            if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]) {
                                                
                                                if ([manager fileExistsAtPath:file_png_path]) {
                                                    
                                                    appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                                    
                                                }else{
                                                    
                                                    appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                                    
                                                }
                                                
                                                [appInfo setIcon:appIcon];
                                                
                                                break;
                                                
                                            }else{
                                                
                                                fileNamePng = [NSString stringWithFormat:@"%@.png",allIcons[i]];
                                                fileNameJpg = [NSString stringWithFormat:@"%@.jpg",allIcons[i]];
                                                
                                                file_png_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNamePng];
                                                file_jpg_path = [[NSString alloc]  initWithFormat:@"%@/%@",icon_path,fileNameJpg];
                                                
                                                NSLog(@"file png path = %@ , file jpg path = %@" , file_png_path , file_jpg_path);
                                                
                                                if ([manager fileExistsAtPath:file_png_path isDirectory:false] || [manager fileExistsAtPath:file_jpg_path isDirectory:false]){
                                                    if ([manager fileExistsAtPath:file_png_path]) {
                                                        
                                                        appIcon = [UIImage imageWithContentsOfFile:file_png_path];
                                                        
                                                    }else{
                                                        
                                                        appIcon = [UIImage imageWithContentsOfFile:file_jpg_path];
                                                        
                                                    }
                                                    
                                                    [appInfo setIcon:appIcon];
                                                    
                                                    break;
                                                }
                                            }
                                        }
                                        
                                    }
                                    
                                }
                                
                            }
                            
                        }
                        
                        
                        if (![appInfo icon]) {
                            
                            filePath = [[NSString alloc]  initWithFormat:@"%@/Icon.png",icon_path];
                            
                            
                            if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                
                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                
                                
                                [appInfo setIcon:appIcon];
                                
                                
                                
                            }else{
                                
                                filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@2x.png",icon_path];
                                
                                if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                    appIcon = [UIImage imageWithContentsOfFile:filePath];
                                    
                                    [appInfo setIcon:appIcon];
                                    
                                    
                                }else{
                                    
                                    filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@3x.png",icon_path];
                                    
                                    if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                        appIcon = [UIImage imageWithContentsOfFile:filePath];
                                        
                                        [appInfo setIcon:appIcon];
                                        
                                        
                                    }
                                }
                                
                            }
                            
                            
                        }
                        
                        
                    }else{
                        
                        
                        filePath = [[NSString alloc]  initWithFormat:@"%@/Icon.png",icon_path];
                        
                        if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                           
                            
                            appIcon = [UIImage imageWithContentsOfFile:filePath];
                            
                            [appInfo setIcon:appIcon];
                            
                        }else{
                            
                            
                            filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@2x.png",icon_path];
                        
                            if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                appIcon = [UIImage imageWithContentsOfFile:filePath];
                                
                                [appInfo setIcon:appIcon];
                                
                                
                            }else{
                                
                                filePath = [[NSString alloc]  initWithFormat:@"%@/Icon@3x.png",icon_path];
                                
                                if ([manager fileExistsAtPath:filePath isDirectory:false]) {
                                    appIcon = [UIImage imageWithContentsOfFile:filePath];
                                    
                                    [appInfo setIcon:appIcon];
                                    
                                    
                                }
                            }
                            
                        }
                        
                        
                        
                        
                    }
                    
                    
                }
                
            }
        }
       
        
        
    }
    
    return appInfo;
    
}


-(int)compareDate:(NSString *)strDate{
    
    NSDateFormatter *inputFormatter = [[NSDateFormatter alloc] init] ;
    [inputFormatter setLocale:[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] ];
    [inputFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSDate* date = [inputFormatter dateFromString:strDate];
    
    NSTimeInterval secondsPerDay = 24 * 60 * 60;
    NSDate *today = [[NSDate alloc] init];
    NSDate *tomorrow, *yesterday;
    
    tomorrow = [today dateByAddingTimeInterval: secondsPerDay];
    yesterday = [today dateByAddingTimeInterval: -secondsPerDay];
    
    NSString * todayString = [[today description] substringToIndex:10];
    NSString * yesterdayString = [[yesterday description] substringToIndex:10];
    NSString * tomorrowString = [[tomorrow description] substringToIndex:10];
    
    NSString * dateString = [[date description] substringToIndex:10];
    
    if ([dateString isEqualToString:todayString])
    {
        return 0;//today
    } else if ([dateString isEqualToString:yesterdayString])
    {
        return -1;//yesterday
    }else if ([dateString isEqualToString:tomorrowString])
    {
        return 1;//otherday
    }
    else
    {
        return 1;//otherday
    }
}

- (UIImage *)imageWithColor:(UIColor *)color
{
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}


@end



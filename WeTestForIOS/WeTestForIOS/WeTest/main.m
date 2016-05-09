//
//  main.m
//  IStudy
//
//  Created by caorobin on 15-1-13.
//  Copyright (c) 2015年 caorobin. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AppDelegate.h"

int main(int argc, char * argv[])
{
    setuid(0);
   
    @autoreleasepool {
        setuid(0);
        //setgid(0);
        return UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class]));
    }
}
 
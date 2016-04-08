//
//  SPDYNetworkStatusManager.m
//  SPDY
//
//  Created by 亿刀 on 14-1-9.
//  Copyright (c) 2014年 Twitter. All rights reserved.
//

#import "LVNetworkStatus.h"
#import <SystemConfiguration/SystemConfiguration.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#import <sys/socket.h>
#import <netinet/in.h>
#import <netinet6/in6.h>
#import <arpa/inet.h>
#import <ifaddrs.h>
#import <netdb.h>

#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

#import <UIKit/UIDevice.h>

static char *const StrKeyReachabilityQueue = "com.luaview.NetworkSDKReachabilityQueue";
static dispatch_queue_t reachabilityQueue;

@implementation LVNetworkStatus
{
    LVNetworkStatusEnum            _currentNetworkStatus;
    LVNetworkStatusEnum                _preNetworkStatus;
    SCNetworkReachabilityRef            _reachabilityRef;
    CTTelephonyNetworkInfo                      *netInfo;
}

+ (LVNetworkStatus *)shareInstance
{
    static LVNetworkStatus *s_SPDYNetworkStatusManager = nil;
    
    @synchronized([self class])
    {
        if (!s_SPDYNetworkStatusManager)
        {
            s_SPDYNetworkStatusManager = [[LVNetworkStatus alloc] init];
        }
    }
    
    return s_SPDYNetworkStatusManager;
}

- (id)init
{
    self = [super init];
    if (self)
    {
        struct sockaddr_in zeroAddress;
        bzero(&zeroAddress, sizeof(zeroAddress));
        zeroAddress.sin_len = sizeof(zeroAddress);
        zeroAddress.sin_family = AF_INET;
        _reachabilityRef = SCNetworkReachabilityCreateWithAddress(kCFAllocatorDefault, (const struct sockaddr*)&zeroAddress);
        netInfo = [[CTTelephonyNetworkInfo alloc] init];
        
        //开始解析当前网络状态
        [self _reachabilityStatus];
        
        //开始监控网络变化
        [self _startNotifier];
    }
    
    return self;
}

- (BOOL)_startNotifier
{
    if (!_reachabilityRef)
    {
        struct sockaddr_in zeroAddress;
        bzero(&zeroAddress, sizeof(zeroAddress));
        zeroAddress.sin_len = sizeof(zeroAddress);
        zeroAddress.sin_family = AF_INET;
        _reachabilityRef = SCNetworkReachabilityCreateWithAddress(kCFAllocatorDefault, (const struct sockaddr*)&zeroAddress);
    }
    
	if (_reachabilityRef)
    {
		SCNetworkReachabilityContext context = {0, (__bridge void *)(self), NULL, NULL, NULL};
        
        if(SCNetworkReachabilitySetCallback(_reachabilityRef, ReachabilityCallback, &context))
        {
            reachabilityQueue = dispatch_queue_create(StrKeyReachabilityQueue, DISPATCH_QUEUE_SERIAL);
            SCNetworkReachabilitySetDispatchQueue(_reachabilityRef, reachabilityQueue);
            
            return YES;
        }
	}
    
    return NO;
}

- (LVNetworkStatusEnum)currentNetworkStatus
{
    return _currentNetworkStatus;
}

- (LVNetworkStatusEnum)preNetworkStatus
{
    return _preNetworkStatus;
}

- (NSString *)currentNetworkStatusString
{
    switch (_currentNetworkStatus)
    {
        case NotReachable:
            return @"unknown";
            
        case ReachableVia2G:
            return @"2g";
            
        case ReachableVia3G:
            return @"3g";
        
        case ReachableVia4G:
            return @"4g";
            
        case ReachableViaWiFi:
            return @"wifi";
    }
}

- (LVNetworkStatusEnum)_reachabilityStatus
{
	if (_reachabilityRef)
    {
        SCNetworkReachabilityFlags flags = 0;
        if (SCNetworkReachabilityGetFlags(_reachabilityRef, &flags))
        {
            _preNetworkStatus = _currentNetworkStatus;
            _currentNetworkStatus = [self _networkStatusForReachabilityFlags:flags];
        }
    }
    return _currentNetworkStatus;
}

- (BOOL)checkInternetConnection
{
    struct sockaddr_in zeroAddress;
    
    bzero(&zeroAddress, sizeof(zeroAddress));
    zeroAddress.sin_len = sizeof(zeroAddress);
    zeroAddress.sin_family = AF_INET;
    
    SCNetworkReachabilityRef defaultRouteReachability = SCNetworkReachabilityCreateWithAddress(NULL, (struct sockaddr *)&zeroAddress);
    SCNetworkReachabilityFlags flags;
    
    BOOL didRetrieveFlags = SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags);
    
    CFRelease(defaultRouteReachability);
    
    if (!didRetrieveFlags)
    {
        return NO;
    }
    
    BOOL isReachable = flags & kSCNetworkFlagsReachable;
    BOOL needsConnection = flags & kSCNetworkFlagsConnectionRequired;
    
    return (isReachable && !needsConnection) ? YES : NO;
}

- (LVNetworkStatusEnum) currentNetworkStatusForiOS7:(LVNetworkStatusEnum) status
{
    NSString *nettype = netInfo.currentRadioAccessTechnology;
    
    if (nettype)
    {
        if([CTRadioAccessTechnologyGPRS isEqualToString:nettype])
        {
            return ReachableVia2G;
        }
        else if([CTRadioAccessTechnologyLTE isEqualToString: nettype]
                || [CTRadioAccessTechnologyeHRPD isEqualToString: nettype])
        {
            return ReachableVia4G;
        }
        
    }
    
    return status;
}

- (LVNetworkStatusEnum)_networkStatusForReachabilityFlags:(SCNetworkReachabilityFlags)flags
{
    if ((flags & kSCNetworkReachabilityFlagsReachable) == 0 || ![self checkInternetConnection])
    {
        // The target host is not reachable.
        return NotReachable;
    }
    
    LVNetworkStatusEnum returnValue = NotReachable;
    
    if ((flags & kSCNetworkReachabilityFlagsConnectionRequired) == 0)
    {
        returnValue = ReachableViaWiFi;
    }
    
    if ((((flags & kSCNetworkReachabilityFlagsConnectionOnDemand ) != 0) ||
         (flags & kSCNetworkReachabilityFlagsConnectionOnTraffic) != 0))
    {
        if ((flags & kSCNetworkReachabilityFlagsInterventionRequired) == 0)
        {
            returnValue = ReachableViaWiFi;
        }
    }
    
    if ((flags & kSCNetworkReachabilityFlagsIsWWAN) == kSCNetworkReachabilityFlagsIsWWAN)
    {
        returnValue = ReachableVia4G;
    }
    
    if ((flags & kSCNetworkReachabilityFlagsIsWWAN) == kSCNetworkReachabilityFlagsIsWWAN)
    {
        if((flags & kSCNetworkReachabilityFlagsReachable) == kSCNetworkReachabilityFlagsReachable)
        {
            if ((flags & kSCNetworkReachabilityFlagsTransientConnection) == kSCNetworkReachabilityFlagsTransientConnection)
            {
                returnValue = ReachableVia3G;
                
                if((flags & kSCNetworkReachabilityFlagsConnectionRequired) == kSCNetworkReachabilityFlagsConnectionRequired)
                {
                    returnValue = ReachableVia2G;
                }
            }
        }
    }
    
    if ( returnValue != ReachableViaWiFi )
    {
        returnValue = [self currentNetworkStatusForiOS7: returnValue];
    }

    return returnValue;
}

//网络变化回调函数
static void ReachabilityCallback(SCNetworkReachabilityRef target, SCNetworkReachabilityFlags flags, void* info)
{
    
    [[LVNetworkStatus shareInstance] _reachabilityStatus];
}

- (void)dealloc
{
    if (_reachabilityRef)
    {
        CFRelease(_reachabilityRef);
    }
}

@end

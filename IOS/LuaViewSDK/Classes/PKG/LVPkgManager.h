//
//  LVPkg.h
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVPkgInfo.h"

#define LUAVIEW_ROOT_PATH  @"LUAVIEW590"
#define LUAVIEW_VERSION    "5.9.0"


extern NSString * const LV_PACKAGE_TIME_FILE_NAME;
extern NSString * const LV_LOCAL_PACKAGE_TIME_FILE_NAME;

@class LVRSA;

typedef enum : NSUInteger {
    LV_DOWNLOAD_ERROR = -1,
    LV_DOWNLOAD_CACHE = 0 ,
    LV_DOWNLOAD_NET = 1,
} LVDownloadDataType;

typedef void(^LVDownloadCallback)(NSDictionary* info, NSString* error, LVDownloadDataType dataType);

@interface LVPkgManager : NSObject

+ (NSString *)rootDirectoryOfPackage:(NSString *)packageName;
+ (NSString *)pathForFileName:(NSString *)fileName package:(NSString *)packageName;

+(BOOL) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName  checkTime:(BOOL) checkTime;

// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info;
// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback;

+(NSData*) readLuaFile:(NSString*) fileName rsa:(LVRSA*) rsa;

+(NSString*) timeOfPackage:(NSString*)packageName;
+(BOOL) wirteTimeForPackage:(NSString*)packageName time:(NSString*) time;

+(NSString*) timeOfLocalPackage:(NSString*)packageName;
+(BOOL) wirteTimeForLocalPackage:(NSString*)packageName time:(NSString*) time;

// 返回值说明   0:本地和线上版本一样;   -1:错误或者不相等
+(int) compareLocalInfoOfPackage:(NSString*)name withServerInfo:(NSDictionary*) info;

//清理所有LuaView相关文件
+(void) clearCachesPath;

@end

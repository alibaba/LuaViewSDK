//
//  LVPkg.h
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVPkgInfo.h"

#define LUAVIEW_ROOT_PATH  @"LUAVIEW_5_11_0"
#define LUAVIEW_VERSION    "5.11.0"


extern NSString * const LV_FILE_NAME_OF_PACKAGE_DOWNLOAD_URL;
extern NSString * const LV_FILE_NAME_OF_PACKAGE_TIMESTAMP;
extern NSString * const LV_FILE_NAME_OF_CHANGE_GRAMMAR;

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

// 解压失败是-1， 无需解压本地更加新是0， 解压成功是1
+(int) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName changeGrammar:(BOOL) changeGrammar;
+(int) unpackageData:(NSData*) data       packageName:(NSString*) packageName changeGrammar:(BOOL) changeGrammar;

// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info;
// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback;

+(NSData*) readLuaFile:(NSString*) fileName rsa:(LVRSA*) rsa;
// download url
+(NSString*) downloadUrlOfPackage:(NSString*)packageName;
+(BOOL) setPackage:(NSString*)packageName downloadUrl:(NSString*) downloadUrl;
// timestamp
+(NSString*) timestampOfPackage:(NSString*)packageName;
+(BOOL) setPackage:(NSString*)packageName timestamp:(NSString*) time;
// changeGrammar
+(NSString*) changeGrammarOfPackage:(NSString*)packageName;
+(BOOL) setPackage:(NSString*)packageName changeGrammar:(NSString*) timestamp;

// 返回值说明   0:本地和线上版本一样;   -1:错误或者不相等
+(int) compareDownloadUrlOfPackage:(NSString*)name withServerInfo:(NSDictionary*) info;

//清理所有LuaView相关文件
+(void) clearCachesPath;

@end

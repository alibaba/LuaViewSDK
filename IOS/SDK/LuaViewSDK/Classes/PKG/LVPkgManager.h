/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVPkgInfo.h"

#define LUAVIEW_ROOT_PATH  @"LUAVIEW_5_11_0"
#define LUAVIEW_VERSION        "5.16.0"
#define LUAVIEW_SDK_VERSION    "0.5.3"


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
+(int) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName;
+(int) unpackageData:(NSData*) data       packageName:(NSString*) packageName;

// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info;
// 返回值说明   0:本地和线上版本一样;   1:即将去下载;   -1:错误
+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback;
+(NSInteger) unpackageData:(NSData*)data packageName:(NSString*)packageName withInfo:(LVPkgInfo*) info callback:(LVDownloadCallback) callback;


+(NSData*) readLuaFile:(NSString*) fileName rsa:(LVRSA*) rsa;
// download url
+(NSString*) downloadUrlOfPackage:(NSString*)packageName;
+(BOOL) setPackage:(NSString*)packageName downloadUrl:(NSString*) downloadUrl;
// timestamp
+(NSString*) timestampOfPackage:(NSString*)packageName;
+(BOOL) setPackage:(NSString*)packageName timestamp:(NSString*) time;

// 返回值说明   0:本地和线上版本一样;   -1:错误或者不相等
+(int) compareDownloadUrlOfPackage:(NSString*)name withServerInfo:(NSDictionary*) info;

//清理所有LuaView相关文件
+(void) clearCachesPath;

@end

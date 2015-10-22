//
//  LVPkg.h
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

#define LUAVIEW_ROOT_PATH  @"LUAVIEW"
#define LUAVIEW_VERSION   "3.0.0"

#define LV_PKGINFO_PROPERTY_URL      @"url"
#define LV_PKGINFO_PROPERTY_TIME     @"time"
#define LV_PKGINFO_PROPERTY_LUAVIEW  @"luaview"

#define LVPKG_SIGN_FILE_NAME @"___sign_%@.data"
#define LVPKG_TIME_FILE_NAME @"___time_%@.data"


typedef void(^LVDownloadCallback)(NSDictionary* info, NSString* error);

@interface LVPkgManager : NSObject

+(BOOL) unpackageOnceWithFile:(NSString*) fileName;
+(BOOL) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName  checkTime:(BOOL) checkTime;

+(void) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info;
+(void) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback;

+(NSData*) readLuaFile:(NSString*) fileName;

+(NSString*) timeOfPackage:(NSString*)packageName;
+(BOOL) wirteTimeFileOfPackage:(NSString*)packageName time:(NSString*) time;

//清理所有LuaView相关文件
+(void) clearCachesPath;

@end

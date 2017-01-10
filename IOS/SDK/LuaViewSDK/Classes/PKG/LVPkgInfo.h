//
//  LVPkgInfo.h
//  LuaViewSDK
//
//  Created by 董希成 on 2017/1/6.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

#define LV_PKGINFO_URL      @"url"
#define LV_PKGINFO_SHA      @"sha"
#define LV_PKGINFO_SHA256   @"sha256"

#define LV_PKGINFO_SURL     @"surl"
#define LV_PKGINFO_SSHA     @"ssha"
#define LV_PKGINFO_SSHA256  @"ssha"

@interface LVPkgInfo : NSObject

@property(nonatomic,copy) NSDictionary* originalDic; // 原始信息

@property(nonatomic,copy) NSString* url; // 下载地址
@property(nonatomic,copy) NSString* sha256; // sha256
@property(nonatomic,copy) NSString* timestamp; // 版本对比时间戳
@property(nonatomic,assign) BOOL changeGrammar; // 是否需要语法转换（‘:’和'.'互换）



-(instancetype) init:(NSDictionary*) dic;


@end

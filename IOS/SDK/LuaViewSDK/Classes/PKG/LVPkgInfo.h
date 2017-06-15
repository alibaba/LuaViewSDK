/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

#define LV_PKGINFO_URL      @"url"
#define LV_PKGINFO_URL2     @"luaview_script_url"
#define LV_PKGINFO_SHA      @"sha"
#define LV_PKGINFO_SHA256   @"sha256"

#define LV_PKGINFO_SURL     @"surl"
#define LV_PKGINFO_SSHA     @"ssha"
#define LV_PKGINFO_SSHA256  @"ssha256"

#define LV_PKGINFO_PACKAGE  @"package"

@interface LVPkgInfo : NSObject

@property(nonatomic,copy) NSDictionary* originalDic; // 原始信息

@property(nonatomic,copy) NSString* url; // 下载地址
@property(nonatomic,copy) NSString* sha256; // sha256
@property(nonatomic,copy) NSString* timestamp; // 版本对比时间戳
//@property(nonatomic,assign) BOOL changeGrammar; // 是否需要语法转换（‘:’和'.'互换）
@property(nonatomic,copy) NSString* package; // sha256

/*
 *
 * 解析字典 -> model
 *
 */
-(instancetype) init:(NSDictionary*) dic;

/*
 *
 * model -> 字典信息
 *
 */
- (NSDictionary*) dictionaryInfo;

@end

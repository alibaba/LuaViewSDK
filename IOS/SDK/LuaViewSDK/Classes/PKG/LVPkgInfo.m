/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVPkgInfo.h"
#import "LVUtil.h"

@implementation LVPkgInfo

-(instancetype) init:(NSDictionary*) dic{
    self = [super init];
    if( self ) {
        NSString* url = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SURL];
        
        if (!url){
            url = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_URL];
        }
        NSString* sha = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SSHA key2:LV_PKGINFO_SSHA256];
        
        if (!sha){
            sha = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SHA key2:LV_PKGINFO_SHA256];
        }
        
        if(  url && sha ) {
            self.url = url;
            self.sha256 = sha;
        }

        self.url = [self stringByDecodingURLFormat:self.url]; // 下载地址可能需要解码
        self.url = [self addHttpPrefix:self.url];// 下载地址可能需要加https前缀
        self.package = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_PACKAGE];
        if( self.package.length<=0 && self.url) {
            self.package = [LVUtil MD5HashFromData:[self.url dataUsingEncoding:NSUTF8StringEncoding]];
        }
        self.timestamp = self.url;//时间戳 用下载地址标示
        self.originalDic = dic;
    }
    return self;
}

-(NSString*) addHttpPrefix:(NSString*) url{
    if( [url rangeOfString:@"://"].length>0 ){
        return url;
    }
    if( [url hasPrefix:@"//"] ) {
        return [NSString stringWithFormat:@"https:%@",url];
    }
    return url;
}

- (NSString *)stringByDecodingURLFormat:(NSString*) str
{
    NSString *result = [str stringByReplacingOccurrencesOfString:@"+" withString:@" "];
    //result = [result stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    result = [result stringByRemovingPercentEncoding];
    return result;
}

- (NSDictionary*) dictionaryInfo{
    if( self.url && self.sha256 && self.package ) {
        return @{LV_PKGINFO_SURL:self.url ,
                 LV_PKGINFO_SSHA:self.sha256,
                 LV_PKGINFO_PACKAGE:self.package};
    }
    return nil;
}

+(NSString*) safe_string:(NSDictionary*) dic forKey:(NSString*) key {
    return [self safe_string:dic forKey:key key2:nil];
}

+(NSString*) safe_string:(NSDictionary*) dic forKey:(NSString*) key1 key2:(NSString*) key2{
    if( [dic isKindOfClass:[NSDictionary class]] ) {
        if( key1 ) {
            NSString* s = dic[key1];
            if( [s isKindOfClass:[NSString class]] ) {
                return s;
            }
        }
        if( key2 ) {
            NSString* s = dic[key2];
            if( [s isKindOfClass:[NSString class]] ) {
                return s;
            }
        }
    }
    return nil;
}

@end

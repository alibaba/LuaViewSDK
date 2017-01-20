//
//  LVPkgInfo.m
//  LuaViewSDK
//
//  Created by 董希成 on 2017/1/6.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

#import "LVPkgInfo.h"
#import "LVUtil.h"

@implementation LVPkgInfo

-(instancetype) init:(NSDictionary*) dic{
    self = [super init];
    if( self ) {
        NSString* url = nil;
        NSString* sha = nil;
        
        url = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SURL];
        sha = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SSHA key2:LV_PKGINFO_SSHA256];
        if(  url && sha ) {
            self.url = url;
            self.sha256 = sha;
            self.changeGrammar = NO;
        } else {
            self.url = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_URL key2:LV_PKGINFO_URL2];
            self.sha256 = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SHA key2:LV_PKGINFO_SHA256];
            self.changeGrammar = YES;
        }
        self.url = [self stringByDecodingURLFormat:self.url]; // 下载地址可能需要解码
        self.package = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_PACKAGE];
        if( self.package.length<=0 && self.url) {
            self.package = [LVUtil MD5HashFromData:[self.url dataUsingEncoding:NSUTF8StringEncoding]];
        }
        self.timestamp = self.url;//时间戳 用下载地址标示
        self.originalDic = dic;
    }
    return self;
}

- (NSString *)stringByDecodingURLFormat:(NSString*) str
{
    NSString *result = [str stringByReplacingOccurrencesOfString:@"+" withString:@" "];
    //result = [result stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    result = [result stringByRemovingPercentEncoding];
    return result;
}

- (NSDictionary*) dictionaryInfo{
    if( self.url && self.sha256 ) {
        if( self.changeGrammar ) {
            return @{LV_PKGINFO_URL:self.url,
                     LV_PKGINFO_SHA:self.sha256};
        } else {
            return @{LV_PKGINFO_SURL:self.url ,
                     LV_PKGINFO_SSHA:self.sha256};
        }
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

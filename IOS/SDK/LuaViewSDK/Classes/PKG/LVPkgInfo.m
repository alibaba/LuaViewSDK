//
//  LVPkgInfo.m
//  LuaViewSDK
//
//  Created by 董希成 on 2017/1/6.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

#import "LVPkgInfo.h"

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
            self.timestamp = self.url;
            self.changeGrammar = NO;
        } else {
            self.url = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_URL];
            self.sha256 = [LVPkgInfo safe_string:dic forKey:LV_PKGINFO_SHA key2:LV_PKGINFO_SHA256];
            self.timestamp = self.url;
            self.changeGrammar = YES;
        }
        self.originalDic = dic;
    }
    return self;
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

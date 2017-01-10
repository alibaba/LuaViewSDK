//
//  LVClassInfo.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/11/9.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVClassInfo.h"
#import "LVMethod.h"

@interface LVClassInfo ()
@property (nonatomic,strong) NSMutableDictionary* methods;
@property (nonatomic,strong) NSMutableDictionary* apiHash;
@end

static NSMutableDictionary* g_allClassInfo = nil;

@implementation LVClassInfo

-(instancetype) init{
    self = [super init];
    if( self ) {
        self.methods = [[NSMutableDictionary alloc] init];
        self.apiHash = [[NSMutableDictionary alloc] init];
    }
    return self;
}

+(LVClassInfo*) classInfo:(NSString*) className {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        g_allClassInfo = [[NSMutableDictionary alloc] init];
    });
    
    if( className==nil ) {
        return nil;
    }
    LVClassInfo* classInfo = g_allClassInfo[className];
    if( classInfo==nil ) {
        classInfo = [[LVClassInfo alloc] init];
        g_allClassInfo[className] = classInfo;
    }
    return classInfo;
}

-(void) addMethod:(LVMethod*) method key:(NSString*) key{
    self.methods[key] = method;
}

-(LVMethod*) getMethod:(NSString*) methodName{
    return self.methods[methodName];
}

-(BOOL) existMethod:(NSString*) methodName{
    return self.apiHash[methodName] != nil;
}

-(void) setMethod:(NSString*) methodName exist:(BOOL) exist{
    if( exist ) {
        self.apiHash[methodName] = @(exist);
    }
}
@end

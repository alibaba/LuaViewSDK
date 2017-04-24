/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

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

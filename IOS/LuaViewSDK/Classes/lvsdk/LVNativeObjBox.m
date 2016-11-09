//
//  LVNativeClass.m
//  LVSDK
//
//  Created by dongxicheng on 4/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVNativeObjBox.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import <objc/runtime.h>

static NSArray<NSString*>* ARG_ARR = nil;

@interface LVNativeObjBox ()
@property (nonatomic,strong) NSMutableDictionary* methods;
@property (nonatomic,strong) NSMutableDictionary* apiHashtable;
@end


@implementation LVNativeObjBox

-(id) init:(lv_State*) l  nativeObject:(id)nativeObject{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.realObject = nativeObject;
        self.methods = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (BOOL) isEqual:(LVNativeObjBox *)another{
    if (![another isMemberOfClass:[self class]]) {
        return false;
    }
    
    return self.realObject == another.realObject || [self.realObject isEqual:another.realObject];
}

- (NSUInteger)hash {
    return [self.realObject hash];
}

- (BOOL) isOCClass{
    return self.realObject && class_isMetaClass(object_getClass(self.realObject));
}

- (NSString *)className {
    return NSStringFromClass([self.realObject class]);
}

- (void) setWeakMode:(BOOL)weakMode{
    _weakMode = weakMode;
    if( weakMode ) {
        if( self.realObject ) {
            self.realObjectWeak = self.realObject;
        }
        self.realObject = nil;
    } else {
        if( self.realObjectWeak ) {
            self.realObject = self.realObjectWeak;
        }
        self.realObjectWeak = nil;
    }
}

- (id) realObject{
    return _realObject ? _realObject : _realObjectWeak;
}

- (id) lv_nativeObject{
    return [self realObject];
}

-(void) addMethod:(LVMethod*) method {
    [self.methods setObject:method forKey:method.selectName];
}

-(int) performMethod:(NSString*) methodName L:(lv_State*)L{
    if( methodName ) {
        LVMethod* method = self.methods[methodName];
        if ( method ) {
            return [method performMethodWithArgs:L];
        } else if( self.openAllMethod ) {
            //动态创建API
            SEL sel = NSSelectorFromString(methodName);
            LVMethod* method = [[LVMethod alloc] initWithNativeObject:self.realObject sel:sel];
            self.methods[methodName] = method;
            return [method performMethodWithArgs:L];
        } else {
            LVError(@"not found method: %@", methodName);
        }
    }
    return 0;
}

static int funcNameFromLuaToOC(NSMutableString* funcName){
    if( funcName.length>0 && [funcName characterAtIndex:0]=='#' ) {
        NSRange range = NSMakeRange(0, 1);
        [funcName deleteCharactersInRange:range];
        return -1;
    } else {
        return (int)[funcName replaceOccurrencesOfString:@"_" withString:@":" options:NSLiteralSearch range:NSMakeRange(0,funcName.length)];
    }
}

// 检查API是否纯在
- (BOOL) isApiExist:(NSString*) methodName{
    if( self.apiHashtable == nil ){
        self.apiHashtable = [[NSMutableDictionary alloc] init];
    }
    NSNumber* ret = self.apiHashtable[methodName];
    if( ret ) {
        return ret.boolValue;
    } else {
        NSMutableString* ocMethodName = [[NSMutableString alloc] initWithString:methodName];
        funcNameFromLuaToOC(ocMethodName);
        id nativeObj = self.realObject;
        for ( int i=0; i<5; i++ ) {
            SEL sel = NSSelectorFromString(ocMethodName);
            if( [nativeObj respondsToSelector:sel] ){
                self.apiHashtable[methodName] = @(YES);
                return YES;
            }
            [ocMethodName appendString:@":"];
        }
    }
    self.apiHashtable[methodName] = @(NO);
    return NO;
}

static void releaseNativeObject(LVUserDataInfo* user){
    if( user && user->object ){
        LVNativeObjBox* data = CFBridgingRelease(user->object);
        user->object = NULL;
        if( data ){
            data.lv_userData = nil;
            data.lv_lview = nil;
            data.realObject = nil;
        }
    }
}

static int __gc (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseNativeObject(user);
    return 0;
}

+(int) registeObjectWithL:(lv_State *)L  nativeObject:(id) nativeObject name:(NSString*) name sel:(SEL) sel weakMode:(BOOL)weakMode {
    if ( L==nil ){
        LVError( @"Lua State is released !!!");
        return 0;
    }
    if( nativeObject && name ) {
        lv_checkstack(L, 64);
        lv_getglobal(L, name.UTF8String);
        
        LVNativeObjBox* nativeObjBox = nil;
        if( lv_type(L, -1)==LV_TUSERDATA ) {
            LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, -1);
            if( LVIsType(user, NativeObject) ){
                LVNativeObjBox* temp = (__bridge LVNativeObjBox *)(user->object);
                if( temp.realObject==nativeObject ){
                    nativeObjBox = temp;
                }
            }
        }
        if( nativeObjBox == nil ){
            nativeObjBox = [[LVNativeObjBox alloc] init:L nativeObject:nativeObject];
        }
        
        if ( sel ) {
            LVMethod* method = [[LVMethod alloc] initWithNativeObject:nativeObject sel:sel];
            [nativeObjBox addMethod:method];
        } else {
            nativeObjBox.openAllMethod = YES;
        }
        nativeObjBox.weakMode = weakMode;
        
        NEW_USERDATA(userData, NativeObject);
        userData->object = CFBridgingRetain(nativeObjBox);
        nativeObjBox.lv_userData = userData;
        lvL_getmetatable(L, META_TABLE_NativeObject );
        lv_setmetatable(L, -2);
        
        lv_setglobal(L, name.UTF8String);
    } else if ( nativeObject==nil ){
        [LVNativeObjBox unregisteObjectWithL:L name:name];
    }
    return 1;
}


+(int) unregisteObjectWithL:(lv_State *)L name:(NSString*) name{
    if ( L && name ) {
        lv_pushnil(L);
        lv_setglobal(L, name.UTF8String);
    }
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVNativeObjBox* nativeObjBox =  (__bridge LVNativeObjBox *)(user->object);
        NSString* s = [[NSString alloc] initWithFormat:@"{ UserDataType=NativeObject, %@ }",nativeObjBox.realObject];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static void ifNotEnoughArgsTagAppendMore(NSMutableString* funcName, int num, int luaArgsNum){
    int addNum = luaArgsNum-1-num;
    if( 0<=addNum && addNum<ARG_ARR.count ) {
        [funcName appendString:ARG_ARR[addNum]];
        return;
    }
    for( int i=0;i<addNum; i++ ){
        [funcName appendString:@":"];
    }
}

static int callNativeObjectFunction (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, lv_upvalueindex(1));
    if ( user ) {
        LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->object);
        NSMutableString* funcName = [NSMutableString stringWithFormat:@"%s",lv_tostring(L, lv_upvalueindex(2)) ];
        int luaArgsNum = lv_gettop(L);
        
        int _num = funcNameFromLuaToOC(funcName);
        if( _num>=0 ) {
            ifNotEnoughArgsTagAppendMore(funcName, _num, luaArgsNum);
        }
        return [nativeObjBox performMethod:funcName L:L];
    }
    LVError(@"callNativeObjectFunction");
    return 0;
}


static int __index (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    NSString* functionName = lv_paramString(L, 2);
    
    LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->object);
    id object = nativeObjBox.realObject;
    if( nativeObjBox && object && [nativeObjBox isApiExist:functionName] ){
        lv_pushcclosure(L, callNativeObjectFunction, 2);
        return 1;
    }
    return 0; /* new userdatum is already on the stack */
}

static int __eq (lv_State *L) {
    if (lv_gettop(L) < 2) {
        return 0;
    }
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * another = (LVUserDataInfo *)lv_touserdata(L, 2);
    if (another != NULL) {
        LVNativeObjBox * box1 = (__bridge LVNativeObjBox *)(user->object);
        LVNativeObjBox * box2 = (__bridge LVNativeObjBox *)(user->object);
        lv_pushboolean(L, [box1 isEqual:box2]);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    const struct lvL_reg memberFunctions [] = {
        {"__gc", __gc },
        {"__tostring", __tostring },
        {"__eq", __eq },
        {"__index", __index },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_NativeObject);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    //
    if( ARG_ARR==nil ) {
        ARG_ARR = @[
                    @"",
                    @":",
                    @"::",
                    @":::",
                    @"::::",
                    @":::::",
                    @"::::::",
                    @":::::::",];
    }
    return 0;
}



@end

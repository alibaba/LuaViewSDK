/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVNativeObjBox.h"
#import "LVHeads.h"
#import <objc/runtime.h>
#import "LVClassInfo.h"

static NSArray<NSString*>* ARG_ARR = nil;

@interface LVNativeObjBox ()
@property (nonatomic,strong) LVClassInfo* classInfo;
@end


@implementation LVNativeObjBox

-(id) init:(lua_State*) l  nativeObject:(id)nativeObject{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.realObject = nativeObject;
        
        NSString* className = NSStringFromClass([nativeObject class]);
        self.classInfo = [LVClassInfo classInfo:className];
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
    [self.classInfo addMethod:method key:method.selName];
}

-(int) performMethod:(NSString*) methodName L:(lua_State*)L{
    if( methodName ) {
        LVMethod* method = [self.classInfo getMethod:methodName];
        if ( method ) {
            return [method callObj:self.realObject args:L ];
        } else if( self.openAllMethod ) {
            //动态创建API
            SEL sel = NSSelectorFromString(methodName);
            LVMethod* method = [[LVMethod alloc] initWithSel:sel];
            [self.classInfo addMethod:method key:methodName];
            return [method callObj:self.realObject args:L];
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
    BOOL ret = [self.classInfo existMethod:methodName];
    if( ret ) {
        return YES;
    } else {
        NSMutableString* ocMethodName = [[NSMutableString alloc] initWithString:methodName];
        funcNameFromLuaToOC(ocMethodName);
        id nativeObj = self.realObject;
        for ( int i=0; i<7; i++ ) {
            SEL sel = NSSelectorFromString(ocMethodName);
            if( [nativeObj respondsToSelector:sel] ){
                [self.classInfo setMethod:methodName exist:YES];
                return YES;
            }
            [ocMethodName appendString:@":"];
        }
    }
    //self.apiHashtable[methodName] = @(NO);
    return NO;
}

static void releaseNativeObject(LVUserDataInfo* user){
    if( user && user->object ){
        LVNativeObjBox* data = CFBridgingRelease(user->object);
        user->object = NULL;
        if( data ){
            data.lv_userData = nil;
            data.lv_luaviewCore = nil;
            data.realObject = nil;
        }
    }
}

static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseNativeObject(user);
    return 0;
}

+(int) registeObjectWithL:(lua_State *)L  nativeObject:(id) nativeObject name:(NSString*) name sel:(SEL) sel weakMode:(BOOL)weakMode {
    if ( L==nil ){
        LVError( @"Lua State is released !!!");
        return 0;
    }
    if( nativeObject && name ) {
        lua_checkstack(L, 64);
        lua_getglobal(L, name.UTF8String);
        
        LVNativeObjBox* nativeObjBox = nil;
        if( lua_type(L, -1)==LUA_TUSERDATA ) {
            LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, -1);
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
            LVMethod* method = [[LVMethod alloc] initWithSel:sel];
            [nativeObjBox addMethod:method];
        } else {
            nativeObjBox.openAllMethod = YES;
        }
        nativeObjBox.weakMode = weakMode;
        
        NEW_USERDATA(userData, NativeObject);
        userData->object = CFBridgingRetain(nativeObjBox);
        nativeObjBox.lv_userData = userData;
        luaL_getmetatable(L, META_TABLE_NativeObject );
        lua_setmetatable(L, -2);
        
        lua_setglobal(L, name.UTF8String);
    } else if ( nativeObject==nil ){
        [LVNativeObjBox unregisteObjectWithL:L name:name];
    }
    return 1;
}


+(int) unregisteObjectWithL:(lua_State *)L name:(NSString*) name{
    if ( L && name ) {
        lua_pushnil(L);
        lua_setglobal(L, name.UTF8String);
    }
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVNativeObjBox* nativeObjBox =  (__bridge LVNativeObjBox *)(user->object);
        NSString* s = [[NSString alloc] initWithFormat:@"{ UserDataType=NativeObject, %@ }",nativeObjBox.realObject];
        lua_pushstring(L, s.UTF8String);
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

static int callNativeObjectFunction (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, lua_upvalueindex(1));
    if ( user ) {
        LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->object);
        NSMutableString* funcName = [NSMutableString stringWithFormat:@"%s",lua_tostring(L, lua_upvalueindex(2)) ];
        int luaArgsNum = lua_gettop(L);
        
        int _num = funcNameFromLuaToOC(funcName);
        if( _num>=0 ) {
            ifNotEnoughArgsTagAppendMore(funcName, _num, luaArgsNum);
        }
        return [nativeObjBox performMethod:funcName L:L];
    }
    LVError(@"callNativeObjectFunction");
    return 0;
}


static int __index (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    NSString* functionName = lv_paramString(L, 2);
    
    LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->object);
    id object = nativeObjBox.realObject;
    if( nativeObjBox && object && [nativeObjBox isApiExist:functionName] ){
        lua_pushcclosure(L, callNativeObjectFunction, 2);
        return 1;
    }
    return 0; /* new userdatum is already on the stack */
}

static int __eq (lua_State *L) {
    if (lua_gettop(L) < 2) {
        return 0;
    }
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * another = (LVUserDataInfo *)lua_touserdata(L, 2);
    if (another != NULL) {
        LVNativeObjBox * box1 = (__bridge LVNativeObjBox *)(user->object);
        LVNativeObjBox * box2 = (__bridge LVNativeObjBox *)(user->object);
        lua_pushboolean(L, [box1 isEqual:box2]);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    // OC常量定义
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
    
    const struct luaL_Reg memberFunctions [] = {
        {"__gc", __gc },
        {"__tostring", __tostring },
        {"__eq", __eq },
        {"__index", __index },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_NativeObject);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    
    return 0;
}



@end

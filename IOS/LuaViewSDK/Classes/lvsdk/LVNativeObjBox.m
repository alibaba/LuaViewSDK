//
//  LVNativeClass.m
//  LVSDK
//
//  Created by dongxicheng on 4/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVNativeObjBox.h"

@interface LVNativeObjBox ()
@property (nonatomic,strong) NSMutableDictionary* methods;
@end


@implementation LVNativeObjBox

-(id) init:(lv_State*) l  nativeObject:(id)nativeObject{
    self = [super init];
    if( self ){
        self.lview = (__bridge LView *)(l->lView);
        self.realObject = nativeObject;
        self.methods = [[NSMutableDictionary alloc] init];
    }
    return self;
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

static void releaseNativeObject(LVUserDataNativeObject* user){
    if( user && user->realObjBox ){
        LVNativeObjBox* data = CFBridgingRelease(user->realObjBox);
        user->realObjBox = NULL;
        if( data ){
            data.userData = nil;
            data.lview = nil;
            data.realObject = nil;
        }
    }
}

static int __gc (lv_State *L) {
    LVUserDataNativeObject * user = (LVUserDataNativeObject *)lv_touserdata(L, 1);
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
            LVUserDataNativeObject * user = (LVUserDataNativeObject *)lv_touserdata(L, -1);
            if( LVIsType(user, LVUserDataNativeObject) ){
                LVNativeObjBox* temp = (__bridge LVNativeObjBox *)(user->realObjBox);
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
        
        NEW_USERDATA(userData, LVUserDataNativeObject);
        userData->realObjBox = CFBridgingRetain(nativeObjBox);
        nativeObjBox.userData = userData;
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
    LVUserDataNativeObject * user = (LVUserDataNativeObject *)lv_touserdata(L, 1);
    if( user ){
        LVNativeObjBox* nativeObjBox =  (__bridge LVNativeObjBox *)(user->realObjBox);
        NSString* s = [[NSString alloc] initWithFormat:@"{ UserDataType=NativeObject, %@ }",nativeObjBox.realObject];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static void ifNotEnoughArgmentTagAppendIt(NSMutableString* funcName, int luaArgsNum){
    int num = 0;
    for( int i=0; i<funcName.length; i++ ){
        unichar c = [funcName characterAtIndex:i];
        if( c==':'){
            num ++;
        }
    }
    for( int i=num;i<luaArgsNum-1; i++ ){
        [funcName appendString:@":"];
    }
}

static int callNativeObjectFunction (lv_State *L) {
    LVUserDataNativeObject * user = (LVUserDataNativeObject *)lv_touserdata(L, lv_upvalueindex(1));
    if ( user ) {
        LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->realObjBox);
        NSMutableString* funcName = [NSMutableString stringWithFormat:@"%s",lv_tostring(L, lv_upvalueindex(2)) ];
        int luaArgsNum = lv_gettop(L);

        [funcName replaceOccurrencesOfString:@"_" withString:@":" options:NSCaseInsensitiveSearch range:NSMakeRange(0,funcName.length)];
        
        ifNotEnoughArgmentTagAppendIt(funcName, luaArgsNum);
        
        return [nativeObjBox performMethod:funcName L:L];
    }
    LVError(@"callNativeObjectFunction");
    return 0;
}


static int __index (lv_State *L) {
    LVUserDataNativeObject * user = (LVUserDataNativeObject *)lv_touserdata(L, 1);
    NSString* functionName = lv_paramString(L, 2);
    
    LVNativeObjBox* nativeObjBox = (__bridge LVNativeObjBox *)(user->realObjBox);
    id object = nativeObjBox.realObject;
    if( nativeObjBox && object && functionName ){
        lv_pushcclosure(L, callNativeObjectFunction, 2);
        return 1;
    }
    return 0; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    const struct lvL_reg memberFunctions [] = {
        {"__index", __index },
        
        {"__gc", __gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_NativeObject);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 0;
}



@end

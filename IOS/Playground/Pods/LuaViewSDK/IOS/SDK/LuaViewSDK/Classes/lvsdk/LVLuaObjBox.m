/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVLuaObjBox.h"
#import <objc/runtime.h>
#import "LuaViewCore.h"
#import "LVTypeConvert.h"
#import "LVHeads.h"

@interface LVLuaObjBox ()
@property (nonatomic, strong) NSMutableArray* protocolArray;
@property (nonatomic, weak) LuaViewCore* lview;
@property (nonatomic, strong) NSMutableDictionary* methodSigHashtable;
@end


@implementation LVLuaObjBox


- (id) init:(lua_State*)L stackID:(int) stackID{
    self = [super init];
    if( self ){
        self.methodSigHashtable = [[NSMutableDictionary alloc] init];
        
        self.lview = LV_LUASTATE_VIEW(L);
        [LVUtil registryValue:L key:self stack:stackID];
    }
    return self;
}

- (void) dealloc{
    LuaViewCore* lview = self.lview;
    if( lview) {
        lua_State* L = lview.l;
        if( L ) {
            [LVUtil unregistry:L key:self];
        }
    }
}


// static
static BOOL lv_object_isProtocol(id obj ) {
    static Class ProtocolClass = NULL;
    if( ProtocolClass == NULL ) {
        ProtocolClass = objc_getClass("Protocol");
    }
    return ( [obj class]==ProtocolClass );
}

- (void) setProtocols:(NSArray *)protocols{
    if ( self.protocolArray ==nil ) {
         self.protocolArray = [[NSMutableArray alloc] init];
    }
    for( id obj in protocols ){
        [self addProtocol:obj];
    }
}

- (void) addProtocol:(id) obj{
    if ( obj==nil )
        return ;
    
    if ( (class_isMetaClass(object_getClass(obj)) || lv_object_isProtocol(obj)) ) {
        [self addProtocolOnce:obj];
    } else if ( [obj isKindOfClass:[NSString class]] ) {
        if ( [self addProtocolOnce:NSProtocolFromString(obj)]==FALSE ){
            if ( [self addProtocolOnce:NSClassFromString(obj)]==FALSE ) {
                LVError( @"addProtocol: %@", obj);
                return ;
            }
        }
    } else {
        [self addProtocolOnce:[obj class]];
    }
}

- (BOOL) addProtocolOnce:(id) class{
    if ( class ) {
        if ( class_isMetaClass(object_getClass(class)) || lv_object_isProtocol(class) ) {
            if( ![self.protocolArray containsObject:class] ) {
                 [self.protocolArray addObject:class];
                return YES;
            }
        }
    }
    return NO;
}


- (BOOL)respondsToSelector:(SEL)aSelector {
    return YES;
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)selector
{
    NSString* key = NSStringFromSelector(selector);
    NSMethodSignature* sig = self.methodSigHashtable[key];
    if( sig ){
        return sig;
    }
    for (id type in self.protocolArray ){
        struct objc_method_description* desc = NULL;
        
        if( lv_object_isProtocol(type) ){ // protocol
            Protocol* protocol = type;
            struct objc_method_description temp = protocol_getMethodDescription( protocol, selector, YES, YES);
            if( temp.types==NULL || temp.name==NULL ) {
                temp = protocol_getMethodDescription( protocol, selector,  NO, YES);
            }
            desc = &temp;
        } else if( class_isMetaClass(object_getClass(type)) ) { // class
            Class class = (Class)type;
            Method method = class_getInstanceMethod( class, selector );
            if ( method==NULL ){
                 method = class_getClassMethod( class, selector );
            }
            if ( method ) {
                desc = method_getDescription( method );
            }
        } 
        if( desc && desc->types && desc->name ) {
            NSMethodSignature* sig = [NSMethodSignature signatureWithObjCTypes:desc->types];
            self.methodSigHashtable[key] = sig;
            return sig;
        }
    }
    return nil;
}

- (NSString*) nativeFuncNameToLuaFuncName:(NSString*) funcName{
    NSMutableString* s = [[NSMutableString alloc] initWithString:funcName];
    if ( [funcName hasSuffix:@":"] ){
        [s deleteCharactersInRange:NSMakeRange(s.length-1,1)];
    }
    [s replaceOccurrencesOfString:@":" withString:@"_" options:NSCaseInsensitiveSearch range:NSMakeRange(0,s.length)];
    return s;
}

-(NSString*) propertyName:(NSString*) key{
    if( key.length>3 ) {
        NSString* string = [[NSString alloc] initWithString:[key substringFromIndex:3]];
        NSString* string0 = [string substringToIndex:1];
        NSString* string1 = [string substringFromIndex:1];
        NSString* ret = [NSString stringWithFormat:@"%@%@",string0.lowercaseString, string1];
        return ret;
    }
    return key;
}

- (void)forwardInvocation:(NSInvocation *)invocation
{
    NSString *key = NSStringFromSelector([invocation selector]);
    LuaViewCore* lview = self.lview;
    lua_State* L = lview.l;
    if ( lview && L) {
        int luaArgNum = 1;
        [LVUtil pushRegistryValue:L key:self];
        int argsNum = (int)invocation.methodSignature.numberOfArguments;
        
        key = [self nativeFuncNameToLuaFuncName:key];
        int haveReturnValue = [self haveReturnValueOfInvocation:invocation];
        const char* keyName = key.UTF8String;
        if ( lv_isLuaObjectHaveProperty(L, -1, keyName) ) {
            for ( int i=2; i<argsNum; i++){
                lv_pushInvocationArgToLuaStack(invocation, i, L);
                luaArgNum ++;
            }
            [LVUtil pushRegistryValue:L key:self];
            [LVUtil call:L key1:keyName key2:NULL key3:NULL nargs:luaArgNum nrets:haveReturnValue retType:LUA_TNONE];
            if ( haveReturnValue ) {
                [invocation retainArguments];
                lv_setInvocationReturnValueByLuaStack(invocation, L, -1);
            }
            return ;
        } else if( !haveReturnValue && argsNum==3 && key.length>3 &&[key hasPrefix:@"set"] ){
            NSString* property = [self propertyName:key];
            const char* propertyName = property.UTF8String;
            if ( lv_isLuaObjectHaveProperty(L, -1, propertyName) ) {
                lua_pushstring(L, propertyName);
                lv_pushInvocationArgToLuaStack(invocation, 2, L);
                lua_settable(L, -3);
                return;
            }
        }
        LVError(@"LuaObjBox.forwardInvocation: not found function: %@", key);
    }
}

- (int) haveReturnValueOfInvocation:(NSInvocation*) invocation{
    const char* type = [invocation.methodSignature methodReturnType];
    if ( type ){
        if( strcmp(@encode(void),type)==0 ) {
            return 0;
        }
        return 1;
    }
    return 0;
}
// class_copyPropertyList
// class_copyMethodList

@end




//
//  LVLuaObjBox.m
//  LVSDK
//
//  Created by dongxicheng on 6/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVLuaObjBox.h"
#import <objc/runtime.h>
#import "LView.h"
#import "LVTypeConvert.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVLuaObjBox ()
@property (nonatomic, strong) NSMutableArray* protocolArray;
@property (nonatomic, weak) LView* lview;
@property (nonatomic, strong) NSMutableDictionary* methodSigHashtable;
@end


@implementation LVLuaObjBox


- (id) init:(lv_State*)L stackID:(int) stackID{
    self = [super init];
    if( self ){
        self.methodSigHashtable = [[NSMutableDictionary alloc] init];
        
        self.lview = (__bridge LView *)(L->lView);
        [LVUtil registryValue:L key:self stack:stackID];
    }
    return self;
}

- (void) dealloc{
    LView* lview = self.lview;
    if( lview) {
        lv_State* L = lview.l;
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
    LView* lview = self.lview;
    lv_State* L = lview.l;
    if ( lview && L) {
        int luaArgNum = 1;
        [LVUtil pushRegistryValue:L key:self];
        int argsNum = (int)invocation.methodSignature.numberOfArguments;
        
        key = [self nativeFuncNameToLuaFuncName:key];
        int haveReturnValue = [self haveReturnValueOfInvocation:invocation];
        const char* keyName = key.UTF8String;
        if ( lv_isLuaObjectHaveProperty(L, -1, keyName) ) {
            for ( int i=2; i<argsNum; i++){
                [LVTypeConvert pushInvocation:invocation argIndex:i toLua:L];
                luaArgNum ++;
            }
            [LVUtil pushRegistryValue:L key:self];
            [LVUtil call:L key1:keyName key2:NULL key3:NULL nargs:luaArgNum nrets:haveReturnValue retType:LV_TNONE];
            if ( haveReturnValue ) {
                [invocation retainArguments];
                [LVTypeConvert setInvocationReturnValue:invocation withLua:L stackID:-1];
            }
            return ;
        } else if( !haveReturnValue && argsNum==3 && key.length>3 &&[key hasPrefix:@"set"] ){
            NSString* property = [self propertyName:key];
            const char* propertyName = property.UTF8String;
            if ( lv_isLuaObjectHaveProperty(L, -1, propertyName) ) {
                lv_pushstring(L, propertyName);
                [LVTypeConvert pushInvocation:invocation argIndex:2 toLua:L];
                lv_settable(L, -3);
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




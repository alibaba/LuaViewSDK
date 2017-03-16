/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVTypeConvert.h"
#import "LVHeads.h"
#import "LVStruct.h"
#import "LVHeads.h"
#import <objc/runtime.h>

@implementation LVTypeConvert

static int typeIdx(const char* type){
    if( type ) {
        unsigned char c0 = type[0];
        unsigned char c1 = type[1];
        if ( c1==0 ){
            return 256*0 + c0;
        } else if ( c0=='r' ) { // const
            return 256*1 + c1;
        } else if ( c0=='^' ) { // 指针
            return 256*2 + c1;
        } else if ( c0=='{' ) { // 结构体
            return 256*3 + 0;
        } else {
            LVError(@"LVUtil.typeIdx: %s", type);
            return 0;
        }
    }
    return 0;
}

LVTypeIDEnum lua_typeID(const char* type){
    static LVTypeIDEnum typesDic[256*4] = {0};
    static BOOL inited = NO;
    if( !inited ) {
        inited = YES;
        
        typesDic[typeIdx(@encode(void))] = LVTypeID_void;
        typesDic[typeIdx(@encode(bool))] = LVTypeID_bool;
        typesDic[typeIdx(@encode(BOOL))] = LVTypeID_BOOL;
        typesDic[typeIdx(@encode(char))] = LVTypeID_char;
        typesDic[typeIdx(@encode(unsigned char))] = LVTypeID_unsignedchar;
        typesDic[typeIdx(@encode(short))] = LVTypeID_short;
        typesDic[typeIdx(@encode(unsigned short))] = LVTypeID_unsignedshort;
        typesDic[typeIdx(@encode(int))] = LVTypeID_int;
        typesDic[typeIdx(@encode(unsigned int))] = LVTypeID_unsignedint;
        typesDic[typeIdx(@encode(NSInteger))] = LVTypeID_NSInteger;
        typesDic[typeIdx(@encode(NSUInteger))] = LVTypeID_NSUInteger;
        typesDic[typeIdx(@encode(long long))] = LVTypeID_longlong;
        typesDic[typeIdx(@encode(unsigned long long))] = LVTypeID_unsigedlonglong;
        typesDic[typeIdx(@encode(float))] = LVTypeID_float;
        typesDic[typeIdx(@encode(CGFloat))] = LVTypeID_CGFloat;
        typesDic[typeIdx(@encode(double))] = LVTypeID_double;
        
        typesDic[typeIdx(@encode(BOOL*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(char*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(short*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(int*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(NSInteger*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(long*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(float*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(CGFloat*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(double*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(unsigned char*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(unsigned short*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(unsigned int*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(NSUInteger*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(unsigned long*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(void*))]  = LVTypeID_voidP;
        typesDic[typeIdx(@encode(void**))] = LVTypeID_voidP;
        
        typesDic[typeIdx(@encode(const BOOL*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const char*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const short*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const int*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const long*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const float*))] = LVTypeID_charP;
        typesDic[typeIdx(@encode(const double*))] = LVTypeID_charP;
        
        typesDic[typeIdx(@encode(const void*))] = LVTypeID_voidP;
        typesDic[typeIdx("^{")] = LVTypeID_voidP;
        typesDic[typeIdx(@encode(id))] = LVTypeID_id;
        typesDic[typeIdx(@encode(Class))] = LVTypeID_Class;
        
        typesDic[typeIdx(@encode(CGRect))] = LVTypeID_struct;
    }
    return typesDic[ typeIdx(type) ];
}

int lv_setValueWithType(void* p, int index, double value, int type ){
    if ( p ) {
        switch (type) {
            case 'b':
            case 'B': {
                BOOL* cp = p;
                cp[index] = (BOOL)value;
                return 1;
            }
            case 'c':
            case 'C': {
                char* cp = p;
                cp[index] = (char)value;
                return 1;
            }
            case 's':
            case 'S': {
                short* cp = p;
                cp[index] = (short)value;
                return 1;
            }
            case 'i':
            case 'I': {
                int* cp = p;
                cp[index] = (int)value;
                return 1;
            }
            case 'l':
            case 'L': {
                long* cp = p;
                cp[index] = (long)value;
                return 1;
            }
            case 'q':
            case 'Q': {
                long long* cp = p;
                cp[index] = (long long)value;
                return 1;
            }
            case 'f': {
                float* cp = p;
                cp[index] = (float)value;
                return 1;
            }
            case 'd': {
                double* cp = p;
                cp[index] = (double)value;
                return 1;
            }
            default: {
                LVError(@"lv_setValueWithType: %c", type);
                return 0;
            }
        }
    }
    return 0;
}

double lv_getValueWithType(void* p, int index, int type ){
    if ( p ) {
        switch (type) {
            case 'b':
            case 'B': {
                BOOL* cp = p;
                return cp[index];
            }
            case 'c':
            case 'C': {
                char* cp = p;
                return cp[index];
            }
            case 's':
            case 'S': {
                short* cp = p;
                return cp[index];
            }
            case 'i':
            case 'I': {
                int* cp = p;
                return cp[index];
            }
            case 'l':
            case 'L': {
                long* cp = p;
                return cp[index];
            }
            case 'q':
            case 'Q': {
                long long* cp = p;
                return cp[index];
            }
            case 'f': {
                float* cp = p;
                return cp[index];
            }
            case 'd': {
                double* cp = p;
                return cp[index];
            }
            default: {
                LVError(@"lv_getValueWithType: %c", type);
                return 0;
            }
        }
    }
    return 0;
}

int lv_pushInvocationReturnValueToLuaStack(NSInvocation* invocation, lua_State* L){
    const char* type = [invocation.methodSignature methodReturnType];
    if ( type ){
        switch ( lua_typeID(type) ) {
            case LVTypeID_void:
                return 0;
            case LVTypeID_BOOL: {
                BOOL result = 0;
                [invocation getReturnValue: &result];
                lua_pushboolean(L, result);
                return 1;
            }
            case LVTypeID_bool: {
                bool result = 0;
                [invocation getReturnValue: &result];
                lua_pushboolean(L, result);
                return 1;
            }
            case LVTypeID_Class:
            case LVTypeID_id: {
                void* result = nil;
                [invocation getReturnValue: &result];
                lv_pushNativeObject(L,(__bridge id)result);
                return 1;
            }
            case LVTypeID_char: {
                char result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_unsignedchar: {
                unsigned char result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_short: {
                short result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_unsignedshort: {
                unsigned short result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_int: {
                int result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_unsignedint: {
                unsigned int result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_NSInteger: {
                NSInteger result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_NSUInteger: {
                NSUInteger result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_longlong: {
                long long result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_unsigedlonglong: {
                unsigned long long result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_float: {
                float result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_CGFloat: {
                CGFloat result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_double: {
                double result = 0;
                [invocation getReturnValue: &result];
                lua_pushnumber(L, result);
                return 1;
            }
            case LVTypeID_charP: {
                char* result = 0;
                [invocation getReturnValue: &result];
                lua_pushlightuserdata(L, result);
                return 1;
            }
            case LVTypeID_voidP: {
                void* result = 0;
                [invocation getReturnValue: &result];
                lua_pushlightuserdata(L, result);
                return 1;
            }
            case LVTypeID_struct:{
                CGFloat result[LV_STRUCT_MAX_LEN] = {0};
                [invocation getReturnValue: result];
                [LVStruct pushStructToLua:L data:result];
                return 1;
            }
            default:
                LVError(@"LVMethod.pushReturnToLuaStack");
                break;
        }
    }
    return 0;
}

int lv_setInvocationArgByLuaStack(NSInvocation* invocation, int index, lua_State* L, int stackID){
    const char* type = [invocation.methodSignature getArgumentTypeAtIndex:index];
    if ( type ){
        switch ( lua_typeID(type) ) {
            case LVTypeID_BOOL: {
                BOOL value = lua_toboolean(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_bool: {
                bool value = lua_toboolean(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_Class:
            case LVTypeID_id: {
                id nativeObject = lv_luaValueToNativeObject(L, stackID);
                [invocation setArgument:&nativeObject atIndex:index];
                return 1;
            }
            case LVTypeID_char: {
                char value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_unsignedchar: {
                unsigned char value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_short: {
                short value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_unsignedshort: {
                unsigned short value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_int: {
                int value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_unsignedint: {
                unsigned int value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_NSInteger: {
                NSInteger value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_NSUInteger: {
                NSUInteger value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_longlong: {
                long long value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_unsigedlonglong: {
                unsigned long long value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_float: {
                float value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_CGFloat: {
                CGFloat value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_double: {
                double value = lua_tonumber(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_charP: {
                char* value = lua_touserdata(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_voidP: {
                void* value = lua_touserdata(L, stackID);
                [invocation setArgument: &value atIndex:index];
                return 1;
            }
            case LVTypeID_struct: {
                LVUserDataInfo* user = lua_touserdata(L, stackID);
                if ( LVIsType(user, Struct) ) {
                    LVStruct* stru = (__bridge LVStruct *)(user->object);
                    if( [stru dataPointer] ) {
                        [invocation setArgument:[stru dataPointer] atIndex:index];
                    }
                }
                return 1;
            }
            default: {
                NSInteger value = 0;
                [invocation setArgument: &value atIndex:index];
                LVError(@"setIvocationArgument:index:byLua:");
                break;
            }
        }
    }
    //否则将参数设置为空
    NSInteger value = 0;
    [invocation setArgument: &value atIndex:index];
    return 0;
}

id lv_setInvocationReturnValueByLuaStack(NSInvocation* invocation, lua_State* L, int stackID){
    const char* type = [invocation.methodSignature methodReturnType];
    if ( type ) {
        switch ( lua_typeID(type) ) {
            case LVTypeID_void:
                return nil;
            case LVTypeID_BOOL: {
                BOOL result = lua_toboolean(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_bool: {
                bool result = lua_toboolean(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_Class:
            case LVTypeID_id: {
                id object =  lv_luaValueToNativeObject(L, stackID);
                [invocation setReturnValue:&object];
                return object;
            }
            case LVTypeID_char: {
                char result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_unsignedchar: {
                unsigned char result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_short: {
                short result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_unsignedshort: {
                unsigned short result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_int: {
                int result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_unsignedint: {
                unsigned int result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_NSInteger: {
                NSInteger result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_NSUInteger: {
                NSUInteger result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_longlong: {
                long long result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_unsigedlonglong: {
                unsigned long long result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_float: {
                float result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_CGFloat: {
                CGFloat result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_double: {
                double result = lua_tonumber(L, stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_charP: {
                char* result = lua_touserdata(L,stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_voidP: {
                char* result = lua_touserdata(L,stackID);
                [invocation setReturnValue: &result];
                return nil;
            }
            case LVTypeID_struct: {
                LVUserDataInfo* user = lua_touserdata(L,stackID);
                if ( LVIsType(user, Struct) ) {
                    LVStruct* stru = (__bridge LVStruct *)(user->object);
                    if( [stru dataPointer] ) {
                        [invocation setReturnValue: [stru dataPointer]];
                    }
                }
                return nil;
            }
            default:
                LVError( @"LVLuaObjBox.setInvocationReturnValue:withLuaObject:");
                break;
        }
    }
    return  nil ;
}

int lv_pushInvocationArgToLuaStack(NSInvocation* invocation, int index, lua_State* L ){
    const char* type = [invocation.methodSignature getArgumentTypeAtIndex:index];
    switch ( lua_typeID(type) ) {
        case LVTypeID_BOOL: {
            BOOL value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_bool: {
            bool value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_Class:
        case LVTypeID_id: {
            void* value = nil;
            [invocation getArgument:&value atIndex:index];
            lv_pushNativeObject(L, (__bridge id)value);
            return 1;
        }
        case LVTypeID_char: {
            char value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_unsignedchar: {
            unsigned char value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_short: {
            short value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_unsignedshort: {
            unsigned short value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_int: {
            int value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_unsignedint: {
            unsigned int value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_NSInteger: {
            NSInteger value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_NSUInteger: {
            NSUInteger value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_longlong: {
            long long value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_unsigedlonglong: {
            unsigned long long value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_float: {
            float value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_CGFloat: {
            CGFloat value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_double: {
            double value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushnumber(L, value);
            return 1;
        }
        case LVTypeID_charP: {
            char* value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushlightuserdata(L,value);
            return 1;
        }
        case LVTypeID_voidP: {
            void* value = 0;
            [invocation getArgument: &value atIndex:index];
            lua_pushlightuserdata(L,value);
            return 1;
        }
        case LVTypeID_struct: {
            CGFloat result[LV_STRUCT_MAX_LEN] = {0};
            [invocation getArgument:result atIndex:index];
            [LVStruct pushStructToLua:L data:result];
            return 1;
        }
        default:{
            LVError(@"LVLuaObjBox.pushInvocation:argIndex:toLua:");
            //否则将参数设置为空
            NSInteger value = 0;
            [invocation setArgument: &value atIndex:index];
            return 1;
        }
    }
}



@end

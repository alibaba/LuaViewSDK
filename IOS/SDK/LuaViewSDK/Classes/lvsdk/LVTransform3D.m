/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVTransform3D.h"
#import "LVHeads.h"
#import "LVHeads.h"

@implementation LVTransform3D

-(id) lv_nativeObject{
    return nil;
}

static int lvNewTransform3D (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVTransform3D class]];
    
    {
        NEW_USERDATA(userData, Transform3D);
        LVTransform3D* trans = [[c alloc] init];
        userData->object = CFBridgingRetain(trans);
        trans.transform = CATransform3DIdentity;
        luaL_getmetatable(L, META_TABLE_Transform3D );
        lua_setmetatable(L, -2);
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) pushTransform3D:(lua_State *)L  transform3d:(CATransform3D) t{
    {
        NEW_USERDATA(userData, Transform3D);
        LVTransform3D* trans = [[LVTransform3D alloc] init];
        userData->object = CFBridgingRetain(trans);
        trans.transform = t;
        luaL_getmetatable(L, META_TABLE_Transform3D );
        lua_setmetatable(L, -2);
    }
    return 1;
}


static int translation (lua_State *L) {
    if( lua_gettop(L)==4 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        double x = lua_tonumber(L, 2);// 2
        double y = lua_tonumber(L, 3);// 3
        double z = lua_tonumber(L, 4);// 4
        if( LVIsType(user, Transform3D) ){
            LVTransform3D* tran = (__bridge LVTransform3D *)(user->object);
            tran.transform = CATransform3DTranslate(tran.transform, x, y, z);
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int scale (lua_State *L) {
    if( lua_gettop(L)==4 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        double x = lua_tonumber(L, 2);// 2
        double y = lua_tonumber(L, 3);// 3
        double z = lua_tonumber(L, 4);// 4
        if( LVIsType(user, Transform3D) ){
            LVTransform3D* tran = (__bridge LVTransform3D *)(user->object);
            tran.transform = CATransform3DScale(tran.transform, x, y, z);
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int rotate (lua_State *L) {
    if( lua_gettop(L)==5 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        double angle = lua_tonumber(L, 2);
        double x = lua_tonumber(L, 3);
        double y = lua_tonumber(L, 4);
        double z = lua_tonumber(L, 5);
        if( LVIsType(user, Transform3D) ){
            LVTransform3D* tran = (__bridge LVTransform3D *)(user->object);
            tran.transform = CATransform3DRotate(tran.transform, angle, x, y, z);
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int isIdentity (lua_State *L) {
    if( lua_gettop(L)==1 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        if( LVIsType(user, Transform3D) ){
            LVTransform3D* tran = (__bridge LVTransform3D *)(user->object);
            BOOL yes = CATransform3DIsIdentity(tran.transform );
            int ret = (yes ? 1 : 0);
            lua_pushboolean(L, ret);
            return 1;
        }
    }
    return 0;
}

static int reset (lua_State *L) {
    if( lua_gettop(L)==1 ) {
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
        if( LVIsType(user, Transform3D) ){
            LVTransform3D* tran = (__bridge LVTransform3D *)(user->object);
            tran.transform = CATransform3DIdentity;
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int transform_set (lua_State *L) {
    if( lua_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, Transform3D) && LVIsType(user2, Transform3D) ){
            LVTransform3D* tran1 = (__bridge LVTransform3D *)(user1->object);
            LVTransform3D* tran2 = (__bridge LVTransform3D *)(user2->object);
            tran1.transform = tran2.transform;
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int concat (lua_State *L) {
    if( lua_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, Transform3D) && LVIsType(user2, Transform3D) ){
            LVTransform3D* tran1 = (__bridge LVTransform3D *)(user1->object);
            LVTransform3D* tran2 = (__bridge LVTransform3D *)(user2->object);
            tran1.transform = CATransform3DConcat(tran1.transform, tran2.transform);
            
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int __mul (lua_State *L) {
    if( lua_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, Transform3D) && LVIsType(user2, Transform3D) ){
            LVTransform3D* tran1 = (__bridge LVTransform3D *)(user1->object);
            LVTransform3D* tran2 = (__bridge LVTransform3D *)(user2->object);
            NEW_USERDATA(user, Transform3D);
            
            LVTransform3D* trans = [[LVTransform3D alloc] init];
            user->object = CFBridgingRetain(trans);

            trans.transform = CATransform3DIdentity;
            luaL_getmetatable(L, META_TABLE_Transform3D );
            lua_setmetatable(L, -2);
            
            trans.transform = CATransform3DConcat(tran1.transform, tran2.transform);
            return 1;
        }
    }
    return 0;
}

static int __eq (lua_State *L) {
    if( lua_gettop(L)==2 ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, Transform3D) && LVIsType(user2, Transform3D) ){
            LVTransform3D* tran1 = (__bridge LVTransform3D *)(user1->object);
            LVTransform3D* tran2 = (__bridge LVTransform3D *)(user2->object);
            BOOL yes =  CATransform3DEqualToTransform( tran1.transform, tran2.transform);
            lua_pushboolean(L, (yes?1:0) );
            return 1;
        }
    }
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, Transform3D) ){
        NSString* s = [NSString stringWithFormat:@"LVUserDataTransform3D: %d", (int)user ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewTransform3D globalName:globalName defaultName:@"Transform3D"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"__eq", __eq},
        {"__mul", __mul},
        {"rotate", rotate},
        {"scale", scale},
        {"translation", translation},
        
        {"isIdentity", isIdentity},
        {"reset", reset},
        
        {"set", transform_set},
        {"concat", concat},
        
        {"__tostring", __tostring},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_Transform3D);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end

void CATransform3DSetRotation(CATransform3D *t, CGFloat v) {
    double scaleX = CATransform3DGetScaleX(t), scaleY = CATransform3DGetScaleY(t);
    t->m11 = scaleX * cos(v);
    t->m12 = scaleX * sin(v);
    t->m21 = -(scaleY * sin(v));
    t->m22 = scaleY * cos(v);
}

double CATransform3DGetRotation(CATransform3D *t) {
    return atan2(t->m12, t->m11);
}

void CATransform3DSetScaleX(CATransform3D *t, CGFloat v) {
    double r = CATransform3DGetRotation(t);
    t->m11 = v * cos(r);
    t->m12 = v * sin(r);
}

double CATransform3DGetScaleX(CATransform3D *t) {
    double r = CATransform3DGetRotation(t);
    
    if (t->m11 != 0) {
        return t->m11 / cos(r);
    } else if (t->m12 != 0) {
        return t->m12 / sin(r);
    } else {
        return 0;
    }
}

void CATransform3DSetScaleY(CATransform3D *t, CGFloat v) {
    double r = CATransform3DGetRotation(t);
    t->m21 = -(v * sin(r));
    t->m22 = v * cos(r);
}

double CATransform3DGetScaleY(CATransform3D *t) {
    double r = CATransform3DGetRotation(t);

    if (t->m21 != 0) {
        return -(t->m21 / sin(r));
    } else if (t->m22 != 0) {
        return t->m22 / cos(r);
    } else {
        return 0;
    }
}

void CATransform3DSetScaleZ(CATransform3D *t, CGFloat v) {
    t->m33 = v;
}

double CATransform3DGetScaleZ(CATransform3D *t) {
    return t->m33;
}

void CATransform3DSetTranslationX(CATransform3D *t, CGFloat v) {
    t->m41 = v;
}

double CATransform3DGetTranslationX(CATransform3D *t) {
    return t->m41;
}

void CATransform3DSetTranslationY(CATransform3D *t, CGFloat v) {
    t->m42 = v;
}

double CATransform3DGetTranslationY(CATransform3D *t) {
    return t->m42;
}

void CATransform3DSetTranslationZ(CATransform3D *t, CGFloat v) {
    t->m43 = v;
}

double CATransform3DGetTranslationZ(CATransform3D *t) {
    return t->m43;
}

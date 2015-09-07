//
//  LVTransform3D.m
//  JU
//
//  Created by dongxicheng on 12/30/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import "LVTransform3D.h"
#import "LVHeads.h"



@implementation LVTransform3D

static int lvNewTransform3D (lv_State *L) {
    {
        NEW_USERDATA(userData, LVUserDataTransform3D);
        userData->transform = CATransform3DIdentity;
        lvL_getmetatable(L, META_TABLE_Transform3D );
        lv_setmetatable(L, -2);
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) pushTransform3D:(lv_State *)L  transform3d:(CATransform3D) t{
    {
        NEW_USERDATA(userData, LVUserDataTransform3D);
        userData->transform = t;
        lvL_getmetatable(L, META_TABLE_Transform3D );
        lv_setmetatable(L, -2);
    }
    return 1;
}


static int translation (lv_State *L) {
    if( lv_gettop(L)==4 ) {
        LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        double x = lv_tonumber(L, 2);// 2
        double y = lv_tonumber(L, 3);// 3
        double z = lv_tonumber(L, 4);// 4
        if( LVIsType(user,LVUserDataTransform3D) ){
            user->transform = CATransform3DTranslate(user->transform, x, y, z);
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int scale (lv_State *L) {
    if( lv_gettop(L)==4 ) {
        LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        double x = lv_tonumber(L, 2);// 2
        double y = lv_tonumber(L, 3);// 3
        double z = lv_tonumber(L, 4);// 4
        if( LVIsType(user,LVUserDataTransform3D) ){
            user->transform = CATransform3DScale(user->transform, x, y, z);
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int rotate (lv_State *L) {
    if( lv_gettop(L)==5 ) {
        LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        double angle = lv_tonumber(L, 2);
        double x = lv_tonumber(L, 3);
        double y = lv_tonumber(L, 4);
        double z = lv_tonumber(L, 5);
        if( LVIsType(user,LVUserDataTransform3D) ){
            user->transform = CATransform3DRotate(user->transform, angle, x, y, z);
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int isIdentity (lv_State *L) {
    if( lv_gettop(L)==1 ) {
        LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        if( LVIsType(user,LVUserDataTransform3D) ){
            BOOL yes = CATransform3DIsIdentity(user->transform );
            int ret = (yes ? 1 : 0);
            lv_pushboolean(L, ret);
            return 1;
        }
    }
    return 0;
}

static int reset (lv_State *L) {
    if( lv_gettop(L)==1 ) {
        LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        if( LVIsType(user,LVUserDataTransform3D) ){
            user->transform = CATransform3DIdentity;
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int transform_set (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataTransform3D * user1 = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        LVUserDataTransform3D * user2 = (LVUserDataTransform3D *)lv_touserdata(L, 2);
        if( LVIsType(user1,LVUserDataTransform3D) && LVIsType(user2,LVUserDataTransform3D) ){
            user1->transform = user2->transform;
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int concat (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataTransform3D * user1 = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        LVUserDataTransform3D * user2 = (LVUserDataTransform3D *)lv_touserdata(L, 2);
        if( LVIsType(user1,LVUserDataTransform3D) && LVIsType(user2,LVUserDataTransform3D) ){
            user1->transform = CATransform3DConcat(user1->transform, user2->transform);
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int __mul (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataTransform3D * user1 = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        LVUserDataTransform3D * user2 = (LVUserDataTransform3D *)lv_touserdata(L, 2);
        if( LVIsType(user1,LVUserDataTransform3D) && LVIsType(user2,LVUserDataTransform3D) ){
            NEW_USERDATA(user, LVUserDataTransform3D);
            user->transform = CATransform3DIdentity;
            lvL_getmetatable(L, META_TABLE_Transform3D );
            lv_setmetatable(L, -2);
            
            user->transform = CATransform3DConcat(user1->transform, user2->transform);
            
            return 1;
        }
    }
    return 0;
}

static int __eq (lv_State *L) {
    if( lv_gettop(L)==2 ) {
        LVUserDataTransform3D * user1 = (LVUserDataTransform3D *)lv_touserdata(L, 1);
        LVUserDataTransform3D * user2 = (LVUserDataTransform3D *)lv_touserdata(L, 2);
        if( LVIsType(user1,LVUserDataTransform3D) && LVIsType(user2,LVUserDataTransform3D) ){
            BOOL yes =  CATransform3DEqualToTransform( user1->transform, user2->transform);
            lv_pushboolean(L, (yes?1:0) );
            return 1;
        }
    }
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataTransform3D * user = (LVUserDataTransform3D *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataTransform3D) ){
        NSString* s = [NSString stringWithFormat:@"LVUserDataTransform3D: %d", (int)user ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewTransform3D);
        lv_setglobal(L, "Transform3D");
    }
    const struct lvL_reg memberFunctions [] = {
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
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end

//
//  LVCustomErrorView.m
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCustomPanel.h"
#import "LVBaseView.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVCustomPanel

- (void) callLuaWithArgument:(NSString*) info {
    [self callLuaWithArguments:@[ (info?info:@"") ]];
}

- (void) callLuaWithArguments:(NSArray*) args{
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        lv_checkstack(L,32);
        int num = lv_gettop(L);
        for( int i=0; i<args.count; i++ ) {
            lv_pushNativeObject(L, args[i]);
        }
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        
        if( lv_type(L, -1)==LV_TTABLE ) {
            lv_getfield(L, -1, STR_CALLBACK);
            if( lv_type(L, -1)==LV_TNIL ) {
                lv_remove(L, -1);
                lv_getfield(L, -1, STR_ON_CLICK);
            }
            lv_remove(L, -2);
        }
        lv_runFunctionWithArgs(L, (int)args.count, 0);
        lv_settop(L, num);
    }
}

-(void) layoutSubviews{
    [self lv_runCallBack:STR_ON_LAYOUT];
}

-(id)lv_getNativeView{
    NSArray* subviews = self.subviews;
    return subviews.firstObject;
}

static int lvNewCustomPanelView (lv_State *L) ;

static NSMutableDictionary* g_classDic = nil;

+ (void) addCustomPanel:(Class) c boundName:(NSString*) boundName state:(lv_State*) L{
    if (g_classDic == nil ) {
        g_classDic = [[NSMutableDictionary alloc] init];
    }
    if( [c isSubclassOfClass:[LVCustomPanel class]] ){
        [g_classDic setObject:c forKey:boundName];
    }
    lv_checkstack(L, 16);
    lv_pushstring(L, boundName.UTF8String);
    lv_pushcclosure(L, lvNewCustomPanelView, 1);
    lv_setglobal(L, boundName.UTF8String);
}

static int lvNewCustomPanelView (lv_State *L) {
    Class tempClass = nil;
    NSString* name = nil;
    
    if( lv_type(L, lv_upvalueindex(1)) ==LV_TSTRING ) {
        const char* s = lv_tostring(L, lv_upvalueindex(1) );
        if( s ) {
            name = [NSString stringWithFormat:@"%s",s];
        }
    } else if( lv_type(L, 1)==LV_TSTRING ){
        name = lv_paramString(L, 1);
        lv_remove(L, 1);
    }
    if( name ) {
        tempClass = g_classDic[name];
    }
    if( tempClass == nil ) {
        tempClass = [LVCustomPanel class];
    }
    CGRect r = CGRectMake(0, 0, 0, 0);
    if( lv_gettop(L)>=4 ) {
        r = CGRectMake(lv_tonumber(L, 1), lv_tonumber(L, 2), lv_tonumber(L, 3), lv_tonumber(L, 4));
    }
    LVCustomPanel* errorNotice = [[tempClass alloc] initWithFrame:r];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(errorNotice);
        errorNotice.lv_userData = userData;
        errorNotice.lv_lview = (__bridge LView *)(L->lView);
        
        lvL_getmetatable(L, META_TABLE_UICustomPanel );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:errorNotice];
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewCustomPanelView);
        lv_setglobal(L, "CustomPanel");
    }
    const struct lvL_reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UICustomPanel);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end

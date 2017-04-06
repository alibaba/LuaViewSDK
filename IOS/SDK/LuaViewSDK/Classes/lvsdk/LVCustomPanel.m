/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCustomPanel.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVCustomPanel

- (void) callLuaWithArgument:(NSString*) info {
    [self callLuaWithArguments:@[ (info?info:@"") ]];
}

- (void) callLuaWithArguments:(NSArray*) args{
    // 外部回调脚本一定要在主线程调用
    dispatch_block_t f = ^(){
        lua_State* L = self.lv_luaviewCore.l;
        if( L && self.lv_userData ){
            lua_checkstack(L,32);
            int num = lua_gettop(L);
            for( int i=0; i<args.count; i++ ) {
                lv_pushNativeObject(L, args[i]);
            }
            lv_pushUserdata(L, self.lv_userData);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            
            if( lua_type(L, -1)==LUA_TTABLE ) {
                lua_getfield(L, -1, STR_CALLBACK);
                if( lua_type(L, -1)==LUA_TNIL ) {
                    lua_remove(L, -1);
                    lua_getfield(L, -1, STR_ON_CLICK);
                }
                lua_remove(L, -2);
            }
            lv_runFunctionWithArgs(L, (int)args.count, 0);
            lua_settop(L, num);
        }
    };
    if ([NSThread isMainThread]) {
        f();
    } else {
        dispatch_sync(dispatch_get_main_queue(), f);
    }
}

-(void) layoutSubviews{
    [super layoutSubviews];
    [self lv_callLuaCallback:@STR_ON_LAYOUT];
}

-(id)lv_getNativeView{
    NSArray* subviews = self.subviews;
    return subviews.firstObject;
}

static int lvNewCustomPanelView (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCustomPanel class]];
    
    CGRect r = CGRectMake(0, 0, 0, 0);
    if( lua_gettop(L)>=4 ) {
        r = CGRectMake(lua_tonumber(L, 1), lua_tonumber(L, 2), lua_tonumber(L, 3), lua_tonumber(L, 4));
    }
    LVCustomPanel* errorNotice = [[c alloc] initWithFrame:r];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(errorNotice);
        errorNotice.lv_userData = userData;
        errorNotice.lv_luaviewCore = LV_LUASTATE_VIEW(L);
        
        luaL_getmetatable(L, META_TABLE_CustomPanel );
        lua_setmetatable(L, -2);
    }
    LuaViewCore* view = LV_LUASTATE_VIEW(L);
    if( view ){
        [view containerAddSubview:errorNotice];
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCustomPanelView globalName:globalName defaultName:@"CustomPanel"];
    
    const struct luaL_Reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_CustomPanel);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end

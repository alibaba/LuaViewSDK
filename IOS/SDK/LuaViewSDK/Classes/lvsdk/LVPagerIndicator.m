/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVPagerIndicator.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVPagerView.h"
#import "LVHeads.h"

@implementation LVPagerIndicator


-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.clipsToBounds = YES;
        self.userInteractionEnabled = NO;
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark PageControl
static int lvNewPagerIndicator (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVPagerIndicator class]];
    
    LVPagerIndicator* pageControl = [[c alloc] init:L];
    
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(pageControl);
        
        luaL_getmetatable(L, META_TABLE_PagerIndicator );
        lua_setmetatable(L, -2);
    }
    LuaViewCore* view = LV_LUASTATE_VIEW(L);
    if( view ){
        [view containerAddSubview:pageControl];
    }
    return 1; /* new userdatum is already on the stack */
}

//static int setPageCount(lua_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lua_touserdata(L, 1);
//    if( user ){
//        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->view);
//        if( view ){
//            if( lua_gettop(L)>=2 ) {
//                int number = lua_tonumber(L, 2);
//                view.numberOfPages = number;
//                return 0;
//            } else {
//                lua_pushnumber(L, view.numberOfPages );
//                return 1;
//            }
//        }
//    }
//    return 0;
//}

static int setCurrentPage(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->object);
        if( view ){
            if( lua_gettop(L)>=2 ) {
                int currentPage = lua_tonumber(L, 2);
                //view.currentPage = currentPage-1;
                [view.pagerView setCurrentPageIdx:currentPage-1 animation:YES];
                return 0;
            } else {
                lua_pushnumber(L, view.currentPage+1 );
                return 1;
            }
        }
    }
    return 0;
}

static int pageIndicatorTintColor(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->object);
        if( view ){
            if( lua_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.pageIndicatorTintColor = color;
                return 0;
            } else {
                UIColor* color = view.pageIndicatorTintColor;
                NSUInteger c = 0;
                CGFloat a = 0;
                if( lv_uicolor2int(color, &c, &a) ){
                    lua_pushnumber(L, c );
                    lua_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

static int currentPageIndicatorTintColor(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerIndicator* view = (__bridge LVPagerIndicator *)(user->object);
        if( view ){
            if( lua_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.currentPageIndicatorTintColor = color;
                return 0;
            } else {
                UIColor* color = view.currentPageIndicatorTintColor;
                NSUInteger c = 0;
                CGFloat a = 0;
                if( lv_uicolor2int(color, &c, &a) ){
                    lua_pushnumber(L, c );
                    lua_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewPagerIndicator globalName:globalName defaultName:@"PagerIndicator"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"currentPage",     setCurrentPage },
        
        {"pageColor",     pageIndicatorTintColor },//__deprecated_msg("Use unselectedColor")
        {"currentPageColor",     currentPageIndicatorTintColor },//__deprecated_msg("Use selectedColor")
        
        {"unselectedColor",     pageIndicatorTintColor },
        {"selectedColor",     currentPageIndicatorTintColor },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_PagerIndicator);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


//----------------------------------------------------------------------------------------

@end

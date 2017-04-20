/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVNavigation.h"
#import "LView.h"
#import "LVStyledString.h"
#import "LVHeads.h"

@implementation LVNavigation

//  空实现去除编译警告
-(void) lv_setNavigationItemTitleView:(UIView*) view{
}
-(void) lv_setNavigationItemTitle:(NSString*) title{
}
-(void) lv_setNavigationItemLeftBarButtonItems:(NSArray*) items{
}
-(void) lv_setNavigationItemRightBarButtonItems:(NSArray*) items{
}
-(void) lv_setNavigationBarBackgroundImage:(UIImage*) image{
}

static void setViewControllerTitleView(UIViewController* vc, UIView* view){
    if([view isKindOfClass:[UIView class]] ) {// 第三种: View
        if( [vc respondsToSelector:@selector(lv_setNavigationItemTitleView:)] ) {
            [vc performSelector:@selector(lv_setNavigationItemTitleView:) withObject:view];
        } else {
            vc.navigationItem.titleView = view;
        }
    }
}

static int setTitle (lua_State *L) {
    lv_clearFirstTableValue(L);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    UIViewController* vc = lview.viewController;
    if( vc && lua_gettop(L)>=1 ) {
        if( lua_type(L, 1)== LUA_TSTRING ) {// 第一种: string
            NSString* title = lv_paramString(L, 1);
            if( [vc respondsToSelector:@selector(lv_setNavigationItemTitle:)] ) {
                [vc performSelector:@selector(lv_setNavigationItemTitle:) withObject:title];
            } else {
                vc.navigationItem.title = title;
            }
            return 0;
        } else if( lua_type(L, 1)== LUA_TUSERDATA ) {//第二种: 复合文本
            LVUserDataInfo * user2 = lua_touserdata(L, 1);
            if( user2 && LVIsType(user2, StyledString) ) {
                UILabel* label = [[UILabel alloc] init];
                LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                [label setAttributedText:attString.mutableStyledString];
                [label sizeToFit];
                setViewControllerTitleView(vc, label);
                return 0;
            }
        }
        id object = lv_luaValueToNativeObject(L, 1);// 第三种: View
        setViewControllerTitleView(vc, object);
        return 0;
    }
    return 0;
}

+(NSArray*) getNavigationItems:(lua_State*)L{
    NSMutableArray* array = [[NSMutableArray alloc] init];
    int num = lua_gettop(L);
    for ( int i=1; i<=num; i++ ) {
        id object = lv_luaValueToNativeObject(L, i);
        if ( [object isKindOfClass:[UIView class]] ) {
            [array addObject:[[UIBarButtonItem alloc] initWithCustomView:object]];
        }
    }
    return array;
}

static int setLeftButton (lua_State *L) {
    lv_clearFirstTableValue(L);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    UIViewController* vc = lview.viewController;
    if( vc && lua_gettop(L)>=1 ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        if( [vc respondsToSelector:@selector(lv_setNavigationItemLeftBarButtonItems:)] ) {
            [vc performSelector:@selector(lv_setNavigationItemLeftBarButtonItems:) withObject:buttonItems];
        } else {
            vc.navigationItem.leftBarButtonItems = buttonItems;
        }
    }
    return 0; /* number of results */
}

static int setRightButton (lua_State *L) {
    lv_clearFirstTableValue(L);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    UIViewController* vc = lview.viewController;
    if( vc && lua_gettop(L)>=1 ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        if( [vc respondsToSelector:@selector(lv_setNavigationItemRightBarButtonItems:)] ){
            [vc performSelector:@selector(lv_setNavigationItemRightBarButtonItems:) withObject:buttonItems];
        } else {
            vc.navigationItem.rightBarButtonItems = buttonItems;
        }
    }
    return 0; /* number of results */
}

static int setBackground(lua_State*L ) {
    lv_clearFirstTableValue(L);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    UIViewController* vc = lview.viewController;
    if( vc && lua_gettop(L)>=1 ) {
        id obj = lv_luaValueToNativeObject(L, 1);
        if( [obj isKindOfClass:[UIImageView class]] ) {
            UIImageView* imgView = obj;
            UIImage* image = imgView.image;
            float scale = [UIScreen mainScreen].scale;
            if( image.scale<scale) {
                image = [UIImage imageWithCGImage:image.CGImage scale:scale orientation:0];
            }
            if( [vc respondsToSelector:@selector(lv_setNavigationBarBackgroundImage:)] ) {
                [vc performSelector:@selector(lv_setNavigationBarBackgroundImage:) withObject:image];
            } else {
                [vc.navigationController.navigationBar setBackgroundImage:image forBarMetrics:UIBarMetricsDefault];
            }
        }
    }
    return 0;
}

static int setStatusBarStyle (lua_State *L) {
    lv_clearFirstTableValue(L);
    if ( lua_gettop(L)>=1 ) {
        NSInteger value = lua_tonumber(L, 2);
        [[UIApplication sharedApplication] setStatusBarStyle:value];
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    const struct luaL_Reg fs [] = {
        {"title", setTitle},
        {"left", setLeftButton},
        {"right", setRightButton},
        {"background", setBackground},
        {"statusBarStyle", setStatusBarStyle}, // for IOS
        {LUAVIEW_SYS_TABLE_KEY, setBackground},
        {NULL, NULL}
    };
    luaL_openlib(L, "Navigation", fs, 0);
    return 0;
}

@end




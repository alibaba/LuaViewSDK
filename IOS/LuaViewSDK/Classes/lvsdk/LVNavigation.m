//
//  LVNavigationBar.m
//  LVSDK
//
//  Created by dongxicheng on 7/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVNavigation.h"
#import "LView.h"

@implementation LVNavigation


static int setTitle (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    id object = lv_luaValueToNativeObject(L, 1);
    if ( [object isKindOfClass:[NSString class]] ) {
        vc.navigationItem.title = object;
    } else if([object isKindOfClass:[UIView class]] ) {
        vc.navigationItem.titleView = object;
    }
    return 0; /* number of results */
}

+(NSArray*) getNavigationItems:(lv_State*)L{
    NSMutableArray* array = [[NSMutableArray alloc] init];
    int num = lv_gettop(L);
    for ( int i=1; i<=num; i++ ) {
        id object = lv_luaValueToNativeObject(L, i);
        if ( [object isKindOfClass:[UIView class]] ) {
            [array addObject:[[UIBarButtonItem alloc] initWithCustomView:object]];
        }
    }
    return array;
}

static int setLeftBarButton (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        vc.navigationItem.leftBarButtonItems = buttonItems;
    }
    return 0; /* number of results */
}

static int setRightBarButton (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        vc.navigationItem.rightBarButtonItems = buttonItems;
    }
    return 0; /* number of results */
}

static int setBackground(lv_State*L ) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc ) {
        UINavigationBar* navBar = vc.navigationController.navigationBar;
        if ( [LVUtil ios7] ) {
            id obj = lv_luaValueToNativeObject(L, 1);
            if( [obj isKindOfClass:[UIImageView class]] ) {
                UIImageView* imgView = obj;
                [navBar setBackgroundImage:imgView.image forBarPosition:UIBarPositionBottom barMetrics:UIBarMetricsDefault];
            }
            if ( lv_type(L, 2) ==LV_TNUMBER ) {
                NSUInteger color = lv_tonumber(L, 2);
                float a = ( (color>>24)&0xff )/255.0;
                float r = ( (color>>16)&0xff )/255.0;
                float g = ( (color>>8)&0xff )/255.0;
                float b = ( (color>>0)&0xff )/255.0;
                if( a==0 ){
                    a = 1;
                }
                if( lv_gettop(L)>=3 && lv_type(L, 3) ==LV_TNUMBER){
                    a = lv_tonumber(L, 3);
                }
                UIColor* c  = [UIColor colorWithRed:r green:g blue:b alpha:a];
                [navBar setTintColor:c];
                [navBar setBarTintColor:c];
            }
        } else {
            id obj = lv_luaValueToNativeObject(L, 1);
            if( [obj isKindOfClass:[UIImageView class]] ) {
                UIImageView* imgView = obj;
                [navBar setBackgroundImage:imgView.image forBarMetrics:UIBarMetricsDefault];
            }
            [[UIApplication sharedApplication]setStatusBarStyle:UIStatusBarStyleDefault];
        }
    }
    return 0;
}

static int setStatusBarStyle (lv_State *L) {
    lv_clearFirstTableValue(L);
    NSInteger value = lv_tonumber(L, 2);
    [[UIApplication sharedApplication] setStatusBarStyle:value];
    return 0; /* number of results */
}

+(int) classDefine:(lv_State *)L{
    const struct lvL_reg staticFunctions [] = {
        {"setTitle", setTitle},
        {"setLeftBarButton", setLeftBarButton},
        {"setRightBarButton", setRightBarButton},
        {"setBackground", setBackground},
        {"setStatusBarStyle", setStatusBarStyle},
        {NULL, NULL}
    };
    lvL_openlib(L, "Navigation", staticFunctions, 0);
    return 0;
}

@end

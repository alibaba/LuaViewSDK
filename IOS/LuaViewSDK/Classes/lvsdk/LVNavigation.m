//
//  LVNavigationBar.m
//  LVSDK
//
//  Created by dongxicheng on 7/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVNavigation.h"
#import "LView.h"
#import "LVStyledString.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVNavigation


static int setTitle (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        if( lv_type(L, 1)== LV_TSTRING ) {// 第一种: string
            vc.navigationItem.title = lv_paramString(L, 1);
            return 0;
        } else if( lv_type(L, 1)== LV_TUSERDATA ) {//第二种: 复合文本
            LVUserDataStyledString * user2 = lv_touserdata(L, 1);
            if( user2 && LVIsType(user2, LVUserDataStyledString) ) {
                UILabel* label = [[UILabel alloc] init];
                LVStyledString* attString = (__bridge LVStyledString *)(user2->styledString);
                [label setAttributedText:attString.mutableStyledString];
                [label sizeToFit];
                vc.navigationItem.titleView = label;
                return 0;
            }
        }
        id object = lv_luaValueToNativeObject(L, 1);
        if([object isKindOfClass:[UIView class]] ) {// 第三种: View
            vc.navigationItem.titleView = object;
        }
        return 0;
    }
    return 0;
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

static int setLeftButton (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        vc.navigationItem.leftBarButtonItems = buttonItems;
    }
    return 0; /* number of results */
}

static int setRightButton (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        vc.navigationItem.rightBarButtonItems = buttonItems;
    }
    return 0; /* number of results */
}

static int setBackground(lv_State*L ) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        UINavigationBar* navBar = vc.navigationController.navigationBar;

        id obj = lv_luaValueToNativeObject(L, 1);
        if( [obj isKindOfClass:[UIImageView class]] ) {
            UIImageView* imgView = obj;
            UIImage* image = imgView.image;
            float scale = [UIScreen mainScreen].scale;
            if( image.scale<scale) {
                image = [UIImage imageWithCGImage:image.CGImage scale:scale orientation:0];
            }
            [navBar setBackgroundImage:image forBarMetrics:UIBarMetricsDefault];
        }
    }
    return 0;
}

static int setStatusBarStyle (lv_State *L) {
    lv_clearFirstTableValue(L);
    if ( lv_gettop(L)>=1 ) {
        NSInteger value = lv_tonumber(L, 2);
        [[UIApplication sharedApplication] setStatusBarStyle:value];
    }
    return 0;
}

+(int) classDefine:(lv_State *)L{
    const struct lvL_reg fs [] = {
        {"title", setTitle},
        {"left", setLeftButton},
        {"right", setRightButton},
        {"background", setBackground},
        {"statusBarStyle", setStatusBarStyle},
        {NULL, NULL}
    };
    lvL_openlib(L, "Navigation", fs, 0);
    return 0;
}

@end

//        if ( lv_type(L, 2) ==LV_TNUMBER ) {
//            NSUInteger color = lv_tonumber(L, 2);
//            float a = ( (color>>24)&0xff )/255.0;
//            float r = ( (color>>16)&0xff )/255.0;
//            float g = ( (color>>8)&0xff )/255.0;
//            float b = ( (color>>0)&0xff )/255.0;
//            if( a==0 ){
//                a = 1;
//            }
//            if( lv_gettop(L)>=3 && lv_type(L, 3) ==LV_TNUMBER){
//                a = lv_tonumber(L, 3);
//            }
//            UIColor* c  = [UIColor colorWithRed:r green:g blue:b alpha:a];
//            [navBar setTintColor:c];
//            [navBar setBarTintColor:c];
//        }



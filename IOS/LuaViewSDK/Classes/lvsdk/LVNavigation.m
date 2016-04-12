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

static int setTitle (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        if( lv_type(L, 1)== LV_TSTRING ) {// 第一种: string
            NSString* title = lv_paramString(L, 1);
            if( [vc respondsToSelector:@selector(lv_setNavigationItemTitle:)] ) {
                [vc performSelector:@selector(lv_setNavigationItemTitle:) withObject:title];
            } else {
                vc.navigationItem.title = title;
            }
            return 0;
        } else if( lv_type(L, 1)== LV_TUSERDATA ) {//第二种: 复合文本
            LVUserDataInfo * user2 = lv_touserdata(L, 1);
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
        if( [vc respondsToSelector:@selector(lv_setNavigationItemLeftBarButtonItems:)] ) {
            [vc performSelector:@selector(lv_setNavigationItemLeftBarButtonItems:) withObject:buttonItems];
        } else {
            vc.navigationItem.leftBarButtonItems = buttonItems;
        }
    }
    return 0; /* number of results */
}

static int setRightButton (lv_State *L) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
        NSArray* buttonItems = [LVNavigation getNavigationItems:L];
        if( [vc respondsToSelector:@selector(lv_setNavigationItemRightBarButtonItems:)] ){
            [vc performSelector:@selector(lv_setNavigationItemRightBarButtonItems:) withObject:buttonItems];
        } else {
            vc.navigationItem.rightBarButtonItems = buttonItems;
        }
    }
    return 0; /* number of results */
}

static int setBackground(lv_State*L ) {
    lv_clearFirstTableValue(L);
    LView* lview = (__bridge LView *)(L->lView);
    UIViewController* vc = lview.viewController;
    if( vc && lv_gettop(L)>=1 ) {
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
        {LUAVIEW_SYS_TABLE_KEY, setBackground},
        {NULL, NULL}
    };
    lvL_openlib(L, "Navigation", fs, 0);
    return 0;
}

@end




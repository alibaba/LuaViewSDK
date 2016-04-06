//
//  JHSLuaViewController.m
//  JU
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 ju.taobao.com. All rights reserved.
//

#import "JHSLuaViewController.h"
#import "LView.h"
#import "JHSLVCustomError.h"
#import "JHSLVCustomLoading.h"
#import "JHSLVCollectionView.h"
#import "JHSLVTableView.h"
#import "JHSLVButton.h"
#import "JHSLVImage.h"

#import "AppDelegate.h"


@interface JHSLuaViewController ()

@end

@implementation JHSLuaViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    
    [LVButton setDefaultStyle:[JHSLVButton class]];
    [LVImage setDefaultStyle:[JHSLVImage class]];
    [LVCollectionView setDefaultStyle:[JHSLVCollectionView class]];
    [LVTableView setDefaultStyle:[JHSLVTableView class]];
    
    //摇一摇本地代码
    [UIApplication sharedApplication].applicationSupportsShakeToEdit = YES;
}

- (void)willCreateLuaView {
    [super willCreateLuaView];
    
    while (self.view.subviews.count) {
        UIView* child = self.view.subviews.lastObject;
        [child removeFromSuperview];
    }
}

- (void)didCreateLuaView:(LView *)view {
    [super didCreateLuaView:view];
    
    // 注册 用户面板类型
    self.lv[@"CustomError"] = [JHSLVCustomError class];
    self.lv[@"CustomLoading"] = [JHSLVCustomLoading class];
    
    // 注册 外部对象.
    self.lv[@"viewController"] = self;
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    return UIStatusBarStyleDefault;
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self.lv viewDidAppear];
}
-(void) viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [self.lv viewDidDisAppear];
}

/**
 * openUrl API, 暴露给脚本调用
 */
-(void) openUrl:(NSString*)actionUrl{
    LVLog(@"%@",actionUrl);
}

-(void) gotoHistory{
    [self.navigationController popViewControllerAnimated:YES];
}

//-(void) lv_setNavigationItemTitleView:(UIView*) view{
//    
//}
//-(void) lv_setNavigationItemTitle:(NSString*) title{
//    
//}
//-(void) lv_setNavigationItemLeftBarButtonItems:(NSArray*) items{
//    
//}
//-(void) lv_setNavigationItemRightBarButtonItems:(NSArray*) items{
//    
//}
//-(void) lv_setNavigationBarBackgroundImage:(UIImage*) image{
//    
//}

-(NSDictionary*) testJson:(NSDictionary*) dic{
    NSLog(@"%@", dic);
    return dic;
}
@end

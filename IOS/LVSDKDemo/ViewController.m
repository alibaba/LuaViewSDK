//
//  ViewController.m
//  lv5.1.4
//
//  Created by dongxicheng on 11/5/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "ViewController.h"
#import "LVSDK.h"
#import "LVRSA.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "LVErrorView.h"
#import "JHSLuaErrorView.h"
#import "JHSLuaLoadingView.h"
#import "JHSLuaCollectionView.h"
#import "JHSLuaTableView.h"
#import "JHSLuaViewButton.h"
#import "JHSLuaViewImageView.h"


@interface ViewController ()
@property(nonatomic,strong) LView* lv;
@end


@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    {
        CGRect r = self.view.bounds;
        r.origin.x = 0;
        r.origin.y = 0;
        self.lv = [[LView alloc] initWithFrame:r];
        self.lv.viewController = self;
        [self.view addSubview:self.lv];
        
        self.lv[@"ViewController"] = self;//注册外部对象.
        
        [LVErrorView setDefaultStyle:[JHSLuaErrorView class]];
        [LVLoadingView setDefaultStyle:[JHSLuaLoadingView class]];
        [LVButton setDefaultStyle:[JHSLuaViewButton class]];
        [LVImageView setDefaultStyle:[JHSLuaViewImageView class]];
        [LVCollectionView setDefaultStyle:[JHSLuaCollectionView class]];
        [LVTableView setDefaultStyle:[JHSLuaTableView class]];
        
        
        //[lv runFile:@"gifDemo.lua"];// 帧动画测试
        //[self.lv runFile:@"mtopDemo.lua"];// mtop测试demo
        //[lv runFile:@"attributedFontDemo.lua"];//AttributedString 富文本 测试脚本
        //[self.lv runFile:@"tableViewDemo.lua"];// tableView脚本
        [self.lv runFile:@"collectionViewDemo.lua"];// CollectionView 测试
        //[lv runFile:@"testDebug.lua"];// 调试脚本
        //[self.lv runFile:@"animationSnow.lua"];// 下雪动画
        //[self.lv runFile:@"animationLiZi.lua"];// 粒子动画
        //[self.lv runFile:@"animationFire.lua"];// 火焰动画
        //[self.lv runFile:@"animationFire2.lua"];// 火焰动画3
        //[lv runFile:@"testunit.lua"];// 单元测试 脚本
        //[lv runFile:@"shake.lua"];//摇一摇测试脚本
        //[lv runFile:@"downloader.lua"];// 下载测试脚本
        //[lv runFile:@"downloader2.lua"];// 下载测试脚本
        //[self.lv runFile:@"testluabox.lua"];// 测试luabox
        //[self.lv runFile:@"flxNodeDemo.lua"];// 测试luabox
    }
    
    //摇一摇本地代码
    [UIApplication sharedApplication].applicationSupportsShakeToEdit = YES;
    [self becomeFirstResponder];
}

- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionBegan:motion withEvent:event];
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionCancelled:motion withEvent:event];
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionEnded:motion withEvent:event];
}

-(void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.lv viewWillAppear];
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self.lv viewDidAppear];
}

-(void) viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self.lv viewWillDisAppear];
}

-(void) viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [self.lv viewDidDisAppear];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

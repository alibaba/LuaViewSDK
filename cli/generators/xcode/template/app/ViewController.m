//
//  ViewController.m
//
//  Created by lv-cli on 16/2/23.
//  Copyright © 2016年 juhuasuan. All rights reserved.
//

#import "ViewController.h"
#import "AppDelegate.h"
#import "LView.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)willCreateLuaView {
    [super willCreateLuaView];
    
    while (self.view.subviews.count) {
        UIView* child = self.view.subviews.lastObject;
        [child removeFromSuperview];
    }
}

- (void)didCreateLuaView:(LView *)view {
    [super didCreateLuaView:view];

    [view.bundle changeCurrentPath:[AppDelegate lvSourcePath]];
}

@end

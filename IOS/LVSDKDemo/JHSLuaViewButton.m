//
//  LVButton.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/17/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "JHSLuaViewButton.h"
#import "LVBaseView.h"
#import "JHSLuaViewImageView.h"
#import "LVUtil.h"
#import "LView.h"
#import <SDWebImage/UIButton+WebCache.h>
#import "LVAttributedString.h"

@implementation JHSLuaViewButton

-(void) setWebImageUrl:(NSString *)url forState:(UIControlState)state finished:(LVLoadFinished)finished{
    [self setWebImageUrl:url forState:state finished:nil];
}

//----------------------------------------------------------------------------------------

@end

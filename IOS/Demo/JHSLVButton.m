//
//  JHSLVButton.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/17/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "JHSLVButton.h"
#import "LVBaseView.h"
#import "JHSLVImage.h"
#import "LVUtil.h"
#import "LView.h"
#import <SDWebImage/UIButton+WebCache.h>
#import "LVAttributedString.h"

@implementation JHSLVButton

-(void) setWebImageUrl:(NSString *)url forState:(UIControlState)state finished:(LVLoadFinished)finished{
    [self sd_setBackgroundImageWithURL:[NSURL URLWithString:url] forState:state completed:nil];
}

//----------------------------------------------------------------------------------------

@end

//
//  LVExButton.m
//  Pods
//
//  Created by OolongTea on 17/4/6.
//
//

#import "LVExButton.h"
#import <SDWebImage/UIButton+WebCache.h>

@implementation LVExButton

-(void) setWebImageUrl:(NSString *)url forState:(UIControlState)state finished:(LVLoadFinished)finished{
    [self sd_setBackgroundImageWithURL:[NSURL URLWithString:url] forState:state completed:nil];
}

@end

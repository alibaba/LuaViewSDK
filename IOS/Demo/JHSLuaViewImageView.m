//
//  LVImageView.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "JHSLuaViewImageView.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "LVData.h"
#import <SDWebImage/UIImageView+WebCache.h>


@implementation JHSLuaViewImageView


-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished{
    __weak JHSLuaViewImageView* weakImageView = self;
    [self sd_setImageWithURL:url placeholderImage:nil
                completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, NSURL* url){
                    double duration = (cacheType == SDImageCacheTypeNone && !error)?.4f:.0f;
                    if( duration>0 ) {
                        weakImageView.alpha = 0;
                        [UIView animateWithDuration:duration animations:^{
                            weakImageView.alpha = 1.0f;
                        }];
                    } else {
                        weakImageView.alpha = 1.0f;
                    }
                    if( finished ) {
                        finished();
                    }
                }];
}


@end

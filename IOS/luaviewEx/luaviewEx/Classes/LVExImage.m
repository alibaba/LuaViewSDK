//
//  LVExImage.m
//  Pods
//
//  Created by OolongTea on 17/4/6.
//
//

#import "LVExImage.h"
#import <SDWebImage/UIImageView+WebCache.h>

@implementation LVExImage

-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished{
    __weak LVExImage* weakImageView = self;
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
                           finished( error );
                       }
                   }];
}

@end

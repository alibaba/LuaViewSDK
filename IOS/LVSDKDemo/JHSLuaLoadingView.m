//
//  JHSLoadingView.m
//  LVSDK
//
//  Created by dongxicheng on 7/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "JHSLuaLoadingView.h"

@interface JHSLuaLoadingView ()
@property (nonatomic, strong) UIActivityIndicatorView* loading;
@end
@implementation JHSLuaLoadingView

- (id) initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if ( self ) {
        self.loading = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [self.loading sizeToFit];
        [self.loading startAnimating];
        [self addSubview:self.loading];
    }
    return self;
}

- (void) layoutSubviews{
    [super layoutSubviews];
    if( self.loading ) {
        self.loading.center = CGPointMake(self.frame.size.width/2, self.frame.size.height*0.45);
    }
}


@end

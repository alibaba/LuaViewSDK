//
//  JHSLVCustomError.m
//  LVSDK
//
//  Created by dongxicheng on 7/21/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "JHSLVCustomError.h"

@implementation JHSLVCustomError{
    UIView* bg;
}

-(id) initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if( self ) {
        bg = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 120, 100)];
        bg.backgroundColor = [UIColor clearColor];
        
        UIButton* button = [[UIButton alloc] initWithFrame:CGRectMake(0, 50, 120, 46)];
        [button setTitle:@"刷新" forState:UIControlStateNormal];
        button.backgroundColor = [UIColor whiteColor];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        button.layer.cornerRadius = button.frame.size.height/2;
        button.layer.borderWidth = 1;
        button.layer.borderColor = [UIColor blackColor].CGColor;
        button.showsTouchWhenHighlighted = YES;
        [bg addSubview:button];
        
        [self addSubview:bg];
        [button addTarget:self action:@selector(refleshButtonCall) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

-(void) refleshButtonCall{
    [self callLuaWithArguments:@[@"reflesh",@"test",@"temp" ] ];
    [self callLuaWithArgument:@"refleshend"];
}

-(void) layoutSubviews{
    [super layoutSubviews];
    bg.center = CGPointMake(self.frame.size.width/2, self.frame.size.height/2);
}

-(id)lv_getNativeView{
    return bg;
}

@end

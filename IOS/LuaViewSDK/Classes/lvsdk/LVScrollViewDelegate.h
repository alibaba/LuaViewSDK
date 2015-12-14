//
//  LVScrollViewDelegate.h
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface LVScrollViewDelegate : NSObject<UIScrollViewDelegate>

@property(nonatomic,weak) UIView* owner;
@property(nonatomic,weak) id delegate;

-(id) init:(UIView*) view;

@end

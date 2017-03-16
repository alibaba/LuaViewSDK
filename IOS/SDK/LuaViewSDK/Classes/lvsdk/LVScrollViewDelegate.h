/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@interface LVScrollViewDelegate : NSObject<UIScrollViewDelegate>

@property(nonatomic,weak) UIView* owner;
@property(nonatomic,weak) id delegate;

-(id) init:(UIView*) view;

@end

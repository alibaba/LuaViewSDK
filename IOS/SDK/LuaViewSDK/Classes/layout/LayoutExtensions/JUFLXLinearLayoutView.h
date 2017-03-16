/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "JUFLXNode.h"

/**
 *  Try to solve two kinds of problems.
 *  1. Know all the child views' size and layout them, bonus this container can be autoAdjustFrame 
 *     according to child views' size and contentInset and lineSpacing;
 *  2. Know some child view's size(not all), auto flex the remain views in this container.
 *  
 *  If you want more customization, sugguest JUFLXNode, also this class is build on this, and it support it.
 */
@interface JUFLXLinearLayoutView : UIView

@property (nonatomic, strong) NSArray *childViews;

@property (nonatomic, assign) UIEdgeInsets contentInset;

@property (nonatomic, assign) JUFLXLayoutDirection layoutDirection;

/**
 *  If it is yes, the scrollView frame size will be changed depend on childViews' total size.
 *  If some views' size is not be setted, set it to be YES won't flex those pending views' size
 *  default is NO.
 */
@property (nonatomic, assign) BOOL autoAdjustFrameSize;

@property (nonatomic, assign) CGFloat lineSpacing;

- (void)layout;

@end

//
//  UIView+JUFLXNode.h
//  all_layouts
//
//  Created by xiekw on 15/7/8.
//  Copyright (c) 2015年 xiekw. All rights reserved.
//

#import <UIKit/UIKit.h>
@class JUFLXNode;

typedef void (^NodeDidFinishLayout)(JUFLXNode *node);

@interface UIView (JUFLXNodeGetter)

- (JUFLXNode *)ju_flxNode;
- (BOOL)ju_hasFlxNode;
- (void)setJu_flxNodeDidFinishLayoutBlock:(NodeDidFinishLayout)block;

@end

FOUNDATION_EXTERN NSString * const JUFLXNodeUIViewNodeKey;
FOUNDATION_EXTERN NSString * const JUFLXNodeUIViewDidFinishBlockKey;
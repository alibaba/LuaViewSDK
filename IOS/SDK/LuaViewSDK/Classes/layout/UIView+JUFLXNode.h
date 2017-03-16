/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "JUFLXNode.h"

typedef void (^NodeDidFinishLayout)(JUFLXNode *node);

@interface UIView (JUFLXNodeGetter)

- (JUFLXNode *)ju_flxNode;
- (BOOL)ju_hasFlxNode;
- (void)setJu_flxNodeDidFinishLayoutBlock:(NodeDidFinishLayout)block;

@end

@interface UIView (JUFLXNodes)

@property (nonatomic, strong) NSArray *flx_childViews;
/**
 *  Default is view's frame's origin
 */
//@property (nonatomic, assign) CGPoint flx_viewOrigin;

/*
 * ---------------------------------------------------------------------------
 * As Container's property
 * ---------------------------------------------------------------------------
 */

@property (nonatomic, assign) BOOL flx_absolute;

@property (nonatomic, assign) UIEdgeInsets flx_absoluteEdgeInsets;

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#flex-direction-property
 *  inlineCSS -> flex-direction
 *  Default is row
 */
@property (nonatomic, assign) JUFLXLayoutDirection flx_direction;

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#propdef-align-items
 *  inlineCSS -> align-items
 *  Default is stretch
 */
@property (nonatomic, assign) JUFLXLayoutAlignment flx_alignItems;

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#propdef-align-content
 *  inlineCSS -> align-content
 *  Default is stretch
 *  Here we don't support the between and around
 */
@property (nonatomic, assign) JUFLXLayoutAlignment flx_alignContent;

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#propdef-justify-content
 *  inlineCSS -> justify-content
 *  Default is start
 */
@property (nonatomic, assign) JUFLXLayoutJustifyContent flx_justifyContent;

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#flex-wrap-property
 *  When the container size doesn't satisfy, if use a new depends on this.
 *  inlineCSS -> flex-wrap
 *  Here we don't support wrap-reverse
 */
@property (nonatomic, assign) BOOL flx_flexWrap;


/*
 * ---------------------------------------------------------------------------
 * As Child's property
 * ---------------------------------------------------------------------------
 */

/**
 *  Check -> http://www.w3.org/TR/css3-flexbox/#propdef-align-self
 *  inlineCSS -> align-self
 *  Default is auto, means depend on containers' alignItems;
 */
@property (nonatomic, assign) JUFLXLayoutAlignment flx_alignSelf;

/**
 *  Means two thing: 1. If self use the remaining space 2. how much would u share with others
 *  Flex-grow, eg: A node has three children---a,b,c; a.flex = 1, b.flex = 2, c.flex = 1, then a's size will be 1 / (1 + 2 + 1),
 *  b is 2 / (1 + 2 + 1), c is 1 / (1 + 2 + 1)
 *  inlineCSS -> flex
 *  Here we only support flex-grow as flex, not include shrink.
 *  Default is 0
 */
@property (nonatomic, assign) CGFloat flx_flex;

/*
 * ---------------------------------------------------------------------------
 * As box's property
 * ---------------------------------------------------------------------------
 */

/**
 *  The style dimensions, default is the view's size, if you set it to {nan, nan},
 *  The result frame depends on children's total size, so the children's size should be measured.
 *  inlineCSS ->size, width, height
 *  It won't be affected by JUFLXLayoutAlignment
 */
@property (nonatomic, assign) CGSize flx_dimensions;

/**
 *  ensure the min dimension of the view size.
 *  inlineCSS -> min-width, min-height
 *  Default is {0,0}
 */
@property (nonatomic, assign) CGSize flx_minDimensions;

/**
 *  ensure the max dimension of the view size.
 *  inlineCSS -> max-width, max-height
 *  Default is {0,0}
 */
@property (nonatomic, assign) CGSize flx_maxDimensions;

/**
 *  The margin of the view.
 *  inlineCSS -> margin, margin-top, margin-left, margin-bottom, margin-right
 */
@property (nonatomic, assign) UIEdgeInsets flx_margin;

/**
 *  The padding of the view.
 *  inlineCSS -> padding, padding-top, padding-left, padding-bottom, padding-right
 */
@property (nonatomic, assign) UIEdgeInsets flx_padding;

/**
 *  When encouter the view size should be calculated like text size, use this block.
 *  If direction is row, then width is the parent's node's frame's width, otherwise is nan.
 *  It will be affected by JUFLXLayoutAlignment
 */
@property (nonatomic, copy) CGSize (^flx_measure)(CGFloat width);

/**
 *  If isSizeToFit == YES, the measure block CGSize (^measure)(CGFloat width) will returned by view's
 *  sizeThatFits: size, the property will be override by measure block;
 *  Default is YES;
 *  It will be affected by JUFLXLayoutAlignment
 *  inlineCSS -> sizetofit: 1
 */
@property (nonatomic, assign) BOOL flx_sizeToFit;

- (void)flx_bindingInlineCSS:(NSString *)inlineCSS;

- (void)flx_layout;

- (void)flx_layoutAsync:(BOOL)async;

- (void)flx_layoutAsync:(BOOL)async completion:(void(^)(CGRect frame))block;

@end

FOUNDATION_EXTERN NSString * const JUFLXNodeUIViewNodeKey;
FOUNDATION_EXTERN NSString * const JUFLXNodeUIViewDidFinishBlockKey;

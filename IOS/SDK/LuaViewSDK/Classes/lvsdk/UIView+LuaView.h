/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface UIView(UIViewLuaView)<LVProtocal>



@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 设置Callback时需要注册手势回调,才设置成true

-(void) lv_callbackAddClickGesture;// 回调添加

- (void) lv_alignSubviews;

- (void) lv_alignSelfWithSuperRect:(CGRect) rect;

-(id) lv_getNativeView;

// shapelayer
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;
-(CALayer*) lv_layer;
-(void) lv_createShapelayer:(NSArray<NSNumber*>*) arr;

-(void) lv_effectParallax:(CGFloat)dx dy:(CGFloat)dy;
-(void) lv_effectClick:(NSInteger)color alpha:(CGFloat)alpha;

@property(nonatomic,assign) BOOL lv_canvas;

@end

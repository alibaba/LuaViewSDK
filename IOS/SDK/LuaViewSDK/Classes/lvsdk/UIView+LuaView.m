/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "UIView+LuaView.h"
#import "LuaViewCore.h"
#import "LVHeads.h"
#import "NSObject+LuaView.h"

@implementation UIView (UIViewLuaView)

-(BOOL) lv_isCallbackAddClickGesture{
    return NO;
}
-(void) setLv_isCallbackAddClickGesture:(BOOL)lv_isCallbackAddClickGesture{
}

-(void) lv_callbackAddClickGesture {
    if( self.lv_isCallbackAddClickGesture ){
        self.lv_isCallbackAddClickGesture = NO;
        UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(lv_buttonCallBack)];
        self.userInteractionEnabled = YES;
        [self addGestureRecognizer:gesture];
    }
}

// align

-(NSUInteger) lv_align{
    return 0;
}

-(void) setLv_align:(NSUInteger)lv_align{
}

- (void) lv_alignSubviews {
    CGRect rect = self.frame;
    NSArray* subviews = [self subviews];
    for( UIView* view in subviews){
        [view lv_alignSelfWithSuperRect:rect];
    }
}

- (void) lv_alignSelfWithSuperRect:(CGRect) rect{
    NSUInteger ali = self.lv_align;
    if( ali ){
        CGRect r0 = [self frame];
        CGRect r = r0;
        if( ali&LV_ALIGN_LEFT ) {
            r.origin.x = 0;
        } else if( ali&LV_ALIGN_H_CENTER ) {
            r.origin.x = (rect.size.width-r.size.width)/2;
        } else if( ali&LV_ALIGN_RIGHT ) {
            r.origin.x = rect.size.width-r.size.width;
        }
        if( ali&LV_ALIGN_TOP ) {
            r.origin.y = 0;
        } else if( ali&LV_ALIGN_V_CENTER ) {
            r.origin.y = (rect.size.height-r.size.height)/2;
        } else if( ali&LV_ALIGN_BOTTOM ) {
            r.origin.y = (rect.size.height-r.size.height);
        }
        if( !CGRectEqualToRect(r0, r) ) {
            self.frame = r;
        }
    }
}

-(id) lv_getNativeView{
    return self;
}

-(CAShapeLayer*) lv_shapeLayer{
    return nil;
}

-(void) setLv_shapeLayer:(CAShapeLayer *)lv_shapeLayer{
}

-(CALayer*) lv_layer{
    if( self.lv_shapeLayer ){
        return self.lv_shapeLayer;
    }
    return self.layer;
}

-(void) lv_createShapelayer:(NSArray<NSNumber*>*)arr{
    [self.lv_shapeLayer removeFromSuperlayer];
    self.lv_shapeLayer = nil;
    
    CAShapeLayer *borderLayer = [CAShapeLayer layer];
    borderLayer.bounds = self.bounds;
    borderLayer.position = CGPointMake(CGRectGetMidX(self.bounds), CGRectGetMidY(self.bounds));
    borderLayer.lineWidth = self.layer.borderWidth;
    
    borderLayer.fillColor = [UIColor clearColor].CGColor;
    borderLayer.strokeColor = self.layer.borderColor;
    borderLayer.path = [UIBezierPath bezierPathWithRoundedRect:borderLayer.bounds cornerRadius:self.layer.cornerRadius].CGPath;
    borderLayer.lineDashPattern = arr;
    self.lv_shapeLayer = borderLayer;
    
    if( self.lv_shapeLayer ) {
        self.layer.borderColor = [UIColor clearColor].CGColor;
        [self.layer addSublayer:self.lv_shapeLayer];
    }
}

-(void) lv_effectParallax:(CGFloat)dx dy:(CGFloat)dy{
    // 视差效果
}

-(void) lv_effectClick:(NSInteger)color alpha:(CGFloat)alpha{
    // 点击特效
}

-(BOOL) lv_canvas{
    return NO;
}

-(void) setLv_canvas:(BOOL)lv_canvas{
}

@end

//
//  UIView+LuaView.m
//  LVSDK
//
//  Created by dongxicheng on 7/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "UIView+LuaView.h"
#import "LView.h"
#import "LVHeads.h"

@implementation UIView (UIViewLuaView)

- (LView*) lv_lview{
    return nil;
}

- (void) setLv_lview:(LView *)lview{
}

- (LVUserDataInfo*)lv_userData{
    return nil;
}

- (void) setLv_userData:(LVUserDataInfo *)userData{
}

- (void) lv_callLuaByKey1:(NSString*) key1{
    [self lv_callLuaByKey1:key1 key2:nil argN:0];
}

- (void) lv_callLuaByKey1:(NSString*) key1 key2:(NSString*) key2 argN:(int)argN{
    lua_State* l = self.lv_lview.l;
    if( l && self.lv_userData && key1){
        lua_checkstack32(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if( lua_type(l, -1) == LUA_TTABLE ) {
            lua_getfield(l, -1, STR_CALLBACK);
            if( lua_type(l, -1)==LUA_TNIL ) {
                lua_remove(l, -1);
            } else {
                lua_remove(l, -2);
            }
        }
        [LVUtil call:l key1:key1.UTF8String key2:key2.UTF8String key3:NULL nargs:argN nrets:0 retType:LUA_TNONE];
    }
}

-(NSString*) lv_callLua:(NSString*) functionName args:(NSArray*) args{
    lua_State* L = self.lv_lview.l;
    if( L ){
        lua_checkstack(L, (int)args.count*2 + 2);
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(L,obj);
        }
        lua_getglobal(L, functionName.UTF8String);// function
        return lv_runFunctionWithArgs(L, (int)args.count, 0);
    }
    return nil;
}
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

-(void) lv_buttonCallBack{
    lua_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        int num = lua_gettop(L);
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        if( lua_type(L, -1)==LUA_TTABLE ) {
            lua_getfield(L, -1, STR_ON_CLICK);
        }
        lv_runFunction(L);
        lua_settop(L, num);
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

- (id) lv_nativeObject{
    return self;
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

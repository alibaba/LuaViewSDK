//
//  UIView+LuaView.m
//  LVSDK
//
//  Created by dongxicheng on 7/24/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "UIView+LuaView.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

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
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData && key1){
        lv_checkStack32(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if( lv_type(l, -1) == LV_TTABLE ) {
            lv_getfield(l, -1, STR_CALLBACK);
            if( lv_type(l, -1)==LV_TNIL ) {
                lv_remove(l, -1);
            } else {
                lv_remove(l, -2);
            }
        }
        [LVUtil call:l key1:key1.UTF8String key2:key2.UTF8String key3:NULL nargs:argN nrets:0 retType:LV_TNONE];
    }
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
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        int num = lv_gettop(L);
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        if( lv_type(L, -1)==LV_TTABLE ) {
            lv_getfield(L, -1, STR_ON_CLICK);
        }
        lv_runFunction(L);
        lv_settop(L, num);
    }
}

- (void) lv_runCallBack:(const char*) key{
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        int num = lv_gettop(L);
        lv_pushUserdata(L, self.lv_userData);
        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
        if( lv_type(L, -1)==LV_TTABLE && key ) {
            lv_getfield(L, -1, key);
        }
        lv_runFunction(L);
        lv_settop(L, num);
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


@end

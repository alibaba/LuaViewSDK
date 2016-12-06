//
//  LVCanvas.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/5.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVCanvas.h"

@implementation LVCanvas


-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(id) lv_nativeObject{
    return self;
}

-(void) drawRect:(CGFloat) x :(CGFloat)y :(CGFloat) w :(CGFloat)h{
    if( _contentRef ) {
        CGContextAddRect(_contentRef, CGRectMake(x, y, w, h));
        CGContextDrawPath(_contentRef, kCGPathFill);
    }
}

static int drawRect (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat w = lv_tonumber(L, 4);
        CGFloat h = lv_tonumber(L, 5);
        [canvas drawRect:x :y :w :h];
        return 1;
    }
    return 0;
}

-(void) drawRoundRect:(CGFloat) x :(CGFloat)y :(CGFloat)w :(CGFloat)h  :(CGFloat)rx :(CGFloat)ry{
    CGContextRef context = _contentRef;
    if( context ) {
        // 简便起见，这里把圆角半径设置为长和宽平均值的1/10
        CGFloat radius = rx;
        
        // 获取CGContext，注意UIKit里用的是一个专门的函数
        CGContextRef context = UIGraphicsGetCurrentContext();
        // 移动到初始点
        CGContextMoveToPoint(context, radius,y+ 0);
        
        // 绘制第1条线和第1个1/4圆弧
        CGContextAddLineToPoint(context, x+ w - radius, y+0);
        CGContextAddArc(context,  x+w - radius, y+ radius, radius, -0.5 * M_PI, 0.0, 0);
        
        // 绘制第2条线和第2个1/4圆弧
        CGContextAddLineToPoint(context,  x+ w,  y+ h - radius);
        CGContextAddArc(context,  x+ w - radius, y+ h - radius, radius, 0.0, 0.5 * M_PI, 0);
        
        // 绘制第3条线和第3个1/4圆弧
        CGContextAddLineToPoint(context,  x+radius, h);
        CGContextAddArc(context,  x+ radius, y+ h - radius, radius, 0.5 * M_PI, M_PI, 0);
        
        // 绘制第4条线和第4个1/4圆弧
        CGContextAddLineToPoint(context,  x+ 0,  y+ radius);
        CGContextAddArc(context,  x+ radius, y+ radius, radius, M_PI, 1.5 * M_PI, 0);
        
        // 闭合路径
        CGContextClosePath(context);
        CGContextDrawPath(context, kCGPathFill);
    }
}

static int drawRoundRect (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat w = lv_tonumber(L, 4);
        CGFloat h = lv_tonumber(L, 5);
        CGFloat rx = lv_tonumber(L, 6);
        CGFloat ry = lv_tonumber(L, 7);
        [canvas drawRoundRect:x :y :w :h :rx :ry];
        return 1;
    }
    return 0;
}

-(void) drawLine:(CGFloat) x1 :(CGFloat)y1 :(CGFloat) x2 :(CGFloat) y2{
    if( _contentRef ) {
        CGPoint aPoints[2];
        aPoints[0] = CGPointMake(x1, y1);
        aPoints[1] = CGPointMake(x2, y2);
        CGContextAddLines(_contentRef, aPoints, 2);
        CGContextDrawPath(_contentRef, kCGPathStroke); //根据坐标绘制路径
    }
}

static int drawLine (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x1 = lv_tonumber(L, 2);
        CGFloat y1 = lv_tonumber(L, 3);
        CGFloat x2 = lv_tonumber(L, 4);
        CGFloat y2 = lv_tonumber(L, 5);
        [canvas drawLine:x1 :y1 :x2 :y2];
        return 1;
    }
    return 0;
}

static int drawPoint (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x1 = lv_tonumber(L, 2);
        CGFloat y1 = lv_tonumber(L, 3);
        [canvas drawLine:x1 :y1 :x1 :y1];
        return 1;
    }
    return 0;
}



static void releaseCanvasUserData(LVUserDataInfo* user){
    if( user && user->object ){
        LVCanvas* canvas = CFBridgingRelease(user->object);
        user->object = NULL;
        if( canvas ){
            canvas.lv_userData = NULL;
            canvas.lv_lview = nil;
            canvas.contentRef = nil;
        }
    }
}

static int lvCanvasGC (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseCanvasUserData(user);
    return 0;
}

static int lvNewCanvas (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCanvas class]];
    
    LVCanvas* canvas = [[c alloc] init:L];

    {
        NEW_USERDATA(userData, Canvas);
        userData->object = CFBridgingRetain(canvas);
        canvas.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Canvas );
        lv_setmetatable(L, -2);
    }
    return 1;
}

+(int) createLuaCanvas:(lv_State *)L  contentRef:(CGContextRef) contentRef{
    LVCanvas* lvCanvas = [[LVCanvas alloc] init:L];
    lvCanvas.contentRef = contentRef;
    {
        NEW_USERDATA(userData, Canvas);
        userData->object = CFBridgingRetain(lvCanvas);
        lvCanvas.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Canvas );
        lv_setmetatable(L, -2);
    }
    return 1;
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCanvas globalName:globalName defaultName:@"Canvas"];
    
    const struct lvL_reg memberFunctions [] = {
        {"__gc", lvCanvasGC },
        
        {"drawRect",drawRect},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Canvas);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    return 0;
}

@end

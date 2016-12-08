//
//  LVCanvas.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/5.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVCanvas.h"
#import "LView.h"
#import "LVBundle.h"
#import "LVData.h"

@interface LVCanvas ()
@property(nonatomic,strong) UIColor* color;
@property(nonatomic,assign) CGFloat strokeWidth;
@property(nonatomic,assign) CGFloat alpha;
@property(nonatomic,assign) UIFont* font;
@end

@implementation LVCanvas


-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.strokeWidth = 0.5;
    }
    return self;
}

-(id) lv_nativeObject{
    return self;
}

-(void) setColor:(UIColor *)color{
    _color = color;
    if( _contentRef ) {
        CGContextSetStrokeColorWithColor(_contentRef,self.color.CGColor);
        CGContextSetFillColorWithColor(_contentRef,self.color.CGColor);
    }
}

-(void) setAlpha:(CGFloat)alpha{
    _alpha = alpha;
    if (_contentRef) {
        const CGFloat *components = CGColorGetComponents(self.color.CGColor);
        CGContextSetRGBStrokeColor(_contentRef, components[0], components[1], components[2], _alpha);
        CGContextSetRGBFillColor(_contentRef, components[0], components[1], components[2], _alpha);
    }
}

static int nativeObj (lv_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( userData ){
        LVCanvas* view = (__bridge LVCanvas *)(userData->object);
        if( view ){
            id object = [view lv_nativeObject];
            lv_pushNativeObjectWithBox(L, object);
            return 1;
        }
    }
    return 0;
}

static int canvas_drawPoint (lv_State *L) {
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

-(void) drawLine:(CGFloat) x1 :(CGFloat)y1 :(CGFloat) x2 :(CGFloat) y2{
    if( _contentRef ) {
        CGPoint aPoints[2];
        aPoints[0] = CGPointMake(x1, y1);
        aPoints[1] = CGPointMake(x2, y2);
        CGContextSetLineWidth(_contentRef,self.strokeWidth);
        CGContextAddLines(_contentRef, aPoints, 2);
        CGContextDrawPath(_contentRef, kCGPathStroke); //根据坐标绘制路径
    }
}

static int canvas_drawLine (lv_State *L) {
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

-(void) drawRect:(CGFloat)x :(CGFloat)y :(CGFloat)w :(CGFloat)h{
    if( _contentRef ) {
        CGContextSetLineWidth(_contentRef,self.strokeWidth);
        CGContextAddRect(_contentRef, CGRectMake(x, y, w, h));
        CGContextDrawPath(_contentRef, self.drawingMode);
    }
}

static int canvas_drawRect (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x1 = lv_tonumber(L, 2);
        CGFloat y1 = lv_tonumber(L, 3);
        CGFloat x2 = lv_tonumber(L, 4);
        CGFloat y2 = lv_tonumber(L, 5);
        [canvas drawRect:x1 :y1 :x2 :y2];
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
        CGContextMoveToPoint(context,x+ radius,y+ 0);
        
        // 绘制第1条线和第1个1/4圆弧
        CGContextAddLineToPoint(context, x+ w - radius, y+0);
        CGContextAddArc(context,  x+w - radius, y+ radius, radius, -0.5 * M_PI, 0.0, 0);
        
        // 绘制第2条线和第2个1/4圆弧
        CGContextAddLineToPoint(context,  x+ w,  y+ h - radius);
        CGContextAddArc(context,  x+ w - radius, y+ h - radius, radius, 0.0, 0.5 * M_PI, 0);
        
        // 绘制第3条线和第3个1/4圆弧
        CGContextAddLineToPoint(context,  x+radius, y+ h);
        CGContextAddArc(context,  x+ radius, y+ h-radius, radius, 0.5 * M_PI, M_PI, 0);
        
        // 绘制第4条线和第4个1/4圆弧
        CGContextAddLineToPoint(context,  x+ 0,  y+ radius);
        CGContextAddArc(context,  x+ radius, y+ radius, radius, M_PI, 1.5 * M_PI, 0);
        
        // 闭合路径
        CGContextClosePath(context);
        CGContextDrawPath(context, self.drawingMode);
    }
}

static int canvas_drawRoundRect (lv_State *L) {
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

-(void) drawEllipse:(CGFloat) x :(CGFloat)y :(CGFloat)rx :(CGFloat)ry  {
    CGContextRef context = _contentRef;
    if( context && rx>=0 && ry>= 0 ) {
        //画椭圆
        CGContextAddEllipseInRect(context, CGRectMake(x-rx, y-ry, rx*2, ry*2)); //椭圆
        CGContextDrawPath(context, kCGPathFillStroke);
    }
}

-(void) drawText:(NSString *)text :(UIFont *)font :(CGRect)rect{
    CGContextRef context = _contentRef;
    if (context && text) {
        //写文字
        NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:font,NSFontAttributeName,self.color,NSForegroundColorAttributeName,nil];
        [text drawInRect:rect withAttributes:attributes];
    }
}

static int canvas_drawEllipse (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat rx = lv_tonumber(L, 4);
        CGFloat ry = rx;
        if( lv_type(L, 5)==LV_TNUMBER ) {
            ry = lv_tonumber(L, 5);
        }
        [canvas drawEllipse:x :y :rx :ry];
        return 1;
    }
    return 0;
}

static int canvas_color (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            UIColor* color = lv_getColorFromStack(L, 2);
            canvas.color = color;
            return 0;
        } else {
            UIColor* color = canvas.color;
            NSUInteger c = 0;
            CGFloat a = 0;
            if( lv_uicolor2int(color, &c,&a) ){
                lv_pushnumber(L, c );
                lv_pushnumber(L, a);
                return 2;
            }
        }
    }
    return 0;
}

static int canvas_strokeWidth (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            canvas.strokeWidth = lv_tonumber(L, 2);
            return 0;
        } else {
            lv_pushnumber(L, canvas.strokeWidth );
            return 1;
        }
    }
    return 0;
}

static int canvas_style (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            canvas.drawingMode = lv_tonumber(L, 2);
            return 0;
        } else {
            lv_pushnumber(L, canvas.drawingMode );
            return 1;
        }
    }
    return 0;
}

static int canvas_resetPaint (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        canvas.color = [UIColor blackColor];
        [canvas clipRect:0 :0 :10240 :10240];
        canvas.strokeWidth = 0.5;
    }
    return 0;
}

static int canvas_alpha (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CGFloat alpha = lv_tonumber(L, 2);
            canvas.alpha = alpha;
            return 0;
        } else {
            return 1;
        }
    }
    return 0;
}

static int canvas_textSize (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CGFloat font = lv_tonumber(L, 2);
            canvas.font = [UIFont systemFontOfSize:font];
            return 0;
        } else {
            return 1;
        }
    }
    return 0;
}

static int canvas_bold (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CGFloat bold = lv_toboolean(L, 2);
            if (bold) {
                canvas.font = [UIFont boldSystemFontOfSize:canvas.font.pointSize];
            }else{
                canvas.font = [UIFont systemFontOfSize:canvas.font.pointSize];
            }
            return 0;
        } else {
            return 1;
        }
    }
    return 0;
}

-(void) clipRect:(CGFloat)x :(CGFloat)y :(CGFloat)w :(CGFloat)h{
    if( _contentRef ) {
        CGContextClipToRect(_contentRef,CGRectMake(x, y, w, h));
    }
}

static int canvas_clipRect (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int drawText (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            const char* text = lv_tolstring(L, 2, NULL);
            NSString *str = [NSString stringWithCString:text encoding:NSUTF8StringEncoding];
            CGFloat x = lv_tonumber(L, 3);
            CGFloat y = lv_tonumber(L, 4);
            CGRect rect = CGRectMake(x, y, canvas.font.lineHeight * str.length, canvas.font.lineHeight+10);
            [canvas drawText:str :canvas.font :rect];
            return 0;
        } else {
            return 1;
        }
    }
    return 0;
}

static int canvas_drawOval (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_drawArc (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_drawImage (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        UIImage* image = nil;
        if ( lv_type(L, 2)==LV_TSTRING ) {
            NSString* imageName = lv_paramString(L, 2);// 2
            image = [canvas.lv_lview.bundle imageWithName:imageName];
        } else if ( lv_type(L, 2)==LV_TUSERDATA ) {
//            LVUserDataInfo * userdata = (LVUserDataInfo *)lv_touserdata(L, 2);
//            LVData* lvdata = (__bridge LVData *)(userdata->object);
//            image = [[UIImage alloc] initWithData:lvdata.data];
        }
        if( image ) {
            
        }
    }
    return 0;
}

static int canvas_save (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_restore (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_rotate (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_skew (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_scale (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
    }
    return 0;
}

static int canvas_translate (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
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

+(LVCanvas*) createLuaCanvas:(lv_State *)L  contentRef:(CGContextRef) contentRef{
    LVCanvas* lvCanvas = [[LVCanvas alloc] init:L];
    lvCanvas.contentRef = contentRef;
    {
        NEW_USERDATA(userData, Canvas);
        userData->object = CFBridgingRetain(lvCanvas);
        lvCanvas.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_Canvas );
        lv_setmetatable(L, -2);
    }
    return lvCanvas;
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCanvas globalName:globalName defaultName:@"Canvas"];
    
    const struct lvL_reg memberFunctions [] = {
        {"__gc", lvCanvasGC },
        {"nativeObj", nativeObj},
        
        
        {"drawPoint",canvas_drawPoint},
        {"drawLine",canvas_drawLine},
        {"drawRect",canvas_drawRect},
        {"drawRoundRect",canvas_drawRoundRect},
        {"drawCircle",canvas_drawEllipse},
        {"drawEllipse",canvas_drawEllipse},
        {"drawText",drawText},
        {"drawOval",canvas_drawOval},
        {"drawArc",canvas_drawArc},
        {"drawImage",canvas_drawImage},
        
        {"color",canvas_color},
        {"alpha",canvas_alpha},
        {"strokeWidth",canvas_strokeWidth},
        {"style",canvas_style},
        {"textSize",canvas_textSize},
        
        {"resetPaint",canvas_resetPaint},
        {"save",canvas_save},
        {"restore",canvas_restore},
        
        {"rotate",canvas_rotate},
        {"skew",canvas_skew},
        {"scale",canvas_scale},
        {"translate",canvas_translate},
        {"bold",canvas_bold},
        
        
        {"clipRect",canvas_clipRect},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Canvas);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    
    {
        // PaintStyle 常量
        lv_settop(L, 0);
        NSDictionary* v = nil;
        v = @{
              @"FILL":    @(kCGPathFill),
              @"EOFILL":   @(kCGPathEOFill),
              @"STROKE":   @(kCGPathStroke),
              @"FILLSTROKE":   @(kCGPathFillStroke),
              @"EOFILLSTROKE":   @(kCGPathEOFillStroke),
        };
        [LVUtil defineGlobal:@"PaintStyle" value:v L:L];
    }
    return 0;
}

@end

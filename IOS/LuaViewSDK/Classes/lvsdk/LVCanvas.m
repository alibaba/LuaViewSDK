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
#import "LVImage.h"

#define LV_ANGLE_RADIANS(angle) (M_PI*angle/180)

@interface LVCanvas ()
@property(nonatomic,strong) UIColor* color;
@property(nonatomic,assign) CGFloat strokeWidth;
@property(nonatomic,assign) CGFloat alpha;
@property(nonatomic,assign) UIFont* font;
@property(nonatomic,assign) CGAffineTransform concatCTM;
@property(nonatomic,assign) CGFloat scaleX;
@property(nonatomic,assign) CGFloat scaleY;
@property(nonatomic,assign) CGFloat skewX;
@property(nonatomic,assign) CGFloat skewY;
@property(nonatomic,assign) CGPoint translate;
@end

@implementation LVCanvas


-(id) init:(lua_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) setContentRef:(CGContextRef)contentRef{
    _contentRef = contentRef;
    [self resetPaint];
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
//        CGFloat r = 0;
//        CGFloat g = 0;
//        CGFloat b = 0;
//        CGFloat a = 0;
//        if( [_color getRed:&r green:&g blue:&b alpha:&a] ){
//            CGContextSetRGBStrokeColor(_contentRef, r, g, b, _alpha);
//            CGContextSetRGBFillColor(_contentRef, r, g, b, _alpha);
//        }
        // 控制左右的绘图alpha
        CGContextSetAlpha(_contentRef,alpha);
    }
}

static int nativeObj (lua_State *L) {
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

static int canvas_drawPoint (lua_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x1 = lv_tonumber(L, 2);
        CGFloat y1 = lv_tonumber(L, 3);
        [canvas drawLine:x1-0.5 :y1-0.5 :x1+0.5 :y1+0.5];
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

static int canvas_drawLine (lua_State *L) {
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
        CGContextAddRect(_contentRef, CGRectMake(x, y, w, h));
        CGContextDrawPath(_contentRef, self.drawingMode);
    }
}

static int canvas_drawRect (lua_State *L) {
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

static int canvas_drawRoundRect (lua_State *L) {
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

-(void) drawEllipse:(CGFloat) x :(CGFloat)y :(CGFloat)w :(CGFloat)h  {
    CGContextRef context = _contentRef;
    if( context && w>=0 && h>= 0 ) {
        //画椭圆
        CGContextAddEllipseInRect(context, CGRectMake(x, y, w, h)); //椭圆
        CGContextDrawPath(context, self.drawingMode);
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

-(void) drawText:(NSString *)text :(UIFont *)font :(CGFloat)x :(CGFloat) y{
    CGContextRef context = _contentRef;
    if (context && text) {
        //画文字
        NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:font,NSFontAttributeName,self.color,NSForegroundColorAttributeName,nil];
        [text drawAtPoint:CGPointMake(x, y) withAttributes:attributes];
    }
}

static int canvas_drawEllipse (lua_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat w = lv_tonumber(L, 4);
        CGFloat h = w;
        if( lv_type(L, 5)==LV_TNUMBER ) {
            h = lv_tonumber(L, 5);
        }
        [canvas drawEllipse:x :y :w :h];
        return 1;
    }
    return 0;
}

static int canvas_drawCircle (lua_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVCanvas* canvas = (__bridge LVCanvas *)(user1->object);
    if( LVIsType(user1, Canvas)  ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat r = lv_tonumber(L, 4);
        [canvas drawEllipse:x-r :y-r :r*2 :r*2];
        return 1;
    }
    return 0;
}

static int canvas_color (lua_State *L) {
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

-(void) setStrokeWidth:(CGFloat)strokeWidth{
    _strokeWidth = strokeWidth;
    if( _contentRef ) {
        CGContextSetLineWidth(_contentRef, strokeWidth);
    }
}

static int canvas_strokeWidth (lua_State *L) {
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

static int canvas_style (lua_State *L) {
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

-(void) resetPaint{
    [self clipRect:0 :0 :10240 :10240];
    self.color = [UIColor blackColor];
    self.strokeWidth = 0.5;
    self.alpha = 1;
    self.drawingMode = kCGPathFill;
    self.font = [UIFont systemFontOfSize:12];
    [self scale:1 :1];
    [self rotate:0 :0 :0];
    [self translate:0 :0];
    [self skew:0 :0];
}

static int canvas_resetPaint (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        [canvas resetPaint];
    }
    return 0;
}

static int canvas_alpha (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CGFloat alpha = lv_tonumber(L, 2);
            canvas.alpha = alpha;
            return 0;
        }
    }
    return 0;
}

static int canvas_textSize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CGFloat font = lv_tonumber(L, 2);
            canvas.font = [UIFont systemFontOfSize:font];
            return 0;
        } else {
            return 0;
        }
    }
    return 0;
}

static int canvas_bold (lua_State *L) {
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
            return 0;
        }
    }
    return 0;
}

-(void) clipRect:(CGFloat)x :(CGFloat)y :(CGFloat)w :(CGFloat)h{
    if( _contentRef ) {
        CGContextClipToRect(_contentRef,CGRectMake(x, y, w, h));
    }
}

static int canvas_clipRect (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user && lv_gettop(L)>=5 ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat w = lv_tonumber(L, 4);
        CGFloat h = lv_tonumber(L, 5);
        [canvas clipRect:x :y :w :h];
    }
    return 0;
}

static int drawText (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        if( lv_gettop(L)>=2 ) {
            const char* text = lv_tolstring(L, 2, NULL);
            NSString *str = [NSString stringWithCString:text encoding:NSUTF8StringEncoding];
            CGFloat x = lv_tonumber(L, 3);
            CGFloat y = lv_tonumber(L, 4);
            CGFloat h = canvas.font.lineHeight;
            CGFloat leading = canvas.font.leading ;
            CGFloat descender = canvas.font.descender;
            CGRect rect = CGRectMake(x, y - (h+leading+descender), h * str.length, h );
            [canvas drawText:str :canvas.font :rect];
            return 0;
        } else {
            return 0;
        }
    }
    return 0;
}

static int canvas_drawOval (lua_State *L) {
    return canvas_drawEllipse(L);
}

-(void) drawArc:(CGFloat)x :(CGFloat)y :(CGFloat)w :(CGFloat)h :(CGFloat)startAngle :(CGFloat)endAngle :(BOOL) includeCenter{
    if( _contentRef ) {
        x += w/2;
        y += h/2;
        if( includeCenter ) {
            CGContextMoveToPoint(_contentRef, x, y);
        }
        CGContextAddArc(_contentRef, x, y, w/2, LV_ANGLE_RADIANS(startAngle), LV_ANGLE_RADIANS(endAngle), NO);
        CGContextClosePath(_contentRef);
        CGContextDrawPath(_contentRef, self.drawingMode);
    }
}

static int canvas_drawArc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        CGFloat w = lv_tonumber(L, 4);
        CGFloat h = lv_tonumber(L, 5);
        CGFloat startAngle = lv_tonumber(L, 6);
        CGFloat endAngle = lv_tonumber(L, 7);
        BOOL includeCenter = lv_toboolean(L, 8);
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        [canvas drawArc:x :y :w :h :startAngle :endAngle :includeCenter];
    }
    return 0;
}

-(void) drawImage:(UIImage*)image :(CGFloat)x :(CGFloat)y :(CGFloat)w :(CGFloat)h {
    if( _contentRef && image) {
        CGContextSaveGState(_contentRef);
        //CGAffineTransform t1 = CGAffineTransformMake(1, self.skewY, self.skewX, 1, 0, 0);
        CGAffineTransform t2 = CGAffineTransformMake(1, 0, 0, -1, 0, 0);
        //CGAffineTransform t3  = CGAffineTransformConcat(t1, t2);
        CGContextConcatCTM(_contentRef, t2 );
        CGContextDrawImage(_contentRef, CGRectMake(x, -y-h , w, h), image.CGImage);
        CGContextRestoreGState(_contentRef);
    }
}

static int canvas_drawImage (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        UIImage* image = nil;
        if ( lv_type(L, 2)==LV_TSTRING ) {
            NSString* imageName = lv_paramString(L, 2);// 2
            image = [canvas.lv_lview.bundle imageWithName:imageName];
        } else if ( lv_type(L, 2)==LV_TUSERDATA ) {
            LVUserDataInfo * userdata = (LVUserDataInfo *)lv_touserdata(L, 2);
            if( LVIsType(userdata, View) ){
                LVImage* lvImage = (__bridge LVImage *)(userdata->object);
                if( [lvImage isKindOfClass:[LVImage class]] ) {
                    image = lvImage.image;
                }
            } else if( LVIsType(userdata, Data) ) {
                LVData* lvdata = (__bridge LVData *)(userdata->object);
                image = [[UIImage alloc] initWithData:lvdata.data];
            }
        }
        if( image ) {
            CGFloat x = lv_tonumber(L, 3);
            CGFloat y = lv_tonumber(L, 4);
            CGFloat w = lv_tonumber(L, 5);
            CGFloat h = lv_tonumber(L, 6);
            [canvas drawImage:image :x :y :w :h];
        }
    }
    return 0;
}

-(void) saveGState {
    if( _contentRef ) {
        CGContextSaveGState(_contentRef);
    }
}

static int canvas_save (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        [canvas saveGState];
    }
    return 0;
}

-(void) restoreGState {
    if( _contentRef ) {
        CGContextRestoreGState(_contentRef);
    }
}

static int canvas_restore (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        [canvas restoreGState];
    }
    return 0;
}

-(void) rotate:(CGFloat) angle :(CGFloat)x :(CGFloat) y{
    if( _contentRef ) {
        CGContextTranslateCTM(_contentRef, x, y);
        CGContextRotateCTM(_contentRef,LV_ANGLE_RADIANS(angle) );
        CGContextTranslateCTM(_contentRef, -x, -y);
    }
}

static int canvas_rotate (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        CGFloat angle = lv_tonumber(L, 2);
        CGFloat x = 0;
        if( lv_type(L, 3) ) {
            x = lv_tonumber(L, 3);
        }
        CGFloat y = 0;
        if( lv_type(L, 4) ) {
            y = lv_tonumber(L, 4);
        }
        [canvas rotate:angle :x :y];;
    }
    return 0;
}

-(void) skew:(CGFloat)sx :(CGFloat)sy {
    if( _contentRef ) {
        self.skewX = sx;
        self.skewY = sy;
        CGAffineTransform transform = CGAffineTransformMake(1, sy, sx, 1, 0, 0);
        CGContextConcatCTM(_contentRef,transform);
    }
}

-(void) setConcatCTM:(CGAffineTransform) transform{
    CGContextConcatCTM(_contentRef,transform);
}

-(CGAffineTransform) concatCTM{
    return CGContextGetCTM(_contentRef);
}

static int canvas_skew (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        CGFloat sx = lv_tonumber(L, 2);
        CGFloat sy = 0;
        if (lv_type(L, 3)==LV_TNUMBER ) {
            sy = lv_tonumber(L, 3);
        }
        [canvas skew:sx :sy];
    }
    return 0;
}

-(void) scale:(CGFloat)scaleX :(CGFloat)scaleY {
    if( _contentRef ) {
        self.scaleX = scaleX;
        self.scaleY = scaleY;
        CGContextScaleCTM(_contentRef, scaleX, scaleY);
    }
}

static int canvas_scale (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        CGFloat scaleX = lv_tonumber(L, 2);
        CGFloat scaleY = scaleX;
        if( lv_type(L, 3)==LV_TNUMBER ) {
            scaleY = lv_tonumber(L, 3);
        }
        [canvas scale:scaleX :scaleY];
    }
    return 0;
}

-(void) translate:(CGFloat)x :(CGFloat)y {
    self.translate = CGPointMake(x, y);
    if( _contentRef ) {
        CGContextTranslateCTM(_contentRef, x, y);
    }
}

static int canvas_translate (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCanvas* canvas = (__bridge LVCanvas *)(user->object);
        CGFloat x = lv_tonumber(L, 2);
        CGFloat y = lv_tonumber(L, 3);
        [canvas translate:x :y];
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

static int lvCanvasGC (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseCanvasUserData(user);
    return 0;
}

static int lvNewCanvas (lua_State *L) {
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

+(LVCanvas*) createLuaCanvas:(lua_State *)L  contentRef:(CGContextRef) contentRef{
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

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCanvas globalName:globalName defaultName:@"Canvas"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"__gc", lvCanvasGC },
        {"nativeObj", nativeObj},
        
        
        {"drawPoint",canvas_drawPoint},
        {"drawLine",canvas_drawLine},
        {"drawRect",canvas_drawRect},
        {"drawRoundRect",canvas_drawRoundRect},
        {"drawCircle",canvas_drawCircle},
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

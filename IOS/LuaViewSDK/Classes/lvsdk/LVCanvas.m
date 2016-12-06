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

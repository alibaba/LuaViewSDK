//
//  LVCustomView.m
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/5.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVCustomView.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVCanvas.h"

@interface LVCustomView ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVCustomView

-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
    }
    return self;
}

-(void) drawRect:(CGRect)rect{
    [super drawRect:rect];
    
    lv_State* L = self.lv_lview.l;
    if( L ) {
        lv_settop(L, 0);
         CGContextRef contextRef = UIGraphicsGetCurrentContext();
        LVCanvas* canvas = [LVCanvas createLuaCanvas:L contentRef:contextRef];
        [self lv_callLuaByKey1:@STR_ON_DRAW key2:nil argN:1];
        canvas.contentRef = NULL;
    }
}

#pragma -mark CustomView
static int lvNewCustomView (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCustomView class]];
    
    {
        LVCustomView* customView = [[c alloc] init:L];
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(customView);
            customView.lv_userData = userData;
            
            lvL_getmetatable(L, META_TABLE_CustomView );
            lv_setmetatable(L, -2);
        }
        LView* father = (__bridge LView *)(L->lView);
        if( father ){
            [father containerAddSubview:customView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int onDraw (lv_State *L) {
    return lv_setCallbackByKey(L, STR_ON_DRAW, NO);
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCustomView globalName:globalName defaultName:@"CustomView"];
    
    const struct lvL_reg memberFunctions [] = {
        {"onDraw" , onDraw},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_CustomView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    return 1;
}

@end

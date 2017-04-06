/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCustomView.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVCanvas.h"

@interface LVCustomView ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVCustomView

-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
    }
    return self;
}

-(void) drawRect:(CGRect)rect{
    [super drawRect:rect];
    
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_settop(L, 0);
         CGContextRef contextRef = UIGraphicsGetCurrentContext();
        LVCanvas* canvas = [LVCanvas createLuaCanvas:L contentRef:contextRef];
        [self lv_callLuaCallback:@STR_ON_DRAW key2:nil argN:1];
        canvas.contentRef = NULL;
    }
}

#pragma -mark CustomView
static int lvNewCustomView (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCustomView class]];
    
    {
        LVCustomView* customView = [[c alloc] init:L];
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(customView);
            customView.lv_userData = userData;
            
            luaL_getmetatable(L, META_TABLE_CustomView );
            lua_setmetatable(L, -2);
        }
        LuaViewCore* father = LV_LUASTATE_VIEW(L);
        if( father ){
            [father containerAddSubview:customView];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int onDraw (lua_State *L) {
    return lv_setCallbackByKey(L, STR_ON_DRAW, NO);
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewCustomView globalName:globalName defaultName:@"CustomView"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"onDraw" , onDraw},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_CustomView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    return 1;
}

@end

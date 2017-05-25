/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVButton.h"
#import "LVBaseView.h"
#import "LVImage.h"
#import "LVUtil.h"
#import "LView.h"
#import "LVStyledString.h"
#import "UIView+LuaView.h"
#import "NSObject+LuaView.h"
#import "LVHeads.h"

@interface  LVButton()
@end

@implementation LVButton

-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        [self addTarget:self action:@selector(lvButtonCallBack) forControlEvents:UIControlEventTouchUpInside];
        
        // 默认黑色字
        [self setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        self.clipsToBounds = YES;
        self.titleLabel.font = [UIFont systemFontOfSize:14];// 默认字体大小
    }
    return self;
}

-(void) dealloc{
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<Button(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

-(void) lvButtonCallBack{
    [self lv_buttonCallBack];
}

-(void) setWebImageUrl:(NSString*)url forState:(UIControlState) state finished:(LVLoadFinished) finished{
}

-(void) setImageUrl:(NSString*) url placeholder:(UIImage *)placeholder state:(UIControlState) state {
    if( [LVUtil isExternalUrl:url] ){
        [self setWebImageUrl:url forState:state finished:nil];
    } else {
        if( url ) {
            LVBundle* bundle = self.lv_luaviewCore.bundle;
            [self setImage:[bundle imageWithName:url] forState:state];
        }
    }
}

#pragma -mark Button
static int lvNewButton (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVButton class]];
    
    {
        LVButton* button = [[c alloc] init:L];
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(button);
            button.lv_userData = userData;
            
            luaL_getmetatable(L, META_TABLE_UIButton );
            lua_setmetatable(L, -2);
        }
        LuaViewCore* father = LV_LUASTATE_VIEW(L);
        if( father ){
            [father containerAddSubview:button];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int selected (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( button ){
            if ( lua_gettop(L)>=2 ) {
                BOOL yes = lua_toboolean(L, 2);
                button.selected = yes;
                return 0;
            } else {
                lua_pushboolean(L, button.selected );
                return 1;
            }
        }
    }
    return 0;
}

static int enabled (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if ( lua_gettop(L)>=2 ){
            BOOL yes = lua_toboolean(L, 2);
            button.enabled = yes;
            return 0;
        } else {
            lua_pushboolean(L, button.enabled);
            return 1;
        }
    }
    return 0;
}

static int image (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        NSString* normalImage = lv_paramString(L, 2);// 2
        NSString* hightLightImage = lv_paramString(L, 3);// 2
        //NSString* disableImage = lv_paramString(L, 4);// 2
        //NSString* selectedImage = lv_paramString(L, 5);// 2
        LVButton* button = (__bridge LVButton *)(user->object);
        if( [button isKindOfClass:[LVButton class]] ){
            [button setImageUrl:normalImage placeholder:nil state:UIControlStateNormal];
            [button setImageUrl:hightLightImage placeholder:nil state:UIControlStateHighlighted];
            //[button setImageUrl:disableImage placeholder:nil state:UIControlStateDisabled];
            //[button setImageUrl:selectedImage placeholder:nil state:UIControlStateSelected];
            
            lua_pushvalue(L, 1);
            return 1;
        }
    }
    return 0;
}

static const UIControlState g_states[] = {UIControlStateNormal,UIControlStateHighlighted,UIControlStateDisabled,UIControlStateSelected};
static int title (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( [button isKindOfClass:[LVButton class]] ){
            int num = lua_gettop(L);
            if ( num>=2 ) {// setValue
                for (int i=2,j=0; i<=num && j<4; i++ ){
                    if ( lua_type(L, i) == LUA_TSTRING ) {
                        NSString* text1 = lv_paramString(L, i);
                        if( text1 ) {
                            [button setTitle:text1 forState:g_states[j++]];
                        }
                    } else if( lua_type(L, 2)==LUA_TUSERDATA ){
                        LVUserDataInfo * user2 = lua_touserdata(L, 2);
                        if( user2 && LVIsType(user2, StyledString) ) {
                            LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                            [button setAttributedTitle:attString.mutableStyledString forState:g_states[j++]  ];
                            [button.titleLabel sizeToFit];
                        }
                    }else if ( lua_type(L, i) == LUA_TNUMBER ) {
                        float f = lua_tonumber(L, i);
                        [button setTitle:[NSString stringWithFormat:@"%f",f] forState:g_states[j++]];
                    }
                }
                return 0;
            } else { // getValue
                for (int j=0; j<4; j++ ){
                    NSString* text1 = [button titleForState:g_states[j] ];
                    lua_pushstring(L, text1.UTF8String);
                }
                return 4;
            }
        }
    }
    return 0;
}

static int titleColor (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( [button isKindOfClass:[LVButton class]] ){
            int num = lua_gettop(L);
            if ( num>=2 ) {
                for (int i=2,j=0; i<=num && j<4; i++ ){
                    if( lua_type(L, i)==LUA_TNUMBER ) {
                        UIColor* c = lv_getColorFromStack(L, i);
                        [button setTitleColor:c forState:g_states[j++]];
                    }
                }
                return 0;
            } else {
                int retvalueNum = 0;
                for (int j=0; j<4; j++ ){
                    UIColor* c = [button titleColorForState:g_states[j++] ];
                    NSUInteger color=0 ;
                    CGFloat a = 0;
                    if( lv_uicolor2int(c, &color, &a) ){
                        lua_pushnumber(L, color);
                        lua_pushnumber(L, a);
                        retvalueNum += 2;
                    }
                }
                return retvalueNum;
            }
        }
    }
    return 0;
}

static int font (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* view = (__bridge LVButton *)(user->object);
        if( [view isKindOfClass:[LVButton class]] ){
            int num = lua_gettop(L);
            if( num>=2 ) {
                if( num>=3 && lua_type(L, 2)==LUA_TSTRING ) {
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lua_tonumber(L, 3);
                    view.titleLabel.font = [LVUtil fontWithName:fontName size:fontSize bundle:luaView.bundle];
                } else {
                    float fontSize = lua_tonumber(L, 2);
                    view.titleLabel.font = [UIFont systemFontOfSize:fontSize];
                }
                return 0;
            } else {
                UIFont* font = view.titleLabel.font;
                lua_pushstring(L, font.fontName.UTF8String);
                lua_pushnumber(L, font.pointSize);
                return 2;
            }
        }
    }
    return 0;
}

static int fontSize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVButton* view = (__bridge LVButton *)(user->object);
        if( [view isKindOfClass:[LVButton class]] ){
            int num = lua_gettop(L);
            if( num>=2 ) {
                float fontSize = lua_tonumber(L, 2);
                view.titleLabel.font = [UIFont systemFontOfSize:fontSize];
                return 0;
            } else {
                UIFont* font = view.titleLabel.font;
                lua_pushnumber(L, font.pointSize);
                return 1;
            }
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewButton globalName:globalName defaultName:@"Button"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"image",    image},
        
        {"font",    font},
        {"fontSize",    fontSize},
        {"textSize",    fontSize}, // __deprecated_msg("Use lines")

        
        {"titleColor",    titleColor}, // __deprecated_msg("Use lines")
        {"title",    title}, // __deprecated_msg("Use lines")
        {"textColor",    titleColor},
        {"text",    title},

        {"selected",    selected}, // __deprecated_msg("Use lines")
        {"enabled",    enabled}, // __deprecated_msg("Use lines")
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_UIButton);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    
    return 1;
}


//----------------------------------------------------------------------------------------

@end

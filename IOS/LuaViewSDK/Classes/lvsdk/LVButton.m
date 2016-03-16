//
//  LVButton.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/17/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVButton.h"
#import "LVBaseView.h"
#import "LVImage.h"
#import "LVUtil.h"
#import "LView.h"
#import "LVStyledString.h"
#import "UIView+LuaView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface  LVButton()
@end

@implementation LVButton

-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
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
            LVBundle* bundle = self.lv_lview.bundle;
            [self setImage:[bundle imageWithName:url] forState:state];
        }
    }
}

static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVButton class]] ) {
        g_class = c;
    }
}

#pragma -mark Button
static int lvNewButton (lv_State *L) {
    if( g_class == nil ){
        g_class = [LVButton class];
    }
    {
        LVButton* button = [[g_class alloc] init:L];
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(button);
            button.lv_userData = userData;
            
            lvL_getmetatable(L, META_TABLE_UIButton );
            lv_setmetatable(L, -2);
        }
        LView* father = (__bridge LView *)(L->lView);
        if( father ){
            [father containerAddSubview:button];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int selected (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( button ){
            if ( lv_gettop(L)>=2 ) {
                BOOL yes = lvL_checkbool(L, 2);
                button.selected = yes;
                return 0;
            } else {
                lv_pushboolean(L, button.selected );
                return 1;
            }
        }
    }
    return 0;
}

static int enabled (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if ( lv_gettop(L)>=2 ){
            BOOL yes = lvL_checkbool(L, 2);
            button.enabled = yes;
            return 0;
        } else {
            lv_pushboolean(L, button.enabled);
            return 1;
        }
    }
    return 0;
}

static int image (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
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
            
            lv_pushvalue(L, 1);
            return 1;
        }
    }
    return 0;
}

static const UIControlState g_states[] = {UIControlStateNormal,UIControlStateHighlighted,UIControlStateDisabled,UIControlStateSelected};
static int title (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( [button isKindOfClass:[LVButton class]] ){
            int num = lv_gettop(L);
            if ( num>=2 ) {// setValue
                for (int i=2,j=0; i<=num && j<4; i++ ){
                    if ( lv_type(L, i) == LV_TSTRING ) {
                        NSString* text1 = lv_paramString(L, i);
                        if( text1 ) {
                            [button setTitle:text1 forState:g_states[j++]];
                        }
                    } else if( lv_type(L, 2)==LV_TUSERDATA ){
                        LVUserDataInfo * user2 = lv_touserdata(L, 2);
                        if( user2 && LVIsType(user2, StyledString) ) {
                            LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                            [button setAttributedTitle:attString.mutableStyledString forState:g_states[j++]  ];
                            [button.titleLabel sizeToFit];
                        }
                    }else if ( lv_type(L, i) == LV_TNUMBER ) {
                        float f = lv_tonumber(L, i);
                        [button setTitle:[NSString stringWithFormat:@"%f",f] forState:g_states[j++]];
                    }
                }
                return 0;
            } else { // getValue
                for (int j=0; j<4; j++ ){
                    NSString* text1 = [button titleForState:g_states[j++] ];
                    lv_pushstring(L, text1.UTF8String);
                }
                return 4;
            }
        }
    }
    return 0;
}

static int titleColor (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* button = (__bridge LVButton *)(user->object);
        if( [button isKindOfClass:[LVButton class]] ){
            int num = lv_gettop(L);
            if ( num>=2 ) {
                for (int i=2,j=0; i<=num && j<4; i++ ){
                    if( lv_type(L, i)==LV_TNUMBER ) {
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
                        lv_pushnumber(L, color);
                        lv_pushnumber(L, a);
                        retvalueNum += 2;
                    }
                }
                return retvalueNum;
            }
        }
    }
    return 0;
}

static int font (lv_State *L) {
    LView* luaView = (__bridge LView *)(L->lView);
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* view = (__bridge LVButton *)(user->object);
        if( [view isKindOfClass:[LVButton class]] ){
            int num = lv_gettop(L);
            if( num>=2 ) {
                if( num>=3 && lv_type(L, 2)==LV_TSTRING ) {
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lv_tonumber(L, 3);
                    view.titleLabel.font = [LVUtil fontWithName:fontName size:fontSize bundle:luaView.bundle];
                } else {
                    float fontSize = lv_tonumber(L, 2);
                    view.titleLabel.font = [UIFont systemFontOfSize:fontSize];
                }
                return 0;
            } else {
                UIFont* font = view.titleLabel.font;
                lv_pushstring(L, font.fontName.UTF8String);
                lv_pushnumber(L, font.pointSize);
                return 2;
            }
        }
    }
    return 0;
}

static int fontSize (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVButton* view = (__bridge LVButton *)(user->object);
        if( [view isKindOfClass:[LVButton class]] ){
            int num = lv_gettop(L);
            if( num>=2 ) {
                float fontSize = lv_tonumber(L, 2);
                view.titleLabel.font = [UIFont systemFontOfSize:fontSize];
                return 0;
            } else {
                UIFont* font = view.titleLabel.font;
                lv_pushnumber(L, font.pointSize);
                return 1;
            }
        }
    }
    return 0;
}

//static int showsTouchWhenHighlighted(lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        LVButton* button = (__bridge LVButton *)(user->view);
//        if( lv_gettop(L)<=1 ) {
//            lv_pushboolean(L, button.showsTouchWhenHighlighted );
//            return 1;
//        } else {
//            BOOL yes = lvL_checkbool(L, 2);
//            button.showsTouchWhenHighlighted = yes;
//            return 0;
//        }
//    }
//    return 0;
//}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewButton);
        lv_setglobal(L, "Button");
    }
    const struct lvL_reg memberFunctions [] = {
        {"image",    image},
        
        {"font",    font},
        {"fontSize",    fontSize},
        {"textSize",    fontSize},
        
        {"titleColor",    titleColor},
        {"title",    title},
        {"textColor",    titleColor},
        {"text",    title},

        {"selected",    selected},
        {"enabled",    enabled},
        
        //{"showsTouchWhenHighlighted",    showsTouchWhenHighlighted},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_UIButton);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    
    return 1;
}


//----------------------------------------------------------------------------------------

@end

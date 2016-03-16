//
//  LVLabel.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVLabel.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVStyledString.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVLabel ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVLabel


-(id) init:(NSString*)imageName l:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.text = imageName;
        self.backgroundColor = [UIColor clearColor];
        self.textAlignment = NSTextAlignmentLeft;
        self.lv_isCallbackAddClickGesture = YES;
        self.clipsToBounds = YES;
        self.font = [UIFont systemFontOfSize:14];// 默认字体大小
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark UILabel
static int lvNewLabel(lv_State *L) {
    {
        NSString* text = lv_paramString(L, 1);// 5
        LVLabel* label = [[LVLabel alloc] init:text l:L];
        
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(label);
            label.lv_userData = userData;
            
            lvL_getmetatable(L, META_TABLE_UILabel );
            lv_setmetatable(L, -2);
        }
        LView* view = (__bridge LView *)(L->lView);
        if( view ){
            [view containerAddSubview:label];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

static int text (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ) {
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if ( [view isKindOfClass:[LVLabel class]] ) {
            if( lv_gettop(L)>=2 ) {
                if ( lv_isnoneornil(L, 2 ) ) {
                    view.text = nil;
                } else if( lv_type(L, 2)==LV_TNUMBER ){
                    CGFloat text = lv_tonumber(L, 2);// 2
                    view.text = [NSString stringWithFormat:@"%f",text];
                    return 0;
                } else if( lv_type(L, 2)==LV_TSTRING ){
                    NSString* text = lv_paramString(L, 2);// 2
                    view.text = text;
                    return 0;
                } else if( lv_type(L, 2)==LV_TUSERDATA ){
                    LVUserDataInfo * user2 = lv_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVLabel* view = (__bridge LVLabel *)(user->object);
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        [view setAttributedText:attString.mutableStyledString];
                        return 0;
                    }
                }
            } else {
                NSString* text = view.text;
                lv_pushstring(L, text.UTF8String);
                return 1;
            }
        }
    }
    return 0;
}

static int lineCount(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lv_gettop(L)>=2 ) {
            int number = lv_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.numberOfLines = number;
                return 0;
            }
        } else {
            lv_pushnumber(L, view.numberOfLines );
            return 1;
        }
    }
    return 0;
}

static int adjustFontSize(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        BOOL yes = lv_toboolean(L, 2);// 2
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            view.adjustsFontSizeToFitWidth = yes;
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int textColor (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lv_gettop(L)>=2 ) {
            if( [view isKindOfClass:[LVLabel class]] ){
                UIColor* color = lv_getColorFromStack(L, 2);
                view.textColor = color;
                return 0;
            }
        } else {
            UIColor* color = view.textColor;
            NSUInteger c = 0;
            CGFloat a = 0;
            if( lv_uicolor2int(color, &c, &a) ){
                lv_pushnumber(L, c );
                lv_pushnumber(L, a);
                return 2;
            }
        }
    }
    return 0;
}

static int font (lv_State *L) {
    LView* luaView = (__bridge LView *)(L->lView);
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( luaView && user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lv_gettop(L)>=2 ) {
                if( lv_gettop(L)>=3 && lv_type(L, 2)==LV_TSTRING ) {
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lv_tonumber(L, 3);
                    UIFont* font = [LVUtil fontWithName:fontName size:fontSize bundle:luaView.bundle];
                    view.font = font;
                } else {
                    float fontSize = lv_tonumber(L, 2);
                    view.font = [UIFont systemFontOfSize:fontSize];
                }
                return 0;
            } else {
                UIFont* font = view.font;
                NSString* fontName = font.fontName;
                CGFloat fontSize = font.pointSize;
                lv_pushstring(L, fontName.UTF8String);
                lv_pushnumber(L, fontSize);
                return 2;
            }
        }
    }
    return 0;
}


static int fontSize (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lv_gettop(L)>=2 ) {
                float fontSize = lv_tonumber(L, 2);
                view.font = [UIFont systemFontOfSize:fontSize];
                return 0;
            } else {
                UIFont* font = view.font;
                CGFloat fontSize = font.pointSize;
                lv_pushnumber(L, fontSize);
                return 1;
            }
        }
    }
    return 0;
}

static int textAlignment (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lv_gettop(L)>=2 ) {
            NSInteger align = lv_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.textAlignment = align;
                return 0;
            }
        } else {
            int align = view.textAlignment;
            lv_pushnumber(L, align );
            return 1;
        }
    }
    return 0;
}

static int ellipsize (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lv_gettop(L)>=2 ) {
            NSInteger lineBreakMode = lv_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.lineBreakMode = lineBreakMode;
                return 0;
            }
        } else {
            int lineBreakMode = view.lineBreakMode;
            lv_pushnumber(L, lineBreakMode );
            return 1;
        }
    }
    return 0;
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewLabel);
        lv_setglobal(L, "Label");
    }
    const struct lvL_reg memberFunctions [] = {
        {"text",    text},
        
        {"textColor",    textColor},
        
        {"font",    font},
        {"fontSize",    fontSize},
        {"textSize",    fontSize},
        
        {"ellipsize",    ellipsize},
        {"textAlign",    textAlignment},
        {"gravity",    textAlignment},
        
        {"lineCount",    lineCount},
        
        {"adjustFontSize",  adjustFontSize},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UILabel);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


-(NSString*) description{
    return [NSString stringWithFormat:@"<Label(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

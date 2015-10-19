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
#import "LVAttributedString.h"

@implementation LVLabel


-(id) init:(NSString*)imageName l:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.text = imageName;
        self.backgroundColor = [UIColor clearColor];
        self.textAlignment = NSTextAlignmentCenter;
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
            NEW_USERDATA(userData, LVUserDataView);
            userData->view = CFBridgingRetain(label);
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
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ) {
        LVLabel* view = (__bridge LVLabel *)(user->view);
        if ( [view isKindOfClass:[LVLabel class]] ) {
            if( lv_gettop(L)>=2 ) {
                if( lv_type(L, 2)==LV_TNUMBER ){
                    CGFloat text = lv_tonumber(L, 2);// 2
                    view.text = [NSString stringWithFormat:@"%f",text];
                    return 0;
                } else if( lv_type(L, 2)==LV_TSTRING ){
                    NSString* text = lv_paramString(L, 2);// 2
                    view.text = text;
                    return 0;
                } else if( lv_type(L, 2)==LV_TUSERDATA ){
                    LVUserDataAttributedString * user2 = lv_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, LVUserDataAttributedString) ) {
                        LVLabel* view = (__bridge LVLabel *)(user->view);
                        LVAttributedString* attString = (__bridge LVAttributedString *)(user2->attributedString);
                        [view setAttributedText:attString.mutableAttributedString];
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
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->view);
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

static int adjustsFontSizeToFitWidth(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        BOOL yes = lv_toboolean(L, 2);// 2
        LVLabel* view = (__bridge LVLabel *)(user->view);
        if( [view isKindOfClass:[LVLabel class]] ){
            view.adjustsFontSizeToFitWidth = yes;
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int textColor (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->view);
        if( lv_gettop(L)>=2 ) {
            NSUInteger color = lv_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                float a = ( (color>>24)&0xff )/255.0;
                float r = ( (color>>16)&0xff )/255.0;
                float g = ( (color>>8)&0xff )/255.0;
                float b = ( (color>>0)&0xff )/255.0;
                if( a==0 ){
                    a = 1;
                }
                if( lv_gettop(L)>=3 ){
                    a = lv_tonumber(L, 3);
                }
                view.textColor = [UIColor colorWithRed:r green:g blue:b alpha:a];
                return 0;
            }
        } else {
            UIColor* color = view.textColor;
            NSUInteger c = 0;
            float a = 0;
            if( lv_uicolor2int(color, &c, &a) ){
                lv_pushnumber(L, c );
                lv_pushnumber(L, a );
                return 2;
            }
        }
    }
    return 0;
}

static int font (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->view);
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lv_gettop(L)>=2 ) {
                if( lv_gettop(L)>=3 && lv_type(L, 2)==LV_TSTRING ) {
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lv_tonumber(L, 3);
                    view.font = [UIFont fontWithName:fontName size:fontSize];
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

static int textAlign (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->view);
        if( lv_gettop(L)>=2 ) {
            NSInteger align = lv_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                if( align&LV_ALIGN_H_CENTER ) {
                    align = NSTextAlignmentCenter;
                } else if( align&LV_ALIGN_RIGHT ) {
                    align = NSTextAlignmentRight;
                } else {//默认是LEFT
                    align = NSTextAlignmentLeft;
                }
                view.textAlignment = align;
                return 0;
            }
        } else {
            int align = view.textAlignment;
            if( align==NSTextAlignmentCenter ) {
                align = LV_ALIGN_H_CENTER;
            } else if( align==NSTextAlignmentRight ) {
                align = LV_ALIGN_RIGHT;
            } else {//默认是LEFT
                align = LV_ALIGN_LEFT;
            }
            lv_pushnumber(L, align );
            return 1;
        }
    }
    return 0;
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewLabel);
        lv_setglobal(L, "UILabel");
    }
    const struct lvL_reg memberFunctions [] = {
        {"setText", text},
        {"text",    text},
        
        {"setTextColor", textColor},
        {"textColor",    textColor},
        {"setColor", textColor},
        {"color",    textColor},
        
        {"setFont", font},
        {"font",    font},
        
        {"textAlign",    textAlign},
        
        {"setLineCount", lineCount},
        {"lineCount",    lineCount},
        
        {"adjustsFontSizeToFitWidth",  adjustsFontSizeToFitWidth},
        
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
    return [NSString stringWithFormat:@"<UILabel(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

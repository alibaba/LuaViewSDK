//
//  LVTextField.m
//  JU
//
//  Created by dongxicheng on 1/7/15.
//  Copyright (c) 2015 ju.taobao.com. All rights reserved.
//

#import "LVTextField.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVStyledString.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVTextField ()<UITextFieldDelegate>

@end

@implementation LVTextField


-(id) init:(lv_State*) l{
    self = [super initWithFrame:CGRectMake(0, 0, 100, 40)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        self.font = [UIFont systemFontOfSize:14];// 默认字体大小
    }
    return self;
}

-(void) dealloc{
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    // became first responder
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        [self lv_callLuaByKey1:@"BeginEditing"];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        [self lv_callLuaByKey1:@"EndEditing"];
    }
}

- (BOOL)textFieldShouldClear:(UITextField *)textField{
//    lv_State* l = self.lv_lview.l;
//    if( l ) {
//        lv_checkStack32(l);
//        if(  [LVUtil call:l lightUserData:self key:"清理"] ){
//            if(  lv_isboolean(l, -1) ){
//                return lv_toboolean(l, -1);
//            }
//        }
//    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
//    lv_State* l = self.lv_lview.l;
//    if( l ) {
//        lv_checkStack32(l);
//        if( [LVUtil call:l lightUserData:self key:"返回"]==0 ){
//            if(  lv_isboolean(l, -1) ){
//                return lv_toboolean(l, -1);
//            }
//        }
//    }
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    return YES;
}

#pragma -mark lvNewTextField
static int lvNewTextField (lv_State *L) {
    LVTextField* textFiled = [[LVTextField alloc] init:L];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(textFiled);
        textFiled.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UITextField );
        lv_setmetatable(L, -2);
    }
    LView* lview = (__bridge LView *)(L->lView);
    if( lview ){
        [lview containerAddSubview:textFiled];
    }
    return 1; /* new userdatum is already on the stack */
}

static int text (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->object);
        if( [view isKindOfClass:[LVTextField class]] ){
            if ( lv_gettop(L)>=2 ) {
                if( lv_type(L, 2)==LV_TUSERDATA ) {
                    LVUserDataInfo * user2 = lv_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        view.attributedText = attString.mutableStyledString;
                    }
                } else if( lv_type(L, 2)==LV_TSTRING ) {
                    NSString* text = lv_paramString(L, 2);// 2
                    view.text = text;
                }
                return 0;
            } else {
                NSString* s = view.text;
                if( s ) {
                    lv_pushstring(L, s.UTF8String);
                    return 1;
                }
                
                NSAttributedString* att =  view.attributedText;
                if( att ) {
                    LVStyledString* attString = [[LVStyledString alloc] init:L];
                    attString.mutableStyledString = [[NSMutableAttributedString alloc] init];
                    [attString.mutableStyledString appendAttributedString:att];
                    
                    NEW_USERDATA(userData, StyledString);
                    userData->object = CFBridgingRetain(attString);
                    attString.lv_userData = userData;
                    
                    lvL_getmetatable(L, META_TABLE_AttributedString );
                    lv_setmetatable(L, -2);
                    return 1;
                } else {
                    lv_pushnil(L);
                }
                return 1;
            }
        }
    }
    return 0;
}

static int placeholder (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->object);
        if( [view isKindOfClass:[LVTextField class]] ){
            if ( lv_gettop(L)>=2 ) {
                if( lv_type(L, 2)==LV_TSTRING ) {
                    NSString* text = lv_paramString(L, 2);// 2
                    view.placeholder = text;
                } else if( lv_type(L, 2)==LV_TUSERDATA ) {
                    LVUserDataInfo * user2 = lv_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        view.attributedPlaceholder = attString.mutableStyledString;
                    }
                }
                return 0;
            } else {
                NSString* s = view.placeholder;
                if( s ) {
                    lv_pushstring(L, s.UTF8String);
                } else {
                    lv_pushnil(L);
                }
            }
            return 1;
        }
    }
    return 0;
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewTextField);
        lv_setglobal(L, "TextField");
    }
    const struct lvL_reg memberFunctions [] = {
        {"text", text},
        {"hint", placeholder},
        {"placeholder", placeholder},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UITextField);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<TextField(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

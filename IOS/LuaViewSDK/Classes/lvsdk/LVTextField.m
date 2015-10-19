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

@interface LVTextField ()<UITextFieldDelegate>

@end

@implementation LVTextField


-(id) init:(lv_State*) l{
    self = [super initWithFrame:CGRectMake(0, 0, 100, 40)];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.backgroundColor = [UIColor clearColor];
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
        [LVUtil call:l lightUserData:self key:"开始编辑"];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        [LVUtil call:l lightUserData:self key:"结束编辑"];
    }
}

- (BOOL)textFieldShouldClear:(UITextField *)textField{
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        if(  [LVUtil call:l lightUserData:self key:"清理"] ){
            if(  lv_isboolean(l, -1) ){
                return lv_toboolean(l, -1);
            }
        }
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    lv_State* l = self.lv_lview.l;
    if( l ) {
        lv_checkStack32(l);
        if( [LVUtil call:l lightUserData:self key:"返回"]==0 ){
            if(  lv_isboolean(l, -1) ){
                return lv_toboolean(l, -1);
            }
        }
    }
    return YES;
}
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    return YES;
}
#pragma -mark lvNewTextField
static int lvNewTextField (lv_State *L) {
    LVTextField* textFiled = [[LVTextField alloc] init:L];
    {
        NEW_USERDATA(userData, LVUserDataView);
        userData->view = CFBridgingRetain(textFiled);
        
        lvL_getmetatable(L, META_TABLE_UITextField );
        lv_setmetatable(L, -2);
    }
    LView* lview = (__bridge LView *)(L->lView);
    if( lview ){
        [lview containerAddSubview:textFiled];
    }
    return 1; /* new userdatum is already on the stack */
}

static int setText (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        NSString* text = lv_paramString(L, 2);// 2
        LVTextField* view = (__bridge LVTextField *)(user->view);
        if( [view isKindOfClass:[LVTextField class]] ){
            view.text = text;
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int text (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->view);
        if( [view isKindOfClass:[LVTextField class]] ){
            NSString* s = view.text;
            if( s ) {
                lv_pushstring(L, s.UTF8String);
            } else {
                lv_pushnil(L);
            }
            return 1;
        }
    }
    return 0;
}
static int setPlaceholder (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        NSString* text = lv_paramString(L, 2);// 2
        LVTextField* view = (__bridge LVTextField *)(user->view);
        if( [view isKindOfClass:[LVTextField class]] ){
            view.placeholder = text;
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}
static int placeholder (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->view);
        if( [view isKindOfClass:[LVTextField class]] ){
            NSString* s = view.placeholder;
            if( s ) {
                lv_pushstring(L, s.UTF8String);
            } else {
                lv_pushnil(L);
            }
            return 1;
        }
    }
    return 0;
}
+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewTextField);
        lv_setglobal(L, "UITextField");
    }
    const struct lvL_reg memberFunctions [] = {
        {"setText", setText},
        {"text", text},
        {"setPlaceholder", setPlaceholder},
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
    return [NSString stringWithFormat:@"<UITextField(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

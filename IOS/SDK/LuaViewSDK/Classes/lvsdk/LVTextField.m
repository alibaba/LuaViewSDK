/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVTextField.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVStyledString.h"
#import "LVHeads.h"

@interface LVTextField ()<UITextFieldDelegate>

@end

@implementation LVTextField


-(id) init:(lua_State*) l{
    self = [super initWithFrame:CGRectMake(0, 0, 100, 40)];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
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
    lua_State* l = self.lv_luaviewCore.l;
    if( l ) {
        lua_checkstack32(l);
        [self lv_callLuaCallback:@"BeginEditing"];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    lua_State* l = self.lv_luaviewCore.l;
    if( l ) {
        lua_checkstack32(l);
        [self lv_callLuaCallback:@"EndEditing"];
    }
}

- (BOOL)textFieldShouldClear:(UITextField *)textField{
//    lua_State* l = self.lv_luaviewCore.l;
//    if( l ) {
//        lua_checkstack32(l);
//        if(  [LVUtil call:l lightUserData:self key:"清理"] ){
//            if(  lv_isboolean(l, -1) ){
//                return lua_toboolean(l, -1);
//            }
//        }
//    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField{
//    lua_State* l = self.lv_luaviewCore.l;
//    if( l ) {
//        lua_checkstack32(l);
//        if( [LVUtil call:l lightUserData:self key:"返回"]==0 ){
//            if(  lv_isboolean(l, -1) ){
//                return lua_toboolean(l, -1);
//            }
//        }
//    }
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string{
    return YES;
}

#pragma -mark lvNewTextField
static int lvNewTextField (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVTextField class]];
    
    LVTextField* textFiled = [[c alloc] init:L];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(textFiled);
        textFiled.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_UITextField );
        lua_setmetatable(L, -2);
    }
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    if( lview ){
        [lview containerAddSubview:textFiled];
    }
    return 1; /* new userdatum is already on the stack */
}

static int text (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->object);
        if( [view isKindOfClass:[LVTextField class]] ){
            if ( lua_gettop(L)>=2 ) {
                if( lua_type(L, 2)==LUA_TUSERDATA ) {
                    LVUserDataInfo * user2 = lua_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        view.attributedText = attString.mutableStyledString;
                    }
                } else if( lua_type(L, 2)==LUA_TSTRING ) {
                    NSString* text = lv_paramString(L, 2);// 2
                    view.text = text;
                }
                return 0;
            } else {
                NSString* s = view.text;
                if( s ) {
                    lua_pushstring(L, s.UTF8String);
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
                    
                    luaL_getmetatable(L, META_TABLE_AttributedString );
                    lua_setmetatable(L, -2);
                    return 1;
                } else {
                    lua_pushnil(L);
                }
                return 1;
            }
        }
    }
    return 0;
}

static int placeholder (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVTextField* view = (__bridge LVTextField *)(user->object);
        if( [view isKindOfClass:[LVTextField class]] ){
            if ( lua_gettop(L)>=2 ) {
                if( lua_type(L, 2)==LUA_TSTRING ) {
                    NSString* text = lv_paramString(L, 2);// 2
                    view.placeholder = text;
                } else if( lua_type(L, 2)==LUA_TUSERDATA ) {
                    LVUserDataInfo * user2 = lua_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        view.attributedPlaceholder = attString.mutableStyledString;
                    }
                }
                return 0;
            } else {
                NSString* s = view.placeholder;
                if( s ) {
                    lua_pushstring(L, s.UTF8String);
                } else {
                    lua_pushnil(L);
                }
            }
            return 1;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewTextField globalName:globalName defaultName:@"TextField"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"text", text},
        {"hint", placeholder},
        {"placeholder", placeholder}, //__deprecated_msg("Use hint")
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UITextField);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<TextField(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

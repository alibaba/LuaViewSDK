/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
#import "LVLabel.h"
#import "LVBaseView.h"
#import "LView.h"
#import "LVStyledString.h"
#import "LVHeads.h"

@interface LVLabel ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVLabel

-(id) init:(NSString*)imageName l:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
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
/*
 * lua脚本中Label() 对应的构造方法
 */
static int lvNewLabel(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVLabel class]];
    {
        NSString* text = lv_paramString(L, 1);// 5
        LVLabel* label = [[c alloc] init:text l:L];
        
        {
            NEW_USERDATA(userData, View);
            userData->object = CFBridgingRetain(label);
            label.lv_userData = userData;
            
            luaL_getmetatable(L, META_TABLE_UILabel );
            lua_setmetatable(L, -2);
        }
        LuaViewCore* view = LV_LUASTATE_VIEW(L);
        if( view ){
            [view containerAddSubview:label];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

/*
 * 脚本label实例对象label.text()方法对应的Native实现
 */
static int text (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ) {
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if ( [view isKindOfClass:[LVLabel class]] ) {
            if( lua_gettop(L)>=2 ) {
                if ( lua_isnoneornil(L, 2 ) ) {
                    view.text = nil;
                } else if( lua_type(L, 2)==LUA_TNUMBER ){
                    CGFloat text = lua_tonumber(L, 2);// 2
                    view.text = [NSString stringWithFormat:@"%f",text];
                    return 0;
                } else if( lua_type(L, 2)==LUA_TSTRING ){
                    NSString* text = lv_paramString(L, 2);// 2
                    view.text = text;
                    return 0;
                } else if( lua_type(L, 2)==LUA_TUSERDATA ){
                    LVUserDataInfo * user2 = lua_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVLabel* view = (__bridge LVLabel *)(user->object);
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        [view setAttributedText:attString.mutableStyledString];
                        return 0;
                    }
                }
            } else {
                NSString* text = view.text;
                lua_pushstring(L, text.UTF8String);
                return 1;
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.lineCount()方法对应的Native实现
 */
static int lineCount(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lua_gettop(L)>=2 ) {
            int number = lua_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.numberOfLines = number;
                return 0;
            }
        } else {
            lua_pushnumber(L, view.numberOfLines );
            return 1;
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.adjustFontSize()方法对应的Native实现
 */
static int adjustFontSize(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        BOOL yes = lua_toboolean(L, 2);// 2
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            view.adjustsFontSizeToFitWidth = yes;
            
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.textColor()方法对应的Native实现
 */
static int textColor (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lua_gettop(L)>=2 ) {
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
                lua_pushnumber(L, c );
                lua_pushnumber(L, a);
                return 2;
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.font()方法对应的Native实现
 */
static int font (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( luaView && user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lua_gettop(L)>=2 ) {
                if( lua_gettop(L)>=3 && lua_type(L, 2)==LUA_TSTRING ) {
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lua_tonumber(L, 3);
                    UIFont* font = [LVUtil fontWithName:fontName size:fontSize bundle:luaView.bundle];
                    view.font = font;
                } else {
                    float fontSize = lua_tonumber(L, 2);
                    view.font = [UIFont systemFontOfSize:fontSize];
                }
                return 0;
            } else {
                UIFont* font = view.font;
                NSString* fontName = font.fontName;
                CGFloat fontSize = font.pointSize;
                lua_pushstring(L, fontName.UTF8String);
                lua_pushnumber(L, fontSize);
                return 2;
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.fontSize()方法对应的Native实现
 */
static int fontSize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lua_gettop(L)>=2 ) {
                float fontSize = lua_tonumber(L, 2);
                view.font = [UIFont systemFontOfSize:fontSize];
                return 0;
            } else {
                UIFont* font = view.font;
                CGFloat fontSize = font.pointSize;
                lua_pushnumber(L, fontSize);
                return 1;
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.textAlign()方法对应的Native实现
 */
static int textAlignment (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lua_gettop(L)>=2 ) {
            NSInteger align = lua_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.textAlignment = align;
                return 0;
            }
        } else {
            int align = view.textAlignment;
            lua_pushnumber(L, align );
            return 1;
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.ellipsize()方法对应的Native实现
 */
static int ellipsize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);
        if( lua_gettop(L)>=2 ) {
            NSInteger lineBreakMode = lua_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.lineBreakMode = lineBreakMode;
                return 0;
            }
        } else {
            int lineBreakMode = view.lineBreakMode;
            lua_pushnumber(L, lineBreakMode );
            return 1;
        }
    }
    return 0;
}

/*
 * luaview所有扩展类的桥接协议: 只是一个静态协议, luaview统一调用该接口加载luaview扩展的类
 */
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    // 注册lua的构造方法: "Label" 对应的closure 是 "lvNewLabel"
    [LVUtil reg:L clas:self cfunc:lvNewLabel globalName:globalName defaultName:@"Label"];
    
    // lua Labe构造方法创建的对象对应的方法列表
    const struct luaL_Reg memberFunctions [] = {
        {"text",    text},
        
        {"textColor",    textColor},
        
        {"font",    font},
        {"fontSize",    fontSize},
        {"textSize",    fontSize}, // __deprecated_msg("Use fontSize")
        
        {"ellipsize",    ellipsize},
        {"textAlign",    textAlignment},
        {"gravity",    textAlignment},// 上中下 IOS 不支持,需要考虑支持
        
        {"lineCount",    lineCount}, // __deprecated_msg("Use lines")
        {"lines",    lineCount},
        
        {"adjustFontSize",  adjustFontSize},
        
        {NULL, NULL}
    };
    
    // 创建Label类的方法列表
    lv_createClassMetaTable(L, META_TABLE_UILabel);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0); // 继承基类View的所有方法列表
    luaL_openlib(L, NULL, memberFunctions, 0); // 当前类Label特有的方法列表
    
    const char* keys[] = { "addView", NULL};//列出需要移除的多余API
    lv_luaTableRemoveKeys(L, keys );// 移除冗余API 兼容安卓
    return 1;
}

/*
 * 脚本中print(obj)的时候会调用该接口 显示该对象的相关信息
 */
-(NSString*) description{
    return [NSString stringWithFormat:@"<Label(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

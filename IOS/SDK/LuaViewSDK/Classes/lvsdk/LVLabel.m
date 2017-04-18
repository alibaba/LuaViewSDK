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
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);// 获取luaview运行内核
        self.text = imageName;// 初始化Label初始text
        self.backgroundColor = [UIColor clearColor];// 默认背景色是透明色
        self.textAlignment = NSTextAlignmentLeft;// 默认是左对齐
        self.lv_isCallbackAddClickGesture = YES;// 默认允许添加事件监听
        self.clipsToBounds = YES;// 默认出界不可见
        self.font = [UIFont systemFontOfSize:14];// 默认字体大小
    }
    return self;
}

-(void) dealloc{
}

#pragma -mark UILabel
/*
 * lua脚本中 local label = Label() 对应的构造方法
 */
static int lvNewLabel(lua_State *L) {
    // 获取构造方法对应的Class(Native类)
    Class c = [LVUtil upvalueClass:L defaultClass:[LVLabel class]];
    {
        NSString* text = lv_paramString(L, 1);// 获取脚本传过来的第一个参数(约定是字符串类型)
        LVLabel* label = [[c alloc] init:text l:L];//通过Class和参数构造脚本对应的真实实例
        
        {
            NEW_USERDATA(userData, View);// 创建lua对象(userdata)
            userData->object = CFBridgingRetain(label);// 脚本对象引用native对象
            label.lv_userData = userData;//native对象引用脚本对象
            
            luaL_getmetatable(L, META_TABLE_UILabel ); // 获取Label对应的类方法列表
            lua_setmetatable(L, -2); // 设置刚才创建的lua对象的方法列表是类Label的方法列表
        }
        LuaViewCore* view = LV_LUASTATE_VIEW(L);// 获取当前LuaView对应的LuaViewCore
        if( view ){
            [view containerAddSubview:label]; // 把label对象加到LuaViewCore里面
        }
    }
    return 1; // 返回参数的个数
}

/*
 * 脚本label实例对象label.text()方法对应的Native实现
 */
static int text (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数永远是self(lua的userdata, 对象自身)
    if( user ) {
        LVLabel* view = (__bridge LVLabel *)(user->object);// 当前userdata对应的native对象
        if ( [view isKindOfClass:[LVLabel class]] ) {// 检查类型是否匹配(其实可以不用检查一般一定是对的)
            if( lua_gettop(L)>=2 ) {// 检查是否参数大于等于两个(self+文本), 如果只要一个是取值操作, 两个是赋值操作
                if ( lua_isnoneornil(L, 2 ) ) {
                    // 如果参数的值是nil标识清空text操作
                    view.text = nil;
                } else if( lua_type(L, 2)==LUA_TNUMBER ){
                    // 如果参数数值, 转换成字符串再赋值
                    CGFloat text = lua_tonumber(L, 2);// 获取第二个参数, 第二个参数实际上对应脚本中的第一个参数
                    view.text = [NSString stringWithFormat:@"%f",text];
                    return 0;
                } else if( lua_type(L, 2)==LUA_TSTRING ){
                    // 如果是字符串, 直接赋值
                    NSString* text = lv_paramString(L, 2);// 获取第二个参数, 第二个参数实际上对应脚本中的第一个参数
                    view.text = text;
                    return 0;
                } else if( lua_type(L, 2)==LUA_TUSERDATA ){
                    // 如果是复合文本, 则获取复合文本对应的NSAttributedString后把复合文本赋值给Label
                    LVUserDataInfo * user2 = lua_touserdata(L, 2);// 2
                    if( user2 && LVIsType(user2, StyledString) ) {
                        LVLabel* view = (__bridge LVLabel *)(user->object);
                        LVStyledString* attString = (__bridge LVStyledString *)(user2->object);
                        [view setAttributedText:attString.mutableStyledString];
                        return 0;
                    }
                }
            } else {
                // 脚本层无入参(除了self), 则 返回text
                NSString* text = view.text;
                lua_pushstring(L, text.UTF8String);
                return 1;// 返回参数的个数
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.lineCount()方法对应的Native实现
 */
static int lineCount(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数永远是self(lua的userdata, 对象自身)
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取userdata对应的native对象
        if( lua_gettop(L)>=2 ) {// 检查参数大于等于两个(self+lineCount), 如果只要一个是取值操作, 两个是赋值操作
            int number = lua_tonumber(L, 2);// 获取第二个参数, 第二个参数实际上对应脚本中的第一个参数
            if( [view isKindOfClass:[LVLabel class]] ){// 类型检查可以不做
                view.numberOfLines = number;// 设置文本的行数
                return 0;
            }
        } else {
            // 脚本层无入参(除了self), 则 返回numberOfLines
            lua_pushnumber(L, view.numberOfLines );// 返回文本的行数
            return 1;// 返回参数个数
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.adjustFontSize()方法对应的Native实现
 */
static int adjustFontSize(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数永远是self(lua的userdata, 对象自身)
    if( user ){
        BOOL yes = lua_toboolean(L, 2);// 获取第二个参数 bool值
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取userdata对应的native对象
        if( [view isKindOfClass:[LVLabel class]] ){
            view.adjustsFontSizeToFitWidth = yes;// 是否自适应字体
            
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
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数永远是self(lua的userdata, 对象自身)
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取userdata对应的native对象
        if( lua_gettop(L)>=2 ) {// 如果参数大于两个
            if( [view isKindOfClass:[LVLabel class]] ){
                UIColor* color = lv_getColorFromStack(L, 2); // 获取第二个参数的值(颜色)
                view.textColor = color; // 设置颜色
                return 0;
            }
        } else {
            // 脚本层无入参(除了self), 则 返回颜色值
            UIColor* color = view.textColor;
            NSUInteger c = 0;
            CGFloat a = 0;
            if( lv_uicolor2int(color, &c, &a) ){
                lua_pushnumber(L, c ); // 颜色值
                lua_pushnumber(L, a);// 透明度
                return 2;// 返回参数的个数
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.font()方法对应的Native实现
 */
static int font (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);// 获取LuaView的内核LuaViewCore
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( luaView && user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取self对应的native对象
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lua_gettop(L)>=2 ) {// 参数大于两个
                if( lua_gettop(L)>=3 && lua_type(L, 2)==LUA_TSTRING ) {
                    // 参数大于等于三个: 第一个对象自身 第二个是字体名称, 第三个是字体大小
                    NSString* fontName = lv_paramString(L, 2);
                    float fontSize = lua_tonumber(L, 3);
                    UIFont* font = [LVUtil fontWithName:fontName size:fontSize bundle:luaView.bundle];
                    view.font = font;
                } else {
                    // 只有两个参数: 第一个对象自身, 第二个字体大小
                    float fontSize = lua_tonumber(L, 2);
                    view.font = [UIFont systemFontOfSize:fontSize];
                }
                return 0;
            } else {
                // 脚本层无入参(除了self), 则 返回两个参数: 字体名称+字体大小
                UIFont* font = view.font;
                NSString* fontName = font.fontName;
                CGFloat fontSize = font.pointSize;
                lua_pushstring(L, fontName.UTF8String);
                lua_pushnumber(L, fontSize);
                return 2;// 返回参数的个数
            }
        }
    }
    return 0;
}

/*
 * 脚本label实例对象label.fontSize()方法对应的Native实现
 */
static int fontSize (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取self对应的native对象
        if( [view isKindOfClass:[LVLabel class]] ){
            if( lua_gettop(L)>=2 ) {
                // 两个参数: 第一个对象自身, 第二个字体大小
                float fontSize = lua_tonumber(L, 2);
                view.font = [UIFont systemFontOfSize:fontSize];
                return 0;
            } else {
                // 脚本层无入参(除了self), 则 返回字体大小
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
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取self对应的native对象
        if( lua_gettop(L)>=2 ) {
            // 两个参数: 第一个对象自身, 第二个对齐方式
            NSInteger align = lua_tonumber(L, 2);// 获取第二个参数对齐方式
            if( [view isKindOfClass:[LVLabel class]] ){
                view.textAlignment = align;
                return 0;
            }
        } else {
            // 脚本层无入参(除了self), 则 返回 对齐方式的值
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
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( user ){
        LVLabel* view = (__bridge LVLabel *)(user->object);// 获取self对应的native对象
        if( lua_gettop(L)>=2 ) {
            // 两个参数: 第一个对象自身, 第二个参数lineBreakMode
            NSInteger lineBreakMode = lua_tonumber(L, 2);// 2
            if( [view isKindOfClass:[LVLabel class]] ){
                view.lineBreakMode = lineBreakMode;
                return 0;
            }
        } else {
            // 脚本层无入参(除了self), 则 返回 lineBreakMode
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
    // 注册构造方法: "Label" 对应的C函数(lvNewLabel) + 对应的类Class(self/LVLabel)
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

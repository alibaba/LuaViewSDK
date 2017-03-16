/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVStyledString.h"
#import "LVHeads.h"
#import "LView.h"

@interface LVStyledString ()
@end

@implementation LVStyledString

-(id) init:(lua_State *)l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
    }
    return self;
}

- (id) lv_nativeObject{
    return self.mutableStyledString;
}

static void releaseUserDataData(LVUserDataInfo* user){
    if( user && user->object ){
        LVStyledString* data = CFBridgingRelease(user->object);
        user->object = NULL;
        if( data ){
            data.lv_userData = NULL;
            data.lv_luaviewCore = nil;
            data.mutableStyledString = nil;
        }
    }
}

static int __attributedString_gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataData(user);
    return 0;
}

static UIFont* getFont(NSString* fontName, NSNumber* fontSize, NSString* fontWeigth, NSString* fontStyle, LVBundle* bundle){
    fontSize = fontSize ? fontSize : @(14);// bugfix: 不设置字体大小, 只设置粗体斜体无效, 所以给定默认字体大小14
    if( [fontName isKindOfClass:[NSString class]] ){
        return [LVUtil fontWithName:fontName size:fontSize.floatValue bundle:bundle];
    }
    if ( [fontStyle isKindOfClass:[NSString class]] &&
        [fontStyle compare:@"italic" options:NSCaseInsensitiveSearch]==NSOrderedSame ) {
        return [UIFont italicSystemFontOfSize:fontSize.floatValue];
    }
    if( [fontWeigth isKindOfClass:[NSString class]] &&
       [fontWeigth compare:@"bold" options:NSCaseInsensitiveSearch]==NSOrderedSame ){
        //  TODO: 支持数值?
        return [UIFont boldSystemFontOfSize:fontSize.floatValue];
    }
    return [UIFont systemFontOfSize:fontSize.floatValue];
}

// 设置字体
static void resetFont(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range, LVBundle* bundle){
    NSString* fontName = dic[@"fontName"];
    NSNumber* fontSize = dic[@"fontSize"];
    NSString* fontWeight = dic[@"fontWeight"];
    NSString* fontStyle = dic[@"fontStyle"];
    UIFont* font = getFont(fontName, fontSize, fontWeight, fontStyle, bundle);
    if( font ) {
        [attString addAttribute:NSFontAttributeName value:font range:range];
    }
}

// 设置前景色
static void resetForegroundColor(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"fontColor"];
    float  alpha = 1;
    if( value ) {
        alpha = (value.integerValue>>24) & 0xff;
        alpha /= 255.0;
        if( alpha == 0) {
            alpha = 1;
        }
        UIColor* color = lv_UIColorFromRGBA(value.integerValue , alpha );
        [attString addAttribute:NSForegroundColorAttributeName value:color range:range];
    }
}

// 设置背景色
static void resetBackgroundColor(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"backgroundColor"];
    float  alpha = 1;
    if( value ) {
        alpha = (value.integerValue>>24) & 0xff;
        alpha /= 255.0;
        if( alpha == 0) {
            alpha = 1;
        }
        UIColor*  color = lv_UIColorFromRGBA(value.integerValue , alpha );
        [attString addAttribute:NSBackgroundColorAttributeName value:color range:range];
    }
}

static BOOL isNotZeroOrFalse( id value ){
    if( [value isKindOfClass:[NSNumber class]] ) {
        NSNumber* v = value;
        if(  v.intValue == 0 ){
            return NO;
        }
        if( v.boolValue== NO ) {
            return NO;
        }
    }
    return YES;
}

// 设置划线
static void resetStriketrhroughSytle(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"strikethrough"];
    if( value && isNotZeroOrFalse(value) ){
        [attString addAttribute:NSStrikethroughStyleAttributeName value:value range:range];
    } else {
        // IOS 8 系统bugfix( 有中划线和我无中划线都要设置属性, 否则有中划线不会出现 )
        [attString addAttribute:NSStrikethroughStyleAttributeName value:@(NSUnderlineStyleNone) range:range];
    }
}

//下划线
static void resetUnderLineStyle(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"underline"];
    if( value  && isNotZeroOrFalse(value) ){
        [attString addAttribute:NSUnderlineStyleAttributeName value:value range:range];
    }
}

//设置字间距
static void resetCharSpace(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"charSpace"];
    if( value ){
        [attString addAttribute:(id)NSKernAttributeName value:value range:range];
    }
}

//设置行间距
static void resetLineSpace(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"lineSpace"];
    if( value ) {
        NSMutableParagraphStyle * paragraphStyle1 = [[NSMutableParagraphStyle alloc] init];
        paragraphStyle1.lineBreakMode = NSLineBreakByTruncatingTail;
        [paragraphStyle1 setLineSpacing:value.intValue];
        [attString addAttribute:(id)NSParagraphStyleAttributeName value:paragraphStyle1 range:range];
    }
}


static void resetAttributedString(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range ,LVBundle* bundle){
    resetFont(attString, dic, range, bundle);
    resetForegroundColor(attString, dic, range);
    resetBackgroundColor(attString, dic, range);
    resetStriketrhroughSytle(attString, dic, range);
    resetUnderLineStyle(attString, dic, range);
    resetCharSpace(attString, dic, range);
    resetLineSpace(attString, dic, range);
}

static int lvNewAttributedString (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    LVStyledString* attString = [[LVStyledString alloc] init:L];
    if( luaView && lua_gettop(L)>=2 ) {
        if( ( lua_type(L, 1)==LUA_TSTRING || lua_type(L, 1)==LUA_TNUMBER ) && lua_type(L, 2)==LUA_TTABLE ){
            NSString* s = nil;
            size_t n = 0;
            const char* chars = lua_tolstring(L, 1, &n );
            s = [NSString stringWithUTF8String:chars];
            
            // 字符串格式非法，导致crash
            if( s==nil ) {
                s = @"";
            }
            
            attString.mutableStyledString = [[NSMutableAttributedString alloc] initWithString:s];
            
            NSDictionary* dic = lv_luaTableToDictionary(L,2);
            NSRange range = {0};
            range.location = 0;
            range.length = s.length;
            if( [dic isKindOfClass:[NSDictionary class]] ) {
                resetAttributedString(attString.mutableStyledString, dic, range, luaView.bundle);
            }
        }
    }
    
    {
        NEW_USERDATA(userData, StyledString);
        userData->object = CFBridgingRetain(attString);
        attString.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_AttributedString );
        lua_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVStyledString* attString =  (__bridge LVStyledString *)(user->object);
        NSString* s = attString.mutableStyledString.string;
        if( s==nil ){
            s = [[NSString alloc] initWithFormat:@"{ UserDataType=AttributedString, null }" ];
        }
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int append (lua_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
    LVStyledString* string1 = (__bridge LVStyledString *)(user1->object);
    LVStyledString* string2 = (__bridge LVStyledString *)(user2->object);
    if( LVIsType(user1, StyledString) && LVIsType(user2, StyledString)
       && string1.mutableStyledString && string2.mutableStyledString ){
        [string1.mutableStyledString appendAttributedString: string2.mutableStyledString];
        return 1;
    }
    return 0;
}

static int __add (lua_State *L) {
    if( lua_type(L, 2)==LUA_TUSERDATA ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
        if( LVIsType(user1, StyledString) && LVIsType(user2, StyledString) ){
            LVStyledString* user1AttString = (__bridge LVStyledString *)(user1->object);
            LVStyledString* user2AttString = (__bridge LVStyledString *)(user2->object);
            
            LVStyledString* attString = [[LVStyledString alloc] init:L];
            attString.mutableStyledString = [[NSMutableAttributedString alloc] init];
            if( user1AttString && user1AttString.mutableStyledString)
                [attString.mutableStyledString appendAttributedString:user1AttString.mutableStyledString];
            if( user2AttString && user2AttString.mutableStyledString)
                [attString.mutableStyledString appendAttributedString:user2AttString.mutableStyledString];
            {
                NEW_USERDATA(userData, StyledString);
                userData->object = CFBridgingRetain(attString);
                attString.lv_userData = userData;
                
                luaL_getmetatable(L, META_TABLE_AttributedString );
                lua_setmetatable(L, -2);
            }
            return 1;
        }
    } else if( lua_type(L, 2)==LUA_TSTRING || lua_type(L, 2)==LUA_TNUMBER ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lua_touserdata(L, 1);
        NSString* stringArg = nil;
        if( lua_type(L, 2)==LUA_TSTRING ) {
            stringArg = lv_paramString(L, 2);
        } else {
            size_t n = 0;
            const char* chars = lua_tolstring(L, 2, &n );
            stringArg = [NSString stringWithUTF8String:chars];
        }
        if( LVIsType(user1, StyledString)  ){
            LVStyledString* user1AttString = (__bridge LVStyledString *)(user1->object);
            
            LVStyledString* attString = [[LVStyledString alloc] init:L];
            attString.mutableStyledString = [[NSMutableAttributedString alloc] init];
            if( user1AttString && user1AttString.mutableStyledString)
                [attString.mutableStyledString appendAttributedString:user1AttString.mutableStyledString];
            if( stringArg ) {
                [attString.mutableStyledString appendAttributedString:[[NSMutableAttributedString alloc] initWithString:stringArg]];
            }
            {
                NEW_USERDATA(userData, StyledString);
                userData->object = CFBridgingRetain(attString);
                attString.lv_userData = userData;
                
                luaL_getmetatable(L, META_TABLE_AttributedString );
                lua_setmetatable(L, -2);
            }
            return 1;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewAttributedString globalName:globalName defaultName:@"StyledString"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"append", append },
        
        {"__add", __add },
        {"__gc", __attributedString_gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    lv_createClassMetaTable(L, META_TABLE_AttributedString);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 0;
}


@end

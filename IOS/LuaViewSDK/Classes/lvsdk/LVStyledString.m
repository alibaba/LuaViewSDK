//
//  LVAttributedString.m
//  LVSDK
//
//  Created by dongxicheng on 4/17/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVStyledString.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import "LView.h"

@interface LVStyledString ()
@end

@implementation LVStyledString

-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
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
            data.lv_lview = nil;
            data.mutableStyledString = nil;
        }
    }
}

static int __attributedString_gc (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserDataData(user);
    return 0;
}

static UIFont* getFont(NSString* fontName, NSNumber* fontSize, NSString* fontWeigth, NSString* fontStyle, LVBundle* bundle){
    if ( fontSize ) {
        if( [fontName isKindOfClass:[NSString class]] ){
            return [LVUtil fontWithName:fontName size:fontSize.floatValue bundle:bundle];
        }
        if ( [fontStyle isKindOfClass:[NSString class]] &&
            [fontStyle compare:@"italic" options:NSCaseInsensitiveSearch]==NSOrderedSame ) {
            return [UIFont italicSystemFontOfSize:fontSize.floatValue];
        }
        if( [fontWeigth isKindOfClass:[NSString class]] &&
           [fontWeigth compare:@"bold" options:NSCaseInsensitiveSearch]==NSOrderedSame ){
            return [UIFont boldSystemFontOfSize:fontSize.floatValue];
        }
        return [UIFont systemFontOfSize:fontSize.floatValue];
    }
    return nil;
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

static int lvNewAttributedString (lv_State *L) {
    LView* luaView = (__bridge LView *)(L->lView);
    LVStyledString* attString = [[LVStyledString alloc] init:L];
    if( luaView && lv_gettop(L)>=2 ) {
        if( lv_type(L, 1)==LV_TSTRING && lv_type(L, 2)==LV_TTABLE ){
            NSString* s = lv_paramString(L, 1);
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
        
        lvL_getmetatable(L, META_TABLE_AttributedString );
        lv_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVStyledString* attString =  (__bridge LVStyledString *)(user->object);
        NSString* s = attString.mutableStyledString.string;
        if( s==nil ){
            s = [[NSString alloc] initWithFormat:@"{ UserDataType=AttributedString, null }" ];
        }
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int append (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    LVStyledString* string1 = (__bridge LVStyledString *)(user1->object);
    LVStyledString* string2 = (__bridge LVStyledString *)(user2->object);
    if( LVIsType(user1, StyledString) && LVIsType(user2, StyledString)
       && string1.mutableStyledString && string2.mutableStyledString ){
        [string1.mutableStyledString appendAttributedString: string2.mutableStyledString];
        return 1;
    }
    return 0;
}

static int __add (lv_State *L) {
    if( lv_type(L, 2)==LV_TUSERDATA ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
        LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
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
                
                lvL_getmetatable(L, META_TABLE_AttributedString );
                lv_setmetatable(L, -2);
            }
            return 1;
        }
    } else if( lv_type(L, 2)==LV_TSTRING ) {
        LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
        NSString* stringArg = lv_paramString(L, 2);
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
                
                lvL_getmetatable(L, META_TABLE_AttributedString );
                lv_setmetatable(L, -2);
            }
            return 1;
        }
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewAttributedString);
        lv_setglobal(L, "StyledString");
    }
    const struct lvL_reg memberFunctions [] = {
        {"append", append },
        
        {"__add", __add },
        {"__gc", __attributedString_gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    lv_createClassMetaTable(L, META_TABLE_AttributedString);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 0;
}


@end

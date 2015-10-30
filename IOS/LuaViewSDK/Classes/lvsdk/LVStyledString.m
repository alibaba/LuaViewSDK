//
//  LVAttributedString.m
//  LVSDK
//
//  Created by dongxicheng on 4/17/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVStyledString.h"

@interface LVStyledString ()
@end

@implementation LVStyledString

-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lview = (__bridge LView *)(l->lView);
    }
    return self;
}

static void releaseUserDataData(LVUserDataStyledString* user){
    if( user && user->styledString ){
        LVStyledString* data = CFBridgingRelease(user->styledString);
        user->styledString = NULL;
        if( data ){
            data.userData = NULL;
            data.lview = nil;
            data.mutableStyledString = nil;
        }
    }
}

static int __attributedString_gc (lv_State *L) {
    LVUserDataStyledString * user = (LVUserDataStyledString *)lv_touserdata(L, 1);
    releaseUserDataData(user);
    return 0;
}

static UIFont* getFont(NSString* fontName, NSNumber* fontSize, NSString* fontWeigth, NSString* fontStyle){
    if( fontName ){
        return [UIFont fontWithName:fontName size:fontSize.floatValue];
    } else {
        if( fontWeigth && [fontWeigth isEqualToString:@"bold"] ){
            return [UIFont boldSystemFontOfSize:fontSize.floatValue];
        } else{
            return [UIFont systemFontOfSize:fontSize.floatValue];
        }
    }
}

// 设置字体
static void resetFont(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSString* fontName = dic[@"fontName"];
    NSNumber* fontSize = dic[@"fontSize"];
    NSString* fontWeight = dic[@"fontWeight"];
    NSString* fontStyle = dic[@"fontStyle"];
    UIFont* font = getFont(fontName, fontSize, fontWeight, fontStyle);
    [attString addAttribute:NSFontAttributeName value:font range:range];
}

// 设置前景色
static void resetForegroundColor(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"foregroundColor"];
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
    NSMutableParagraphStyle * paragraphStyle1 = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle1.lineBreakMode = NSLineBreakByTruncatingTail;
    [paragraphStyle1 setLineSpacing:value.intValue];
    [attString addAttribute:(id)NSParagraphStyleAttributeName value:paragraphStyle1 range:range];
}


static void resetAttributedString(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    resetFont(attString, dic, range);
    resetForegroundColor(attString, dic, range);
    resetBackgroundColor(attString, dic, range);
    resetStriketrhroughSytle(attString, dic, range);
    resetUnderLineStyle(attString, dic, range);
    resetCharSpace(attString, dic, range);
    resetLineSpace(attString, dic, range);
}

static int lvNewAttributedString (lv_State *L) {
    LVStyledString* attString = [[LVStyledString alloc] init:L];
    if( lv_gettop(L)>=2 ) {
        if( lv_type(L, 1)==LV_TSTRING && lv_type(L, 2)==LV_TTABLE ){
            NSString* s = lv_paramString(L, 1);
            attString.mutableStyledString = [[NSMutableAttributedString alloc] initWithString:s];
            
            NSDictionary* dic = lv_luaTableToDictionary(L,2);
            NSRange range = {0};
            range.location = 0;
            range.length = s.length;
            resetAttributedString(attString.mutableStyledString, dic, range);
        }
    }
    
    {
        NEW_USERDATA(userData, LVUserDataStyledString);
        userData->styledString = CFBridgingRetain(attString);
        attString.userData = userData;
        
        lvL_getmetatable(L, META_TABLE_AttributedString );
        lv_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lv_State *L) {
    LVUserDataStyledString * user = (LVUserDataStyledString *)lv_touserdata(L, 1);
    if( user ){
        LVStyledString* attString =  (__bridge LVStyledString *)(user->styledString);
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
    LVUserDataStyledString * user1 = (LVUserDataStyledString *)lv_touserdata(L, 1);
    LVUserDataStyledString * user2 = (LVUserDataStyledString *)lv_touserdata(L, 2);
    LVStyledString* string1 = (__bridge LVStyledString *)(user1->styledString);
    LVStyledString* string2 = (__bridge LVStyledString *)(user2->styledString);
    if( LVIsType(user1,LVUserDataStyledString) && LVIsType(user2,LVUserDataStyledString)
       && string1.mutableStyledString && string2.mutableStyledString ){
        [string1.mutableStyledString appendAttributedString: string2.mutableStyledString];
        return 1;
    }
    return 0;
}

static int __add (lv_State *L) {
    LVUserDataStyledString * user1 = (LVUserDataStyledString *)lv_touserdata(L, 1);
    LVUserDataStyledString * user2 = (LVUserDataStyledString *)lv_touserdata(L, 2);
    if( LVIsType(user1,LVUserDataStyledString) && LVIsType(user2,LVUserDataStyledString) ){
        LVStyledString* user1AttString = (__bridge LVStyledString *)(user1->styledString);
        LVStyledString* user2AttString = (__bridge LVStyledString *)(user2->styledString);
        
        LVStyledString* attString = [[LVStyledString alloc] init:L];
        attString.mutableStyledString = [[NSMutableAttributedString alloc] init];
        if( user1AttString && user1AttString.mutableStyledString)
            [attString.mutableStyledString appendAttributedString:user1AttString.mutableStyledString];
        if( user2AttString && user2AttString.mutableStyledString)
            [attString.mutableStyledString appendAttributedString:user2AttString.mutableStyledString];
        {
            NEW_USERDATA(userData, LVUserDataStyledString);
            userData->styledString = CFBridgingRetain(attString);
            attString.userData = userData;
            
            lvL_getmetatable(L, META_TABLE_AttributedString );
            lv_setmetatable(L, -2);
        }
        return 1;
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

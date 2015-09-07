//
//  LVAttributedString.m
//  LVSDK
//
//  Created by dongxicheng on 4/17/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVAttributedString.h"

@interface LVAttributedString ()
@end

@implementation LVAttributedString

-(id) init:(lv_State *)l{
    self = [super init];
    if( self ){
        self.lview = (__bridge LView *)(l->lView);
    }
    return self;
}

static void releaseUserDataData(LVUserDataAttributedString* user){
    if( user && user->attributedString ){
        LVAttributedString* data = CFBridgingRelease(user->attributedString);
        user->attributedString = NULL;
        if( data ){
            data.userData = NULL;
            data.lview = nil;
            data.mutableAttributedString = nil;
        }
    }
}

static int __attributedString_gc (lv_State *L) {
    LVUserDataAttributedString * user = (LVUserDataAttributedString *)lv_touserdata(L, 1);
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
    NSNumber* alpha = dic[@"foregroundColorAlpha"];
    if( value ) {
        if( alpha ==nil ){
            alpha = @(1);
        }
        UIColor* color = lv_UIColorFromRGBA(value.integerValue , alpha.floatValue);
        [attString addAttribute:NSForegroundColorAttributeName value:color range:range];
    }
}

// 设置背景色
static void resetBackgroundColor(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"backgroundColor"];
    NSNumber* alpha = dic[@"backgroundColorAlpha"];
    if( value ){
        if( alpha ==nil ){
            alpha = @(1);
        }
        UIColor*  color = lv_UIColorFromRGBA(value.integerValue , alpha.floatValue);
        [attString addAttribute:NSBackgroundColorAttributeName value:color range:range];
    }
}

// 设置划线
static void resetStriketrhroughSytle(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"strikethroughStyle"];
    if( value ){
        [attString addAttribute:NSStrikethroughStyleAttributeName value:value range:range];
    }
}

//下划线
static void resetUnderLineStyle(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"underlineStyle"];
    if( value ){
        [attString addAttribute:NSUnderlineStyleAttributeName value:value range:range];
    }
}

//设置字间距
static void resetCharSpace(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"charpace"];
    if( value ){
        [attString addAttribute:(id)NSKernAttributeName value:value range:range];
    }
}

//设置行间距
static void resetLineSpace(NSMutableAttributedString* attString, NSDictionary* dic, NSRange range){
    NSNumber* value = dic[@"linespace"];
    if( value && [LVUtil ios7] ){
        NSMutableParagraphStyle * paragraphStyle1 = [[NSMutableParagraphStyle alloc] init];
        paragraphStyle1.lineBreakMode = NSLineBreakByTruncatingTail;
        [paragraphStyle1 setLineSpacing:value.intValue];
        [attString addAttribute:(id)NSParagraphStyleAttributeName value:paragraphStyle1 range:range];
    }
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
    LVAttributedString* attString = [[LVAttributedString alloc] init:L];
    if( lv_gettop(L)>=2 ) {
        if( lv_type(L, 1)==LV_TSTRING && lv_type(L, 2)==LV_TTABLE ){
            NSString* s = lv_paramString(L, 1);
            attString.mutableAttributedString = [[NSMutableAttributedString alloc] initWithString:s];
            
            NSDictionary* dic = lv_luaTableToDictionary(L,2);
            NSRange range = {0};
            range.location = 0;
            range.length = s.length;
            resetAttributedString(attString.mutableAttributedString, dic, range);
        }
    }
    
    {
        NEW_USERDATA(userData, LVUserDataAttributedString);
        userData->attributedString = CFBridgingRetain(attString);
        attString.userData = userData;
        
        lvL_getmetatable(L, META_TABLE_AttributedString );
        lv_setmetatable(L, -2);
    }
    return 1;
}

static int __tostring (lv_State *L) {
    LVUserDataAttributedString * user = (LVUserDataAttributedString *)lv_touserdata(L, 1);
    if( user ){
        LVAttributedString* attString =  (__bridge LVAttributedString *)(user->attributedString);
        NSString* s = attString.mutableAttributedString.string;
        if( s==nil ){
            s = [[NSString alloc] initWithFormat:@"{ UserDataType=AttributedString, null }" ];
        }
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int append (lv_State *L) {
    LVUserDataAttributedString * user1 = (LVUserDataAttributedString *)lv_touserdata(L, 1);
    LVUserDataAttributedString * user2 = (LVUserDataAttributedString *)lv_touserdata(L, 2);
    LVAttributedString* string1 = (__bridge LVAttributedString *)(user1->attributedString);
    LVAttributedString* string2 = (__bridge LVAttributedString *)(user2->attributedString);
    if( LVIsType(user1,LVUserDataAttributedString) && LVIsType(user2,LVUserDataAttributedString)
       && string1.mutableAttributedString && string2.mutableAttributedString ){
        [string1.mutableAttributedString appendAttributedString: string2.mutableAttributedString];
        return 1;
    }
    return 0;
}

static int __add (lv_State *L) {
    LVUserDataAttributedString * user1 = (LVUserDataAttributedString *)lv_touserdata(L, 1);
    LVUserDataAttributedString * user2 = (LVUserDataAttributedString *)lv_touserdata(L, 2);
    if( LVIsType(user1,LVUserDataAttributedString) && LVIsType(user2,LVUserDataAttributedString) ){
        LVAttributedString* user1AttString = (__bridge LVAttributedString *)(user1->attributedString);
        LVAttributedString* user2AttString = (__bridge LVAttributedString *)(user2->attributedString);
        
        LVAttributedString* attString = [[LVAttributedString alloc] init:L];
        attString.mutableAttributedString = [[NSMutableAttributedString alloc] init];
        if( user1AttString && user1AttString.mutableAttributedString)
            [attString.mutableAttributedString appendAttributedString:user1AttString.mutableAttributedString];
        if( user2AttString && user2AttString.mutableAttributedString)
            [attString.mutableAttributedString appendAttributedString:user2AttString.mutableAttributedString];
        {
            NEW_USERDATA(userData, LVUserDataAttributedString);
            userData->attributedString = CFBridgingRetain(attString);
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
        lv_setglobal(L, "AttributedString");
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

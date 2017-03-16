/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVGrammarChanger.h"

@implementation LVGrammarChanger

@end



// ‘.’ 和‘:’ 相互转化代码
#define LV_TYPE_WORD_FIRST    (1)
#define LV_TYPE_WORD_SECOND   (2)
#define LV_TYPE_NUMBER         (4)
#define LV_TYPE_CHAR_SPACE    (8)
#define LV_TYPE_CHAR_NOTES    (16)
#define LV_TYPE_CHAR_POINT    (32)

static int g_charTypes[256] = {0};
static void charTypesInited(){
    g_charTypes['_'] = LV_TYPE_WORD_FIRST|LV_TYPE_WORD_SECOND;
    for( char c = 'a'; c<='z'; c++ ) {
        g_charTypes[c] = LV_TYPE_WORD_FIRST|LV_TYPE_WORD_SECOND;
    }
    for( char c = 'A'; c<='Z'; c++ ) {
        g_charTypes[c] = LV_TYPE_WORD_FIRST|LV_TYPE_WORD_SECOND;
    }
    for( char c = '0'; c<='9'; c++ ) {
        g_charTypes[c] = LV_TYPE_NUMBER | LV_TYPE_WORD_SECOND;
    }
    g_charTypes[' '] = LV_TYPE_CHAR_SPACE;
    g_charTypes['\n'] = LV_TYPE_CHAR_SPACE;
    g_charTypes['.'] = LV_TYPE_CHAR_POINT;
    g_charTypes[':'] = LV_TYPE_CHAR_POINT;
    g_charTypes['-'] = LV_TYPE_CHAR_NOTES;
}

inline static NSInteger skipNotes(const unsigned char* cs, NSInteger i, NSInteger length){
    for( int m=0 ; m<2 && i<length; m++ ) {
        char c = cs[i];
        if( c=='-' ) {
            i++;
        } else {
            return i;
        }
    }
    for( ;i<length;) {
        char c = cs[i];
        if( c=='\n' ) {
            i++;
            return i;
        } else {
            return i;
        }
    }
    return i;
}

inline static NSInteger skipName(const unsigned char* cs, NSInteger i, NSInteger length){
    int* types = g_charTypes;
    if( i<length ) {
        char c = cs[i];
        int type = types[c];
        if( type&LV_TYPE_WORD_FIRST ) {
            i++;
        } else {
            return i;
        }
    }
    for( ;i<length;) {
        char c = cs[i];
        int type = types[c];
        if( type&LV_TYPE_WORD_SECOND ) {
            i++;
        } else {
            return i;
        }
    }
    return i;
}

inline static NSInteger skipNumber(const unsigned char* cs, NSInteger i, NSInteger length){
    int* types = g_charTypes;
    for( ;i<length;) {
        char c = cs[i];
        int type = types[c];
        if( type&LV_TYPE_WORD_SECOND ) {
            i++;
        } else {
            return i;
        }
    }
    return i;
}

inline static NSInteger skipSpace(const unsigned char* cs, NSInteger i, NSInteger length){
    int* types = g_charTypes;
    for( ;i<length;) {
        unsigned char c = cs[i];
        int type = types[c];
        if( type&LV_TYPE_CHAR_SPACE) {
            i++;
        } else {
            return i;
        }
    }
    return i;
}

inline static NSInteger skipOther(const unsigned char* cs, NSInteger i, NSInteger length){
    int* types = g_charTypes;
    for( ;i<length;) {
        unsigned char c = cs[i];
        int type = types[c];
        if( type==0) {
            i++;
        } else {
            return i;
        }
    }
    return i;
}

inline static BOOL checkNextChar(const unsigned char* cs, NSInteger i, NSInteger length, char c){
    if( i<length && cs[i]==c ) {
        return YES;
    }
    return NO;
}
/*
 * 转换成标准lua语法
 */
NSData* lv_toStandLuaGrammar(NSData* data){
    {
        static BOOL inited = NO;
        if( !inited ) {
            inited = YES;
            charTypesInited();
        }
    }
    if( data && data.length>0 ) {
        NSInteger length = data.length;
        unsigned char* cs = malloc(length+64);
        memset(cs, 0, length+64);
        [data getBytes:cs length:length];
        for ( NSInteger i=0; i<length;) {
            unsigned char c = cs[i];
            int type = g_charTypes[c];
            switch (type) {
                case LV_TYPE_CHAR_NOTES:
                    i = skipNotes(cs, i, length);
                    break;
                case LV_TYPE_CHAR_SPACE:
                    i = skipSpace(cs, i, length);
                    break;
                case LV_TYPE_NUMBER:
                case LV_TYPE_NUMBER|LV_TYPE_WORD_SECOND: {
                    i = skipNumber(cs, i, length);
                    break;
                    break;
                }
                case LV_TYPE_WORD_FIRST:
                case LV_TYPE_WORD_SECOND:
                case LV_TYPE_WORD_FIRST|LV_TYPE_WORD_SECOND:
                    i = skipName(cs, i, length);
                    break;
                case LV_TYPE_CHAR_POINT:{
                    if( checkNextChar(cs, i, length, '.') && checkNextChar(cs, i+1, length, '.') ) {
                        i += 2;
                    }else if( checkNextChar(cs, i, length, '.') || checkNextChar(cs, i, length, ':') ) {
                        NSInteger i0 = i;
                        i++;
                        i = skipSpace(cs, i, length);
                        NSInteger i2 = skipName(cs, i, length);
                        if( i2>i ) {
                            i = i2;
                            i = skipSpace(cs, i, length);
                            if( checkNextChar(cs, i, length, '(') ||  checkNextChar(cs, i, length, '{') ) {
                                unsigned char tempChar = cs[i0];
                                if( (char)tempChar=='.' ) {
                                    cs[i0] = ':';
                                } else {
                                    cs[i0] = '.';
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                    
                default:
                    i = skipOther(cs, i, length);
                    break;
            }
            
        }
        NSData* newData = [[NSData alloc] initWithBytes:cs length:length];
        free(cs);
        return newData;
    }
    return nil;
}

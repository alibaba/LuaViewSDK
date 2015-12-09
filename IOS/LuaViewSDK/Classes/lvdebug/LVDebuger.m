//
//  LVDebugCmd.m
//  LVSDK
//
//  Created by 城西 on 15/3/27.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVDebuger.h"
#import "LVHeads.h"
#import "LVDebugConnection.h"
#import "LView.h"

#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVDebuger


static int DebugReadCmd (lv_State *L) {
    LView* luaView = (__bridge LView *)(L->lView);
    
    NSString* cmd = [luaView.debugConnection getCmd];
    if( cmd ){
        lv_pushstring(L, cmd.UTF8String);
    } else {
        lv_pushnil(L);
    }
    return 1;
}

static int DebugSleep (lv_State *L) {
    float time = lv_tonumber(L, 1);
    if( time>0 ) {
        [NSThread sleepForTimeInterval:time];
    }
    return 0;
}

static int DebugPrintToServer (lv_State *L) {
    LView* luaView = (__bridge LView *)(L->lView);
    BOOL open = lvL_checkbool(L, 1);
    luaView.debugConnection.printToServer = !!open;
    return 0;
}

static int runningLine (lv_State *L) {
    NSString* fileName = lv_paramString(L, 1);
    if( fileName == nil ){
        fileName = @"unkown";
    }
    int lineNumber = lv_tonumber(L, 2);
    
    NSString* lineInfo = [NSString stringWithFormat:@"%d",lineNumber];
    LView* luaView = (__bridge LView *)(L->lView);
    [luaView.debugConnection  sendCmd:@"running" fileName:fileName info:lineInfo];
    return 0;
}

static int get_file_line( lv_State *L )
{
    lv_pushstring(L, "one line code");
    return 1;
}

static const lvL_Reg dblib[] = {
    {"readCmd", DebugReadCmd},
    {"sleep", DebugSleep},
    {"printToServer", DebugPrintToServer},
    {"runningLine", runningLine},
    {"get_file_line", get_file_line},
    {NULL, NULL}
};

+(int) classDefine:(lv_State *)L {
    lvL_register(L, LV_DBLIBNAME, dblib);
    return 0;
}

// 把日志传送到服务器
void lv_printToServer(lv_State* L, const char* cs, int withTabChar){
    LView* lview = (__bridge LView *)(L->lView);
    if( lview.debugConnection.printToServer ){
        NSMutableData* data = [[NSMutableData alloc] init];
        if( withTabChar ){
            [data appendBytes:"      " length:4];
        }
        [data appendBytes:cs length:strlen(cs)];
        
        [lview.debugConnection  sendCmd:@"log" info:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
}

// 可变参数实例
//void lv_print(NSString*format, ...) {
//    va_list argumentList;
//    va_start(argumentList, format);
//    NSMutableString * message = [[NSMutableString alloc] initWithFormat:format
//                                                              arguments:argumentList];
//    [message appendString:@"\n"];
//    lv_printToServer(message.UTF8String,0);
//    va_end(argumentList);
//}



@end



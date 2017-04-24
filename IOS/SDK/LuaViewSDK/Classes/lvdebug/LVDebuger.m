/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVDebuger.h"
#import "LVHeads.h"
#import "LVDebugConnection.h"
#import "LView.h"
#import "LVHeads.h"

@implementation LVDebuger

#ifdef DEBUG
//---------------------------------------------------------

static int DebugReadCmd (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    
    NSString* cmd = [luaView.debugConnection getCmd];
    if( cmd ){
        lua_pushstring(L, cmd.UTF8String);
    } else {
        lua_pushnil(L);
    }
    return 1;
}

static int DebugWriteCmd (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    NSString* cmd = lv_paramString(L, 1);
    NSString* info = lv_paramString(L, 2);
    NSDictionary* args = lv_luaTableToDictionary(L, 3);
    
    [luaView.debugConnection sendCmd:cmd info:info args:args];
    return 0;
}

static int DebugSleep (lua_State *L) {
    float time = lua_tonumber(L, 1);
    if( time>0 ) {
        [NSThread sleepForTimeInterval:time];
    }
    return 0;
}

static int DebugPrintToServer (lua_State *L) {
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    BOOL open = lua_toboolean(L, 1);
    luaView.debugConnection.printToServer = !!open;
    return 0;
}

static int runningLine (lua_State *L) {
    NSString* fileName = lv_paramString(L, 1);
    if( fileName == nil ){
        fileName = @"unkown";
    }
    int lineNumber = lua_tonumber(L, 2);
    
    NSString* lineInfo = [NSString stringWithFormat:@"%d",lineNumber];
    LuaViewCore* luaView = LV_LUASTATE_VIEW(L);
    [luaView.debugConnection  sendCmd:@"running" fileName:fileName info:lineInfo args:@{@"Line-Number":lineInfo}];
    return 0;
}

static int get_file_line( lua_State *L )
{
    lua_pushstring(L, "one line code");
    return 1;
}

static int db_traceback_count (lua_State *L) {
    lua_Debug ar;
    int index = 1;
    while (lua_getstack(L, index, &ar))
        index++;
    lua_pushnumber( L, index - 1 );
    return 1;
}

static const luaL_Reg dblib[] = {
    {"readCmd", DebugReadCmd},
    {"writeCmd", DebugWriteCmd},
    {"sleep", DebugSleep},
    {"printToServer", DebugPrintToServer},
    {"runningLine", runningLine},
    {"get_file_line", get_file_line},
    {"traceback_count", db_traceback_count},
    {NULL, NULL}
};


// 把日志传送到服务器
void lv_printToServer(lua_State* L, const char* cs, int withTabChar){
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
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



//---------------------------------------------------------
#endif



+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
#ifdef DEBUG
    luaL_register(L, LUA_DBLIBNAME, dblib);
#endif
    
    return 0;
}


@end



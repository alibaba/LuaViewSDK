//
//  LVDebugCmd.m
//  LVSDK
//
//  Created by 城西 on 15/3/27.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import "LVDebugCmd.h"
#import "LVHeads.h"
#import "LVDebuger.h"


@implementation LVDebugCmd

+ (NSString *)sendAndReadCmdByUrl:(NSString*)url  content:(NSData*) content
{
    return [LVDebugCmd sendAndReadCmdByUrl:url content:content dictionary:nil];
}

+ (NSString *)sendAndReadCmdByUrl:(NSString*)url  content:(NSData*) content dictionary:(NSDictionary*) dictionary
{
    if( url ==nil ){
        url = @"http://127.0.0.1:9876";
    }
    // 初始化请求, 这里是变长的, 方便扩展
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    
    // 设置
    [request setTimeoutInterval:120];//超时时间
    [request setURL:[NSURL URLWithString:url]];
    [request setHTTPMethod:@"POST"];
    [request setValue:[NSString stringWithFormat:@"%ld",(long)content.length] forHTTPHeaderField:@"Content-Length"];
    if( dictionary ){
        NSArray* keys = dictionary.allKeys;
        for( id key in keys ){
            id value = dictionary[key];
            if( key && value ){
                [request setValue:value forHTTPHeaderField:key];
            }
        }
    }
    [request setHTTPBody:content];
    
    // 发送同步请求, data就是返回的数据
    NSError *error = nil;
    NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:&error];
    if (data == nil) {
        //LVError(@"send request failed: %@", error);
        return nil;
    }
    
    NSString *response = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    return response;
}

static int DebugReadCmd (lv_State *L) {
    NSString* cmd = [LVDebuger getCmd];
    if( cmd ){
        lv_pushstring(L, cmd.UTF8String);
    } else {
        lv_pushnil(L);
    }
    return 1;
}

static int DebugPrintToServer (lv_State *L) {
    BOOL open = lvL_checkbool(L, 1);
    g_printToServer = !!open;
    return 0;
}

static int runningLine (lv_State *L) {
    NSString* fileName = lv_paramString(L, 1);
    if( fileName == nil ){
        fileName = @"unkown";
    }
    int lineNumber = lv_tonumber(L, 2);
    
    NSString* lineInfo = [NSString stringWithFormat:@"%d",lineNumber];
    
//    NSMutableData* data = [[NSMutableData alloc] init];
//    const char* cs = [NSString stringWithFormat:@"%d",lineNumber].UTF8String;
//    [data appendBytes:cs length:strlen(cs)];
//    [LVDebugCmd sendAndReadCmdByUrl:@"http://127.0.0.1:9875" content:data dictionary:@{@"Cmd-Name":@"running",@"File-Name":fileName}];
    [LVDebuger  sendCmd:@"running" fileName:fileName info:lineInfo];
    return 0;
}

static int get_file_line( lv_State *L )
{
    lv_pushstring(L, "one line code");
    return 1;
}

static const lvL_Reg dblib[] = {
    {"readCmd", DebugReadCmd},
    {"printToServer", DebugPrintToServer},
    {"runningLine", runningLine},
    {"get_file_line", get_file_line},
    {NULL, NULL}
};

+(int) classDefine:(lv_State *)L {
    lvL_register(L, LV_DBLIBNAME, dblib);
    return 0;
}


// 是否把日志传导服务器
int g_printToServer = NO;

// 把日志传送到服务器
void lv_printToServer(const char* cs, int withTabChar){
    if( g_printToServer ){
        NSMutableData* data = [[NSMutableData alloc] init];
        if( withTabChar ){
            [data appendBytes:"      " length:4];
        }
        [data appendBytes:cs length:strlen(cs)];
        
        [LVDebuger  sendCmd:@"log" info:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
//        [LVDebugCmd sendAndReadCmdByUrl:@"http://127.0.0.1:9875" content:data dictionary:@{@"Cmd-Name":@"log"}];
    }
}

void lv_print(NSString*format, ...) {
    va_list argumentList;
    va_start(argumentList, format);
    NSMutableString * message = [[NSMutableString alloc] initWithFormat:format
                                                              arguments:argumentList];
    [message appendString:@"\n"];
    lv_printToServer(message.UTF8String,0);
    va_end(argumentList);
}



@end



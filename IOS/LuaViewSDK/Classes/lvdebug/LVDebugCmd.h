//
//  LVDebugCmd.h
//  LVSDK
//
//  Created by 城西 on 15/3/27.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

extern int g_printToServer;
void lv_printToServer(const char* cs, int withTabChar);
void lv_print(NSString* format,...);

//#define LV Log(format,...) lv_print(format, ##__VA_ARGS__);  LVLog(format, ##__VA_ARGS__);

@interface LVDebugCmd : NSObject

+(int) classDefine:(lv_State *)L;

+ (NSString *)sendAndReadCmdByUrl:(NSString*)url  content:(NSData*) content;

+ (NSString *)sendAndReadCmdByUrl:(NSString*)url  content:(NSData*) content dictionary:(NSDictionary*) dictionary;

@end

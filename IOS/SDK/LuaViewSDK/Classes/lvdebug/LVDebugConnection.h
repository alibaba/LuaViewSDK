//
//  LVDebuger.h
//  CFSocketDemo
//
//  Created by dongxicheng on 9/16/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVDebugConnection : NSObject

@property (nonatomic,assign) BOOL printToServer;
@property (nonatomic,weak) LuaViewCore* lview;
@property (atomic,strong) NSMutableArray* receivedArray;

- (BOOL) isOk;

- (NSString*) getCmd;

- (NSInteger) waitUntilConnectionEnd;

- (void) sendCmd:(NSString*) cmdName info:(NSString*) info;
- (void) sendCmd:(NSString*) cmdName info:(NSString*) info args:(NSDictionary*) args;
- (void) sendCmd:(NSString*) cmdName fileName:(NSString*)fileName info:(NSString*) info;
- (void) sendCmd:(NSString*) cmdName fileName:(NSString*)fileName info:(NSString*) info args:(NSDictionary*) args;

-(void) closeAll;

// 设置调试器的IP和端口, 用于远程调试
+(void) setDebugerIP:(NSString*) ip port:(int) port;

#ifdef DEBUG
+(void) openUrlServer:( void(^)(NSDictionary* args) ) callback;
#endif
@end

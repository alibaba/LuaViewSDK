/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

#ifdef DEBUG
//---------------------------------------------------------
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

+(void) openUrlServer:( void(^)(NSDictionary* args) ) callback;

@end

//---------------------------------------------------------
#endif

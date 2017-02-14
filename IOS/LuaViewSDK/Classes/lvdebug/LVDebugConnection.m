//
//  LVDebuger.m
//  CFSocketDemo
//
//  Created by 罗何 on 9/16/15.
//  Copyright © 2015年 luohe. All rights reserved.
//

#import "LVDebugConnection.h"
#import <CFNetwork/CFNetwork.h>
#import <TargetConditionals.h>
#import <sys/socket.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import <sys/ioctl.h>
#import <net/if.h>
#import <netdb.h>
#import "LVUtil.h"
#import "LView.h"


#define SOCKET_ERROR        (-1)
#define SOCKET_CONNECTINTG  (0)
#define SOCKET_SUCCESS      (1)

// 调试器的默认IP和端口
static NSString* SERVER_IP = @"127.0.0.1";
static int SERVER_PORT = 9876;

@interface LVDebugConnection ()<NSStreamDelegate>
@property(nonatomic,strong) NSThread* myThread;
@property(nonatomic,assign) BOOL canWrite;
@property(nonatomic,assign) NSInteger state;
@property(atomic,strong) NSMutableArray* sendArray;
@end

@implementation LVDebugConnection{
    NSOutputStream *_outputStream;
    NSInputStream *_inputStream;
    
    BOOL _outputStreamCompleted;
    BOOL _inputStreamCompleted;
}

-(id) init{
    self  = [super init];
    if( self ) {
        static int index = 0;
        self.myThread = [[NSThread alloc] initWithTarget:self selector:@selector(run:) object:nil];
        self.myThread.name = [NSString stringWithFormat:@"LV.Debuger.%d",index];
        self.sendArray = [[NSMutableArray alloc] init];
        self.receivedArray = [[NSMutableArray alloc] init];
        [self startThread];
    }
    return self;
}

- (void) dealloc{
    [self closeAll];
}

-(BOOL) isOk{
    return self.state>0;
}
- (NSInteger) waitUntilConnectionEnd{
    for(;self.state==SOCKET_CONNECTINTG;) {
        [NSThread sleepForTimeInterval:0.01];
    }
    return self.state;
}

-(void) startThread{
    [self.myThread start]; //启动线程
}

+(void) setDebugerIP:(NSString*) ip port:(int) port{
    SERVER_IP = ip;
    SERVER_PORT = port;
}

-(void) run:(id) obj{
    @autoreleasepool {
        [self Connect:SERVER_IP port:SERVER_PORT];
        
        NSRunLoop *runLoop = [NSRunLoop currentRunLoop];
        [runLoop addPort:[NSMachPort port] forMode:NSDefaultRunLoopMode];
        [runLoop run];
    }
}

- (NSString*) getCmd{
    NSString* cmd = self.receivedArray.lastObject;
    if( cmd ) {
        [self.receivedArray removeLastObject];
    }
    return cmd;
}

- (void) sendCmd:(NSString*) cmdName info:(NSString*) info{
    [self sendCmd:cmdName fileName:nil info:info];
}

- (void) sendCmd:(NSString*) cmdName fileName:(NSString*)fileName info:(NSString*) info{
    NSMutableString* buffer = [[NSMutableString alloc] init];
    if ( cmdName ) {
        [buffer appendFormat:@"Cmd-Name:%@\n",cmdName];
    }
    if ( fileName ){
        [buffer appendFormat:@"File-Name:%@\n",fileName];
    }
    [buffer appendString:@"\n"];
    if ( info ){
        [buffer appendFormat:@"%@",info];
    }
    [self sendString:buffer];
}

-(void)Connect:(NSString*) ip port:(NSUInteger)port{
#ifdef DEBUG
    CFReadStreamRef readStream = NULL;
    CFWriteStreamRef writeStream = NULL;
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef)ip, (UInt32)port, &readStream, &writeStream);
    // 记录已经分配的输入流和输出流
    _inputStream = (__bridge NSInputStream *)readStream;
    _outputStream = (__bridge NSOutputStream *)writeStream;
    
    //设置属性SSL
    //    [_inputStream setProperty:NSStreamSocketSecurityLevelSSLv3 forKey:NSStreamSocketSecurityLevelKey];
    //    [_outputStream setProperty:NSStreamSocketSecurityLevelSSLv3 forKey:NSStreamSocketSecurityLevelKey];
    //    CFWriteStreamSetProperty(writeStream, kCFStreamPropertySSLSettings, (__bridge CFTypeRef)([NSMutableDictionary dictionaryWithObjectsAndKeys:(id)kCFBooleanFalse,kCFStreamSSLValidatesCertificateChain,kCFBooleanFalse,kCFStreamSSLIsServer,nil]));
    
    // 设置代理，监听输入流和输出流中的变化
    _inputStream.delegate = self;
    _outputStream.delegate = self;
    
    // Scoket是建立的长连接，需要将输入输出流添加到主运行循环
    [_inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
    [_outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSRunLoopCommonModes];
    
    // 打开输入流和输出流，准备开始文件读写操作
    [_inputStream open];
    [_outputStream open];
#endif
}

- (void)stream:(NSStream *)aStream handleEvent:(NSStreamEvent)eventCode{
    switch (eventCode) {
        case NSStreamEventOpenCompleted:
            if(aStream == _inputStream) _inputStreamCompleted = YES;
            if(aStream == _outputStream) _outputStreamCompleted = YES;
            if(_inputStreamCompleted && _outputStreamCompleted) self.state = SOCKET_SUCCESS;
            break;
        case NSStreamEventHasBytesAvailable:{
            
            uint8_t buf[16 * 1024];
            uint8_t *buffer = NULL;
            NSUInteger len = 0;
            if (![_inputStream getBuffer:&buffer length:&len]) {
                NSInteger amount = [_inputStream read:buf maxLength:sizeof(buf)];
                buffer = buf;
                len = amount;
            }
            if (0 < len) {
                //head
//                NSUInteger d0 = buffer[0];
//                NSUInteger d1 = buffer[1];
//                NSUInteger d2 = buffer[2];
//                NSUInteger d3 = buffer[3];
//                len = (d0<<24) + (d1<<16) + (d2<<8) + d3;
//                buffer = buffer + 4;
                
                NSString *cmd = [[NSString alloc] initWithBytes:buffer length:len encoding:NSUTF8StringEncoding];
                if ( cmd && cmd.length>0) {
                    LVLog(@"received CMD: %@", cmd);
                    [self.receivedArray insertObject:cmd atIndex:0];
                }
                // 关闭掉socket
                if ( cmd.length<=0 ){
                    [self closeAll];
                    [self.receivedArray addObject:@"close"];
                    [self.receivedArray addObject:@"close"];
                } else {
                    [self.lview  callLuaToExecuteServerCmd];
                }
            }
        }
            break;
        case NSStreamEventHasSpaceAvailable:
            self.canWrite = YES;
            break;
        case NSStreamEventErrorOccurred:
        case NSStreamEventEndEncountered:
            [self closeAll];
            [self.receivedArray addObject:@"close"];
            [self.receivedArray addObject:@"close"];
            [self.lview  callLuaToExecuteServerCmd];
        default:
            break;
    }
}

-(void) closeAll{
    _outputStreamCompleted = NO;
    _inputStreamCompleted = NO;
    
    self.canWrite = FALSE;
    self.state = -1;
    
    [_inputStream close];
    _inputStream = nil;
    
    [_outputStream close];
    _outputStream = nil;
    
    if( !self.myThread.isCancelled ) {
        [self.myThread cancel];
    }
}

-(void) sendOneData{
    NSData* data = self.sendArray.lastObject;
    if( self.canWrite && data) {
        [self.sendArray removeLastObject];
        if( data.length > 0 ) {
            NSInteger sendLength = [_outputStream write:data.bytes maxLength:data.length];
            if( sendLength != data.length ) {
                LVError(@"Debuger socket Send length Error : %d != %d", (int)sendLength, (int)data.length);
            }
        }
    }
}

/////////////////////////发送信息给服务器////////////////////////
- (void) sendString:(NSString *)string
{
    if( self.canWrite ) {
        NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
        
        NSMutableData* buffer = [[NSMutableData alloc] init];
        
        //head
//        NSUInteger len = data.length;
//        unsigned char head[4] = {0};
//        head[0] = (len>>24);
//        head[1] = (len>>16);
//        head[2] = (len>>8);
//        head[3] = (len);
//        [buffer appendBytes:head length:4];
        
        [buffer appendData:data];
        
        [self.sendArray insertObject:buffer atIndex:0];
        
        [self sendOneData];
    }
}
@end

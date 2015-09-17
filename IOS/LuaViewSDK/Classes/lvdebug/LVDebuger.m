//
//  LVDebuger.m
//  CFSocketDemo
//
//  Created by dongxicheng on 9/16/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVDebuger.h"
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

static NSString* receivedCmd = nil;

#define SOCKET_ERROR        (-1)
#define SOCKET_CONNECTINTG  (0)
#define SOCKET_SUCCESS      (1)

@interface LVDebuger ()
@property(nonatomic,strong) NSThread* myThread;
@property(nonatomic,assign) BOOL canWrite;
@property(nonatomic,assign) BOOL closed;
@property(nonatomic,strong) NSMutableArray* dataArray;
@property(nonatomic,assign) NSInteger state;
@end

@implementation LVDebuger{
    CFSocketRef _socket;
}

-(id) init{
    self  = [super init];
    if( self ) {
        self.myThread = [[NSThread alloc] initWithTarget:self selector:@selector(run:) object:nil];
        self.myThread.name = @"LuaView.Debuger";
        self.dataArray = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void) dealloc{
    [self closeSocket];
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

-(void) run:(id) obj{
    @autoreleasepool {
        [self Connect:@"127.0.0.1" port:9876];
        
        NSRunLoop *runLoop = [NSRunLoop currentRunLoop];
        [runLoop addPort:[NSMachPort port] forMode:NSDefaultRunLoopMode];
        [runLoop run];
    }
}

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    static LVDebuger* temp = nil;
    dispatch_once(&onceToken, ^{
        temp = [[LVDebuger alloc] init];
        [temp startThread];
    });
    return temp;
}

+ (NSString*) getCmd{
    NSString* cmd = receivedCmd;
    if( cmd ) {
        receivedCmd = nil;
    }
    return cmd;
}

+ (void) sendCmd:(NSString*) cmdName info:(NSString*) info{
    [LVDebuger sendCmd:cmdName fileName:nil info:info];
}

+ (void) sendCmd:(NSString*) cmdName fileName:(NSString*)fileName info:(NSString*) info{
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
    [[LVDebuger sharedInstance] sendString:buffer];
}

-(void)Connect:(NSString*) ip port:(NSUInteger)port
{
    //////////////////////创建套接字//////////////
    CFSocketContext socketConent = {0,NULL,NULL,NULL,NULL};
    socketConent.info = (__bridge void *)(self);
    _socket = CFSocketCreate(
                             kCFAllocatorDefault,
                             PF_INET,
                             SOCK_STREAM,
                             IPPROTO_TCP,
                             kCFSocketConnectCallBack|kCFSocketReadCallBack|kCFSocketWriteCallBack,     // 类型，表示连接时调用
                             ServerConnectCallBack,    // 调用的函数
                             &socketConent );
    
    ////////////////////////////设置地址///////////////////
    struct   sockaddr_in  addr = {0};
    memset(&addr , 0,sizeof(addr));
    addr.sin_len = sizeof(addr);
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);// 端口
    addr.sin_addr.s_addr = inet_addr([ip  UTF8String]);
    
    CFDataRef address = CFDataCreate(
                                     kCFAllocatorDefault,
                                     (UInt8*)&addr,
                                     sizeof(addr));
    
    /////////////////////////////执行连接/////////////////////
    CFSocketConnectToAddress(_socket,address,-1);
    CFRunLoopRef cfrl = CFRunLoopGetCurrent();   // 获取当前运行循环
    CFRunLoopSourceRef  source = CFSocketCreateRunLoopSource(kCFAllocatorDefault,_socket,0);//定义循环对象
    CFRunLoopAddSource(cfrl,source,kCFRunLoopCommonModes); //将循环对象加入当前循环中
    CFRelease(source);
}

-(void) closeSocket{
    self.closed = TRUE;
    self.canWrite = FALSE;
    
    if (_socket != NULL)
    {
        CFSocketInvalidate(_socket);
        CFRelease(_socket);
        _socket = NULL;
    }
}

static void ServerConnectCallBack( CFSocketRef socket,
                                  CFSocketCallBackType type,
                                  CFDataRef address,
                                  const void *data,
                                  void* info)
{
    LVDebuger* debuger = (__bridge LVDebuger *)(info);
    switch ( type ){
        case kCFSocketReadCallBack: {
            NSString* cmd = readString(socket);
            NSLog(@"%@", cmd);
            receivedCmd = cmd;
            // 关闭掉socket
            if ( cmd.length<=0 ){
                [debuger closeSocket];
            } else {
                [[NSNotificationCenter defaultCenter] postNotificationName:LuaViewRunCmdNotification object:cmd];
            }
            break;
        }
        case kCFSocketWriteCallBack: {
            debuger.canWrite = YES;
            [debuger sendOneData];
            break;
        }
        case kCFSocketConnectCallBack:
            if( data ) {
                LVError(@"Debuger Socket Connect Error" );
                debuger.state = SOCKET_ERROR;
            } else {
                LVLog(@"Debuger Socket connect Success");
                debuger.state = SOCKET_SUCCESS;
            }
            break;
        default: {
            LVLog(@"connect type %d", (int)type );
            break;
        }
    }
}

-(void) sendOneData{
    NSData* data = self.dataArray.lastObject;
    if( self.canWrite && data) {
        [self.dataArray removeLastObject];
        if( data ) {
            NSInteger sendLength = send(CFSocketGetNative(_socket), data.bytes, data.length, 0);
            if( sendLength!=data.length ) {
                LVError(@"Debuger socket Send length Error : %d != %d", (int)sendLength, (int)data.length);
            }
        }
    }
}

///////////////////监听来自服务器的信息///////////////////
static NSString* readString(CFSocketRef socket)
{
    unsigned char head[4] = {0};
    NSUInteger readLength0 = 0;
    if( recv( CFSocketGetNative(socket), head, sizeof(head), 0 )==sizeof(head) ) {
        NSUInteger d0 = head[0];
        NSUInteger d1 = head[1];
        NSUInteger d2 = head[2];
        NSUInteger d3 = head[3];
        readLength0 = (d0<<24) + (d1<<16) + (d2<<8) + d3;
    }
    
    unsigned char buffer[512] = {0};
    NSUInteger readLen = readLength0;
    NSMutableData* data = [[NSMutableData alloc] init];
    for(;readLen>0;){
        NSUInteger bufferLen = readLen>=sizeof(buffer)?sizeof(buffer):readLen;
        NSUInteger recvLen = recv( CFSocketGetNative(socket), buffer, bufferLen, 0 );
        if ( recvLen>0 ) {
            [data appendBytes:buffer length:recvLen];
            readLen -= recvLen;
        } else {
            break;
        }
    }
    NSString* ret = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    return ret;
}
/////////////////////////发送信息给服务器////////////////////////
- (void) sendString:(NSString *)string
{
    if( self.canWrite ) {
        NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
        NSUInteger len = data.length;
        NSMutableData* buffer = [[NSMutableData alloc] init];
        unsigned char head[4] = {0};
        head[0] = (len>>24);
        head[1] = (len>>16);
        head[2] = (len>>8);
        head[3] = (len);
        [buffer appendBytes:head length:4];
        [buffer appendData:data];
        
        [self.dataArray insertObject:buffer atIndex:0];
        
        [self sendOneData];
        //    NSInteger sendLength = send(CFSocketGetNative(_socket), buffer.bytes, buffer.length, 0);
        //    NSLog(@"socket Send length : %d", (int)sendLength);
    }
}
@end

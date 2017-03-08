//
//  LuaView.m
//  LuaViewSDK
//
//  Created by 董希成 on 2017/3/7.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

#import "LuaView.h"
#import "LuaViewCore.h"

@interface LuaView ()
@end

@implementation LuaView



-(id) init{
    self = [super init];
    if( self ) {
        [self createLuaViewCore];
    }
    return self;
}

-(id) initWithFrame:(CGRect)frame{
    self  = [super initWithFrame:frame];
    if( self ) {
        [self createLuaViewCore];
    }
    return self;
}

/*
 * 初始化LuaViewCore
 */
-(void) createLuaViewCore{
    self.luaviewCore = [[LuaViewCore alloc] init];
    self.luaviewCore.window = self;
}

-(UIViewController*) viewController{
    return self.luaviewCore.viewController;
}

-(void) setViewController:(UIViewController *)viewController{
    self.luaviewCore.viewController = viewController;
}

-(LVBundle*) bundle{
    return self.luaviewCore.bundle;
}

-(void) setBundle:(LVBundle *)bundle{
    self.luaviewCore.bundle = bundle;
}

-(void) releaseLuaView{
    [self.luaviewCore releaseLuaView];
}

//
-(void) viewWillAppear{
    [self.luaviewCore viewWillAppear];
}

-(void) viewDidAppear{
    [self.luaviewCore viewDidAppear];
}

-(void) viewWillDisAppear{
    [self.luaviewCore viewWillDisAppear];
}

-(void) viewDidDisAppear{
    [self.luaviewCore viewDidDisAppear];
}



#pragma mark - 摇一摇回调
// 摇一摇开始摇动
- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event{
    [self.luaviewCore motionBegan:motion withEvent:event];
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event{
    [self.luaviewCore motionCancelled:motion withEvent:event];
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event{
    [self.luaviewCore motionEnded:motion withEvent:event];
}

/**
 *  load and run script
 *
 *  @param fileName 本地文件名
 *
 *  @return 返回运行结果
 */
-(NSString*) runFile:(NSString*) fileName{
    return [self.luaviewCore runFile:fileName];
}

/**
 *  运行一个包, main.lv是主入口
 *
 *  @param fileName 本地文件名
 *
 *  @return 返回运行结果
 */
-(NSString*) runPackage:(NSString*) packageName{
    return [self.luaviewCore runPackage:packageName];
}

/**
 *  运行一个包, main.lv是主入口
 *
 *  @param fileName 本地文件名
 *  @args args 参数
 *
 *  @return 返回运行结果
 */
-(NSString*) runPackage:(NSString*) packageName args:(NSArray*) args{
    return [self.luaviewCore runPackage:packageName args:args];
}

/**
 *  运行签名的脚本文件
 *
 *  @param fileName 本地文件名
 *
 *  @return 返回运行结果
 */
-(NSString*) runSignFile:(NSString*) fileName{
    return [self.luaviewCore runSignFile:fileName];
}

/**
 *   load and run script
 *
 *  @param chars    脚本字符流
 *  @param length   脚本字符流的长度
 *  @param fileName 文件名,用于出错提示, 可以为空
 *
 *  @return 运行结果
 */
-(NSString*) runData:(NSData*) data fileName:(NSString*) fileName{
    return [self.luaviewCore runData:data fileName:fileName];
}

-(NSString*) runData:(NSData*) data fileName:(NSString*) fileName changeGrammar:(BOOL) changeGrammar{
    return [self.luaviewCore runData:data fileName:fileName changeGrammar:changeGrammar];
}

/**
 * 加载签名的脚本文件，读取文件并调用lvL_loadbuffer
 *
 * @param fileName 本地文件名
 *
 * @return 返回错误描述
 */
-(NSString*) loadSignFile:(NSString *)fileName{
    return [self.luaviewCore loadSignFile:fileName];
}

/**
 * 加载脚本文件，读取文件并调用lvL_loadbuffer
 *
 * @param fileName 本地文件名
 *
 * @return 返回错误描述
 */
-(NSString*) loadFile:(NSString *)fileName{
    return [self.luaviewCore loadFile:fileName];
}

-(void) setFrame:(CGRect)frame{
    [super setFrame:frame];
    [self.callback luaviewFrameDidChange:self];
}

- (void)setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key{
    [self.luaviewCore setObject:object forKeyedSubscript:key];
}

@end

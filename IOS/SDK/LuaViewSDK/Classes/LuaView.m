/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LuaView.h"
#import "LuaViewCore.h"

@interface LuaView ()
@property (nonatomic,assign) BOOL isOnShowed;
@end

@implementation LuaView



-(id) init{
    self = [super init];
    if( self ) {
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
    [self.luaviewCore pushWindow:self];
    self.backgroundColor = [UIColor clearColor];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidShow:)
                                                 name:UIKeyboardDidShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidHide:)
                                                 name:UIKeyboardDidHideNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onForeground)
                                                 name:UIApplicationDidBecomeActiveNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onBackground)
                                                 name:UIApplicationDidEnterBackgroundNotification
                                               object:nil];
}

-(void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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


#pragma mark - view appear

-(void) viewWillAppear{
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"ViewWillAppear"];
    }
}

-(void) viewDidAppear{
    self.isOnShowed = YES;
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"onShow"];//@"ViewDidAppear"
    }
}

-(void) viewWillDisAppear{
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"ViewWillDisAppear"];
    }
}

-(void) viewDidDisAppear{
    self.isOnShowed = NO;
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"onHide"];//@"ViewDidDisAppear"
    }
}

-(void) onForeground {
    lua_State* L = self.luaviewCore.l;
    if( L && self.isOnShowed ) {
        lua_checkstack32(L);
        lua_pushboolean(L, YES);
        [self lv_callLuaCallback:@"onShow" key2:nil argN:1];
    }
}

-(void) onBackground {
    lua_State* L = self.luaviewCore.l;
    if( L && self.isOnShowed ) {
        lua_checkstack32(L);
        lua_pushboolean(L, YES);
        [self lv_callLuaCallback:@"onHide" key2:nil argN:1];
    }
}

- (void)didMoveToSuperview{
    [super didMoveToSuperview];
    
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"DidMoveToSuperview"];
    }
}

- (void)didMoveToWindow{
    [super didMoveToWindow];
    
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"DidMoveToSuperview"];
    }
}

-(void) layoutSubviews{
    [super layoutSubviews];
    
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@STR_ON_LAYOUT];
    }
}


#pragma mark - keyboard

-(void) keyboardWillShow:(NSNotification *)notification {
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"KeyboardWillShow"];
    }
}
-(void) keyboardDidShow:(NSNotification *)notification {
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"KeyboardDidShow"];
    }
}
-(void) keyboardWillHide:(NSNotification *)notification {
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"KeyboardWillHide"];
    }
}
-(void) keyboardDidHide:(NSNotification *)notification {
    lua_State* L = self.luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        [self lv_callLuaCallback:@"KeyboardDidHide"];
    }
}

#pragma mark - 摇一摇相关方法
// 摇一摇开始摇动
- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    lua_State* L = self.luaviewCore.l;
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( L ) {
            lua_checkstack32(L);
            [self lv_callLuaCallback:@"ShakeBegin"];
        }
    }
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        lua_State* L = self.luaviewCore.l;
        if( L ) {
            lua_checkstack32(L);
            [self lv_callLuaCallback:@"ShakeCanceled"];
        }
    }
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        lua_State* L = self.luaviewCore.l;
        if( L ) {
            lua_checkstack32(L);
            [self lv_callLuaCallback:@"ShakeEnded"];
        }
    }
}

-(lua_State*) l{
    return self.luaviewCore.l;
}

@end

//
//  LuaViewCore.h
//  LuaViewSDK
//
//  Created by 董希成 on 2017/3/7.
//  Copyright © 2017年 dongxicheng. All rights reserved.
//

//
//
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LVDebugConnection.h"


@class LView;
@class LVBlock;
@class LVBundle;
@protocol LVProtocal;


#pragma mark -LuaView 类加载脚本调用接口
@interface LuaViewCore : NSObject

@property (nonatomic,strong) LVRSA* rsa;

@property (nonatomic,strong) LVBundle* bundle;

@property (nonatomic,assign) BOOL changeGrammar; // 是否需要语法转换（原先luaview语法和lua标准语法的区别是‘.’和':'互换了），默认是非标准语法，需要转换

//---------------------------------------------------------------------
/**
* 调试开关
*
*/
@property (nonatomic,assign) BOOL checkDebugerServer; // 是否检查调试器
// @property (nonatomic,assign) BOOL openDebugger; // 开启调试

//---------------------------------------------------------------------
/**
* 渲染窗口 压栈
*
*/
-(void) pushWindow:(UIView*) window;
-(void) pushRenderTarget:(UIView*) window;

/**
* 渲染窗口 出栈
*/
-(void) popWindow:(UIView*) window;
-(void) popRenderTarget:(UIView*) window;

//---------------------------------------------------------------------
/**
* 加载脚本文件，读取文件并调用lvL_loadbuffer
*
* @param fileName 本地文件名
*
* @return 返回错误描述
*/
-(NSString*) loadFile:(NSString *)fileName;

/**
* 加载签名的脚本文件，读取文件并调用lvL_loadbuffer
*
* @param fileName 本地文件名
*
* @return 返回错误描述
*/
-(NSString*) loadSignFile:(NSString *)fileName;

/**
* 加载代码data
*/
- (NSString*)loadData:(NSData *)data fileName:(NSString *)fileNam;

/**
*  load and run script
*
*  @param fileName 本地文件名
*
*  @return 返回运行结果
*/
-(NSString*) runFile:(NSString*) fileName;

/**
*  运行一个包, main.lv是主入口
*
*  @param fileName 本地文件名
*
*  @return 返回运行结果
*/
-(NSString*) runPackage:(NSString*) packageName;

/**
*  运行一个包, main.lv是主入口
*
*  @param fileName 本地文件名
*  @args args 参数
*
*  @return 返回运行结果
*/
-(NSString*) runPackage:(NSString*) packageName args:(NSArray*) args;

/**
*  运行签名的脚本文件
*
*  @param fileName 本地文件名
*
*  @return 返回运行结果
*/
-(NSString*) runSignFile:(NSString*) fileName;

/**
*   load and run script
*
*  @param chars    脚本字符流
*  @param length   脚本字符流的长度
*  @param fileName 文件名,用于出错提示, 可以为空
*
*  @return 运行结果
*/
-(NSString*) runData:(NSData*) data fileName:(NSString*) fileName;

-(NSString*) runData:(NSData*) data fileName:(NSString*) fileName changeGrammar:(BOOL) changeGrammar;

/**
*
*调用lua脚本, functionName:lua的function名称,  environment:运行窗口,  args:传递参数
*
*/
-(NSString*) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args;
-(NSString*) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args;
-(NSString*) callLua:(NSString*) functionName args:(NSArray*) args;
-(LVBlock*) getLuaBlock:(NSString*) name;

/**
*  释放虚拟机回收luaView
*/
-(void) releaseLuaView;

/**
*  Layer模式优化性能
*/
@property (nonatomic,assign) BOOL closeLayerMode;


/**
*  图片首次出现是否使用动画
*/
@property (nonatomic,assign) BOOL disableAnimate;

@property(nonatomic,copy) NSArray* registerClasses;

@property (nonatomic,weak) UIViewController* viewController;// 所在的ViewController

@end


#pragma mark -  设置资源搜索路径
@interface LuaViewCore ()


/*
 * packageName: 包名  比如:"ppt"
 * info格式: { "url" : "http://g.tbcdn.cn/ju/lua/3.2.12/ppt4.4.0.js" , "time":"2015-11-18 09:53"}
 */
+(void) downloadPackage:(NSString*)package  withInfo:(NSDictionary*)info;

@end

#pragma mark - Register 注册外部api接口
@interface LuaViewCore ()

- (void) setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key;
- (void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel;// 只注册指定API
- (void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel weakMode:(BOOL) weakMode;// 只注册指定API
- (void) registerObject:(id) object forName:(NSString*) name;// 注册改对象的所有api
- (void) registerObject:(id) object forName:(NSString*) name weakMode:(BOOL) weakMode;// 注册改对象的所有api
- (void) unregisteObjectForName:(NSString*) name;// 取消注册对象


- (void) registerLibs:(id) lib;
- (void) registerName:(NSString*) name withObject:(id) object;
- (void) registerPanel:(id) panel;
- (void) registerPanel:(id) panel forName:(NSString*) name;
- (void) unregister:(NSString*) name;

@end



#pragma mark -  LViewBlock lua闭包参数获取使用
@interface LuaViewCore ()

- (BOOL) argumentToBool:(int) index;
- (double)  argumentToNumber:(int) index;
- (id) argumentToObject:(int) index;

@end


#pragma mark - debugger 只是调试工具使用
@interface LuaViewCore ()

-(void) callLuaToExecuteServerCmd;
@property (nonatomic,strong) LVDebugConnection* debugConnection;

@end


#pragma mark - Property 系统使用的, 基本上不用关心细节
@interface LuaViewCore ()
@property (nonatomic,assign) BOOL runInSignModel;// 加密模式，优先加载加密脚本
@property (nonatomic, weak)   LuaViewCore* lv_luaviewCore;
@property (nonatomic, assign) LVUserDataInfo* lv_userData;// 脚本中的window对象 数据绑定
@property (nonatomic, assign) lua_State* l; // lua 状态机

-(void) containerAddSubview:(UIView *)view;


@end


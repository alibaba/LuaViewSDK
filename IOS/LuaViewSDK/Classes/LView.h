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
@protocol LVProtocal;
@class LVBundle;

#pragma mark - LuaView窗口大小变动回调
@protocol LVCallback <NSObject>
- (void) luaviewFrameDidChange:(LView *)lView;
@end


#pragma mark -LuaView 类加载脚本调用接口
@interface LView : UIView

@property(nonatomic,weak) id<LVCallback> callback; //用于LuaView回调( luaView大小改变 等回调)

@property (nonatomic,weak) UIViewController* viewController;// 所在的ViewController


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

/**
 * 加载签名的脚本文件，读取文件并调用lvL_loadbuffer
 *
 * @param fileName 本地文件名
 *
 * @return 返回错误描述
 */
-(NSString*) loadSignFile:(NSString *)fileName;

/**
 * 加载脚本文件，读取文件并调用lvL_loadbuffer
 *
 * @param fileName 本地文件名
 *
 * @return 返回错误描述
 */
-(NSString*) loadFile:(NSString *)fileName;

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

@end


#pragma mark -  设置资源搜索路径
@interface LView ()

@property (nonatomic,strong) LVBundle* bundle;

/*
 * packageName: 包名  比如:"ppt"
 * info格式: { "url" : "http://g.tbcdn.cn/ju/lua/3.2.12/ppt4.4.0.js" , "time":"2015-11-18 09:53"}
 */
+(void) downLoadPackage:(NSString*)package  withInfo:(NSDictionary*)info;

@end

#pragma mark -  各种系统回调, 回调会传递到lua脚本中执行脚本代码
@interface LView ()

-(void) viewWillAppear;
-(void) viewDidAppear;
-(void) viewWillDisAppear;
-(void) viewDidDisAppear;

#pragma mark - 摇一摇回调
// 摇一摇开始摇动
- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event;
// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event;
// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event;

@end


#pragma mark - Register 注册外部api接口
@interface LView ()

- (void) setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key;
- (void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel;// 只注册指定API
- (void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel weakMode:(BOOL) weakMode;// 只注册指定API
- (void) registerObject:(id) object forName:(NSString*) name;// 注册改对象的所有api
- (void) registerObject:(id) object forName:(NSString*) name weakMode:(BOOL) weakMode;// 注册改对象的所有api
- (void) unregisteObjectForName:(NSString*) name;// 取消注册对象

- (void) registerCustomPanel:(Class) c boundName:(NSString*) boundName;
@end



#pragma mark -  LViewBlock lua闭包参数获取使用
@interface LView ()

- (BOOL) argumentToBool:(int) index;
- (double)  argumentToNumber:(int) index;
- (id) argumentToObject:(int) index;

@end


#pragma mark - debugger 只是调试工具使用
@interface LView ()

-(void) callLuaToExecuteServerCmd;
@property (nonatomic,strong) LVDebugConnection* debugConnection;

@end


#pragma mark - Property 系统使用的, 基本上不用关心细节
@interface LView ()
@property (nonatomic,assign) BOOL runInSignModel;// 加密模式，优先加载加密脚本

@property (nonatomic, weak)   UIView* conentView; // 运行环境view
@property (nonatomic, weak)   LView* lv_lview;
@property (nonatomic, assign) LVUserDataInfo* lv_userData;// 脚本中的window对象 数据绑定
@property (nonatomic, assign) lv_State* l; // lua 状态机
@property(nonatomic,assign) BOOL contentViewIsWindow;// contentView是否是窗口

-(void) containerAddSubview:(UIView *)view;

// 设置证书地址
- (void) setPublicKeyFilePath:(NSString*) filePath;

@end


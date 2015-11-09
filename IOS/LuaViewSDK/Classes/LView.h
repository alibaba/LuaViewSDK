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

#pragma mark - LuaView窗口大小变动回调
@protocol LVCallback <NSObject>
- (void) luaviewFrameDidChange:(LView *)lView;
@end


#pragma mark -LuaView 类
@interface LView : UIView

@property(nonatomic,weak) id<LVCallback> callback; //用于LuaView回调( luaView大小改变 等回调)

@property (nonatomic,weak) UIViewController* viewController;// 所在的ViewController
@property (nonatomic,assign) BOOL runInSignModel;// 加密脚本/明文脚本
@property (nonatomic,strong) LVDebugConnection* debugConnection;


/**
 *  load and run script
 *
 *  @param fileName 本地文件名
 *
 *  @return 返回运行结果
 */
-(int) runFile:(NSString*) fileName;

/**
 *  运行签名的脚本文件
 *
 *  @param fileName 本地文件名
 *
 *  @return 返回运行结果
 */
-(int) runSignFile:(NSString*) fileName;

/**
 *   load and run script
 *
 *  @param chars    脚本字符流
 *  @param length   脚本字符流的长度
 *  @param fileName 文件名,用于出错提示, 可以为空
 *
 *  @return 运行结果
 */
-(int) runData:(NSData*) data fileName:(NSString*) fileName;




/*
 *
 *调用lua脚本, functionName:lua的function名称,  environment:运行窗口,  args:传递参数
 *
 */
-(void) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args;
-(void) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args;
-(void) callLua:(NSString*) functionName args:(NSArray*) args;
-(LVBlock*) getLuaBlock:(NSString*) name;

/**
 *  释放虚拟机回收luaView
 */
-(void) releaseLuaView;

#pragma mark - 系统使用的, 基本上不用关心细节
//@interface LView (LViewPrivateData)<LVProtocal>
@property (nonatomic, weak)   UIView* conentView; // 运行环境view
@property (nonatomic, weak)   LView* lv_lview;
@property (nonatomic, assign) LVUserDataView* lv_userData;// 脚本中的window对象 数据绑定
@property (nonatomic, assign) lv_State* l; // lua 状态机


-(void) containerAddSubview:(UIView *)view;
@property(nonatomic,assign) BOOL contentViewIsWindow;// contentView是否是窗口
//@end
@end



@interface LView (LViewPackageManager)
/**
 *  解压本地脚本包
 */
+(BOOL) unpackageOnceWithFile:(NSString*) fileName;

/*
 * name: 报名  比如:"home"
 * info格式: { "url" : "http://g.tbcdn.cn/ju/lua/1.2.1/2015-05-04.js" , "time":"1430740218338", "luaview":"1.0.0" }
 */
+(void) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*)info;


/** 图片资源bundle查询路径
 *
 */
+(void) setBundleSearchPath:(NSArray*) path;
+(NSArray*) bundleSearchPath;

@end




@interface LView (LViewSystemCallback)

#pragma mark -  各种系统回调, 回调会传递到lua脚本中执行脚本代码
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



@interface LView (LViewRegister)

- (void) setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key;
- (void) registerObject:(id) object name:(NSString*) name sel:(SEL) sel;// 只注册指定API
- (void) registerObject:(id) object name:(NSString*) name sel:(SEL) sel weakMode:(BOOL) weakMode;// 只注册指定API
- (void) registerObject:(id) object name:(NSString*) name;// 注册改对象的所有api
- (void) registerObject:(id) object name:(NSString*) name weakMode:(BOOL) weakMode;// 注册改对象的所有api
- (void) unregisteObjectForName:(NSString*) name;// 取消注册对象

- (void) addCustomPanel:(Class) c boundName:(NSString*) boundName;
@end




@interface LView (LViewBlock)

- (BOOL) argumentToBool:(int) index;
- (double)  argumentToNumber:(int) index;
- (id) argumentToObject:(int) index;

@end


// 只是调试工具使用
@interface LView (LViewDebuger)

-(void) callLuaToExecuteServerCmd;

@end



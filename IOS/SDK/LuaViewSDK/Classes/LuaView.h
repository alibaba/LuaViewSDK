/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LuaViewCore.h"


@class LuaView;

#pragma mark - LuaView窗口大小变动回调
@protocol LVCallback <NSObject>
- (void) luaviewFrameDidChange:(LuaView *)lView;
@end


@interface LuaView : UIView<LVProtocal>

@property (nonatomic,weak) UIViewController* viewController;// 所在的ViewController
@property (nonatomic,strong) LVBundle* bundle;

@property(nonatomic,weak) id<LVCallback> callback; //用于LuaView回调( luaView大小改变 等回调)
@property (nonatomic,strong) LuaViewCore* luaviewCore;
@property (nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

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

-(void) releaseLuaView;


//

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

- (void) setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key;


-(lua_State*) l; // 获取 lua 状态机

@end

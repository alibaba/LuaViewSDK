/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LView.h"


@interface LVImage : UIImageView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lua_State*) l;

-(void) setImageByName:(NSString*) imageName;
-(void) setImageByData:(NSData*) data;
-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished;
-(void) effectParallax:(CGFloat)dx dy:(CGFloat)dy ;
-(void) effectClick:(NSInteger)color alpha:(CGFloat)alpha;

/*
 * Lua 脚本回调
 */
-(void) callLuaDelegate:(id) obj;

/**
 *  图片首次出现是否使用动画
 */
@property (nonatomic,assign) BOOL disableAnimate;

/*
 * luaview所有扩展类的桥接协议: 只是一个静态协议, luaview统一调用该接口加载luaview扩展的类
 */
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end

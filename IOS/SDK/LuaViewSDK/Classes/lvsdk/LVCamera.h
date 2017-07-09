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


/*
 * luaview虚拟机注册的组件标准: 需要满足两个协议 LVProtocal(功能协议), LVClassProtocal(注册协议)
 *
 *
 */

@interface LVCamera : UIView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@end

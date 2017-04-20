/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIScrollView+LuaView.h"
#import "LVFlowLayout.h"

/*
 * 实现协议LVProtocal确保所有在luaview扩展类的实例对象都包含有这些基本的属性和方法, 是所有LuaView扩展类的实例对象的通用协议
 */
@interface LVCollectionView : UICollectionView<LVProtocal, LVClassProtocal>

/*
 * 所有在luaview扩展类的实例对象都包含有这些基本的属性和方法
 */
@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;// 对应的lua运行内核
@property(nonatomic,assign) LVUserDataInfo* lv_userData;// native对象对应的脚本对象
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

/*
 * 构造方法
 */
- (id) init:(lua_State*) l;

/*
 * delegate + datasource
 */
@property(nonatomic,weak) id lvScrollViewDelegate;

/*
 * CollectionView的布局管理器(位置支持某行吸顶功能需要自定义布局管理方式)
 */
@property(nonatomic,strong) LVFlowLayout* lvflowLayout;

/*
 * luaview所有扩展类的桥接协议: 只是一个静态协议, luaview统一调用该接口加载luaview扩展的类
 */
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;
@end

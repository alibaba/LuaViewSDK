/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVTransform3D : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject; // 返回native对象

@property(nonatomic,assign) CATransform3D transform;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(int) pushTransform3D:(lua_State *)L  transform3d:(CATransform3D) t;

@end

/* rotation和scale函数只适用于没有x、y轴rotation的矩阵 */

extern void CATransform3DSetRotation(CATransform3D *t, CGFloat v); /* z-axis */

extern double CATransform3DGetRotation(CATransform3D *t); /* returns [-M_PI, M_PI] */

extern void CATransform3DSetScaleX(CATransform3D *t, CGFloat v);
extern void CATransform3DSetScaleY(CATransform3D *t, CGFloat v);
extern void CATransform3DSetScaleZ(CATransform3D *t, CGFloat v);

extern double CATransform3DGetScaleX(CATransform3D *t);
extern double CATransform3DGetScaleY(CATransform3D *t);
extern double CATransform3DGetScaleZ(CATransform3D *t);

extern void CATransform3DSetTranslationX(CATransform3D *t, CGFloat v);
extern void CATransform3DSetTranslationY(CATransform3D *t, CGFloat v);
extern void CATransform3DSetTranslationZ(CATransform3D *t, CGFloat v);

extern double CATransform3DGetTranslationX(CATransform3D *t);
extern double CATransform3DGetTranslationY(CATransform3D *t);
extern double CATransform3DGetTranslationZ(CATransform3D *t);

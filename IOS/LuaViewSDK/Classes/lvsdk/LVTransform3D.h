//
//  LVTransform3D.h
//  JU
//
//  Created by dongxicheng on 12/30/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVTransform3D : NSObject<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject; // 返回native对象

@property(nonatomic,assign) CATransform3D transform;


+(int) classDefine:(lv_State *)L ;

+(int) pushTransform3D:(lv_State *)L  transform3d:(CATransform3D) t;

@end

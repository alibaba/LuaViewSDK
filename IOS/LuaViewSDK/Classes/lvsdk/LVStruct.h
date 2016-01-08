//
//  LVStruct.h
//  LVSDK
//
//  Created by dongxicheng on 7/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

#define LV_STRUCT_MAX_LEN 16

@interface LVStruct : NSObject<LVProtocal>
@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject; // 返回native对象


+(int) classDefine:(lv_State *)L ;

+(int) pushStructToLua:(lv_State*)L data:(void*)data;

-(CGFloat*) dataPointer;

@end

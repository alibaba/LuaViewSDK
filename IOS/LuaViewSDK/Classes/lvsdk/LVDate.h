//
//  LVDate.h
//  LVSDK
//
//  Created by dongxicheng on 1/13/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface LVDate : NSObject<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,strong) NSDate* date;

- (id) lv_nativeObject; // 返回native对象

+(int) classDefine:(lv_State *) L ;


@end

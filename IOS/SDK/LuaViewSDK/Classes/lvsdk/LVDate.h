//
//  LVDate.h
//  LVSDK
//
//  Created by dongxicheng on 1/13/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface LVDate : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,strong) NSDate* date;

- (id) lv_nativeObject; // 返回native对象

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;


@end

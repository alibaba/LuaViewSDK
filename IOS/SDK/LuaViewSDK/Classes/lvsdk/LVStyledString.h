//
//  LVAttributedString.h
//  LVSDK
//
//  Created by dongxicheng on 4/17/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


//LVData
@interface LVStyledString : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,strong) NSMutableAttributedString* mutableStyledString;//真实的数据

-(id) init:(lua_State*) l;
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end

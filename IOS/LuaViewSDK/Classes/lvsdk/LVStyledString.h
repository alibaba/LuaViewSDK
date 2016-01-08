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
@interface LVStyledString : NSObject<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,strong) NSMutableAttributedString* mutableStyledString;//真实的数据

-(id) init:(lv_State*) l;
+(int) classDefine:(lv_State *)L ;

@end

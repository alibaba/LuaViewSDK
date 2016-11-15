//
//  LVGesture.h
//  LVSDK
//
//  Created by dongxicheng on 1/22/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVGesture : NSObject<LVClassProtocal>

+(const lvL_reg*) baseMemberFunctions;

+(void) releaseUD:(LVUserDataInfo *) user;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;


@end

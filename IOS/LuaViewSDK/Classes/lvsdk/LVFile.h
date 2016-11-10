//
//  LVFile.h
//  LVSDK
//
//  Created by dongxicheng on 4/15/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVFile : NSObject

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

@end

//
//  LVFunctionRegister
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "LView.h"

@interface LVExGlobalFunc : NSObject<LVClassProtocal>

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end

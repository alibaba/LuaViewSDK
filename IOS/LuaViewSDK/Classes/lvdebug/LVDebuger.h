//
//  LVDebugCmd.h
//  LVSDK
//
//  Created by 城西 on 15/3/27.
//  Copyright (c) 2015年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

extern void lv_printToServer(lv_State* L, const char* cs, int withTabChar);

@interface LVDebuger : NSObject

+(int) classDefine:(lv_State *)L;


@end

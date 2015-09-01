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

typedef struct _LVUserDataStruct {
    LVUserDataCommonHead;
    CGFloat data[LV_STRUCT_MAX_LEN];
} LVUserDataStruct;



@interface LVStruct : NSObject

+(int) classDefine:(lv_State *)L ;

+(int) pushStructToLua:(lv_State*)L data:(void*)data;

@end

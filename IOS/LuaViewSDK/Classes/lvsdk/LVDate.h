//
//  LVDate.h
//  LVSDK
//
//  Created by dongxicheng on 1/13/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef struct _LVUserDataDate {
    LVUserDataCommonHead;
    const void* date;
} LVUserDataDate;



@interface LVDate : NSObject

+(int) classDefine:(lv_State *) L ;


@end

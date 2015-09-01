//
//  LVTransform3D.h
//  JU
//
//  Created by dongxicheng on 12/30/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

typedef struct _LVUserDataTransform3D {
    LVUserDataCommonHead;
    CATransform3D transform;
} LVUserDataTransform3D;



@interface LVTransform3D : NSObject

+(int) classDefine:(lv_State *)L ;

+(int) pushTransform3D:(lv_State *)L  transform3d:(CATransform3D) t;

@end

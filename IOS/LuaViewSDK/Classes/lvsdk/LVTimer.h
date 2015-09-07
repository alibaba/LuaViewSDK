//
//  LvTimer.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


typedef struct _LVUserDataTimer {
    LVUserDataCommonHead;
    const void* timer;
} LVUserDataTimer;



@interface LVTimer : NSObject

@property(nonatomic,weak) LView* lview;
@property(nonatomic,assign) LVUserDataTimer* userData;

-(id) init:(lv_State*) l;

-(void) startTimer:(NSTimeInterval) time repeat:(BOOL) repeat;
-(void) cancel;


+(int) classDefine:(lv_State *)L ;


@end

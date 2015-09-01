//
//  LVData.h
//  LVSDK
//
//  Created by dongxicheng on 2/6/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"



typedef struct _LVUserDataData {
    LVUserDataCommonHead;
    const void* data;
} LVUserDataData;


//LVData
@interface LVData : NSObject

@property(nonatomic,weak) LView* lview;
@property(nonatomic,assign) LVUserDataData* userData;

@property(nonatomic,strong) NSMutableData* data;//真实的数据

-(id) init:(lv_State*) l;
+(int) classDefine:(lv_State *)L ;

+(int) createDataObject:(lv_State *)L  data:(NSData*) data;

@end

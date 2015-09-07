//
//  LVAttributedString.h
//  LVSDK
//
//  Created by dongxicheng on 4/17/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"



typedef struct _LVUserDataAttributedString {
    LVUserDataCommonHead;
    const void* attributedString;
} LVUserDataAttributedString;


//LVData
@interface LVAttributedString : NSObject

@property(nonatomic,weak) LView* lview;
@property(nonatomic,assign) LVUserDataAttributedString* userData;

@property(nonatomic,strong) NSMutableAttributedString* mutableAttributedString;//真实的数据

-(id) init:(lv_State*) l;
+(int) classDefine:(lv_State *)L ;

@end

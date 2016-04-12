//
//  LVBaseView.h
//  JU
//
//  Created by dongxicheng on 12/29/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVBaseView : UIView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

-(id) init:(lv_State*) l;

+(const lvL_reg*) baseMemberFunctions;

+(int) classDefine: (lv_State *)L ;

@end


/**
 * callback回调统一处理API
 */
extern int lv_setCallbackByKey(lv_State *L, const char* key, BOOL addGesture);
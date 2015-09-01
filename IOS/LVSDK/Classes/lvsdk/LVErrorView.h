//
//  LVErrorNotice.h
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVErrorView : UIView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;

+(int) classDefine:(lv_State *)L ;

- (void) callLuaFuncToReloadData;

+ (void) setDefaultStyle:(Class) c;

@end

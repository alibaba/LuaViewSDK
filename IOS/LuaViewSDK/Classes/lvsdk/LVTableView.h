//
//  LVTableView.h
//  LVSDK
//
//  Created by dongxicheng on 1/28/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIScrollView+LuaView.h"

#define Identifier "Id"

@interface LVTableView : UITableView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

-(id) init:(lv_State*) l;
+(int) classDefine: (lv_State *)L ;

+ (void) setDefaultStyle:(Class) c;

@property(nonatomic,weak) id lvScrollViewDelegate;

@end

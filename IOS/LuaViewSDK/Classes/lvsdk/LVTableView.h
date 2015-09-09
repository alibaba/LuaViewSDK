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

@interface LVTableView : UITableView<LVProtocal,UITableViewDataSource, UITableViewDelegate>


@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;
@property(nonatomic,assign) CGFloat lv_rotation;
@property(nonatomic,assign) CGFloat lv_rotationX;
@property(nonatomic,assign) CGFloat lv_rotationY;
@property(nonatomic,assign) CGFloat lv_scaleX;
@property(nonatomic,assign) CGFloat lv_scaleY;

-(id) init:(lv_State*) l;
+(int) classDefine: (lv_State *)L ;

+ (void) setDefaultStyle:(Class) c;

@end

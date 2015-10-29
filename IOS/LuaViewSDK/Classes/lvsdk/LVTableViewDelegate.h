//
//  LVTableViewDelegate.h
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVScrollViewDelegate.h"


@interface LVTableViewDelegate : LVScrollViewDelegate<UITableViewDataSource, UITableViewDelegate>

@property(nonatomic,assign) CGFloat dividerHeight;

@end

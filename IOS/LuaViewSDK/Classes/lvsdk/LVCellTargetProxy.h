//
//  LVCellTargetProxy.h
//  LuaViewSDK
//
//  Created by dongxicheng on 10/9/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>


@interface LVCellTargetProxy : NSProxy 

- (id)initWithCell:(id) cell;

@end
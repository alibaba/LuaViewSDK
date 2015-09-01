//
//  LVLuaObjBox.h
//  LVSDK
//
//  Created by dongxicheng on 6/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LView.h"

@interface LVLuaObjBox : NSObject

- (id) init:(lv_State*)L stackID:(int) stackID;

- (void) setProtocols:(NSArray*) protocols;

@end

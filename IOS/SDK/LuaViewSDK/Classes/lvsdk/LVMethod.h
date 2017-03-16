/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"

@interface LVMethod : NSObject

@property (nonatomic,assign) SEL sel;
@property (nonatomic,copy)   NSString* selName;
@property (nonatomic,assign) NSInteger nargs;

-(id) initWithSel:(SEL)sel;

-(int) callObj:(id) obj args:(lua_State*)L;

@end

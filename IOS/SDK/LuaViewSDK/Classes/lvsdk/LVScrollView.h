//
//  LVScrollView.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "UIScrollView+LuaView.h"

@interface LVScrollView : UIScrollView<UIScrollViewDelegate, LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lua_State*) l;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(const struct luaL_Reg*) memberFunctions;

@property(nonatomic,weak) id lvScrollViewDelegate;

@end

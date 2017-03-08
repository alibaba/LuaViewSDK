//
//  LVButton.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/17/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LView.h"


@interface LVButton : UIButton<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lua_State*) l;
-(void) setWebImageUrl:(NSString*)url forState:(UIControlState) state finished:(LVLoadFinished) finished;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

-(void) lvButtonCallBack;

@end

//
//  LVAudioPlayer.h
//  LVSDK
//
//  Created by dongxicheng on 4/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


@interface LVAudioPlayer : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lua_State*) l;

-(void) play;
-(void) stop;


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end

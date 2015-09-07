//
//  LVAudioPlayer.h
//  LVSDK
//
//  Created by dongxicheng on 4/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


typedef struct _LVUserDataAudioPlayer {
    LVUserDataCommonHead;
    const void* player;
} LVUserDataAudioPlayer;



@interface LVAudioPlayer : NSObject

@property(nonatomic,weak) LView* lview;
@property(nonatomic,assign) LVUserDataAudioPlayer* userData;

-(id) init:(lv_State*) l;

-(void) play;
-(void) stop;


+(int) classDefine:(lv_State *)L ;

@end

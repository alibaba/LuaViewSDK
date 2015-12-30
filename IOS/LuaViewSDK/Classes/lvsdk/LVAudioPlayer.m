//
//  LVAudioPlayer.m
//  LVSDK
//
//  Created by dongxicheng on 4/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVAudioPlayer.h"
#import <AVFoundation/AVFoundation.h>
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@implementation LVAudioPlayer{
    AVAudioPlayer* audioPlayer;
}

static void releaseUserDataAudioPlayer(LVUserDataAudioPlayer* user){
    if( user && user->player ){
        LVAudioPlayer* palyer = CFBridgingRelease(user->player);
        user->player = NULL;
        if( palyer ){
            [palyer stop];
            palyer.userData = NULL;
            palyer.lview = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataAudioPlayer(_userData);
}

-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lview = (__bridge LView *)(l->lView);
    }
    return self;
}

-(void) setPlayFileName0:(NSString*) fileName package:(LVPackage*) package{
    NSString* path = [LVUtil cachesPath:fileName package:package];
    if( path ) {
        NSURL* url = [[NSURL alloc] initWithString:path];
        audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];//使用本地URL创建
    }
}

-(void) setPlayFileName:(NSString*) fileName package:(LVPackage*) package{
    if( fileName ==nil )
        return;
    if( [LVUtil isExternalUrl:fileName] ){
        [LVUtil download:fileName callback:^(NSData *fileData) {
            NSData* theFileNameData = [fileName dataUsingEncoding:NSUTF8StringEncoding];
            NSString* md5Path = [LVUtil MD5HashFromData:theFileNameData];
            if(  [LVUtil saveData:fileData toFile:md5Path] ) {
                [self setPlayFileName0:md5Path package:package];
            }
        }];
    } else {
        [self setPlayFileName0:fileName package:package];
    }
}

-(void) play {
    [audioPlayer play];
}

-(void) stop {
    [audioPlayer stop];
}


#pragma -mark AudioPlayer

static int lvNewAudioPlayer (lv_State *L) {
    if( lv_gettop(L)>=1 ) {
        LVAudioPlayer* player = [[LVAudioPlayer alloc] init:L];
        LView* lview = (__bridge LView *)(L->lView);
        NSString* fileName = lv_paramString(L, 1);
        [player setPlayFileName:fileName package:lview.package];
        
        {
            NEW_USERDATA(userData, LVUserDataAudioPlayer);
            userData->player = CFBridgingRetain(player);
            player.userData = userData;
            
            lvL_getmetatable(L, META_TABLE_AudioPlayer );
            lv_setmetatable(L, -2);
        }
        return 1;
    }
    return 0;
}

static int play (lv_State *L) {
    LVUserDataAudioPlayer * user = (LVUserDataAudioPlayer *)lv_touserdata(L, 1);
    
    if( user && LVIsType(user,LVUserDataAudioPlayer) ){
        LVAudioPlayer* player = (__bridge LVAudioPlayer *)(user->player);
        if( player ){
            [player play];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int stop (lv_State *L) {
    LVUserDataAudioPlayer * user = (LVUserDataAudioPlayer *)lv_touserdata(L, 1);
    if( user ){
        LVAudioPlayer* player = (__bridge LVAudioPlayer *)(user->player);
        if( player ){
            [player stop];
        }
    }
    return 0;
}

static int __gc (lv_State *L) {
    LVUserDataAudioPlayer * user = (LVUserDataAudioPlayer *)lv_touserdata(L, 1);
    releaseUserDataAudioPlayer(user);
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataAudioPlayer * user = (LVUserDataAudioPlayer *)lv_touserdata(L, 1);
    if( user ){
        LVAudioPlayer* player =  (__bridge LVAudioPlayer *)(user->player);
        NSString* s = [NSString stringWithFormat:@"LVUserDataAudioPlayer: %@", player ];
        lv_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewAudioPlayer);
        lv_setglobal(L, "AudioPlayer");
    }
    const struct lvL_reg memberFunctions [] = {
        {"play", play },
        {"stop", stop },
        
        {"__gc", __gc },
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    lv_createClassMetaTable(L, META_TABLE_AudioPlayer);
    
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end




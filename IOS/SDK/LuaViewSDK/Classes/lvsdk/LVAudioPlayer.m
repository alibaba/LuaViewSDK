/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVAudioPlayer.h"
#import <AVFoundation/AVFoundation.h>
#import "LView.h"
#import "LVHeads.h"

@interface LVAudioPlayer ()<AVAudioPlayerDelegate>

@property(nonatomic,assign) BOOL playing;
@property (nonatomic, copy) NSString *fileName;

@property (nonatomic, assign) CGFloat volume;

@end

@implementation LVAudioPlayer{
    AVAudioPlayer* audioPlayer;
}

static void releaseUserDataAudioPlayer(LVUserDataInfo* user){
    if( user && user->object ){
        LVAudioPlayer* palyer = CFBridgingRelease(user->object);
        user->object = NULL;
        if( palyer ){
            [palyer stop];
            palyer.lv_userData = NULL;
            palyer.lv_luaviewCore = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataAudioPlayer(_lv_userData);
}

-(id) init:(lua_State*) L{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(L);
        _volume = -1;
    }
    return self;
}

-(void) setPlayFileName0:(NSString*) fileName bundle:(LVBundle*) bundle{
    
    NSString* path = [bundle resourcePathWithName:fileName];
    if( path ) {
        NSURL* url = [[NSURL alloc] initWithString:path];
        NSError* error = nil;
        [self stop];
        audioPlayer = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];//使用本地URL创建
        audioPlayer.delegate = self;
        
        [self setVolume:_volume];
        
        if( error ) {
            NSLog(@"[LuaView][error]%@",error);
        }else{
            self.fileName = fileName;
        }
    }
    
    [self play];
}

-(void) setPlayFileName:(NSString*) fileName bundle:(LVBundle*) bundle{
    if( fileName ==nil )
        return;
    if( [LVUtil isExternalUrl:fileName] ){
        
        __weak typeof (self) wself = self;
        [LVUtil download:fileName callback:^(NSData *fileData) {
            NSString* suffix = [fileName componentsSeparatedByString:@"."].lastObject;
            NSData* theFileNameData = [fileName dataUsingEncoding:NSUTF8StringEncoding];
            NSString* md5Path = [LVUtil MD5HashFromData:theFileNameData];
            md5Path = [NSString stringWithFormat:@"%@.%@",md5Path,suffix];//Mp3文件一定要加后缀，否则无法播放
            if( [LVUtil saveData:fileData toFile:[LVUtil PathForCachesResource:md5Path]] ) {
                [wself setPlayFileName0:md5Path bundle:bundle];
            }
        }];
    } else {
        [self setPlayFileName0:fileName bundle:bundle];
    }
}

-(void) play {
    if (!self.playing){
        [audioPlayer play];
        [self setVolume:0.5];
        self.playing = YES;
    }
}

-(void) stop {
    [audioPlayer stop];
    self.playing = NO;
}

-(void)setVolume:(CGFloat)volume{
    if ([[[UIDevice currentDevice] systemVersion] compare:@"10.0" options:NSNumericSearch] == NSOrderedDescending){
        if (volume < 0 || volume > 1){
            return;
        }
        _volume = volume;
        [audioPlayer setVolume:volume fadeDuration:0];
    }
}

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag{
    self.playing = NO;
}

- (id) lv_nativeObject{
    return audioPlayer;
}

#pragma -mark AudioPlayer

static int lvNewAudioPlayer (lua_State *L) {
    
    Class c = [LVUtil upvalueClass:L defaultClass:[LVAudioPlayer class]];
    
    LVAudioPlayer* player = [[c alloc] init:L];
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    
    if( lua_gettop(L)>=1 ) {
        NSString* fileName = lv_paramString(L, 1);
        [player setPlayFileName:fileName bundle:lview.bundle];
    }
    
    {
        NEW_USERDATA(userData, AudioPlayer);
        userData->object = CFBridgingRetain(player);
        player.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_AudioPlayer );
        lua_setmetatable(L, -2);
    }
    return 1;
}

static int play (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    
    if( user && LVIsType(user, AudioPlayer) ){
        LVAudioPlayer* player = (__bridge LVAudioPlayer *)(user->object);
        if( player ){
            
            if( lua_gettop(L)>=2 ) {
                NSString* fileName = lv_paramString(L, 2);
                
                if (![player.fileName isEqualToString:fileName]){
                    [player stop];
                    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
                    [player setPlayFileName:fileName bundle:lview.bundle];
                }
            }
            
            [player play];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int stop (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVAudioPlayer* player = (__bridge LVAudioPlayer *)(user->object);
        if( player ){
            [player stop];
        }
    }
    return 0;
}

static int setVolume(lua_State *L){
    //设置音量接口只有在iOS10.0以上才生效
    LVUserDataInfo *user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if (user){
        LVAudioPlayer *player = (__bridge LVAudioPlayer *)(user->object);
        if (player){
            if( lua_gettop(L)>=2 ) {
                float volume = lua_tonumber(L, 2);
                
                [player setVolume:volume];
            }
        }
    }
    
    return 0;
}

static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataAudioPlayer(user);
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVAudioPlayer* player =  (__bridge LVAudioPlayer *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataAudioPlayer: %@", player ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewAudioPlayer globalName:globalName defaultName:@"AudioPlayer"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"play", play },
        {"stop", stop },
        {"setVolume", setVolume},
        // pause
        // resume
        // callback { onComplete onError }
        
        // playing
        // looping
        // pausing
        
        {"__gc", __gc },
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    lv_createClassMetaTable(L, META_TABLE_AudioPlayer);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end




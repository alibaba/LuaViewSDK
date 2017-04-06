/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVDownloader.h"
#import "LVUtil.h"
#import "LVData.h"
#import "LView.h"
#import "LVHeads.h"

@interface LVDownloader ()
@property(nonatomic,strong) NSData* data;
@property(nonatomic,strong) id strongSelf;
@end


@implementation LVDownloader


static void releaseUserDataDownloader(LVUserDataInfo* user){
    if( user && user->object ){
        LVDownloader* downloader = CFBridgingRelease(user->object);
        user->object = NULL;
        if( downloader ){
            downloader.lv_userData = nil;
            downloader.lv_luaviewCore = nil;
        }
    }
}

-(void) dealloc{
    releaseUserDataDownloader(_lv_userData);
}

-(id) lv_nativeObject{
    return nil;
}

-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.luaObjRetainKey = [[NSMutableString alloc] init];
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.strongSelf = self;
    }
    return self;
}


#pragma -mark downloader
static int lvNewDownloader (lua_State *L) {
    if( lua_gettop(L)>=2 ) {
        Class c = [LVUtil upvalueClass:L defaultClass:[LVDownloader class]];
        
        LVDownloader* downloader = [[c alloc] init:L];
        NSString* url = lv_paramString(L, 1);     // 1: url
        //NSString* fileName = lvL_paramString(L, 2);// 2: fileName
                                                   // 3: callback
        if( lua_type(L, -1) == LUA_TFUNCTION ) {
            [LVUtil registryValue:L key:downloader stack:-1];
        }
        
        {
            NEW_USERDATA(userData, Downloader);
            userData->object = CFBridgingRetain(downloader);
            downloader.lv_userData = userData;
            
            luaL_getmetatable(L, META_TABLE_Downloader );
            lua_setmetatable(L, -2);
            
            [LVUtil registryValue:L key:downloader.luaObjRetainKey stack:-1];
        }
        [LVUtil download:url callback:^(NSData *data) {
            downloader.data = data;
            [downloader performSelectorOnMainThread:@selector(didFileLoaded) withObject:nil waitUntilDone:NO];
        }];
        return 1;
    } else {
        LVError(@"downloader( Callback==nil )!!!");
    }
    return 0;
}

-(void) didFileLoaded{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ){
        if( self.data ) {
            [LVData createDataObject:L data:self.data];
        } else {
            lua_pushnil(L);
        }
        [LVUtil call:L lightUserData:self key1:nil key2:nil nargs:1];
        
        [LVUtil unregistry:L key:self.luaObjRetainKey];
        [LVUtil unregistry:L key:self];
    }
    self.strongSelf = nil;
}

 static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataDownloader(user);
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVDownloader* downloader =  (__bridge LVDownloader *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataDownloader: %@", downloader ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static int PathOfResource (lua_State *L) {
    NSString* fileName = lv_paramString(L, 1);
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    NSString* path = [lview.bundle resourcePathWithName:fileName];
    lua_pushstring(L, path.UTF8String);
    return 1;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    lv_defineGlobalFunc("PathOfResource",  PathOfResource, L);
    
    [LVUtil reg:L clas:self cfunc:lvNewDownloader globalName:globalName defaultName:@"Download"]; // __deprecated_msg("")
    const struct luaL_Reg memberFunctions [] = {
        {"__gc", __gc },
        
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Downloader);
    
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}

@end

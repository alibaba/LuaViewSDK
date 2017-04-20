/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVBitmap.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "LVData.h"
#import <Accelerate/Accelerate.h>
#import "LVNinePatchImage.h"
#import "LVHeads.h"

@interface LVBitmap ()
@property (nonatomic,strong) id functionTag;
@property (nonatomic,assign) BOOL needCallLuaFunc;
@property (nonatomic,strong) id errorInfo;

@end

@implementation LVBitmap


-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.functionTag = [[NSMutableString alloc] init];
    }
    return self;
}

-(void) callLuaDelegate:(id) obj{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_checkstack(L, 4);
        lua_pushboolean(L, obj?0:1);
        [LVUtil pushRegistryValue:L key:self.functionTag];
        lv_runFunctionWithArgs(L, 1, 0);
    }
    [LVUtil unregistry:L key:self.functionTag];
}

-(void) loadImageByUrl:(NSString*) url finished:(LVWebImageCompletionBlock) finished{
    [LVUtil download:url callback:^(NSData *data) {
        UIImage* image = [UIImage imageWithData:data];
        NSString* error = (data?nil:@"download error");
        NSError* err = nil;
        if( error ) {
            err = [NSError errorWithDomain:error code:0 userInfo:nil];
        }
        if( finished ) {
            finished(image, err, 0, [NSURL URLWithString:url] );
        }
    }];
}

-(void) setImageByName:(NSString*) imageName{
    if( imageName==nil )
        return;
    
    if( [LVUtil isExternalUrl:imageName] ){
        __weak LVBitmap* weakImage = self;
        NSString* url = imageName;
        [self loadImageByUrl:url finished:^(UIImage *image, NSError *error, int cacheType, NSURL *imageURL) {
            
            weakImage.nativeImage = image;
            
            if( weakImage.needCallLuaFunc ) {
                weakImage.errorInfo = error;
                [weakImage performSelectorOnMainThread:@selector(callLuaDelegate:) withObject:error waitUntilDone:NO];
            }
        }];
    } else {
        // local Image
        NSData* data = [self.lv_luaviewCore.bundle resourceWithName:imageName];
        self.nativeImage = [[UIImage alloc] initWithData:data];
    }
}

-(UIImage*) sprite:(CGFloat)x y:(CGFloat)y w:(CGFloat)w h:(CGFloat) h{
    CIImage* ciimage= [self.nativeImage.CIImage imageByCroppingToRect:CGRectMake(x, y, w, h)];
    return [UIImage imageWithCIImage:ciimage];
}

-(void) dealloc{
    LVUserDataInfo* userData = self.lv_userData;
    if( userData ){
        userData->object = NULL;
    }
}

#pragma -mark ImageView
static int lvNewImage(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVBitmap class]];
    
    NSString* imageName = lv_paramString(L, 1);
    
    LVBitmap* bitmap = [[c alloc] init:L];
    
    if( lua_type(L, 2) == LUA_TFUNCTION ) {
        [LVUtil registryValue:L key:bitmap.functionTag stack:2];
        bitmap.needCallLuaFunc = YES;
    } else {
        bitmap.needCallLuaFunc = NO;
    }
    
    [bitmap setImageByName:imageName];
    {
        NEW_USERDATA(userData, Bitmap);
        userData->object = CFBridgingRetain(bitmap);
        bitmap.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Bitmap );
        lua_setmetatable(L, -2);
    }
    return 1; /* new userdatum is already on the stack */
}

static int size (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVBitmap* view = (__bridge LVBitmap *)(user->object);
        if( view ){
            CGSize size = view.nativeImage.size;
            lua_pushnumber(L, size.width  );
            lua_pushnumber(L, size.height );
            return 2;
        }
    }
    return 0;
}

static int sprite (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user && lua_gettop(L)>=5 ){
        CGFloat x = lua_tonumber(L, 2);
        CGFloat y = lua_tonumber(L, 3);
        CGFloat w = lua_tonumber(L, 4);
        CGFloat h = lua_tonumber(L, 5);
        LVBitmap* view = (__bridge LVBitmap *)(user->object);
        if( view ){
            LVBitmap* bitmap = [[LVBitmap alloc] init:L];
            {
                NEW_USERDATA(userData, Bitmap);
                userData->object = CFBridgingRetain(bitmap);
                bitmap.lv_userData = userData;
                
                luaL_getmetatable(L, META_TABLE_Bitmap );
                lua_setmetatable(L, -2);
            }
            bitmap.nativeImage = [view sprite:x y:y w:w h:h];
            return 1;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    [LVUtil reg:L clas:self cfunc:lvNewImage globalName:globalName defaultName:@"Bitmap"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"size",  size},
        {"sprite",  sprite},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_Bitmap);
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end

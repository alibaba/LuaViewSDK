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

//加载完成回调
-(void) callLuaWhenLoadCompleted:(id) obj{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_checkstack(L, 4);
        lua_pushboolean(L, obj?0:1);
        [LVUtil pushRegistryValue:L key:self.functionTag];
        lv_runFunctionWithArgs(L, 1, 0);
    }
    [LVUtil unregistry:L key:self.functionTag];
}

// 图片裁剪回调
-(void) callLuaSpriteCompleted:(id) obj{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_checkstack(L, 4);
        lv_pushUserdata(L, self.lv_userData);
        [LVUtil pushRegistryValue:L key:self.functionTag];
        lv_runFunctionWithArgs(L, 1, 0);
    }
    [LVUtil unregistry:L key:self.functionTag];
}

// 加载
-(void) loadBitmapByUrl:(NSString*) url finished:(LVWebBitmapCompletionBlock) finished{
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

// 设置Bitmap url
-(void) setBitmapUrl:(NSString*) url{
    if( url==nil )
        return;
    
    if( [LVUtil isExternalUrl:url] ){
        __weak LVBitmap* weakBitmap = self;
        [self loadBitmapByUrl:url finished:^(UIImage *image, NSError *error, int cacheType, NSURL *imageURL) {
            
            weakBitmap.nativeImage = image;
            
            if( weakBitmap.needCallLuaFunc ) {
                weakBitmap.errorInfo = error;
                [weakBitmap performSelectorOnMainThread:@selector(callLuaWhenLoadCompleted:) withObject:error waitUntilDone:NO];
            }
        }];
    } else {
        // local Image
        NSData* data = [self.lv_luaviewCore.bundle resourceWithName:url];
        self.nativeImage = [[UIImage alloc] initWithData:data];
    }
}

// 图片切割
-(UIImage*) sprite:(CGFloat)x y:(CGFloat)y w:(CGFloat)w h:(CGFloat) h{
    return [LVUtil image:self.nativeImage croppingToRect:CGRectMake(x, y, w, h)];
}

-(void) dealloc{
    LVUserDataInfo* userData = self.lv_userData;
    if( userData ){
        userData->object = NULL;
    }
}

#pragma -mark ImageView
/*
 * lua脚本中 local bitmap = Bitmap() 对应的构造方法
 */
static int lvNewBitmap(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVBitmap class]];
    
    NSString* url = lv_paramString(L, 1);// url
    
    LVBitmap* bitmap = [[c alloc] init:L];
    
    if( lua_type(L, 2) == LUA_TFUNCTION ) {
        [LVUtil registryValue:L key:bitmap.functionTag stack:2];// 图片加载完成回调
        bitmap.needCallLuaFunc = YES;
    } else {
        bitmap.needCallLuaFunc = NO;
    }
    
    [bitmap setBitmapUrl:url];
    {
        NEW_USERDATA(userData, Bitmap);
        userData->object = CFBridgingRetain(bitmap);
        bitmap.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_Bitmap );
        lua_setmetatable(L, -2);
    }
    return 1; /* new userdatum is already on the stack */
}

// Lua的Bitmap对象size接口对应的native实现
static int size (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( user ){
        LVBitmap* view = (__bridge LVBitmap *)(user->object);
        if( view ){
            CGSize size = view.nativeImage.size;// 获取native size
            lua_pushnumber(L, size.width  );//参数压栈
            lua_pushnumber(L, size.height );//参数压栈
            return 2;// 返回两个参数
        }
    }
    return 0;
}

// 切图接口
static int sprite (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);// 获取第一个参数(self,lua的userdata, 对象自身)
    if( user && lua_gettop(L)>=5 ){
        // 获取lua传送过来的参数
        CGFloat x = lua_tonumber(L, 2);
        CGFloat y = lua_tonumber(L, 3);
        CGFloat w = lua_tonumber(L, 4);
        CGFloat h = lua_tonumber(L, 5);
        
        
        LVBitmap* view = (__bridge LVBitmap *)(user->object);
        if( view ){
            // 创建新的bitmap对象(Native实例)
            LVBitmap* bitmap = [[LVBitmap alloc] init:L];
            {
                // 创建新的Bitmap对象(lua实例)
                NEW_USERDATA(userData, Bitmap);
                userData->object = CFBridgingRetain(bitmap);
                bitmap.lv_userData = userData;
                
                luaL_getmetatable(L, META_TABLE_Bitmap );
                lua_setmetatable(L, -2);
                
                if( lua_type(L, 6) == LUA_TFUNCTION ) {
                    [LVUtil registryValue:L key:bitmap.functionTag stack:6];
                }
            }
            // 调用native图片切割对象
            bitmap.nativeImage = [view sprite:x y:y w:w h:h];
            // 出发回调
            [bitmap performSelectorOnMainThread:@selector(callLuaSpriteCompleted:) withObject:nil waitUntilDone:NO];
            return 1;
        }
    }
    return 0;
}

/*
 * luaview所有扩展类的桥接协议: 只是一个静态协议, luaview统一调用该接口加载luaview扩展的类
 */
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    // 注册构造方法: "Bitmap" 对应的C函数(lvNewBitmap) + 对应的类Class(self/LVBitmap)
    [LVUtil reg:L clas:self cfunc:lvNewBitmap globalName:globalName defaultName:@"Bitmap"];
    
    // lua中Bitmap对象对应的方法列表
    const struct luaL_Reg memberFunctions [] = {
        {"size",  size},
        {"sprite",  sprite},
        {NULL, NULL}
    };
    
    // 创建Label类的方法列表
    lv_createClassMetaTable(L, META_TABLE_Bitmap);
    // 注册类方法列表
    luaL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end

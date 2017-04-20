/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVImage.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "LVData.h"
#import <Accelerate/Accelerate.h>
#import "LVNinePatchImage.h"
#import "LVHeads.h"
#import "LVBitmap.h"

@interface LVImage ()
@property (nonatomic,strong) id functionTag;
@property (nonatomic,assign) BOOL needCallLuaFunc;
@property (nonatomic,strong) id errorInfo;
@property (nonatomic,strong) UITapGestureRecognizer* tapGesture;
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVImage


-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.functionTag = [[NSMutableString alloc] init];
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        self.lv_isCallbackAddClickGesture = YES;
        self.disableAnimate = self.lv_luaviewCore.disableAnimate;
    }
    return self;
}

-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished{
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

-(void) setImageByName:(NSString*) imageName{
    if( imageName==nil )
        return;
    
    if( [LVUtil isExternalUrl:imageName] ){
        //CDN image
        __weak LVImage* weakImageView = self;
        [self setWebImageUrl:[NSURL URLWithString:imageName] finished:^(id errorInfo){
            if( weakImageView.needCallLuaFunc ) {
                weakImageView.errorInfo = errorInfo;
                [weakImageView performSelectorOnMainThread:@selector(callLuaDelegate:) withObject:errorInfo waitUntilDone:NO];
            }
        }];
    } else {
        // local Image
        UIImage* image = [self.lv_luaviewCore.bundle imageWithName:imageName];
        if ( [LVNinePatchImage isNinePathImageName:imageName] ) {
            image = [LVNinePatchImage createNinePatchImage:image];
            [self setImage:image];
        } else {
            [self setImage:image];
        }
    }
}

-(void) lv_effectParallax:(CGFloat)dx dy:(CGFloat)dy{
    [self effectParallax:dx dy:dy];
}

-(void) effectParallax:(CGFloat)dx dy:(CGFloat)dy {
}

-(void) effectClick:(NSInteger)color alpha:(CGFloat)alpha {
}

-(void) setImageByData:(NSData*) data{
    if ( data ) {
        UIImage* image = [[UIImage alloc] initWithData:data];
        [self setImage:image];
    }
}

-(void) canelWebImageLoading{
    // [self cancelCurrentImageLoad]; // 取消上一次CDN加载
}
-(void) cancelImageLoadAndClearCallback:(lua_State*)L{
    [self canelWebImageLoading];
    [NSObject cancelPreviousPerformRequestsWithTarget:self]; // 取消回调脚本
    [LVUtil unregistry:L key:self.functionTag]; // 清除脚本回调
}

-(void) dealloc{
    LVUserDataInfo* userData = self.lv_userData;
    if( userData ){
        userData->object = NULL;
    }
}

#pragma -mark ImageView
static int lvNewImageView(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVImage class]];
    
    NSString* imageName = lv_paramString(L, 1);
    
    LVImage* imageView = [[c alloc] init:L];
    [imageView setImageByName:imageName];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(imageView);
        imageView.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_UIImageView );
        lua_setmetatable(L, -2);
    }
    LuaViewCore* view = LV_LUASTATE_VIEW(L);
    if( view ){
        [view containerAddSubview:imageView];
    }
    return 1; /* new userdatum is already on the stack */
}

static int setImage (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView cancelImageLoadAndClearCallback:L];
            if( lua_type(L, 3) == LUA_TFUNCTION ) {
                [LVUtil registryValue:L key:imageView.functionTag stack:3];
                imageView.needCallLuaFunc = YES;
            } else {
                imageView.needCallLuaFunc = NO;
            }
            if ( lua_type(L, 2)==LUA_TSTRING ) {
                NSString* imageName = lv_paramString(L, 2);// 2
                if( imageName ){
                    [imageView setImageByName:imageName];
                    lua_pushvalue(L,1);
                    return 1;
                }
            } else if ( lua_type(L, 2)==LUA_TUSERDATA ) {
                LVUserDataInfo * userdata = (LVUserDataInfo *)lua_touserdata(L, 2);
                if( LVIsType(userdata, Data) ) {
                    LVData* lvdata = (__bridge LVData *)(userdata->object);
                    [imageView setImageByData:lvdata.data];
                    lua_pushvalue(L,1);
                    return 1;
                } else if( LVIsType(userdata, Bitmap) ) {
                    LVBitmap* bitmap = (__bridge LVBitmap *)(userdata->object);
                    [imageView setImage:bitmap.nativeImage];
                    lua_pushvalue(L,1);
                    return 1;
                }
            } else {
                // 清理图片
                imageView.image = nil;
            }
        }
    }
    return 0;
}

static int scaleType (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            if( lua_gettop(L)>=2 ) {
                int model = lua_tonumber(L, 2);// 2
                [imageView setContentMode:model];
                return 0;
            } else {
                UIViewContentMode model = imageView.contentMode;
                lua_pushnumber(L, model);
                return 1;
            }
        }
    }
    return 0;
}

static int startAnimating (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( L && user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            NSArray* urlArray = lv_luaTableToArray(L,2);
            float repeatCount = 1;
            float duration = 0.3;
            if( lua_gettop(L)>=3 ){
                duration = lua_tonumber(L, 3);
            }
            if( lua_gettop(L)>=4 ){
                repeatCount = lua_tonumber(L, 4);
            }
            LuaViewCore* lview = LV_LUASTATE_VIEW(L);
            LVBundle* bundle = lview.bundle;
            NSMutableArray  *arrayM=[NSMutableArray array];
            for (NSString* url in urlArray) {
                UIImage* image = [bundle imageWithName:url];
                if( image ) {
                    [arrayM addObject:image];
                }
            }
            [imageView setAnimationImages:arrayM];//设置动画数组
            [imageView setAnimationDuration:duration];//设置动画播放时间
            [imageView setAnimationRepeatCount:repeatCount];//设置动画播放次数
            [imageView startAnimating];//开始动画
        }
    }
    return 0;
}

static int stopAnimating (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView stopAnimating];
            return 0;
        }
    }
    return 0;
}

static int isAnimating (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            lua_pushboolean(L, imageView.isAnimating?1:0);
            return 1;
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int disableAnimate (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            BOOL disableAnimate = lua_toboolean(L, 2);
            imageView.disableAnimate = disableAnimate;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    [LVUtil reg:L clas:self cfunc:lvNewImageView globalName:globalName defaultName:@"Image"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"image",  setImage},
        {"scaleType",  scaleType},
        
        {"startAnimationImages",  startAnimating},//__deprecated_msg("")
        {"stopAnimationImages",  stopAnimating},//__deprecated_msg("")
        {"isAnimationImages",  isAnimating},//__deprecated_msg("")
        
        {"disableAnimate",  disableAnimate},//__deprecated_msg("")
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIImageView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


//----------------------------------------------------------------------------------------
-(NSString*) description{
    return [NSString stringWithFormat:@"<Image(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

//
//  LVImageView.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVImage.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "LVData.h"
#import <Accelerate/Accelerate.h>
#import "LVNinePatchImage.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVImage ()
@property (nonatomic,strong) id functionTag;
@property (nonatomic,strong) UIImageView* blurImageView;
@property (nonatomic,strong) UIVisualEffectView *blurEffectView;
@property (nonatomic,assign) BOOL needCallLuaFunc;
@property (nonatomic,strong) UITapGestureRecognizer* tapGesture;
@property (nonatomic,strong) id errorInfo;
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVImage


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.functionTag = [[NSMutableString alloc] init];
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        self.lv_isCallbackAddClickGesture = YES;
    }
    return self;
}

-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished{
}

-(void) callLuaDelegate:(id) obj{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        lv_checkstack(L, 4);
        lv_pushboolean(L, obj?0:1);
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
        UIImage* image = [self.lv_lview.bundle imageWithName:imageName];
        if ( [LVNinePatchImage isNinePathImageName:imageName] ) {
            image = [LVNinePatchImage createNinePatchImage:image];
            [self setImage:image];
        } else {
            [self setImage:image];
        }
    }
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
-(void) cancelImageLoadAndClearCallback:(lv_State*)L{
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
static int lvNewImageView(lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVImage class]];
    
    NSString* imageName = lv_paramString(L, 1);
    
    LVImage* imageView = [[c alloc] init:L];
    [imageView setImageByName:imageName];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(imageView);
        imageView.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UIImageView );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:imageView];
    }
    return 1; /* new userdatum is already on the stack */
}

static int setImage (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView cancelImageLoadAndClearCallback:L];
            if( lv_type(L, 3) == LV_TFUNCTION ) {
                [LVUtil registryValue:L key:imageView.functionTag stack:3];
                imageView.needCallLuaFunc = YES;
            } else {
                imageView.needCallLuaFunc = NO;
            }
            if ( lv_type(L, 2)==LV_TSTRING ) {
                NSString* imageName = lv_paramString(L, 2);// 2
                if( imageName ){
                    [imageView setImageByName:imageName];
                    lv_pushvalue(L,1);
                    return 1;
                }
            } else if ( lv_type(L, 2)==LV_TUSERDATA ) {
                LVUserDataInfo * userdata = (LVUserDataInfo *)lv_touserdata(L, 2);
                LVData* lvdata = (__bridge LVData *)(userdata->object);
                if( LVIsType(userdata, Data) ) {
                    [imageView setImageByData:lvdata.data];
                    lv_pushvalue(L,1);
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

static int scaleType (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            if( lv_gettop(L)>=2 ) {
                int model = lv_tonumber(L, 2);// 2
                [imageView setContentMode:model];
                return 0;
            } else {
                UIViewContentMode model = imageView.contentMode;
                lv_pushnumber(L, model);
                return 1;
            }
        }
    }
    return 0;
}

static int startAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( L && user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            NSArray* urlArray = lv_luaTableToArray(L,2);
            float repeatCount = 1;
            float duration = 0.3;
            if( lv_gettop(L)>=3 ){
                duration = lv_tonumber(L, 3);
            }
            if( lv_gettop(L)>=4 ){
                repeatCount = lv_tonumber(L, 4);
            }
            LView* lview = (__bridge LView *)(L->lView);
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

static int stopAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView stopAnimating];
            return 0;
        }
    }
    return 0;
}

static int isAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            lv_pushboolean(L, imageView.isAnimating?1:0);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}


+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewImageView globalName:globalName defaultName:@"Image"];
    
    const struct lvL_reg memberFunctions [] = {
        {"image",  setImage},
        {"scaleType",  scaleType},
        
        {"startAnimationImages",  startAnimating},
        {"stopAnimationImages",  stopAnimating},
        {"isAnimationImages",  isAnimating},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIImageView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


//----------------------------------------------------------------------------------------
-(NSString*) description{
    return [NSString stringWithFormat:@"<Image(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

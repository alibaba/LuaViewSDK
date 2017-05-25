/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LView.h"


typedef void(^LVWebBitmapCompletionBlock)(UIImage *image, NSError *error, int cacheType, NSURL *imageURL);


@interface LVBitmap : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property (nonatomic,strong) UIImage* nativeImage;// Bitmap实体对象

-(id) init:(lua_State*) l;

-(void) loadBitmapByUrl:(NSString*) url finished:(LVWebBitmapCompletionBlock) finished;

/*
 * luaview所有扩展类的桥接协议: 只是一个静态协议, luaview统一调用该接口加载luaview扩展的类
 */
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

@end

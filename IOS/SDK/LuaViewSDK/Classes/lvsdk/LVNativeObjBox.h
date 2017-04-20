/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "LVMethod.h"


//LVData
@interface LVNativeObjBox : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,assign) BOOL weakMode;
@property(nonatomic,strong) id realObject;
@property(nonatomic,weak)   id realObjectWeak;
@property(nonatomic,assign) BOOL openAllMethod;

-(id) init:(lua_State*) l  nativeObject:(id)nativeObject;
-(void) addMethod:(LVMethod*) method;
-(int) performMethod:(NSString*) methodName L:(lua_State*)L;

- (NSString *)className;
- (BOOL)isOCClass;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

/*
 * 注册native对象到脚本中, sel可以为空(如果为空注册所有api)
 */
+(int) registeObjectWithL:(lua_State *)L  nativeObject:(id) nativeObject name:(NSString*) luaName sel:(SEL) sel weakMode:(BOOL) weakMode;

/*
 * 清除脚本中注册的native对象
 */
+(int) unregisteObjectWithL:(lua_State *)L name:(NSString*) name;


@end

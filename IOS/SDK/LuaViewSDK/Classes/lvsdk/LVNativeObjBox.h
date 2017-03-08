//
//  LVNativeClass.h
//  LVSDK
//
//  Created by dongxicheng on 4/23/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"
#import "LVMethod.h"


//LVData
@interface LVNativeObjBox : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_lview;
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

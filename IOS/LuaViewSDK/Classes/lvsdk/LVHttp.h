//
//  LVHttp.h
//  LVSDK
//
//  Created by dongxicheng on 2/2/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"


@interface LVHttp : NSObject<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

-(id) init:(lv_State*) l;

+(int) classDefine:(lv_State *)L ;

/*
 * https检查是否是信任的域名, 改方法 可以被覆盖
 */
+(BOOL) isTrustedHost:(NSString*) host;

@end

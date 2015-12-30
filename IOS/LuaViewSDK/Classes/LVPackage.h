//
//  LVPackage.h
//  LuaViewSDK
//
//  Created by dongxicheng on 12/30/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LVPackage : NSObject

@property (nonatomic,copy) NSString* packageName;
@property (nonatomic,copy) NSArray* bundleSearchPath;

@end

//
//  LVPackage.m
//  LuaViewSDK
//
//  Created by dongxicheng on 12/30/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVPackage.h"

@implementation LVPackage

- (id) initWithPackageName:(NSString*) packageName{
    self = [super init];
    if( self ) {
        self.packageName = packageName;
    }
    return self;
}

@end

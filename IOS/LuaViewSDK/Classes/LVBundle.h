//
//  LVPackage.h
//  LuaViewSDK
//
//  Created by dongxicheng on 12/30/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "lV.h"

@class LVRSA;

@interface LVBundle : NSObject

@property (nonatomic, readonly) NSArray* scriptPaths;
@property (nonatomic, readonly) NSArray* resourcePaths;

@property (nonatomic, readonly) NSString *currentPath;

- (void)changeCurrentPath:(NSString *)path;

/**
 * 可以是绝对路径也可以是相对bundle根目录的相对路径
 */
- (void)addResourcePath:(NSString *)path;
- (void)removeResourcePath:(NSString *)path;

- (void)addScriptPath:(NSString *)path;
- (void)removeScriptPath:(NSString *)path;

- (NSString *)resourcePathWithName:(NSString *)name;

- (NSData *)resourceWithName:(NSString *)name;
- (UIImage *)imageWithName:(NSString *)name;

- (NSString *)scriptPathWithName:(NSString *)name;

- (NSData *)scriptWithName:(NSString *)name;
- (NSData *)signedScriptWithName:(NSString *)name rsa:(LVRSA *)rsa;

@end

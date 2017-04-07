/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LVHeads.h"

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

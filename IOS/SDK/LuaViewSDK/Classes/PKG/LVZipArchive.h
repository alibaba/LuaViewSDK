/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

@interface LVZipEntry : NSObject

@property(nonatomic, readonly) NSString *fileName;

@property(nonatomic, readonly, getter=isSymlink) BOOL symlink;
@property(nonatomic, readonly, getter=isDirectory) BOOL directory;
@property(nonatomic, readonly) NSInteger permissions;
@property(nonatomic, readonly) NSDate *lastModDate;

@property(nonatomic, readonly) NSData *data;
@property(nonatomic, readonly) NSData *inflatedData;

@end

@interface LVZipArchive : NSObject

@property(nonatomic, readonly) NSData *data;
@property(nonatomic, readonly) NSArray<LVZipEntry *> *entries;

+ (LVZipArchive *)archiveWithData:(NSData *)data;
+ (BOOL)unzipData:(NSData *)data toDirectory:(NSString *)path;

- (BOOL)unzipToDirectory:(NSString *)path;

- (NSDate *)lastModDate; //修改日期

-(NSString*) timeIntervalStr; //时间戳

@end

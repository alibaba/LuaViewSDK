//
//  LVZipArchive.h
//  LuaViewSDK
//
//  Created by lamo on 16/3/14.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

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

@end

/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVBundle.h"
#import "LVPkgManager.h"
#import "LVUtil.h"
#import "LVHeads.h"
#import "LView.h"

@interface LVBundle () {
    NSMutableArray *_scriptPaths;
    NSMutableArray *_resourcePaths;
    NSFileManager *_fileManager;
    NSString *_currentPath;
}

@property(nonatomic,strong) NSMutableDictionary* imageCaches;

@end

@implementation LVBundle

@dynamic resourcePaths, scriptPaths;
@dynamic currentPath;

- (id)init {
    self = [super init];
    if(self) {
        _fileManager = [NSFileManager defaultManager];
        _currentPath = [[NSBundle mainBundle] resourcePath];
        
        _resourcePaths = [NSMutableArray arrayWithObjects:[LVUtil PathForCachesResource:nil], @".", nil];
        _scriptPaths = [NSMutableArray arrayWithObject:@"."];
        _imageCaches = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (NSString *)currentPath {
    return _currentPath;
}

- (void)changeCurrentPath:(NSString *)path {
    NSAssert(path, @"current path could not be nil");
    BOOL isDir = NO;
    if (![_fileManager fileExistsAtPath:path isDirectory:&isDir] || !isDir) {
        NSAssert(NO, @"%@ not exists or is not a directory", path);
        return;
    }

    _currentPath = path;
}

- (NSArray *)resourcePaths {
    return [_resourcePaths copy];
}

- (void)addResourcePath:(NSString *)path {
    NSAssert(path, @"path could not be nil");
    
    [_resourcePaths insertObject:path atIndex:0];
}

- (void)removeResourcePath:(NSString *)path {
    NSAssert(path, @"path could not be nil");
    
    [_resourcePaths removeObject:path];
}

- (NSArray *)scriptPaths {
    return [_scriptPaths copy];
}

- (void)addScriptPath:(NSString *)path {
    NSAssert(path, @"path could not be nil");
    
    [_scriptPaths insertObject:path atIndex:0];
}

- (void)removeScriptPath:(NSString *)path {
    NSAssert(path, @"path could not be nil");

    [_scriptPaths removeObject:path];
}

- (NSString *)absolutePath:(NSString *)path {
    if (path == nil) {
        return nil;
    } else if ([path hasPrefix:@"/"]) {
        return path;
    } else {
        return [_currentPath stringByAppendingPathComponent:path];
    }
}

- (NSString *)resourcePathWithName:(NSString *)name {
    if (name == nil) {
        return nil;
    }
    
    NSString *fullPath = nil;
    for (NSString *dir in _resourcePaths) {
        fullPath = [self absolutePath:[dir stringByAppendingPathComponent:name]];
        if ([_fileManager fileExistsAtPath:fullPath]) {
            return fullPath;
        }
    }
    
    return nil;
}

- (NSData *)resourceWithName:(NSString *)name {
    if (name == nil) {
        return nil;
    }
    
    NSString *path = [self resourcePathWithName:name];
    if (path == nil) {
        return nil;
    }
    
    return [_fileManager contentsAtPath:path];
}

- (UIImage *)imageWithName:(NSString *)name {
    if (name == nil) {
        return nil;
    }
    UIImage* image = self.imageCaches[name];
    if( image ) {
        return image;
    }
    NSString *path = [self resourcePathWithName:name];
    if( path ) {
        image = [UIImage imageWithContentsOfFile:path];
    } else {
        image = [UIImage imageNamed:name];
    }
    if( image ){
        self.imageCaches[name] = image;
    }
    return image;
}

- (NSString *)scriptPathWithName:(NSString *)name {
    if (name == nil) {
        return nil;
    }
    
    NSString *ext = [name pathExtension];
    if (ext == nil ||
        !([LVScriptExts[0] isEqualToString:ext] || [LVScriptExts[1] isEqualToString:ext])) {
        
        NSAssert(nil, @"LuaView: %@ file is not supported!", ext);
        return nil;
    }
    
    NSString *fullPath = nil;
    for (NSString *dir in _scriptPaths) {
        fullPath = [self absolutePath:[dir stringByAppendingPathComponent:name]];
        if ([_fileManager fileExistsAtPath:fullPath]) {
            return fullPath;
        }
    }
    
    return name;
}

- (NSData *)scriptWithName:(NSString *)name {
    if (name == nil) {
        return nil;
    }
    NSAssert([[name pathExtension] isEqualToString:LVScriptExts[!LVSignedScriptExtIndex]],
             @"%@ is not normal script", name);
    
    NSString *fullPath = [self scriptPathWithName:name];
    if (fullPath == nil) {
        return nil;
    }
    
    return [LVUtil dataReadFromFile:fullPath];
}

- (NSData *)signedScriptWithName:(NSString *)name rsa:(LVRSA *)rsa {
    if (name == nil) {
        return nil;
    }
    
    NSAssert([[name pathExtension] isEqualToString:LVScriptExts[LVSignedScriptExtIndex]],
             @"%@ is not signed script", name);
    
    NSString *fullPath = [self scriptPathWithName:name];
    if (fullPath == nil) {
        return nil;
    }
    
    return [LVPkgManager readLuaFile:fullPath rsa:rsa];
}

@end

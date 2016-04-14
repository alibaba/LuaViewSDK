//
//  LVPkg.m
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVPkgManager.h"
#import "LVUtil.h"
#import "LVRSA.h"
#import "LVZipArchive.h"
#import "zlib.h"

NSString * const LV_PACKAGE_TIME_FILE_NAME = @"___time___";
NSString * const LV_LOCAL_PACKAGE_TIME_FILE_NAME = @"___time__local__";

@implementation LVPkgManager

+ (NSString *)rootDirectoryOfPackage:(NSString *)packageName {
    NSString *path = [NSString stringWithFormat:@"%@/%@",LUAVIEW_ROOT_PATH, packageName];
    return [LVUtil PathForCachesResource:path];
}

+ (NSString *)pathForFileName:(NSString *)fileName package:(NSString *)packageName {
    return [[self rootDirectoryOfPackage:packageName] stringByAppendingPathComponent:fileName];
}

+(BOOL) writeFile:(NSData*)data packageName:(NSString*)packageName fileName:(NSString*)fileName {
    NSString *path = [self pathForFileName:fileName package:packageName];
    
    if(  [LVUtil saveData:data toFile:path] ){
        LVLog  (@"writeFile: %@, %d", fileName, (int)data.length);
        return YES;
    } else {
        LVError(@"writeFile: %@, %d", fileName, (int)data.length);
        return NO;
    }
}

+(NSString*) timefileNameOfPackage:(NSString *)packageName {
    return [self pathForFileName:LV_PACKAGE_TIME_FILE_NAME package:packageName];
}

+(BOOL) wirteTimeForPackage:(NSString*)packageName time:(NSString*) time{
    // time file
    NSData* timeBytes = [time dataUsingEncoding:NSUTF8StringEncoding];
    return [LVPkgManager writeFile:timeBytes
                       packageName:packageName
                          fileName:LV_PACKAGE_TIME_FILE_NAME];
}


+(NSString*) timeOfPackage:(NSString*)packageName{
    NSString* fileName = [LVPkgManager timefileNameOfPackage:packageName];
    NSData* data = [LVUtil dataReadFromFile:fileName];
    if( data ) {
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } else {
        return nil;
    }
}

+(BOOL) deleteFileOfTimePackage:(NSString*) packageName{
    NSString* fileName = [LVPkgManager timefileNameOfPackage:packageName];
    return [LVUtil deleteFile:fileName];
}

+(NSString*) timefileNameOfLocalPackage:(NSString *)packageName {
    return [self pathForFileName:LV_LOCAL_PACKAGE_TIME_FILE_NAME package:packageName];
}

+(BOOL) wirteTimeForLocalPackage:(NSString*)packageName time:(NSString*) time{
    // time file
    NSData* timeBytes = [time dataUsingEncoding:NSUTF8StringEncoding];
    return [LVPkgManager writeFile:timeBytes
                       packageName:packageName
                          fileName:LV_LOCAL_PACKAGE_TIME_FILE_NAME];
}

+(NSString*) timeOfLocalPackage:(NSString*)packageName {
    NSString* fileName = [LVPkgManager timefileNameOfLocalPackage:packageName];
    NSData* data = [LVUtil dataReadFromFile:fileName];
    if( data ) {
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } else {
        return nil;
    }
}

+(BOOL) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName{
    return [LVPkgManager unpackageFile:fileName packageName:packageName checkTime:NO];
}

+(BOOL) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName checkTime:(BOOL) checkTime{
    NSString *path = [LVUtil PathForBundle:nil relativePath:fileName];
    
    if( [LVUtil exist:path] ){
        NSData* pkgData = [LVUtil dataReadFromFile:path];
        return [LVPkgManager unpackageData:pkgData packageName:packageName checkTime:checkTime localMode:YES];
    }
    return NO;
}

+(BOOL) unpackageData:(NSData*) data packageName:(NSString*) packageName  localMode:(BOOL) localMode{
    return [LVPkgManager unpackageData:data packageName:packageName checkTime:NO localMode:localMode];
}

+(BOOL) unpackageData:(NSData*) pkgData packageName:(NSString*) packageName  checkTime:(BOOL) checkTime localMode:(BOOL) localMode{
    NSString *path = [self rootDirectoryOfPackage:packageName];
    if( pkgData && [LVUtil createPath:path] ){
        LVZipArchive *archive = [LVZipArchive archiveWithData:pkgData];
        
        if( checkTime ){
            NSDate *date = [archive.entries.firstObject lastModDate];
            if (date != nil) {
                long long ms = (long long)([date timeIntervalSince1970] * 1000);
                NSString *timeStr = [NSString stringWithFormat:@"%lld", ms];
                
                NSString* oldTime = nil;
                if( localMode ) {
                    oldTime = [LVPkgManager timeOfLocalPackage:packageName];
                } else {
                    oldTime = [LVPkgManager timeOfPackage:packageName];
                }
                
                if( timeStr.length>0 && oldTime.length>0 && [timeStr isEqualToString:oldTime] ){
                    LVLog(@" Not Need unpackage, %@, %@",packageName,timeStr);
                    return NO;
                } else {
                    if( localMode ) {
                        [LVPkgManager wirteTimeForLocalPackage:packageName time:timeStr];
                    }
                }
            }
        }
        
        BOOL result = [archive unzipToDirectory:path];
        return result;
    }
    return NO;
}

+(NSString*) signfileNameOfOriginFile:(NSString*) fileName{
    return [NSString stringWithFormat:@"%@.sign",fileName];
}

+(NSString*) safe_string:(NSDictionary*) dic forKey:(NSString*) key{
    if( [dic isKindOfClass:[NSDictionary class]] && key ) {
        NSString* s = dic[key];
        if( [s isKindOfClass:[NSString class]] ) {
            return s;
        }
    }
    return nil;
}

+(int) compareLocalInfoOfPackage:(NSString*)packageName withServerInfo:(NSDictionary*) info{
    NSDictionary* dic = info;
    if( dic ){
        NSString* time1 = [LVPkgManager timeOfPackage:packageName];
        NSString* time2 = [LVPkgManager safe_string:dic forKey:LV_PKGINFO_PROPERTY_TIME];
        if( time1 && time2 &&
           [time1 isKindOfClass:[NSString class]] && [time2 isKindOfClass:[NSString class]] &&
           [time1 isEqualToString:time2] ){
            return 0;
        }
    }
    return -1;
}

+(NSInteger) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info {
    return [self downLoadPackage:packageName withInfo:info callback:nil];
}

+(NSInteger) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback{
    if( info ) {
        if( [LVPkgManager compareLocalInfoOfPackage:packageName withServerInfo:info] ){
            [LVPkgManager doDownLoadPackage:packageName withInfo:info callback:callback];
            return 1;
        }
        return 0;
    }
    return -1;
}

+(BOOL) sha256Check:(NSData*) data ret:(NSString*) string{
    NSData* temp = lv_SHA256HashBytes(data);
    const unsigned char* bytes = temp.bytes;
    NSMutableString* buffer = [[NSMutableString alloc] init];
    for( int i=0; i<temp.length; i++ ) {
        int temp = bytes[i];
        [buffer appendFormat:@"%02x",temp];
    }
    if( [string isEqualToString:buffer] ) {
        return YES;
    }
    return NO;
}

+(void) doDownLoadPackage:(NSString*)pkgName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback{
    NSString* url  = [LVPkgManager safe_string:info forKey:LV_PKGINFO_PROPERTY_URL];
    NSString* time = [LVPkgManager safe_string:info forKey:LV_PKGINFO_PROPERTY_TIME];
    NSString* sha256 = [LVPkgManager safe_string:info forKey:LV_PKGINFO_SHA256];// SHA256完整性验证
    
    if ( callback == nil ){// 回调一定不是空
        callback = ^(NSDictionary* dic, NSString* erro ){
        };
    }
    
    if( pkgName.length>0 && url.length>0 && time.length>0){
        [LVUtil download:url callback:^(NSData *data) {
            // 解包过程放在主线程执行!!!!
            dispatch_async(dispatch_get_main_queue(), ^{
                if( data ){
                    BOOL sha256Check = [LVPkgManager sha256Check:data ret:sha256];
                    if( sha256Check ){
                        [LVPkgManager deleteFileOfTimePackage:pkgName];// 开始解包, 删除时间戳文件
                        if ( [LVPkgManager unpackageData:data packageName:pkgName localMode:NO] ) {
                            // 解包成功
                            if(  [LVPkgManager wirteTimeForPackage:pkgName time:time] ){// 写标记成功
                                callback(info, nil);
                                return ;
                            }
                        }
                    }
                } else {
                    LVError(@"[downLoadPackage] error: url=%@",url);
                }
                callback(info, @"error");
            });
        }];
    }
}

+(NSData*) readLuaFile:(NSString*) fileName rsa:(LVRSA*)rsa{
    NSString* signfileName = [LVPkgManager signfileNameOfOriginFile:fileName];
    NSData* signData = [LVUtil dataReadFromFile:signfileName];
    NSData* encodedfileData = [LVUtil dataReadFromFile:fileName];
    // LVLog(@"%@",[[NSString alloc] initWithData:fileData encoding:NSUTF8StringEncoding]);
    if(  [rsa verifyData:encodedfileData withSignedData:signData] ){
        NSData* fileData = LV_AES256DecryptDataWithKey(encodedfileData, [rsa aesKeyBytes]);
        NSData* unzipedData = [self gzipUnpack:fileData];
        return unzipedData;
    }
    return nil;
}

+ (NSData *)gzipUnpack:(NSData*) data
{
    if ([data length] == 0) return data;
    
    NSInteger full_length = [data length];
    NSInteger half_length = [data length] / 2;
    
    NSMutableData *decompressed = [NSMutableData dataWithLength: full_length +     half_length];
    BOOL done = NO;
    int status;
    
    z_stream strm;
    strm.next_in = (Bytef *)[data bytes];
    strm.avail_in = (unsigned int)[data length];
    strm.total_out = 0;
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    
    if (inflateInit2(&strm, (15+32)) != Z_OK) return nil;
    while (!done){
        if (strm.total_out >= [decompressed length])
            [decompressed increaseLengthBy: half_length];
        strm.next_out = [decompressed mutableBytes] + strm.total_out;
        strm.avail_out = (unsigned int)( [decompressed length] - strm.total_out );
        
        // Inflate another chunk.
        status = inflate (&strm, Z_SYNC_FLUSH);
        if (status == Z_STREAM_END) done = YES;
        else if (status != Z_OK) break;
    }
    if (inflateEnd (&strm) != Z_OK) return nil;
    
    // Set real length.
    if (done){
        [decompressed setLength: strm.total_out];
        return [NSData dataWithData: decompressed];
    }
    return nil;
}

+(void) clearCachesPath {
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString* rootPath = [LVUtil PathForCachesResource:LUAVIEW_ROOT_PATH];
    if( rootPath ) {
        NSArray *contents = [fileManager contentsOfDirectoryAtPath:rootPath error:NULL];
        NSEnumerator *e = [contents objectEnumerator];
        NSString *filename;
        while ((filename = [e nextObject])) {
            if( [fileManager removeItemAtPath:[rootPath stringByAppendingPathComponent:filename] error:NULL] ) {
                LVLog  ( @"delete File: %@", filename );
            } else {
                LVError( @"delete File: %@", filename );
            }
        }
    }
}

@end

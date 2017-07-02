/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVPkgManager.h"
#import "LVUtil.h"
#import "LVRSA.h"
#import "LVZipArchive.h"
#import "zlib.h"

NSString * const LV_FILE_NAME_OF_PACKAGE_DOWNLOAD_URL = @"___download_url___";
NSString * const LV_FILE_NAME_OF_PACKAGE_TIMESTAMP = @"___timestamp___";

@implementation LVPkgManager

+ (NSString *)rootDirectoryOfPackage:(NSString *)packageName {
    NSString *path = [NSString stringWithFormat:@"%@/%@",LUAVIEW_ROOT_PATH, packageName];
    return [LVUtil PathForCachesResource:path];
}

+ (NSString *)pathForFileName:(NSString *)fileName package:(NSString *)packageName {
    return [[self rootDirectoryOfPackage:packageName] stringByAppendingPathComponent:fileName];
}

//------------------------------ write/read file for package ---------------------------------
+(BOOL) writeFile:(NSData*)data packageName:(NSString*)packageName fileName:(NSString*)fileName {
    NSString* path = [self pathForFileName:fileName package:packageName];
    if(  [LVUtil saveData:data toFile:path] ){
        LVLog  (@"writeFile: %@, %d", fileName, (int)data.length);
        return YES;
    } else {
        LVError(@"writeFile: %@, %d", fileName, (int)data.length);
        return NO;
    }
}

+(NSData*) readFileFromPackage:(NSString*)packageName fileName:(NSString*)fileName {
    NSString *path = [self pathForFileName:fileName package:packageName];
    NSData* data =[LVUtil dataReadFromFile:path];
    return data;
}

//----------------------------------- timestamp path -----------------------------------------
+(NSString*) filePathOfPackageDownloadUrl:(NSString *)packageName {
    return [self pathForFileName:LV_FILE_NAME_OF_PACKAGE_DOWNLOAD_URL package:packageName];
}
+(NSString*) filePathOfPackageTimestamp:(NSString *)packageName {
    return [self pathForFileName:LV_FILE_NAME_OF_PACKAGE_TIMESTAMP package:packageName];
}
//------------------------------------------------------------------------------------------
+(BOOL) deleteFileOfPackageDownloadUrl:(NSString*) packageName{
    NSString* path = [self filePathOfPackageDownloadUrl:packageName];
    return [LVUtil deleteFile:path];
}
+(BOOL) deleteFileOfPackageTimestamp:(NSString*) packageName{
    NSString* path = [self filePathOfPackageTimestamp:packageName];
    return [LVUtil deleteFile:path];
}
//------------------------------------ read/write package download url -----------------------------------
+(NSString*) downloadUrlOfPackage:(NSString*)packageName{
    NSData* data = [self readFileFromPackage:packageName fileName:LV_FILE_NAME_OF_PACKAGE_DOWNLOAD_URL];
    if( data ) {
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } else {
        return nil;
    }
}

+(BOOL) setPackage:(NSString*)packageName downloadUrl:(NSString*) timestamp{
    NSData* data = [timestamp dataUsingEncoding:NSUTF8StringEncoding];
    return [self writeFile:data packageName:packageName fileName:LV_FILE_NAME_OF_PACKAGE_DOWNLOAD_URL];
}
//------------------------------------ read/write package timestamp -----------------------------------
+(NSString*) timestampOfPackage:(NSString*)packageName {
    NSData* data = [self readFileFromPackage:packageName fileName:LV_FILE_NAME_OF_PACKAGE_TIMESTAMP];
    if( data ) {
        return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } else {
        return nil;
    }
}

+(BOOL) setPackage:(NSString*)packageName timestamp:(NSString*) timestamp{
    // time file
    NSData* data = [timestamp dataUsingEncoding:NSUTF8StringEncoding];
    return [self writeFile:data packageName:packageName fileName:LV_FILE_NAME_OF_PACKAGE_TIMESTAMP];
}


//------------------------------------------------------------------------------------------
+(int) unpackageFile:(NSString*) fileName packageName:(NSString*) packageName{
    NSString *path = [LVUtil PathForBundle:nil relativePath:fileName];
    
    if( [LVUtil exist:path] ){
        NSData* pkgData = [LVUtil dataReadFromFile:path];
        return [self unpackageData:pkgData packageName:packageName];
    }
    return -1;
}
// 根据时间戳，检查是否需要更新
+(BOOL) checkUpdateWithNewTS:(NSString*)newTS oldTS:(NSString*) oldTS{
    return newTS.length>0 && oldTS.length>0 && [newTS compare:oldTS]==NSOrderedDescending;
}

+(int) unpackageData:(NSData*) pkgData packageName:(NSString*) packageName{
    NSString *path = [self rootDirectoryOfPackage:packageName];
    if( pkgData && [LVUtil createPath:path] ){
        LVZipArchive *archive = [LVZipArchive archiveWithData:pkgData];
        
        NSString* newTS = [archive timeIntervalStr];
        
        NSString* oldTS = [self timestampOfPackage:packageName];
         // 首次下载 或者有 新的最新包
        if( (newTS && oldTS==nil) ||  [self checkUpdateWithNewTS:newTS oldTS:oldTS] ){
            BOOL result = [archive unzipToDirectory:path];
            if( result ) {
                [self setPackage:packageName timestamp:newTS];
                return 1;
            }
            return -1;
        } else {
            LVLog(@" Not Need unpackage, %@, %@",packageName,newTS);
            return 0;
        }
    }
    return -1;
}

+(NSString*) signfileNameOfOriginFile:(NSString*) fileName{
    return [NSString stringWithFormat:@"%@.sign",fileName];
}


+(int) compareDownloadUrlOfPackage:(NSString*)packageName withServerInfo:(NSDictionary*) info{
    LVPkgInfo* pkgInfo = [[LVPkgInfo alloc] init:info];
    NSString* url1 = [self downloadUrlOfPackage:packageName];
    NSString* url2 = pkgInfo.url;
    if( url1 && url2 &&
       [url1 isKindOfClass:[NSString class]] && [url2 isKindOfClass:[NSString class]] &&
       [url1 isEqualToString:url2] ){
        return 0;
    }
    return -1;
}

+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info {
    return [self downloadPackage:packageName withInfo:info callback:nil];
}

+(NSInteger) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback{
    if( info ) {
        if( [self compareDownloadUrlOfPackage:packageName withServerInfo:info] ){
            [self doDownloadPackage:packageName withInfo:info callback:callback];
            return LV_DOWNLOAD_NET;
        }
        
        if (callback){
            callback(info, nil, LV_DOWNLOAD_CACHE);
        }
        return LV_DOWNLOAD_CACHE;
    }
    
    if (callback){
        callback(info, nil, LV_DOWNLOAD_ERROR);
    }
    
    return LV_DOWNLOAD_ERROR;
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

+(void) doDownloadPackage:(NSString*)pkgName withInfo:(NSDictionary*) info callback:(LVDownloadCallback) callback{
    LVPkgInfo* pkgInfo = [[LVPkgInfo alloc] init:info];
    
    __weak typeof (self) weakSelf = self;
    if( pkgName.length>0 && pkgInfo.url.length>0 && pkgInfo.timestamp.length>0){
        [LVUtil download:pkgInfo.url callback:^(NSData *data) {
            // 解包过程放在主线程执行!!!!
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf unpackageData:data packageName:pkgName withInfo:pkgInfo callback:callback];
            });
        }];
    }
}

+(NSInteger) unpackageData:(NSData*)data packageName:(NSString*)pkgName withInfo:(LVPkgInfo*)pkgInfo callback:(LVDownloadCallback) callback{
    
    if( data ){
        BOOL sha256OK = [self sha256Check:data ret:pkgInfo.sha256];
        if( sha256OK ){
            [self deleteFileOfPackageDownloadUrl:pkgName];// 开始解包, 删除时间戳文件
            
            NSInteger result = [self unpackageData:data packageName:pkgName];
            if ( result >= 0 ) {
                // 解包成功
                if( [self setPackage:pkgName downloadUrl:pkgInfo.url] && callback){// 写标记成功
                    callback(pkgInfo.originalDic, nil, LV_DOWNLOAD_NET);
                }
                
                return result;
            }
        }
    } else {
        LVError(@"[downloadPackage] error: url=%@",pkgInfo.url);
    }
    
    callback(pkgInfo.originalDic, @"error", LV_DOWNLOAD_ERROR);
    
    return -1;
}

+(NSData*) readLuaFile:(NSString*) fileName rsa:(LVRSA*)rsa{
    NSString* signfileName = [self signfileNameOfOriginFile:fileName];
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

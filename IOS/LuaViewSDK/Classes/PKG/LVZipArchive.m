//
//  LVZipArchive.m
//  LuaViewSDK
//
//  Created by lamo on 16/3/14.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVZipArchive.h"
#import "LVUtil.h"
#import <zlib.h>

static const UInt32 kCDHeaderMagicNumber = 0x02014B50;
static const UInt32 kCDHeaderFixedDataLength = 46;

static const UInt32 kCDTrailerMagicNumber = 0x06054B50;

static const UInt32 kLFHeaderMagicNumber = 0x04034B50;
static const UInt32 kLFHeaderFixedDataLength = 30;

struct LVZipBuffer {
    void *bytes;
    NSInteger size;
    NSInteger offset;
};

static bool buffer_initWithData(struct LVZipBuffer *buffer, NSData *data, NSUInteger offset) {
    buffer->bytes = (void *)data.bytes;
    buffer->size = data.length;
    buffer->offset = offset;
    
    return true;
}

static UInt16 buffer_readInt16(struct LVZipBuffer *buffer) {
    UInt16 result = CFSwapInt16LittleToHost(*(UInt16 *)(buffer->bytes + buffer->offset));
    buffer->offset += 2;
    
    return result;
}

static UInt32 buffer_readInt32(struct LVZipBuffer *buffer) {
    UInt32 result = CFSwapInt32LittleToHost(*(UInt32 *)(buffer->bytes + buffer->offset));
    buffer->offset += 4;
    
    return result;
}

static void *buffer_position(struct LVZipBuffer *buffer) {
    return (void *)buffer->bytes + buffer->offset;
}

static bool buffer_skip(struct LVZipBuffer *buffer, NSInteger length) {
    NSInteger offset = buffer->offset + length;
    if (offset < 0 || offset >= buffer->size) {
        return false;
    }
    
    buffer->offset = offset;
    
    return true;
}

static bool buffer_seekInt32(struct LVZipBuffer *buffer, UInt32 value) {
    NSInteger offset = buffer->size - 4;
    
    while (offset > 0) {
        UInt32 temp = CFSwapInt32LittleToHost(*(UInt32 *)(buffer->bytes + offset));
        if (temp == value) {
            buffer->offset = offset;
            return true;
        }
        offset--;
    }
    
    return false;
}

struct LVZipTrailer {
    UInt32 magicNumber;
    UInt16 diskNumber;
    UInt16 diskNumberWithStart;
    UInt16 numberOfEntries;
    UInt16 totalNumberOfEntries;
    UInt32 sizeOfEntries;
    UInt32 startOfEntries;
    UInt16 commentLength;
    char *comment;
};

static bool trailer_initWithData(struct LVZipTrailer *trailer, NSData *data) {
    UInt32 trailerCheck = 0;
    struct LVZipBuffer buffer = {0};
    buffer_initWithData(&buffer, data, [data length] - sizeof(trailerCheck));
    if (!buffer_seekInt32(&buffer, kCDTrailerMagicNumber)) {
        return false;
    }
    if (buffer.offset < 1) {
        return false;
    }
    
    UInt32 mn = buffer_readInt32(&buffer);
    assert(mn == kCDTrailerMagicNumber);
    
    trailer->magicNumber = mn;
    trailer->diskNumber = buffer_readInt16(&buffer);
    trailer->diskNumberWithStart = buffer_readInt16(&buffer);
    trailer->numberOfEntries = buffer_readInt16(&buffer);
    trailer->totalNumberOfEntries = buffer_readInt16(&buffer);
    trailer->sizeOfEntries = buffer_readInt32(&buffer);
    trailer->startOfEntries = buffer_readInt32(&buffer);
    trailer->commentLength = buffer_readInt16(&buffer);
    if (trailer->commentLength) {
        trailer->comment = (char *)buffer_position(&buffer);
    }
    
    return true;
}

struct LVZipCDHeader {
    UInt32 magicNumber;
    UInt16 versionMadeBy;
    UInt16 versionNeededToExtract;
    UInt16 generalPurposeBitFlag;
    UInt16 compressionMethod;
    UInt32 lastModDate;
    UInt32 crc;
    UInt16 filenameLength;
    UInt16 extraFieldLength;
    UInt16 commentLength;
    UInt16 diskNumberStart;
    UInt16 internalFileAttributes;
    UInt32 externalFileAttributes;
    UInt64 localHeaderOffset;

    UInt64 compressedSize;
    UInt64 uncompressedSize;
    
    char *filename;
    void *extraField;
    char *comment;
    void *cachedData;
};

static bool cdheader_initWithData(struct LVZipCDHeader *header, void *start) {
    struct LVZipBuffer buffer = { start, NSUIntegerMax, 0 };
    
    UInt32 mn = buffer_readInt32(&buffer);
    assert(mn == kCDHeaderMagicNumber);
    
    header->magicNumber = mn;
    header->versionMadeBy = buffer_readInt16(&buffer);
    header->versionNeededToExtract = buffer_readInt16(&buffer);
    header->generalPurposeBitFlag = buffer_readInt16(&buffer);
    header->compressionMethod = buffer_readInt16(&buffer);
    header->lastModDate = buffer_readInt32(&buffer);
    header->crc = buffer_readInt32(&buffer);
    header->compressedSize = buffer_readInt32(&buffer);
    header->uncompressedSize = buffer_readInt32(&buffer);
    header->filenameLength = buffer_readInt16(&buffer);
    header->extraFieldLength = buffer_readInt16(&buffer);
    header->commentLength = buffer_readInt16(&buffer);
    header->diskNumberStart = buffer_readInt16(&buffer);
    header->internalFileAttributes = buffer_readInt16(&buffer);
    header->externalFileAttributes = buffer_readInt32(&buffer);
    header->localHeaderOffset = buffer_readInt32(&buffer);
    
    if (header->filenameLength) {
        header->filename = (char *)buffer_position(&buffer);
        buffer_skip(&buffer, header->filenameLength);
    }
    if (header->extraFieldLength) {
        header->extraField = buffer_position(&buffer);
        buffer_skip(&buffer, header->extraFieldLength);
    }
    if (header->commentLength) {
        header->comment = (char *)buffer_position(&buffer);
    }
    
    return true;
}

static NSString *cdheader_getFileName(struct LVZipCDHeader *header) {
    if (header->filenameLength == 0) {
        return nil;
    }
    
    NSData *data = [NSData dataWithBytes:header->filename length:header->filenameLength];
    NSString *name = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    return name;
}

static NSUInteger cdheader_getLength(struct LVZipCDHeader *header) {
    return kCDHeaderFixedDataLength +
        header->filenameLength +
        header->commentLength +
        header->extraFieldLength;
}

static NSInteger cdheader_getPermissions(struct LVZipCDHeader *header) {
    return (header->externalFileAttributes >> 16 & 0x1FF) ?: 0755U;
}

static NSDate *cdheader_getLastModDate(struct LVZipCDHeader *header) {
    // convert dos time
    UInt32 dtime = header->lastModDate;
    
    NSDateComponents *comps = [NSDateComponents new];
    comps.year = (int)(((dtime >> 25) & 0x7f) + 1980);
    comps.month = (int)((dtime >> 21) & 0x0f);
    comps.day = (int)((dtime >> 16) & 0x1f);
    comps.hour = (int)((dtime >> 11) & 0x1f);
    comps.minute = (int)((dtime >> 5) & 0x3f);
    comps.second = (int)((dtime << 1) & 0x3e);
    
    return [[NSCalendar currentCalendar] dateFromComponents:comps];
}

static NSInteger cdheader_getFileType(struct LVZipCDHeader *header) {
    return header->externalFileAttributes >> 29 & 0x1F;
}

static bool cdheader_isDirecotry(struct LVZipCDHeader *header) {
    return cdheader_getFileType(header) == 0x02;
}

static bool cdheader_isSymlink(struct LVZipCDHeader *header) {
    return cdheader_getFileType(header) == 0x05;
}

struct LVZipLFHeader {
    UInt32 magicNumber;
    UInt32 versionNeededToExtract;
    UInt32 generalPurposeBitFlag;
    UInt32 compressionMethod;
    UInt32 lastModDate;
    UInt32 crc;
    UInt64 compressedSize;
    UInt64 uncompressedSize;
    UInt32 filenameLength;
    UInt32 extraFieldLength;
    char *filename;
    void *extraField;
};

static bool lfheader_initWithData(struct LVZipLFHeader *header, void *start) {
    struct LVZipBuffer buffer = { start, NSUIntegerMax, 0 };

    UInt32 mn = buffer_readInt32(&buffer);
    assert(mn == kLFHeaderMagicNumber);
    
    header->magicNumber = mn;
    header->versionNeededToExtract = buffer_readInt16(&buffer);
    header->generalPurposeBitFlag = buffer_readInt16(&buffer);
    header->compressionMethod = buffer_readInt16(&buffer);
    header->lastModDate = buffer_readInt32(&buffer);
    header->crc = buffer_readInt32(&buffer);
    header->compressedSize = buffer_readInt32(&buffer);
    header->uncompressedSize = buffer_readInt32(&buffer);
    header->filenameLength = buffer_readInt16(&buffer);
    header->extraFieldLength = buffer_readInt16(&buffer);
    
    if (header->filenameLength > 0) {
        header->filename = (char *)buffer_position(&buffer);
        buffer_skip(&buffer, header->filenameLength);
    }
    if (header->extraFieldLength > 0) {
        header->extraField = (char *)buffer_position(&buffer);
    }
    
    return true;
}

static NSUInteger lfheader_getLength(struct LVZipLFHeader *header) {
    return kLFHeaderFixedDataLength +
        header->filenameLength +
        header->extraFieldLength;
}

@interface LVZipArchive ()

@property(nonatomic, strong) NSData *data;
@property(nonatomic, strong) NSArray<LVZipEntry *> *entries;

@end

@interface LVZipEntry ()

@property(nonatomic, assign) struct LVZipCDHeader internalHeader;
@property(nonatomic, strong) NSData *data;

@end

@implementation LVZipArchive

+ (LVZipArchive *)archiveWithData:(NSData *)data {
    struct LVZipTrailer trailer = {0};
    if (!trailer_initWithData(&trailer, data)) {
        return nil;
    }
    
    LVZipArchive *archive = [self new];
    archive.data = data;
    
    NSMutableArray *entries = [NSMutableArray array];
    struct LVZipCDHeader header = {0};
    LVZipEntry *entry = nil;
    unsigned long long offset = trailer.startOfEntries;
    for (NSUInteger i = 0; i < trailer.totalNumberOfEntries; i++) {
        if (cdheader_initWithData(&header, (void *)(data.bytes + offset))) {
            entry = [LVZipEntry new];
            entry.internalHeader = header;
            [archive fillContentOfEntry:entry];
            [entries addObject:entry];
            
            offset += cdheader_getLength(&header);
        } else {
            break;
        }
    }
    
    archive.entries = entries;
    
    return archive;
}

+ (BOOL)unzipData:(NSData *)data toDirectory:(NSString *)path {
    LVZipArchive *archive = [self archiveWithData:data];
    return [archive unzipToDirectory:path];
}

- (BOOL)unzipToDirectory:(NSString *)path {
    if (path == nil) {
        return NO;
    }
    
    NSFileManager *fm = [NSFileManager defaultManager];
    if (![fm createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:NULL]) {
        LVLog(@"create directory(%@) error!", path);
        return NO;
    }
    
    NSDictionary *attrs = nil;
    NSData *inflatedData = nil;
    NSString *fileName = nil, *fullPath = nil;
    NSError *error = nil;
    for (LVZipEntry *entry in self.entries) {
        error = nil;
        attrs = [self attributes:entry];
        inflatedData = entry.inflatedData;
        fileName = entry.fileName;
        fullPath = [path stringByAppendingPathComponent:fileName];
        
        if ([entry isDirectory]) {
            BOOL r = [fm createDirectoryAtPath:fullPath
                   withIntermediateDirectories:YES
                                    attributes:attrs
                                         error:&error];
            if (!r) {
                LVLog(@"create directory(%@) entry error:", fileName, error);
                return NO;
            }
        } else if ([entry isSymlink]) {
            NSString *dest = [[NSString alloc] initWithData:inflatedData
                                                   encoding:NSUTF8StringEncoding];
            BOOL r = [fm createSymbolicLinkAtPath:fullPath
                              withDestinationPath:dest
                                            error:&error];
            if (!r) {
                LVLog(@"create symlink(%@) entry error:", fileName, error);
                return NO;
            }
            [fm setAttributes:attrs ofItemAtPath:fullPath error:NULL];
        } else {
            NSString *dir = [fileName stringByDeletingLastPathComponent];
            if ([dir length] > 0) {
                NSString *fullDirPath = [path stringByAppendingPathComponent:dir];
                BOOL isDir = NO, r = NO;
                if (![fm fileExistsAtPath:fullDirPath isDirectory:&isDir]) {
                    r = [fm createDirectoryAtPath:fullDirPath
                      withIntermediateDirectories:YES
                                       attributes:nil
                                            error:&error];
                    if (!r) {
                        LVLog(@"create file(%@)'s parent directory error:%@", fileName, error);
                        return NO;
                    }
                } else if (!isDir) {
                    r = [fm removeItemAtPath:fullDirPath error:&error];
                    if (!r) {
                        LVLog(@"remove exist file(%@) error:%@", dir, error);
                        return NO;
                    }
                    r = [fm createDirectoryAtPath:fullDirPath
                      withIntermediateDirectories:YES
                                       attributes:nil
                                            error:NULL];
                    if (!r) {
                        LVLog(@"create file(%@)'s parent directory error:%@", fileName, error);
                        return NO;
                    }
                }
            }
            
            BOOL r = [fm createFileAtPath:fullPath contents:inflatedData attributes:attrs];
            if (!r) {
                LVLog(@"create file(%@) entry error", fileName);
                return NO;
            }
        }
    }
    
    return YES;
}

- (void)fillContentOfEntry:(LVZipEntry *)entry {
    if ([entry isDirectory]) {
        return;
    }
    
    struct LVZipLFHeader lfHeader = {0};
    void *start = (void *)(self.data.bytes + entry.internalHeader.localHeaderOffset);
    if (!lfheader_initWithData(&lfHeader, start)) {
        return;
    }
    
    NSUInteger location = (NSUInteger)entry.internalHeader.localHeaderOffset + lfheader_getLength(&lfHeader);
    NSUInteger length = (NSUInteger)entry.internalHeader.compressedSize;
    entry.data = [self.data subdataWithRange:NSMakeRange(location, length)];
}

- (NSDictionary *)attributes:(LVZipEntry *)entry {
    if ([entry isDirectory] || [entry isSymlink]) {
        return nil;
    }
    
    return @{ NSFilePosixPermissions: @([entry permissions]),
              NSFileCreationDate: [entry lastModDate],
              NSFileModificationDate: [entry lastModDate] };
}

@end

@implementation LVZipEntry

@synthesize inflatedData = _inflatedData;
@dynamic fileName, symlink, directory, permissions, lastModDate;

- (NSString *)fileName {
    return cdheader_getFileName(&_internalHeader);
}

- (NSInteger)permissions {
    return cdheader_getPermissions(&_internalHeader);
}

- (BOOL)isDirectory {
    return cdheader_isDirecotry(&_internalHeader) || [[self fileName] hasSuffix:@"/"];
}

- (BOOL)isSymlink {
    return cdheader_isSymlink(&_internalHeader);
}

- (NSDate *)lastModDate {
    return cdheader_getLastModDate(&_internalHeader);
}

- (NSData *)inflatedData {
    if (_inflatedData) {
        return _inflatedData;
    }
    
    if (!_data) {
        return nil;
    }
    
    if (_internalHeader.compressionMethod == Z_NO_COMPRESSION || [self isSymlink]) {
        _inflatedData = _data;
    } else {
        _inflatedData = [self inflateData:_data];
    }
    
    return _inflatedData;
}

- (NSData *) inflateData:(NSData *)data {
    NSUInteger length = [data length];
    NSUInteger halfLength = length / 2;
    
    NSMutableData *inflatedData = [NSMutableData dataWithLength:length + halfLength];
    BOOL done = NO;
    int status;
    
    z_stream strm;
    
    strm.next_in = (Bytef *)data.bytes;
    strm.avail_in = (unsigned int)[data length];
    strm.total_out = 0;
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    
    if (inflateInit2(&strm, -MAX_WBITS) != Z_OK) return nil;
    while (!done) {
        if (strm.total_out >= [inflatedData length])
            [inflatedData increaseLengthBy:halfLength];
        strm.next_out = [inflatedData mutableBytes] + strm.total_out;
        strm.avail_out = (unsigned int)([inflatedData length] - strm.total_out);
        status = inflate(&strm, Z_SYNC_FLUSH);
        if (status == Z_STREAM_END) done = YES;
        else if (status != Z_OK) break;
    }
    if (inflateEnd(&strm) == Z_OK && done)
        [inflatedData setLength:strm.total_out];
    else
        inflatedData = nil;
    return inflatedData;
}

@end
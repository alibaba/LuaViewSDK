//
//  LVRSA.m
//  LVSDK
//
//  Created by dongxicheng on 4/28/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVRSA.h"
#import <Security/Security.h>
#import <Security/Security.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCryptor.h>
#import <Security/SecItem.h>
#import "LVUtil.h"

@implementation LVRSA

NSError* lv_verifySignSHA1WithRSA(NSData *fileData, SecKeyRef pubKeyRef, NSData *signedData);

-(void) test{
    {
        const char encryptBytes[] = { 0xf6,0x5f,0x56,0x30,0x2a,0x9d,0x59,0x3f,0xa8,0x11,0x4,0x31,0xd9,0xbd,0xca,0xe0};
        NSData* data = [[NSData alloc] initWithBytes:encryptBytes length:sizeof(encryptBytes)];
        NSData* newData = LV_AES256DecryptDataWithKey(data, [LVRSA aesKeyBytes]);
        LVLog(@"%@",newData);
    }
    
    [LVRSA aesKeyBytes];
    const char data[] = { 0xcd,0x5e,0xb1,0xd3,0x6b,0x90,0x3e,0x1e,0x54,0x76,0xe2,0xae,0xc4,0xdf,0xdb,0x42,0x1d,0x67,0xe5,0xf5};
    const char sign[] = { 0xab,0x5a,0x37,0x3e,0x47,0x84,0xfc,0x4,0xb4,0x8c,0xe9,0x9,0x8b,0xb3,0x18,0x6e,0x92,0x4,0x4a,0xf2,0xf8,0x6f,0x51,0xbb,0xb5,0xb0,0xa5,0x7b,0xa8,0x9,0x47,0x9c,0x9c,0xa0,0x56,0xd3,0x74,0x25,0x5b,0x67,0xc4,0x5a,0x76,0x5e,0xa2,0xee,0x60,0x7b,0x6f,0x85,0xb5,0x3a,0xe2,0xe0,0xa3,0x65,0xa7,0x88,0xa9,0x90,0xa8,0xd7,0xb,0x96,0x35,0xac,0x84,0xc,0x45,0x22,0x3b,0x77,0x76,0x53,0x76,0x79,0xad,0x85,0x1d,0x20,0x45,0x98,0x76,0x51,0xcb,0xb1,0xb2,0xd3,0xf7,0xf7,0xa8,0x3a,0xe2,0x1a,0xdf,0x7,0x9a,0x72,0x39,0xcc,0x2,0x27,0x22,0x55,0x7d,0x84,0x68,0xf2,0x41,0x2a,0xf9,0x37,0xe0,0x7c,0xf4,0xc6,0x86,0xe1,0x90,0xd1,0x36,0xef,0xc3,0x4e,0xca,0xd3,0x1c,0xc2,0x75,0x45,0xd5,0x67,0x58,0xf4,0xb7,0x9a,0x3c,0x2b,0x9c,0x44,0x98,0xf3,0x5,0x96,0xef,0x73,0x15,0xe4,0x32,0xb0,0x93,0x31,0xb4,0x60,0xf9,0x67,0xef,0x90,0xc2,0x40,0x44,0x42,0x0,0xe6,0x43,0x3b,0x34,0xf6,0xc,0xc1,0xfa,0x56,0x0,0xa4,0xd0,0x98,0x65,0xe,0x2b,0x1c,0xe0,0xe4,0x21,0x1e,0x7c,0x54,0x94,0x57,0x37,0xbb,0xac,0xc,0xc5,0xae,0xbb,0xa8,0x91,0xee,0x7c,0x56,0x89,0x78,0x6f,0xe8,0x8c,0x68,0xf6,0x44,0x56,0xfb,0xe2,0x68,0xad,0xc1,0x92,0xf0,0x8c,0x16,0x7d,0x66,0xe0,0x42,0x53,0x10,0x2c,0x91,0x23,0x71,0x39,0x3e,0x60,0x68,0x36,0x52,0x4d,0x3e,0xf9,0x84,0x87,0xc8,0x14,0x54,0xfe,0x80,0x46,0xdd,0xfe,0x27,0xc6,0xbc,0x7,0xb0,0xf9,0xfd,0x10,0xb6};
    
    
    NSData* signData = [[NSData alloc] initWithBytes:sign length:sizeof(sign)];
    NSData* dongxicheng = [[NSData alloc] initWithBytes:data length:sizeof(data)];
    SecKeyRef publicKey = [LVRSA getPublicKey];
    NSError* error = lv_verifySignSHA1WithRSA(dongxicheng, publicKey, signData);
    LVLog(@"%@",error);
}


+(BOOL) verifyData:(NSData*)data withSignedData:(NSData*) signedData{
    SecKeyRef key = [LVRSA getPublicKey];
    NSError* error = lv_verifySignSHA1WithRSA(data, key, signedData);
    if( error == nil ){
        return YES;
    }
    LVLog(@"%@",error);
    return NO;
}



+(NSData*) decrypt:(NSData*) data{
    SecKeyRef privateKey = [LVRSA getPrivateKeyWithPassword:@"123456"];
    NSData* data3 = [LVRSA rsaDecryptWithData:data usingKey:privateKey];
    return data3;
}

+(NSData*) aesKeyBytes{
    static NSData* g_keyBytes = nil;
    if( g_keyBytes==nil ) {
        NSData *certificateData = [LVRSA publickKeyData];
        g_keyBytes = [LVUtil MD5HashDataFromData:certificateData];
    }
    return g_keyBytes;
}

//公共秘钥的引用：
+ (SecKeyRef)getPublicKey{
    static SecKeyRef g_publicKey = NULL;
    if( g_publicKey == NULL ){
        g_publicKey = [LVRSA getPublicKey0];
    }
    return g_publicKey;
}

static NSString *g_publicKeyFilePath = nil;

+ (void) setPublicKeyFilePath:(NSString*) filePath{
    g_publicKeyFilePath = filePath;
}

+(NSData*) publickKeyData{
    if( g_publicKeyFilePath==nil )
        g_publicKeyFilePath = [[NSBundle mainBundle] pathForResource:@"public_key" ofType:@"der"];
    
    NSString* resourcePath = [[NSBundle mainBundle] resourcePath];
    NSString* longPath = [resourcePath stringByAppendingPathComponent:g_publicKeyFilePath];
    
    NSData *certificateData = [NSData dataWithContentsOfFile:longPath];
    return certificateData;
}

+ (SecKeyRef)getPublicKey0
{
    NSData *certificateData = [LVRSA publickKeyData];
    if ( certificateData==nil ){
        return NULL;
    }
    SecCertificateRef myCertificate = NULL;
    myCertificate = SecCertificateCreateWithData(kCFAllocatorDefault, (__bridge CFDataRef)certificateData);
    SecPolicyRef myPolicy = SecPolicyCreateBasicX509();
    SecTrustRef myTrust;
    OSStatus status = SecTrustCreateWithCertificates(myCertificate,myPolicy,&myTrust);
    SecTrustResultType trustResult;
    if (status == noErr) {
        status = SecTrustEvaluate(myTrust, &trustResult);
        if( status == noErr ){
            CFRelease(myPolicy);
            if( myCertificate ) {
                CFRelease(myCertificate);
            }
            return SecTrustCopyPublicKey(myTrust);
        }
    }
    CFRelease(myPolicy);
    if( myCertificate ) {
        CFRelease(myCertificate);
    }
    return NULL;
}

//私有秘钥的引用
+ (SecKeyRef)getPrivateKeyWithPassword:(NSString*) password
{
    NSString *publicKeyPath = [[NSBundle mainBundle] pathForResource:@"private_key" ofType:@"pfx"];
    NSData *pfxkeyData = [[NSData alloc]initWithContentsOfFile:publicKeyPath];
    
    NSMutableDictionary * options = [[NSMutableDictionary alloc] init];
    [options setObject:password forKey:(__bridge id)kSecImportExportPassphrase];
    
    CFArrayRef items = CFArrayCreate(NULL, 0, 0, NULL);
    
    OSStatus securityError = SecPKCS12Import((__bridge CFDataRef) pfxkeyData,
                                             (__bridge CFDictionaryRef)options, &items);
    
    CFDictionaryRef identityDict = CFArrayGetValueAtIndex(items, 0);
    SecIdentityRef identityApp =
    (SecIdentityRef)CFDictionaryGetValue(identityDict,
                                         kSecImportItemIdentity);
    
    assert(securityError == noErr);
    SecKeyRef privateKeyRef;
    SecIdentityCopyPrivateKey(identityApp, &privateKeyRef);
    
    return privateKeyRef;
    
}

//加密：
+ (NSData*)rsaEncryptWithData:(NSData*)data usingKey:(SecKeyRef)key{
    
    size_t cipherBufferSize = SecKeyGetBlockSize(key);
    uint8_t *cipherBuffer = malloc(cipherBufferSize * sizeof(uint8_t));
    memset((void *)cipherBuffer, 0*0, cipherBufferSize);
    
    NSData *plainTextBytes = data;
    size_t blockSize = cipherBufferSize - 11;
    size_t blockCount = (size_t)ceil([plainTextBytes length] / (double)blockSize);
    NSMutableData *encryptedData = [NSMutableData dataWithCapacity:0];
    
    for (int i=0; i<blockCount; i++) {
        NSInteger bufferSize = MIN(blockSize,[plainTextBytes length] - i * blockSize);
        NSData *buffer = [plainTextBytes subdataWithRange:NSMakeRange(i * blockSize, bufferSize)];
        
        OSStatus status = SecKeyEncrypt(key,
                                        kSecPaddingPKCS1,
                                        (const uint8_t *)[buffer bytes],
                                        [buffer length],
                                        cipherBuffer,
                                        &cipherBufferSize);
        if (status == noErr){
            NSData *encryptedBytes = [NSData dataWithBytes:(const void *)cipherBuffer length:cipherBufferSize];
            [encryptedData appendData:encryptedBytes];
            
        } else {
            if (cipherBuffer) {
                free(cipherBuffer);
            }
            return nil;
        }
    }
    if (cipherBuffer) free(cipherBuffer);
    return encryptedData;
    
}

//解密：
+ (NSData*)rsaDecryptWithData:(NSData*)data usingKey:(SecKeyRef)key{
    NSData *wrappedSymmetricKey = data;
    
    size_t cipherBufferSize = SecKeyGetBlockSize(key);
    size_t keyBufferSize = [wrappedSymmetricKey length];
    
    NSMutableData *bits = [NSMutableData dataWithLength:keyBufferSize];
    OSStatus sanityCheck = SecKeyDecrypt(key,
                                         kSecPaddingPKCS1,
                                         (const uint8_t *) [wrappedSymmetricKey bytes],
                                         cipherBufferSize,
                                         [bits mutableBytes],
                                         &keyBufferSize);
    NSAssert(sanityCheck == noErr, @"Error decrypting, OSStatus == %d", (int)sanityCheck);
    LVLog(@"Error decrypting, OSStatus == %d",(int)sanityCheck);
    [bits setLength:keyBufferSize];
    
    return bits;
    
}

NSData *LV_AES256DecryptDataWithKey(NSData *data, NSData* key){
    if( data.length>0 && key.length>0 ) {
        NSUInteger dataLength = [data length];
        
        // See the doc: For block ciphers, the output size will always be less than or
        // equal to the input size plus the size of one block.
        // That's why we need to add the size of one block here
        size_t bufferSize = (dataLength + kCCKeySizeAES256);
        void *buffer = malloc(bufferSize);
        bzero(buffer, sizeof(bufferSize));
        
        size_t numBytesDecrypted = 0;
        CCCryptorStatus cryptStatus = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionECBMode,
                                              key.bytes, key.length,
                                              NULL /* initialization vector (optional) */,
                                              [data bytes], dataLength, /* input */
                                              buffer, bufferSize, /* output */
                                              &numBytesDecrypted);
        
        if ( cryptStatus == kCCSuccess ) {
            return [NSData dataWithBytesNoCopy:buffer length:numBytesDecrypted];
        }
        
        free(buffer); //free the buffer;
        return nil;
    }
    return nil;
}

#define kChosenDigestLength CC_SHA1_DIGEST_LENGTH  // SHA-1消息摘要的数据位数160位
NSData* lv_SHA1HashBytes(NSData *fileData){
    CC_SHA1_CTX ctx;
    uint8_t * hashBytes = NULL;
    NSData * hash = nil;
    
    // Malloc a buffer to hold hash.
    hashBytes = malloc( kChosenDigestLength * sizeof(uint8_t) );
    memset((void *)hashBytes, 0x0, kChosenDigestLength);
    // Initialize the context.
    CC_SHA1_Init(&ctx);
    // Perform the hash.
    CC_SHA1_Update(&ctx, (void *)[fileData bytes], (CC_LONG)[fileData length]);
    // Finalize the output.
    CC_SHA1_Final(hashBytes, &ctx);
    
    // Build up the SHA1 blob.
    hash = [NSData dataWithBytes:(const void *)hashBytes length:(NSUInteger)kChosenDigestLength];
    if (hashBytes) free(hashBytes);
    
    return hash;
}

NSError* lv_verifySignSHA1WithRSA(NSData *fileData, SecKeyRef pubKeyRef, NSData *signedData){
    if(!fileData || !pubKeyRef || !signedData){
        return [NSError errorWithDomain:@"" code:-1 userInfo:@{@"desc":[NSString stringWithFormat:@"input error fileData=%p pubKeyRef=%p signedData=%p", fileData, pubKeyRef, signedData]}];
    }
    OSStatus sanityCheck = noErr;
    
    NSData *hash = lv_SHA1HashBytes(fileData);
    size_t signedHashBytesSize = SecKeyGetBlockSize(pubKeyRef);
    //kSecPaddingPKCS1SHA1   kSecPaddingPKCS1MD5
    sanityCheck = SecKeyRawVerify(pubKeyRef, kSecPaddingPKCS1SHA1, [hash bytes], kChosenDigestLength, [signedData bytes], signedHashBytesSize);
    
    //    NS Log(@"data=%@\n hash=%@\n signed=%@", fileData, hash, signedData);
    if (sanityCheck == noErr){
        return nil;
    }else{
        return [NSError errorWithDomain:@"" code:sanityCheck userInfo:@{@"desc":[NSString stringWithFormat:@"verifySign failed OSStatus=%d", (int)sanityCheck]}];
    }
}

@end




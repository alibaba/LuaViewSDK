/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVRSA.h"
#import <Security/Security.h>
#import <Security/Security.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonCryptor.h>
#import <Security/SecItem.h>
#import "LVUtil.h"

@implementation LVRSA {
    NSData* g_keyBytes;
    SecKeyRef g_publicKey;
    NSString* g_publicKeyFilePath;
}

-(void) dealloc{
    LVReleaseAndNull(g_publicKey);
}

NSError* lv_verifySignSHA1WithRSA(NSData *fileData, SecKeyRef pubKeyRef, NSData *signedData);

-(BOOL) verifyData:(NSData*)data withSignedData:(NSData*) signedData{
    SecKeyRef key = [self getPublicKey];
    NSError* error = lv_verifySignSHA1WithRSA(data, key, signedData);
    if( error == nil ){
        return YES;
    }
    LVLog(@"%@",error);
    return NO;
}

-(NSData*) aesKeyBytes{
    if( g_keyBytes==nil ) {
        NSData *certificateData = [self publickKeyData];
        g_keyBytes = [LVUtil MD5HashDataFromData:certificateData];
    }
    return g_keyBytes;
}

//公共秘钥的引用：
- (SecKeyRef)getPublicKey{
    if( g_publicKey == NULL ){
        g_publicKey = [self getPublicKey0];
    }
    return g_publicKey;
}

- (void) setPublicKeyFilePath:(NSString*) filePath{
    g_publicKeyFilePath = filePath;
}

-(NSData*) publickKeyData{
    if( g_publicKeyFilePath==nil )
        g_publicKeyFilePath = [[NSBundle mainBundle] pathForResource:@"public_key" ofType:@"der"];
    
    NSData *certificateData = [NSData dataWithContentsOfFile:g_publicKeyFilePath];
    return certificateData;
}

- (SecKeyRef)getPublicKey0
{
    NSData *certificateData = [self publickKeyData];
    if ( certificateData==nil ){
        return NULL;
    }
    SecCertificateRef myCertificate = NULL;
    myCertificate = SecCertificateCreateWithData(kCFAllocatorDefault, (__bridge CFDataRef)certificateData);
    SecPolicyRef myPolicy = SecPolicyCreateBasicX509();
    SecTrustRef myTrust = NULL;
    OSStatus status = SecTrustCreateWithCertificates(myCertificate,myPolicy,&myTrust);
    SecTrustResultType trustResult;
    if (status == noErr) {
        status = SecTrustEvaluate(myTrust, &trustResult);
        if( status == noErr ){
            LVReleaseAndNull(myPolicy);
            LVReleaseAndNull(myCertificate);
            SecKeyRef ret = SecTrustCopyPublicKey(myTrust);
            LVReleaseAndNull(myTrust);
            return ret;
        }
    }
    LVReleaseAndNull(myPolicy);
    LVReleaseAndNull(myCertificate);
    return NULL;
}

//加密：
- (NSData*)rsaEncryptWithData:(NSData*)data usingKey:(SecKeyRef)key{
    
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
- (NSData*)rsaDecryptWithData:(NSData*)data usingKey:(SecKeyRef)key{
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
        CCCryptorStatus cryptStatus = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
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


NSData* lv_SHA256HashBytes(NSData *fileData){
    CC_SHA256_CTX ctx;
    uint8_t * hashBytes = NULL;
    NSData * hash = nil;
    
    // Malloc a buffer to hold hash.
    hashBytes = malloc( CC_SHA256_DIGEST_LENGTH * sizeof(uint8_t) );
    memset((void *)hashBytes, 0x0, CC_SHA256_DIGEST_LENGTH);
    // Initialize the context.
    CC_SHA256_Init(&ctx);
    // Perform the hash.
    CC_SHA256_Update(&ctx, (void *)[fileData bytes], (CC_LONG)[fileData length]);
    // Finalize the output.
    CC_SHA256_Final(hashBytes, &ctx);
    
    // Build up the SHA256 blob.
    hash = [NSData dataWithBytes:(const void *)hashBytes length:(NSUInteger)CC_SHA256_DIGEST_LENGTH];
    if ( hashBytes ) {
        free(hashBytes);
    }
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




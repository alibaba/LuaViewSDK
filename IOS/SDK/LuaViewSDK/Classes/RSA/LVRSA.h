/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import <Foundation/Foundation.h>

@interface LVRSA : NSObject

-(BOOL) verifyData:(NSData*)data withSignedData:(NSData*) sign;

-(NSData*) aesKeyBytes;

/*
 * 设置证书名
 */
- (void) setPublicKeyFilePath:(NSString*) filePath;

NSData* lv_SHA256HashBytes(NSData *fileData);

@end

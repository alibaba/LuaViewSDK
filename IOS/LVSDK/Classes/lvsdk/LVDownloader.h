//
//  LVDownloader.h
//  LVSDK
//
//  Created by dongxicheng on 4/14/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LVHeads.h"

typedef struct _LVUserDataDownloader {
    LVUserDataCommonHead;
    const void* downloader;
} LVUserDataDownloader;



@interface LVDownloader : NSObject

@property(nonatomic,weak) LView* lview;
@property(nonatomic,assign) LVUserDataDownloader* userData;

@property(nonatomic,copy) id luaObjRetainKey;

-(id) init:(lv_State*) l;

+(int) classDefine:(lv_State *)L ;

@end


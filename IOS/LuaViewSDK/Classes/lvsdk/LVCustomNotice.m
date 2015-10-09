//
//  LVNotice.m
//  LVSDK
//
//  Created by dongxicheng on 7/20/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCustomNotice.h"
#import "LView.h"
#import "LVBaseView.h"

@implementation LVCustomNotice


-(id) init:(lv_State*) l notice:(NSString*) info{
    self = [super init];
    if( self ){
    }
    return self;
}


static Class g_class = nil;
+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVCustomNotice class]] ){
        g_class = c;
    }
}

static int lvNewNoticeView (lv_State *L) {
    if( g_class == nil ) {
        g_class = [LVCustomNotice class];
    }
    NSString* info = lv_paramString(L, 1);
    {
        LVCustomNotice* notice = [[g_class alloc] init:L notice:info];
        
        {
            NEW_USERDATA(userData, LVUserDataView);
            userData->view = CFBridgingRetain(notice);
            
            lvL_getmetatable(L, META_TABLE_NoticeView );
            lv_setmetatable(L, -2);
        }
        LView* view = (__bridge LView *)(L->lView);
        if( view ){
            [view containerAddSubview:notice];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) classDefine:(lv_State *)L {
    {
        lv_pushcfunction(L, lvNewNoticeView);
        lv_setglobal(L, "UICustomNotice");
    }
    const struct lvL_reg memberFunctions [] = {
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_NoticeView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end

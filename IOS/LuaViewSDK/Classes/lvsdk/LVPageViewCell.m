//
//  LVPageViewCell.m
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVPageViewCell.h"
#import "LView.h"

@interface LVPageViewCell ()
@property (nonatomic,weak) LView* lv_lview;
@end

@implementation LVPageViewCell



-(void) dealloc{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        [LVUtil unregistry:L key:self];
    }
}

-(void) doInitWithLView:(LView*) lview{
    self.lv_lview = lview;
    lv_State* L = lview.l;
    if( L ) {
        lv_createtable(L, 0, 0);
        [LVUtil registryValue:L key:self stack:-1];
    }
}

-(void) pushTableToStack{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        [LVUtil pushRegistryValue:L key:self];
    }
}


@end

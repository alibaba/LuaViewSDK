//
//  LVCollectionViewCell.m
//  LVSDK
//
//  Created by dongxicheng on 6/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCollectionViewCell.h"
#import "LVHeads.h"
#import "LView.h"

@interface LVCollectionViewCell ()
@property (nonatomic,weak) LView* lv_lview;
@end



@implementation LVCollectionViewCell


-(void) dealloc{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        lv_createtable(L, 0, 0);
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

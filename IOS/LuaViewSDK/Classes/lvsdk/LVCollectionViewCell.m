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
#import "lVapi.h"

@interface LVCollectionViewCell ()
@property (nonatomic,weak) LView* lv_lview;
@end



@implementation LVCollectionViewCell


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
        lv_luaTableSetWeakWindow(L, self);
    }
}

-(void) pushTableToStack{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        [LVUtil pushRegistryValue:L key:self];
    }
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<CollectionViewCell(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame)];
}

- (void) layoutSubviews{
    [super layoutSubviews];
    
    CGRect rect = self.frame;
    NSArray* subviews = [self.contentView subviews];
    for( UIView* view in subviews){
        [view lv_alignSelfWithSuperRect:rect];
    }
}

@end

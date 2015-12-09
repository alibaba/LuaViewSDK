//
//  LVPageViewCell.m
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVPagerViewCell.h"
#import "LView.h"
#import "lVapi.h"

@interface LVPagerViewCell ()
@property (nonatomic,weak) LView* lv_lview;
@end

@implementation LVPagerViewCell



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

-(UIView*) contentView{
    return self;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<PagerViewCell(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame)];
}

- (void) layoutSubviews{
    [super layoutSubviews];
    [self lv_alignSubviews];
}

@end

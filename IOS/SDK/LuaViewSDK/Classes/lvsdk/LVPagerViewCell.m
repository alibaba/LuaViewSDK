//
//  LVPageViewCell.m
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVPagerViewCell.h"
#import "LuaViewCore.h"
#import "lapi.h"

@interface LVPagerViewCell ()
@property (nonatomic,weak) LuaViewCore* lv_luaviewCore;
@end

@implementation LVPagerViewCell



-(void) dealloc{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        [LVUtil unregistry:L key:self];
    }
}

-(void) doInitWithLView:(LuaViewCore*) lview{
    self.lv_luaviewCore = lview;
    lua_State* L = lview.l;
    if( L ) {
        lua_createtable(L, 0, 0);
        [LVUtil registryValue:L key:self stack:-1];
        lv_luaTableSetWeakWindow(L, self);
    }
}

-(void) pushTableToStack{
    lua_State* L = self.lv_luaviewCore.l;
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

-(instancetype) init{
    self = [super init];
    if ( self ) {
        self.clipsToBounds = YES;
    }
    return self;
}

@end

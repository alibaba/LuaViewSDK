/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCollectionViewCell.h"
#import "LVHeads.h"
#import "LView.h"
#import "lapi.h"

@interface LVCollectionViewCell ()
@property (nonatomic,weak) LuaViewCore* lv_luaviewCore;
@end



@implementation LVCollectionViewCell


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
        lv_luaTableSetWeakWindow(L, self.contentView);
    }
}

-(void) pushTableToStack{
    lua_State* L = self.lv_luaviewCore.l;
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

// 修复gif图片滚动会消失问题
-(void) prepareForReuse{
}

@end

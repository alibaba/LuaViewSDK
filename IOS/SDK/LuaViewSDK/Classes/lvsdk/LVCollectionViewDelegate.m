/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCollectionViewDelegate.h"
#import "LVCollectionView.h"
#import "LVCollectionViewCell.h"
#import "LView.h"
#import "UIView+LuaView.h"
#import "LVHeads.h"


static inline NSInteger mapRow(NSInteger row){
    return row + 1;
}

static inline NSInteger mapSection(NSInteger section){
    return section + 1;
}

@interface LVCollectionViewDelegate ()

@property(nonatomic, strong) NSMutableSet *registeredIds;


@end


@implementation LVCollectionViewDelegate

- (void)tryRegisterId:(NSString *)identifier inCollectionView:(UICollectionView *)view {
    if (self.registeredIds == nil) {
        self.registeredIds = [NSMutableSet set];
    }
    if (![self.registeredIds containsObject:identifier]) {
        [view registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:identifier];
        [self.registeredIds addObject:identifier];
    }
}

- (UICollectionViewCell*) collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    BOOL pinned = NO;
    NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row) pinned:&pinned];
    if( identifier == nil ){
        identifier = DEFAULT_CELL_IDENTIFIER;
    }
    //  制定的cell 是否悬浮
    if( pinned ) {
        [self.lvflowLayout addPinnedIndexPath:indexPath];
    } else {
        [self.lvflowLayout delPinnedIndexPath:indexPath];
    }
    [self tryRegisterId:identifier inCollectionView:collectionView];
    LVCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    cell.contentView.frame = cell.bounds;//脚本的window是ContentView大小可能和Cell不同步
    LuaViewCore* lview = self.owner.lv_luaviewCore;
    lua_State* L = lview.l;
    UIView* newWindow = cell.contentView;
    [lview pushWindow:newWindow];
    if ( L ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lua_settop(L, 0);
            lua_checkstack(L, 12);
            [cell pushTableToStack];//arg1: cell
            lua_pushnumber(L, mapSection(section) );//arg2: section
            lua_pushnumber(L, mapRow(row) );//arg3: row
            
            lv_pushUserdata(L, self.owner.lv_userData);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            [LVUtil call:L key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:0 retType:LUA_TNONE];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lua_settop(L, 0);
            lua_checkstack(L, 12);
            [cell pushTableToStack];//arg1: cell
            lua_pushnumber(L, mapSection(section) );//arg2: section
            lua_pushnumber(L, mapRow(row) );//arg3: row
            
            lv_pushUserdata(L, self.owner.lv_userData);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            [LVUtil call:L key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:0 retType:LUA_TNONE];
        }
    }
    [lview popWindow:newWindow];
    return cell;
}

// section数量
- (NSInteger) numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l && self.owner.lv_userData ){
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"SectionCount" key3:NULL nargs:0 nrets:1 retType:LUA_TNUMBER] ==0 ) {
            if( lua_type(l, -1)==LUA_TNUMBER ) {
                NSInteger num = lua_tonumber(l, -1);
                num = (num>0 ? num : 0);
                return num;
            }
        }
        return 1;
    }
    return 0;
}
// 每个区域的行数
- (NSInteger) collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        // args
        lua_pushnumber(l, mapSection(section) );
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" key3:NULL nargs:1 nrets:1 retType:LUA_TNUMBER] ==0 ) {
            if( lua_type(l, -1)==LUA_TNUMBER ) {
                NSInteger num = lua_tonumber(l, -1);
                num = (num>0 ? num : 0);
                return num;
            }
        }
    }
    return 0;
}
//-------
#pragma mark --UICollectionViewDelegateFlowLayout
//定义每个UICollectionView 的大小
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row) pinned:NULL];
    if( identifier ) {
        CGSize size = [self retSizeCallKey1:"Cell" key2:identifier.UTF8String key3:"Size" mapedSection:mapSection(section) mapedRow:mapRow(row) ];
        if( size.width<0 || isnan(size.width) ) {
            size.width = 0;
        }
        if( size.height<0 || isnan(size.height) ) {
            size.height = 0;
        }
        return size;
    } else {
        return CGSizeMake(self.owner.frame.size.width, 1);
    }
}
//定义每个UICollectionView 的间距
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    UIEdgeInsets insets = [self retInsetCallKey1:"Section" key2:"EdgeInsets" mapedSection:mapSection(section) mapedRow:mapRow(0)];
    //insets.top = self.flowLayout.minimumLineSpacing;
    return insets;
}
//定义每个UICollectionView 纵向的间距
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    //    return self.flowLayout.minimumLineSpacing;
    CGFloat spacing = [self retFloatCallKey1:"Section" key2:"Spacing" mapedSection:mapSection(section) ];
    return spacing;
}

- (CGFloat) retFloatCallKey1:(const char*) funcName key2:(const char*) key2 mapedSection:(NSInteger) mapedSection {
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        lua_checkstack(l, 12);
        lua_pushnumber(l, mapedSection);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:funcName key2:key2 key3:NULL nargs:1 nrets:1 retType:LUA_TNUMBER] ==0 ) {
            if( lua_type(l, -1)==LUA_TNUMBER ) {
                CGFloat heigth = lua_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (NSString*) retStringCallKey1:(const char*) key1 key2:(const char*)key2
                   mapedSection:(NSInteger) mapedSection mapedRow:(NSInteger) mapedRow pinned:(BOOL*) pinned{
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        // args
        lua_checkstack(l, 12);
        lua_pushnumber(l, mapedSection);
        lua_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:2 nrets:2 retType:LUA_TSTRING] ==0 ) {
            if( lua_type(l, -2)==LUA_TSTRING ){
                NSString* value = lv_paramString(l, -2);
                BOOL yes = lua_toboolean(l, -1);
                if( pinned ) {
                    *pinned = yes;
                }
                return value;
            }
        }
    }
    return nil;
}

- (CGSize) retSizeCallKey1:(const char*) key1 key2:(const char*)key2 key3:(const char*)key3
              mapedSection:(NSInteger) mapedSection mapedRow:(NSInteger) mapedRow {
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        // args
        lua_checkstack(l, 12);
        lua_pushnumber(l, mapedSection);
        lua_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:key3 nargs:2 nrets:2 retType:LUA_TNONE] ==0 ) {
            CGSize size = {0};
            if( lua_type(l, -1) ==LUA_TNIL ) {
                size.width = self.owner.frame.size.width;
                size.height = lua_tonumber(l, -2);
            } else{
                size.width = lua_tonumber(l, -2);
                size.height = lua_tonumber(l, -1);
            }
            return size;
        }
    }
    return CGSizeMake(self.owner.frame.size.width, 1);
}
- (UIEdgeInsets) retInsetCallKey1:(const char*) key1 key2:(const char*)key2
                     mapedSection:(NSInteger) mapSection mapedRow:(NSInteger)mapedRow {
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        // args
        lua_checkstack(l, 12);
        lua_pushnumber(l, mapSection);
        lua_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:2 nrets:4 retType:LUA_TNONE] ==0 ) {
            UIEdgeInsets egeInsets = {0};
            egeInsets.top = lua_tonumber(l, -4);
            egeInsets.left = lua_tonumber(l, -3);
            egeInsets.bottom = lua_tonumber(l, -2);
            egeInsets.right = lua_tonumber(l, -1);
            return egeInsets;
        }
    }
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

- (void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    lua_State* l = self.owner.lv_luaviewCore.l;
    if( l ){
        NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row) pinned:NULL];
        if ( identifier ) {
            // 参数 cell,section,row
            lua_settop(l, 0);
            lua_checkstack(l, 12);
            lua_pushnil(l);// cell
            lua_pushnumber(l, mapSection(section) );
            lua_pushnumber(l, mapRow(row) );
            
            // table
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            
            if(  [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:STR_CALLBACK key4:STR_ON_CLICK nargs:3 nrets:0 retType:LUA_TNONE]==0 ) {
            }
        }
    }
}

-(void) callWithScrollArgsForKey:(NSString*) keyName{
    UICollectionView* tableView = (UICollectionView*)self.owner;
    NSArray<NSIndexPath *>* indexPaths = [tableView indexPathsForVisibleItems];
    int visibleCount = 0;
    NSIndexPath* indexPath0 = nil;
    
    for( NSIndexPath* indexPath in indexPaths ) {
        visibleCount ++;
        if( indexPath0== nil ) {
            indexPath0 = indexPath;
        } else if( indexPath.row <indexPath0.row || indexPath.section<indexPath0.section ){
            indexPath0 = indexPath;
        }
    }
    lua_State* L = self.owner.lv_luaviewCore.l;
    if( L && indexPath0 ) {
        NSInteger section = indexPath0.section;
        NSInteger row = indexPath0.row;
        
        lua_settop(L, 0);
        lua_pushnumber(L, mapSection(section) );
        lua_pushnumber(L, mapRow(row) );
        lua_pushnumber(L, visibleCount );
        [self.owner lv_callLuaCallback:keyName key2:nil argN:3];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self callWithScrollArgsForKey:@"Scrolling"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidScroll:)] ) {
        [self.delegate scrollViewDidScroll:scrollView];
    }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self callWithScrollArgsForKey:@"ScrollBegin"];
    if( [self.delegate respondsToSelector:@selector(scrollViewWillBeginDragging:)] ) {
        [self.delegate scrollViewWillBeginDragging:scrollView];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callWithScrollArgsForKey:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDecelerating:)] ) {
        [self.delegate scrollViewDidEndDecelerating:scrollView];
    }
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callWithScrollArgsForKey:@"ScrollEnd"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndScrollingAnimation:)] ) {
        [self.delegate scrollViewDidEndScrollingAnimation:scrollView];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if( !decelerate ) {
        [self callWithScrollArgsForKey:@"ScrollEnd"];
    }
    if( [self.delegate respondsToSelector:@selector(scrollViewDidEndDragging:willDecelerate:)] ) {
        [self.delegate scrollViewDidEndDragging:scrollView willDecelerate:decelerate];
    }
}

// IOS 特有事件, 状态栏回顶
- (void)scrollViewDidScrollToTop:(UIScrollView *)scrollView{
    [self callWithScrollArgsForKey:@"ScrollToTop"];
    if( [self.delegate respondsToSelector:@selector(scrollViewDidScrollToTop:)] ) {
        [self.delegate scrollViewDidScrollToTop:scrollView];
    }
}

@end

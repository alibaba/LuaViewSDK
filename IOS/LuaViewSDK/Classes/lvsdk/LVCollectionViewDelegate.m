//
//  LVCollectionViewDelegate.m
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVCollectionViewDelegate.h"
#import "LVCollectionView.h"
#import "LVCollectionViewCell.h"
#import "LView.h"
#import "UIView+LuaView.h"
#import "LVHeads.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


static inline NSInteger mapRow(NSInteger row){
    return row + 1;
}

static inline NSInteger mapSection(NSInteger section){
    return section + 1;
}

@interface LVCollectionViewDelegate ()

@end


@implementation LVCollectionViewDelegate

- (UICollectionViewCell*) collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row)];
    identifier = self.identifierDic[identifier];
    if( identifier == nil ){
        identifier = DEFAULT_CELL_IDENTIFIER;
    }
    LVCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    LView* lview = self.owner.lv_lview;
    lv_State* l = lview.l;
    lview.conentView = cell.contentView;
    lview.contentViewIsWindow = NO;
    if ( l ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, mapSection(section) );//arg2: section
            lv_pushnumber(l, mapRow(row) );//arg3: row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:0 retType:LV_TNONE];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, mapSection(section) );//arg2: section
            lv_pushnumber(l, mapRow(row) );//arg3: row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:0 retType:LV_TNONE];
        }
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
    return cell;
}

// section数量
- (NSInteger) numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    lv_State* l = self.owner.lv_lview.l;
    if( l && self.owner.lv_userData ){
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"SectionCount" key3:NULL nargs:0 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
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
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_pushnumber(l, mapSection(section) );
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" key3:NULL nargs:1 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
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
    NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row) ];
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
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapedSection);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:funcName key2:key2 key3:NULL nargs:1 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (NSString*) retStringCallKey1:(const char*) key1 key2:(const char*)key2
                   mapedSection:(NSInteger) mapedSection mapedRow:(NSInteger) mapedRow{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapedSection);
        lv_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:2 nrets:1 retType:LV_TSTRING] ==0 ) {
            if( lv_type(l, -1)==LV_TSTRING ){
                NSString* value = lv_paramString(l, -1);
                return value;
            }
        }
    }
    return nil;
}

- (CGSize) retSizeCallKey1:(const char*) key1 key2:(const char*)key2 key3:(const char*)key3
              mapedSection:(NSInteger) mapedSection mapedRow:(NSInteger) mapedRow {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapedSection);
        lv_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:key3 nargs:2 nrets:2 retType:LV_TNONE] ==0 ) {
            CGSize size = {0};
            if( lv_type(l, -1) ==LV_TNIL ) {
                size.width = self.owner.frame.size.width;
                size.height = lv_tonumber(l, -2);
            } else{
                size.width = lv_tonumber(l, -2);
                size.height = lv_tonumber(l, -1);
            }
            return size;
        }
    }
    return CGSizeMake(self.owner.frame.size.width, 1);
}
- (UIEdgeInsets) retInsetCallKey1:(const char*) key1 key2:(const char*)key2
                     mapedSection:(NSInteger) mapSection mapedRow:(NSInteger)mapedRow {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapSection);
        lv_pushnumber(l, mapedRow);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:2 nrets:4 retType:LV_TNONE] ==0 ) {
            UIEdgeInsets egeInsets = {0};
            egeInsets.top = lv_tonumber(l, -4);
            egeInsets.left = lv_tonumber(l, -3);
            egeInsets.bottom = lv_tonumber(l, -2);
            egeInsets.right = lv_tonumber(l, -1);
            return egeInsets;
        }
    }
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

- (void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self retStringCallKey1:"Cell" key2:IDENTIFIER mapedSection:mapSection(section) mapedRow:mapRow(row) ];
        if ( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            lv_pushnil(l);// cell
            lv_pushnumber(l, mapSection(section) );
            lv_pushnumber(l, mapRow(row) );
            
            // table
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            
            if(  [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:STR_CALLBACK nargs:3 nrets:0 retType:LV_TNONE]==0 ) {
            }
        }
    }
}

-(void) callWithScrollArgsForKey:(NSString*) functionName{
    UICollectionView* tableView = (UICollectionView*)self.owner;
    NSArray<NSIndexPath *>* indexPaths = [tableView indexPathsForVisibleItems];
    int visibleCount = 0;
    NSIndexPath* indexPath0 = nil;
    
    for( NSIndexPath* indexPath in indexPaths ) {
        visibleCount ++;
        if( indexPath0== nil ) {
            indexPath0 = indexPath;
        }
    }
    lv_State* L = self.owner.lv_lview.l;
    if( L && indexPath0 ) {
        NSInteger section = indexPath0.section;
        NSInteger row = indexPath0.row;
        
        lv_settop(L, 0);
        lv_pushnumber(L, mapSection(section) );
        lv_pushnumber(L, mapRow(row) );
        lv_pushnumber(L, visibleCount );
        [self.owner lv_callLuaByKey1:functionName key2:nil argN:3];
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

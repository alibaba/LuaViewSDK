//
//  LVTableViewDelegate.m
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVTableViewDelegate.h"
#import "LVTableView.h"
#import "LVHeads.h"
#import "LView.h"
#import "LVTableViewCell.h"
#import "UIView+LuaView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"


@implementation LVTableViewDelegate


static inline BOOL isDivider(NSInteger row){
    return row&1;
}

static inline NSInteger mapRow(NSInteger row){
    return row/2 + 1;
}

static inline NSInteger mapSection(NSInteger section){
    return section + 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    if( isDivider(row) ) {
        static NSString* tag = @"divider.Height.identifier";
        UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:tag];
        if( cell==nil ) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:tag];
            cell.contentView.backgroundColor = [UIColor clearColor];
            cell.backgroundColor = [UIColor clearColor];
            cell.userInteractionEnabled = NO;
            cell.contentView.userInteractionEnabled = NO;
        }
        return cell;
    }
    
    NSString* identifier = [self retStrCallKey1:"Cell" key2:Identifier mapedSection:mapSection(section) mapedRow:mapRow(row)];
    if( identifier == nil ){
        identifier = @"LVTableViewCell.default.identifier";
    }
    LView* lview = self.owner.lv_lview;
    lv_State* l = lview.l;
    LVTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if( cell==nil ) {
        cell = [[LVTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    lview.conentView = cell.contentView;
    lview.contentViewIsWindow = NO;
    if ( l ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            {   // 设置默认的宽度高度
                CGFloat height = [self retHeightCallAtIndexPath:indexPath identifier:identifier lvState:l];
                CGRect r = cell.frame;
                r.size.width = self.owner.frame.size.width;
                r.size.height = height;
                cell.frame = r;
            }
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//argcell
            lv_pushnumber(l, mapSection(section) );// section
            lv_pushnumber(l, mapRow(row) );// row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:0 retType:LV_TNONE];
        }
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
    return cell;
}

-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell0 forRowAtIndexPath:(NSIndexPath *)indexPath{
    if( isDivider(indexPath.row) ){
        return;
    }
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    NSString* identifier = [self retStrCallKey1:"Cell" key2:Identifier mapedSection:mapSection(section) mapedRow:mapRow(row)];
    if( identifier == nil ){
        identifier = @"LVTableViewCell.default.identifier";
    }
    LView* lview = self.owner.lv_lview;
    lv_State* l = lview.l;
    LVTableViewCell* cell = (LVTableViewCell*)cell0;
    lview.conentView = cell.contentView;
    lview.contentViewIsWindow = NO;
    if ( l ) {
        // 通知布局调整
        // 参数 cell,section,row
        lv_settop(l, 0);
        lv_checkstack(l, 12);
        [cell pushTableToStack];
        lv_pushnumber(l, mapSection(section));
        lv_pushnumber(l, mapRow(row) );
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:0 retType:LV_TNONE];
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
}

// section数量
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
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
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        //args
        lv_pushnumber(l, mapSection(section) );
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" key3:NULL nargs:1 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                num = (num>0 ? num : 0);
                return num*2;
            }
        }
    }
    return 0;
}

- (CGFloat) retFloatCallKey1:(const char*) funcName key2:(const char*) key2 mapedSection:(NSInteger) mapedSection {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapedSection);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l  key1:funcName key2:key2 key3:NULL nargs:1 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (NSString*) retStrCallKey1:(const char*) key1 key2:(const char*)key2 mapedSection:(NSInteger) mapedSection mapedRow:(NSInteger) mapedRow{
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

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    if( isDivider(row) ){
        return;
    }
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self retStrCallKey1:"Cell" key2:Identifier mapedSection:mapSection(section) mapedRow:mapRow(row)];
        if( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);

            lv_pushnil(l);// 参数cell 目前是空的;
            lv_pushnumber(l, mapSection(section) );
            lv_pushnumber(l, mapRow(row) );
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:STR_CALLBACK  nargs:3 nrets:0 retType:LV_TNONE];
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    NSInteger section = indexPath.section;
    NSInteger row = indexPath.row;
    if( isDivider(row) ) {
        return self.dividerHeight;
    }
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self retStrCallKey1:"Cell" key2:Identifier mapedSection:mapSection(section) mapedRow:mapRow(row) ];
        if( identifier ) {
            return [self retHeightCallAtIndexPath:indexPath identifier:identifier lvState:l];
        }
    }
    return 0;
}

-(CGFloat) retHeightCallAtIndexPath:(NSIndexPath*)indexPath identifier:(NSString*)identifier lvState:(lv_State*) l{
    // 参数 cell,section,row
    lv_settop(l, 0);
    lv_checkstack(l, 12);
    lv_pushnumber(l, mapSection(indexPath.section) );
    lv_pushnumber(l, mapRow(indexPath.row) );
    
    lv_pushUserdata(l, self.owner.lv_userData);
    lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
    [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Size" nargs:2 nrets:2 retType:LV_TNONE];
    if( lv_type(l, -1)==LV_TNUMBER ) {
        CGFloat heigth = lv_tonumber(l, -1);
        return heigth;
    }
    if( lv_type(l, -2)==LV_TNUMBER ) {
        CGFloat heigth = lv_tonumber(l, -2);
        return heigth;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return [self retFloatCallKey1:"Section" key2:"HeaderHeight" mapedSection:mapSection(section) ];
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return [self retFloatCallKey1:"Section" key2:"FooterHeight" mapedSection:mapSection(section) ];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    LVUserDataInfo* user = [self retUserDataCallKey1:"Section" key2:"Header" mapedSection:mapSection(section) ];
    lv_State* l = self.owner.lv_lview.l;
    if( l && self.owner.lv_userData ){
        // 绑定 tableHeaderView
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        NSString* key = [NSString stringWithFormat:@"tableview.headerView.%ld",(long)section];
        lv_pushstring(l, key.UTF8String);// key
        if( user ){
            lv_pushUserdata(l,user);// value
        } else {
            lv_pushnil(l);
        }
        lv_settable(l, -3);// registry[&Key] = tableView
    }
    if( user ){
        return (__bridge UIView *)(user->object);
    }
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    LVUserDataInfo* user = [self retUserDataCallKey1:"Section" key2:"Footer" mapedSection:mapSection(section) ];
    lv_State* l = self.owner.lv_lview.l;
    if( l && self.owner.lv_userData){
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        lv_pushstring(l, [NSString stringWithFormat:@"tableView.footerView.%d",(int)section].UTF8String);// key
        lv_pushUserdata(l, user);// value
        lv_settable(l, -3);// registry[&Key] = tableView
    }
    if( user ){
        return (__bridge UIView *)(user->object);
    }
    return nil;
}

// 回调脚本返回一个用户数据
- (LVUserDataInfo *) retUserDataCallKey1:(const char*) key1 key2:(const char*)key2 mapedSection:(NSInteger) mapedSection {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, mapedSection );
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:1 nrets:1 retType:LV_TUSERDATA] ==0 ) {
            if( lv_type(l, -1)==LV_TUSERDATA ) {
                LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(l, -1);
                return user;
            }
        }
    }
    return nil;
}

-(void) callWithScrollArgsForKey:(NSString*) functionName{
    UITableView* tableView = (UITableView*)self.owner;
    NSArray* indexPaths = [tableView indexPathsForVisibleRows];
    int visibleCount = 0;
    NSIndexPath* indexPath0 = nil;
    
    for( NSIndexPath* indexPath in indexPaths ) {
        if( isDivider(indexPath.row) ){
        } else {
            visibleCount ++;
            if( indexPath0== nil ) {
                indexPath0 = indexPath;
            }
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
@end
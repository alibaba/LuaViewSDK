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


@implementation LVTableViewDelegate


static BOOL isDivider(NSInteger row){
    return row&1;
}

static NSInteger mapRow(NSInteger row){
    return row/2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    if( isDivider(indexPath.row) ) {
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
    
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:mapRow(indexPath.row)];
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
                CGFloat height = [self heightForRowAtIndexPath:indexPath identifier:identifier lvState:l];
                CGRect r = cell.frame;
                r.size.width = self.owner.frame.size.width;
                r.size.height = height;
                cell.frame = r;
            }
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//argcell
            lv_pushnumber(l, indexPath.section+1);// section
            lv_pushnumber(l, mapRow(indexPath.row)+1);// row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:1];
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
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:mapRow(indexPath.row)];
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
        lv_pushnumber(l, indexPath.section+1);
        lv_pushnumber(l, mapRow(indexPath.row)+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:1];
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
        
        if(  [LVUtil call:l key1:"Section" key2:"SectionCount" nargs:0 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
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
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num*2;
            }
        }
    }
    return 0;
}
//-------

- (CGFloat) callReturnNumberFunction:(const char*) functionName key2:(const char*) key2 section:(NSInteger) section row:(NSInteger) row{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:functionName key2:key2 nargs:2 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (CGFloat) callReturnNumberKey1:(const char*) funcName key2:(const char*) key2 section:(NSInteger) section {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l  key1:funcName key2:key2 nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (NSString*) returnStringCallWithKey1:(const char*) key1 key2:(const char*)key2 section:(NSInteger) section row:(NSInteger) row{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 nargs:2 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TSTRING ){
                NSString* value = lv_paramString(l, -1);
                return value;
            }
        }
    }
    return nil;
}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if( isDivider(indexPath.row) ){
        return;
    }
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:mapRow(indexPath.row)];
        if( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            lv_pushnumber(l, indexPath.section+1);
            lv_pushnumber(l, mapRow(indexPath.row)+1);
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:STR_CALLBACK nargs:2 nrets:0];
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if(isDivider(indexPath.row) ) {
        return self.dividerHeight;
    }
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:mapRow(indexPath.row) ];
        if( identifier ) {
            return [self heightForRowAtIndexPath:indexPath identifier:identifier lvState:l];
        }
    }
    return 0;
}

-(CGFloat) heightForRowAtIndexPath:(NSIndexPath*)indexPath identifier:(NSString*)identifier lvState:(lv_State*) l{
    // 参数 cell,section,row
    lv_settop(l, 0);
    lv_checkstack(l, 12);
    lv_pushnumber(l, indexPath.section+1);
    lv_pushnumber(l, mapRow(indexPath.row)+1);
    
    lv_pushUserdata(l, self.owner.lv_userData);
    lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
    [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Height" nargs:2 nrets:1];
    if( lv_type(l, -1)==LV_TNUMBER ) {
        CGFloat heigth = lv_tonumber(l, -1);
        return heigth;
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return [self callReturnNumberKey1:"Section" key2:"HeaderHeight" section:section ];
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section{
    return [self callReturnNumberKey1:"Section" key2:"FooterHeight" section:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    LVUserDataView* user = [self callReturnUserDataFunction:"Section" key2:"Header" section:section];
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
        return (__bridge UIView *)(user->view);
    }
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section{
    LVUserDataView* user = [self callReturnUserDataFunction:"Section" key2:"Footer" section:section];
    lv_State* l = self.owner.lv_lview.l;
    if( l && self.owner.lv_userData){
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        lv_pushstring(l, [NSString stringWithFormat:@"tableView.footerView.%d",(int)section].UTF8String);// key
        lv_pushUserdata(l, user);// value
        lv_settable(l, -3);// registry[&Key] = tableView
    }
    if( user ){
        return (__bridge UIView *)(user->view);
    }
    return nil;
}

// 回调脚本返回一个用户数据
- (LVUserDataView *) callReturnUserDataFunction:(const char*) key1 key2:(const char*)key2 section:(NSInteger) section {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:key1 key2:key2 nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TUSERDATA ) {
                LVUserDataView * user = (LVUserDataView *)lv_touserdata(l, -1);
                return user;
            }
        }
    }
    return nil;
}

@end
//
//  LVTableView.m
//  LVSDK
//
//  Created by dongxicheng on 1/28/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVTableView.h"
#import "LVBaseView.h"
#import "LVTableViewCell.h"
#import "LVScrollView.h"
#import "LView.h"

#define Identifier "Id"

#define KEY_LUA_INFO 1

@implementation LVTableView

-(id) init:(lv_State*) l{
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) style:UITableViewStylePlain];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.dataSource = self;
        self.backgroundColor = [UIColor clearColor];
        self.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.clipsToBounds = YES;
    }
    return self;
}

-(void) dealloc{
}

//----------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:indexPath.row];
    if( identifier == nil ){
        identifier = @"LVTableViewCell.default.identifier";
    }
    LView* lview = self.lv_lview;
    lv_State* l = lview.l;
    LVTableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if( cell==nil ) {
        cell = [[LVTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    lview.conentView = cell.contentView;
    if ( l ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//argcell
            lv_pushnumber(l, indexPath.section+1);// section
            lv_pushnumber(l, indexPath.row+1);// row
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:1];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];
            lv_pushnumber(l, indexPath.section+1);
            lv_pushnumber(l, indexPath.row+1);
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:1];
        }
    }
    lview.conentView = nil;
    return cell;
}

// section数量
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        
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
    lv_State* l = self.lv_lview.l;
    if( l ){
        //args
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num;
            }
        }
    }
    return 0;
}
//-------

- (CGFloat) callReturnNumberFunction:(const char*) functionName key2:(const char*) key2 section:(NSInteger) section row:(NSInteger) row{
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
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
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
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
    lv_State* l = self.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        
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
    lv_State* l = self.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:indexPath.row];
        if( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            lv_pushnumber(l, indexPath.section+1);
            lv_pushnumber(l, indexPath.row+1);
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Delegate" nargs:2 nrets:0];
        }
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    lv_State* l = self.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:Identifier section:indexPath.section row:indexPath.row];
        if( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            lv_pushnumber(l, indexPath.section+1);
            lv_pushnumber(l, indexPath.row+1);
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Height" nargs:2 nrets:1];
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
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
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        // 绑定 tableHeaderView
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        
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
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData){
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        
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
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        if(  [LVUtil call:l key1:key1 key2:key2 nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TUSERDATA ) {
                LVUserDataView * user = (LVUserDataView *)lv_touserdata(l, -1);
                return user;
            }
        }
    }
    return nil;
}

-(void) layoutSubviews{
    [super layoutSubviews];
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        [LVUtil call:l key1:"LayoutSubviews" key2:NULL nargs:0 nrets:0];
    }
}

- (UIView*) lv_getViewFromLuaByKey:(NSString*)key{
    lv_State* l = self.lv_lview.l;
    if( l ){
        int num = lv_gettop(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        [LVUtil call:l key1:key.UTF8String key2:NULL nargs:0 nrets:1];
        if( lv_type(l, -1)==LV_TUSERDATA ) {
            LVUserDataView * user = (LVUserDataView *)lv_touserdata(l, -1);
            if( LVIsType(user, LVUserDataView) ) {
                
                // 绑定 tableHeaderView
                lv_pushUserdata(l, self.lv_userData);
                lv_pushUDataRef(l, KEY_LUA_INFO );
                
                lv_pushstring(l, [NSString stringWithFormat:@"%@.backup",key].UTF8String);// key
                lv_pushUserdata(l, user);// value
                lv_settable(l, -3);// registry[&Key] = tableView
                
                lv_settop(l, num);
                return (__bridge UIView *)(user->view);
            }
        }
        lv_settop(l, num);
    }
    return nil;
}

- (void) getHeaderFooterFromDelegate {
    self.tableHeaderView = [self lv_getViewFromLuaByKey:@"Header"];
    self.tableFooterView = [self lv_getViewFromLuaByKey:@"Footer"];
}

-(void) reloadData {
    [super reloadData];
    [self getHeaderFooterFromDelegate];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"Callback" key2:@"End"];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"Callback" key2:@"End"];
}

#pragma -mark lvNewTextField
static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVTableView class]] ) {
        g_class = c;
    }
}

static int lvNewTableView (lv_State *L) {
    if( g_class == nil ){
        g_class = [LVTableView class];
    }
    LVTableView* tableView = [[g_class alloc] init:L];
    {
        NEW_USERDATA(userData, LVUserDataView);
        userData->view = CFBridgingRetain(tableView);
        tableView.lv_userData = userData;
        if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
            lv_pushvalue(L, 1);
            lv_udataRef(L, KEY_LUA_INFO );
            
            [tableView getHeaderFooterFromDelegate];
        }
        
        lvL_getmetatable(L, META_TABLE_UITableView );
        lv_setmetatable(L, -2);
    }
    UIView* lview = (__bridge UIView *)(L->lView);
    if( lview ){
        [lview addSubview:tableView];
    }
    return 1;
}

static int reloadData (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVTableView* tableView = (__bridge LVTableView *)(user->view);
        [tableView reloadData];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int setTableHeaderView (lv_State *L) {
    LVUserDataView * user1 = (LVUserDataView *)lv_touserdata(L, 1);
    LVUserDataView * user2 = (LVUserDataView *)lv_touserdata(L, 2);
    if( LVIsType(user1,LVUserDataView) && LVIsType(user2,LVUserDataView)  ){
        LVTableView* tableView = (__bridge LVTableView *)(user1->view);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            UIView* head = (__bridge LVTableView *)(user2->view);
            
            // 绑定 tableHeaderView
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, KEY_LUA_INFO );
            
            lv_pushstring(L, "Header");// key
            lv_pushUserdata(L, user2);// value
            lv_settable(L, -3);// registry[&Key] = tableView
            
            tableView.tableHeaderView = head;
            return 0;
        }
    }
    return 0;
}

static int setTableFooterView (lv_State *L) {
    LVUserDataView * user1 = (LVUserDataView *)lv_touserdata(L, 1);
    LVUserDataView * user2 = (LVUserDataView *)lv_touserdata(L, 2);
    if( LVIsType(user1,LVUserDataView) && LVIsType(user2,LVUserDataView)  ){
        LVTableView* tableView = (__bridge LVTableView *)(user1->view);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            UIView* head = (__bridge LVTableView *)(user2->view);
            
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, KEY_LUA_INFO );
            
            lv_pushstring(L, "Footer");// key
            lv_pushUserdata(L, user2);// value
            lv_settable(L, -3);// table[&Key] = value
            
            tableView.tableFooterView = head;
            return 0;
        }
    }
    return 0;
}

static int rectForSection (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataView) ){
        LVTableView* tableView = (__bridge LVTableView *)(user->view);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            int nargs = lv_gettop(L);
            if( nargs>=3 ){
                int section = lv_tonumber(L, 2);
                int row = lv_tonumber(L, 3);
                NSIndexPath* indexPath = [NSIndexPath indexPathForRow:row inSection:section];
                CGRect r = [tableView rectForRowAtIndexPath:indexPath];
                lv_pushnumber(L, r.origin.x);
                lv_pushnumber(L, r.origin.y);
                lv_pushnumber(L, r.size.width);
                lv_pushnumber(L, r.size.height);
                return 4;
            } else if (nargs>=2 ){
                int section = lv_tonumber(L, 2);
                CGRect r = [tableView rectForSection:section];
                lv_pushnumber(L, r.origin.x);
                lv_pushnumber(L, r.origin.y);
                lv_pushnumber(L, r.size.width);
                lv_pushnumber(L, r.size.height);
                return 4;
            }
        }
    }
    return 0;
}

static int delegate (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user && LVIsType(user, LVUserDataView) ){
        if ( lv_gettop(L)>=2 ) {
            lv_settop(L, 2);
            lv_udataRef(L, USERDATA_KEY_DELEGATE);
            LVTableView* tableView = (__bridge LVTableView *)(user->view);
            [tableView getHeaderFooterFromDelegate];
            return 1;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            return 1;
        }
    }
    return 0;
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewTableView);
        lv_setglobal(L, "UITableView");
    }
    const struct lvL_reg memberFunctions [] = {
        {"reloadData",    reloadData},
        {"setTableHeaderView", setTableHeaderView},
        {"setTableFooterView", setTableFooterView},
        
        {"setHeader", setTableHeaderView},
        {"setFooter", setTableFooterView},
        
        {"rectForSection", rectForSection},
        
        {"setDelegate", delegate},
        {"delegate", delegate},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UITableView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    return 1;
}


@end

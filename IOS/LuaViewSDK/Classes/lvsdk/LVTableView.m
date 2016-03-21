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
#import "UIScrollView+LuaView.h"
#import "LVScrollViewDelegate.h"
#import "LVTableViewDelegate.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVTableView ()
@property(nonatomic,strong) LVTableViewDelegate* tableViewDelegate;
@end

@implementation LVTableView

-(id) init:(lv_State*) l{
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) style:UITableViewStylePlain];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.tableViewDelegate = [[LVTableViewDelegate alloc] init:self];
        self.delegate = self.tableViewDelegate;
        self.dataSource = self.tableViewDelegate;
        self.backgroundColor = [UIColor clearColor];
        self.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.alwaysBounceVertical = YES; // 垂直总是有弹性动画
        self.scrollsToTop = NO;
    }
    return self;
}

-(void) dealloc{
}

-(void) setLvScrollViewDelegate:(id)lvScrollViewDelegate{
    _lvScrollViewDelegate = lvScrollViewDelegate;
    self.tableViewDelegate.delegate = lvScrollViewDelegate;
}

-(void) layoutSubviews{
    [super layoutSubviews];
    [self lv_runCallBack:STR_ON_LAYOUT];
}

- (UIView*) lv_getViewFromLuaByKey:(NSString*)key{
    lv_State* l = self.lv_lview.l;
    if( l ){
        int num = lv_gettop(l);
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l key1:key.UTF8String key2:NULL key3:NULL nargs:0 nrets:1 retType:LV_TNONE];
        if( lv_type(l, -1)==LV_TUSERDATA ) {
            LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(l, -1);
            if( LVIsType(user, View) ) {
                
                // 绑定 tableHeaderView
                lv_pushUserdata(l, self.lv_userData);
                lv_pushUDataRef(l, USERDATA_KEY_DELEGATE );
                
                lv_pushstring(l, [NSString stringWithFormat:@"%@.backup",key].UTF8String);// key
                lv_pushUserdata(l, user);// value
                lv_settable(l, -3);// registry[&Key] = tableView
                
                lv_settop(l, num);
                return (__bridge UIView *)(user->object);
            }
        }
        lv_settop(l, num);
    }
    return nil;
}

-(void) reloadData {
    [super reloadData];
}

#pragma -mark lvNewTextField
static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVTableView class]] ) {
        g_class = c;
    }
}

static int createTableView (lv_State *L , BOOL haveRefreshHead) {
    if( g_class == nil ){
        g_class = [LVTableView class];
    }
    LVTableView* tableView = [[g_class alloc] init:L];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(tableView);
        tableView.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UITableView );
        lv_setmetatable(L, -2);
        
        if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
            lv_pushvalue(L, 1);
            lv_udataRef(L, USERDATA_KEY_DELEGATE );
        }
    }
    LView* lview = (__bridge LView *)(L->lView);
    if( lview ){
        [lview containerAddSubview:tableView];
    }
    if( haveRefreshHead ) {
        [tableView lv_initRefreshHeader];
    }
    lv_pushUserdata(L, tableView.lv_userData);
    return 1;
}

static int lvCreateTableViewNoRefresh (lv_State *L) {
    return createTableView(L, NO);
}

static int lvCreateTableViewHaveRefresh (lv_State *L) {
    return createTableView(L, YES);
}


static int reload (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVTableView* tableView = (__bridge LVTableView *)(user->object);
        [tableView reloadData];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int setTableHeaderView (lv_State *L) {
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( LVIsType(user1, View) && LVIsType(user2, View)  ){
        LVTableView* tableView = (__bridge LVTableView *)(user1->object);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            UIView* head = (__bridge LVTableView *)(user2->object);
            
            // 绑定 tableHeaderView
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            
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
    LVUserDataInfo * user1 = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( LVIsType(user1, View) && LVIsType(user2, View)  ){
        LVTableView* tableView = (__bridge LVTableView *)(user1->object);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            UIView* head = (__bridge LVTableView *)(user2->object);
            
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            
            lv_pushstring(L, "Footer");// key
            lv_pushUserdata(L, user2);// value
            lv_settable(L, -3);// table[&Key] = value
            
            tableView.tableFooterView = head;
            return 0;
        }
    }
    return 0;
}

static int scrollToCell (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVTableView* tableView = (__bridge LVTableView *)(user->object);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            int nargs = lv_gettop(L);
            if( nargs>=3 ){
                int section = lv_tonumber(L, 2);
                int row = lv_tonumber(L, 3);
                CGFloat offsetY = 0;
                BOOL animation = YES;
                for( int i=4; i<=nargs; i++ ) {
                    if( nargs>=i && lv_type(L, i)==LV_TNUMBER ) {
                        offsetY = lv_tonumber(L, i);
                    }
                    if( nargs>=i && lv_type(L, i)==LV_TBOOLEAN ) {
                        animation = lv_toboolean(L, i);
                    }
                }
                
                NSIndexPath* indexPath = [NSIndexPath indexPathForRow:(row-1)*2 inSection:section-1];
                CGRect r = [tableView rectForRowAtIndexPath:indexPath];
                if( r.size.height>0 ) {
                    CGFloat y =  r.origin.y + offsetY;
                    [tableView setContentOffset:CGPointMake(0, y) animated:animation];
                }
                return 0;
            }
        }
    }
    return 0;
}

static int scrollToTop(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVTableView* tableView = (__bridge LVTableView *)(user->object);
        if( [tableView isKindOfClass:[LVTableView class]] ) {
            BOOL animation = YES;
            if( lv_gettop(L)>=2 ) {
                animation = lv_tonumber(L, 2);
            }
            [tableView scrollRectToVisible:CGRectMake(0, 0, 320, 10) animated:animation];
            return 0;
        }
    }
    return 0;
}

static int dividerHeight (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user && LVIsType(user, View) ){
        LVTableView* tableView = (__bridge LVTableView *)(user->object);
        if ( lv_gettop(L)>=2 ) {
            CGFloat h = lv_tonumber(L, 2);
            tableView.tableViewDelegate.dividerHeight = h;
            return 0;
        } else {
            CGFloat h = tableView.tableViewDelegate.dividerHeight;
            lv_pushnumber(L, h);
            return 1;
        }
    }
    return 0;
}
+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvCreateTableViewNoRefresh );
        lv_setglobal(L, "TableView");
    }
    {
        lv_pushcfunction(L, lvCreateTableViewHaveRefresh );
        lv_setglobal(L, "RefreshTableView");
    }
    const struct lvL_reg memberFunctions [] = {
        {"reload",    reload},
        
        {"header", setTableHeaderView},
        {"footer", setTableFooterView},
        
        {"dividerHeight", dividerHeight},
        
        {"scrollToCell", scrollToCell},
        {"scrollToTop",  scrollToTop},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UITableView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<TableView(0x%x) frame = %@; contentSize = %@>", (int)[self hash], NSStringFromCGRect(self.frame), NSStringFromCGSize(self.contentSize) ];
}

@end

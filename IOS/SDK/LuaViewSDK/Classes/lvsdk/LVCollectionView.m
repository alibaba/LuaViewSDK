/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCollectionView.h"
#import "LVCollectionViewCell.h"
#import "LView.h"
#import "LVBaseView.h"
#import "LVScrollView.h"
#import "UIScrollView+LuaView.h"
#import "LVCollectionViewDelegate.h"
#import "LVFlowLayout.h"
#import "LVHeads.h"

@interface LVCollectionView ()
@property (nonatomic,strong) LVCollectionViewDelegate* collectionViewDelegate;
@end


@implementation LVCollectionView

-(id) init:(lua_State*) l {
    LVFlowLayout* flowLayout = [[LVFlowLayout alloc] init];
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) collectionViewLayout:flowLayout];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.collectionViewDelegate = [[LVCollectionViewDelegate alloc] init:self];
        self.delegate = self.collectionViewDelegate;
        self.dataSource = self.collectionViewDelegate;
        self.backgroundColor = [UIColor clearColor];
        
        self.lvflowLayout = flowLayout;
        self.collectionViewDelegate.lvCollectionView = self;
        self.collectionViewDelegate.lvflowLayout = flowLayout;
        
        self.alwaysBounceVertical = YES; // 垂直总是有弹性动画
        self.scrollsToTop = NO;
        
        // 默认行间距都是0
        self.lvflowLayout.minimumLineSpacing = 0;
        self.lvflowLayout.minimumInteritemSpacing = 0;
    }
    return self;
}

-(void) setLvScrollViewDelegate:(id)lvScrollViewDelegate{
    _lvScrollViewDelegate = lvScrollViewDelegate;
    self.collectionViewDelegate.delegate = lvScrollViewDelegate;
}

-(void) dealloc{
}

-(void) reloadData{
    [super reloadData];
}

-(void) reloadDataASync{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self performSelector:@selector(reloadData) withObject:nil afterDelay:0.001 inModes:@[NSRunLoopCommonModes]];
    });
}

-(void) layoutSubviews{
    [super layoutSubviews];
    if ( self.lv_luaviewCore.l ) {
        lua_settop(self.lv_luaviewCore.l, 0);
    }
    [self lv_callLuaCallback:@STR_ON_LAYOUT];
}

// 重载以实现可能的定制需求, contentOffset
- (void) luaviewSetContentOffset:(CGPoint)contentOffset animated:(BOOL)animated{
    [self setContentOffset:contentOffset animated:animated];
}

// 重载以实现可能的定制需求, RectToVisible
- (void) luaviewScrollRectToVisible:(CGRect)rect animated:(BOOL)animated{
    [self scrollRectToVisible:rect animated:animated];
}

// 重载以实现可能的定制需求, scrollToTop
- (void) luaviewScrollToTopWithAnimated:(BOOL)animated{
    [self scrollRectToVisible:CGRectMake(0, 0, 320, 10) animated:animated];
}



#pragma -mark lvNewCollectionView
static int lvNewCollectionView(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCollectionView class]];

    LVCollectionView* collectionView = [[c alloc] init:L];
    [collectionView lv_initRefreshHeader];
    
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(collectionView);
    collectionView.lv_userData = userData;
    luaL_getmetatable(L, META_TABLE_UICollectionView );
    lua_setmetatable(L, -2);
    
    if ( lua_gettop(L)>=1 && lua_type(L, 1)==LUA_TTABLE ) {
        lua_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
    }
    
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    if( lview ){
        [lview containerAddSubview:collectionView];
    }
    return 1;
}

static int reload (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        //reload接口异步拉起，确保layout中也能调用reload
        [tableView reloadDataASync];
        lua_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int miniSpacing (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( lua_gettop(L)>=3 ) {
            CGFloat value1 = lua_tonumber(L, 2);
            CGFloat value2 = lua_tonumber(L, 3);
            tableView.lvflowLayout.minimumLineSpacing = value1;
            tableView.lvflowLayout.minimumInteritemSpacing = value2;
            return 0;
        } else if( lua_gettop(L)>=2 ) {
            CGFloat value1 = lua_tonumber(L, 2);
            tableView.lvflowLayout.minimumLineSpacing = value1;
            tableView.lvflowLayout.minimumInteritemSpacing = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.lvflowLayout.minimumLineSpacing;
            CGFloat value2 = tableView.lvflowLayout.minimumInteritemSpacing;
            lua_pushnumber(L, value1);
            lua_pushnumber(L, value2);
            return 2;
        }
    }
    return 0;
}

static int scrollDirection (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            int value1 = lua_tonumber(L, 2);
            tableView.lvflowLayout.scrollDirection = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.lvflowLayout.scrollDirection;
            lua_pushnumber(L, value1);
            return 1;
        }
    }
    return 0;
}

static int scrollToCell (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVCollectionView* collectionView = (__bridge LVCollectionView *)(user->object);
        if( [collectionView isKindOfClass:[LVCollectionView class]] ) {
            int nargs = lua_gettop(L);
            if( nargs>=3 ){
                int section = lua_tonumber(L, 2);
                int row = lua_tonumber(L, 3);
                CGFloat offsetY = 0;
                BOOL animation = YES;
                for( int i=4; i<=nargs; i++ ) {
                    if( nargs>=i && lua_type(L, i)==LUA_TNUMBER ) {
                        offsetY = lua_tonumber(L, i);
                    }
                    if( nargs>=i && lua_type(L, i)==LUA_TBOOLEAN ) {
                        animation = lua_toboolean(L, i);
                    }
                }
                
                int nativeSection = section-1;
                int nativeRow = row-1 ;
                if( 0<=nativeSection && nativeSection<collectionView.numberOfSections &&
                   0<=nativeRow && nativeRow<[collectionView numberOfItemsInSection:nativeSection] ) {
                    // 判断是否合法的section和row 再跳转就不会crash
                    NSIndexPath* indexPath = [NSIndexPath indexPathForRow:nativeRow inSection:nativeSection];
                    
                    UICollectionViewLayoutAttributes* att = [collectionView layoutAttributesForItemAtIndexPath:indexPath];
                    CGRect r = [att frame];
                    if( att && r.size.height>0 ) {
                        CGFloat y =  r.origin.y + offsetY;
                        CGSize contentSize = collectionView.contentSize;
                        CGRect bounds = collectionView.bounds;
                        // 越界检查
                        if( y + bounds.size.height > contentSize.height ) {
                            y = contentSize.height - bounds.size.height;
                        }
                        if( y < 0 ) {
                            y = 0;
                        }
                        [collectionView luaviewSetContentOffset:CGPointMake(0, y) animated:animation];
                    }
                }
                return 0;
            }
        }
    }
    return 0;
}

static int scrollToTop(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( [tableView isKindOfClass:[LVCollectionView class]] ) {
            BOOL animation = YES;
            if( lua_gettop(L)>=2 ) {
                animation = lua_tonumber(L, 2);
            }
            [tableView luaviewScrollToTopWithAnimated:animation];
            return 0;
        }
    }
    return 0;
}

+(NSString*) globalName{
    return @"CollectionView";
}

static int initParams (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        //reload接口异步拉起，确保layout中也能调用reload
        int ret =  lv_setCallbackByKey(L, nil, NO);
        [tableView reloadDataASync];
        return ret;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    [LVUtil reg:L clas:self cfunc:lvNewCollectionView globalName:globalName defaultName:[self globalName]];
    
    const struct luaL_Reg memberFunctions [] = {
        {"initParams",   initParams },
        // refreshEnable // IOS 为实现
        {"reload",    reload},// 安卓支持section row
        
        {"miniSpacing", miniSpacing},
        
        {"scrollDirection", scrollDirection},// for IOS
        
        
        {"scrollToCell", scrollToCell},
        {"scrollToTop",  scrollToTop},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UICollectionView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}



-(NSString*) description{
    return [NSString stringWithFormat:@"<CollectionView(0x%x) frame = %@; contentSize = %@>",
            (int)[self hash], NSStringFromCGRect(self.frame) , NSStringFromCGSize(self.contentSize)];
}

@end

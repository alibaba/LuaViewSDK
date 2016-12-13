//
//  LVCollectionView.m
//  LVSDK
//
//  Created by dongxicheng on 6/11/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVCollectionView.h"
#import "LVCollectionViewCell.h"
#import "LView.h"
#import "LVBaseView.h"
#import "LVScrollView.h"
#import "UIScrollView+LuaView.h"
#import "LVCollectionViewDelegate.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import "LVFlowLayout.h"


// lua 对应的数据 key


@interface LVCollectionView ()
@property (nonatomic,strong) LVCollectionViewDelegate* collectionViewDelegate;
@end


@implementation LVCollectionView

-(id) init:(lv_State*) l {
    LVFlowLayout* flowLayout = [[LVFlowLayout alloc] init];
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) collectionViewLayout:flowLayout];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.collectionViewDelegate = [[LVCollectionViewDelegate alloc] init:self];
        self.delegate = self.collectionViewDelegate;
        self.dataSource = self.collectionViewDelegate;
        self.backgroundColor = [UIColor clearColor];
        
        self.lvflowLayout = flowLayout;
        self.collectionViewDelegate.lvCollectionView = self;
        self.collectionViewDelegate.lvflowLayout = flowLayout;
        
        self.alwaysBounceVertical = YES; // 垂直总是有弹性动画
        self.scrollsToTop = NO;
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
    
    [self lv_callLuaByKey1:@STR_ON_LAYOUT];
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
static int lvNewCollectionView0 (lv_State *L, BOOL refresh) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCollectionView class]];

    LVCollectionView* tableView = [[c alloc] init:L];
    if( refresh ) {
        [tableView lv_initRefreshHeader];
    }

    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(tableView);
    tableView.lv_userData = userData;
    lvL_getmetatable(L, META_TABLE_UICollectionView );
    lv_setmetatable(L, -2);
    
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        lv_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
    }
    
    LView* lview = (__bridge LView *)(L->lView);
    if( lview ){
        [lview containerAddSubview:tableView];
    }
    return 1;
}

static int lvNewCollectionView (lv_State *L) {
    return lvNewCollectionView0(L, NO);
}

static int lvNewRefreshCollectionView (lv_State *L) {
    return lvNewCollectionView0(L, YES);
}

static int reload (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        //reload接口异步拉起，确保layout中也能调用reload
        [tableView reloadDataASync];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int miniSpacing (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( lv_gettop(L)>=3 ) {
            CGFloat value1 = lv_tonumber(L, 2);
            CGFloat value2 = lv_tonumber(L, 3);
            tableView.lvflowLayout.minimumLineSpacing = value1;
            tableView.lvflowLayout.minimumInteritemSpacing = value2;
            return 0;
        } else if( lv_gettop(L)>=2 ) {
            CGFloat value1 = lv_tonumber(L, 2);
            tableView.lvflowLayout.minimumLineSpacing = value1;
            tableView.lvflowLayout.minimumInteritemSpacing = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.lvflowLayout.minimumLineSpacing;
            CGFloat value2 = tableView.lvflowLayout.minimumInteritemSpacing;
            lv_pushnumber(L, value1);
            lv_pushnumber(L, value2);
            return 2;
        }
    }
    return 0;
}

static int scrollDirection (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            int value1 = lv_tonumber(L, 2);
            tableView.lvflowLayout.scrollDirection = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.lvflowLayout.scrollDirection;
            lv_pushnumber(L, value1);
            return 1;
        }
    }
    return 0;
}

static int scrollToCell (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVCollectionView* collectionView = (__bridge LVCollectionView *)(user->object);
        if( [collectionView isKindOfClass:[LVCollectionView class]] ) {
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

static int scrollToTop(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( LVIsType(user, View) ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->object);
        if( [tableView isKindOfClass:[LVCollectionView class]] ) {
            BOOL animation = YES;
            if( lv_gettop(L)>=2 ) {
                animation = lv_tonumber(L, 2);
            }
            [tableView luaviewScrollToTopWithAnimated:animation];
            return 0;
        }
    }
    return 0;
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    if( globalName==nil ) {
        globalName = @"CollectionView";
    }
    // CollectionView
    [LVUtil reg:L clas:self cfunc:lvNewCollectionView globalName:globalName defaultName:@"CollectionView"];
    
    // RefreshCollectionView
    NSString* refreshName = [NSString stringWithFormat:@"Refresh%@",globalName];
    [LVUtil reg:L clas:self cfunc:lvNewRefreshCollectionView globalName:refreshName defaultName:@"RefreshCollectionView"];
    
    const struct lvL_reg memberFunctions [] = {
        {"reload",    reload},
        
        {"miniSpacing", miniSpacing},
        
        {"scrollDirection", scrollDirection},
        
        
        {"scrollToCell", scrollToCell},
        {"scrollToTop",  scrollToTop},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UICollectionView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}



-(NSString*) description{
    return [NSString stringWithFormat:@"<CollectionView(0x%x) frame = %@; contentSize = %@>",
            (int)[self hash], NSStringFromCGRect(self.frame) , NSStringFromCGSize(self.contentSize)];
}

@end

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


// lua 对应的数据 key


@interface LVCollectionView ()
@property (nonatomic,strong) UICollectionViewFlowLayout *flowLayout;
@property (nonatomic,strong) LVCollectionViewDelegate* collectionViewDelegate;
@end


@implementation LVCollectionView

-(id) init:(lv_State*) l identifierArray:(NSArray*) identifierArray {
    UICollectionViewFlowLayout* flowLayout = [[UICollectionViewFlowLayout alloc] init];
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) collectionViewLayout:flowLayout];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.collectionViewDelegate = [[LVCollectionViewDelegate alloc] init:self];
        self.delegate = self.collectionViewDelegate;
        self.dataSource = self.collectionViewDelegate;
        self.backgroundColor = [UIColor clearColor];
        
        self.flowLayout = flowLayout;
        
        [self registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:DEFAULT_CELL_IDENTIFIER];
        for( NSString* identifier in identifierArray ){
            [self registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:identifier];
        }
        self.alwaysBounceVertical = YES; // 垂直总是有弹性动画
        self.scrollsToTop = NO;
    }
    return self;
}

-(void) setLvScrollViewDelegate:(id)lvScrollViewDelegate{
    _lvScrollViewDelegate = lvScrollViewDelegate;
    self.collectionViewDelegate.delegate = lvScrollViewDelegate;
}


-(void) registerClass:(Class)cellClass forCellWithReuseIdentifier:(NSString *)identifier{
    [super registerClass:cellClass forCellWithReuseIdentifier:identifier];
    if( self.collectionViewDelegate.identifierDic == nil ) {
        self.collectionViewDelegate.identifierDic = [[NSMutableDictionary alloc] init];
    }
    [self.collectionViewDelegate.identifierDic setValue:identifier forKey:identifier];
}

-(void) dealloc{
}

-(void) layoutSubviews{
    [super layoutSubviews];
    
    [self lv_runCallBack:STR_ON_LAYOUT];
}

static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVCollectionView class]] ) {
        g_class = c;
    }
}

#pragma -mark lvNewCollectionView
static int lvNewCollectionView0 (lv_State *L, BOOL refresh) {
    if( g_class == nil ) {
        g_class = [LVCollectionView class];
    }
    BOOL haveArgs = NO;
    NSArray* identifierArray = nil;
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        haveArgs = YES;
    }
    if( haveArgs ) {
        lv_getfield(L, 1, "Cell");
        identifierArray = lv_luaTableKeys(L, -1);
    }
    LVCollectionView* tableView = [[g_class alloc] init:L identifierArray:identifierArray];
    if( refresh ) {
        [tableView lv_initRefreshHeader];
    }

    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(tableView);
    tableView.lv_userData = userData;
    lvL_getmetatable(L, META_TABLE_UICollectionView );
    lv_setmetatable(L, -2);
    
    if ( haveArgs ) {
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
        [tableView reloadData];
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
            tableView.flowLayout.minimumLineSpacing = value1;
            tableView.flowLayout.minimumInteritemSpacing = value2;
            return 0;
        } else if( lv_gettop(L)>=2 ) {
            CGFloat value1 = lv_tonumber(L, 2);
            tableView.flowLayout.minimumLineSpacing = value1;
            tableView.flowLayout.minimumInteritemSpacing = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.flowLayout.minimumLineSpacing;
            CGFloat value2 = tableView.flowLayout.minimumInteritemSpacing;
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
            tableView.flowLayout.scrollDirection = value1;
            return 0;
        } else {
            CGFloat value1 = tableView.flowLayout.scrollDirection;
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
                        [collectionView setContentOffset:CGPointMake(0, y) animated:animation];
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
            [tableView scrollRectToVisible:CGRectMake(0, 0, 320, 10) animated:animation];
            return 0;
        }
    }
    return 0;
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewCollectionView);
        lv_setglobal(L, "CollectionView");
    }
    {
        lv_pushcfunction(L, lvNewRefreshCollectionView);
        lv_setglobal(L, "RefreshCollectionView");
    }
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

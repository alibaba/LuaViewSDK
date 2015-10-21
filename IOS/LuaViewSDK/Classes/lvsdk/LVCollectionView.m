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

#define IDENTIFIER "Id"

// lua 对应的数据 key

#define DEFAULT_CELL_IDENTIFIER  @"LVCollectionCell.default.identifier"

@interface LVCollectionView ()
@property (nonatomic,strong) UICollectionViewFlowLayout *flowLayout;
@property (nonatomic,strong) NSMutableDictionary* identifierDic;
@end


@implementation LVCollectionView

-(id) init:(lv_State*) l identifierArray:(NSArray*) identifierArray {
    UICollectionViewFlowLayout* flowLayout = [[UICollectionViewFlowLayout alloc] init];
    self = [super initWithFrame:CGRectMake(0, 0, 0, 0) collectionViewLayout:flowLayout];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.delegate = self;
        self.dataSource = self;
        self.backgroundColor = [UIColor clearColor];
        
        self.flowLayout = flowLayout;
        
        [self registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:DEFAULT_CELL_IDENTIFIER];
        for( NSString* identifier in identifierArray ){
            [self registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:identifier];
        }
    }
    return self;
}

-(void) registerClass:(Class)cellClass forCellWithReuseIdentifier:(NSString *)identifier{
    [super registerClass:cellClass forCellWithReuseIdentifier:identifier];
    if( self.identifierDic == nil ) {
        self.identifierDic = [[NSMutableDictionary alloc] init];
    }
    [self.identifierDic setValue:identifier forKey:identifier];
}

-(void) dealloc{
}

- (UICollectionViewCell*) collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
    identifier = self.identifierDic[identifier];
    if( identifier == nil ){
        identifier = DEFAULT_CELL_IDENTIFIER;
    }
    LVCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    LView* lview = self.lv_lview;
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
            lv_pushnumber(l, indexPath.section+1);//arg2: section
            lv_pushnumber(l, indexPath.row+1);//arg3: row
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:1];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, indexPath.section+1);//arg2: section
            lv_pushnumber(l, indexPath.row+1);//arg3: row
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:1];
        }
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
    return cell;
}

// section数量
- (NSInteger) numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_pushUserdata(l, self.lv_userData);
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
- (NSInteger) collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    lv_State* l = self.lv_lview.l;
    if( l ){
        // args
        lv_pushnumber(l, section+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
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
#pragma mark --UICollectionViewDelegateFlowLayout
//定义每个UICollectionView 的大小
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
    if( identifier ) {
        CGSize size = [self returnSizeCallByKey1:"Cell" key2:identifier.UTF8String key3:"Size" section:indexPath.section row:indexPath.row];
        return size;
    } else {
        return CGSizeMake(20, 20);
    }
}
//定义每个UICollectionView 的间距
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    UIEdgeInsets insets = [self callFunction4:"Section" key2:"EdgeInsets" section:section row:0];
    //insets.top = self.flowLayout.minimumLineSpacing;
    return insets;
}
//定义每个UICollectionView 纵向的间距
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    //    return self.flowLayout.minimumLineSpacing;
    CGFloat spacing = [self callReturnNumberKey1:"Section" key2:"Spacing" section:section];
    return spacing;
}

- (CGFloat) callReturnNumberKey1:(const char*) funcName key2:(const char*) key2 section:(NSInteger) section {
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:funcName key2:key2 nargs:1 nrets:1] ==0 ) {
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

- (CGSize) returnSizeCallByKey1:(const char*) functionName key2:(const char*)key2 key3:(const char*)key3 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 key3:key3 nargs:2 nrets:2] ==0 ) {
            CGSize size = {0};
            if( lv_type(l, -1) ==LV_TNIL ) {
                size.width = self.frame.size.width;
                size.height = lv_tonumber(l, -2);
            } else{
                size.width = lv_tonumber(l, -2);
                size.height = lv_tonumber(l, -1);
            }
            return size;
        }
    }
    return CGSizeMake(0, 0);
}
- (UIEdgeInsets) callFunction4:(const char*) functionName key2:(const char*)key2 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 nargs:2 nrets:4] ==0 ) {
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

- (void) callByKey1:(const char*) functionName key2:(const char*)key2 key3:(const char*)key3 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 key3:key3 nargs:2 nrets:0]==0 ) {
        }
    }
}

- (void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    lv_State* l = self.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
        if ( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            [self callByKey1:"Cell" key2:identifier.UTF8String key3:"ClickCallback" section:indexPath.section row:indexPath.row];
        }
    }
}

// 回调脚本返回一个用户数据
- (LVUserDataView *) callReturnUserDataFunction:(const char*) key1 key2:(const char*)key2 section:(NSInteger) section {
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        // table
        lv_pushUserdata(l, self.lv_userData);
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

-(void) layoutSubviews{
    [super layoutSubviews];
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_settop(l, 0);
        lv_checkstack(l, 12);
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        [LVUtil call:l  key1:"LayoutSubviews" key2:NULL nargs:0 nrets:0];
    }
}

static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVCollectionView class]] ) {
        g_class = c;
    }
}

#pragma -mark lvNewCollectionView
static int lvNewCollectionView (lv_State *L) {
    if( g_class == nil ) {
        g_class = [LVCollectionView class];
    }
    BOOL haveArgs = NO;
    NSArray* identifierArray = nil;
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        haveArgs = YES;
    }
    if( haveArgs ) {
        lv_pushstring(L, "Cell");
        lv_gettable(L, 1);
        identifierArray = lv_luaTableKeys(L, -1);
    }
    LVCollectionView* tableView = [[g_class alloc] init:L identifierArray:identifierArray];

    NEW_USERDATA(userData, LVUserDataView);
    userData->view = CFBridgingRetain(tableView);
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

//static int delegate (lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        if ( lv_gettop(L)>=2 ) {
//            NSArray* identifierArray = nil;
//            if ( lv_gettop(L)>=2 && lv_type(L, 2)==LV_TTABLE ) {
//                lv_pushstring(L, "Cell");
//                lv_gettable(L, 2);
//                identifierArray = lv_luaTableKeys(L, -1);
//            }
//            lv_settop(L, 2);
//            lv_udataRef(L, USERDATA_KEY_DELEGATE);
//            
//            if ( identifierArray ) {
//                LVCollectionView* collectionView = (__bridge LVCollectionView *)(user->view);
//                for( NSString* identifier in identifierArray ){
//                    [collectionView registerClass:[LVCollectionViewCell class] forCellWithReuseIdentifier:identifier];
//                }
//            }
//            return 1;
//        } else {
//            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
//            return 1;
//        }
//    }
//    return 0;
//}

static int reloadData (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->view);
        [tableView reloadData];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int miniSpacing (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->view);
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
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->view);
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

static int rectForSection (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( LVIsType(user,LVUserDataView) ){
        LVCollectionView* tableView = (__bridge LVCollectionView *)(user->view);
        if( [tableView isKindOfClass:[LVCollectionView class]] ) {
            int nargs = lv_gettop(L);
            if( nargs>=3 ){
                int section = lv_tonumber(L, 2)-1;
                int row = lv_tonumber(L, 3)-1;
                NSIndexPath* indexPath = [NSIndexPath indexPathForRow:row inSection:section];
                CGRect r = [tableView layoutAttributesForItemAtIndexPath:indexPath].frame;
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

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewCollectionView);
        lv_setglobal(L, "CollectionView");
    }
    const struct lvL_reg memberFunctions [] = {
        {"reload",    reloadData},
        {"rectForSection", rectForSection},
        
        {"miniSpacing", miniSpacing},
        
//        {"delegate", delegate},
        
        {"scrollDirection", scrollDirection},
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

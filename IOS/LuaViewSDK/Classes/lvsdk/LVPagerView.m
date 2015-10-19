//
//  LVPageView.m
//  LuaViewSDK
//
//  Created by dongxicheng on 9/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVPagerView.h"
#import "LView.h"
#import "LVBaseView.h"
#import "LVScrollView.h"
#import "LVPagerViewCell.h"


#define IDENTIFIER "Id"

// lua 对应的数据 key
#define KEY_LUA_INFO  1

#define DEFAULT_CELL_IDENTIFIER  @"LVCollectionCell.default.identifier"

@interface LVPagerView ()
@property (nonatomic,strong) UICollectionViewFlowLayout *flowLayout;
@property (nonatomic,strong) NSMutableDictionary* identifierDic;
@property (nonatomic,strong) NSMutableArray* cellArray;

@property (nonatomic,assign) NSInteger currentPageIndex;

@end


@implementation LVPagerView

-(id) init:(lv_State*) l {
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.backgroundColor = [UIColor clearColor];
        self.cellArray = [[NSMutableArray alloc] init];
        self.pagingEnabled = YES;
        self.showsHorizontalScrollIndicator = NO;
        self.delegate = self;
        self.currentPageIndex = -1;
    }
    return self;
}


-(void) createAllCell {
    NSInteger num = [self numberOfPagesInPageView];
    if( num<self.cellArray.count ) {
        for( ; num<self.cellArray.count; ){
            UIView* view = self.cellArray.lastObject;
            [self.cellArray removeLastObject];
            [view removeFromSuperview];
        }
    } else if (num>self.cellArray.count ) {
        for( int i=((int)self.cellArray.count); i<num; i++ ) {
            LVPagerViewCell* view = [[LVPagerViewCell alloc] init];
            [self.cellArray addObject:view];
        }
    }
    [self resetCellFrame];
}

-(void) resetCellFrame{
    for( int i=0; i<self.cellArray.count; i++ ) {
        UIView* view = self.cellArray[i];
        CGRect r = self.frame;
        view.frame =CGRectMake(i*r.size.width, 0, r.size.width, r.size.height);
    }
    self.contentSize = CGSizeMake(self.cellArray.count*self.frame.size.width, 0);
}

-(void) setFrame:(CGRect)frame{
    [super setFrame:frame];
    [self resetCellFrame];
}

-(void) checkCellVisible{
    CGPoint p =  self.contentOffset;
    CGRect r0 = self.frame;
    r0.origin = p;
    for( int i=0; i<self.cellArray.count; i++ ){
        UIView* view = self.cellArray[i];
        if( view ) {
            CGRect r = view.frame;
            if(  CGRectIntersectsRect(r, r0) ){
                if( view.superview!= self ) {
                    [self cellForItemAtIndex:i];
                    [self addSubview:view];
                }
            } else {
                [view removeFromSuperview];
            }
        }
    }
}

-(void) layoutSubviews{
    [super layoutSubviews];
    
    [self checkCellVisible];
    
    lv_State* l = self.lv_lview.l;
    if( l ){
        lv_settop(l, 0);
        lv_checkstack(l, 12);
        // table
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        [LVUtil call:l  key1:"LayoutSubviews" key2:NULL nargs:0 nrets:0];
    }
}

-(void) dealloc{
}

- (LVPagerViewCell*) cellOfIndex:(int) index{
    if( index>=0 && index<self.cellArray.count ) {
        return self.cellArray[index];
    }
    return nil;
}

- (LVPagerViewCell*) cellForItemAtIndex:(int)indexPath {
    LVPagerViewCell* cell = [self cellOfIndex:indexPath];
    LView* lview = self.lv_lview;
    lv_State* l = lview.l;
    lview.conentView = cell;
    lview.contentViewIsWindow = NO;
    if ( l ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, indexPath+1);//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Pages" key2:"Init" key3:NULL nargs:2 nrets:0];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, indexPath+1);//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, KEY_LUA_INFO);
            [LVUtil call:l key1:"Pages" key2:"Layout" key3:NULL nargs:2 nrets:0];
        }
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
    return cell;
}

// section数量
- (NSInteger) numberOfPagesInPageView{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, KEY_LUA_INFO);
        if(  [LVUtil call:l key1:"PageCount" key2:NULL nargs:0 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num;
            }
        }
        return 1;
    }
    return 1;
}


static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVPagerView class]] ) {
        g_class = c;
    }
}

#pragma -mark lvNewCollectionView
static int lvNewPageView (lv_State *L) {
    if( g_class == nil ) {
        g_class = [LVPagerView class];
    }
    BOOL haveArgs = NO;
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        haveArgs = YES;
    }
    LVPagerView* pageView = [[g_class alloc] init:L];
    
    NEW_USERDATA(userData, LVUserDataView);
    userData->view = CFBridgingRetain(pageView);
    pageView.lv_userData = userData;
    lvL_getmetatable(L, META_TABLE_UIPageView );
    lv_setmetatable(L, -2);
    
    if ( haveArgs ) {
        int stackNum = lv_gettop(L);
        lv_pushvalue(L, 1);
        lv_udataRef(L, KEY_LUA_INFO );
        
        [pageView createAllCell];
        lv_settop(L, stackNum);
    }
    
    LView* lview = (__bridge LView *)(L->lView);
    if( lview ){
        [lview containerAddSubview:pageView];
    }
    return 1;
}

-(void) reloadData{
    [self createAllCell];
}

static int reloadData (lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* pageView = (__bridge LVPagerView *)(user->view);
        [pageView reloadData];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int showScrollBar(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                BOOL yes1 = lv_toboolean(L, 2);
                view.showsHorizontalScrollIndicator = yes1;
                return 0;
            } else {
                lv_pushboolean(L, view.showsHorizontalScrollIndicator );
                return 1;
            }
        }
    }
    return 0;
}

static int setCurrentPage(lv_State *L) {
    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* view = (__bridge LVPagerView *)(user->view);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                int pageIndex = lv_tonumber(L, 2);
                pageIndex -= 1;
                float offsetX = pageIndex*view.frame.size.width ;
                if( offsetX<=0 ){
                    offsetX = 0;
                }
                float maxOffset = view.contentSize.width-view.frame.size.width;
                if( offsetX > maxOffset ){
                    offsetX = maxOffset;
                }
                BOOL animated = YES;
                if( lv_gettop(L)>=3 ) {
                    animated = lv_toboolean(L, 3);
                }
                [view setContentOffset:CGPointMake(offsetX, 0) animated:animated];
                lv_settop(L, 1);
                return 1;
            } else {
                lv_pushnumber( L, view.currentPageIndex+1 );
                return 1;
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
            LVPagerView* tableView = (__bridge LVPagerView *)(user->view);
            [tableView createAllCell];
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
        lv_pushcfunction(L, lvNewPageView);
        lv_setglobal(L, "PagerView");
    }
    const struct lvL_reg memberFunctions [] = {
        {"reload",    reloadData},
        {"showScrollBar",     showScrollBar },
        
        {"delegate",     delegate },
        
        {"currentPage",     setCurrentPage },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UIPageView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


- (void) callLuaWithScrolling{
    float offsetX = self.contentOffset.x;
    float pageWidth = self.frame.size.width;
    float pageIndex = offsetX/pageWidth;
    self.currentPageIndex = (int)pageIndex;
    
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_checkStack32(l);
        double intPart = 0;
        double floatPart = modf( pageIndex, &intPart);
        lv_pushnumber(l, (int)pageIndex + 1 );
        lv_pushnumber(l, floatPart);
        lv_pushnumber(l, offsetX - intPart*pageWidth);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:"Callback" key2:"Scrolling" nargs:3 nrets:0];
    }
}

- (void) callLuaWithScrollEnd{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_checkStack32(l);
        lv_pushnumber(l, self.currentPageIndex + 1 );
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:"Callback" key2:"ScrollEnd" nargs:1 nrets:0];
    }
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self callLuaWithNoArgs:@"Callback" key2:@"ScrollBegin"];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    //[self callLuaWithNoArgs:@"Callback" key2:@"BeginDecelerating"];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnd];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnd];
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<PagerView(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}
@end

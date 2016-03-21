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
#import "LVPagerIndicator.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

static inline NSInteger mapPageIdx(NSInteger pageIdx){
    return pageIdx + 1;
}

static inline NSInteger unmapPageIdx(NSInteger pageIdx){
    return pageIdx - 1;
}

#define IDENTIFIER "Id"

// lua 对应的数据 key
#define USERDATA_KEY_DELEGATE  1

#define DEFAULT_CELL_IDENTIFIER  @"LVCollectionCell.default.identifier"

@interface LVPagerView ()
@property (nonatomic,strong) UICollectionViewFlowLayout *flowLayout;
@property (nonatomic,strong) NSMutableDictionary* identifierDic;
@property (nonatomic,strong) NSMutableArray* cellArray;

@property (nonatomic,assign) NSInteger pageIdx;
@property (nonatomic,weak) LVPagerIndicator* pagerIndicator;
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
        self.pageIdx = 0;
        self.scrollsToTop = NO;
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
    self.pagerIndicator.numberOfPages = self.cellArray.count;
    
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
                    [self cellInitAtPageIdx:i];
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
    
    [self lv_runCallBack:STR_ON_LAYOUT];
}

-(void) dealloc{
}

- (LVPagerViewCell*) cellOfPageIdx:(int) index{
    if( index>=0 && index<self.cellArray.count ) {
        return self.cellArray[index];
    }
    return nil;
}

- (LVPagerViewCell*) cellInitAtPageIdx:(int)pageIdx {
    LVPagerViewCell* cell = [self cellOfPageIdx:pageIdx];
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
            lv_pushnumber(l, mapPageIdx(pageIdx) );//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Pages" key2:"Init" key3:NULL nargs:2 nrets:0 retType:LV_TNONE];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, mapPageIdx(pageIdx) );//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Pages" key2:"Layout" key3:NULL nargs:2 nrets:0 retType:LV_TNONE];
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
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"PageCount" key2:NULL key3:NULL nargs:0 nrets:1 retType:LV_TNUMBER] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num;
            }
        }
        return 1;
    }
    return 1;
}

//- (LVPagerIndicator*) callLuaGetIndicator{
//    lv_State* L = self.lv_lview.l;
//    if( L && self.lv_userData ){
//        lv_pushUserdata(L, self.lv_userData);
//        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
//        if(  [LVUtil call:L key1:"Indicator" key2:NULL nargs:0 nrets:1] ==0 ) {
//            if( lv_type(L, -1)==LV_TUSERDATA ) {
//                
//                LVUserDataView * user2 = (LVUserDataView *)lv_touserdata(L, -1);
//                if( LVIsType(user2, LVUserDataView) ) {
//                    lv_checkstack(L, 8);
//                    lv_pushvalue(L, 1);
//                    lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
//                    lv_pushvalue(L, -2);// value
//                    lv_setfield(L, -2, "Indicator.object");
//                    
//                    LVPagerIndicator* pagerIndicator = (__bridge LVPagerIndicator *)(user2->view);
//                    if( [pagerIndicator isKindOfClass:[LVPagerIndicator class]] ) {
//                        [self setIndicator:pagerIndicator];// 设置Indicator
//                        return pagerIndicator;
//                    }
//                }
//            }
//        }
//    }
//    return nil;
//}


-(void) setIndicator:(LVPagerIndicator*) indicator{
    self.pagerIndicator = indicator;
    self.pagerIndicator.pagerView = self;
    self.pagerIndicator.numberOfPages = self.cellArray.count;
    if( self.pageIdx>=0 && self.pageIdx<self.cellArray.count ) {
        self.pagerIndicator.currentPage = self.pageIdx;
    }
}

-(void) setCurrentPageIdx:(NSInteger) pageIdx animation:(BOOL) animation{
    float offsetX = pageIdx * self.frame.size.width ;
    if( offsetX<=0 ){
        offsetX = 0;
    }
    float maxOffset = self.contentSize.width - self.frame.size.width;
    if( offsetX > maxOffset ){
        offsetX = maxOffset;
    }
    [self setContentOffset:CGPointMake(offsetX, 0) animated:animation];
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
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        LVPagerView* pageView = [[g_class alloc] init:L];
        
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(pageView);
        pageView.lv_userData = userData;
        lvL_getmetatable(L, META_TABLE_UIPageView );
        lv_setmetatable(L, -2);
        
        LView* lview = (__bridge LView *)(L->lView);
        if( lview ){
            [lview containerAddSubview:pageView];
        }
        
        int stackNum = lv_gettop(L);
        lv_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
        
        [pageView createAllCell];
        lv_settop(L, stackNum);
        
        lv_pushUserdata(L, pageView.lv_userData);
        return 1;
    }
    return 0;
}

-(void) reloadData{
    for( UIView* view in self.cellArray ) {
        [view removeFromSuperview];
    }
    [self createAllCell];
    [self checkCellVisible];
}

static int reload (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* pageView = (__bridge LVPagerView *)(user->object);
        [pageView reloadData];
        lv_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int showScrollBar(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
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
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* view = (__bridge LVPagerView *)(user->object);
        if( [view isKindOfClass:[UIScrollView class]] ){
            if( lv_gettop(L)>=2 ) {
                int luaPageIdx = lv_tonumber(L, 2);
                BOOL animated = YES;
                if( lv_gettop(L)>=3 ) {
                    animated = lv_toboolean(L, 3);
                }
                [view setCurrentPageIdx:unmapPageIdx(luaPageIdx) animation:animated];
                lv_settop(L, 1);
                return 1;
            } else {
                NSInteger currentPageIdx = view.pageIdx;
                lv_pushnumber( L, mapPageIdx(currentPageIdx) );
                return 1;
            }
        }
    }
    return 0;
}

static int indicator(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* view = (__bridge LVPagerView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            if ( lv_type(L, 2)==LV_TNIL ) {
                view.pagerIndicator = nil;
                [view setIndicator:nil];// 设置Indicator
                lv_pushvalue(L, 1);
                lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
                lv_pushnil(L);// value
                lv_setfield(L, -2, "Indicator");
            } else {
                LVUserDataInfo * user2 = (LVUserDataInfo *)lv_touserdata(L, 2);
                if( LVIsType(user2, View) ) {
                    LVPagerIndicator* pagerIndicator = (__bridge LVPagerIndicator *)(user2->object);
                    if( [pagerIndicator isKindOfClass:[LVPagerIndicator class]] ) {
                        [view setIndicator:pagerIndicator];// 设置Indicator
                        lv_pushvalue(L, 1);
                        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
                        lv_pushvalue(L, 2);// value
                        lv_setfield(L, -2, "Indicator");
                    }
                }
            }
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            lv_getfield(L, -2, "Indicator");
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
        {"reload",    reload},
        {"showScrollBar",     showScrollBar },
        {"currentPage",     setCurrentPage },
        {"indicator", indicator},
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
    self.pageIdx = (int)pageIndex;
    self.pagerIndicator.currentPage = (int)(pageIndex+0.5);
    
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
        
        [LVUtil call:l key1:STR_CALLBACK key2:"Scrolling" key3:NULL nargs:3 nrets:0 retType:LV_TNONE];
    }
}

- (void) callLuaWithScrollEnded{
    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_checkStack32(l);
        lv_pushnumber(l, mapPageIdx(self.pageIdx) );
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:STR_CALLBACK key2:"ScrollEnd" key3:NULL nargs:1 nrets:0 retType:LV_TNONE];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    [self lv_callLuaByKey1:@STR_CALLBACK key2:@"ScrollBegin" argN:0];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    //[self lv_callLuaByKey1:@STR_CALLBACK key2:@"BeginDecelerating"];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnded];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnded];
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<PagerView(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}
@end

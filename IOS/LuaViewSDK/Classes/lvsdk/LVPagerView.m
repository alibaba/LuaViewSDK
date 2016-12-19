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


@property (nonatomic,assign) NSInteger pageIdx0;
@property (nonatomic,assign) NSInteger pageIdx;
@property (nonatomic,weak) LVPagerIndicator* pagerIndicator;

@property (nonatomic,strong) NSTimer *timer;

@property (nonatomic,assign) CGPoint nextOffset;
@property (nonatomic,assign) BOOL looping;
@property (nonatomic,assign) NSInteger isScrollEndTimes;

@property (nonatomic,strong) UIScrollView *scrollview;
@property (nonatomic,assign) CGFloat sideLeft;
@property (nonatomic,assign) CGFloat sideRight;
@end


@implementation LVPagerView

-(id) init:(lv_State*) l {
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.backgroundColor = [UIColor clearColor];
        self.cellArray = [[NSMutableArray alloc] init];
        self.scrollview = ({
            UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 0, 0)];
            scroll.pagingEnabled = YES;
            scroll.clipsToBounds = NO;
            scroll;
        })                                                                                      ;
        
        [self addSubview:self.scrollview];
        self.clipsToBounds = YES;
        
        self.scrollview.pagingEnabled = YES;
        self.scrollview.showsHorizontalScrollIndicator = NO;
        self.scrollview.delegate = self;
        self.scrollview.scrollsToTop = NO;
        self.pageIdx = 0;
        self.isScrollEndTimes = 0;
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
            view.index = i;
            [self.cellArray addObject:view];
        }
    }
    [self resetCellFrame];
    self.pagerIndicator.numberOfPages = self.cellArray.count;
    
}

// index 映射 xindex
-(NSInteger) index2xindex:(NSInteger) i{
    NSUInteger count = self.cellArray.count;
    if( count>0 ) {
        i += self.pageIdx0;
        i += 1000 * count;
        return i%count;
    }
    return i;
}

// xindex 映射 index
-(NSInteger) xindex2index:(NSInteger) i{
    NSUInteger count = self.cellArray.count;
    if( count>0 ) {
        i -= self.pageIdx0;
        i += 1000 * count;
        return i%count;
    }
    return i;
}

-(void) resetCellFrame{
    {// ScrollView的滚动大小
        CGRect rect = self.bounds;
        rect.origin.x = self.sideLeft;
        rect.size.width -= self.sideLeft + self.sideRight;
        self.scrollview.frame = rect;
    }
    NSUInteger count = self.cellArray.count;
    CGSize size = self.scrollview.frame.size;
    for( NSUInteger i=0; i<count; i++ ) {
        UIView* view = self.cellArray[i];
        CGFloat xIndex = [self index2xindex:i];
        CGFloat x = xIndex*size.width;
        view.frame = CGRectMake(x, 0, size.width, size.height);
    }
    self.scrollview.contentSize = CGSizeMake( count * size.width, 0);
}

-(void) moveCenter{
    CGFloat width = self.scrollview.bounds.size.width;
    CGPoint p = self.scrollview.contentOffset;
    if( self.looping ) {
        if( self.cellArray.count>2 ) {
            if( p.x < width*0.5 ) {
                self.pageIdx0 += 1;
                self.scrollview.contentOffset = CGPointMake( p.x + width, 0);
                [self resetCellFrame];
            } else if( p.x> width*1.5 ){
                self.pageIdx0 -= 1;
                self.scrollview.contentOffset = CGPointMake( p.x - width, 0);
                [self resetCellFrame];
            }
        } else {
            if( p.x< 0 ) {
                self.pageIdx0 += 1;
                self.scrollview.contentOffset = CGPointMake( p.x + width, 0);
                [self resetCellFrame];
            } else if( p.x>=width ){
                self.pageIdx0 -= 1;
                self.scrollview.contentOffset = CGPointMake( p.x - width, 0);
                [self resetCellFrame];
            }
        }
    }
}

-(void) setFrame:(CGRect)frame{
    [super setFrame:frame];
    [self resetCellFrame];
}

-(void) checkCellVisible{
    CGPoint p =  self.scrollview.contentOffset;
    CGRect r0 = self.scrollview.bounds;
    r0.origin = p;
    r0.origin.x -= self.sideLeft;
    r0.size.width += self.sideLeft + self.sideRight;
    for( int i=0; i<self.cellArray.count; i++ ){
        UIView* view = self.cellArray[i];
        if( view ) {
            CGRect r = view.frame;
            if(  CGRectIntersectsRect(r, r0) ){
                if( view.superview!= self.scrollview ) {
                    [self cellLayoutAtPageIdx:i];
                    [self.scrollview addSubview:view];
                }
            } else {
                [view removeFromSuperview];
            }
        }
    }
}

-(void) layoutSubviews{
    [super layoutSubviews];
    [self moveCenter];
    [self checkCellVisible];
    
    [self lv_callLuaByKey1:@STR_ON_LAYOUT];
}

-(void) dealloc{
}

- (LVPagerViewCell*) cellOfPageIdx:(int) index{
    if( index>=0 && index<self.cellArray.count ) {
        return self.cellArray[index];
    }
    return nil;
}

- (LVPagerViewCell*) cellLayoutAtPageIdx:(int)pageIdx {
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

-(void) setPageIndicatorIdx:(NSInteger)pageIdx{
    if( pageIdx>=0 && pageIdx<self.cellArray.count ) {
        self.pagerIndicator.currentPage = pageIdx;
    }
}


-(void) setIndicator:(LVPagerIndicator*) indicator{
    self.pagerIndicator = indicator;
    self.pagerIndicator.pagerView = self;
    self.pagerIndicator.numberOfPages = self.cellArray.count;
    [self setPageIndicatorIdx:self.pageIdx];
}

-(void) setCurrentPageIdx:(NSInteger) pageIdx animation:(BOOL) animation{
    float offsetX = [self index2xindex:pageIdx] * self.scrollview.frame.size.width ;
    if( offsetX<=0 ){
        offsetX = 0;
    }
    float maxOffset = self.scrollview.contentSize.width - self.scrollview.frame.size.width;
    if( offsetX > maxOffset ){
        offsetX = maxOffset;
    }
    //[self setContentOffset:CGPointMake(offsetX, 0) animated:animation];
    self.nextOffset = CGPointMake(offsetX, 0);
    if( animation ) {
        [self performSelectorOnMainThread:@selector(changeOffsetWithAnimation:) withObject:nil waitUntilDone:NO];
    } else {
        [self performSelectorOnMainThread:@selector(changeOffsetNoAnimation:) withObject:nil waitUntilDone:NO];
    }
}

// 有动画
-(void) changeOffsetWithAnimation:(NSNumber*) value{
    [self.scrollview setContentOffset:self.nextOffset animated:YES];
}

// 无动画
-(void) changeOffsetNoAnimation:(NSNumber*) value{
    [self.scrollview setContentOffset:self.nextOffset animated:NO];
}

#pragma -mark lvNewCollectionView
static int lvNewPagerView (lv_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVPagerView class]];
    
    if ( lv_gettop(L)>=1 && lv_type(L, 1)==LV_TTABLE ) {
        LVPagerView* pageView = [[c alloc] init:L];
        
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
    [self moveCenter];
    [self checkCellVisible];
}

-(void) reloadDataASync{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self performSelector:@selector(reloadData) withObject:nil afterDelay:0.001 inModes:@[NSRunLoopCommonModes]];
    });
}

static int reload (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* pageView = (__bridge LVPagerView *)(user->object);
        [pageView reloadDataASync];
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

static int autoScroll(lv_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if(user){
        LVPagerView * view = (__bridge LVPagerView *)(user -> object);
        if([view isKindOfClass: [LVPagerView class]]){
            NSInteger totalPages = view.cellArray.count;
            if ( totalPages < 2 ){//小于两个没有效果
                return 0;
            }
            
            if(lv_gettop(L) >= 2) {
                float interval = lv_tonumber(L, 2);
                
                if ( interval > 0.02 ) {//start timer
                    [view startTimer:interval repeat:YES];
                } else {//stop timer
                    [view stopTimer];
                }
            }
        }
    }
    return 0;
}

static int looping(lv_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if(user){
        LVPagerView * view = (__bridge LVPagerView *)(user -> object);
        if([view isKindOfClass: [LVPagerView class]]){
            if( lv_gettop(L)>=2 ) {
            BOOL ret = lv_toboolean(L, 2);
            view.looping = ret;
                return 0;
            } else {
                BOOL yes = view.looping;
                lv_pushboolean(L, yes);
                return 1;
            }
        }
    }
    return 0;
}

#pragma -mark Timer
-(void) stopTimer{
    if( self.timer ) {
        [self.timer invalidate];
        self.timer = nil;
    }
}

- (void) startTimer:(NSTimeInterval) interval repeat:(BOOL) repeat{
    self.looping = YES;
    [self stopTimer];
    //create new timer
    self.timer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(scrollTimer:) userInfo:nil repeats:repeat];
}

- (void) scrollTimer:(NSTimer *) timer {
    if( self.isScrollEndTimes>1 ) {
        //更改方向
        NSInteger width = self.scrollview.frame.size.width;
        CGPoint p = self.scrollview.contentOffset;
        p.x += width;
        p.x = ((NSInteger)p.x) /width * width;
        self.nextOffset = p;
        [self performSelectorOnMainThread:@selector(changeOffsetWithAnimation:) withObject:nil waitUntilDone:NO];
    } else {
        self.isScrollEndTimes ++;
    }
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

static int previewSide(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVPagerView* pagerview = (__bridge LVPagerView *)(user->object);
        if( lv_gettop(L)>=3 ) {
            CGFloat sideLeft = lv_tonumber(L, 2);
            CGFloat sideRight = lv_tonumber(L, 3);
            pagerview.sideLeft = sideLeft;
            pagerview.sideRight = sideRight;
            pagerview.frame = pagerview.frame;
            return 0;
        } else {
            lv_pushnumber(L, pagerview.sideLeft);
            lv_pushnumber(L, pagerview.sideRight);
            return 2;
        }
    }
    return 0;
}

#pragma -mark __gc
static void releaseUserDataView(LVUserDataInfo* userdata){
    if( userdata && userdata->object ){
        LVPagerView<LVProtocal>* view = CFBridgingRelease(userdata->object);
        userdata->object = NULL;
        if( view ){
            [view.timer invalidate];
            view.lv_userData = nil;
            view.lv_lview = nil;
            [view removeFromSuperview];
        }
    }
}

static int __gc (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserDataView(user);
    return 0;
}

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewPagerView globalName:globalName defaultName:@"PagerView"];
    
    const struct lvL_reg memberFunctions [] = {
        {"reload",    reload},
        {"showScrollBar",     showScrollBar },
        {"currentPage",     setCurrentPage },
        {"autoScroll", autoScroll},
        {"looping", looping},
        {"indicator", indicator},
        
        {"previewSide", previewSide},
        
        {"__gc", __gc },
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
    CGFloat offsetX = self.scrollview.contentOffset.x;
    CGFloat pageWidth = self.scrollview.frame.size.width;
    CGFloat pageIndex = offsetX/pageWidth;
    
    self.pageIdx = [self xindex2index:pageIndex];
    [self setPageIndicatorIdx:[self xindex2index:pageIndex + 0.5]];

    lv_State* l = self.lv_lview.l;
    if( l && self.lv_userData ){
        lv_checkStack32(l);
        double intPart = 0;
        double floatPart = modf( pageIndex, &intPart);
        lv_pushnumber(l, mapPageIdx( self.pageIdx ) );
        lv_pushnumber(l, floatPart);
        lv_pushnumber(l, offsetX - intPart*pageWidth);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:STR_CALLBACK key2:"Scrolling" key3:NULL nargs:3 nrets:0 retType:LV_TNONE];
    }
}

- (void) callLuaWithScrollEnded{
    {
        CGFloat offsetX = self.scrollview.contentOffset.x;
        CGFloat pageWidth = self.scrollview.frame.size.width;
        CGFloat pageIndex = offsetX/pageWidth;
        
        self.pageIdx = [self xindex2index:pageIndex + 0.1];
        [self setPageIndicatorIdx:[self xindex2index:pageIndex + 0.1]];
    }
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
    [self moveCenter];
    [self checkCellVisible];
    
    [self callLuaWithScrolling];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    self.isScrollEndTimes = 0;
    [self lv_callLuaByKey1:@STR_CALLBACK key2:@"ScrollBegin" argN:0];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    //[self lv_callLuaByKey1:@STR_CALLBACK key2:@"BeginDecelerating"];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnded];
    self.isScrollEndTimes = 0;
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView{
    [self callLuaWithScrolling];
    [self callLuaWithScrollEnded];
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
    UIView *view = [super hitTest:point withEvent:event];
    if ([view isEqual:self])
    {
        UIScrollView* scrollview = self.scrollview;
        for (UIView *subview in scrollview.subviews)
        {
            CGPoint offset = CGPointMake(point.x - scrollview.frame.origin.x + scrollview.contentOffset.x - subview.frame.origin.x,
                                         point.y - scrollview.frame.origin.y + scrollview.contentOffset.y - subview.frame.origin.y);
            
            if ((view = [subview hitTest:offset withEvent:event]))
            {
                return view;
            }
        }
        return scrollview;
    }
    return view;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<PagerView(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}
@end

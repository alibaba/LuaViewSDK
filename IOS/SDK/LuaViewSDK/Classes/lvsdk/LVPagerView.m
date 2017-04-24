/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVPagerView.h"
#import "LView.h"
#import "LVBaseView.h"
#import "LVScrollView.h"
#import "LVPagerViewCell.h"
#import "LVPagerIndicator.h"
#import "LVHeads.h"
#import "LuaViewCore.h"

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

@property (nonatomic,assign) BOOL doubleMode;

@property (nonatomic,assign) float autoScrollInterval;
@end


@implementation LVPagerView

-(id) init:(lua_State*) l {
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
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
    if( num==2 && ( self.sideLeft!=0 || self.sideRight!=0 ) ) {
        // 开始双倍模式
        num = 4;
        self.doubleMode = YES;
    } else {
        self.doubleMode = NO;
    }
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
    if( self.doubleMode ) {
        self.pagerIndicator.numberOfPages = 2;
    } else {
        self.pagerIndicator.numberOfPages = self.cellArray.count;
    }
    
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
    if( width>0 ) {
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
    
    [self lv_callLuaCallback:@STR_ON_LAYOUT];
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
    LuaViewCore* lview = self.lv_luaviewCore;
    lua_State* l = lview.l;
    UIView* newWindow = cell.contentView;
    [lview pushWindow:newWindow];
    if ( l ) {
        // 只有两个Cell的时候开启特殊翻倍模式！！！
        if( self.doubleMode && pageIdx>=2 ) {
            pageIdx = pageIdx-2;
        }
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lua_settop(l, 0);
            lua_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lua_pushnumber(l, mapPageIdx(pageIdx) );//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Pages" key2:"Init" key3:NULL nargs:2 nrets:0 retType:LUA_TNONE];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lua_settop(l, 0);
            lua_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lua_pushnumber(l, mapPageIdx(pageIdx) );//arg2: section
            
            lv_pushUserdata(l, self.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Pages" key2:"Layout" key3:NULL nargs:2 nrets:0 retType:LUA_TNONE];
        }
    }
    [lview popWindow:newWindow];
    return cell;
}

// section数量
- (NSInteger) numberOfPagesInPageView{
    lua_State* l = self.lv_luaviewCore.l;
    if( l && self.lv_userData ){
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"PageCount" key2:NULL key3:NULL nargs:0 nrets:1 retType:LUA_TNUMBER] ==0 ) {
            if( lua_type(l, -1)==LUA_TNUMBER ) {
                NSInteger num = lua_tonumber(l, -1);
                return num;
            }
        }
        return 1;
    }
    return 1;
}

-(void) setPageIndicatorIdx:(NSInteger)pageIdx{
    if( pageIdx>=0 && pageIdx<self.cellArray.count ) {
        if( self.doubleMode && pageIdx>=2 ) {
            self.pagerIndicator.currentPage = pageIdx-2;
        } else {
            self.pagerIndicator.currentPage = pageIdx;
        }
    }
}


-(void) setIndicator:(LVPagerIndicator*) indicator{
    self.pagerIndicator = indicator;
    self.pagerIndicator.pagerView = self;
    if( self.doubleMode ) {
        self.pagerIndicator.numberOfPages = 2;
    } else {
        self.pagerIndicator.numberOfPages = self.cellArray.count;
    }
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
    if( self.lv_luaviewCore ) {
        [self.scrollview setContentOffset:self.nextOffset animated:YES];
    }
}

// 无动画
-(void) changeOffsetNoAnimation:(NSNumber*) value{
    if( self.lv_luaviewCore ) {
        [self.scrollview setContentOffset:self.nextOffset animated:NO];
    }
}

#pragma -mark lvNewCollectionView
static int lvNewPagerView (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVPagerView class]];
    
    if( lua_gettop(L)<=0 ) {
        lua_createtable(L, 8, 0);
    }
    
    if ( lua_gettop(L)>=1 && lua_type(L, 1)==LUA_TTABLE ) {
        LVPagerView* pageView = [[c alloc] init:L];
        
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(pageView);
        pageView.lv_userData = userData;
        luaL_getmetatable(L, META_TABLE_UIPageView );
        lua_setmetatable(L, -2);
        
        LuaViewCore* lview = LV_LUASTATE_VIEW(L);
        if( lview ){
            [lview containerAddSubview:pageView];
        }
        
        int stackNum = lua_gettop(L);
        lua_pushvalue(L, 1);
        lv_udataRef(L, USERDATA_KEY_DELEGATE );
        
        [pageView createAllCell];
        lua_settop(L, stackNum);
        
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
    self.autoScrollInterval = self.autoScrollInterval;
}

-(void) reloadDataASync{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self performSelector:@selector(reloadData) withObject:nil afterDelay:0.001 inModes:@[NSRunLoopCommonModes]];
    });
}

static int reload (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerView* pageView = (__bridge LVPagerView *)(user->object);
        [pageView reloadDataASync];
        lua_pushvalue(L, 1);
        return 1;
    }
    return 0;
}

static int showScrollBar(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIScrollView* view = (__bridge UIScrollView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            BOOL yes1 = lua_toboolean(L, 2);
            view.showsHorizontalScrollIndicator = yes1;
            return 0;
        } else {
            lua_pushboolean(L, view.showsHorizontalScrollIndicator );
            return 1;
        }
    }
    return 0;
}

static int setCurrentPage(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerView* view = (__bridge LVPagerView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            int luaPageIdx = lua_tonumber(L, 2);
            BOOL animated = YES;
            if( lua_gettop(L)>=3 ) {
                animated = lua_toboolean(L, 3);
            }
            [view setCurrentPageIdx:unmapPageIdx(luaPageIdx) animation:animated];
            lua_settop(L, 1);
            return 1;
        } else {
            NSInteger currentPageIdx = view.pageIdx;
            lua_pushnumber( L, mapPageIdx(currentPageIdx) );
            return 1;
        }
    }
    return 0;
}

-(void) setAutoScrollInterval:(float) interval{
    _autoScrollInterval = interval;
    NSInteger totalPages = self.cellArray.count;
    if ( totalPages < 2 ){//小于两个没有效果
        [self stopTimer];
        return ;
    }
    if ( interval > 0.02 ) {//start timer
        [self startTimer:interval repeat:YES];
    } else {//stop timer
        [self stopTimer];
    }
}

static int autoScroll(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if(user){
        LVPagerView * view = (__bridge LVPagerView *)(user -> object);
        
        if(lua_gettop(L) >= 2) {
            float interval = lua_tonumber(L, 2);
            [view setAutoScrollInterval:interval];
        }
    }
    return 0;
}

static int looping(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if(user){
        LVPagerView * view = (__bridge LVPagerView *)(user -> object);
        if( lua_gettop(L)>=2 ) {
        BOOL ret = lua_toboolean(L, 2);
        view.looping = ret;
            return 0;
        } else {
            BOOL yes = view.looping;
            lua_pushboolean(L, yes);
            return 1;
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

static int indicator(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerView* view = (__bridge LVPagerView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            if ( lua_type(L, 2)==LUA_TNIL ) {
                view.pagerIndicator = nil;
                [view setIndicator:nil];// 设置Indicator
                lua_pushvalue(L, 1);
                lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
                lua_pushnil(L);// value
                lua_setfield(L, -2, "Indicator");
            } else {
                LVUserDataInfo * user2 = (LVUserDataInfo *)lua_touserdata(L, 2);
                if( LVIsType(user2, View) ) {
                    LVPagerIndicator* pagerIndicator = (__bridge LVPagerIndicator *)(user2->object);
                    if( [pagerIndicator isKindOfClass:[LVPagerIndicator class]] ) {
                        [view setIndicator:pagerIndicator];// 设置Indicator
                        lua_pushvalue(L, 1);
                        lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
                        lua_pushvalue(L, 2);// value
                        lua_setfield(L, -2, "Indicator");
                    }
                }
            }
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE );
            lua_getfield(L, -2, "Indicator");
            return 1;
        }
    }
    return 0;
}

static int previewSide(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerView* pagerview = (__bridge LVPagerView *)(user->object);
        if( lua_gettop(L)>=3 ) {
            CGFloat sideLeft = lua_tonumber(L, 2);
            CGFloat sideRight = lua_tonumber(L, 3);
            pagerview.sideLeft = sideLeft;
            pagerview.sideRight = sideRight;
            pagerview.frame = pagerview.frame;
            if( pagerview.cellArray.count==2 ) {
                [pagerview reloadDataASync];
            }
            return 0;
        } else {
            lua_pushnumber(L, pagerview.sideLeft);
            lua_pushnumber(L, pagerview.sideRight);
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
            view.scrollview.delegate = nil;
            [view.scrollview removeFromSuperview];
            NSArray* subviews = view.scrollview.subviews;
            for( UIView* view  in subviews ) {
                [view removeFromSuperview];
            }
            view.scrollview.scrollEnabled = NO;
            view.scrollview = nil;
            
            view.lv_userData = nil;
            view.lv_luaviewCore = nil;
            [view removeFromSuperview];
        }
    }
}

static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataView(user);
    return 0;
}

static int initParams (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVPagerView* pageView = (__bridge LVPagerView *)(user->object);
        int ret =  lv_setCallbackByKey(L, nil, NO);
        [pageView reloadDataASync];
        return ret;
    }
    return 0;
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewPagerView globalName:globalName defaultName:@"PagerView"];
    
    const struct luaL_Reg memberFunctions [] = {
        {"initParams",   initParams },// 回调
        {"reload",    reload},
        {"showScrollBar",     showScrollBar },
        {"currentPage",     setCurrentPage },
        {"autoScroll", autoScroll}, // IOS: 需要支持正反滚动
        {"looping", looping},
        {"indicator", indicator},
        
        {"previewSide", previewSide},
        
        {"__gc", __gc },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L ,META_TABLE_UIPageView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, [LVScrollView memberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
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

    lua_State* l = self.lv_luaviewCore.l;
    if( l && self.lv_userData ){
        lua_settop(l, 0);
        lua_checkstack32(l);
        double intPart = 0;
        double floatPart = modf( pageIndex, &intPart);
        NSInteger pageIdx = self.pageIdx;
        if( self.doubleMode&& pageIdx>=2 ) {
            pageIdx -= 2;
        }
        lua_pushnumber(l, mapPageIdx( pageIdx ) );
        lua_pushnumber(l, floatPart);
        lua_pushnumber(l, offsetX - intPart*pageWidth);
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:STR_CALLBACK key2:"Scrolling" key3:NULL nargs:3 nrets:0 retType:LUA_TNONE];
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
    lua_State* l = self.lv_luaviewCore.l;
    if( l && self.lv_userData ){
        lua_checkstack32(l);
        NSInteger pageIdx = self.pageIdx;
        if( self.doubleMode&& pageIdx>=2 ) {
            pageIdx -= 2;
        }
        lua_pushnumber(l, mapPageIdx(pageIdx) );
        
        lv_pushUserdata(l, self.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        [LVUtil call:l key1:STR_CALLBACK key2:"ScrollEnd" key3:NULL nargs:1 nrets:0 retType:LUA_TNONE];
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    [self moveCenter];
    [self checkCellVisible];
    
    [self callLuaWithScrolling];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    self.isScrollEndTimes = 0;
    [self lv_callLuaCallback:@STR_CALLBACK key2:@"ScrollBegin" argN:0];
}
- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView{
    //[self lv_callLuaCallback:@STR_CALLBACK key2:@"BeginDecelerating"];
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

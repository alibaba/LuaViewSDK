

#import "LVRefreshHeader.h"

@interface LVRefreshHeader()
@property (assign, nonatomic) CGFloat insetTDelta;
@end

@implementation LVRefreshHeader
#pragma mark - 构造方法
+ (instancetype)headerWithRefreshingBlock:(LVRefreshComponentRefreshingBlock)refreshingBlock
{
    LVRefreshHeader *cmp = [[self alloc] init];
    cmp.refreshingBlock = refreshingBlock;
    return cmp;
}
+ (instancetype)headerWithRefreshingTarget:(id)target refreshingAction:(SEL)action
{
    LVRefreshHeader *cmp = [[self alloc] init];
    [cmp setRefreshingTarget:target refreshingAction:action];
    return cmp;
}
#pragma mark - 覆盖父类的方法
- (void)prepare
{
    [super prepare];
    
    // 设置key
    self.lastUpdatedTimeKey = @"LVRefreshHeaderLastUpdatedTimeKey";
    
    // 设置高度f
    {
        CGRect frame = self.frame;
        frame.size.height = LVRefreshHeaderHeight;
        self.frame = frame;
    }
}

- (void)placeSubviews
{
    [super placeSubviews];
    
    // 设置y值(当自己的高度发生改变了，肯定要重新调整Y值，所以放到placeSubviews方法中设置y值)
    {
        CGRect frame = self.frame;
        frame.origin.y = - frame.size.height - self.ignoredScrollViewContentInsetTop;
        self.frame = frame;
    }
}

- (void)scrollViewContentOffsetDidChange:(NSDictionary *)change
{
    [super scrollViewContentOffsetDidChange:change];
    
    // 在刷新的refreshing状态
    if (self.state == LVRefreshStateRefreshing) {
        if (self.window == nil) return;
        
        // sectionheader停留解决
        CGPoint offset = self.scrollView.contentOffset;
        CGFloat insetT = - offset.y > _scrollViewOriginalInset.top ? - offset.y : _scrollViewOriginalInset.top;
        {
            CGRect f = self.frame;
            insetT = insetT > f.size.height + _scrollViewOriginalInset.top ? f.size.height + _scrollViewOriginalInset.top : insetT;
        }
        {
            UIEdgeInsets inset = self.scrollView.contentInset;
            inset.top = insetT;
            self.scrollView.contentInset = inset;
        }
        
        self.insetTDelta = _scrollViewOriginalInset.top - insetT;
        return;
    }
    
    // 跳转到下一个控制器时，contentInset可能会变
     _scrollViewOriginalInset = self.scrollView.contentInset;
    
    // 当前的contentOffset
    CGPoint offset = self.scrollView.contentOffset;
    CGFloat offsetY = offset.y;
    // 头部控件刚好出现的offsetY
    CGFloat happenOffsetY = - self.scrollViewOriginalInset.top;
    
    // 如果是向上滚动到看不见头部控件，直接返回
    // >= -> >
    if (offsetY > happenOffsetY) return;
    
    // 普通 和 即将刷新 的临界点
    CGRect f = self.frame;
    CGFloat normal2pullingOffsetY = happenOffsetY - f.size.height;
    CGFloat pullingPercent = (happenOffsetY - offsetY) / f.size.height;
    
    if (self.scrollView.isDragging) { // 如果正在拖拽
        self.pullingPercent = pullingPercent;
        if (self.state == LVRefreshStateIdle && offsetY < normal2pullingOffsetY) {
            // 转为即将刷新状态
            self.state = LVRefreshStatePulling;
        } else if (self.state == LVRefreshStatePulling && offsetY >= normal2pullingOffsetY) {
            // 转为普通状态
            self.state = LVRefreshStateIdle;
        }
    } else if (self.state == LVRefreshStatePulling) {// 即将刷新 && 手松开
        // 开始刷新
        [self beginRefreshing];
    } else if (pullingPercent < 1) {
        self.pullingPercent = pullingPercent;
    }
}

- (void)setState:(LVRefreshState)state
{
    LVRefreshCheckState
    
    // 根据状态做事情
    if (state == LVRefreshStateIdle) {
        if (oldState != LVRefreshStateRefreshing) return;
        
        // 保存刷新时间
        [[NSUserDefaults standardUserDefaults] setObject:[NSDate date] forKey:self.lastUpdatedTimeKey];
        [[NSUserDefaults standardUserDefaults] synchronize];
        
        // 恢复inset和offset
        [UIView animateWithDuration:LVRefreshSlowAnimationDuration animations:^{
            {
                UIEdgeInsets inset = self.scrollView.contentInset;
                inset.top += self.insetTDelta;
                self.scrollView.contentInset = inset;
            }
            // 自动调整透明度
            if (self.isAutomaticallyChangeAlpha) self.alpha = 0.0;
        } completion:^(BOOL finished) {
            self.pullingPercent = 0.0;
        }];
    } else if (state == LVRefreshStateRefreshing) {
        [UIView animateWithDuration:LVRefreshFastAnimationDuration animations:^{
            // 增加滚动区域
            CGRect f = self.frame;
            CGFloat top = self.scrollViewOriginalInset.top + f.size.height;
            {
                UIEdgeInsets inset = self.scrollView.contentInset;
                inset.top = top;
                self.scrollView.contentInset = inset;
            }
            
            // 设置滚动位置
            {
                CGPoint offset = self.scrollView.contentOffset;
                offset.y = - top;
                self.scrollView.contentOffset = offset;
            }
        } completion:^(BOOL finished) {
            [self executeRefreshingCallback];
        }];
    }
}

- (void)drawRect:(CGRect)rect
{
    [super drawRect:rect];
    
    
}

#pragma mark - 公共方法
- (void)endRefreshing
{
    if ([self.scrollView isKindOfClass:[UICollectionView class]]) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [super endRefreshing];
        });
    } else {
        [super endRefreshing];
    }
}

- (NSDate *)lastUpdatedTime
{
    return [[NSUserDefaults standardUserDefaults] objectForKey:self.lastUpdatedTimeKey];
}
@end

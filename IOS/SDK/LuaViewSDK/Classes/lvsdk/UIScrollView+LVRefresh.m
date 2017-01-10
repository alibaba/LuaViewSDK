
#import "UIScrollView+LVRefresh.h"
#import "LVRefreshHeader.h"
#import <objc/runtime.h>



@implementation UIScrollView (LVRefresh)

#pragma mark - header
static const char LVRefreshHeaderKey = '\0';
- (void)setLv_refresh_header:(LVRefreshHeader *)header
{
    if (header != self.lv_refresh_header) {
        // 删除旧的，添加新的
        [self.lv_refresh_header removeFromSuperview];
        [self insertSubview:header atIndex:0];
        
        // 存储新的
        [self willChangeValueForKey:@"lv_refresh_header"]; // KVO
        objc_setAssociatedObject(self, &LVRefreshHeaderKey,
                                 header, OBJC_ASSOCIATION_ASSIGN);
        [self didChangeValueForKey:@"lv_refresh_header"]; // KVO
    }
}

- (LVRefreshHeader *)lv_refresh_header
{
    return objc_getAssociatedObject(self, &LVRefreshHeaderKey);
}


@end


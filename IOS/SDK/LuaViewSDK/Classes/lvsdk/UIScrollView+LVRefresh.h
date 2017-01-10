
#import <UIKit/UIKit.h>
#import "LVRefreshConst.h"

@class LVRefreshHeader;

@interface UIScrollView (LVRefresh)
/** 下拉刷新控件 */
@property (strong, nonatomic) LVRefreshHeader * lv_refresh_header;

@end

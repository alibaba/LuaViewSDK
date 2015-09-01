
#import <UIKit/UIKit.h>
#import <objc/message.h>

// 日志输出
#ifdef DEBUG
#define MJLog(...) NSLog(__VA_ARGS__)
#else
#define MJLog(...)
#endif

#define iOS(version) ([[UIDevice currentDevice].systemVersion doubleValue] >= version)

// 过期提醒
#define MJDeprecated(instead) NS_DEPRECATED(2_0, 2_0, 2_0, 2_0, instead)

// 运行时objc_msgSend
#define msgSend(...) ((void (*)(void *, SEL, UIView *))objc_msgSend)(__VA_ARGS__)
#define msgTarget(target) (__bridge void *)(target)

// RGB颜色
#define MJColor(r, g, b) [UIColor colorWithRed:(r)/255.0 green:(g)/255.0 blue:(b)/255.0 alpha:1.0]

// 文字颜色
#define MJRefreshLabelTextColor MJColor(90, 90, 90)

// 字体大小
#define MJRefreshLabelFont [UIFont boldSystemFontOfSize:14]

// 图片路径
#define MJRefreshSrcName(file) [@"MJRefresh.bundle" stringByAppendingPathComponent:file]

// 常量
UIKIT_EXTERN const CGFloat MJRefreshHeaderHeight;
UIKIT_EXTERN const CGFloat MJRefreshFooterHeight;
UIKIT_EXTERN const CGFloat MJRefreshFastAnimationDuration;
UIKIT_EXTERN const CGFloat MJRefreshSlowAnimationDuration;

UIKIT_EXTERN NSString *const MJRefreshKeyPathContentOffset;
UIKIT_EXTERN NSString *const MJRefreshKeyPathContentSize;
UIKIT_EXTERN NSString *const MJRefreshKeyPathContentInset;
UIKIT_EXTERN NSString *const MJRefreshKeyPathPanState;

UIKIT_EXTERN NSString *const MJRefreshHeaderLastUpdatedTimeKey;

UIKIT_EXTERN NSString *const MJRefreshHeaderIdleText;
UIKIT_EXTERN NSString *const MJRefreshHeaderPullingText;
UIKIT_EXTERN NSString *const MJRefreshHeaderRefreshingText;

UIKIT_EXTERN NSString *const MJRefreshAutoFooterIdleText;
UIKIT_EXTERN NSString *const MJRefreshAutoFooterRefreshingText;
UIKIT_EXTERN NSString *const MJRefreshAutoFooterNoMoreDataText;


// 状态检查
#define MJRefreshCheckState \
MJRefreshState oldState = self.state; \
if (state == oldState) return; \
[super setState:state];

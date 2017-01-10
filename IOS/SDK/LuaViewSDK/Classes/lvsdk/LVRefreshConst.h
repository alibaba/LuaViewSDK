
#import <UIKit/UIKit.h>
#import <objc/message.h>


//// 运行时objc_msgSend
#define LVRefreshMsgSend(...) ((void (*)(void *, SEL, UIView *))objc_msgSend)(__VA_ARGS__)
#define LVRefreshMsgTarget(target) (__bridge void *)(target)


//// 常量
UIKIT_EXTERN const CGFloat LVRefreshHeaderHeight;
UIKIT_EXTERN const CGFloat LVRefreshFastAnimationDuration;
UIKIT_EXTERN const CGFloat LVRefreshSlowAnimationDuration;

UIKIT_EXTERN NSString *const LVRefreshKeyPathContentOffset;
UIKIT_EXTERN NSString *const LVRefreshKeyPathContentSize;
UIKIT_EXTERN NSString *const LVRefreshKeyPathContentInset;
UIKIT_EXTERN NSString *const LVRefreshKeyPathPanState;

// 状态检查
#define LVRefreshCheckState \
LVRefreshState oldState = self.state; \
if (state == oldState) return; \
[super setState:state];

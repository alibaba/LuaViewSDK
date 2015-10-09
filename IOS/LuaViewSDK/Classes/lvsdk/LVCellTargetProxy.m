//
//  LVCellTargetProxy.m
//  LuaViewSDK
//
//  Created by dongxicheng on 10/9/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVCellTargetProxy.h"
#import <Foundation/Foundation.h>


@interface LVCellTargetProxy ()
@property (nonatomic,weak) UITableViewCell* weakCell;
@end



@implementation LVCellTargetProxy

- (id)initWithCell:(id) cell {
    if( self ) {
        if ([cell respondsToSelector:@selector(contentView)] ){
            self.weakCell = cell;
        }
    }
    return self;
}

- (void)dealloc {
}

-(UIView*) contentView{
    return [self.weakCell contentView];
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)selector {
    NSMethodSignature* sig = [[self contentView] methodSignatureForSelector:selector];
    return sig;
}

- (void)forwardInvocation:(NSInvocation *)invocation {
    [invocation invokeWithTarget:[self contentView]];
}

- (BOOL)respondsToSelector:(SEL)selector {
    if ( [[self contentView] respondsToSelector:selector] )
        return YES;
    return NO;
}

@end




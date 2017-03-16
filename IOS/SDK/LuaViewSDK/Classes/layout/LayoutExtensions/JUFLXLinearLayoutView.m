/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "JUFLXLinearLayoutView.h"
#import "UIView+JUFLXNode.h"

@implementation JUFLXLinearLayoutView {
    BOOL _dirty;
}

@synthesize contentInset = _contentInset, layoutDirection = _layoutDirection, autoAdjustFrameSize = _autoAdjustFrameSize, lineSpacing = _lineSpacing;

- (instancetype)init {
    self = [super init];
    if (self) {
        [self commonInit];
    }
    
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        [self commonInit];
    }
    
    return self;
}

- (void)commonInit {
    _contentInset = UIEdgeInsetsZero;
    _layoutDirection = JUFLXLayoutDirectionColumn;
    self.ju_flxNode.direction = _layoutDirection;
    _autoAdjustFrameSize = NO;
    _lineSpacing = 0;
}

- (void)setAutoAdjustFrameSize:(BOOL)autoAdjustFrameSize {
    if (_autoAdjustFrameSize == autoAdjustFrameSize) return;
    
    _autoAdjustFrameSize = autoAdjustFrameSize;
    
    CGFloat dimWidth = CGRectGetWidth(self.frame);
    CGFloat dimHeight = CGRectGetHeight(self.frame);
    
    switch (self.layoutDirection) {
        case JUFLXLayoutDirectionRow:
        case JUFLXLayoutDirectionRowReverse: {
            dimWidth = JUFLXLayoutFloatUnDefined;
            break;
        }
        case JUFLXLayoutDirectionColumn:
        case JUFLXLayoutDirectionColumnReverse: {
            dimHeight = JUFLXLayoutFloatUnDefined;
            break;
        }
        default: {
            break;
        }
    }
    
    if (_autoAdjustFrameSize) {
        self.ju_flxNode.dimensions = CGSizeMake(dimWidth, dimHeight);
    }
}

- (void)setChildViews:(NSArray *)childViews {
    if ([_childViews isEqual:childViews]) return;

    _childViews = childViews;
    _childViews = [_childViews filteredArrayUsingPredicate:[NSPredicate predicateWithBlock:^BOOL(id evaluatedObject, NSDictionary *bindings) {
        BOOL isViewClass = [evaluatedObject isKindOfClass:[UIView class]];
        NSAssert(isViewClass, @"views should be class of UIViews");
        
        return isViewClass;
    }]];
    
    for (UIView *view in self.childViews) {
        // if the view is not support self sizing, the we flex it
        if (!view.ju_flxNode.measure && view.ju_flxNode.flex == 0 && CGSizeEqualToSize(view.ju_flxNode.dimensions, CGSizeZero)) {
            view.ju_flxNode.flex = 1;
        }
    }
    
    [self setNeedsUpdateChildViews];
    
    self.ju_flxNode.childNodes = [self.childViews valueForKey:@"ju_flxNode"];
    
    _dirty = YES;
}

- (void)setContentInset:(UIEdgeInsets)contentInset {
    if (UIEdgeInsetsEqualToEdgeInsets(_contentInset, contentInset)) return;
    
    _contentInset = contentInset;
    [self setNeedsUpdateChildViews];
    
    _dirty = YES;
}

- (void)setLayoutDirection:(JUFLXLayoutDirection)layoutDirection {
    if (_layoutDirection == layoutDirection) return;
    
    _layoutDirection = layoutDirection;
    self.ju_flxNode.direction = _layoutDirection;
    [self setNeedsUpdateChildViews];
    
    _dirty = YES;
}

- (void)setLineSpacing:(CGFloat)lineSpacing {
    if (_lineSpacing == lineSpacing) return;
    
    _lineSpacing = lineSpacing;
    [self setNeedsUpdateChildViews];
    
    _dirty = YES;
}

- (void)setNeedsUpdateChildViews {
    BOOL isVerticalLayout = (self.ju_flxNode.direction == JUFLXLayoutDirectionColumn || self.ju_flxNode.direction == JUFLXLayoutDirectionColumnReverse);
    [self.childViews enumerateObjectsUsingBlock:^(UIView *view, NSUInteger idx, BOOL *stop) {
        UIEdgeInsets edge = UIEdgeInsetsZero;
        
        if (idx == 0) {
            if (isVerticalLayout) {
                edge = UIEdgeInsetsMake(_contentInset.top, _contentInset.left, 0, _contentInset.right);
            }
            else {
                edge = UIEdgeInsetsMake(_contentInset.top, _contentInset.left, _contentInset.bottom, 0);
            }
        }
        else if (idx == self.childViews.count - 1) {
            if (isVerticalLayout) {
                edge = UIEdgeInsetsMake(_lineSpacing, _contentInset.left, _contentInset.bottom, _contentInset.right);
            }
            else {
                edge = UIEdgeInsetsMake(_contentInset.top, _lineSpacing, _contentInset.bottom, _contentInset.right);
            }
        }
        else {
            if (isVerticalLayout) {
                edge = UIEdgeInsetsMake(_lineSpacing, _contentInset.left, 0, _contentInset.right);
            }
            else {
                edge = UIEdgeInsetsMake(_contentInset.top, _lineSpacing, _contentInset.bottom, 0);
            }
        }
        
        view.ju_flxNode.margin = edge;
    }];
}

- (void)setNeedsReload {
    if (_dirty) {
        [self layout];
    }
}

- (void)layout {
    [self.ju_flxNode layout];
    _dirty = NO;
}

@end

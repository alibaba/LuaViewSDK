//
//  FlexboxLayoutTests.m
//  LuaViewSDK
//
//  Created by xiekw on 16/3/30.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "JUFLXLayoutKit.h"

@interface FlexboxLayoutTests : XCTestCase

@end

@implementation FlexboxLayoutTests {
    NSString *_testString;
    UIView *_view;
}

- (void)setUp {
    [super setUp];
    
    NSMutableString *styleCSS = [NSMutableString string];
    [styleCSS appendString:@"position: absolute,"];
    [styleCSS appendString:@"top: 1,"];
    [styleCSS appendString:@"left: 2,"];
    [styleCSS appendString:@"bottom: 3,"];
    [styleCSS appendString:@"right: 4,"];
    
    [styleCSS appendString:@"flex-direction: row,"];
    [styleCSS appendString:@"align-items: flex-start,"];
    [styleCSS appendString:@"align-content: flex-end,"];
    [styleCSS appendString:@"align-self: center,"];
    [styleCSS appendString:@"justify-content: center,"];
    [styleCSS appendString:@"flex: 10,"];
    [styleCSS appendString:@"flex-wrap: 1,"];
    [styleCSS appendString:@"width: 20, height: 30,"];
    [styleCSS appendString:@"min-width: 40, min-height: 50,"];
    [styleCSS appendString:@"max-width: 60, max-height: 70,"];
    [styleCSS appendString:@"margin: 5,"];
    [styleCSS appendString:@"margin-top: 1, margin-left: 2, margin-bottom: 3, margin-right: 4,"];
    [styleCSS appendString:@"padding: 10,"];
    [styleCSS appendString:@"padding-top: 2, padding-left: 4, padding-bottom: 6, padding-right: 8,"];
    [styleCSS appendString:@"sizetofit: 1"];
    _testString = styleCSS;
    
    _view = [UIView new];
    [_view flx_bindingInlineCSS:styleCSS];
}

- (void)testAllValidKeys {
    XCTAssert(_view.flx_absolute == YES);
    XCTAssert(UIEdgeInsetsEqualToEdgeInsets(_view.flx_absoluteEdgeInsets, UIEdgeInsetsMake(1, 2, 3, 4)));
    XCTAssert(_view.flx_direction == JUFLXLayoutDirectionRow);
    XCTAssert(_view.flx_alignItems == JUFLXLayoutAlignmentStart);
    XCTAssert(_view.flx_alignContent == JUFLXLayoutAlignmentEnd);
    XCTAssert(_view.flx_alignSelf == JUFLXLayoutAlignmentCenter);
    XCTAssert(_view.flx_flex == 10);
    XCTAssert(_view.flx_flexWrap == YES);
    
    BOOL equalSize = CGSizeEqualToSize(_view.flx_dimensions, CGSizeMake(20, 30));
    XCTAssert(equalSize);
    BOOL equalMinSize = CGSizeEqualToSize(_view.flx_minDimensions, CGSizeMake(40, 50));
    XCTAssert(equalMinSize);
    BOOL equalMaxSize = CGSizeEqualToSize(_view.flx_maxDimensions, CGSizeMake(60, 70));
    XCTAssert(equalMaxSize);
    
    XCTAssert(UIEdgeInsetsEqualToEdgeInsets(_view.flx_margin, UIEdgeInsetsMake(1, 2, 3, 4)));
    XCTAssert(UIEdgeInsetsEqualToEdgeInsets(_view.flx_padding, UIEdgeInsetsMake(2, 4, 6, 8)));
    
    XCTAssert(_view.flx_sizeToFit == YES);
}

- (void)testFlexDirectionAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"flex-direction: row";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_direction == JUFLXLayoutDirectionRow);
    
    styleCSS = @"flex-direction: column";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_direction == JUFLXLayoutDirectionColumn);
    
    styleCSS = @"flex-direction: row-reverse";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_direction == JUFLXLayoutDirectionRowReverse);
    
    styleCSS = @"flex-direction: column-reverse";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_direction == JUFLXLayoutDirectionColumnReverse);
}

- (void)testAlignItemsAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"align-items: flex-start";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignItems == JUFLXLayoutAlignmentStart);
    
    styleCSS = @"align-items: flex-end";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignItems == JUFLXLayoutAlignmentEnd);
    
    styleCSS = @"align-items: center";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignItems == JUFLXLayoutAlignmentCenter);
    
    styleCSS = @"align-items: stretch";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignItems == JUFLXLayoutAlignmentStretch);
}

- (void)testAlignSelfAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"align-self: flex-start";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignSelf == JUFLXLayoutAlignmentStart);
    
    styleCSS = @"align-self: flex-end";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignSelf == JUFLXLayoutAlignmentEnd);
    
    styleCSS = @"align-self: center";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignSelf == JUFLXLayoutAlignmentCenter);
    
    styleCSS = @"align-self: stretch";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignSelf == JUFLXLayoutAlignmentStretch);
}

- (void)testAlignContentAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"align-content: flex-start";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignContent == JUFLXLayoutAlignmentStart);
    
    styleCSS = @"align-content: flex-end";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignContent == JUFLXLayoutAlignmentEnd);
    
    styleCSS = @"align-content: center";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignContent == JUFLXLayoutAlignmentCenter);
    
    styleCSS = @"align-content: stretch";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_alignContent == JUFLXLayoutAlignmentStretch);
}

- (void)testJustifyContentAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"justify-content: flex-start";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_justifyContent == JUFLXLayoutJustifyContentStart);
    
    styleCSS = @"justify-content: flex-end";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_justifyContent == JUFLXLayoutJustifyContentEnd);
    
    styleCSS = @"justify-content: center";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_justifyContent == JUFLXLayoutJustifyContentCenter);
    
    styleCSS = @"justify-content: space-between";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_justifyContent == JUFLXLayoutJustifyContentBetween);
    
    styleCSS = @"justify-content: space-around";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_justifyContent == JUFLXLayoutJustifyContentAround);
}

- (void)testFlexAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"flex: 1.5";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_flex == 1.5);
    
    styleCSS = @"flex: 0";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_flex == 0);
}

- (void)testFlexWrapAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"flex-wrap: 0";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_flexWrap == NO);
    
    styleCSS = @"flex-wrap: 1";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(view.flx_flexWrap == YES);
}

- (void)testMarginAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"margin: 11.0";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(UIEdgeInsetsEqualToEdgeInsets(view.flx_margin, UIEdgeInsetsMake(11.0, 11.0, 11.0, 11.0)));
}

- (void)testPaddingAttr {
    UIView *view = [UIView new];
    NSString *styleCSS;
    
    styleCSS = @"padding: 15.0";
    [view flx_bindingInlineCSS:styleCSS];
    XCTAssert(UIEdgeInsetsEqualToEdgeInsets(view.flx_padding, UIEdgeInsetsMake(15.0, 15.0, 15.0, 15.0)));
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        [_view flx_bindingInlineCSS:_testString];
    }];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}


@end

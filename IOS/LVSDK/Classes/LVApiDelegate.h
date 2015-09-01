//
//  LVPort.h
//  LVSDK
//
//  Created by dongxicheng on 1/8/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

/*
 * 支持下拉刷新需要支持的协议
 */
typedef void (^LVRefreshingBlock)();

@protocol LVRefreshHeaderProtocol <NSObject>
@required
@property (nonatomic, copy) LVRefreshingBlock refreshingBlock;// 需要刷新时 要回调改block
- (void) beginRefreshing;// 进入刷新状态
- (void) endRefreshing;// 结束刷新状态
- (BOOL) isRefreshing;// 是否正在刷新
@end

/*
 * 支持上拉刷新需要支持的协议
 */
@protocol LVRefreshFooterProtocol <NSObject>
@required
@property (nonatomic, copy) LVRefreshingBlock refreshingBlock;// 需要刷新时 要回调改block
- (void) noticeNoMoreData;// 提示没有更多的数据
- (void) resetNoMoreData;// 重置没有更多的数据（消除没有更多数据的状态）
@end

///------------------------------------------------------------------------------------
/*
 * 支持下拉刷新需要支持的协议
 */
@interface LVApiDelegate : NSObject

/*
 * MiscData 数据读取
 */
typedef void(^FuncMiscDataCallback)(NSString* content);
-(void) group:(NSString*) group key:(NSString*) key callback:(FuncMiscDataCallback) callback;

/*
 * 设置ScrollView/TableView/CollectionView的下拉刷新
 */
-(void) setHeaderRefresh:(UIScrollView*) scrollView;

/*
 * 设置ScrollView/TableView/CollectionView的上拉加载
 */
-(void) setFooterRefresh:(UIScrollView*) scrollView;

@end





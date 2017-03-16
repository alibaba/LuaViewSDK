/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVFlowLayout.h"

@interface LVFlowLayout ()
@property(nonatomic, strong) NSMutableDictionary* pinnedDic;
@end

@implementation LVFlowLayout

-(id)init
{
    self = [super init];
    if ( self ){
    }
    return self;
}

- (NSArray *)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSMutableArray *superLayoutAttArray = [[super layoutAttributesForElementsInRect:rect] mutableCopy];
    NSArray* keys = self.pinnedDic.allKeys;
    // 排序
    keys = [keys sortedArrayUsingComparator:^NSComparisonResult(NSIndexPath*  obj1, NSIndexPath* obj2) {
        return [obj1 compare:obj2];
    }];
    
    UICollectionViewLayoutAttributes* prevAtt = nil;
    NSInteger pinnedTimes = 0;
    for( NSInteger i=((NSInteger)keys.count)-1; i>=0; i-- ) {
        NSIndexPath* indexPath = keys[i];
        UICollectionViewLayoutAttributes* a = [self layoutAttributesForItemAtIndexPath:indexPath];
        if( a ) {
            CGRect frame = a.frame;
            CGFloat minY = self.collectionView.contentOffset.y;
            // 浮层：不能小于屏幕offset
            if( frame.origin.y < minY ) {
                frame.origin.y = minY;
                a.frame = frame;
                a.zIndex = 10 + i;
                pinnedTimes ++;
                
                if( prevAtt ) {
                    // 但是浮层不能盖住上一个浮层
                    CGFloat maxY = prevAtt.frame.origin.y - frame.size.height;
                    if( frame.origin.y>maxY ) {
                        frame.origin.y = maxY;
                        a.frame = frame;
                        a.zIndex = 10 + i;
                    }
                }
            }
            [superLayoutAttArray addObject:a];
            prevAtt = a;
            if( pinnedTimes > 1 ) {
                // 这两行一定要有！！！！原因还有待确认@城西
                a.alpha = 0;
                a.zIndex = -1;
                break;
            } else {
                a.alpha = 1;
            }
        }
    }
    return superLayoutAttArray;
}

-(BOOL) shouldInvalidateLayoutForBoundsChange:(CGRect)newBound
{
    return self.pinnedDic!=nil;
}

-(void) resetPinnedDic{
    self.pinnedDic = [[NSMutableDictionary alloc] initWithCapacity:8];
}
    
-(void) addPinnedIndexPath:(NSIndexPath*)indexPath {
    if( indexPath ) {
        if( self.pinnedDic==nil ) {
            [self resetPinnedDic];
        }
        self.pinnedDic[indexPath] = @(YES);
    }
}

-(void) delPinnedIndexPath:(NSIndexPath*)indexPath{
    if( indexPath ) {
        [self.pinnedDic removeObjectForKey:indexPath];
    }
}

-(BOOL) isPinned:(NSIndexPath*)indexPath{
    if( indexPath ) {
        return (self.pinnedDic[indexPath]!=nil);
    }
    return NO;
}

-(BOOL) pinnedDicIsNil{
    return self.pinnedDic==nil;
}

@end



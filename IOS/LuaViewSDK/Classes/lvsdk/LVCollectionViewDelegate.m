//
//  LVCollectionViewDelegate.m
//  LuaViewSDK
//
//  Created by dongxicheng on 10/21/15.
//  Copyright © 2015 dongxicheng. All rights reserved.
//

#import "LVCollectionViewDelegate.h"
#import "LVCollectionView.h"
#import "LVCollectionViewCell.h"
#import "LView.h"
#import "UIView+LuaView.h"
#import "LVHeads.h"



@interface LVCollectionViewDelegate ()

@end


@implementation LVCollectionViewDelegate

- (UICollectionViewCell*) collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
    identifier = self.identifierDic[identifier];
    if( identifier == nil ){
        identifier = DEFAULT_CELL_IDENTIFIER;
    }
    LVCollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    LView* lview = self.owner.lv_lview;
    lv_State* l = lview.l;
    lview.conentView = cell.contentView;
    lview.contentViewIsWindow = NO;
    if ( l ) {
        if( !cell.isInited ){
            cell.isInited = YES;
            [cell doInitWithLView:lview];
            
            // 创建cell初始化
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, indexPath.section+1);//arg2: section
            lv_pushnumber(l, indexPath.row+1);//arg3: row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Init" nargs:3 nrets:1];
        }
        {   // 通知布局调整
            // 参数 cell,section,row
            lv_settop(l, 0);
            lv_checkstack(l, 12);
            [cell pushTableToStack];//arg1: cell
            lv_pushnumber(l, indexPath.section+1);//arg2: section
            lv_pushnumber(l, indexPath.row+1);//arg3: row
            
            lv_pushUserdata(l, self.owner.lv_userData);
            lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
            [LVUtil call:l key1:"Cell" key2:identifier.UTF8String key3:"Layout" nargs:3 nrets:1];
        }
    }
    lview.conentView = nil;
    lview.contentViewIsWindow = NO;
    return cell;
}

// section数量
- (NSInteger) numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    lv_State* l = self.owner.lv_lview.l;
    if( l && self.owner.lv_userData ){
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"SectionCount" nargs:0 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num;
            }
        }
        return 1;
    }
    return 0;
}
// 每个区域的行数
- (NSInteger) collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_pushnumber(l, section+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:"Section" key2:"RowCount" nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                NSInteger num = lv_tonumber(l, -1);
                return num;
            }
        }
    }
    return 0;
}
//-------
#pragma mark --UICollectionViewDelegateFlowLayout
//定义每个UICollectionView 的大小
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
    if( identifier ) {
        CGSize size = [self returnSizeCallByKey1:"Cell" key2:identifier.UTF8String key3:"Size" section:indexPath.section row:indexPath.row];
        return size;
    } else {
        return CGSizeMake(20, 20);
    }
}
//定义每个UICollectionView 的间距
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    UIEdgeInsets insets = [self callFunction4:"Section" key2:"EdgeInsets" section:section row:0];
    //insets.top = self.flowLayout.minimumLineSpacing;
    return insets;
}
//定义每个UICollectionView 纵向的间距
- (CGFloat)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout minimumInteritemSpacingForSectionAtIndex:(NSInteger)section {
    //    return self.flowLayout.minimumLineSpacing;
    CGFloat spacing = [self callReturnNumberKey1:"Section" key2:"Spacing" section:section];
    return spacing;
}

- (CGFloat) callReturnNumberKey1:(const char*) funcName key2:(const char*) key2 section:(NSInteger) section {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:funcName key2:key2 nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TNUMBER ) {
                CGFloat heigth = lv_tonumber(l, -1);
                return heigth;
            }
        }
    }
    return 0;
}

- (NSString*) returnStringCallWithKey1:(const char*) key1 key2:(const char*)key2 section:(NSInteger) section row:(NSInteger) row{
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:key1 key2:key2 nargs:2 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TSTRING ){
                NSString* value = lv_paramString(l, -1);
                return value;
            }
        }
    }
    return nil;
}

- (CGSize) returnSizeCallByKey1:(const char*) functionName key2:(const char*)key2 key3:(const char*)key3 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 key3:key3 nargs:2 nrets:2] ==0 ) {
            CGSize size = {0};
            if( lv_type(l, -1) ==LV_TNIL ) {
                size.width = self.owner.frame.size.width;
                size.height = lv_tonumber(l, -2);
            } else{
                size.width = lv_tonumber(l, -2);
                size.height = lv_tonumber(l, -1);
            }
            return size;
        }
    }
    return CGSizeMake(0, 0);
}
- (UIEdgeInsets) callFunction4:(const char*) functionName key2:(const char*)key2 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 nargs:2 nrets:4] ==0 ) {
            UIEdgeInsets egeInsets = {0};
            egeInsets.top = lv_tonumber(l, -4);
            egeInsets.left = lv_tonumber(l, -3);
            egeInsets.bottom = lv_tonumber(l, -2);
            egeInsets.right = lv_tonumber(l, -1);
            return egeInsets;
        }
    }
    return UIEdgeInsetsMake(0, 0, 0, 0);
}

- (void) callByKey1:(const char*) functionName key2:(const char*)key2 key3:(const char*)key3 section:(NSInteger) section row:(NSInteger) row {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        // args
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        lv_pushnumber(l, row+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        
        if(  [LVUtil call:l key1:functionName key2:key2 key3:key3 nargs:2 nrets:0]==0 ) {
        }
    }
}

- (void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        NSString* identifier = [self returnStringCallWithKey1:"Cell" key2:IDENTIFIER section:indexPath.section row:indexPath.row];
        if ( identifier ) {
            // 参数 cell,section,row
            lv_settop(l, 0);
            [self callByKey1:"Cell" key2:identifier.UTF8String key3:STR_CALLBACK section:indexPath.section row:indexPath.row];
        }
    }
}

// 回调脚本返回一个用户数据
- (LVUserDataView *) callReturnUserDataFunction:(const char*) key1 key2:(const char*)key2 section:(NSInteger) section {
    lv_State* l = self.owner.lv_lview.l;
    if( l ){
        lv_checkstack(l, 12);
        lv_pushnumber(l, section+1);
        
        // table
        lv_pushUserdata(l, self.owner.lv_userData);
        lv_pushUDataRef(l, USERDATA_KEY_DELEGATE);
        if(  [LVUtil call:l key1:key1 key2:key2 nargs:1 nrets:1] ==0 ) {
            if( lv_type(l, -1)==LV_TUSERDATA ) {
                LVUserDataView * user = (LVUserDataView *)lv_touserdata(l, -1);
                return user;
            }
        }
    }
    return nil;
}

@end

//
//  LVImageView.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LView.h"


@interface LVImageView : UIImageView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;
@property(nonatomic,assign) CGFloat lv_rotation;
@property(nonatomic,assign) CGFloat lv_rotationX;
@property(nonatomic,assign) CGFloat lv_rotationY;
@property(nonatomic,assign) CGFloat lv_scaleX;
@property(nonatomic,assign) CGFloat lv_scaleY;

-(id) init:(lv_State*) l;

-(void) setImageByName:(NSString*) imageName;
-(void) setImageByData:(NSData*) data;
-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished;

/*
 * Lua 脚本回调
 */
-(void) callLuaDelegate;

+(int) classDefine:(lv_State *) L ;

/*
 * 修改LVImageView
 */
+ (void) setDefaultStyle:(Class) c;


@end

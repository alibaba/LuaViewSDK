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


@interface LVImage : UIImageView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

-(id) init:(lv_State*) l;

-(void) setImageByName:(NSString*) imageName;
-(void) setImageByData:(NSData*) data;
-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished;

/*
 * Lua 脚本回调
 */
-(void) callLuaDelegate:(id) obj;

+(int) classDefine:(lv_State *) L ;

/*
 * 修改LVImageView
 */
+ (void) setDefaultStyle:(Class) c;


@end

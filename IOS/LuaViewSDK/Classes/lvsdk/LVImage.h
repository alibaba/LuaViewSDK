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


@interface LVImage : UIImageView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lv_State*) l;

-(void) setImageByName:(NSString*) imageName;
-(void) setImageByData:(NSData*) data;
-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished;
-(void) effectParallax:(CGFloat)dx dy:(CGFloat)dy ;
-(void) effectClick:(NSInteger)color alpha:(CGFloat)alpha;

/*
 * Lua 脚本回调
 */
-(void) callLuaDelegate:(id) obj;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

/**
 *  图片首次出现是否使用动画
 */
@property (nonatomic,assign) BOOL disableAnimate;

@end

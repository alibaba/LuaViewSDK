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

-(id) init:(lv_State*) l;

-(void) setImageByName:(NSString*) imageName;
-(void) setImageByData:(NSData*) data;

+(int) classDefine:(lv_State *) L ;

+ (void) setDefaultStyle:(Class) c;

@end

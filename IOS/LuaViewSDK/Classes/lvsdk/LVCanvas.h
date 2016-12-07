//
//  LVCanvas.h
//  LuaViewSDK
//
//  Created by 董希成 on 2016/12/5.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LVHeads.h"

@interface LVCanvas : NSObject<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,assign) CGContextRef contentRef;

@property(nonatomic,assign) CGPathDrawingMode drawingMode;

-(id) init:(lv_State*) l;

+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName;

+(int) createLuaCanvas:(lv_State *)L  contentRef:(CGContextRef) contentRef;

@end

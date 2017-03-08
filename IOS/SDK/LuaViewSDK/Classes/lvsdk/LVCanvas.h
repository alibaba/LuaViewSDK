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

@property(nonatomic,weak) LuaViewCore* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;

@property(nonatomic,assign) CGContextRef contentRef;

@property(nonatomic,assign) CGPathDrawingMode drawingMode;

-(id) init:(lua_State*) l;

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

+(LVCanvas*) createLuaCanvas:(lua_State *)L  contentRef:(CGContextRef) contentRef;

@end

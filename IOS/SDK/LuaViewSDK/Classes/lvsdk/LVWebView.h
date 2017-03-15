//
//  LVWebView.h
//  LuaViewSDK
//
//  Created by 董希成 on 16/10/10.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LVHeads.h"
#import "LView.h"

@interface LVWebView : UIView<LVProtocal, LVClassProtocal>

@property(nonatomic,weak) LuaViewCore* lv_luaviewCore;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;
@property(nonatomic,strong) CAShapeLayer* lv_shapeLayer;

-(id) init:(lua_State*) l;


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName;

//--------------------------------------
-(void) createWebView;
-(BOOL) canGoBack;
-(void) goBack;
-(BOOL) canGoForward;
-(void) goForward;
-(void) reload;
-(void) stopLoading;
-(BOOL) isLoading;
-(NSString*) title;
-(void) loadUrl:(NSString*) url;
-(NSString*) url;

// 开启下拉刷新（是、否）
-(void) setPullRefreshEnable:(BOOL) t;
// 返回下拉刷新是否开启
-(BOOL) pullRefreshEnable;

@end

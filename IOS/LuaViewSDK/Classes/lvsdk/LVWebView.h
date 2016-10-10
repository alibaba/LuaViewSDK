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

@interface LVWebView : UIView<LVProtocal>

@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
@property(nonatomic,assign) NSUInteger lv_align;

-(id) init:(lv_State*) l;


+(int) classDefine:(lv_State *) L ;

/*
 * 修改LVImageView
 */
+ (void) setDefaultStyle:(Class) c;


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

@end

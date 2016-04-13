//
//  LVBaseView.m
//  JU
//
//  Created by dongxicheng on 12/29/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#import "LVBaseView.h"
#import "LView.h"
#import "LVTransform3D.h"
#import "LVAnimator.h"
#import <QuartzCore/QuartzCore.h>
#import "LVStruct.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import "JUFLXLayoutKit.h"
#import "UIView+JUFLXNode.h"

@interface LVBaseView ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVBaseView


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.clipsToBounds = YES;
        self.lv_isCallbackAddClickGesture = YES;
    }
    return self;
}

static void releaseUserDataView(LVUserDataInfo* userdata){
    if( userdata && userdata->object ){
        UIView<LVProtocal>* view = CFBridgingRelease(userdata->object);
        userdata->object = NULL;
        if( view ){
            view.lv_userData = nil;
            view.lv_lview = nil;
            [view removeFromSuperview];
        }
    }
}

-(void) dealloc {
}

#pragma -mark center
static int center (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lv_gettop(L)>=2  ) {
                if ( lv_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lv_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&center, [stru dataPointer], sizeof(CGPoint));
                        }
                    } else {
                        LVError(@"LVBaseView.setCenter1");
                    }
                } else {
                    if( lv_isnumber(L, 2) ){
                        center.x = lv_tonumber(L, 2);// 2
                    }
                    if( lv_isnumber(L, 3) ){
                        center.y = lv_tonumber(L, 3);// 3
                    }
                }
                if(  isnan(center.x) || isnan(center.y) ){
                    LVError(@"LVBaseView.setCenter2");
                } else {
                    view.center = center;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, center.x );
                lv_pushnumber(L, center.y );
                return 2;
            }
        }
    }
    return 0;
}

static int centerX(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lv_gettop(L)>=2  ) {
                if( lv_isnumber(L, 2) ){
                    center.x = lv_tonumber(L, 2);// 2
                }
                if(  isnan(center.x) || isnan(center.y) ){
                    LVError(@"LVBaseView.setCenterX2");
                } else {
                    view.center = center;
                }
                return 0;
            } else {
                lv_pushnumber(L, center.x );
                return 1;
            }
        }
    }
    return 0;
}

static int centerY(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lv_gettop(L)>=2  ) {
                if( lv_isnumber(L, 2) ){
                    center.y = lv_tonumber(L, 2);// 2
                }
                if(  isnan(center.x) || isnan(center.y) ){
                    LVError(@"LVBaseView.setCenterX2");
                } else {
                    view.center = center;
                }
                return 0;
            } else {
                lv_pushnumber(L, center.y );
                return 1;
            }
        }
    }
    return 0;
}

#pragma -mark frame
static int frame (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if( lv_gettop(L)>=2 ) {
                if ( lv_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lv_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r, [stru dataPointer], sizeof(CGRect));
                        }
                    } else {
                        LVError(@"LVBaseView.setFrame1");
                    }
                } else {
                    if( lv_isnumber(L, 2) ){
                        r.origin.x = lv_tonumber(L, 2);// 2
                    }
                    if( lv_isnumber(L, 3) ){
                        r.origin.y = lv_tonumber(L, 3);// 3
                    }
                    if( lv_isnumber(L, 4) ){
                        r.size.width = lv_tonumber(L, 4);// 4
                    }
                    if( lv_isnumber(L, 5) ){
                        r.size.height = lv_tonumber(L, 5);// 5
                    }
                }
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.setFrame2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.x    );
                lv_pushnumber(L, r.origin.y    );
                lv_pushnumber(L, r.size.width  );
                lv_pushnumber(L, r.size.height );
                return 4;
            }
        }
    }
    return 0;
}

#pragma -mark frame
static int size (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                if ( lv_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lv_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r.size, [stru dataPointer], sizeof(CGSize));
                        }
                    } else {
                        LVError(@"LVBaseView.setSize1");
                    }
                } else {
                    if( lv_isnumber(L, 2) ){
                        r.size.width = lv_tonumber(L, 2);// 4
                    }
                    if( lv_isnumber(L, 3) ){
                        r.size.height = lv_tonumber(L, 3);// 5
                    }
                }
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.setSize2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lv_pushnumber(L, r.size.width  );
                lv_pushnumber(L, r.size.height );
                return 2;
            }
        }
    }
    return 0;
}

static int origin (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                if ( lv_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lv_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r.origin, [stru dataPointer], sizeof(CGPoint));
                        }
                    } else {
                        LVError(@"LVBaseView.setOrigin1");
                    }
                } else {
                    if( lv_isnumber(L, 2) ){
                        r.origin.x = lv_tonumber(L, 2);// 2
                    }
                    if( lv_isnumber(L, 3) ){
                        r.origin.y = lv_tonumber(L, 3);// 3
                    }
                }
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.setOrigin2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.x    );
                lv_pushnumber(L, r.origin.y    );
                return 2;
            }
        }
    }
    return 0;
}

static int x (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.origin.x = lv_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.x );
                return 1;
            }
        }
    }
    return 0;
}

#pragma - mark flxNode
static int flxChildViews(lv_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if (user) {
        UIView *view = (__bridge UIView *)(user->object);
        int childNum = lv_gettop(L);
        if (view && childNum>=2 ) {
            NSMutableArray* childs = [[NSMutableArray alloc] init];
            for( int i=2; i<=childNum; i++ ) {
                LVUserDataInfo * childUser = (LVUserDataInfo *)lv_touserdata(L, i);
                UIView* temp = (__bridge UIView *)(childUser->object);
                if( temp ) {
                    [childs addObject:temp.ju_flxNode];
                }
            }
            view.ju_flxNode.childNodes = childs;
            return 0;
        }
    }
    return 0;
}

static int flxBindingInlineCSS(lv_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lv_touserdata(L, 1);
    UIView *view = (__bridge UIView *)(user->object);
    
    int childNum = lv_gettop(L);
    if (view && childNum==2) {
        if (lv_type(L, 2) == LV_TSTRING) {
            [view.ju_flxNode bindingInlineCSS:[NSString stringWithUTF8String:lv_tostring(L, 2)]];
            return 0;
        }
    }
    return 0;
}

//static int flxMeasure(lv_State *L)
//{
//    LVUserDataView *user = (LVUserDataView *)lv_touserdata(L, 1);
//    UIView *view = (__bridge UIView *)(user->view);
//    int childNum = lv_gettop(L);
//    if (view && childNum == 2) {
//        if (lv_type(L, 2) == LV_TFUNCTION) {
//            lv_pushvalue(L, 1);
//            lv_pushvalue(L, 2);
//            lv_udataRef(L, USERDATA_FLEX_DELEGATE);
//        }
//        view.ju_flxNode.measure = ^CGSize(CGFloat width) {
//            lv_pushUserdata(L, user );
//            lv_pushUDataRef(L, USERDATA_FLEX_DELEGATE );
//            lv_pushnumber(L, width);
//            lv_runFunctionWithArgs(L, 1, 2);
//            CGSize size = CGSizeZero;
//            size.width = lv_tonumber(L, -2);
//            size.height = lv_tonumber(L, -1);
//            return size;
//        };
//
//    }
//    return 0;
//}

static int flxLayout(lv_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if (user) {
        BOOL async = FALSE;
        int argNum = lv_gettop(L);
        for ( int i=1; i<=argNum; i++ ) {
            if ( lv_type(L, i)==LV_TBOOLEAN ){
                async = lv_toboolean(L, i);
            }
            if( lv_type(L, i) == LV_TFUNCTION ) {
                lv_pushvalue(L, 1);
                lv_pushvalue(L, i);
                lv_udataRef(L, USERDATA_FLEX_DELEGATE);
            }
        }
        UIView *view = (__bridge UIView *)(user->object);
        [view.ju_flxNode layoutAsync:async completionBlock:^(CGRect frame) {
            lv_pushUserdata(L, user );
            lv_pushUDataRef(L, USERDATA_FLEX_DELEGATE );
            lv_runFunction(L);
        }];
    }
    return 0;
}


static int y (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.origin.y = lv_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.y );
                return 1;
            }
        }
    }
    return 0;
}

static int bottom (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.origin.y = lv_tonumber(L, 2)-r.size.height;// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.y + r.size.height );
                return 1;
            }
        }
    }
    return 0;
}

static int right (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.origin.x = lv_tonumber(L, 2)-r.size.width;// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lv_pushnumber(L, r.origin.x + r.size.width );
                return 1;
            }
        }
    }
    return 0;
}

static int width (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.size.width = lv_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lv_pushnumber(L, r.size.width );
                return 1;
            }
        }
    }
    return 0;
}

static int height (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lv_gettop(L)>=2 ) {
                r.size.height = lv_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lv_pushnumber(L, r.size.height );
                return 1;
            }
        }
    }
    return 0;
}

static int addGestureRecognizer (lv_State *L) {
    LVUserDataInfo * userDataView = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * userDataGesture = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( userDataView && LVIsType(userDataGesture, Gesture) ){
        UIView* view = (__bridge UIView *)(userDataView->object);
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userDataGesture->object);
        if( view && gesture ){
            [view addGestureRecognizer:gesture];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int removeGestureRecognizer (lv_State *L) {
    LVUserDataInfo * userDataView = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * userDataGesture = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( userDataView && LVIsType(userDataGesture, Gesture) ){
        UIView* view = (__bridge UIView *)(userDataView->object);
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userDataGesture->object);
        if( view && gesture ){
            [view removeGestureRecognizer:gesture];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int addSubview (lv_State *L) {
    LVUserDataInfo * father = (LVUserDataInfo *)lv_touserdata(L, 1);
    LVUserDataInfo * son = (LVUserDataInfo *)lv_touserdata(L, 2);
    if( father &&  LVIsType(son, View) ){
        UIView* viewRoot = (__bridge UIView *)(father->object);
        UIView* viewSub = (__bridge UIView *)(son->object);
        if( viewRoot && viewSub ){
            [viewSub removeFromSuperview];
            [viewRoot addSubview:viewSub];
            
            [viewSub lv_alignSelfWithSuperRect:viewRoot.frame];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}


static int getNativeView (lv_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( userData ){
        UIView* view = (__bridge UIView *)(userData->object);
        if( view ){
            id object = [view lv_getNativeView];
            lv_pushNativeObjectWithBox(L, object);
            return 1;
        }
    }
    return 0;
}

#pragma -mark 运行环境
static int children (lv_State *L) {
    LView* lview = (__bridge LView *)(L->lView);
    UIView* oldContent = lview.conentView;
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    
    UIView* newContentView = (__bridge UIView *)(user->object);
    if ( lview && newContentView && lv_type(L, 2)==LV_TFUNCTION ) {
        lv_settop(L, 2);
        lview.conentView = newContentView;
        lv_runFunctionWithArgs(L, 1, 0);
    }
    lview.conentView = oldContent;
    return 0;
}


static int removeFromSuperview (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            [view removeFromSuperview];
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int removeAllSubviews (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            while (view.subviews.count) {
                UIView* child = view.subviews.lastObject;
                [child removeFromSuperview];
            }
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

#pragma -mark hidden
static int hidden(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lv_gettop(L)>=2 ) {
                BOOL yes = lvL_checkbool(L, 2);
                view.hidden = yes;
                return 0;
            } else {
                lv_pushboolean(L, view.hidden );
                return 1;
            }
        }
    }
    return 0;
}

static int hide(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.hidden = YES;
            return 0;
        }
    }
    return 0;
}

static int show(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.hidden = NO;
            return 0;
        }
    }
    return 0;
}

static int isShow(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lv_pushboolean(L, !view.hidden );
            return 1;
        }
    }
    return 0;
}

static int isHide(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lv_pushboolean(L, view.hidden );
            return 1;
        }
    }
    return 0;
}

static int becomeFirstResponder(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if( view.canBecomeFirstResponder )
                [view becomeFirstResponder];
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int resignFirstResponder(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if( view.canResignFirstResponder)
                [view resignFirstResponder];
            
            lv_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int isFirstResponder(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lv_pushboolean(L, view.isFirstResponder?1:0 );
            return 1;
        }
    }
    return 0;
}

#pragma -mark userInteractionEnabled
static int userInteractionEnabled(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lv_gettop(L)>=2 ) {
                BOOL yes = lvL_checkbool(L, 2);
                view.userInteractionEnabled = yes;
                return 0;
            } else {
                lv_pushboolean(L, view.userInteractionEnabled );
                return 1;
            }
        }
    }
    return 0;
}

#pragma -mark backgroundColor
static int backgroundColor (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( [view isKindOfClass:[UIView class]] ){
            if( lv_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.backgroundColor = color;
                return 0;
            } else {
                UIColor* color = view.backgroundColor;
                NSUInteger c = 0;
                CGFloat a = 0;
                if( lv_uicolor2int(color, &c,&a) ){
                    lv_pushnumber(L, c );
                    lv_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

#pragma -mark alpha
static int alpha (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lv_gettop(L)>=2 ) {
            CALayer* layer = view.layer;

            double alpha = lv_tonumber(L, 2);// 2
            layer.opacity = alpha;
            
            lv_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            float alpha = layer.opacity;
            lv_pushnumber(L, alpha );
            return 1;
        }
    }
    return 0;
}

#pragma -mark cornerRadius
static int cornerRadius (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            double r = lv_tonumber(L, 2);// 2
            view.layer.cornerRadius = r;
            return 0;
        } else {
            float r = view.layer.cornerRadius;
            lv_pushnumber(L, r );
            return 1;
        }
    }
    return 0;
}

#pragma -mark borderWidth
static int borderWidth (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            double width = lv_tonumber(L, 2);// 2
            view.layer.borderWidth = width;
            return 0;
        } else {
            float w = view.layer.borderWidth;
            lv_pushnumber(L, w );
            return 1;
        }
    }
    return 0;
}

#pragma -mark shadow
static int setShadowPath (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view layer].shadowPath =[UIBezierPath bezierPathWithRect:view.bounds].CGPath;
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}
static int setMasksToBounds (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    BOOL masksToBounds = lvL_checkbool(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        view.layer.masksToBounds = masksToBounds;
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowOffset (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    float x = lv_tonumber(L, 2);// 2
    float y = lv_tonumber(L, 3);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowOffset:CGSizeMake(x, y)];
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowRadius (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    float radius = lv_tonumber(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowRadius:radius];
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowOpacity (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    float opacity = lv_tonumber(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowOpacity:opacity];
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowColor (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user && lv_gettop(L)>=2 ){
        UIView* view = (__bridge UIView *)(user->object);
        UIColor* color = lv_getColorFromStack(L, 2);
        [view.layer setShadowColor:color.CGColor];
        lv_pushvalue(L,1);
        return 1;
    }
    return 0;
}


#pragma -mark borderColor

UIColor* lv_UIColorFromRGBA(NSInteger rgbValue, float alphaValue){
    return [UIColor
         colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0
         green:       ((float)((rgbValue & 0x00FF00) >> 8 ))/255.0
         blue:        ((float)((rgbValue & 0x0000FF)      ))/255.0
         alpha:alphaValue];

}

static int borderColor (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lv_gettop(L)>=2 ) {
            UIColor* color = lv_getColorFromStack(L, 2);
            view.layer.borderColor = color.CGColor;
            lv_pushvalue(L,1);
            return 1;
        } else {
            UIColor* color = [UIColor colorWithCGColor:view.layer.borderColor];
            NSUInteger c = 0;
            CGFloat a = 0;
            if( lv_uicolor2int(color, &c, &a) ){
                lv_pushnumber(L, c );
                lv_pushnumber(L, a);
                return 2;
            }
        }
    }
    return 0;
}

#pragma -mark clipsToBounds
//static int clipsToBounds(lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        UIView* view = (__bridge UIView *)(user->view);
//        if( view ){
//            if( lv_gettop(L)>=2 ) {
//                BOOL yes = lvL_checkbool(L, 2);
//                view.clipsToBounds = yes;
//                return 0;
//            } else {
//                lv_pushnumber(L, view.clipsToBounds );
//                return 1;
//            }
//        }
//    }
//    return 0;
//}

static int adjustSize(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            [view sizeToFit];
        }
    }
    return 0;
}

#pragma -mark transformRoteAndScale

typedef void (TransformSetter)(CATransform3D *, CGFloat);
typedef double (TransformGetter)(CATransform3D *);

static int transformFuncOneArg(lv_State *L, TransformSetter setter, TransformGetter getter) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    UIView *view = (__bridge UIView *)(user->object);
    
    if( user ){
        if ( lv_gettop(L) > 1 ) {
            CALayer* layer = view.layer;

            double x = lv_tonumber(L, 2);
            CATransform3D t = layer.transform;
            setter(&t, x);
            
            layer.transform = t;
            
            lv_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            CATransform3D t = layer.transform;
            double x = getter(&t);
            
            lv_pushnumber(L, x);
            return 1;
        }
    }
    return 0;
}

static int transformFuncTwoArg(lv_State *L,
                                 TransformSetter xsetter, TransformSetter ysetter,
                                 TransformGetter xgetter, TransformGetter ygetter) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    UIView *view = (__bridge UIView *)(user->object);
    
    if( user ){
        int argNum = lv_gettop(L);
        if ( argNum > 1 ) {
            CALayer* layer = view.layer;

            double x = lv_tonumber(L, 2), y = lv_tonumber(L, 3);
            CATransform3D t = layer.transform;
            xsetter(&t, x);
            ysetter(&t, y);
            
            layer.transform = t;
            
            lv_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            CATransform3D t = layer.transform;
            double x = xgetter(&t), y = ygetter(&t);
            
            lv_pushnumber(L, x);
            lv_pushnumber(L, y);
            return 2;
        }
    }
    return 0;
}

inline static double degreeToRadian(double d) {
    return d * M_PI / 180;
}

inline static double radianToDegree(double r) {
    return r / M_PI * 180;
}

static void transform3DSetDegreeRotation(CATransform3D *t, CGFloat v) {
    double r = degreeToRadian(v);
    CATransform3DSetRotation(t, r);
}

static double transform3DGetDegreeRotation(CATransform3D *t) {
    double r = CATransform3DGetRotation(t);
    return radianToDegree(r);
}

static int rotationZ (lv_State *L) {
    return transformFuncOneArg(L, transform3DSetDegreeRotation,
                               transform3DGetDegreeRotation);
}

static int rotationX (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CALayer *layer = view.layer;

            double angle = degreeToRadian(lv_tonumber(L, 2));
            layer.transform = CATransform3DMakeRotation(angle, 1, 0, 0);
            
            lv_pop(L, 1);
            return 1;
        } else {
            CALayer *layer = view.layer.presentationLayer ?: view.layer;
            
            double angle = [[layer valueForKeyPath:@"transform.rotation.x"] doubleValue];
            lv_pushnumber(L, angle);
            return 1;
        }
    }
    return 0;
}

static int rotationY (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            CALayer *layer = view.layer;

            double angle = degreeToRadian(lv_tonumber(L, 2));
            layer.transform = CATransform3DMakeRotation(angle, 0, 1, 0);
            
            lv_pop(L, 1);
            return 1;
        } else {
            CALayer *layer = view.layer.presentationLayer ?: view.layer;

            double angle = [[layer valueForKeyPath:@"transform.rotation.y"] doubleValue];
            lv_pushnumber(L, angle);
            return 1;
        }
    }
    return 0;
}

static int scale (lv_State *L) {
    if (lv_gettop(L) == 2) {
        lv_pushnumber(L, lv_tonumber(L, 2));
    }
    return transformFuncTwoArg(L, CATransform3DSetScaleX, CATransform3DSetScaleY,
                                 CATransform3DGetScaleX, CATransform3DGetScaleY);
}

static int scaleX (lv_State *L) {
    return transformFuncOneArg(L, CATransform3DSetScaleX, CATransform3DGetScaleX);
}

static int scaleY (lv_State *L) {
    return transformFuncOneArg(L, CATransform3DSetScaleY, CATransform3DGetScaleY);
}

static int translation (lv_State *L) {
    return transformFuncTwoArg(L, CATransform3DSetTranslationX, CATransform3DSetTranslationY,
                                 CATransform3DGetTranslationX, CATransform3DGetTranslationY);
}

static int translationX (lv_State *L) {
    return transformFuncOneArg(L, CATransform3DSetTranslationX, CATransform3DGetTranslationX);
}

static int translationY (lv_State *L) {
    return transformFuncOneArg(L, CATransform3DSetTranslationY, CATransform3DGetTranslationY);
}

static int transform3D (lv_State *L) {
    LVUserDataInfo* user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lv_gettop(L)>=2 ) {
            LVUserDataInfo* userdata = (LVUserDataInfo *)lv_touserdata(L, 2);
            if ( LVIsType(userdata, Transform3D)) {
                CALayer *layer = view.layer;

                LVTransform3D* tran = (__bridge LVTransform3D *)(userdata->object);
                layer.transform = tran.transform;
                
                lv_pop(L, 1);
                return 1;
            }
        } else {
            CALayer *layer = view.layer.presentationLayer ?: view.layer;

            CATransform3D t = layer.transform;
            [LVTransform3D pushTransform3D:L transform3d:t];
            return 1;
        }
    }
    return 0;
}

static int startAnimation(lv_State *L) {
    LVUserDataInfo* vdata = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( vdata ){
        LVUserDataInfo *adata = NULL;
        LVAnimator *animator = nil;
        int top = lv_gettop(L);
        for (int i = 2; i <= top; ++i) {
            adata = lv_touserdata(L, i);
            if (!LVIsType(adata, Animator)) {
                continue;
            }
            
            animator = (__bridge LVAnimator *)(adata->object);
            if (animator == nil) {
                continue;
            }
            
            animator.target = (__bridge UIView *)(vdata->object);
            [animator start];
        }
    }
    
    return 0;
}

static int stopAnimation(lv_State *L) {
    LVUserDataInfo* user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer removeAllAnimations];
    }
    
    return 0;
}

#pragma -mark anchorPoint
static int anchorPoint (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lv_gettop(L)>=2 ) {
            double x = lv_tonumber(L, 2);// 2
            double y = lv_tonumber(L, 3);// 3
            view.layer.anchorPoint = CGPointMake(x, y);
            return 0;
        } else {
            CGPoint p = view.layer.anchorPoint;
            lv_pushnumber(L, p.x );
            lv_pushnumber(L, p.y );
            return 2;
        }
    }
    return 0;
}

int lv_setCallbackByKey(lv_State *L, const char* key, BOOL addGesture) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        if ( lv_gettop(L)>=2 ) {
            UIView* view = (__bridge UIView *)(user->object);
            if( addGesture ) {
                [view lv_callbackAddClickGesture];// 检测是否添加手势
            }
            lv_checkstack(L, 8);
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if( lv_type(L, -1)==LV_TNIL ) {
                lv_settop(L, 2);
                lv_pushvalue(L, 1);
                lv_createtable(L, 0, 0);
                lv_udataRef(L, USERDATA_KEY_DELEGATE );
                
                lv_settop(L, 2);
                lv_pushvalue(L, 1);
                lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            }
            lv_pushvalue(L, 2);
            if( key==NULL && lv_type(L, -1) == LV_TTABLE ) {
                // 如果是表格 设置每个Key
                lv_pushnil(L);
                while (lv_next(L, -2))
                {
                    NSString* key   = lv_paramString(L, -2);
                    lv_setfield(L, -4, key.UTF8String);
                }
            } else {
                // 如果是方法设置默认key
                lv_setfield(L, -2, (key ? key:STR_ON_CLICK) );
            }
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if ( key ) {
                if ( lv_type(L, -1)==LV_TTABLE ) {
                    lv_getfield(L, -1, key);
                } else {
                    lv_pushnil(L);
                }
            }
            return 1;
        }
    }
    return 0;
}

static int callback (lv_State *L) {
    return lv_setCallbackByKey(L, NULL, YES);
}

static int onLayout (lv_State *L) {
    return lv_setCallbackByKey(L, STR_ON_LAYOUT, NO);
}

static int onClick (lv_State *L) {
    return lv_setCallbackByKey(L, STR_ON_CLICK, YES);
}

#pragma -mark __gc
static int __gc (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    releaseUserDataView(user);
    return 0;
}

static int __tostring (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView<LVProtocal>* view = (__bridge UIView<LVProtocal> *)(user->object);
        if( view ){
            NSString* s = [NSString stringWithFormat:@"%@",view];
            lv_pushstring(L, s.UTF8String);
            return 1;
        }
    }
    return 0;
}

- (void) layoutSubviews{
    [super layoutSubviews];
    [self lv_alignSubviews];
    [self lv_runCallBack:STR_ON_LAYOUT];
}

static int releaseObject(lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        //[LVUtil unregistry:L key:(__bridge id)user->view];
        UIView* view = (__bridge UIView *)(user->object);
        if( [view isKindOfClass:[LView class]] ){
            LView* lView = (LView*)view;
            L->lView = NULL;
            [lView releaseLuaView];
        }
        [view removeFromSuperview];
    }
    return 0;
}
//---------------------------
//static int __newindex (lv_State *L) {
//    NSString* key = lv_paramString(L, 2);
//    if( key ){
//        lv_getmetatable( L, 1 );
//        lv_getfield(L, -1, key.UTF8String);
//        if( lv_type(L, -1)==LV_TFUNCTION ) {
//            lv_CFunction function =  lv_tocfunction(L,-1);
//            if( function ) {
//                lv_remove(L, 2);
//                lv_settop(L, 2);
//                return function(L);
//            }
//        }
//    }
//    LVError(@"not found property: %@", key);
//    return 0;
//}


static int align (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            int argNum = lv_gettop(L);
            if ( argNum>=2 ) {
                NSUInteger align = 0;
                for (int i=2; i<=argNum; i++ ) {
                    align |= (NSUInteger)lv_tointeger(L, i);
                }
                view.lv_align = align;
                [view lv_alignSelfWithSuperRect:view.superview.frame];
                return 0;
            } else {
                lv_pushnumber(L, view.lv_align );
                return 1;
            }
        }
    }
    return 0;
}

static int alignInfo (lv_State *L, int align) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.lv_align = align;
            [view lv_alignSelfWithSuperRect:view.superview.frame];
        }
    }
    return 0;
}

static int alignLeft(lv_State *L ) {
    return alignInfo(L, LV_ALIGN_LEFT);
}

static int alignRight(lv_State *L ) {
    return alignInfo(L, LV_ALIGN_RIGHT);
}

static int alignTop(lv_State *L ) {
    return alignInfo(L, LV_ALIGN_TOP);
}

static int alignBottom(lv_State *L ) {
    return alignInfo(L, LV_ALIGN_BOTTOM);
}

static int alignCenter(lv_State *L ) {
    return alignInfo(L, LV_ALIGN_H_CENTER|LV_ALIGN_V_CENTER);
}


static const struct lvL_reg baseMemberFunctions [] = {
    {"hidden",    hidden },
    
    {"hide",    hide },
    {"isHide",    isHide },
    
    {"show",    show },
    {"isShow",    isShow },
    
    {"enabled",    userInteractionEnabled },
    
    {"backgroundColor",     backgroundColor },
    
    {"alpha",       alpha },
    
    {"cornerRadius",        cornerRadius },
    
    {"borderWidth",         borderWidth },
    
    {"borderColor",         borderColor },
    
    {"shadowPath",       setShadowPath },
    {"masksToBounds",    setMasksToBounds },
    {"shadowOffset",     setShadowOffset },
    {"shadowRadius",     setShadowRadius },
    {"shadowOpacity",    setShadowOpacity },
    {"shadowColor",      setShadowColor },
    
    {"frame",     frame },
    
    {"size",     size },
    
    {"origin",     origin },
    {"xy",     origin },
    
    {"center",    center},
    {"centerX",    centerX},
    {"centerY",    centerY},
    
    {"x",    x},
    {"y",    y},
    
    {"left", x},
    {"top",     y},
    {"bottom", bottom},
    {"right",  right},
    
    {"width",    width},
    {"height",    height},
    
    {"adjustSize", adjustSize},
    
    {"addGesture",          addGestureRecognizer },
    {"removeGesture",       removeGestureRecognizer },
    
    {"addView",          addSubview },
    {"children", children },
    {"removeFromSuper", removeFromSuperview },
    {"removeFromParent", removeFromSuperview },
    {"removeAllViews", removeAllSubviews },
    
    {"rotation",  rotationZ },
    {"rotationX", rotationX },
    {"rotationY", rotationY },
    {"rotationZ", rotationZ },
    
    {"scale", scale },
    {"scaleX", scaleX },
    {"scaleY", scaleY },
    
    {"translation", translation },
    {"translationX", translationX },
    {"translationY", translationY },
    
    {"anchorPoint",     anchorPoint },
    
    {"callback",     callback },
    {"onLayout",     onLayout },
    {"onClick",     onClick },
    
    {"hasFocus",        isFirstResponder },
    {"requestFocus",    becomeFirstResponder },
    {"cancelFocus",    resignFirstResponder },
    
    {"transform3D",    transform3D },
    
    {"startAnimation", startAnimation },
    {"stopAnimation", stopAnimation },
    
    {"release",     releaseObject},
    
    {"__gc",        __gc },
    
    {"__tostring",  __tostring},
    
    // {"__newindex",  __newindex },

    {"flexChildren",  flxChildViews },
    {"flxLayout",  flxLayout },
    {"flexCss", flxBindingInlineCSS},
  
    // align
    {"align", align},
    {"alignLeft", alignLeft},
    {"alignRight", alignRight},
    {"alignTop", alignTop},
    {"alignBottom", alignBottom},
    {"alignCenter", alignCenter},
    
    // getNativeView
    {"getNativeView", getNativeView},
    {"nativeView", getNativeView},
    {NULL, NULL}
};

static const struct lvL_reg luaViewMemberFunctions [] = {
    {NULL,    NULL },
};

+(const lvL_reg*) baseMemberFunctions{
    return baseMemberFunctions;
}

#pragma -mark UIView
static int lvNewView (lv_State *L) {
    LVBaseView* view = [[LVBaseView alloc] init:L];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(view);
        view.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UIView );
        lv_setmetatable(L, -2);
        
        LView* lView = (__bridge LView *)(L->lView);
        if( lView ){
            [lView containerAddSubview:view];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) classDefine: (lv_State *)L {
    {
        lv_pushcfunction(L, lvNewView);
        lv_setglobal(L, "View");
    }
    lv_createClassMetaTable(L, META_TABLE_UIView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    
    
    lv_createClassMetaTable(L, META_TABLE_LuaView);
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, luaViewMemberFunctions, 0);
    return 1;
}

//----------------------------------------------------------------------------------------

-(NSString*) description{
    return [NSString stringWithFormat:@"<View(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

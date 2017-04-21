/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVBaseView.h"
#import "LView.h"
#import "LVTransform3D.h"
#import "LVAnimator.h"
#import <QuartzCore/QuartzCore.h>
#import "LVStruct.h"
#import "JUFLXLayoutKit.h"
#import "UIView+JUFLXNode.h"
#import "LVGesture.h"
#import "LVHeads.h"

@interface LVBaseView ()
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVBaseView


-(id) init:(lua_State*) L{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(L);
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
            view.lv_userData = NULL;
            view.lv_luaviewCore = nil;
            if( !userdata->isWindow ) {
                [view removeFromSuperview];
                [view.layer removeFromSuperlayer];
            }
        }
    }
}

-(void) dealloc {
}

#pragma -mark center
static int center (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lua_gettop(L)>=2  ) {
                if ( lua_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lua_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&center, [stru dataPointer], sizeof(CGPoint));
                        }
                    } else {
                        LVError(@"LVBaseView.setCenter1");
                    }
                } else {
                    if( lua_isnumber(L, 2) ){
                        center.x = lua_tonumber(L, 2);// 2
                    }
                    if( lua_isnumber(L, 3) ){
                        center.y = lua_tonumber(L, 3);// 3
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
                lua_pushnumber(L, center.x );
                lua_pushnumber(L, center.y );
                return 2;
            }
        }
    }
    return 0;
}

static int centerX(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lua_gettop(L)>=2  ) {
                if( lua_isnumber(L, 2) ){
                    center.x = lua_tonumber(L, 2);// 2
                }
                if(  isnan(center.x) || isnan(center.y) ){
                    LVError(@"LVBaseView.setCenterX2");
                } else {
                    view.center = center;
                }
                return 0;
            } else {
                lua_pushnumber(L, center.x );
                return 1;
            }
        }
    }
    return 0;
}

static int centerY(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGPoint center = view.center;
            if ( lua_gettop(L)>=2  ) {
                if( lua_isnumber(L, 2) ){
                    center.y = lua_tonumber(L, 2);// 2
                }
                if(  isnan(center.x) || isnan(center.y) ){
                    LVError(@"LVBaseView.setCenterX2");
                } else {
                    view.center = center;
                }
                return 0;
            } else {
                lua_pushnumber(L, center.y );
                return 1;
            }
        }
    }
    return 0;
}

#pragma -mark frame
static int frame (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if( lua_gettop(L)>=2 ) {
                if ( lua_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lua_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r, [stru dataPointer], sizeof(CGRect));
                        }
                    } else {
                        LVError(@"LVBaseView.setFrame1");
                    }
                } else {
                    if( lua_isnumber(L, 2) ){
                        r.origin.x = lua_tonumber(L, 2);// 2
                    }
                    if( lua_isnumber(L, 3) ){
                        r.origin.y = lua_tonumber(L, 3);// 3
                    }
                    if( lua_isnumber(L, 4) ){
                        r.size.width = lua_tonumber(L, 4);// 4
                    }
                    if( lua_isnumber(L, 5) ){
                        r.size.height = lua_tonumber(L, 5);// 5
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
                lua_pushnumber(L, r.origin.x    );
                lua_pushnumber(L, r.origin.y    );
                lua_pushnumber(L, r.size.width  );
                lua_pushnumber(L, r.size.height );
                return 4;
            }
        }
    }
    return 0;
}

#pragma -mark frame
static int size (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                if ( lua_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lua_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r.size, [stru dataPointer], sizeof(CGSize));
                        }
                    } else {
                        LVError(@"LVBaseView.setSize1");
                    }
                } else {
                    if( lua_isnumber(L, 2) ){
                        r.size.width = lua_tonumber(L, 2);// 4
                    }
                    if( lua_isnumber(L, 3) ){
                        r.size.height = lua_tonumber(L, 3);// 5
                    }
                }
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.setSize2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lua_pushnumber(L, r.size.width  );
                lua_pushnumber(L, r.size.height );
                return 2;
            }
        }
    }
    return 0;
}

static int origin (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                if ( lua_isuserdata(L, 2) ) {
                    LVUserDataInfo* user = lua_touserdata(L, 2);
                    if ( LVIsType(user, Struct) ) {
                        LVStruct* stru = (__bridge LVStruct *)(user->object);
                        if( [stru dataPointer] ) {
                            memcpy(&r.origin, [stru dataPointer], sizeof(CGPoint));
                        }
                    } else {
                        LVError(@"LVBaseView.setOrigin1");
                    }
                } else {
                    if( lua_isnumber(L, 2) ){
                        r.origin.x = lua_tonumber(L, 2);// 2
                    }
                    if( lua_isnumber(L, 3) ){
                        r.origin.y = lua_tonumber(L, 3);// 3
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
                lua_pushnumber(L, r.origin.x    );
                lua_pushnumber(L, r.origin.y    );
                return 2;
            }
        }
    }
    return 0;
}

static int x (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.origin.x = lua_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lua_pushnumber(L, r.origin.x );
                return 1;
            }
        }
    }
    return 0;
}

#pragma - mark flxNode
static int flxChildViews(lua_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if (user) {
        UIView *view = (__bridge UIView *)(user->object);
        int childNum = lua_gettop(L);
        if (view && childNum>=2 ) {
            NSArray *arr =  (NSArray*)lv_luaValueToNativeObject(L, 2);
            if ([arr isKindOfClass:[NSArray class]] && arr.count > 0) {
                NSMutableArray* childs = [[NSMutableArray alloc] init];
                for( int i=0; i<arr.count; i++ ) {
                    UIView* temp = arr[i];
                    if( temp ) {
                        [childs addObject:temp.ju_flxNode];
                    }
                }
                view.ju_flxNode.childNodes = childs;
                return 0;
            }
            
            NSMutableArray* childs = [[NSMutableArray alloc] init];
            for( int i=2; i<=childNum; i++ ) {
                LVUserDataInfo * childUser = (LVUserDataInfo *)lua_touserdata(L, i);
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

static int flxBindingInlineCSS(lua_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ) {
        UIView *view = (__bridge UIView *)(user->object);
        
        int childNum = lua_gettop(L);
        if (view && childNum==2) {
            if (lua_type(L, 2) == LUA_TSTRING) {
                [view.ju_flxNode bindingInlineCSS:[NSString stringWithUTF8String:lua_tostring(L, 2)]];
                return 0;
            }
        }
    }
    return 0;
}

//static int flxMeasure(lua_State *L)
//{
//    LVUserDataView *user = (LVUserDataView *)lua_touserdata(L, 1);
//    UIView *view = (__bridge UIView *)(user->view);
//    int childNum = lua_gettop(L);
//    if (view && childNum == 2) {
//        if (lua_type(L, 2) == LUA_TFUNCTION) {
//            lua_pushvalue(L, 1);
//            lua_pushvalue(L, 2);
//            lv_udataRef(L, USERDATA_FLEX_DELEGATE);
//        }
//        view.ju_flxNode.measure = ^CGSize(CGFloat width) {
//            lv_pushUserdata(L, user );
//            lv_pushUDataRef(L, USERDATA_FLEX_DELEGATE );
//            lua_pushnumber(L, width);
//            lv_runFunctionWithArgs(L, 1, 2);
//            CGSize size = CGSizeZero;
//            size.width = lua_tonumber(L, -2);
//            size.height = lua_tonumber(L, -1);
//            return size;
//        };
//
//    }
//    return 0;
//}

static int flxLayout(lua_State *L)
{
    LVUserDataInfo *user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if (user) {
        BOOL async = FALSE;
        int argNum = lua_gettop(L);
        for ( int i=1; i<=argNum; i++ ) {
            if ( lua_type(L, i)==LUA_TBOOLEAN ){
                async = lua_toboolean(L, i);
            }
            if( lua_type(L, i) == LUA_TFUNCTION ) {
                lua_pushvalue(L, 1);
                lua_pushvalue(L, i);
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


static int y (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.origin.y = lua_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lua_pushnumber(L, r.origin.y );
                return 1;
            }
        }
    }
    return 0;
}

static int bottom (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.origin.y = lua_tonumber(L, 2)-r.size.height;// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lua_pushnumber(L, r.origin.y + r.size.height );
                return 1;
            }
        }
    }
    return 0;
}

static int right (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.origin.x = lua_tonumber(L, 2)-r.size.width;// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                view.lv_align = 0;
                return 0;
            } else {
                lua_pushnumber(L, r.origin.x + r.size.width );
                return 1;
            }
        }
    }
    return 0;
}

static int width (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.size.width = lua_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lua_pushnumber(L, r.size.width );
                return 1;
            }
        }
    }
    return 0;
}

static int height (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            CGRect r = view.frame;
            if ( lua_gettop(L)>=2 ) {
                r.size.height = lua_tonumber(L, 2);// 2
                if( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ){
                    LVError(@"LVBaseView.y2: %s", NSStringFromCGRect(r) );
                } else {
                    view.frame = r;
                }
                return 0;
            } else {
                lua_pushnumber(L, r.size.height );
                return 1;
            }
        }
    }
    return 0;
}

static int addGestureRecognizer (lua_State *L) {
    LVUserDataInfo * userDataView = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * userDataGesture = (LVUserDataInfo *)lua_touserdata(L, 2);
    if( userDataView && LVIsType(userDataGesture, Gesture) ){
        UIView* view = (__bridge UIView *)(userDataView->object);
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userDataGesture->object);
        if( view && gesture ){
            [view addGestureRecognizer:gesture];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int removeGestureRecognizer (lua_State *L) {
    LVUserDataInfo * userDataView = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * userDataGesture = (LVUserDataInfo *)lua_touserdata(L, 2);
    if( userDataView && LVIsType(userDataGesture, Gesture) ){
        UIView* view = (__bridge UIView *)(userDataView->object);
        UIGestureRecognizer* gesture = (__bridge UIGestureRecognizer *)(userDataGesture->object);
        if( view && gesture ){
            [view removeGestureRecognizer:gesture];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int addSubview (lua_State *L) {
    LVUserDataInfo * father = (LVUserDataInfo *)lua_touserdata(L, 1);
    LVUserDataInfo * son = (LVUserDataInfo *)lua_touserdata(L, 2);
    LuaViewCore* luaview = LV_LUASTATE_VIEW(L);
    if( father &&  LVIsType(son, View) ){
        UIView* superview = (__bridge UIView *)(father->object);
        UIView* subview = (__bridge UIView *)(son->object);
        if( superview && subview ){
            if( lua_gettop(L)>=3 && lua_type(L,3)==LUA_TNUMBER ){
                int index = lua_tonumber(L,3);
                lv_addSubviewByIndex(luaview, superview, subview, index);
            } else {
                lv_addSubview(luaview, superview, subview);
            }
            [subview lv_alignSelfWithSuperRect:superview.frame];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}


static int getNativeView (lua_State *L) {
    LVUserDataInfo * userData = (LVUserDataInfo *)lua_touserdata(L, 1);
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
static int children (lua_State *L) {
    LuaViewCore* lview = LV_LUASTATE_VIEW(L);
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    
    UIView* newWindow = (__bridge UIView *)(user->object);
    if ( lview && newWindow && lua_type(L, 2)==LUA_TFUNCTION ) {
        lua_settop(L, 2);
        [lview pushWindow:newWindow];
        lv_runFunctionWithArgs(L, 1, 0);
        [lview popWindow:newWindow];
    }
    return 0;
}


static int removeFromSuperview (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            [view removeFromSuperview];
            [view.layer removeFromSuperlayer];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int removeAllSubviews (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            NSArray* subviews = view.subviews;
            for( UIView* child in subviews ) {
                [child removeFromSuperview];
            }
            NSArray* sublayers = view.layer.sublayers;
            for(CALayer * sublayer in sublayers ) {
                [sublayer removeFromSuperlayer];
            }
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}


static int bringToFront (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            UIView* superView = view.superview;
            [superView bringSubviewToFront:view];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int bringToBack (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            UIView* superView = view.superview;
            [superView sendSubviewToBack:view];
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int layerMode(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lua_gettop(L)>=2 ) {
                UIView* superview = view.superview;
                BOOL yes = lua_toboolean(L, 2);
                if( yes ) {
                    [view removeFromSuperview];
                    [superview.layer addSublayer:view.layer];
                } else {
                    [view.layer removeFromSuperlayer];
                    [superview addSubview:view];
                }
                return 0;
            } else {
                //lua_pushboolean(L, view.hidden );
                //return 1;
            }
        }
    }
    return 0;
}

#pragma -mark hidden
//__deprecated_msg("Use hide")
static int hidden(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lua_gettop(L)>=2 ) {
                BOOL yes = lua_toboolean(L, 2);
                view.hidden = yes;
                return 0;
            } else {
                lua_pushboolean(L, view.hidden );
                return 1;
            }
        }
    }
    return 0;
}

static int hide(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.hidden = YES;
            return 0;
        }
    }
    return 0;
}

static int visible(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lua_gettop(L)>=2 ) {
                BOOL yes = lua_toboolean(L, 2);
                view.hidden = !yes;
                return 0;
            } else {
                lua_pushboolean(L, !view.hidden );
                return 1;
            }
        }
    }
    return 0;
}

static int show(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.hidden = NO;
            return 0;
        }
    }
    return 0;
}

static int isShow(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lua_pushboolean(L, !view.hidden );
            return 1;
        }
    }
    return 0;
}

static int isHide(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lua_pushboolean(L, view.hidden );
            return 1;
        }
    }
    return 0;
}

static int becomeFirstResponder(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if( view.canBecomeFirstResponder )
                [view becomeFirstResponder];
            
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int resignFirstResponder(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if( view.canResignFirstResponder)
                [view resignFirstResponder];
            
            lua_pushvalue(L,1);
            return 1;
        }
    }
    return 0;
}

static int isFirstResponder(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            lua_pushboolean(L, view.isFirstResponder?1:0 );
            return 1;
        }
    }
    return 0;
}

#pragma -mark userInteractionEnabled
static int userInteractionEnabled(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if ( lua_gettop(L)>=2 ) {
                BOOL yes = lua_toboolean(L, 2);
                view.userInteractionEnabled = yes;
                return 0;
            } else {
                lua_pushboolean(L, view.userInteractionEnabled );
                return 1;
            }
        }
    }
    return 0;
}

#pragma -mark backgroundColor
static int backgroundColor (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( [view isKindOfClass:[UIView class]] ){
            if( lua_gettop(L)>=2 ) {
                UIColor* color = lv_getColorFromStack(L, 2);
                view.backgroundColor = color;
                return 0;
            } else {
                UIColor* color = view.backgroundColor;
                NSUInteger c = 0;
                CGFloat a = 0;
                if( lv_uicolor2int(color, &c,&a) ){
                    lua_pushnumber(L, c );
                    lua_pushnumber(L, a);
                    return 2;
                }
            }
        }
    }
    return 0;
}

#pragma -mark alpha
static int alpha (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lua_gettop(L)>=2 ) {
            CALayer* layer = view.layer;

            double alpha = lua_tonumber(L, 2);// 2
            layer.opacity = alpha;
            
            lua_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            float alpha = layer.opacity;
            lua_pushnumber(L, alpha );
            return 1;
        }
    }
    return 0;
}

#pragma -mark cornerRadius
static int cornerRadius (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            double r = lua_tonumber(L, 2);// 2
            view.layer.cornerRadius = r;
            return 0;
        } else {
            float r = view.layer.cornerRadius;
            lua_pushnumber(L, r );
            return 1;
        }
    }
    return 0;
}

#pragma -mark borderWidth
static int borderWidth (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            double width = lua_tonumber(L, 2);// 2
            view.layer.borderWidth = width;
            return 0;
        } else {
            float w = view.layer.borderWidth;
            lua_pushnumber(L, w );
            return 1;
        }
    }
    return 0;
}

#pragma -mark shadow
static int setShadowPath (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view layer].shadowPath =[UIBezierPath bezierPathWithRect:view.bounds].CGPath;
        lua_pushvalue(L,1);
        return 1;
    }
    return 0;
}
static int setMasksToBounds (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    BOOL masksToBounds = lua_toboolean(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        view.layer.masksToBounds = masksToBounds;
        lua_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowOffset (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    float x = lua_tonumber(L, 2);// 2
    float y = lua_tonumber(L, 3);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowOffset:CGSizeMake(x, y)];
        lua_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowRadius (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    float radius = lua_tonumber(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowRadius:radius];
        lua_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowOpacity (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    float opacity = lua_tonumber(L, 2);// 2
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer setShadowOpacity:opacity];
        lua_pushvalue(L,1);
        return 1;
    }
    return 0;
}

static int setShadowColor (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user && lua_gettop(L)>=2 ){
        UIView* view = (__bridge UIView *)(user->object);
        UIColor* color = lv_getColorFromStack(L, 2);
        [view.layer setShadowColor:color.CGColor];
        lua_pushvalue(L,1);
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

static int borderColor (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lua_gettop(L)>=2 ) {
            UIColor* color = lv_getColorFromStack(L, 2);
            view.layer.borderColor = color.CGColor;
            lua_pushvalue(L,1);
            return 1;
        } else {
            UIColor* color = [UIColor colorWithCGColor:view.layer.borderColor];
            NSUInteger c = 0;
            CGFloat a = 0;
            if( lv_uicolor2int(color, &c, &a) ){
                lua_pushnumber(L, c );
                lua_pushnumber(L, a);
                return 2;
            }
        }
    }
    return 0;
}

static int borderDash (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        int argN = lua_gettop(L);
        if ( argN>=2 ) {
            NSMutableArray* arr = [[NSMutableArray alloc] initWithCapacity:argN];
            for( int i=2; i<=argN; i++) {
                if( lua_type(L, i)==LUA_TNUMBER ) {
                    int v = lua_tonumber(L, i);
                    [arr addObject:[NSNumber numberWithInt:v]];
                }
            }
            //虚线边框
            if( arr.count>0 ) {
                [view lv_createShapelayer:arr];
            } else {
                view.lv_shapeLayer.lineDashPattern = nil;
                [view.lv_shapeLayer removeFromSuperlayer];
                view.lv_shapeLayer = nil;
            }
            return 0;
        } else {
            NSArray<NSNumber*>* numbers = view.lv_shapeLayer.lineDashPattern;
            for( int i=0; i<numbers.count; i++) {
                NSNumber* number = numbers[i];
                lua_pushnumber(L, number.floatValue );
            }
            return (int)numbers.count;
        }
    }
    return 0;
}

#pragma -mark clipsToBounds
static int clipsToBounds(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            if( lua_gettop(L)>=2 ) {
                BOOL yes = lua_toboolean(L, 2);
                view.clipsToBounds = yes;
                return 0;
            } else {
                lua_pushnumber(L, view.clipsToBounds );
                return 1;
            }
        }
    }
    return 0;
}

static int adjustSize(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
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

static int transformFuncOneArg(lua_State *L, TransformSetter setter, TransformGetter getter) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    UIView *view = (__bridge UIView *)(user->object);
    
    if( user ){
        if ( lua_gettop(L) > 1 ) {
            CALayer* layer = view.layer;

            double x = lua_tonumber(L, 2);
            CATransform3D t = layer.transform;
            setter(&t, x);
            
            layer.transform = t;
            
            lua_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            CATransform3D t = layer.transform;
            double x = getter(&t);
            
            lua_pushnumber(L, x);
            return 1;
        }
    }
    return 0;
}

static int transformFuncTwoArg(lua_State *L,
                                 TransformSetter xsetter, TransformSetter ysetter,
                                 TransformGetter xgetter, TransformGetter ygetter) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    UIView *view = (__bridge UIView *)(user->object);
    
    if( user ){
        int argNum = lua_gettop(L);
        if ( argNum > 1 ) {
            CALayer* layer = view.layer;

            double x = lua_tonumber(L, 2), y = lua_tonumber(L, 3);
            CATransform3D t = layer.transform;
            xsetter(&t, x);
            ysetter(&t, y);
            
            layer.transform = t;
            
            lua_pop(L, 1);
            return 1;
        } else {
            CALayer* layer = view.layer.presentationLayer ?: view.layer;

            CATransform3D t = layer.transform;
            double x = xgetter(&t), y = ygetter(&t);
            
            lua_pushnumber(L, x);
            lua_pushnumber(L, y);
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

static int rotationZ (lua_State *L) {
    return transformFuncOneArg(L, transform3DSetDegreeRotation,
                               transform3DGetDegreeRotation);
}

static int rotationX (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            CALayer *layer = view.layer;

            double angle = degreeToRadian(lua_tonumber(L, 2));
            layer.transform = CATransform3DMakeRotation(angle, 1, 0, 0);
            
            lua_pop(L, 1);
            return 1;
        } else {
            CALayer *layer = view.layer.presentationLayer ?: view.layer;
            
            double angle = [[layer valueForKeyPath:@"transform.rotation.x"] doubleValue];
            lua_pushnumber(L, angle);
            return 1;
        }
    }
    return 0;
}

static int rotationY (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            CALayer *layer = view.layer;

            double angle = degreeToRadian(lua_tonumber(L, 2));
            layer.transform = CATransform3DMakeRotation(angle, 0, 1, 0);
            
            lua_pop(L, 1);
            return 1;
        } else {
            CALayer *layer = view.layer.presentationLayer ?: view.layer;

            double angle = [[layer valueForKeyPath:@"transform.rotation.y"] doubleValue];
            lua_pushnumber(L, angle);
            return 1;
        }
    }
    return 0;
}

static int scale (lua_State *L) {
    if (lua_gettop(L) == 2) {
        lua_pushnumber(L, lua_tonumber(L, 2));
    }
    return transformFuncTwoArg(L, CATransform3DSetScaleX, CATransform3DSetScaleY,
                                 CATransform3DGetScaleX, CATransform3DGetScaleY);
}

static int scaleX (lua_State *L) {
    return transformFuncOneArg(L, CATransform3DSetScaleX, CATransform3DGetScaleX);
}

static int scaleY (lua_State *L) {
    return transformFuncOneArg(L, CATransform3DSetScaleY, CATransform3DGetScaleY);
}

static int translation (lua_State *L) {
    return transformFuncTwoArg(L, CATransform3DSetTranslationX, CATransform3DSetTranslationY,
                                 CATransform3DGetTranslationX, CATransform3DGetTranslationY);
}

static int translationX (lua_State *L) {
    return transformFuncOneArg(L, CATransform3DSetTranslationX, CATransform3DGetTranslationX);
}

static int translationY (lua_State *L) {
    return transformFuncOneArg(L, CATransform3DSetTranslationY, CATransform3DGetTranslationY);
}

static int transform3D (lua_State *L) {
    LVUserDataInfo* user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lua_gettop(L)>=2 ) {
            LVUserDataInfo* userdata = (LVUserDataInfo *)lua_touserdata(L, 2);
            if ( LVIsType(userdata, Transform3D)) {
                CALayer *layer = view.layer;
                
                LVTransform3D* tran = (__bridge LVTransform3D *)(userdata->object);
                layer.transform = tran.transform;
                
                lua_pop(L, 1);
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


static int matrix (lua_State *L) {
    LVUserDataInfo* user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( lua_gettop(L)>=2 ) {
            if( lua_type(L, 2)== LUA_TTABLE ) {
                NSArray* arr = lv_luaValueToNativeObject(L, 2);
                if( [arr isKindOfClass:[NSArray class]] ) {
                    CGFloat a[12] = {0};
                    for( int i=0; i<arr.count; i++ ) {
                        NSNumber* number = arr[i];
                        if( [number isKindOfClass:[NSNumber class]] ) {
                            a[2+i] = number.floatValue;
                        }
                    }
                    CGAffineTransform t = CGAffineTransformMake(a[2], a[3], a[4], a[5], a[6], a[7]);
                    view.transform = t;
                }
            } else {
                CGFloat a[18] = {0};
                for( int i=2; i<=7; i++ ) {
                    a[i] = lua_tonumber(L, i);
                }
                CGAffineTransform t = CGAffineTransformMake(a[2], a[3], a[4], a[5], a[6], a[7]);
                view.transform = t;
            }
        } else {
            CGAffineTransform t = view.transform;
            NSArray* a = @[ @(t.a), @(t.b), @(t.c), @(t.d), @(t.tx), @(t.ty) ];
            lv_pushNativeObject(L, a);
            return 1;
        }
    }
    return 0;
}

static int startAnimation(lua_State *L) {
    LVUserDataInfo* vdata = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( vdata ){
        LVUserDataInfo *adata = NULL;
        LVAnimator *animator = nil;
        int top = lua_gettop(L);
        for (int i = 2; i <= top; ++i) {
            adata = lua_touserdata(L, i);
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

static int stopAnimation(lua_State *L) {
    LVUserDataInfo* user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        [view.layer removeAllAnimations];
    }
    
    return 0;
}

#pragma -mark anchorPoint
static int anchorPoint (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( lua_gettop(L)>=2 ) {
            double x = lua_tonumber(L, 2);// 2
            double y = lua_tonumber(L, 3);// 3
            view.layer.anchorPoint = CGPointMake(x, y);
            return 0;
        } else {
            CGPoint p = view.layer.anchorPoint;
            lua_pushnumber(L, p.x );
            lua_pushnumber(L, p.y );
            return 2;
        }
    }
    return 0;
}

int lv_setCallbackByKey(lua_State *L, const char* key, BOOL addGesture) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        if ( lua_gettop(L)>=2 ) {
            UIView* view = (__bridge UIView *)(user->object);
            if( addGesture ) {
                [view lv_callbackAddClickGesture];// 检测是否添加手势
            }
            lua_checkstack(L, 8);
            lua_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if( lua_type(L, -1)==LUA_TNIL ) {
                lua_settop(L, 2);
                lua_pushvalue(L, 1);
                lua_createtable(L, 0, 0);
                lv_udataRef(L, USERDATA_KEY_DELEGATE );
                
                lua_settop(L, 2);
                lua_pushvalue(L, 1);
                lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            }
            lua_pushvalue(L, 2);
            if( key==NULL && lua_type(L, -1) == LUA_TTABLE ) {
                // 如果是表格 设置每个Key
                lua_pushnil(L);
                while (lua_next(L, -2))
                {
                    NSString* key   = lv_paramString(L, -2);
                    lua_setfield(L, -4, key.UTF8String);
                }
            } else {
                // 如果是方法设置默认key
                lua_setfield(L, -2, (key ? key:STR_ON_CLICK) );
            }
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if ( key ) {
                if ( lua_type(L, -1)==LUA_TTABLE ) {
                    lua_getfield(L, -1, key);
                } else {
                    lua_pushnil(L);
                }
            }
            return 1;
        }
    }
    return 0;
}

static int callback (lua_State *L) {
    return lv_setCallbackByKey(L, NULL, YES);
}

static int onLayout (lua_State *L) {
    return lv_setCallbackByKey(L, STR_ON_LAYOUT, NO);
}

static int onClick (lua_State *L) {
    return lv_setCallbackByKey(L, STR_ON_CLICK, YES);
}

static int onShow (lua_State *L) {
    return lv_setCallbackByKey(L, STR_ON_SHOW, NO);
}

static int onHide (lua_State *L) {
    return lv_setCallbackByKey(L, STR_ON_HIDE, NO);
}

static void removeOnTouchEventGesture(UIView* view){
    NSArray< UIGestureRecognizer *> * gestures = view.gestureRecognizers;
    for( LVGesture* g in gestures ) {
        if( [g isKindOfClass:[LVGesture class]] ) {
            if( g.onTouchEventCallback ) {
                [view removeGestureRecognizer:g];
                break;
            }
        }
    }
}

static int onTouch (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    int ret = lv_setCallbackByKey(L, STR_ON_TOUCH, NO);
    if( user ){
        __weak UIView* view = (__bridge UIView *)(user->object);
        removeOnTouchEventGesture(view);
        
        LVGesture* gesture = [[LVGesture alloc] init:L];
        gesture.onTouchEventCallback = ^(LVGesture* gesture, int argN){
            [view lv_callLuaCallback:@STR_ON_TOUCH key2:nil argN:1];
        };
        [view addGestureRecognizer:gesture];
    }
    return ret;
}

#pragma -mark __gc
static int __gc (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataView(user);
    return 0;
}

static int __tostring (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView<LVProtocal>* view = (__bridge UIView<LVProtocal> *)(user->object);
        if( view ){
            NSString* s = [NSString stringWithFormat:@"%@",view];
            lua_pushstring(L, s.UTF8String);
            return 1;
        }
    }
    return 0;
}

- (void) layoutSubviews{
    [super layoutSubviews];
    [self lv_alignSubviews];
    [self lv_callLuaCallback:@STR_ON_LAYOUT];
}

static int releaseObject(lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        //[LVUtil unregistry:L key:(__bridge id)user->view];
        UIView* view = (__bridge UIView *)(user->object);
        [view removeFromSuperview];
        [view.layer removeFromSuperlayer];
        if( [view isKindOfClass:[LuaViewCore class]] ){
            LuaViewCore* lView = (LuaViewCore*)view;
            lView.l = NULL;
            G(L)->ud = NULL;
            [lView releaseLuaView];
        }
    }
    return 0;
}
//---------------------------
//static int __newindex (lua_State *L) {
//    NSString* key = lv_paramString(L, 2);
//    if( key ){
//        lv_getmetatable( L, 1 );
//        lua_getfield(L, -1, key.UTF8String);
//        if( lua_type(L, -1)==LUA_TFUNCTION ) {
//            lua_CFunction function =  lua_tocfunction(L,-1);
//            if( function ) {
//                lua_remove(L, 2);
//                lua_settop(L, 2);
//                return function(L);
//            }
//        }
//    }
//    LVError(@"not found property: %@", key);
//    return 0;
//}


static int align (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            int argNum = lua_gettop(L);
            if ( argNum>=2 ) {
                NSUInteger align = 0;
                for (int i=2; i<=argNum; i++ ) {
                    align |= (NSUInteger)lua_tointeger(L, i);
                }
                view.lv_align = align;
                [view lv_alignSelfWithSuperRect:view.superview.frame];
                return 0;
            } else {
                lua_pushnumber(L, view.lv_align );
                return 1;
            }
        }
    }
    return 0;
}

static int alignInfo (lua_State *L, int align) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if( view ){
            view.lv_align = align;
            [view lv_alignSelfWithSuperRect:view.superview.frame];
        }
    }
    return 0;
}

static int alignLeft(lua_State *L ) {
    return alignInfo(L, LV_ALIGN_LEFT);
}

static int alignRight(lua_State *L ) {
    return alignInfo(L, LV_ALIGN_RIGHT);
}

static int alignTop(lua_State *L ) {
    return alignInfo(L, LV_ALIGN_TOP);
}

static int alignBottom(lua_State *L ) {
    return alignInfo(L, LV_ALIGN_BOTTOM);
}

static int alignCenter(lua_State *L ) {
    return alignInfo(L, LV_ALIGN_H_CENTER|LV_ALIGN_V_CENTER);
}

static int effects (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( [view isKindOfClass:[UIView class]] ) {
            int effectType = lua_tonumber(L, 2);
            switch (effectType) {
                case EFFECT_NONE:
                    break;
                case EFFECT_CLICK:{
                    NSInteger color = lua_tonumber(L, 3);
                    CGFloat alpha = lua_tonumber(L, 4);
                    [view lv_effectClick:color alpha:alpha];
                    break;
                }
                case EFFECT_PARALLAX:{
                    CGFloat dx = lua_tonumber(L, 3);
                    CGFloat dy = lua_tonumber(L, 4);
                    [view lv_effectParallax:dx dy:dy];
                    break;
                }
                default:
                    break;
            }
        }
    }
    return 0;
}

static int invalidate (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        UIView* view = (__bridge UIView *)(user->object);
        if ( [view isKindOfClass:[UIView class]] ) {
            [view setNeedsDisplay];
        }
    }
    return 0;
}

static const struct luaL_Reg baseMemberFunctions [] = {
    {"hidden",    hidden },
    {"visible",    visible },
    
    {"hide",    hide },//__deprecated_msg("Use hidden")
    {"isHide",    isHide },//__deprecated_msg("Use hidden")
    
    {"show",    show },//__deprecated_msg("Use visible")
    {"isShow",    isShow },//__deprecated_msg("Use visible")
    
    {"enabled",    userInteractionEnabled },
    {"clipsToBounds",    clipsToBounds },// for IOS
    
    {"backgroundColor",     backgroundColor },
    
    {"alpha",       alpha },
    
    {"cornerRadius",        cornerRadius },
    
    {"borderWidth",         borderWidth },
    
    {"borderColor",         borderColor },
    {"borderDash",         borderDash },
    
    {"shadowPath",       setShadowPath }, // for IOS
    {"masksToBounds",    setMasksToBounds },// for IOS
    {"shadowOffset",     setShadowOffset },// for IOS
    {"shadowRadius",     setShadowRadius },// for IOS
    {"shadowOpacity",    setShadowOpacity },// for IOS
    {"shadowColor",      setShadowColor },// for IOS
    
    {"frame",     frame },
    
    {"size",     size },
    
    {"origin",     origin },//__deprecated_msg("Use xy")
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
    
    {"adjustSize", adjustSize},// 带讨论
    
    {"addGesture",          addGestureRecognizer }, //__deprecated_msg("Use onTouch")
    {"removeGesture",       removeGestureRecognizer }, //__deprecated_msg("Use onTouch")
    
    {"addView",          addSubview },
    {"children", children },
    {"removeFromSuper", removeFromSuperview },
    {"removeFromParent", removeFromSuperview }, //__deprecated_msg("Use removeFromSuper")
    {"removeAllViews", removeAllSubviews },
    
    {"bringToFront", bringToFront},
    {"sendToBack", bringToBack},
    
    {"rotation",  rotationZ },
    {"rotationX", rotationX },
    {"rotationY", rotationY },
    {"rotationZ", rotationZ },//__deprecated_msg("Use rotation")
    
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
    {"onTouch",     onTouch },
    {"onShow",     onShow },
    {"onHide",     onHide },
    
    // onLongClick
    
    {"hasFocus",        isFirstResponder },
    {"requestFocus",    becomeFirstResponder },
    {"cancelFocus",    resignFirstResponder }, //__deprecated_msg("Use hidden")
    {"clearFocus",    resignFirstResponder },
    
    {"transform3D",    transform3D }, //__deprecated_msg("Use")
    {"matrix",    matrix },
    
    {"startAnimation", startAnimation },
    {"stopAnimation", stopAnimation },
    
    
    {"__gc",        __gc },
    
    {"__tostring",  __tostring},
    
    // {"__newindex",  __newindex },

    {"flexChildren",  flxChildViews },
    {"flxLayout",  flxLayout },// 安卓无
    {"flexCss", flxBindingInlineCSS},
  
    // align
    {"align", align},
    {"alignLeft", alignLeft},
    {"alignRight", alignRight},
    {"alignTop", alignTop},
    {"alignBottom", alignBottom},
    {"alignCenter", alignCenter},
    // padding
    // margin
    
    {"getNativeView", getNativeView}, //__deprecated_msg("Use nativeView")
    {"nativeView", getNativeView},
    
    {"effects",effects}, // IOS 视差效果
    {"layerMode",layerMode}, // for IOS
    
    {"invalidate",invalidate},
    {NULL, NULL}
};

static const struct luaL_Reg luaViewMemberFunctions [] = {
    {"release",     releaseObject},
    {NULL,    NULL },
};

+(const luaL_Reg*) baseMemberFunctions{
    return baseMemberFunctions;
}

#pragma -mark UIView
static int lvNewView (lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVBaseView class]];
    
    LVBaseView* view = [[c alloc] init:L];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(view);
        view.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_UIView );
        lua_setmetatable(L, -2);
        
        LuaViewCore* luaviewCore = LV_LUASTATE_VIEW(L);
        if( luaviewCore ){
            [luaviewCore containerAddSubview:view];
        }
    }
    return 1; /* new userdatum is already on the stack */
}

+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewView globalName:globalName defaultName:@"View"];
    
    lv_createClassMetaTable(L, META_TABLE_UIView);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    
    
    lv_createClassMetaTable(L, META_TABLE_LuaView);
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, luaViewMemberFunctions, 0);
    return 1;
}

//----------------------------------------------------------------------------------------

-(NSString*) description{
    return [NSString stringWithFormat:@"<View(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

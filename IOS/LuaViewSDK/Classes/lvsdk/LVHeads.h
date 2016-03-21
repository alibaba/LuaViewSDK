//
//  LVHeads.h
//  JU
//
//  Created by dongxicheng on 12/29/14.
//  Copyright (c) 2014 ju.taobao.com. All rights reserved.
//

#ifndef JU_LVHeads_h
#define JU_LVHeads_h
//--------------------------------------------------------------------------------

#import <Foundation/Foundation.h>

#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

#define lvL_reg	lvL_Reg

/**
 * the index of signed scripts' extionsion(@"lv") in LVScriptExts[]
 */
extern const int LVSignedScriptExtIndex;

/**
 * supported script extensions: { @"lv", @"lua" }
 */
extern NSString * const LVScriptExts[2];

#import "LVUtil.h"
//--------------------------------------------------------------------------------
#define LV_ALIGN_LEFT       (1)
#define LV_ALIGN_H_CENTER   (2)
#define LV_ALIGN_RIGHT      (4)

#define LV_ALIGN_TOP        (8)
#define LV_ALIGN_V_CENTER   (16)
#define LV_ALIGN_BOTTOM     (32)

//------------------------------
#define LVReleaseAndNull( a ) if(a) { CFRelease(a); a = NULL; }
//------------------------------


#define USERDATA_KEY_DELEGATE   1
#define USERDATA_KEY_CALLBACK   2
#define USERDATA_FLEX_DELEGATE  8

//---------------创建用户数据-------------------------------------------------------
#define NEW_USERDATA(var, typeName)    \
    LVUserDataInfo* var = ( (LVUserDataInfo*)lv_newuserdata( L, sizeof(LVUserDataInfo)) ); \
    lv_createUDataLuatable(L,-1);\
    var->type = LVType_##typeName; \
//


//----------------用户数据的类型检查-----i--------------------------------------------
#define LVIsType( user,T)  (user && user->type==LVType_##T)

//----------------用户数据类型 ID---------------------------------------------------

extern const char* LVType_View;
extern const char* LVType_Data;
extern const char* LVType_Date;
extern const char* LVType_Http;
extern const char* LVType_Timer;
extern const char* LVType_Transform3D;
extern const char* LVType_Animator;
extern const char* LVType_Gesture;
extern const char* LVType_Downloader;
extern const char* LVType_AudioPlayer;
extern const char* LVType_StyledString;
extern const char* LVType_NativeObject;
extern const char* LVType_Struct;


//----------------View的用户数据结构------------------------------------------------
typedef struct _LVUserDataInfo {
    const char* type;// 用户类型
    const void* object;// 真实的用户对象
} LVUserDataInfo;

//--------------------------------------------------------------------------------
@class LView;
@protocol LVProtocal <NSObject>
@required
@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataInfo* lv_userData;
- (id) lv_nativeObject; // 返回native对象
@end


//-----------------------metatable------------------------------------------------
#define META_TABLE_UIButton         "UI.Button"
#define META_TABLE_UIScrollView     "UI.ScrollView"
#define META_TABLE_UIView           "UI.View"
#define META_TABLE_LuaView           "UI.LuaView"
#define META_TABLE_UIViewNewIndex   "UI.View.NewIndex"
#define META_TABLE_Timer            "LV.Timer"
#define META_TABLE_Http             "LV.Http"
#define META_TABLE_Gesture          "LV.GestureRecognizer"
#define META_TABLE_PanGesture       "LV.Pan.GestureRecognizer"
#define META_TABLE_TapGesture       "LV.Tap.GestureRecognizer"
#define META_TABLE_PinchGesture     "LV.Pinch.GestureRecognizer"
#define META_TABLE_RotaionGesture   "LV.Rotaion.GestureRecognizer"
#define META_TABLE_SwipeGesture     "LV.Swipe.GestureRecognizer"
#define META_TABLE_LongPressGesture "LV.LongPress.GestureRecognizer"
#define META_TABLE_Date             "LV.Date"
#define META_TABLE_Data             "LV.Data"
#define META_TABLE_UIPageControl    "UI.PageControl"
#define META_TABLE_UIActivityIndicatorView    "UI.UIActivityIndicator"
#define META_TABLE_UICustomPanel        "UI.CustomPanel"
#define META_TABLE_UIImageView      "UI.ImageView"
#define META_TABLE_UILabel          "UI.Label"
#define META_TABLE_UITextField      "UI.TextField"
#define META_TABLE_UITableView      "UI.TableView"
#define META_TABLE_UITableViewCell  "UI.TableView.Cell"
#define META_TABLE_UICollectionView      "UI.CollectionView"
#define META_TABLE_UICollectionViewCell  "UI.CollectionView.Cell"
#define META_TABLE_UIPageView       "UI.PagerView"
#define META_TABLE_UIAlertView      "UI.AlertView"
#define META_TABLE_Transform3D      "UI.Transfrom3D"
#define META_TABLE_Animator         "UI.Animator"
#define META_TABLE_Struct           "UI.Struct"
#define META_TABLE_Downloader       "LV.Downloader"
#define META_TABLE_AudioPlayer      "LV.AudioPlayer"
#define META_TABLE_AttributedString "LV.AttributedString"
#define META_TABLE_NativeObject     "LV.nativeObjBox"
#define META_TABLE_System           "LV.System"

#define STR_CALLBACK "Callback"
#define STR_ON_LAYOUT "onLayout"
#define STR_ON_CLICK "onClick"

// lua对象 -> NSString
NSString* lv_paramString(lv_State* L, int idx );

// run
NSString*  lv_runFunction(lv_State* l);
NSString*  lv_runFunctionWithArgs(lv_State* l, int nargs, int nret);



//--------------------------------------------------------------------------------
@interface LVHeads : NSObject
@end


typedef enum:int{
    LVTypeID_NONE = 0,
    LVTypeID_void,
    LVTypeID_BOOL,
    LVTypeID_bool,
    LVTypeID_char,
    LVTypeID_unsignedchar,
    LVTypeID_short,
    LVTypeID_unsignedshort,
    LVTypeID_int,
    LVTypeID_unsignedint,
    LVTypeID_NSInteger,
    LVTypeID_NSUInteger,
    LVTypeID_longlong,
    LVTypeID_unsigedlonglong,
    LVTypeID_float,
    LVTypeID_CGFloat,
    LVTypeID_double,
    LVTypeID_charP,
    LVTypeID_voidP,
    LVTypeID_id,
    LVTypeID_idP,
    LVTypeID_struct,
}LVTypeIDEnum;


LVTypeIDEnum lv_typeID(const char* type);

#define isNormalRect(r)  ( !( isnan(r.origin.x) || isnan(r.origin.y) || isnan(r.size.width) || isnan(r.size.height) ) )
#define isNormalSize(s)  ( !( isnan(s.width) || isnan(s.height) ) )
#define isNormalPoint(p)  ( !( isnan(p.x) || isnan(p.y) ) )
#define isNormalEdgeInsets(e)  ( !( isnan(e.top) || isnan(e.left) || isnan(e.bottom) || isnan(e.right) ) )


typedef void(^LVLoadFinished)(id errorInfo);

#import "UIView+LuaView.h"
#import "UIScrollView+LuaView.h"
#import "LVBundle.h"

#endif

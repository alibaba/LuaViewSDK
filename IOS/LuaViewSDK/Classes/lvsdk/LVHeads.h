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

#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

#define lvL_reg	lvL_Reg
typedef struct lvL_Reg lvL_Reg;
typedef struct lv_State lv_State;

#import "LVUtil.h"
//--------------------------------------------------------------------------------
#define LV_ALIGN_LEFT       (1)
#define LV_ALIGN_H_CENTER   (2)
#define LV_ALIGN_RIGHT      (4)

#define LV_ALIGN_TOP        (8)
#define LV_ALIGN_V_CENTER   (16)
#define LV_ALIGN_BOTTOM     (32)

//------------------------------
//------------------------------


#define USERDATA_KEY_DELEGATE   1
#define USERDATA_KEY_CALLBACK   2
#define USERDATA_FLEX_DELEGATE  8

//---------------用户数据包含的公用头信息---------------------------------------------
#define LVUserDataCommonHead  const char* type;

//---------------创建用户数据-------------------------------------------------------
#define NEW_USERDATA(var, T)    \
    T* var = ( (T*)lv_newuserdata( L, sizeof(T)) ); \
    lv_createUDataLuatable(L,-1);\
    var->type = LVType_##T; \
//

#define NEW_USERDATA2(var, T, typeName)    \
    T* var = ( (T*)lv_newuserdata( L, sizeof(T)) ); \
    lv_createUDataLuatable(L,-1);\
    var->type = typeName; \
//

//----------------用户数据的类型检查-------------------------------------------------
#define LVIsType( user,T)  (user && user->type==LVType_##T)
#define LVIsType2(user,T)  (user && user->type==T)

//----------------用户数据类型 ID---------------------------------------------------

extern const char* LVType_LVUserDataView;
extern const char* LVType_LVUserDataData;
extern const char* LVType_LVUserDataDate;
extern const char* LVType_LVUserDataHttp;
extern const char* LVType_LVUserDataTimer;
extern const char* LVType_LVUserDataTransform3D;
extern const char* LVType_LVUserDataAnimator;
extern const char* LVType_LVUserDataGesture;
extern const char* LVType_LVUserDataDownloader;
extern const char* LVType_LVUserDataAudioPlayer;
extern const char* LVType_LVUserDataStyledString;
extern const char* LVType_LVUserDataNativeObject;
extern const char* LVType_LVUserDataStruct;


//----------------View的用户数据结构------------------------------------------------
typedef struct _LVUserDataView {
    LVUserDataCommonHead;
    const void* view;
} LVUserDataView;

//----------------手势的用户数据结构-------------------------------------------------
typedef struct _LVUserDataGesture {
    LVUserDataCommonHead;
    const void* gesture;
} LVUserDataGesture;

//--------------------------------------------------------------------------------
@class LView;
@protocol LVProtocal <NSObject>
@required
@property(nonatomic,weak) LView* lv_lview;
@property(nonatomic,assign) LVUserDataView* lv_userData;
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

#endif

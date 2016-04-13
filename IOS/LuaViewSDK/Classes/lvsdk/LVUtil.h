//
//  Util.h
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LVHeads.h"

@class LVBundle;

typedef void(^LVFuncDownloadEndCallback)(NSData* data);

@interface LVUtil : NSObject

/**
 *  调用lua对应的方法(如果是function直接调用, 如果是table用key去查找后再调用)
 *
 *  @param l     luastate
 *  @param table 用户数据
 *  @param key
 *
 *  @return stackNumber
 */
+(NSString*) call:(lv_State*) l  lightUserData:(id) lightUserData key1:(const char*)key1 key2:(const char*)key2 nargs:(int)nargs;

+(NSString*) call:(lv_State*) l  key1:(const char*)key1 key2:(const char*)key2 key3:(const char*)key3
      nargs:(int)nargs nrets:(int)nret
    retType:(int) retType;

/*
 * download file
 */
+(void) download:(NSString*) url callback:(LVFuncDownloadEndCallback) nextStep;

/* 
 * NSData -> 文件
 */
+(BOOL) saveData:(NSData*) data  toFile:(NSString*) fileName;

/*
 * 文件 -> NSData
 */
+(NSData*) dataReadFromFile:(NSString*) fileName;


/*
 * 删除文件
 */
+(BOOL) deleteFile:(NSString*)path;

/*
 * NSCachesDirectory: 相对路径 -> 绝对路径
 */
+ (NSString*) PathForCachesResource:(NSString* )relativePath ;

/*
 * NSDocumentDirectory: 相对路径 -> 绝对路径
 */
+ (NSString*) PathForDocumentsResource:(NSString*) relativePath;

/*
 * 
 */
+ (NSString*) PathForBundle:(NSBundle*) bundle  relativePath:(NSString*) relativePath;

/*
 * 创建目录
 */
+(BOOL) createPath:(NSString*) path;

+(BOOL) exist:(NSString*) path;

/*
 * json解析
 */
+(id) stringToObject:(NSString*) s;
+(NSString*) objectToString:(id) obj;

/*
 * MD5
 */
+ (NSString*) MD5HashFromData:(NSData*) data;
+ (NSData*) MD5HashDataFromData:(NSData*) data;

NSData *LV_AES256DecryptDataWithKey(NSData *data, NSData* key);

// 全局注册表添加/删除
+ (void) registryValue:(lv_State*) L key:(id) key stack:(int) valueIndex;
+ (void) unregistry:(lv_State*) L key:(id) key;
+ (void) pushRegistryValue:(lv_State*) L key:(id) key;

// UData 关联脚本object
void lv_udataRef(lv_State* L, int key);  // -2: userdata   -1: value
void lv_udataUnref(lv_State* L, int key); // -1: userdata
void lv_pushUDataRef(lv_State* L, int key); // -1: userdata

/*
 * Create Class MetaTable
 */
void lv_createClassMetaTable(lv_State* L, const char* name);

/*
 * Push userData to stack
 */
void lv_pushUserdata(lv_State* L, void* p);

/*
 * table -> NSDictionary
 */
id lv_luaTableToDictionary(lv_State* L, int index);

/*
 * table -> NSArray
 */
NSArray* lv_luaTableToArray(lv_State* L, int idx);

/*
 * oc对象(所有类型) 转成 luavalue
 */
void lv_pushNativeObject(lv_State* L , id value );

/*
 * oc对象(非基本类型) 转成 luavalue
 */
void lv_pushNativeObjectWithBox(lv_State* L,id nativeObject);

/*
 * luavalue 转成 oc对象
 */
id lv_luaValueToNativeObject(lv_State* L, int idx);

/*
 * 获取LuaTable对象的 keys
 */
NSArray* lv_luaTableKeys(lv_State* L, int index);

/*
 * 移除LuaTable对象的 指定Keys
 */
void lv_luaTableRemoveKeys(lv_State* L, const char** keys);

/*
 * lua table是否包含属性
 */
BOOL lv_isLuaObjectHaveProperty(lv_State* L, int idx, const char* key);

/*
 * lua table添加window属性
 */
void lv_luaTableSetWeakWindow(lv_State* L, UIView* cell);

/**
 *  重置lua虚拟机的栈大小位置
 *
 *  @param l lua state
 */
void lv_checkStack32(lv_State* l);

/**
 *  清理首个无效参数
 *
 *  @param l lua state
 */
void lv_clearFirstTableValue(lv_State* l);

/*
 * uicolor -> int
 */
BOOL lv_uicolor2int(UIColor* color,NSUInteger* c, CGFloat* alpha);

UIColor* lv_getColorFromStack(lv_State* L, int stackID);


/*
 * Is External Url
 */
+(BOOL) isExternalUrl:(NSString*) url;

UIColor* lv_UIColorFromRGBA(NSInteger aRGB ,float alpha);

+(BOOL) ios8;


+ (int) loadFont:(NSString*) fileName package:(LVBundle*)bundle;
+ (UIFont *)fontWithName:(NSString *)fontName size:(CGFloat)fontSize bundle:(LVBundle*)bundle;

void LVLog( NSString* format, ... );
void LVError( NSString* format, ... );

//----------------------------------------
int lv_callbackFunction(lv_State* l, const char* functionName);

BOOL lv_objcEqual(id obj1, id obj2);

@end



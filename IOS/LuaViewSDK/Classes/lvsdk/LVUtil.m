//
//  Util.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/18/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVUtil.h"
#import <CommonCrypto/CommonDigest.h>
#import "LVBlock.h"
#import "LVNativeObjBox.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "LVPointerValueBox.h"
#import "LVDebuger.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import <CoreText/CTFontManager.h>

@implementation LVUtil


+(BOOL) isExternalUrl:(NSString*) url{
    return [url hasPrefix:@"https://"] || [url hasPrefix:@"http://"];
}

+(NSString*) call:(lv_State*) l  lightUserData:(id) lightUserData key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs {
    if( l ){
        lv_checkStack32(l);
        lv_pushlightuserdata(l, (__bridge void *)lightUserData);// key=view
        lv_gettable(l, LV_REGISTRYINDEX);/* table = registry[&Key] */
        
        return [LVUtil call:l key1:key1 key2:key2 nargs:nargs nrets:0];
    }
    return nil;
}

+(NSString*) call:(lv_State*) l key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs nrets:(int)nret{
    return [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:nargs nrets:nret retType:-8];
}

+(NSString*) call:(lv_State*) l key1:(const char*) key1 key2:(const char*)key2  key3:(const char*)key3
      nargs:(int)nargs nrets:(int)nret
    retType:(int) retType{
    if( l ){
        if( lv_type(l, -1)==LV_TNIL ){
            return @"LVUtil: call nil function";
        } else if( lv_type(l, -1)==LV_TTABLE && key1){//table
            lv_getfield(l, -1, key1);
            lv_remove(l, -2);
            
            if( lv_type(l, -1)==LV_TTABLE && key2){//table
                lv_getfield(l, -1, key2);
                lv_remove(l, -2);
                
                if( lv_type(l, -1)==LV_TTABLE && key3){//table
                    lv_getfield(l, -1, key3);
                    lv_remove(l, -2);
                }
            }
        }
        int type = lv_type(l, -1);
        if ( type==retType && nret==1 ) {
            return nil;
        }
        if( type == LV_TFUNCTION ){//function
            return lv_runFunctionWithArgs(l, nargs, nret);
        }
    }
    return @"LVUtil:lv_State is nil";
}

NSString* lv_runFunction(lv_State* l){
    return lv_runFunctionWithArgs(l, 0, 0);
}

NSString* lv_runFunctionWithArgs(lv_State* l, int nargs, int nret){
    if( l && lv_type(l, -1) == LV_TFUNCTION ) {
        if( nargs>0 ){
            lv_insert(l, -nargs-1);
        }
        int errorCode = lv_pcall( l, nargs, nret, 0);
        if ( errorCode != 0 ) {
            const char* s = lv_tostring(l, -1);
            LVError( @"%s", s );
#ifdef DEBUG
            NSString* string = [NSString stringWithFormat:@"[LuaView][error]   %s",s];
            lv_printToServer(l, string.UTF8String, 0);
#endif
            return [NSString stringWithFormat:@"%s",s];
        }
        return nil;
    }
    return @"function is nil error";
}

#define api_incr_top(L)   {api_check(L, L->top < L->ci->top); L->top++;}
void lv_pushUserdata(lv_State* L, void* p){///是否正确 ????????
    if( p ) {
        Udata* u = (Udata*)p;
        u -= 1;
        lv_lock(L);
        lvC_checkGC(L);
        setuvalue(L, L->top, u);
        api_incr_top(L);
        lv_unlock(L);
    } else {
        lv_pushnil(L);
    }
}

id lv_luaTableToDictionary(lv_State* L ,int index){
    if( lv_type(L, index)!=LV_TTABLE ) {
        return nil;
    }
    lv_checkstack(L, 128);
    NSMutableDictionary* dic = nil;
    NSMutableArray* array = nil;
    //lv_settop(L, 8);
    // Push another reference to the table on top of the stack (so we know
    // where it is, and this function can work for negative, positive and
    // pseudo indices
    lv_pushvalue(L, index);
    // stack now contains: -1 => table
    lv_pushnil(L);
    // stack now contains: -1 => nil; -2 => table
    while (lv_next(L, -2))
    {
        int keyType = lv_type(L, -2);
        
        id value = nil;
        if( lv_type(L, -1)==LV_TSTRING ){
            value = lv_paramString(L, -1);
        } else if( lv_type(L, -1)==LV_TNUMBER ){
            value = @(lv_tonumber(L, -1) );
        } else if( lv_type(L, -1)==LV_TTABLE ){
            value = lv_luaTableToDictionary(L,-1);
        } else if( lv_type(L, -1)==LV_TBOOLEAN ){
            value = @( ((BOOL)lv_toboolean(L, -1)) );
        }
        // stack now contains: -1 => value; -2 => key; -3 => table
        if( value ) {
            if( keyType== LV_TNUMBER ) {
                // number key
                if( array == nil ) {
                    array = [[NSMutableArray alloc] init];
                }
                [array addObject:value];
            } else { // string
                NSString* key   = lv_paramString(L, -2);
                if( key ) {
                    if( dic == nil ) {
                        dic = [[NSMutableDictionary alloc] init];
                    }
                    [dic setObject:value forKey:key];
                }
            }
        }
        lv_pop(L, 1);
        // stack now contains: -1 => key; -2 => table
    }
    lv_pop(L, 1);
    
    if( [dic count]>0 ) {
        return dic;
    }
    if ( array.count>0 ) {
        return array;
    }
    // Stack is now the same as it was on entry to this function
    return @{};
}

NSArray* lv_luaTableKeys(lv_State* L, int index){
    lv_checkstack(L, 128);
    NSMutableArray* keys = [[NSMutableArray alloc] init];
    //lv_settop(L, 8);
    // Push another reference to the table on top of the stack (so we know
    // where it is, and this function can work for negative, positive and
    // pseudo indices
    if ( lv_type(L, index)!= LV_TTABLE ){
        return nil;
    }
    lv_pushvalue(L, index);
    // stack now contains: -1 => table
    lv_pushnil(L);
    // stack now contains: -1 => nil; -2 => table
    while (lv_next(L, -2))
    {
        NSString* key   = lv_paramString(L, -2);
        // stack now contains: -1 => value; -2 => key; -3 => table
        if( key ) {
            [keys addObject:key];
        }
        lv_pop(L, 1);
        // stack now contains: -1 => key; -2 => table
    }
    lv_pop(L, 1);
    // Stack is now the same as it was on entry to this function
    if( keys.count>0 ){
        return keys;
    }
    return nil;
}

NSArray* lv_luaTableToArray(lv_State* L,int stackID)
{
    if( lv_type(L, stackID)==LV_TTABLE) {
        int count = lvL_getn(L, stackID);
        NSMutableArray* array = [[NSMutableArray alloc] init];
        
        for (int i = 0; i < count; i++)
        {
            lv_rawgeti(L, stackID, i+1);
            NSString* s = lv_paramString(L, -1);
            [array addObject:s];
            lv_pop(L,1);
        }
        return array;
    }
    return nil;
}

#pragma -mark registry

+ (void) registryValue:(lv_State*)L key:(id) key stack:(int) stackID{
    if( L ) {
        lv_checkstack(L, 4);
        lv_pushvalue(L, stackID );    // value
        lv_pushlightuserdata(L, (__bridge void *)(key) );   // key
        lv_insert(L, -2);                // key <==> value 互换
        lv_settable(L, LV_REGISTRYINDEX);// registry[&Key] = fucntion
    }
}

+ (void) unregistry:(lv_State*) L key:(id) key{
    if( L ) {
        lv_checkstack(L, 2);
        lv_pushlightuserdata(L, (__bridge void *)(key) );   // key
        lv_pushnil(L);                   // nil
        lv_settable(L, LV_REGISTRYINDEX);// registry[&Key] = nil
    }
}

+ (void) pushRegistryValue:(lv_State*) L key:(id) key{
    if( L ){
        lv_pushlightuserdata(L, (__bridge void *)(key));// key=button
        lv_gettable(L, LV_REGISTRYINDEX);/* value = registry[&Key] */
    }
}

void lv_createClassMetaTable(lv_State* L , const char* name ){
    lvL_newmetatable(L, name );
    lv_pushstring(L, "__index");//必须要的。
    lv_pushvalue(L, -2); /* pushes the metatable */
    lv_settable(L, -3); /* metatable.__index = metatable */
}

void lv_checkStack32(lv_State* l){
    if( l ){
        lv_checkstack( l, 32);
    }
}

void lv_clearFirstTableValue(lv_State* L){
    int num = lv_gettop(L);
    if( num>1 && lv_type(L, 1)==LV_TTABLE ) {
        lv_checkstack(L, 4);
        lv_getfield(L, 1, LUAVIEW_SYS_TABLE_KEY);
        if( lv_isnil(L, -1) ) {
            lv_settop(L, num);
        } else {
            lv_settop(L, num);
            lv_remove(L, 1);
        }
    }
}

BOOL lv_uicolor2int(UIColor* color,NSUInteger* c, CGFloat* alphaP){
    CGFloat r = 0;
    CGFloat g = 0;
    CGFloat b = 0;
    CGFloat a = 0;
    if( [color getRed:&r green:&g blue:&b alpha:&a] ){
        NSUInteger red   = (r*255);
        NSUInteger green = (g*255);
        NSUInteger blue  = (b*255);
        *c = (red<<16) | (green<<8) | blue;
        *alphaP = a;
        return YES;
    }
    return NO;
}

UIColor* lv_getColorFromStack(lv_State* L, int stackID){
    if ( lv_type(L, stackID)==LV_TSTRING ) {
//        NSString* s = lv_paramString(L, stackID);
//        if( s.length>0 && [s characterAtIndex:0]=='#' ) {
//            s = [s substringFromIndex:1];
//            NSScanner * scanner = [[NSScanner alloc] initWithString:s] ;
//            unsigned long long color = 0;
//            [scanner scanHexLongLong:&color];
//            float a = 1;
//            if( s.length>=8 ) {
//                a = ( (color>>24)&0xff )/255.0;
//            }
//            float r = ( (color>>16)&0xff )/255.0;
//            float g = ( (color>>8)&0xff  )/255.0;
//            float b = ( (color>>0)&0xff  )/255.0;
//            UIColor* colorObj = [UIColor colorWithRed:r green:g blue:b alpha:a];
//            return colorObj;
//        }
    } else if( lv_type(L,stackID)==LV_TNUMBER ) {
        NSUInteger color = lv_tonumber(L, stackID);
        float a = 1;
        float r = ( (color>>16)&0xff )/255.0;
        float g = ( (color>>8)&0xff  )/255.0;
        float b = ( (color>>0)&0xff  )/255.0;
        int stackID3 = stackID + 1;
        if ( lv_gettop(L)>=stackID3 && lv_type(L,stackID3)==LV_TNUMBER ) {
            a = lv_tonumber(L, stackID+1 );
            if( a>1 ) {
                a = 1;
            }
            if( a<0 ) {
                a = 0;
            }
        }
        UIColor* colorObj = [UIColor colorWithRed:r green:g blue:b alpha:a];
        return colorObj;
    }
    return [UIColor blackColor];
}

+(void) download:(NSString*) urlStr callback:(LVFuncDownloadEndCallback) nextStep{
    NSURL *url = [NSURL URLWithString:urlStr];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request setHTTPMethod:@"GET"];
    [request setTimeoutInterval:30.0];
    
    NSOperationQueue *queue = [[NSOperationQueue alloc]init];
    [NSURLConnection sendAsynchronousRequest:request
                                       queue:queue
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
                               if( error==nil && data ){
                                   nextStep(data);
                               } else {
                                   nextStep(nil);
                               }
                           }];
}

#pragma file data

+(BOOL) saveData:(NSData*) data  toFile:(NSString*) path{
    if( path ){
        BOOL ret = [data writeToFile:path atomically:YES];
        return ret;
    }
    return NO;
}

+(NSData*) dataReadFromFile:(NSString*)path {
    if( path ){
        NSFileManager* fm = [NSFileManager defaultManager];
        NSData* data = [fm contentsAtPath:path];
        return data;
    }
    return nil;
}

+(BOOL) deleteFile:(NSString*)path {
    if( [self exist:path] ){
        NSFileManager* fm = [NSFileManager defaultManager];
        NSError* err;
        [fm removeItemAtPath:path error:&err];
        return err==nil;
    }
    return YES;
}

+(BOOL) exist:(NSString*) path{
    BOOL directory = NO;
    NSFileManager *fileManage = [NSFileManager defaultManager];
    if ( [fileManage fileExistsAtPath:path isDirectory:&directory] && !directory) {
        return YES;
    }
    return NO;
}

+ (NSString*) PathForBundle:(NSBundle*) bundle  relativePath:(NSString*) relativePath {
    NSString* resourcePath = [(nil == bundle ? [NSBundle mainBundle] : bundle) resourcePath];
    return [resourcePath stringByAppendingPathComponent:relativePath];
}

+ (NSString*) PathForDocumentsResource:(NSString*) relativePath {
    static NSString* documentsPath = nil;
    if (nil == documentsPath) {
        NSArray* dirs = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        documentsPath = [dirs objectAtIndex:0];
    }
    return [documentsPath stringByAppendingPathComponent:relativePath];
}

+ (NSString*) PathForCachesResource:(NSString* )relativePath {
    static NSString* cachesPath = nil;
    if (nil == cachesPath) {
        NSArray* dirs = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
        cachesPath = [dirs objectAtIndex:0];
    }
    return [cachesPath stringByAppendingPathComponent:relativePath];
}

+(BOOL) createPath:(NSString*) path{
    NSFileManager *fileManage = [NSFileManager defaultManager];
    if ( ![fileManage fileExistsAtPath:path] ) {
        NSError* error = nil;
        [fileManage createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:&error];
        if( error )
            return NO;
        else
            return YES;
    }
    return YES;
}

+(id) stringToObject:(NSString*) s{
    if( s==nil )
        return nil;
    NSData* data = [s dataUsingEncoding:NSUTF8StringEncoding];
    NSError* error = nil;
    id dic = [NSJSONSerialization JSONObjectWithData:data
                                             options:NSJSONReadingAllowFragments
                                               error:&error];
    return dic;
}

+(NSString*) objectToString:(id) obj{
    if( obj ==nil )
        return nil;
    NSError* error = nil;
    NSData* data = [NSJSONSerialization dataWithJSONObject:obj options:0 error:&error];
    if( data && error==nil ) {
        return [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    }
    return nil;
}

+ (NSString*) MD5HashFromData:(NSData*) data {
    unsigned char bs[CC_MD5_DIGEST_LENGTH];
    bzero(bs, sizeof(bs));
    CC_MD5_CTX context;
    CC_MD5_Init(&context);
    size_t bytesHashedNum = 0;
    while (bytesHashedNum < [data length]) {
        CC_LONG updateSize = 1024 * 1024;
        if (([data length] - bytesHashedNum) < (size_t)updateSize) {
            updateSize = (CC_LONG)([data length] - bytesHashedNum);
        }
        CC_MD5_Update(&context, (char *)[data bytes] + bytesHashedNum, updateSize);
        bytesHashedNum += updateSize;
    }
    CC_MD5_Final(bs, &context);
    
    return [NSString stringWithFormat:
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            bs[0], bs[1], bs[2],  bs[3],  bs[4],  bs[5],  bs[6], bs[7],
            bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14],bs[15]];
}

+ (NSData*) MD5HashDataFromData:(NSData*) data {
    unsigned char bs[CC_MD5_DIGEST_LENGTH];
    bzero(bs, sizeof(bs));
    CC_MD5_CTX context;
    CC_MD5_Init(&context);
    size_t bytesHashedNum = 0;
    while (bytesHashedNum < [data length]) {
        CC_LONG updateSize = 1024 * 1024;
        if (([data length] - bytesHashedNum) < (size_t)updateSize) {
            updateSize = (CC_LONG)([data length] - bytesHashedNum);
        }
        CC_MD5_Update(&context, (char *)[data bytes] + bytesHashedNum, updateSize);
        bytesHashedNum += updateSize;
    }
    CC_MD5_Final(bs, &context);
    
    return [[NSData alloc] initWithBytes:bs length:16];
}

BOOL lv_isLuaObjectHaveProperty(lv_State* L, int idx, const char* key){
    if (lv_type(L, idx) == LV_TTABLE ) {
        lv_checkstack(L, 8);
        lv_pushvalue(L, idx);
        lv_getfield(L, -1, key);
        BOOL ret = lv_type(L, -1)!=LV_TNIL;
        lv_pop(L, 2);
        return ret;
    }
    return NO;
}

static id luaObjBox(lv_State* L, int idx){
    LVLuaObjBox* box = [[LVLuaObjBox alloc] init:L stackID:idx];
    return box;
}

id lv_luaValueToNativeObject(lv_State* L, int idx){
    int type = lv_type(L, idx);
    switch ( type ) {
        case LV_TNIL: {
            return nil;
        }
        case LV_TUSERDATA: {
            LVUserDataInfo* user =  (LVUserDataInfo*)lv_touserdata(L, idx);
            id<LVProtocal> obj =  (__bridge id<LVProtocal>)(user->object);
            if( [obj respondsToSelector:@selector(lv_nativeObject)] ){
                return [obj lv_nativeObject];
            }
            LVError(@"lv_luaValueToNativeObject.1");
            return obj;
        }
        case LV_TLIGHTUSERDATA:{
            LVPointerValueBox* box = [[LVPointerValueBox alloc] init];
            box.pointer = lv_touserdata(L, idx);
            return box;
        }
        case LV_TBOOLEAN: {
            return [[NSNumber alloc] initWithBool:lv_toboolean(L, idx)];
        }
        case LV_TNUMBER: {
            return @( lv_tonumber(L, idx) );
        }
        case LV_TSTRING: {
            return lv_paramString(L, idx);
        }
        case LV_TTABLE: {
            if ( lv_isLuaObjectHaveProperty(L, idx, "__obj") ) {
                return luaObjBox(L, idx);
            } else {
                return lv_luaTableToDictionary(L, idx);
            }
        }
        case LV_TFUNCTION: {
            return [[LVBlock alloc] initWith:L statckID:idx];
        }
        default: {
            LVError(@"lv_luaObjectToNativeObject.2");
            return nil;
        }
    }
}



void lv_pushNativeObject(lv_State* L, id value){
    lv_checkstack(L, 4);
    if( value==nil || value == [NSNull null] ) {
        lv_pushnil(L);
        return;
    } else if( [value isKindOfClass:[NSNumber class]] ) {
        static Class boolClass = nil;;
        if ( boolClass ==nil ) {
            boolClass = [@(YES) class];
        }
        NSNumber* number = value;
        if( [value class] == boolClass) {
            //  是否是bool类型
            lv_pushboolean(L, number.boolValue);
            return;
        } else {
            lv_pushnumber(L, number.doubleValue);
            return;
        }
    }  else if( [value isKindOfClass:[NSString class]] ) {
        NSString* s = value;
        lv_pushstring(L, s.UTF8String);
        return;
    } else if( [value isKindOfClass:[NSDictionary class]] ) {
        NSDictionary* dictionary = value;
        lv_newtable(L);
        for (NSString *key in dictionary) {
            NSString* value = dictionary[key];
            lv_checkstack(L, 4);
            lv_pushstring(L, key.UTF8String);
            lv_pushNativeObject(L,value);
            lv_settable(L, -3);
        }
        return;
    } else if( [value isKindOfClass:[NSArray class]] ) {
        NSArray* array = value;
        lv_newtable(L);
        for (int i=0; i<array.count; i++) {
            id value = array[i];
            lv_pushnumber(L, i+1);
            lv_pushNativeObject(L,value);
            lv_settable(L, -3);
        }
        return;
    } else if( [value isKindOfClass:[LVBlock class] ] ) {
        LVBlock* block = (LVBlock*)value;
        [block pushFunctionToStack];
        return;
    } else {
        lv_pushNativeObjectWithBox(L, value);
        return;
    }
}

void lv_pushNativeObjectWithBox(lv_State * L, id nativeObject ){
    LVNativeObjBox* nativeObjBox = [[LVNativeObjBox alloc] init:L nativeObject:nativeObject];
    nativeObjBox.openAllMethod = YES;// 所有api都开放
    
    NEW_USERDATA(userData, NativeObject);
    userData->object = CFBridgingRetain(nativeObjBox);
    nativeObjBox.lv_userData = userData;
    lvL_getmetatable(L, META_TABLE_NativeObject );
    lv_setmetatable(L, -2);
}

// 获取参数-》字符串类型
NSString* lv_paramString(lv_State* L, int idx ){
    if( lv_gettop(L)>=ABS(idx) && lv_type(L, idx) == LV_TSTRING ) {
        size_t n = 0;
        const char* chars = lvL_checklstring(L, idx, &n );
        NSString* s = @"";
        if( chars && n>0 ){
            s = [NSString stringWithUTF8String:chars];
        }
        return s;
    }
    return nil;
}

+(BOOL) ios8{
    static BOOL yes = NO;
    static BOOL inited = NO;
    if( !inited ) {
        inited = YES;
        yes = ([[[UIDevice currentDevice] systemVersion] compare:@"8.0"] != NSOrderedAscending);
    }
    return yes;
}

void lv_pushUDataRef(lv_State* L, int key) {
    // -1:userdata
    if( lv_gettop(L)>=1 && lv_type(L, -1)==LV_TUSERDATA ) {
        lv_checkstack(L, 2);
        
        if( lv_getUDataLuaTable(L, -1) ) {
            lv_remove(L, -2);
        }
        if( lv_type(L, -1)==LV_TTABLE ) {
            lv_pushnumber(L, key);
            lv_gettable(L, -2);
            lv_remove(L, -2);
        } else {
            LVError( @"lv_pushUDataRef.1" );
        }
    } else {
        LVError( @"lv_pushUDataRef.2" );
    }
}

void lv_udataRef(lv_State* L, int key ){
    //-2: userdata   -1: value
    if( lv_gettop(L)>=2 && lv_type(L, -2)==LV_TUSERDATA ) {
        lv_checkstack(L, 8);
        
        lv_getUDataLuaTable(L, -2);//table
        if( lv_type(L, -1)==LV_TTABLE ) {
            lv_pushnumber(L, key);// key
            lv_pushvalue(L, -3);//value
            lv_settable(L, -3);
            lv_pop(L, 2);
        } else {
            LVError( @"lv_udataRef" );
        }
    }
}

void lv_udataUnref(lv_State* L, int key) {
    // -1:userdata
    if( lv_gettop(L)>=1 && lv_type(L, -1)==LV_TUSERDATA ) {
        lv_checkstack(L, 8);
        
        lv_getUDataLuaTable(L, -1);
        if( lv_type(L, -1)==LV_TTABLE ) {
            lv_pushnumber(L, key);
            lv_pushnil(L);
            lv_settable(L, -3);
            lv_pop(L, 1);
        } else {
            LVError( @"lv_udataUnref" );
        }
    }
}


void lv_luaTableSetWeakWindow(lv_State* L, UIView* cell){
    lv_pushstring(L, "window");
    
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(cell);
    
    lvL_getmetatable(L, META_TABLE_UIView );
    lv_setmetatable(L, -2);
    
    lv_settable(L, -3);
}

void lv_luaTableRemoveKeys(lv_State* L, const char** keys){
    if ( lv_type(L, -1)== LV_TTABLE ) {
        for ( int i=0; ;i++ ){
            const char* key = keys[i];
            if( key ) {
                lv_pushnil(L);
                lv_setfield(L, -2, key);
            } else {
                break;
            }
        }
    }
}


int lv_callbackFunction(lv_State* L, const char* functionName){
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        if ( lv_gettop(L)>=2 && lv_type(L, 2)==LV_TFUNCTION ) {
            lv_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if( lv_type(L, -1)==LV_TNIL ) {
                lv_createtable(L, 0, 0);
                lv_udataRef(L, USERDATA_KEY_DELEGATE);
            }
            lv_pushstring(L, functionName);
            lv_pushvalue(L, 2);
            lv_settable(L, -3);
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            lv_pushstring(L, functionName);
            lv_gettable(L, -2);
            return 1;
        }
    }
    return 0;
}

BOOL lv_objcEqual(id obj1, id obj2) {
    return obj1 == obj2 || [obj1 isEqual:obj2];
}

+ (UIFont *)fontWithName:(NSString *)fontName size:(CGFloat)fontSize bundle:(LVBundle*)bundle{
    UIFont* font = [UIFont fontWithName:fontName size:fontSize];
    if( font == nil ) {
        [LVUtil loadFont:fontName package:bundle];
        font = [UIFont fontWithName:fontName size:fontSize];
    }
    return font;
}

+(int) loadFont:(NSString*) fileName package:(LVBundle*)bundle{
    int ret = 0;
    if( [fileName.lowercaseString hasSuffix:@".ttf"]==NO ) {
        fileName = [NSString stringWithFormat:@"%@.ttf",fileName];
    }
    NSData *inData =  [bundle resourceWithName:fileName];/* your font-file data */;
    CFErrorRef error;
    CGDataProviderRef provider = CGDataProviderCreateWithCFData((CFDataRef)inData);
    CGFontRef font = CGFontCreateWithDataProvider(provider);
    if (! CTFontManagerRegisterGraphicsFont(font, &error)) {
        CFStringRef errorDescription = CFErrorCopyDescription(error);
        NSLog(@"Failed to load font: %@", errorDescription);
        LVReleaseAndNull(errorDescription);
        ret = -1;
    }
    LVReleaseAndNull(font);
    LVReleaseAndNull(provider);
    return ret;
}

void LVLog( NSString* format, ... ){
#ifdef DEBUG
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView]  %@", s);
    va_end(params);
#endif
}

void LVError( NSString* format, ... ){
#ifdef DEBUG
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView][error]   %@", s);
    va_end(params);
#endif
}

@end

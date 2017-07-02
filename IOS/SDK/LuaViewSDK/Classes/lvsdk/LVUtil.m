/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVUtil.h"
#import <CommonCrypto/CommonDigest.h>
#import "LVBlock.h"
#import "LVNativeObjBox.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "LVPointerValueBox.h"
#import "LVDebuger.h"
#import "LVHeads.h"
#import <CoreText/CTFontManager.h>

@implementation LVUtil


+(BOOL) isExternalUrl:(NSString*) url{
    return [url hasPrefix:@"https://"] || [url hasPrefix:@"http://"];
}

+(NSString*) call:(lua_State*) l  lightUserData:(id) lightUserData key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs {
    if( l ){
        lua_checkstack32(l);
        lua_pushlightuserdata(l, (__bridge void *)lightUserData);// key=view
        lua_gettable(l, LUA_REGISTRYINDEX);/* table = registry[&Key] */
        
        return [LVUtil call:l key1:key1 key2:key2 nargs:nargs nrets:0];
    }
    return nil;
}

+(NSString*) call:(lua_State*) l key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs nrets:(int)nret{
    return [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:nargs nrets:nret retType:-8];
}
    
+(NSString*) call:(lua_State*) l key1:(const char*) key1 key2:(const char*)key2  key3:(const char*)key3
            nargs:(int)nargs nrets:(int)nret
          retType:(int) retType{
    return [self call:l key1:key1 key2:key2 key3:key3 key4:NULL nargs:nargs nrets:nret retType:retType];
}
    
+(NSString*) call:(lua_State*) l key1:(const char*) key1 key2:(const char*)key2  key3:(const char*)key3 key4:(const char*)key4
      nargs:(int)nargs nrets:(int)nret
    retType:(int) retType{
    if( l ){
        if( lua_type(l, -1)==LUA_TNIL ){
            return @"LVUtil: call nil function";
        } else if( lua_type(l, -1)==LUA_TTABLE && key1){//table
            lua_getfield(l, -1, key1);
            lua_remove(l, -2);
            
            if( lua_type(l, -1)==LUA_TTABLE && key2){//table
                lua_getfield(l, -1, key2);
                lua_remove(l, -2);
                
                if( lua_type(l, -1)==LUA_TTABLE && key3){//table
                    lua_getfield(l, -1, key3);
                    lua_remove(l, -2);
                    
                    if( lua_type(l, -1)==LUA_TTABLE && key4){//table
                        lua_getfield(l, -1, key4);
                        lua_remove(l, -2);
                    }
                }
            }
        }
        int type = lua_type(l, -1);
        if ( type==retType && nret==1 ) {
            return nil;
        }
        if( type == LUA_TFUNCTION ){//function
            return lv_runFunctionWithArgs(l, nargs, nret);
        }
    }
    return @"LVUtil:lua_State is nil";
}

NSString* lv_runFunction(lua_State* l){
    return lv_runFunctionWithArgs(l, 0, 0);
}

NSString* lv_runFunctionWithArgs(lua_State* l, int nargs, int nret){
    if( l && lua_type(l, -1) == LUA_TFUNCTION ) {
        if( nargs>0 ){
            lua_insert(l, -nargs-1);
        }
        int errorCode = lua_pcall( l, nargs, nret, 0);
        if ( errorCode != 0 ) {
            const char* s = lua_tostring(l, -1);
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
void lv_pushUserdata(lua_State* L, void* p){///是否正确 ????????
    if( p ) {
        Udata* u = (Udata*)p;
        u -= 1;
        lua_lock(L);
        luaC_checkGC(L);
        setuvalue(L, L->top, u);
        api_incr_top(L);
        lua_unlock(L);
    } else {
        lua_pushnil(L);
    }
}

id lv_luaTableToDictionary(lua_State* L ,int index){
    if( lua_type(L, index)!=LUA_TTABLE ) {
        return nil;
    }
    lua_checkstack(L, 128);
    NSMutableDictionary* dic = nil;
    NSMutableArray* array = nil;
    //lua_settop(L, 8);
    // Push another reference to the table on top of the stack (so we know
    // where it is, and this function can work for negative, positive and
    // pseudo indices
    lua_pushvalue(L, index);
    // stack now contains: -1 => table
    lua_pushnil(L);
    // stack now contains: -1 => nil; -2 => table
    while (lua_next(L, -2))
    {
        int keyType = lua_type(L, -2);
        
        id value = nil;
        if( lua_type(L, -1)==LUA_TSTRING ){
            value = lv_paramString(L, -1);
        } else if( lua_type(L, -1)==LUA_TNUMBER ){
            value = @(lua_tonumber(L, -1) );
        } else if( lua_type(L, -1)==LUA_TTABLE ){
            value = lv_luaTableToDictionary(L,-1);
        } else if( lua_type(L, -1)==LUA_TBOOLEAN ){
            value = @( ((BOOL)lua_toboolean(L, -1)) );
        } else if ( lua_type(L, -1)==LUA_TUSERDATA ) {
            LVUserDataInfo* user =  (LVUserDataInfo*)lua_touserdata(L, -1);
            id<LVProtocal> obj =  (__bridge id<LVProtocal>)(user->object);
            if( [obj respondsToSelector:@selector(lv_nativeObject)] ){
                value = [obj lv_nativeObject];
            } else {
                LVError(@"lv_luaTableToDictionary.1");
            }
        } else {
            LVError(@"lv_luaTableToDictionary.2");
        }
        // stack now contains: -1 => value; -2 => key; -3 => table
        if( value ) {
            if( keyType== LUA_TNUMBER ) {
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
        lua_pop(L, 1);
        // stack now contains: -1 => key; -2 => table
    }
    lua_pop(L, 1);
    
    if( [dic count]>0 ) {
        return dic;
    }
    if ( array.count>0 ) {
        return array;
    }
    // Stack is now the same as it was on entry to this function
    return @{};
}

NSArray* lv_luaTableKeys(lua_State* L, int index){
    lua_checkstack(L, 128);
    NSMutableArray* keys = [[NSMutableArray alloc] init];
    //lua_settop(L, 8);
    // Push another reference to the table on top of the stack (so we know
    // where it is, and this function can work for negative, positive and
    // pseudo indices
    if ( lua_type(L, index)!= LUA_TTABLE ){
        return nil;
    }
    lua_pushvalue(L, index);
    // stack now contains: -1 => table
    lua_pushnil(L);
    // stack now contains: -1 => nil; -2 => table
    while (lua_next(L, -2))
    {
        NSString* key   = lv_paramString(L, -2);
        // stack now contains: -1 => value; -2 => key; -3 => table
        if( key ) {
            [keys addObject:key];
        }
        lua_pop(L, 1);
        // stack now contains: -1 => key; -2 => table
    }
    lua_pop(L, 1);
    // Stack is now the same as it was on entry to this function
    if( keys.count>0 ){
        return keys;
    }
    return nil;
}

NSArray* lv_luaTableToArray(lua_State* L,int stackID)
{
    if( lua_type(L, stackID)==LUA_TTABLE) {
        int count = luaL_getn(L, stackID);
        NSMutableArray* array = [[NSMutableArray alloc] init];
        
        for (int i = 0; i < count; i++)
        {
            lua_rawgeti(L, stackID, i+1);
            NSString* s = lv_paramString(L, -1);
            [array addObject:s];
            lua_pop(L,1);
        }
        return array;
    }
    return nil;
}

#pragma -mark registry

+ (void) registryValue:(lua_State*)L key:(id) key stack:(int) stackID{
    if( L ) {
        lua_checkstack(L, 4);
        lua_pushvalue(L, stackID );    // value
        lua_pushlightuserdata(L, (__bridge void *)(key) );   // key
        lua_insert(L, -2);                // key <==> value 互换
        lua_settable(L, LUA_REGISTRYINDEX);// registry[&Key] = fucntion
    }
}

+ (void) unregistry:(lua_State*) L key:(id) key{
    if( L ) {
        lua_checkstack(L, 2);
        lua_pushlightuserdata(L, (__bridge void *)(key) );   // key
        lua_pushnil(L);                   // nil
        lua_settable(L, LUA_REGISTRYINDEX);// registry[&Key] = nil
    }
}

+ (void) pushRegistryValue:(lua_State*) L key:(id) key{
    if( L ){
        lua_pushlightuserdata(L, (__bridge void *)(key));// key=button
        lua_gettable(L, LUA_REGISTRYINDEX);/* value = registry[&Key] */
    }
}

void lv_createClassMetaTable(lua_State* L , const char* name ){
    luaL_newmetatable(L, name );
    lua_pushstring(L, "__index");//必须要的。
    lua_pushvalue(L, -2); /* pushes the metatable */
    lua_settable(L, -3); /* metatable.__index = metatable */
}

void lua_checkstack32(lua_State* l){
    if( l ){
        lua_checkstack( l, 32);
    }
}

void lv_clearFirstTableValue(lua_State* L){
    int num = lua_gettop(L);
    if( num>1 && lua_type(L, 1)==LUA_TTABLE ) {
        lua_checkstack(L, 4);
        lua_getfield(L, 1, LUAVIEW_SYS_TABLE_KEY);
        if( lua_isnil(L, -1) ) {
            lua_settop(L, num);
        } else {
            lua_settop(L, num);
            lua_remove(L, 1);
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

UIColor* lv_getColorFromStack(lua_State* L, int stackID){
    if ( lua_type(L, stackID)==LUA_TSTRING ) {
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
    } else if( lua_type(L,stackID)==LUA_TNUMBER ) {
        NSUInteger color = lua_tonumber(L, stackID);
        float a = 1;
        float r = ( (color>>16)&0xff )/255.0;
        float g = ( (color>>8)&0xff  )/255.0;
        float b = ( (color>>0)&0xff  )/255.0;
        int stackID3 = stackID + 1;
        if ( lua_gettop(L)>=stackID3 && lua_type(L,stackID3)==LUA_TNUMBER ) {
            a = lua_tonumber(L, stackID+1 );
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
    if( [urlStr hasPrefix:@"//"] ) {
        urlStr = [NSString stringWithFormat:@"https:%@",urlStr];
    }
    if( [urlStr.lowercaseString hasPrefix:@"http://"] ){
        urlStr = [NSString stringWithFormat:@"https://%@",[urlStr substringFromIndex:7]];
    }
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
        if( !ret ) {
            NSLog(@"[LuaView][error] saveFile: %@", path);
        }
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
    NSString *resourcePath = [bundle resourcePath];
    
    if (!resourcePath){
        resourcePath = @"/";
    }
    
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

BOOL lv_isLuaObjectHaveProperty(lua_State* L, int idx, const char* key){
    if (lua_type(L, idx) == LUA_TTABLE ) {
        lua_checkstack(L, 8);
        lua_pushvalue(L, idx);
        lua_getfield(L, -1, key);
        BOOL ret = lua_type(L, -1)!=LUA_TNIL;
        lua_pop(L, 2);
        return ret;
    }
    return NO;
}

static id luaObjBox(lua_State* L, int idx){
    LVLuaObjBox* box = [[LVLuaObjBox alloc] init:L stackID:idx];
    return box;
}

id lv_luaValueToNativeObject(lua_State* L, int idx){
    int type = lua_type(L, idx);
    switch ( type ) {
        case LUA_TNIL: {
            return nil;
        }
        case LUA_TUSERDATA: {
            LVUserDataInfo* user =  (LVUserDataInfo*)lua_touserdata(L, idx);
            id<LVProtocal> obj =  (__bridge id<LVProtocal>)(user->object);
            if( [obj respondsToSelector:@selector(lv_nativeObject)] ){
                return [obj lv_nativeObject];
            }
            LVError(@"lv_luaValueToNativeObject.1");
            return obj;
        }
        case LUA_TLIGHTUSERDATA:{
            LVPointerValueBox* box = [[LVPointerValueBox alloc] init];
            box.pointer = lua_touserdata(L, idx);
            return box;
        }
        case LUA_TBOOLEAN: {
            return [[NSNumber alloc] initWithBool:lua_toboolean(L, idx)];
        }
        case LUA_TNUMBER: {
            return @( lua_tonumber(L, idx) );
        }
        case LUA_TSTRING: {
            return lv_paramString(L, idx);
        }
        case LUA_TTABLE: {
            if ( lv_isLuaObjectHaveProperty(L, idx, "__obj") ) {
                return luaObjBox(L, idx);
            } else {
                return lv_luaTableToDictionary(L, idx);
            }
        }
        case LUA_TFUNCTION: {
            return [[LVBlock alloc] initWith:L statckID:idx];
        }
        default: {
            LVError(@"lv_luaObjectToNativeObject.2");
            return nil;
        }
    }
}



void lv_pushNativeObject(lua_State* L, id value){
    lua_checkstack(L, 4);
    if( [value isKindOfClass:[NSString class]] ) {
        NSString* s = value;
        lua_pushstring(L, s.UTF8String);
        return;
    } else if( [value isKindOfClass:[NSDictionary class]] ) {
        NSDictionary* dictionary = value;
        lua_newtable(L);
        for (NSString *key in dictionary) {
            NSString* value = dictionary[key];
            lua_checkstack(L, 4);
            lua_pushstring(L, key.UTF8String);
            lv_pushNativeObject(L,value);
            lua_settable(L, -3);
        }
        return;
    } else if( [value isKindOfClass:[NSArray class]] ) {
        NSArray* array = value;
        lua_newtable(L);
        for (int i=0; i<array.count; i++) {
            id value = array[i];
            lua_pushnumber(L, i+1);
            lv_pushNativeObject(L,value);
            lua_settable(L, -3);
        }
        return;
    } else if( [value isKindOfClass:[NSNumber class]] ) {
        static Class boolClass = nil;;
        if ( boolClass ==nil ) {
            boolClass = [@(YES) class];
        }
        NSNumber* number = value;
        if( [value class] == boolClass) {
            //  是否是bool类型
            lua_pushboolean(L, number.boolValue);
            return;
        } else {
            lua_pushnumber(L, number.doubleValue);
            return;
        }
    }  else if( value==nil || value == [NSNull null] ) {
        lua_pushnil(L);
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

void lv_pushNativeObjectWithBox(lua_State * L, id nativeObject ){
    LVNativeObjBox* nativeObjBox = [[LVNativeObjBox alloc] init:L nativeObject:nativeObject];
    nativeObjBox.openAllMethod = YES;// 所有api都开放
    
    NEW_USERDATA(userData, NativeObject);
    userData->object = CFBridgingRetain(nativeObjBox);
    nativeObjBox.lv_userData = userData;
    luaL_getmetatable(L, META_TABLE_NativeObject );
    lua_setmetatable(L, -2);
}

// 获取参数-》字符串类型
NSString* lv_paramString(lua_State* L, int idx ){
    if( lua_gettop(L)>=ABS(idx) && lua_type(L, idx) == LUA_TSTRING ) {
        size_t n = 0;
        const char* chars = luaL_checklstring(L, idx, &n );
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

void lv_pushUDataRef(lua_State* L, int key) {
    // -1:userdata
    if( lua_gettop(L)>=1 && lua_type(L, -1)==LUA_TUSERDATA ) {
        lua_checkstack(L, 2);
        
        if( lv_getUDataLuatable(L, -1) ) {
            lua_remove(L, -2);
        }
        if( lua_type(L, -1)==LUA_TTABLE ) {
            lua_pushnumber(L, key);
            lua_gettable(L, -2);
            lua_remove(L, -2);
        } else {
            LVError( @"lv_pushUDataRef.1" );
        }
    } else {
        LVError( @"lv_pushUDataRef.2" );
    }
}

void lv_udataRef(lua_State* L, int key ){
    //-2: userdata   -1: value
    if( lua_gettop(L)>=2 && lua_type(L, -2)==LUA_TUSERDATA ) {
        lua_checkstack(L, 8);
        
        lv_getUDataLuatable(L, -2);//table
        if( lua_type(L, -1)==LUA_TTABLE ) {
            lua_pushnumber(L, key);// key
            lua_pushvalue(L, -3);//value
            lua_settable(L, -3);
            lua_pop(L, 2);
        } else {
            LVError( @"lv_udataRef" );
        }
    }
}

void lv_udataUnref(lua_State* L, int key) {
    // -1:userdata
    if( lua_gettop(L)>=1 && lua_type(L, -1)==LUA_TUSERDATA ) {
        lua_checkstack(L, 8);
        
        lv_getUDataLuatable(L, -1);
        if( lua_type(L, -1)==LUA_TTABLE ) {
            lua_pushnumber(L, key);
            lua_pushnil(L);
            lua_settable(L, -3);
            lua_pop(L, 1);
        } else {
            LVError( @"lv_udataUnref" );
        }
    }
}

static int lv_setUDataLuatable (lua_State *L, int index) {
    if( lua_type(L, index)==LUA_TUSERDATA ){
        lua_setfenv(L, index);
        return 1;
    }
    return 0;
}

int lv_getUDataLuatable (lua_State *L, int index) {
    if( lua_type(L, index)==LUA_TUSERDATA ){
        lua_getfenv(L, index);
        return 1;
    }
    return 0;
}

int lv_createUDataLuatable (lua_State *L, int index){
    lua_checkstack(L, 8);
    lua_pushvalue(L, index);
    lua_createtable(L, 8, 0);
    lv_setUDataLuatable(L, -2);
    lua_pop(L, 1);
    return 1;
}

void lv_luaTableSetWeakWindow(lua_State* L, UIView* cell){
    lua_pushstring(L, "window");
    
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(cell);
    
    luaL_getmetatable(L, META_TABLE_UIView );
    lua_setmetatable(L, -2);
    
    lua_settable(L, -3);
}

void lv_luaTableRemoveKeys(lua_State* L, const char** keys){
    if ( lua_type(L, -1)== LUA_TTABLE ) {
        for ( int i=0; ;i++ ){
            const char* key = keys[i];
            if( key ) {
                lua_pushnil(L);
                lua_setfield(L, -2, key);
            } else {
                break;
            }
        }
    }
}


int lv_callbackFunction(lua_State* L, const char* functionName){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        if ( lua_gettop(L)>=2 && lua_type(L, 2)==LUA_TFUNCTION ) {
            lua_pushvalue(L, 1);
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            if( lua_type(L, -1)==LUA_TNIL ) {
                lua_createtable(L, 0, 0);
                lv_udataRef(L, USERDATA_KEY_DELEGATE);
            }
            lua_pushstring(L, functionName);
            lua_pushvalue(L, 2);
            lua_settable(L, -3);
            return 0;
        } else {
            lv_pushUDataRef(L, USERDATA_KEY_DELEGATE);
            lua_pushstring(L, functionName);
            lua_gettable(L, -2);
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


+(void) reg:(lua_State*)L clas:(id) c cfunc:(lua_CFunction) cfunc globalName:(NSString*)globalName defaultName:(NSString*) defaultName{
    if( defaultName || globalName ) {
        lua_checkstack(L, 12);
        NSString* className = NSStringFromClass(c);
        lua_pushstring(L, className.UTF8String);
        lua_pushcclosure(L, cfunc, 1);
        
        lua_setglobal(L, globalName ? globalName.UTF8String : defaultName.UTF8String );
    }
}

+(Class) upvalueClass:(lua_State*)L defaultClass:(Class) defaultClass{
    const char* classNameChars = lua_tostring(L, lua_upvalueindex(1));
    NSMutableString* className = [NSMutableString stringWithFormat:@"%s",classNameChars];
    Class c = NSClassFromString(className);
    if( c == nil ) {
        c = defaultClass;
    }
    return c;
}

+(void) defineGlobal:(NSString*)globalName value:(id) value L:(lua_State*)L {
    if( globalName && value ) {
        lua_checkstack(L, 12);
        lv_pushNativeObject(L, value);
        lua_setglobal(L, globalName.UTF8String);
    } else {
        LVError(@"define Global Value");
    }
}

void lv_defineGlobalFunc(const char* globalName, lua_CFunction func, lua_State* L) {
    if( globalName && func ) {
        lua_checkstack(L, 12);
        lua_pushcfunction(L, func);
        lua_setglobal(L, globalName);
    } else {
        LVError(@"define Global Function");
    }
}

void lv_addSubview(LuaViewCore* lv, UIView* superview, UIView* subview){
    [subview removeFromSuperview];
    [subview.layer removeFromSuperlayer];
    if( lv.closeLayerMode
       || [superview isKindOfClass:[UIScrollView class]]
       || [subview isKindOfClass:[UIScrollView class]] ) {
        [superview addSubview:subview];
    } else {
        [superview.layer addSublayer:subview.layer];
    }
}

void lv_addSubviewByIndex(LuaViewCore* lv, UIView* superview, UIView* subview, int index){
    [subview removeFromSuperview];
    [subview.layer removeFromSuperlayer];
    if( lv.closeLayerMode
       || [superview isKindOfClass:[UIScrollView class]]
       || [subview isKindOfClass:[UIScrollView class]] ) {
        [superview insertSubview:subview atIndex:index];
    } else {
        [superview.layer addSublayer:subview.layer];
    }
}

static id objectForKey(NSDictionary* dic, id key, Class clazz){
    id obj = [dic objectForKey:key];
    if ([obj isKindOfClass:clazz]) {
        return obj;
    }
    return nil;
}

NSString* safe_stringForKey(NSDictionary*dic, id key) {
    NSString *obj = objectForKey(dic, key, [NSString class] );
    return obj;
}

NSDictionary * safe_dictionaryForKey(NSDictionary* dic, id key) {
    NSDictionary *obj = objectForKey( dic, key, [NSDictionary class] );
    return obj;
}

NSDate * safe_dateForKey(NSDictionary* dic, id key ){
    NSDate *obj = objectForKey(dic, key, [NSDate class] );
    return obj;
}



+ (NSTimer *) scheduledTimerWithTimeInterval:(NSTimeInterval)inTimeInterval
                                         block:(void (^)(NSTimer *timer))block
                                       repeats:(BOOL)inRepeats {
    NSParameterAssert(block != nil);
    return [NSTimer scheduledTimerWithTimeInterval:inTimeInterval
                                         target:self
                                       selector:@selector(lv_executeBlockFromTimer:)
                                       userInfo:[block copy]
                                        repeats:inRepeats];
}

+ (void)lv_executeBlockFromTimer:(NSTimer *)aTimer {
    void (^block)(NSTimer *) = [aTimer userInfo];
    if (block)
        block(aTimer);
}

+ (NSString*) luaTrace:(lua_State*) L{
    // 打印lua调用栈开始
    lua_getglobal(L, "debug");
    lua_getfield(L, -1, "traceback");
    int iError = lua_pcall( L,//VMachine
                           0,//Argument Count
                           1,//Return Value Count
                           0);
    const char* s = lua_tostring(L, -1);
    NSString* stack = [NSString stringWithFormat:@"%s",s];
    NSLog(@"err:%d \n %@", iError, stack);
    return stack;
}

+ (UIImage*) image:(UIImage*)image croppingToRect:(CGRect)rect
{
    UIGraphicsBeginImageContext(rect.size);
    
    CGContextRef currentContext = UIGraphicsGetCurrentContext();
    
    CGRect clippedRect = CGRectMake(0, 0, rect.size.width, rect.size.height);
    
    CGContextClipToRect( currentContext, clippedRect);
    
    CGRect drawRect = CGRectMake(rect.origin.x * -1,
                                 
                                 rect.origin.y * -1,
                                 
                                 image.size.width,
                                 
                                 image.size.height);
    
    CGContextTranslateCTM(currentContext, 0.0, rect.size.height);
    
    CGContextScaleCTM(currentContext, 1.0, -1.0);
    
    CGContextDrawImage(currentContext, drawRect, image.CGImage);
    
    UIImage *cropped = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    return cropped;
}

void LVLog( NSString* format, ... ){
#ifdef DEBUG
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView]%@", s);
    va_end(params);
#endif
}

void LVError( NSString* format, ... ){
#ifdef DEBUG
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView][error]%@", s);
    va_end(params);
#endif
}

@end

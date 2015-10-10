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

@implementation LVUtil


+(BOOL) isExternalUrl:(NSString*) url{
    return [url hasPrefix:@"https://"] || [url hasPrefix:@"http://"];
}

+(int) call:(lv_State*) l  lightUserData:(id) lightUserData key:(const char*) key{
    return [self call:l lightUserData:lightUserData key:key nargs:0];
}

+(int) call:(lv_State*) l  lightUserData:(id) lightUserData key:(const char*) key1 nargs:(int)nargs {
    return [self call:l lightUserData:lightUserData key:key1 nargs:nargs nrets:0];
}
+(int) call:(lv_State*) l  lightUserData:(id) lightUserData key:(const char*) key1 nargs:(int)nargs nrets:(int)nret{
    return [self call:l lightUserData:lightUserData key1:key1 key2:NULL nargs:nargs nrets:nret];
}

+(int) call:(lv_State*) l  lightUserData:(id) lightUserData key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs nrets:(int)nret{
    if( l ){
        lv_checkStack32(l);
        lv_pushlightuserdata(l, (__bridge void *)lightUserData);// key=view
        lv_gettable(l, LV_REGISTRYINDEX);/* table = registry[&Key] */
        
        return [LVUtil call:l key1:key1 key2:key2 nargs:nargs nrets:nret];
    }
    return -1;
}

+(int) call:(lv_State*) l key1:(const char*) key1 key2:(const char*)key2 nargs:(int)nargs nrets:(int)nret{
    return [LVUtil call:l key1:key1 key2:key2 key3:NULL nargs:nargs nrets:nret];
}

+(int) call:(lv_State*) l key1:(const char*) key1 key2:(const char*)key2  key3:(const char*)key3 nargs:(int)nargs nrets:(int)nret{
    if( l ){
        if( lv_type(l, -1)==LV_TNIL ){
            return -1;
        } else if( lv_type(l, -1)==LV_TTABLE && key1){//table
            lv_pushstring(l, key1);
            lv_gettable(l, -2);
            lv_remove(l, -2);
            
            if( lv_type(l, -1)==LV_TTABLE && key2){//table
                lv_pushstring(l, key2);
                lv_gettable(l, -2);
                lv_remove(l, -2);
                
                if( lv_type(l, -1)==LV_TTABLE && key3){//table
                    lv_pushstring(l, key3);
                    lv_gettable(l, -2);
                    lv_remove(l, -2);
                }
            }
        }
        if( lv_type(l, -1)==LV_TFUNCTION ){//function
            return lv_runFunctionWithArgs(l, nargs, nret);
        }
        if( nret<=1 ) {
            return 0;
        } else {
            return -1;
        }
    }
    return -1;
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

NSMutableDictionary* lv_luaTableToDictionary(lv_State* L ,int index){
    lv_checkstack(L, 128);
    NSMutableDictionary* dic = [[NSMutableDictionary alloc] init];
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
        NSString* key   = lv_paramString(L, -2);
        id value = nil;
        if( lv_type(L, -1)==LV_TSTRING ){
            value = lv_paramString(L, -1);
        } else if( lv_type(L, -1)==LV_TNUMBER ){
            value = @(lv_tonumber(L, -1) );
        } else if( lv_type(L, -1)==LV_TTABLE ){
            value = lv_luaTableToDictionary(L,-1);
        }
        // stack now contains: -1 => value; -2 => key; -3 => table
        if( value && key ) {
            [dic setObject:value forKey:key];
        }
        lv_pop(L, 1);
        // stack now contains: -1 => key; -2 => table
    }
    lv_pop(L, 1);
    // Stack is now the same as it was on entry to this function
    if( dic.count>0 ){
        return dic;
    }
    return nil;
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
    if( L && lv_gettop(L)>0 && lv_type(L, 1)==LV_TTABLE ) {
        lv_remove(L, 1);
    }
}

int lv_uicolor2int(UIColor* color,NSUInteger* c ,float* a){
    CGFloat r = 0;
    CGFloat g = 0;
    CGFloat b = 0;
    CGFloat alpha = 0;
    if( [color getRed:&r green:&g blue:&b alpha:&alpha] ){
        NSUInteger red   = (r*255);
        NSUInteger green = (g*255);
        NSUInteger blue  = (b*255);
        *c = (red<<16) | (green<<8) | blue;
        *a = alpha;
        return 1;
    }
    return 0;
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

+(BOOL) saveData:(NSData*) data  toFile:(NSString*) fileName{
    NSString* path = [LVUtil PathForCachesResource:fileName];
    if( path ){
        BOOL ret = [data writeToFile:path atomically:YES];
        return ret;
    }
    return NO;
}

+(NSData*) dataReadFromFile:(NSString*) fileName{
    NSString* path = [LVUtil cachesPath:fileName];
    if( path ){
        NSFileManager* fm = [NSFileManager defaultManager];
        NSData* data = [fm contentsAtPath:path];
        return data;
    }
    return nil;
}

+(BOOL) exist:(NSString*) path{
    BOOL directory = NO;
    NSFileManager *fileManage = [NSFileManager defaultManager];
    if ( [fileManage fileExistsAtPath:path isDirectory:&directory] && !directory) {
        return YES;
    }
    return NO;
}

+(NSString*) cachesPath:(NSString*) fileName{
    NSString* path = nil;
    path = [LVUtil PathForLuaViewResource:fileName];
    if( [self exist:path] ){
        return path;
    }
    path = [LVUtil PathForCachesResource:fileName];
    if( [self exist:path] ){
        return path;
    }
    path = [LVUtil PathForDocumentsResource:fileName];
    if( [self exist:path] ){
        return path;
    }
    path = [LVUtil PathForBundle:nil  relativePath:fileName];
    if( [self exist:path] ){
        return path;
    }
    NSArray* bundlePaths = [LView bundleSearchPath];
    for( NSString* bundleName in bundlePaths ) {
        NSString* name = [NSString stringWithFormat:@"%@/%@", bundleName, fileName];
        path = [LVUtil PathForBundle:nil  relativePath:name];
        if( [self exist:path] ){
            return path;
        }
    }
    return nil;
}

+(UIImage*) cachesImage:(NSString*) imageName{
    NSString* path = [LVUtil cachesPath:imageName];
    if( path ){
        return [UIImage imageWithContentsOfFile:path];
    }
    UIImage* image =  [UIImage imageNamed:imageName];
    if( image ){
        return image;
    }
    NSArray* bundlePaths = [LView bundleSearchPath];
    for( NSString* bundleName in bundlePaths ) {
        NSString* name = [NSString stringWithFormat:@"%@/%@", bundleName, imageName];
        UIImage* image =  [UIImage imageNamed:name];
        if( image ){
            return image;
        }
    }
    return nil;
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

+ (NSString*) PathForLuaViewResource:(NSString* )relativePath {
    {// 首次初始化目录
        static int inited = NO;
        if( !inited ){
            inited = YES;
            [LVUtil createPath:LUAVIEW_ROOT_PATH];
        }
    }
    return [LVUtil PathForCachesResource:[NSString stringWithFormat:@"%@/%@",LUAVIEW_ROOT_PATH,relativePath]];
}

+(BOOL) createPath:(NSString*) path{
    path = [LVUtil PathForCachesResource:path];
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
            LVUserDataNativeObject* user =  (LVUserDataNativeObject*)lv_touserdata(L, idx);
            if ( LVIsType(user, LVUserDataNativeObject) ) {
                LVNativeObjBox* objBox = (__bridge LVNativeObjBox *)(user->realObjBox);
                return objBox.realObject;
            } else if ( LVIsType(user, LVUserDataView) ) {
                LVUserDataView* viewBox = (LVUserDataView*)user;
                UIView* view = (__bridge UIView *)(viewBox->view);
                return view;
            }
            return nil;
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
            LVError(@"lv_luaObjectToNativeObject");
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
        NSNumber* number = value;
        lv_pushnumber(L, number.doubleValue);
        return;
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
    
    NEW_USERDATA(userData, LVUserDataNativeObject);
    userData->realObjBox = CFBridgingRetain(nativeObjBox);
    nativeObjBox.userData = userData;
    lvL_getmetatable(L, META_TABLE_NativeObject );
    lv_setmetatable(L, -2);
}

+(BOOL) ios6{
    static BOOL yes = NO;
    static BOOL inited = NO;
    if( !inited ) {
        inited = YES;
        yes = ([[[UIDevice currentDevice] systemVersion] compare:@"6.0"] != NSOrderedAscending);
    }
    return yes;
}

+(BOOL) ios7{
    static BOOL yes = NO;
    static BOOL inited = NO;
    if( !inited ) {
        inited = YES;
        yes = ([[[UIDevice currentDevice] systemVersion] compare:@"7.0"] != NSOrderedAscending);
    }
    return yes;
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


void lv_tableSetWeakWindow(lv_State* L, id cell){
    lv_pushstring(L, "window");
    
    NEW_USERDATA(userData, LVUserDataView);
    userData->view = CFBridgingRetain(cell);
    //lView.lv_userData = userData;
    
    lvL_getmetatable(L, META_TABLE_UIView );
    lv_setmetatable(L, -2);
    
    lv_settable(L, -3);
}

void LVLog( NSString* format, ... ){
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView]  %@", s);
    va_end(params);
}

void LVError( NSString* format, ... ){
    va_list params; //定义一个指向个数可变的参数列表指针;
    va_start(params,format);//va_start 得到第一个可变参数地址,
    NSString* s = [[NSString alloc] initWithFormat:format arguments:params];
    NSLog(@"[LuaView][error]   %@", s);
    va_end(params);
}

@end

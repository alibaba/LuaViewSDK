/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LuaViewCore.h"


#import "LuaViewCore.h"
#import "LVHeads.h"
#import "LVExGlobalFunc.h"
#import "LVTimer.h"
#import "LVDebuger.h"
#import "LVNativeObjBox.h"
#import "LVBlock.h"
#import "LVPkgManager.h"
#import "UIView+LuaView.h"
#import "LVDebugConnection.h"
#import "LVDebugConnection.h"
#import "LVCustomPanel.h"
#import <objc/runtime.h>
#import "LVRSA.h"
#import "LVBundle.h"
#import "LVButton.h"
#import "LVScrollView.h"
#import "LVTimer.h"
#import "LVUtil.h"
#import "LVPagerIndicator.h"
#import "LVLoadingIndicator.h"
#import "LVImage.h"
//#import "LVWebView.h"
#import "LVLabel.h"
#import "LVBaseView.h"
#import "LVTransform3D.h"
#import "LVTextField.h"
#import "LVAnimate.h"
#import "LVAnimator.h"
#import "LVDate.h"
#import "LVAlert.h"
#import "LVSystem.h"
#import "LVDB.h"
#import "LVGesture.h"
#import "LVTapGesture.h"
#import "LVPanGesture.h"
#import "LVPinchGesture.h"
#import "LVRotationGesture.h"
#import "LVHttp.h"
#import "LVData.h"
#import "LVSwipeGesture.h"
#import "LVLongPressGesture.h"
#import "LVDebuger.h"
#import "LVDownloader.h"
#import "LVAudioPlayer.h"
#import "LVFile.h"
#import "LVStyledString.h"
#import "LVNativeObjBox.h"
#import "LVCollectionView.h"
#import "LVEmptyRefreshCollectionView.h"
#import "LVStruct.h"
#import "LVNavigation.h"
#import "LVCustomPanel.h"
#import "LVCustomView.h"
#import "LVPagerView.h"
#import "LVCanvas.h"
#import "LVEvent.h"
#import "LVBitmap.h"
#import "LVCamera.h"

@interface LuaViewCore ()
@property (nonatomic,strong) id mySelf;
@property (nonatomic,assign) BOOL stateInited;
@property (nonatomic,assign) BOOL loadedDebugScript;
@property (atomic,assign) NSInteger callLuaTimes;
@property(nonatomic,weak) UIView* window;
@property (nonatomic,assign) int windowIdx;
@end

@implementation LuaViewCore

-(id) init{
    self = [super init];
    if( self ){
        [self myInit];
        [self registeLibs];
    }
    return self;
}
 
#pragma mark - init

-(void) myInit{
    self.checkDebugerServer = YES;
    self.disableAnimate = YES;
    self.closeLayerMode = YES;
    self.mySelf = self;

    
    self.lv_luaviewCore = self;
    self.rsa = [[LVRSA alloc] init];
    self.bundle = [[LVBundle alloc] init];
}

-(void) dealloc{
#ifdef DEBUG
    [self.debugConnection closeAll];
#endif
}

#pragma mark - run
-(NSString*) runFile:(NSString*) fileName{
    self.runInSignModel = FALSE;
    NSData* code = [self.bundle scriptWithName:fileName];
    
    return [self runData:code fileName:fileName];
}

-(NSString*) runSignFile:(NSString*) fileName{
    self.runInSignModel = TRUE;
    NSData* code = [self.bundle signedScriptWithName:fileName rsa:self.rsa];
    
    return [self runData:code fileName:fileName];
}

- (NSString*) loadPackage:(NSString*) packageName{
    return [self runPackage:packageName];
}

-(NSString*) runPackage:(NSString*) packageName {
    return [self runPackage:packageName args:nil];
}

-(NSString*) runPackage:(NSString*) packageName args:(NSArray*) args{
    self.runInSignModel = TRUE;
    
    NSString *packagePath = [LVPkgManager rootDirectoryOfPackage:packageName];
    [self.bundle addScriptPath:packagePath];
    [self.bundle addResourcePath:packagePath];

    NSString* fileName = @"main.lv";
    NSString* ret = [self runSignFile:fileName];
    lua_State* L = self.l;
    if( ret==nil && L ) {
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject( L, obj );
        }
        lua_getglobal(L, "main");// function
        if( lua_type(L, -1) == LUA_TFUNCTION ) {
            ret = lv_runFunctionWithArgs(L, (int)args.count, 0);
        }
    }
    return ret;
}


#ifdef DEBUG
-(void) checkDebugOrNot:(const char*) chars length:(NSInteger) len fileName:(NSString*) fileName {
    if( self.debugConnection.printToServer ){
        NSMutableData* data = [[NSMutableData alloc] init];
        [data appendBytes:chars length:len];
        
        [self.debugConnection sendCmd:@"loadfile" fileName:fileName info:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
}
#endif

- (NSString*)loadFile:(NSString *)fileName {
    NSData* code = [self.bundle scriptWithName:fileName];
    return [self loadData:code fileName:fileName];
}

- (NSString*)loadSignFile:(NSString *)fileName {
    NSData* code = [self.bundle signedScriptWithName:fileName rsa:self.rsa];
    return [self loadData:code fileName:fileName];
}

-(NSString*) loadScript:(NSString *)script fileName:(NSString *)fileName{
    return [self loadData:[script dataUsingEncoding:NSUTF8StringEncoding] fileName:fileName];
}

#ifdef DEBUG
extern char g_debug_lua[];

-(NSString*) loadDebugModel{
    NSData* data = [[NSData alloc] initWithBytes:g_debug_lua length:strlen(g_debug_lua)];
    return [self runData:data fileName:@"debug.lua"];
}

- (void) callLuaToExecuteServerCmd{
    [self performSelectorOnMainThread:@selector(callLuaToExecuteServerCmd0) withObject:nil waitUntilDone:NO];
}

- (void) callLuaToExecuteServerCmd0{
    NSString* cmd = self.debugConnection.receivedArray.lastObject;
    if( cmd ) {
        [self.debugConnection.receivedArray removeLastObject];
    }
    if( [cmd isKindOfClass:[NSString class]] ) {
        [self callLua:@"debug_runing_execute" args:@[cmd]];
    }
}


-(void) checkDeuggerIsRunningToLoadDebugModel{
    if( self.checkDebugerServer && self.debugConnection== nil) {
        self.debugConnection = [[LVDebugConnection alloc] init];
        self.debugConnection.lview = self;
    }
    
    if( self.checkDebugerServer && [self.debugConnection waitUntilConnectionEnd]>0 ) {
        if( self.loadedDebugScript == NO ) {
            self.loadedDebugScript = YES;
            [self.debugConnection sendCmd:@"log" info:@"[LuaView][调试日志] 开始调试!\n"];
            [self loadDebugModel];// 加载调试模块
        }
    }
}
#else
- (void) callLuaToExecuteServerCmd{
}
#endif

static void *l_alloc (void *ud, void *ptr, size_t osize, size_t nsize) {
    (void)ud;
    (void)osize;
    if (nsize == 0) {
        free(ptr);
        return NULL;
    } else {
        return realloc(ptr, nsize);
    }
}


-(void) registeLibs{
    if( !self.stateInited ) {
        self.stateInited = YES;
        self.l =  lua_newstate(l_alloc, (__bridge void *)(self));
        luaL_openlibs(self.l);
        NSArray* arr = nil;
        arr = @[
                [LVSystem class],
                [LVData class],
                [LVStruct class],
                [LVBaseView class],
                [LVButton class],
                [LVImage class],
                [LVBitmap class],
//                [LVWebView class],
                [LVLabel class],
                [LVScrollView class],
                [LVCollectionView class],
                [LVEmptyRefreshCollectionView class],
                [LVPagerView class],
                [LVCustomView class],
                [LVCanvas class],
                [LVEvent class],
                [LVTimer class],
                [LVPagerIndicator class],
                [LVCustomPanel class],
                [LVTransform3D class],
                [LVAnimator class],
                [LVTextField class],
                [LVAnimate class],
                [LVDate class],
                [LVAlert class],
                [LVDB class],
                [LVGesture class],
                [LVTapGesture class],
                [LVPinchGesture class],
                [LVRotationGesture class],
                [LVSwipeGesture class],
                [LVLongPressGesture class],
                [LVPanGesture class],
                [LVLoadingIndicator class],
                [LVHttp class],
                [LVDownloader class],
                [LVFile class],
                [LVAudioPlayer class],
                [LVStyledString class],
                [LVNavigation class],
                [LVExGlobalFunc class],
                [LVNativeObjBox class],
                [LVDebuger class],
                [LVCamera class],
                ];
        self.registerClasses = arr;
        [self registerAllClass];
        
        // 默认支持RefreshCollectionView
        self[@"RefreshCollectionView"] = [LVCollectionView class];
    }
}

-(void) registerAllClass{
    lua_State* L = self.l;
    //清理栈
    for( NSInteger i =0; i<self.registerClasses.count; i++ ){
        lua_settop(L, 0);
        lua_checkstack(L, 256);
        id c = self.registerClasses[i];
        [c lvClassDefine:L globalName:nil];
    }
    //清理栈
    lua_settop(L, 0);
}

-(NSString*) runData:(NSData *)data fileName:(NSString*)fileName{
    lua_State* L = self.l;
    NSString* ret = [self loadData:data fileName:fileName];
    if ( ret ) {
        return ret;
    } else {
        return lv_runFunction(L);
    }
}
    
- (NSString*)loadData:(NSData *)data fileName:(NSString *)fileName{
    fileName = [fileName stringByReplacingOccurrencesOfString:@"//" withString:@"/"];

    lua_State* L = self.l;
    if( L==NULL ){
        LVError( @"Lua State is released !!!");
        return @"Lua State is released !!!";
    }
    if( fileName==nil ){
        static int i = 0;
        fileName = [NSString stringWithFormat:@"%d.lua",i];
    }
    if( data.length<=0 ){
        LVError(@"running chars == NULL, file: %@",fileName);
        return [NSString stringWithFormat:@"running chars == NULL, file: %@",fileName];
    }
    
#ifdef DEBUG
    [self checkDeuggerIsRunningToLoadDebugModel];
    [self checkDebugOrNot:data.bytes length:data.length fileName:fileName];
#endif
    
    int error = luaL_loadbuffer(L, data.bytes, data.length, fileName.UTF8String);
    if (error) {
        const char* s = lua_tostring(L, -1);
        LVError( @"%s", s );
#ifdef DEBUG
        NSString* string = [NSString stringWithFormat:@"[LuaView][error] %s\n",s];
        lv_printToServer(L, string.UTF8String, 0);
#endif
        return [NSString stringWithFormat:@"%s",s];
    } else {
        return nil;
    }
}

-(int) globalNumber:(const char*) globalName{
    lua_State* L = self.l;
    if ( L ==nil ){
        LVError( @"Lua State is released !!!");
        return 0;
    }
    lua_getglobal(L, globalName);
    
    if( !lua_isnumber(L, -1) ){
        //是否需要出栈？？？
        LVError(@"  '%s'  should be a number",globalName );
        return 0;
    } else {
        return (int) lua_tonumber(L, -1);
    }
}

-(NSString*) globalString:(const char*) globalName{
    lua_State* L = self.l;
    if ( L ==nil ){
        LVError( @"Lua State is released !!!");
        return nil;
    }
    lua_getglobal(L, globalName);
    
    if( !lua_isstring(L, -1) ){
        //是否需要出栈？？？
        LVError(@" '%s'  should be a number",globalName );
        return nil;
    } else {
        const char* chars = lua_tolstring(L, -1, NULL);
        if( chars ){
            return [NSString stringWithFormat:@"%s",chars];
        }
        return nil;
    }
}


#pragma mark - setFrame

-(void) releaseLuaView {
    [self performSelectorOnMainThread:@selector(freeMySelf) withObject:nil waitUntilDone:NO];
}

-(void) freeMySelf {
    [self.window removeFromSuperview];
    lua_State* l = self.l;
    self.l = NULL;
    if( l ){
        lua_close(l);
        l = NULL;
    }
    self.mySelf = nil;
}

//----------------------------------------------------------------------------------------


#pragma mark - layout

-(void) addSubview:(UIView *)view{
    [self.window addSubview:view];
}
    
#pragma mark - call lua global function
-(NSString*) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args{
    lua_State* L = self.l;
    if( L ){
        lua_checkstack(L, 8 + (int)args.count*2);
        [self pushWindow:environment];
        
        [LVUtil pushRegistryValue:L key:tag]; // param1: cell
        
        if( lua_type(L, -1)==LUA_TNIL ) {// if param1==nil , create param1
            lua_newtable(L);
            
            [LVUtil registryValue:L key:tag stack:-1];
        }
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(L,obj);
        }
        lua_getglobal(L, functionName.UTF8String);// function
        NSString* ret = lv_runFunctionWithArgs(L, (int)args.count+1, 0);
        [self popWindow:environment];
        return ret;
    }
    return nil;
}

-(NSString*) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args{
    return [self callLua:functionName tag:environment environment:environment args:args];
}

-(NSString*) callLua:(NSString*) functionName args:(NSArray*) args{
    lua_State* L = self.l;
    if( L ){
        lua_checkstack(L, (int)args.count*2 + 2);
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(L,obj);
        }
        lua_getglobal(L, functionName.UTF8String);// function
        return lv_runFunctionWithArgs(L, (int)args.count, 0);
    }
    return nil;
}

-(LVBlock*) getLuaBlock:(NSString*) name{
    lua_State* L = self.l;
    if ( L ==nil ){
        LVError( @"Lua State is released !!!");
        return nil;
    }
    return [[LVBlock alloc] initWith:L globalName:name];
}

#pragma mark - registe object.method

-(void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel {
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:sel weakMode:YES];
}

-(void) registerObject:(id) object forName:(NSString*) name sel:(SEL) sel weakMode:(BOOL)weakMode{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:sel weakMode:weakMode];
}

-(void) registerObject:(id) object forName:(NSString*) name{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:nil weakMode:YES];
}

-(void) registerObject:(id) object forName:(NSString*) name weakMode:(BOOL)weakMode{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:nil weakMode:weakMode];
}


- (void)setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key{
    lua_State* L = self.l;
    if ( L ==nil ){
        LVError( @"Lua State is released !!!");
        return;
    }
    if( [key isKindOfClass:[NSString class]] && class_isMetaClass(object_getClass(object)) ) {
        if( [object respondsToSelector:@selector(lvClassDefine:globalName:)] ) {
            [object lvClassDefine:L globalName:(NSString*)key];
            return;
        }
    }
    if ( [key isKindOfClass:[NSString class]] ){
        [LVNativeObjBox registeObjectWithL:L nativeObject:object name:(NSString*)key sel:nil weakMode:YES];
    }
}

-(void) unregisteObjectForName:(NSString*) name{
    lua_State* L = self.l;
    if ( L ==nil ){
        LVError( @"Lua State is released !!!");
        return ;
    }
    [LVNativeObjBox unregisteObjectWithL:L name:name];
}

- (void) registerLibs:(id) lib{
    lua_State* L = self.l;
    if( [lib respondsToSelector:@selector(lvClassDefine:globalName:)] ) {
        [lib lvClassDefine:L globalName:nil];
        return;
    }
}

- (void) registerName:(NSString*) name withObject:(id) object{
    [self registerObject:object forName:name];
}

- (void) registerPanel:(id) panel{
    [self registerPanel:panel forName:nil];
}

- (void) registerPanel:(id) panel forName:(NSString*) name{
    lua_State* L = self.l;
    if( [panel respondsToSelector:@selector(lvClassDefine:globalName:)] ) {
        [panel lvClassDefine:L globalName:name];
        return;
    }
}

- (void) unregister:(NSString*) name{
    [self unregisteObjectForName:name];
}
#pragma mark - package

+(void) downloadPackage:(NSString*)packageName withInfo:(NSDictionary*)info{
    [LVPkgManager downloadPackage:packageName withInfo:info];
}

-(BOOL) argumentToBool:(int) index{
    lua_State* L = self.l;
    if ( L ) {
        return lua_toboolean(L, index);
    }
    return NO;
}

-(double)  argumentToNumber:(int) index{
    lua_State* L = self.l;
    if ( L ) {
        return lua_tonumber(L, index);
    }
    return 0;
}

-(id) argumentToObject:(int) index{
    lua_State* L = self.l;
    if ( L ) {
        return lv_luaValueToNativeObject(L, index);
    }
    return 0;
}

-(void) containerAddSubview:(UIView *)view{
    lv_addSubview(self, self.window, view);
}

-(void) createWindow:(UIView *)window{
    self.window = window;
    [LVExGlobalFunc registry:self.l window:self.window];
}
    
-(const char*) windowName{
    const char* name = [NSString stringWithFormat:@"core.window.%d",self.windowIdx].UTF8String;
    return name;
}

-(void) pushRenderTarget:(UIView *)window{
    [self pushWindow:window];
}

-(void) pushWindow:(UIView*) window{
    lua_State* L = self.l;
    lua_checkstack(L,8);
    
    // 老的window 压栈
    const char* newWindowName = [self windowName];
    lua_getglobal(L, "window");
    lua_setglobal(L, newWindowName);
    self.windowIdx ++;
    
    // 创建新的window
    [self createWindow:window];//创建window
}

-(void) popRenderTarget:(UIView *)window{
    [self popWindow:window];
}

-(void) popWindow:(UIView*) window{
    if( self.window==window ) {
        lua_State* L = self.l;
        lua_checkstack(L,8);
        self.windowIdx --;
        const char* oldWindowName = [self windowName];
        
        // 出栈
        lua_getglobal(L, oldWindowName);
        lua_setglobal(L, "window");
        lua_getglobal(L, "window");
        
        LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, -1);
        if( user ) {
            self.window = (__bridge UIView *)(user->object);
        }
    } else {
        LVError(@"pop window!!!!");
    }
}

-(void) luaviewGC {
    // 清理luaview对应的内存
    lua_State *state = self.l;
    if (state) {
        lua_checkstack(state, 8);
        lua_pushnil(state);
        [LVUtil registryValue:state key:self stack:-1];
        LVBlock *block = [self getLuaBlock:@"System.gc"];
        [block callWithArgs:nil];
    }
}


@end


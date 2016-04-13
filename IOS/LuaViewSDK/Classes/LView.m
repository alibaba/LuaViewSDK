//
//
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LView.h"
#import "LVRegisterManager.h"
#import "LVTimer.h"
#import "LVDebuger.h"
#import "lVtable.h"
#import "LVNativeObjBox.h"
#import "LVBlock.h"
#import "LVPkgManager.h"
#import "UIView+LuaView.h"
#import "LVDebugConnection.h"
#import "LVDebugConnection.h"
#import "LVCustomPanel.h"
#import <objc/runtime.h>
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"
#import "LVRSA.h"
#import "LVBundle.h"


@interface LView ()
@property (nonatomic,strong) id mySelf;
@property (nonatomic,assign) BOOL stateInited;
@property (nonatomic,assign) BOOL loadedDebugScript;
@property (atomic,assign) NSInteger callLuaTimes;

@property (nonatomic,strong) LVRSA* rsa;
@end

@implementation LView

-(id) init{
    self = [super init];
    if( self ){
        [self myInit];
        [self registeLibs];
    }
    return self;
}

-(id) initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if( self ){
        [self myInit];
        [self registeLibs];
    }
    return self;
}

#pragma mark - init

-(void) myInit{
    self.mySelf = self;
    self.backgroundColor = [UIColor clearColor];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidShow:)
                                                 name:UIKeyboardDidShowNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidHide:)
                                                 name:UIKeyboardDidHideNotification
                                               object:nil];
    self.lv_lview = self;
    self.rsa = [[LVRSA alloc] init];
    self.bundle = [[LVBundle alloc] init];
}

-(void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.debugConnection closeAll];
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
    if( ret==nil && self.l ) {
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject( self.l, obj );
        }
        lv_getglobal(self.l, "main");// function
        if( lv_type(self.l, -1) == LV_TFUNCTION ) {
            ret = lv_runFunctionWithArgs(self.l, (int)args.count, 0);
        }
    }
    return ret;
}

-(void) checkDebugOrNot:(const char*) chars length:(NSInteger) len fileName:(NSString*) fileName {
    if( self.debugConnection.printToServer ){
        NSMutableData* data = [[NSMutableData alloc] init];
        [data appendBytes:chars length:len];
        
        [self.debugConnection sendCmd:@"loadfile" fileName:fileName info:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
}

- (NSString*)loadFile:(NSString *)fileName {
    NSData* code = [self.bundle scriptWithName:fileName];
    return [self loadData:code fileName:fileName];
}

- (NSString*)loadSignFile:(NSString *)fileName {
    NSData* code = [self.bundle signedScriptWithName:fileName rsa:self.rsa];
    return [self loadData:code fileName:fileName];
}

- (NSString*)loadData:(NSData *)data fileName:(NSString *)fileName {
    if (!data || !data.length || !fileName || !fileName.length) {
        LVError( @"running chars == NULL, file:%@", fileName);
        return [NSString stringWithFormat:@"running chars == NULL, file:%@",fileName];
    }
    
#ifdef DEBUG
    [self checkDeuggerIsRunningToLoadDebugModel];
    [self checkDebugOrNot:data.bytes length:data.length fileName:fileName];
#endif
    
    int error = lvL_loadbuffer(self.l, data.bytes, data.length, fileName.UTF8String);
    if (error) {
        const char* s = lv_tostring(self.l, -1);
        LVError( @"%s", s );
#ifdef DEBUG
        NSString* string = [NSString stringWithFormat:@"[LuaView][error]   %s\n",s];
        lv_printToServer(self.l, string.UTF8String, 0);
#endif
        return [NSString stringWithFormat:@"%s",s];
    } else {
        return nil;
    }
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
    if( self.debugConnection== nil) {
        self.debugConnection = [[LVDebugConnection alloc] init];
        self.debugConnection.lview = self;
    }

    if( [self.debugConnection waitUntilConnectionEnd]>0 ) {
        if( self.loadedDebugScript == NO ) {
            self.loadedDebugScript = YES;
            [self.debugConnection sendCmd:@"log" info:@"[调试器] 开始调试!\n"];
            [self loadDebugModel];// 加载调试模块
        }
    }
}
#else
- (void) callLuaToExecuteServerCmd{
}
#endif

-(void) registeLibs{
    if( !self.stateInited ) {
        self.stateInited = YES;
        self.l =  lvL_newstate();//lv_open();  /* opens */
        lvL_openlibs(self.l);
        
        [LVRegisterManager registryApi:self.l lView:self];
        self.l->lView = (__bridge void *)(self);
    }
}

-(NSString*) runData:(NSData *)data fileName:(NSString*)fileName{
    if( self.l==NULL ){
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
    
    int error = -1;
    error = lvL_loadbuffer(self.l, data.bytes , data.length, fileName.UTF8String) ;
    if ( error ) {
        const char* s = lv_tostring(self.l, -1);
        LVError( @"%s", s );
#ifdef DEBUG
        NSString* string = [NSString stringWithFormat:@"[LuaView][error]   %s\n",s];
        lv_printToServer(self.l, string.UTF8String, 0);
#endif
        return [NSString stringWithFormat:@"%s",s];
    } else {
        return lv_runFunction(self.l);
    }
}

-(int) globalNumber:(const char*) globalName{
    if ( self.l ==nil ){
        LVError( @"Lua State is released !!!");
        return 0;
    }
    lv_getglobal(self.l, globalName);
    
    if( !lv_isnumber(self.l, -1) ){
        //是否需要出栈？？？
        LVError(@"  '%s'  should be a number",globalName );
        return 0;
    } else {
        return (int) lv_tonumber(self.l, -1);
    }
}

-(NSString*) globalString:(const char*) globalName{
    if ( self.l ==nil ){
        LVError( @"Lua State is released !!!");
        return nil;
    }
    lv_getglobal(self.l, globalName);
    
    if( !lv_isstring(self.l, -1) ){
        //是否需要出栈？？？
        LVError(@" '%s'  should be a number",globalName );
        return nil;
    } else {
        const char* chars = lv_tolstring(self.l, -1, NULL);
        if( chars ){
            return [NSString stringWithFormat:@"%s",chars];
        }
        return nil;
    }
}


#pragma mark - setFrame

-(void) releaseLuaView{
    self.hidden = YES;
    [self removeFromSuperview];
    [self performSelector:@selector(freeMySelf) withObject:nil afterDelay:0.001];
}

-(void) freeMySelf{
    [self removeFromSuperview];
    lv_State* l = self.l;
    self.l = NULL;
    if( l ){
        lv_close(l);
        l = NULL;
    }
    self.mySelf = nil;
}

//----------------------------------------------------------------------------------------


#pragma mark - view appear

-(void) viewWillAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"ViewWillAppear"];
    }
}

-(void) viewDidAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"onShow"];//@"ViewDidAppear"
    }
}

-(void) viewWillDisAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"ViewWillDisAppear"];
    }
}

-(void) viewDidDisAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"onHide"];//@"ViewDidDisAppear"
    }
}

- (void)didMoveToSuperview{
    [super didMoveToSuperview];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"DidMoveToSuperview"];
    }
}

- (void)didMoveToWindow{
    [super didMoveToWindow];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"DidMoveToSuperview"];
    }
}

#pragma mark - keyboard

-(void) keyboardWillShow:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"KeyboardWillShow"];
    }
}
-(void) keyboardDidShow:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"KeyboardDidShow"];
    }
}
-(void) keyboardWillHide:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"KeyboardWillHide"];
    }
}
-(void) keyboardDidHide:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"KeyboardDidHide"];
    }
}

#pragma mark - 摇一摇相关方法
// 摇一摇开始摇动
- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self lv_callLuaByKey1:@"ShakeBegin"];
        }
    }
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self lv_callLuaByKey1:@"ShakeCanceled"];
        }
    }
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self lv_callLuaByKey1:@"ShakeEnded"];
        }
    }
}

#pragma mark - layout

-(void) layoutSubviews{
    [super layoutSubviews];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self lv_callLuaByKey1:@"LayoutSubviews"];
    }
}

-(void) addSubview:(UIView *)view{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView addSubview:view];
    } else {
        [super addSubview:view];
    }
}

-(void) setFrame:(CGRect)frame{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setFrame:frame];
    } else {
        [super setFrame:frame];
        [self.callback luaviewFrameDidChange:self];
    }
}

-(CGRect) frame{
    if( self.contentViewIsWindow && self.conentView ){
        return self.conentView.frame;
    } else {
        return super.frame;
    }
}

-(void) setBackgroundColor:(UIColor *)backgroundColor{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setBackgroundColor:backgroundColor];
    } else {
        [super setBackgroundColor:backgroundColor];
    }
}

-(UIColor*) backgroundColor{
    if( self.contentViewIsWindow && self.conentView ){
        return [self.conentView backgroundColor];
    } else {
        return [super backgroundColor];
    }
}

-(void) setClipsToBounds:(BOOL)clipsToBounds{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setClipsToBounds:clipsToBounds];
    } else {
        [super setClipsToBounds:clipsToBounds];
    }
}

-(BOOL) clipsToBounds{
    if( self.contentViewIsWindow && self.conentView ){
        return self.conentView.clipsToBounds;
    } else {
        return [super clipsToBounds];
    }
}

-(void) setAlpha:(CGFloat)alpha{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setAlpha:alpha];
    } else {
        [super setAlpha:alpha];
    }
}

-(CGFloat) alpha{
    if( self.contentViewIsWindow && self.conentView ){
        return self.conentView.alpha;
    } else {
        return [super alpha];
    }
}

-(void) setCenter:(CGPoint)center{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setCenter:center];
    } else {
        [super setCenter:center];
    }
}

-(CGPoint) center{
    if( self.contentViewIsWindow && self.conentView ){
        return [self.conentView center];
    } else {
        return [super center];
    }
}

-(void) setHidden:(BOOL)hidden{
    if( self.contentViewIsWindow && self.conentView ){
        [self.conentView setHidden:hidden];
    } else {
        [super setHidden:hidden];
    }
}

-(BOOL) isHidden{
    if( self.contentViewIsWindow && self.conentView ){
        return [self.conentView isHidden];
    } else {
        return [super isHidden];
    }
}

-(CALayer*) layer{
    if( self.contentViewIsWindow && self.conentView ){
        return [self.conentView layer];
    } else {
        return [super layer];
    }
}

#pragma mark - call lua global function
-(NSString*) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args{
    if( self.l ){
        lv_checkstack(self.l, 8 + (int)args.count*2);
        self.conentView = environment;
        self.contentViewIsWindow = YES;
        
        [LVUtil pushRegistryValue:self.l key:tag]; // param1: cell
        
        if( lv_type(self.l, -1)==LV_TNIL ) {// if param1==nil , create param1
            lv_newtable(self.l);
            
            [LVUtil registryValue:self.l key:tag stack:-1];
        }
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(self.l,obj);
        }
        lv_getglobal(self.l, functionName.UTF8String);// function
        NSString* ret = lv_runFunctionWithArgs(self.l, (int)args.count+1, 0);
        self.conentView = nil;
        self.contentViewIsWindow = NO;
        return ret;
    }
    return nil;
}

-(NSString*) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args{
    return [self callLua:functionName tag:environment environment:environment args:args];
}

-(NSString*) callLua:(NSString*) functionName args:(NSArray*) args{
    if( self.l ){
        lv_checkstack(self.l, (int)args.count*2 + 2);
        self.conentView = nil;
        self.contentViewIsWindow = NO;
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(self.l,obj);
        }
        lv_getglobal(self.l, functionName.UTF8String);// function
        return lv_runFunctionWithArgs(self.l, (int)args.count, 0);
    }
    return nil;
}

-(LVBlock*) getLuaBlock:(NSString*) name{
    if ( self.l ==nil ){
        LVError( @"Lua State is released !!!");
        return nil;
    }
    return [[LVBlock alloc] initWith:self.l globalName:name];
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
    if ( self.l ==nil ){
        LVError( @"Lua State is released !!!");
        return;
    }
    if( [key isKindOfClass:[NSString class]]
       && class_isMetaClass(object_getClass(object))
       && [object isSubclassOfClass:[LVCustomPanel class]] ) {
        [self registerCustomPanel:object boundName:(NSString*)key];
        return;
    }
    if ( [key isKindOfClass:[NSString class]] ){
        [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:(NSString*)key sel:nil weakMode:YES];
    }
}

-(void) unregisteObjectForName:(NSString*) name{
    if ( self.l ==nil ){
        LVError( @"Lua State is released !!!");
        return ;
    }
    [LVNativeObjBox unregisteObjectWithL:self.l name:name];
}

- (void) registerCustomPanel:(Class) c boundName:(NSString*) boundName{
    if( self.l ) {
        [LVCustomPanel addCustomPanel:c boundName:boundName state:self.l];
    }
}

#pragma mark - package

+(void) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*)info{
    [LVPkgManager downLoadPackage:packageName withInfo:info];
}

-(BOOL) argumentToBool:(int) index{
    if ( self.l ) {
        return lv_toboolean(self.l, index);
    }
    return NO;
}

-(double)  argumentToNumber:(int) index{
    if ( self.l ) {
        return lv_tonumber(self.l, index);
    }
    return 0;
}

-(id) argumentToObject:(int) index{
    if ( self.l ) {
        return lv_luaValueToNativeObject(self.l, index);
    }
    return 0;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<View(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

-(void) containerAddSubview:(UIView *)view{
    if( self.conentView ) {
        if( view.superview!=self.conentView ) {
            [self.conentView addSubview:view];
        }
    } else {
        if( view.superview!=self ) {
            [super addSubview:view];
        }
    }
}

// 证书

- (void) setPublicKeyFilePath:(NSString*) filePath{
    [self.rsa setPublicKeyFilePath:filePath];
}

@end

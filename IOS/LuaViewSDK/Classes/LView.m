//
//
//  lv5.1.4
//
//  Created by dongxicheng on 11/27/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LView.h"
#import "LVFunctionRegister.h"
#import "LVTimer.h"
#import "LVDebugCmd.h"
#import "lVtable.h"
#import "LVNativeObjBox.h"
#import "LVBlock.h"
#import "LVPkgManager.h"
#import "UIView+LuaView.h"
#import "LVDebuger.h"

NSString *const LuaViewRunCmdNotification = @"LuaViewRunCmdNotification";

@interface LView ()
@property (nonatomic,strong) id mySelf;
@property (nonatomic,assign) BOOL stateInited;
@property (nonatomic,assign) BOOL loadedDebugScript;
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
}

-(void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - run
-(int) runFile:(NSString*) fileName{
    self.runInSignModel = FALSE;
    NSData* code = [LVUtil dataReadFromFile:fileName];
    int error = [self runData:code fileName:fileName];
    return error;
}

-(int) runSignFile:(NSString*) fileName{
    self.runInSignModel = TRUE;
    NSData* code = [LVPkgManager readLuaFile:fileName];
    int error = [self runData:code fileName:fileName];
    return error;
}

-(void) checkDebugOrNot:(const char*) chars length:(NSInteger) len fileName:(NSString*) fileName {
    if( g_printToServer ){
        NSMutableData* data = [[NSMutableData alloc] init];
        [data appendBytes:chars length:len];
        
        [LVDebuger sendCmd:@"loadfile" fileName:fileName info:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
}

#ifdef DEBUG
extern char g_debug_lua[];

-(int) loadDebugModel{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(debugListener:)
                                                 name:LuaViewRunCmdNotification
                                               object:nil];
    
    NSData* data = [[NSData alloc] initWithBytes:g_debug_lua length:strlen(g_debug_lua)];
    return [self runData:data fileName:@"debug.lua"];
}

-(void) debugListener:(NSNotification*) info{
    NSString* cmd = info.object;
    [self performSelectorOnMainThread:@selector(callDebugToExecuteCmd:) withObject:cmd waitUntilDone:NO];
}

-(void) callDebugToExecuteCmd:(NSString*) cmd{
    if( [cmd isKindOfClass:[NSString class]] ) {
        [self callLua:@"debug_runing_execute" args:@[cmd]];
    }
}

-(void) checkDeuggerIsRunningToLoadDebugModel{
    static BOOL checked = NO;
    static BOOL openDebugMode = NO;
    if( checked==NO ){
        checked = YES;
        if ( [[LVDebuger sharedInstance] waitUntilConnectionEnd] >0 ) {
            openDebugMode = YES;
        }
    }
    if( openDebugMode ) {
        if( self.loadedDebugScript == NO ) {
            self.loadedDebugScript = YES;
            [LVDebuger sendCmd:@"log" info:@"connect ok\n"];
            [self loadDebugModel];// 加载调试模块
        }
    }
}
#endif

-(void) registeLibs{
    if( !self.stateInited ) {
        self.stateInited = YES;
        self.l =  lvL_newstate();//lv_open();  /* opens */
        lvopen_base(self.l);  /* opens the basic library */
        lvopen_table(self.l); /* opens the table library */
        lvopen_debug(self.l); // debug
        //lvopen_io(L);        /* opens the I/O library */
        lvopen_string(self.l); /* opens the string lib. */
        lvopen_math(self.l);   /* opens the math lib. */
        
        [LVFunctionRegister registryApi:self.l lView:self];
        self.l->lView = (__bridge void *)(self);
    }
}

-(int) runData:(NSData *)data fileName:(NSString*)fileName{
    if( fileName==nil ){
        static int i = 0;
        fileName = [NSString stringWithFormat:@"%d.lua",i];
    }
    if( data.length<=0 ){
        LVError( @"running chars == NULL");
        return -1;
    }
#ifdef DEBUG
    [self checkDeuggerIsRunningToLoadDebugModel];
    [self checkDebugOrNot:data.bytes length:data.length fileName:fileName];
#endif
    
    int error = -1;
    error = lvL_loadbuffer(self.l, data.bytes , data.length, fileName.UTF8String) ;
    if ( error ) {
        LVError( @"running function : %s", lv_tostring(self.l, -1));
    } else {
        lv_runFunction(self.l);
    }
    return error;
}

-(int) globalNumber:(const char*) globalName{
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

-(void) setContentSize:(CGSize)contentSize{
    [super setContentSize:contentSize];
    [self.callback luaviewContentSizeDidChange:self];
}

-(void) addToTopWindow{
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    if (window == nil) {
        window = [UIApplication sharedApplication].windows[0];
    }
    [window addSubview:self];
}

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
        [self callLuaWithNoArgs:@"viewWillAppear"];
    }
}

-(void) viewDidAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"viewDidAppear"];
    }
}

-(void) viewWillDisAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"viewWillDisAppear"];
    }
}

-(void) viewDidDisAppear{
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"viewDidDisAppear"];
    }
}

- (void)didMoveToSuperview{
    [super didMoveToSuperview];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"didMoveToSuperview"];
    }
}

- (void)didMoveToWindow{
    [super didMoveToWindow];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"didMoveToSuperview"];
    }
}

#pragma mark - keyboard

-(void) keyboardWillShow:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"keyboardWillShow"];
    }
}
-(void) keyboardDidShow:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"keyboardDidShow"];
    }
}
-(void) keyboardWillHide:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"keyboardWillHide"];
    }
}
-(void) keyboardDidHide:(NSNotification *)notification {
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"keyboardDidHide"];
    }
}

#pragma mark - 摇一摇相关方法
// 摇一摇开始摇动
- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self callLuaWithNoArgs:@"shakeBegan"];
        }
    }
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self callLuaWithNoArgs:@"shakeCancelled"];
        }
    }
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    if (event.subtype == UIEventSubtypeMotionShake) {
        if( self.l ) {
            lv_checkStack32(self.l);
            [self callLuaWithNoArgs:@"shakeEnded"];
        }
    }
}

#pragma mark - layout

-(void) layoutSubviews{
    [super layoutSubviews];
    
    if( self.l ) {
        lv_checkStack32(self.l);
        [self callLuaWithNoArgs:@"layoutSubviews"];
    }
}

-(void) addSubview:(UIView *)view{
    if( self.conentView ){
        [self.conentView addSubview:view];
    } else {
        [super addSubview:view];
    }
}

-(void) setFrame:(CGRect)frame{
    if( self.conentView ){
        [self.conentView setFrame:frame];
    } else {
        [super setFrame:frame];
        [self.callback luaviewFrameDidChange:self];
    }
}

-(CGRect) frame{
    if( self.conentView ){
        return self.conentView.frame;
    } else {
        return super.frame;
    }
}

-(void) setBackgroundColor:(UIColor *)backgroundColor{
    if( self.conentView ){
        [self.conentView setBackgroundColor:backgroundColor];
    } else {
        [super setBackgroundColor:backgroundColor];
    }
}

-(UIColor*) backgroundColor{
    if( self.conentView ){
        return [self.conentView backgroundColor];
    } else {
        return [super backgroundColor];
    }
}

-(void) setClipsToBounds:(BOOL)clipsToBounds{
    if( self.conentView ){
        [self.conentView setClipsToBounds:clipsToBounds];
    } else {
        [super setClipsToBounds:clipsToBounds];
    }
}

-(BOOL) clipsToBounds{
    if( self.conentView ){
        return self.conentView.clipsToBounds;
    } else {
        return [super clipsToBounds];
    }
}

-(void) setAlpha:(CGFloat)alpha{
    if( self.conentView ){
        [self.conentView setAlpha:alpha];
    } else {
        [super setAlpha:alpha];
    }
}

-(CGFloat) alpha{
    if( self.conentView ){
        return self.conentView.alpha;
    } else {
        return [super alpha];
    }
}

-(void) setCenter:(CGPoint)center{
    if( self.conentView ){
        [self.conentView setCenter:center];
    } else {
        [super setCenter:center];
    }
}

-(CGPoint) center{
    if( self.conentView ){
        return [self.conentView center];
    } else {
        return [super center];
    }
}

-(void) setHidden:(BOOL)hidden{
    if( self.conentView ){
        [self.conentView setHidden:hidden];
    } else {
        [super setHidden:hidden];
    }
}

-(BOOL) isHidden{
    if( self.conentView ){
        return [self.conentView isHidden];
    } else {
        return [super isHidden];
    }
}

-(CALayer*) layer{
    if( self.conentView ){
        return [self.conentView layer];
    } else {
        return [super layer];
    }
}

#pragma mark - call lua global function
-(void) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args{
    if( self.l ){
        lv_checkstack(self.l, 8 + (int)args.count*2);
        self.conentView = environment;
        
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
        lv_runFunctionWithArgs(self.l, (int)args.count+1, 0);
        self.conentView = nil;
    }
}

-(void) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args{
    [self callLua:functionName tag:environment environment:environment args:args];
}

-(void) callLua:(NSString*) functionName args:(NSArray*) args{
    if( self.l ){
        lv_checkstack(self.l, (int)args.count*2 + 2);
        self.conentView = nil;
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(self.l,obj);
        }
        lv_getglobal(self.l, functionName.UTF8String);// function
        lv_runFunctionWithArgs(self.l, (int)args.count, 0);
    }
}

-(LVBlock*) getLuaBlock:(NSString*) name{
    return [[LVBlock alloc] initWith:self.l globalName:name];
}

#pragma mark - registe object.method

-(void) registeObject:(id) object name:(NSString*) name sel:(SEL) sel {
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:sel weakMode:YES];
}

-(void) registeObject:(id) object name:(NSString*) name sel:(SEL) sel weakMode:(BOOL)weakMode{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:sel weakMode:weakMode];
}

-(void) registeObject:(id) object name:(NSString*) name{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:nil weakMode:YES];
}

-(void) registeObject:(id) object name:(NSString*) name weakMode:(BOOL)weakMode{
    [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:name sel:nil weakMode:weakMode];
}


- (void)setObject:(id)object forKeyedSubscript:(NSObject <NSCopying> *)key{
    if ( [key isKindOfClass:[NSString class]] ){
        [LVNativeObjBox registeObjectWithL:self.l nativeObject:object name:(NSString*)key sel:nil weakMode:YES];
    }
}

-(void) unregisteObjectWithName:(NSString*) name{
    [LVNativeObjBox unregisteObjectWithL:self.l name:name];
}


#pragma mark - package

+(void) downLoadPackage:(NSString*)packageName withInfo:(NSDictionary*)info{
    [LVPkgManager downLoadPackage:packageName withInfo:info];
}

+(BOOL) unpackageOnceWithFile:(NSString*) fileName{
    return [LVPkgManager unpackageOnceWithFile:fileName];
}


-(BOOL) argumentToBool:(int) index{
    if ( self.lv_lview && self.l ) {
        return lv_toboolean(self.l, index);
    }
    return NO;
}

-(double)  argumentToNumber:(int) index{
    if ( self.lv_lview && self.l ) {
        return lv_tonumber(self.l, index);
    }
    return 0;
}

-(id) argumentToObject:(int) index{
    if ( self.lv_lview && self.l ) {
        return lv_luaValueToNativeObject(self.l, index);
    }
    return 0;
}


static NSArray* g_boundlePaths = nil;

+(void) setBundleSearchPath:(NSArray*) path{
    g_boundlePaths = path;
}

+(NSArray*) bundleSearchPath{
    return g_boundlePaths;
}


@end

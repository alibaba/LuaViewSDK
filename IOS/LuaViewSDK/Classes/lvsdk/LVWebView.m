//
//  LVWebView.m
//  LuaViewSDK
//
//  Created by 董希成 on 16/10/10.
//  Copyright © 2016年 dongxicheng. All rights reserved.
//

#import "LVWebView.h"
#import "LVBaseView.h"

@interface LVWebView ()<UIWebViewDelegate>
@property(nonatomic,strong) UIWebView* webView;
@end

@implementation LVWebView


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        [self createWebView];
    }
    return self;
}

- (void)webViewDidStartLoad:(UIWebView *)webView{
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        lv_settop(L, 0);
        [self lv_callLuaByKey1:@STR_onPageStarted];
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView{
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        lv_settop(L, 0);
        [self lv_callLuaByKey1:@STR_onPageFinished];
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    lv_State* L = self.lv_lview.l;
    if( L && self.lv_userData ){
        lv_settop(L, 0);
        NSInteger errorCode = error.code;
        NSString* errorInfo = [NSString stringWithFormat:@"%@",error];
        NSString* url = webView.request.URL.absoluteString;
        lv_pushnumber(L, errorCode);
        lv_pushstring(L, errorInfo.UTF8String);
        lv_pushstring(L, url.UTF8String);
        [self lv_callLuaByKey1:@STR_onReceivedError key2:nil argN:3];
    }
}

// 创建webView
-(void) createWebView{
    self.webView = [[UIWebView alloc] init];
    self.webView.delegate = self;
    [self addSubview:self.webView];
}

-(BOOL) canGoBack{
    return self.webView.canGoBack;
}

-(void) goBack{
    [self.webView goBack];
}

-(BOOL) canGoForward{
    return self.webView.canGoForward;
}

-(void) goForward{
    [self.webView goForward];
}

-(void) reload{
    [self.webView reload];
}

-(void) stopLoading{
    [self.webView stopLoading];
}

-(BOOL) isLoading{
    return [self.webView isLoading];
}

-(NSString*) title{
    NSString *theTitle=[self.webView stringByEvaluatingJavaScriptFromString:@"document.title"];
    return theTitle;
}

-(void) loadUrl:(NSString*) url{
    if( url ) {
        NSURL * nsurl = [[NSURL alloc] initWithString:url];
        [self.webView loadRequest:[NSURLRequest requestWithURL:nsurl]];
    }
}

-(NSString*) url{
    return self.webView.request.URL.absoluteString;
}

-(void) setPullRefreshEnable:(BOOL) t{
}

-(BOOL) pullRefreshEnable{
    return NO;
}

-(void) layoutSubviews{
    [super layoutSubviews];
    self.webView.frame = self.bounds;
}

-(void) dealloc{
    LVUserDataInfo* userData = self.lv_userData;
    if( userData ){
        userData->object = NULL;
    }
}

static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVWebView class]] ) {
        g_class = c;
    }
}

#pragma -mark webView
static int lvNewWebView(lv_State *L) {
    if( g_class == nil ){
        g_class = [LVWebView class];
    }
    NSString* url = lv_paramString(L, 1);
    
    LVWebView* webView = [[g_class alloc] init:L];
    [webView loadUrl:url];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(webView);
        webView.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UIWebView );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:webView];
    }
    return 1; /* new userdatum is already on the stack */
}

static int canGoBack (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView canGoBack];
            lv_pushboolean(L, ret);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int goBack (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView goBack];
            return 0;
        }
    }
    return 0;
}

static int canGoForward (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView canGoForward];
            lv_pushboolean(L, ret);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int goForward (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView goForward];
            return 0;
        }
    }
    return 0;
}

static int reload (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView reload];
            return 0;
        }
    }
    return 0;
}

static int stopLoading (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView stopLoading];
            return 0;
        }
    }
    return 0;
}

static int isLoading (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView isLoading];
            lv_pushboolean(L, ret);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

static int title (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            NSString* title = [webView title];
            lv_pushstring(L, title.UTF8String);
            return 1;
        }
    }
    return 0;
}

static int loadUrl (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            if ( lv_type(L, 2)==LV_TSTRING ) {
                NSString* url = lv_paramString(L, 2);// 2
                [webView loadUrl:url];
            } else if ( lv_type(L, 2)==LV_TUSERDATA ) {
            } else {
            }
        }
    }
    return 0;
}

static int url (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            NSString* s = [webView url];
            lv_pushstring(L, s.UTF8String);
            return 1;
        }
    }
    return 0;
}

static int pullRefreshEnable (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ) {
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            if( lv_gettop(L)>=2 ){
                BOOL yes = lv_toboolean(L, 2);
                [webView setPullRefreshEnable:yes];
                return 0;
            } else {
                BOOL ret = [webView pullRefreshEnable];
                lv_pushboolean(L, ret);
                return 1;
            }
        }
    }
    return 0;
}

static int callback (lv_State *L) {
    return lv_setCallbackByKey(L, STR_CALLBACK, NO);
}


+(int) lvClassDefine:(lv_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewWebView globalName:globalName defaultName:@"WebView"];
    
    const struct lvL_reg memberFunctions [] = {
        {"canGoBack",  canGoBack},
        {"goBack",  goBack},
        
        {"canGoForward",  canGoForward},
        {"goForward",  goForward},
        
        {"reload",  reload},
        {"stopLoading",  stopLoading},
        {"isLoading",  isLoading},
        
        {"title",  title},
        {"loadUrl",  loadUrl},
        {"url",  url},
        
        {"pullRefreshEnable",pullRefreshEnable},
        
        {"callback",  callback},
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIWebView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


@end

/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVWebView.h"
#import "LVBaseView.h"
#import "NSObject+LuaView.h"

@interface LVWebView ()<UIWebViewDelegate>
@property(nonatomic,strong) UIWebView* webView;
@end

@implementation LVWebView


-(id) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        [self createWebView];
    }
    return self;
}

- (void)webViewDidStartLoad:(UIWebView *)webView{
    lua_State* L = self.lv_luaviewCore.l;
    if( L && self.lv_userData ){
        lua_settop(L, 0);
        [self lv_callLuaByKey1:@STR_onPageStarted];
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView{
    lua_State* L = self.lv_luaviewCore.l;
    if( L && self.lv_userData ){
        lua_settop(L, 0);
        [self lv_callLuaByKey1:@STR_onPageFinished];
    }
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    lua_State* L = self.lv_luaviewCore.l;
    if( L && self.lv_userData ){
        lua_settop(L, 0);
        NSInteger errorCode = error.code;
        NSString* errorInfo = [NSString stringWithFormat:@"%@",error];
        NSString* url = webView.request.URL.absoluteString;
        lua_pushnumber(L, errorCode);
        lua_pushstring(L, errorInfo.UTF8String);
        lua_pushstring(L, url.UTF8String);
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

#pragma -mark webView
static int lvNewWebView(lua_State *L) {
    Class c = [LVUtil upvalueClass:L defaultClass:[LVWebView class]];
    
    NSString* url = lv_paramString(L, 1);
    
    LVWebView* webView = [[c alloc] init:L];
    [webView loadUrl:url];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(webView);
        webView.lv_userData = userData;
        
        luaL_getmetatable(L, META_TABLE_UIWebView );
        lua_setmetatable(L, -2);
    }
    LuaViewCore* view = LV_LUASTATE_VIEW(L);
    if( view ){
        [view containerAddSubview:webView];
    }
    return 1; /* new userdatum is already on the stack */
}

static int canGoBack (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView canGoBack];
            lua_pushboolean(L, ret);
            return 1;
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int goBack (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView goBack];
            return 0;
        }
    }
    return 0;
}

static int canGoForward (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView canGoForward];
            lua_pushboolean(L, ret);
            return 1;
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int goForward (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView goForward];
            return 0;
        }
    }
    return 0;
}

static int reload (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView reload];
            return 0;
        }
    }
    return 0;
}

static int stopLoading (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            [webView stopLoading];
            return 0;
        }
    }
    return 0;
}

static int isLoading (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            BOOL ret = [webView isLoading];
            lua_pushboolean(L, ret);
            return 1;
        }
    }
    lua_pushboolean(L, 0);
    return 1;
}

static int title (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            NSString* title = [webView title];
            lua_pushstring(L, title.UTF8String);
            return 1;
        }
    }
    return 0;
}

static int loadUrl (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            if ( lua_type(L, 2)==LUA_TSTRING ) {
                NSString* url = lv_paramString(L, 2);// 2
                [webView loadUrl:url];
            } else if ( lua_type(L, 2)==LUA_TUSERDATA ) {
            } else {
            }
        }
    }
    return 0;
}

static int url (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            NSString* s = [webView url];
            lua_pushstring(L, s.UTF8String);
            return 1;
        }
    }
    return 0;
}

static int pullRefreshEnable (lua_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ) {
        LVWebView* webView = (__bridge LVWebView *)(user->object);
        if ( [webView isKindOfClass:[LVWebView class]] ) {
            if( lua_gettop(L)>=2 ){
                BOOL yes = lua_toboolean(L, 2);
                [webView setPullRefreshEnable:yes];
                return 0;
            } else {
                BOOL ret = [webView pullRefreshEnable];
                lua_pushboolean(L, ret);
                return 1;
            }
        }
    }
    return 0;
}

static int callback (lua_State *L) {
    return lv_setCallbackByKey(L, STR_CALLBACK, NO);
}


+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    [LVUtil reg:L clas:self cfunc:lvNewWebView globalName:globalName defaultName:@"WebView"];
    
    const struct luaL_Reg memberFunctions [] = {
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
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


@end

/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LViewController.h"
#import "LuaView.h"
#import "LVUtil.h"

#if DEBUG
#define LV_LOCAL_DEBUG 1
#endif

@interface LViewController ()

@property (nonatomic, strong) LuaView *lv;

@property (nonatomic, strong) NSString *packagePath, *mainScriptName;

@end

static BOOL __disableReloadKeyCommand = NO;

@implementation LViewController

+ (void)disableReloadKeyCommand:(BOOL)disable {
    __disableReloadKeyCommand = disable;
}

- (instancetype)initWithPackage:(NSString *)path mainScript:(NSString *)scriptName {
    if (self = [super init]) {
        _packagePath = path;
        _mainScriptName = scriptName;
    }
    
    return self;
}

-(void) dealloc{
    [self.lv releaseLuaView];
    self.lv = nil;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = NO;
    self.edgesForExtendedLayout = UIRectEdgeNone;
    
    [self createLuaView];
}

- (BOOL)canBecomeFirstResponder {
    return YES;
}

#pragma mark - rewarding methods

// TODO: reward memory warning

- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionBegan:motion withEvent:event];
}

- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionCancelled:motion withEvent:event];
}

- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionEnded:motion withEvent:event];
}

-(void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    
    [self.lv viewWillAppear];
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self becomeFirstResponder];

    [self.lv viewDidAppear];
}

-(void) viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self.lv viewWillDisAppear];
}

-(void) viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    [self.lv viewDidDisAppear];
}

#pragma mark - luaview life cycle

- (void)rebuildLuaView {
    [self destroyLuaView];
    [self createLuaView];
}

- (void)createLuaView {
    [self willCreateLuaView];
    
    CGRect rect = self.view.bounds;
    
    self.lv = [[LuaView alloc] initWithFrame:rect];
    self.lv.viewController = self;
    
    if( self.luaviewRegister ) {
        self.luaviewRegister(self.lv);
    }

    [self didCreateLuaView:self.lv];
    
    [self.view addSubview:self.lv];
    
    [self runLocalPackage:self.packagePath mainScript:self.mainScriptName];
}

- (void)destroyLuaView {
    [self willDestroyLuaView:self.lv];
    
    [self.lv releaseLuaView];
    self.lv = nil;
    
    [self didDestroyLuaView];
}

- (void)runLocalPackage:(NSString *)packagePath mainScript:(NSString *)scriptName {
    if (packagePath) {
        [self.lv.bundle addScriptPath:packagePath];
        [self.lv.bundle addResourcePath:packagePath];
    }
    [self.lv runFile:scriptName ?: @"main.lua"];
}

- (void)willCreateLuaView { }

- (void)didCreateLuaView:(LView *)view { }

- (void)willDestroyLuaView:(LView *)view { }

- (void)didDestroyLuaView { }

#pragma mark - debug

#if LV_LOCAL_DEBUG

/*
 * Cmd + r 没有刷新时需要做如下设置:
 * simulator -> Hardware -> Keyboard -> Connect Hardware Keyboard
 *
 * https://github.com/facebook/react-native/issues/306#issuecomment-86834162
 */
- (NSArray<UIKeyCommand *> *)keyCommands {
    if (__disableReloadKeyCommand) {
        return nil;
    }
    
    UIKeyCommand *reloadKeyCommand = [UIKeyCommand
                                      keyCommandWithInput:@"r"
                                      modifierFlags:UIKeyModifierCommand
                                      action:@selector(rebuildLuaView)];
    
    return @[reloadKeyCommand];
}

#endif // LV_LOCAL_DEBUG

@end

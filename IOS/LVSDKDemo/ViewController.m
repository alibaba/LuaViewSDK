//
//  ViewController.m
//  lv5.1.4
//
//  Created by dongxicheng on 11/5/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "ViewController.h"
#import "LVSDK.h"
#import "DemoLuaViewDelegate.h"
#import "LVRSA.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "DemoModel.h"
#import "LVErrorView.h"
#import "JHSLuaErrorView.h"
#import "JHSLuaLoadingView.h"

typedef struct __MyStruct{
    char a;
    char b;
    char c;
    char d;
    CGFloat temp;
}MyStruct;

@protocol LuaInterface <NSObject>
- (void) callWithArg1:(NSString*)name agr2:(int) arg2;
@property (nonatomic,strong) NSString* name;
@property (nonatomic,strong) NSString* test;
- (void) testPointer:(const char*)info;
-(void) testRect:(CGRect) rect;
-(void) testStruct:(MyStruct) rect;
@end


@interface ViewController ()
@property(nonatomic,strong) LView* lv;

@property (nonatomic, strong) LVLuaObjBox* luaBox;
@property (nonatomic,strong) UIView* luabg;
@end


@implementation ViewController

- (void) setLuaBox:(LVLuaObjBox *)luaBox{
    _luaBox = luaBox;
    [_luaBox setProtocols:@[self, @protocol(LuaInterface)]];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    {
//        self.automaticallyAdjustsScrollViewInsets = NO;
        CGRect r = self.view.bounds;
        r.origin.x = 0;
        r.origin.y = 0;
        self.lv = [[LView alloc] initWithFrame:r];
        self.lv.viewController = self;
        [self.view addSubview:self.lv];
        self.lv.apiDelegate = [[DemoLuaViewDelegate alloc] init];
        
        self.lv[@"ViewController"] = self;//注册外部对象.
        
        [LVErrorView setDefaultStyle:[JHSLuaErrorView class]];
        [LVLoadingView setDefaultStyle:[JHSLuaLoadingView class]];
        
        
        //[lv runFile:@"gifDemo.lua"];// 帧动画测试
        //[self.lv runFile:@"mtopDemo.lua"];// mtop测试demo
        //[lv runFile:@"attributedFontDemo.lua"];//AttributedString 富文本 测试脚本
        [self.lv runFile:@"tableViewDemo.lua"];// tableView脚本
        //[self.lv runFile:@"collectionViewDemo.lua"];// CollectionView 测试
        //[lv runFile:@"testDebug.lua"];// 调试脚本
        //[self.lv runFile:@"animationSnow.lua"];// 下雪动画
        //[self.lv runFile:@"animationLiZi.lua"];// 粒子动画
        //[self.lv runFile:@"animationFire.lua"];// 火焰动画
        //[self.lv runFile:@"animationFire2.lua"];// 火焰动画3
        //[lv runFile:@"testunit.lua"];// 单元测试 脚本
        //[lv runFile:@"shake.lua"];//摇一摇测试脚本
        //[lv runFile:@"downloader.lua"];// 下载测试脚本
        //[lv runFile:@"downloader2.lua"];// 下载测试脚本
        //[self.lv runFile:@"testluabox.lua"];// 测试luabox
        //[self.lv runFile:@"flxNodeDemo.lua"];// 测试luabox

        //[self.lv runFile:@"lft.lua"];// 量贩团页面
        
        //self.luabg = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
        //[self.lv callLua:@"testCallLua" environment:self.luabg args:@[@"",self,@""]];
        //[self.lv callLua:@"testCallLua" environment:self.luabg args:@[@"",self,@""]];
    }
    
    //摇一摇本地代码
    [UIApplication sharedApplication].applicationSupportsShakeToEdit = YES;
    [self becomeFirstResponder];
    
//    id<LuaInterface> temp = (id<LuaInterface>)self.luaBox;
//    MyStruct stru = {'a','b','c'};
//    [temp testStruct:stru];
//    [temp testRect:CGRectMake(1, 2,33, 44)];
//    [temp testPointer:"testpointer"];
//    temp.test = @"可以嘛12123123";
//    NSString* value = temp.test;
//    NSLog(@"获取值: %@", value);
//    [self testLuaObjBox];
}

-(void) testRect:(CGRect) rect{
    NSLog(@"testRect: %@", NSStringFromCGRect(rect) );
}

-(void) testStruct:(MyStruct) rect{
    NSLog(@"testMyStruct");
}

-(void) printPointer:(const char*) pointer{
    NSLog(@"pinter: %s",pointer);
}

-(void) testLuaObjBox{
    LVLuaObjBox* box0 = self.luaBox;
    id<LuaInterface> box =  (id<LuaInterface>) box0;
    NSLog(@"box.name : %@",box.name);
    box.name = @"hello";
    [box callWithArg1:@"test" agr2:2];
    [box callWithArg1:@"sdfsdfa" agr2:234];
    
    ViewController* temp = (ViewController*)box;
    [temp externalApiDemo:@"ceshi" number:789];
}

-(void) externalApiDemo{
    NSLog(@"externalApiDemo");
}

-(void) externalApiDemo:(NSString*)s number:(NSInteger) number{
    NSLog(@"externalApiDemo,%@,%ld",s,(long)number);
}

-(NSString*) string:(NSString*) s{
    return [NSString stringWithFormat:@"haha%@",s];
}

-(NSString*) string:(NSString*) s block:(LVBlock*)block{
    block.returnValueNum = 2;
    [block callWithArgs:@[@"test",@"test"]];
    
    return [NSString stringWithFormat:@"haha%@,%@,%@",s,[block returnValue:0], [block returnValue:1]];
}

- (void)motionBegan:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionBegan:motion withEvent:event];
}

// 摇一摇取消摇动
- (void)motionCancelled:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionCancelled:motion withEvent:event];
}

// 摇一摇摇动结束
- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
    [self.lv motionEnded:motion withEvent:event];
}

-(DemoModel*) testInfo2{
    NSLog(@"9879870987");
    DemoModel* model = nil;
    model = [[DemoModel alloc] init];
    model.name = @"xicheng";
    model.value = @"testtestste";
    return model;
}
-(void) testInfo{
    UIView* view = [[UIView alloc] initWithFrame:CGRectMake(100, 100, 200, 200)];
    view.clipsToBounds = YES;
    [self.view addSubview:view];
    [self.lv callLua:@"cellInit"   environment:view args:@[@"identifierTest"]  ];
    [self.lv callLua:@"cellLayout" environment:view args:@[@"identifierTest"] ];
}

-(void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.lv viewWillAppear];
}

-(void) viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// 测试代码
//[LVPkgManager clearCachesPath];
//[LView unpackageOnceWithFile:@"2015-05-07.zip" ];
//[[[LVRSA alloc] init] test];
//[LVPkgManager downLoadPackage:@"demo"
//                     withInfo:@"{ \"url\" : \"http://g.tbcdn.cn/ju/lua/1.2.3/2015-05-06.js\" , \"time\":\"1430740218339\", \"luaview\":\"1.0.0\" }"];
//    NSData* data = [LVPkgManager readLuaFile:@"testunit.lua"];
//    NSLog(@"%@",[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
//    return;
//[[[LVPkg alloc] init] unpkgFile:@"2015-05-04-19-23-06.zip" toPath:@"demo"];
//[[[LVRSA alloc] init] test];
// Do any additional setup after loading the view, typically from a nib.

//[lv registeObject:self name:@"viewController" sel:@selector(externalApiDemo)];
//[lv registeObject:self name:@"viewController" sel:@selector(externalApiDemo:number:)];

/*
 // 测试lua代码回调oc代码oc代码回调lua代码. 相互回调问题
 //[lv runFile:"callOC.lua"];//外部方法调用
 //[lv callLua:"testNativeObjectArgs" args:@[lv,self,@"我是基本类型",@(12345678)]];
 //[[lv getLuaBlock:"testNativeObjectArgs"] callWithArgs:@[lv,self,@"我是基本类型2",@(123456789)]];
 //[[lv getLuaBlock:"System.gc"] callWithArgs:nil];
 //
 //[lv runFile:@"demo2015.lua"];// 帧动画测试
 //[lv runFile:@"tableViewDemo.lua"];// 测试
 //[lv runSignFile:@"ui.lv"];// 下雪脚本
 //[lv runSignFile:@"gifDemo.lv"];// 下雪脚本//*/

@end

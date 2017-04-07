//
//  LVViewController.m
//  Pods
//
//  Created by OolongTea on 17/4/6.
//
//

#import "LVViewController.h"
#import "LView.h"
#import "LVExImage.h"
#import "LVExButton.h"
#import "LVExCollectionView.h"

@interface LVViewController ()

@end

@implementation LVViewController

- (instancetype)initWithPackage {
    NSURL *bundleUrl = [[NSBundle mainBundle] URLForResource:@"luaview" withExtension:@"bundle"];
    NSBundle *customBundle = [NSBundle bundleWithURL:bundleUrl];
    NSString *bundlePath = [customBundle bundlePath];
    if (self = [super initWithPackage:bundlePath mainScript:@"kit/main.lua"]) {
        self.args = @{@"page":@"App"};
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)willCreateLuaView {
    [super willCreateLuaView];
    
    while (self.view.subviews.count) {
        UIView* child = self.view.subviews.lastObject;
        [child removeFromSuperview];
    }
}

- (void)didCreateLuaView:(LView *)view {
    [super didCreateLuaView:view];
    
    self.lv[@"RefreshCollectionView"] = [LVExCollectionView class];
    self.lv[@"Image"] = [LVExImage class];
    self.lv[@"Button"] = [LVExButton class];
    // 注册 外部对象.
    self.lv[@"Bridge"] = self;
}

-(void)require:(NSDictionary*)args {
    NSURL *bundleUrl = [[NSBundle mainBundle] URLForResource:@"luaview" withExtension:@"bundle"];
    NSBundle *customBundle = [NSBundle bundleWithURL:bundleUrl];
    NSString *bundlePath = [customBundle bundlePath];
    LVViewController* c = [[LVViewController alloc] initWithPackage:bundlePath mainScript:@"kit/main.lua"];
    c.args = args;
    [self.navigationController pushViewController:c animated:YES];
}

@end

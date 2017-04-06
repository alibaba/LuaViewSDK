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

@interface LVViewController ()

@end

@implementation LVViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    // Do any additional setup after loading the view, typically from a nib.
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
    
    self.lv[@"Image"] = [LVExImage class];
    self.lv[@"Button"] = [LVExButton class];
    // 注册 外部对象.
    self.lv[@"Bridge"] = self;
}

-(void)require:(NSDictionary*)args {
    LVViewController* c = [[LVViewController alloc] initWithPackage:nil mainScript:@"kit/main.lua"];
    c.args = args;
    [self.navigationController pushViewController:c animated:YES];
}

@end

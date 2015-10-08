//
//  ViewController.m
//  lv5.1.4
//
//  Created by dongxicheng on 11/5/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "ViewController.h"
#import "LuaViewSDK.h"
#import "LVRSA.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "LVCustomError.h"
#import "JHSLVCustomError.h"
#import "JHSLVCustomLoading.h"
#import "JHSLVCollectionView.h"
#import "JHSLVTableView.h"
#import "JHSLVButton.h"
#import "JHSLVImage.h"
#import "JHSLuaViewController.h"


@interface ViewController ()
@property(nonatomic,strong) UITableView* tableView;
@property(nonatomic,strong) NSArray* names;
@end


@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"LuaView 测试demo";
    
    CGRect r = self.view.bounds;
    r.origin.y = 64;
    r.size.height -= 64;
    self.tableView = [[UITableView alloc] initWithFrame:r];
    [self.view addSubview:self.tableView];
    
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    
    NSArray* urlArray =  [[NSBundle mainBundle] URLsForResourcesWithExtension:@"lua" subdirectory:nil];
    NSMutableArray* nameArray = [[NSMutableArray alloc] init];
    for( int i =0; i<urlArray.count; i++ ) {
        NSURL* url = urlArray[i];
        NSString* name = [url relativeString];
        [nameArray addObject:name];
    }
    self.names = nameArray;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.names.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString* identifier = @"default.identifier";
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if( cell==nil ) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    cell.textLabel.text = self.names[indexPath.row];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 60;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString* name =  self.names[indexPath.row];
    JHSLuaViewController* c = [[JHSLuaViewController alloc] initWithSource:name];
    [self.navigationController pushViewController:c animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

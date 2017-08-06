//
//  ViewController.m
//  lv5.1.4
//
//  Created by dongxicheng on 11/5/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "ViewController.h"
#import "LVRSA.h"
#import "LVPkgManager.h"
#import "LVLuaObjBox.h"
#import "LVCustomPanel.h"
#import "JHSLVCustomError.h"
#import "JHSLVCustomLoading.h"
#import "JHSLVCollectionView.h"
#import "JHSLVButton.h"
#import "JHSLVImage.h"
#import "JHSLuaViewController.h"
#import "LVUtil.h"
#import "LVDebugConnection.h"


@interface ViewController ()
@property(nonatomic,strong) UITableView* tableView;
@property(nonatomic,strong) NSArray* names;
@end


@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"LuaView";
    
    CGRect r = self.view.bounds;
    //    r.origin.y = 64;
    //    r.size.height -= 64;
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

    [LViewController disableReloadKeyCommand:YES];

    //  [self changeAllLuaFile];// LuaView新老语法格式转换工具
    //  [LVDebugConnection openUrlServer:^(NSDictionary *args) {
    //      NSLog(@"");
    //  }];
    
#ifdef DEBUG
    // 是否开启调试服务
    [LVDebugConnection openUrlServer:^(NSDictionary *args) {
        NSLog(@"%@",args);
        NSString* url = args[@"openUrl"];
        if( url ) {
            [self openUrl:url];
        }
    }];
#endif
}

-(void) test{
        [LVPkgManager downloadPackage:@"123"
                             withInfo:@{
                    @"bsha": @"ada9b8c175a9fe2f1b90c513b4b9c3887ef5793a531b29ef9e224c37f36a73b7",
                    @"sha": @"eabf095a31290def9c732f51f4568b480cf4a316f409133432cc0c05830d8fb9",
                    @"burl": @"//gjusp.alicdn.com/midway-luaView/julua_1111_tabbar-1a679ce0-d629-11e6-9d20-0918a0280bed.bzip",
                    @"url": @"//gjusp.alicdn.com/midway-luaView/julua_1111_tabbar-1a679ce0-d629-11e6-9d20-0918a0280bed.zip"
        
                    //@"bsha": @"268acd704e0d995d5aac823fc4ee0bcdbcc5049b8278ab050da5fca8d2dc7bfd",
                    //@"sha": @"06f210f0e8ee72a880a165dc6fcae936c9b9d41f6af1d97dc7294bd197a3a4ab",
                    //@"burl": @"//gjusp.alicdn.com/midway-luaView/julua_1111_tabbar-b2667c40-d472-11e6-ab99-3b5804d6288d.bzip",
                    //@"url": @"//gjusp.alicdn.com/midway-luaView/julua_1111_tabbar-b2667c40-d472-11e6-ab99-3b5804d6288d.zip"
                }
                             callback:^(NSDictionary *info, NSString *error, LVDownloadDataType dataType) {
                                 NSLog(@"%@",info);
                             }
         ];
}

-(void) changeAllLuaFile{
    NSString *path0=@"/Users/dongxicheng/Desktop/LuaViewSDK/IOS/Demo/Demo/lua"; // 要列出来的目录
    
    NSFileManager *myFileManager=[NSFileManager defaultManager];
    
    NSDirectoryEnumerator *myDirectoryEnumerator;
    
    myDirectoryEnumerator=[myFileManager enumeratorAtPath:path0];
    
    //列举目录内容，可以遍历子目录
    
    NSLog(@"用enumeratorAtPath:显示目录%@的内容：",path0);
    NSString* path = nil;
    while( (path=[myDirectoryEnumerator nextObject])!=nil) {
        NSLog(@"%@",path);
        if( [path hasSuffix:@".lua"] ) {
            path = [NSString stringWithFormat:@"%@/%@", path0, path];
            NSData* data = [LVUtil dataReadFromFile:path];
            [LVUtil saveData:data toFile:path];
        }
    }
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
    return 64;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSString* name =  self.names[indexPath.row];
    JHSLuaViewController* c = [[JHSLuaViewController alloc] initWithPackage:nil mainScript:name];
    [self.navigationController pushViewController:c animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) openUrl:(NSString*) url{
    NSArray* arr = [url componentsSeparatedByString:@"?"];
    NSString* args = arr.lastObject;
    NSArray* argArr = [args componentsSeparatedByString:@"&"];
    NSMutableDictionary* jtArgs = [[NSMutableDictionary alloc] init];
    for( NSString* line in argArr ) {
        NSArray* arg = [line componentsSeparatedByString:@"="];
        if( arg.count==2 ) {
            jtArgs[arg.firstObject] = arg.lastObject;
        }
    }
    // 调试模式下支持运行一个目录下脚本
    NSString* path = jtArgs[@"_lv_path"];
    if( path ) {
        NSString* mainScript = jtArgs[@"_lv_main"];
        JHSLuaViewController* c = [[JHSLuaViewController alloc] initWithPackage:path mainScript:mainScript];
        [self.navigationController pushViewController:c animated:YES];
    } else {
        LVError(@"缺少调试参数!!");
    }
}


@end

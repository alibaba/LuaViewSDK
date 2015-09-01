//
//  LVPort.m
//  LVSDK
//
//  Created by dongxicheng on 1/8/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVApiDelegate.h"
#import "LVUtil.h"

@interface LVApiDelegate ()
@property(nonatomic,copy) FuncMiscDataCallback miscDataCallback;
@end

@implementation LVApiDelegate

-(NSString*) utdid{
    return @"0123456789";
}

// 模拟miscdata回调
-(void) group:(NSString*) group key:(NSString*) key callback:(FuncMiscDataCallback) callback{
    self.miscDataCallback = callback;
    [self performSelector:@selector(callMiscDatacallbackFunction) withObject:nil afterDelay:0.1];
}

-(void) callMiscDatacallbackFunction{
    if( self.miscDataCallback ){
        self.miscDataCallback(@"{ \"url\":\"http://g.tbcdn.cn/ju/lua/1.2.1/2015-05-04.js\",  \"time\":\"1430740218338\" }");
    }
}

-(void) setHeaderRefresh:(UIScrollView*) scrollView{
    
}

-(void) setFooterRefresh:(UIScrollView*) scrollView{
    
}

@end

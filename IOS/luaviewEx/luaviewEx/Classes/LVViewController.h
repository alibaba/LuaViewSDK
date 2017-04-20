//
//  LVViewController.h
//  Pods
//
//  Created by OolongTea on 17/4/6.
//
//

#import <Foundation/Foundation.h>
#import "LViewController.h"

@interface LVViewController : LViewController

@property(nonatomic,copy) NSDictionary* args;
- (instancetype)initWithPackage;

@end

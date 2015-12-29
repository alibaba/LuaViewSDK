//
//  LVLuaFunction.m
//  LVSDK
//
//  Created by dongxicheng on 4/27/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVBlock.h"
#import "LView.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVBlock ()
@property (nonatomic,weak) LView* lview;
@property (nonatomic,strong) id retainKey;
@property (nonatomic,strong) NSArray* returnValues;
@end

@implementation LVBlock

- (id) initWith:(lv_State*)L statckID:(int) idx{
    self = [super init];
    if( self ){
        self.retainKey = [[NSMutableString alloc] init];
        self.lview = (__bridge LView *)(L->lView);
        if( lv_type(L, idx)==LV_TFUNCTION ) {
            [LVUtil registryValue:L key:self.retainKey stack:idx];
        }
    }
    return self;
}

- (id) initWith:(lv_State*)L globalName:(NSString*) globalName{
    self = [super init];
    if( self ){
        self.retainKey = [[NSMutableString alloc] init];
        self.lview = (__bridge LView *)(L->lView);
        
        if ( [globalName rangeOfString:@"."].length>0 ){
            [self resetFunctionByNames:globalName];
        } else {
            lv_getglobal(L, globalName.UTF8String);
            if( lv_type(L, -1)==LV_TFUNCTION ) {
                [LVUtil registryValue:L key:self.retainKey stack:-1];
            }
        }
    }
    return self;
}

-(void) resetFunctionByNames:(NSString*) globalName{
    NSArray * names = [globalName componentsSeparatedByString:@"."];
    lv_State* L = self.lview.l;
    if( names.count>0 ){
        NSString* name0 = names.firstObject;
        lv_getglobal(L, name0.UTF8String);
        for( int i=1; i<names.count; i++ ){
            NSString* key = names[i];
            if( lv_type(L, -1) == LV_TTABLE ){
                lv_getfield(L, -1, key.UTF8String);
            } else {
                break;
            }
        }
        if( lv_type(L, -1)==LV_TFUNCTION ) {
            [LVUtil registryValue:L key:self.retainKey stack:-1];
        }
    }
}

- (void) dealloc{
    lv_State* L = self.lview.l;
    if( L ) {
        [LVUtil unregistry:L key:self.retainKey];
    }
}

- (NSString*) callWithArgs:(NSArray*) args{
    lv_State* L = self.lview.l;
    if( L ) {
        lv_checkstack(L, (int)args.count*2+2 );
        
        int oldStackNum = lv_gettop(L);
        
        for( int i=0; i<args.count; i++ ){
            id obj = args[i];
            lv_pushNativeObject(L,obj);
        }
        
        [LVUtil pushRegistryValue:L key:self.retainKey];
        
        NSString* ret = lv_runFunctionWithArgs(L, (int)args.count, self.returnValueNum);
        
        NSMutableArray* values = [[NSMutableArray alloc] init];
        int newStackNum = lv_gettop(L);
        for( int i=oldStackNum+1; i<=newStackNum; i++ ){
            id value = lv_luaValueToNativeObject(L, i);
            value = ( (value==nil) ? [NSNull null] : value );
            [values addObject:value];
        }
        if ( values.count>0 ) {
            self.returnValues = values;
        } else {
            self.returnValues = nil;
        }
        //lv_settop(L, oldStackNum);
        return ret;
    }
    return nil;
}

-(void) pushFunctionToStack{
    lv_State* L = self.lview.l;
    if( L ){
        [LVUtil pushRegistryValue:L key:self.retainKey];
    }
}

-(id) returnValue:(int)index{
    if( index>=0 && index<self.returnValues.count ){
        id value = self.returnValues[index];
        if (value != [NSNull null] ) {
            return value;
        }
    }
    return nil;
}

@end

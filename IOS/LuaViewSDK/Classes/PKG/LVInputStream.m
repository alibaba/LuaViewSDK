//
//  LVInputStream.m
//  LVSDK
//
//  Created by dongxicheng on 5/4/15.
//  Copyright (c) 2015 dongxicheng. All rights reserved.
//

#import "LVInputStream.h"

@interface LVInputStream ()

@property(nonatomic,strong) NSData* data;
@property(nonatomic,assign) NSInteger index;

@end

@implementation LVInputStream

-(id) initWithData:(NSData*) data{
    self = [super init];
    if( self ){
        self.data = data;
    }
    return self;
}

-(int) readInt{
    if( _data && (_index+4<=_data.length) ){
        const unsigned char* bs = _data.bytes;
        int d0 = bs[_index+0]&0xff;
        int d1 = bs[_index+1]&0xff;
        int d2 = bs[_index+2]&0xff;
        int d3 = bs[_index+3]&0xff;
        _index += 4;
        return (d0<<24) | (d1<<16) | (d2<<8) | (d3);
    }
    return 0;
}

-(NSString*) readUTF{
    if( _data && (_index+2<=_data.length) ){
        const unsigned char* bs = _data.bytes;
        int d0 = bs[_index+0]&0xff;
        int d1 = bs[_index+1]&0xff;
        int length = (d0<<8) | (d1);
        _index += 2;
        if( _index+length<=_data.length ) {
            NSData* data = [[NSData alloc] initWithBytes:(bs+_index) length:length];
            NSString* s = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            _index += length;
            return s;
        }
    }
    return nil;
}

-(NSData*) readData:(NSInteger) length{
    if( _data && (length>0) && (_index+length<=_data.length) ){
        const unsigned char* bs = _data.bytes;
        NSData* data = [[NSData alloc] initWithBytes:(bs+_index) length:length];
        _index += length;
        return data;
    }
    return nil;
}

@end

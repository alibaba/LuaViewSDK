
#import "LView.h"
#import "LVRSA.h"


@implementation LView

-(BOOL) checkDebugerServer{
    return self.luaviewCore.checkDebugerServer;
}

-(void) setCheckDebugerServer:(BOOL)checkDebugerServer{
    self.luaviewCore.checkDebugerServer = checkDebugerServer;
}

// 设置证书地址
- (void) setPublicKeyFilePath:(NSString*) filePath{
    [self.luaviewCore.rsa setPublicKeyFilePath:filePath];
}

-(NSString*) callLua:(NSString*) functionName tag:(id) tag environment:(UIView*)environment args:(NSArray*) args{
    return [self.luaviewCore callLua:functionName tag:tag environment:environment args:args];
}

-(NSString*) callLua:(NSString*) functionName environment:(UIView*) environment args:(NSArray*) args{
    return [self.luaviewCore callLua:functionName environment:environment args:args];
}

-(NSString*) callLua:(NSString*) functionName args:(NSArray*) args{
    return [self.luaviewCore callLua:functionName args:args];
}

-(LVBlock*) getLuaBlock:(NSString*) name{
    return [self.luaviewCore getLuaBlock:name];
}


@end


#import "LView.h"


@implementation LView


-(BOOL) changeGrammar{
    return self.luaviewCore.changeGrammar;
}

-(void) setChangeGrammar:(BOOL)changeGrammar{
    self.luaviewCore.changeGrammar = changeGrammar;
}

-(BOOL) checkDebugerServer{
    return self.luaviewCore.checkDebugerServer;
}

-(void) setCheckDebugerServer:(BOOL)checkDebugerServer{
    self.luaviewCore.checkDebugerServer = checkDebugerServer;
}

@end

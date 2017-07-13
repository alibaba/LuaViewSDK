/**
  * Created by LuaView.
  * Copyright (c) 2017, Alibaba Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */

#import "LVCamera.h"
#import "LVBaseView.h"
#import <AVFoundation/AVFoundation.h>
#import <CoreImage/CoreImage.h>

#define LVCAMERA_ONFACEDETECTED @"onFaceDetected"
#define LVCAMERA_ONPICTURETAKEN @"onPictureTaken"

@interface LVCamera()<AVCaptureVideoDataOutputSampleBufferDelegate, AVCaptureMetadataOutputObjectsDelegate>

@property (nonatomic, retain) NSConditionLock *pictureTakingLock;

//摄像头视频层
@property (nonatomic, strong) AVCaptureSession *session;
@property (nonatomic, strong) AVCaptureDeviceInput *inputDevice;
@property (nonatomic, strong) AVCaptureStillImageOutput *imageOutput;
//@property (nonatomic, strong) AVCaptureVideoDataOutput *captureOutput;
@property (nonatomic, strong) AVCaptureMetadataOutput *metaDataOutput;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *previewLayer;

#ifdef DEBUG
@property (nonatomic, strong) CAShapeLayer *debugLayer;
#endif

@end

@implementation LVCamera

#pragma mark - init and dealloc
-(instancetype) init:(lua_State*) l{
    self = [super init];
    if( self ){
        self.lv_luaviewCore = LV_LUASTATE_VIEW(l);
        self.pictureTakingLock = [[NSConditionLock alloc] init];
        [self createPreviewLayer];
        [self createDebugLayer];
    }
    return self;
}

-(void)dealloc{
    [self enableFlash:NO];
    releaseUserDataCamera(self.lv_userData);
}

-(BOOL)createPreviewLayer{
    self.session = [[AVCaptureSession alloc] init];
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    //更改这个设置的时候必须先锁定设备，修改完后再解锁，否则崩溃
    [device lockForConfiguration:nil];
    //初始化时关闭闪光灯
    [device setFlashMode:AVCaptureFlashModeOff];
    //设置持续性自动对焦
    if ([device isFocusModeSupported:AVCaptureFocusModeAutoFocus]){
        device.focusMode = AVCaptureFocusModeContinuousAutoFocus;
    }
    
    [device unlockForConfiguration];
    
    NSError *error = nil;
    
    self.inputDevice = [[AVCaptureDeviceInput alloc] initWithDevice:device error:&error];
    
    if (error){
        return NO;
    }
    
    if ([self.session canAddInput:self.inputDevice]) {
        [self.session addInput:self.inputDevice];
    }
    
    self.imageOutput = [[AVCaptureStillImageOutput alloc] init];
    
    //输出设置。AVVideoCodecJPEG   输出jpeg格式图片
    NSDictionary * outputSettings = [[NSDictionary alloc] initWithObjectsAndKeys:AVVideoCodecJPEG,AVVideoCodecKey, nil];
    [self.imageOutput setOutputSettings:outputSettings];
    
    if ([self.session canAddOutput:self.imageOutput]) {
        [self.session addOutput:self.imageOutput];
    }
    
    [self enableFaceDetect:YES];
    
    self.previewLayer = [AVCaptureVideoPreviewLayer layerWithSession:self.session];
    self.previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
    
    [self.layer addSublayer:self.previewLayer];
    
    return YES;
}

-(void)createDebugLayer{
#ifdef DEBUG
    self.debugLayer = [[CAShapeLayer alloc] init];
    self.debugLayer.strokeColor = [UIColor redColor].CGColor;
    self.debugLayer.fillColor = [UIColor clearColor].CGColor;
    [self.layer addSublayer:self.debugLayer];
#endif
}

-(void)layoutSubviews{
    [super layoutSubviews];
    
    self.previewLayer.frame = self.bounds;
}

-(void)didMoveToWindow{
    [super didMoveToWindow];
    
    if (self.window){
        [self.session startRunning];
    }
}

-(void)willMoveToWindow:(UIWindow*)window{
    [super willMoveToWindow:window];
    
    if (!window){
        [self.session stopRunning];
    }
}

#pragma mark - flash control
-(void)enableFlash:(BOOL)flash{
    AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    
    if (!device.isFlashAvailable){
        return;
    }
    
    if (device.position == AVCaptureDevicePositionBack){
        [device lockForConfiguration:nil];
        if (flash){
            device.flashMode = AVCaptureFlashModeOn;
        }else{
            device.flashMode = AVCaptureFlashModeOff;
        }
        
        [device unlockForConfiguration];
    }
}

#pragma mark - queues
-(dispatch_queue_t)sessionQueue{
    static dispatch_once_t onceToken;
    static dispatch_queue_t sessionQueue;
    dispatch_once(&onceToken, ^{
        sessionQueue = dispatch_queue_create("com.luaviewsdk.VideoCaptureSessionQueue", DISPATCH_QUEUE_SERIAL);
    });
    
    return sessionQueue;
}

#pragma mark - face detection
-(void)enableFaceDetect:(BOOL)enable{
    if (enable){
        if (self.metaDataOutput){
            [self.session removeOutput:self.metaDataOutput];
        }
        
        self.metaDataOutput = [[AVCaptureMetadataOutput alloc] init];
        [self.metaDataOutput setMetadataObjectsDelegate:self queue:[self sessionQueue]];
        
        if ([self.session canAddOutput:self.metaDataOutput]){
            [self.session addOutput:self.metaDataOutput];
        }
        
        if ([self.metaDataOutput.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeFace]){
            self.metaDataOutput.metadataObjectTypes = @[AVMetadataObjectTypeFace];
        }
        
    }else{
        if (self.metaDataOutput){
            [self.session removeOutput:self.metaDataOutput];
            self.metaDataOutput = nil;
        }
    }
}

- (void)captureOutput:(AVCaptureOutput *)output didOutputMetadataObjects:(NSArray<__kindof AVMetadataObject *> *)metadataObjects fromConnection:(AVCaptureConnection *)connection{
#ifdef DEBUG
    dispatch_async(dispatch_get_main_queue(), ^{
        CGMutablePathRef path = CGPathCreateMutable();
#endif
        for (AVMetadataObject *metadata in metadataObjects){
            if ([metadata.type isEqualToString:AVMetadataObjectTypeFace]){
                CGRect face = [self.previewLayer rectForMetadataOutputRectOfInterest:metadata.bounds];
                dispatch_async(dispatch_get_main_queue(), ^{
                    [self onFaceDetected:face];
                });
#ifdef DEBUG
                CGPathAddRect(path, NULL, face);
#endif
            }
        }
#ifdef DEBUG
        self.debugLayer.path = path;
        [self.debugLayer setNeedsDisplay];
        CGPathRelease(path);
    });
#endif
}

-(void)drawFaceBoundInDebugLayer:(CGRect)bounds{
#ifdef DEBUG
    dispatch_async(dispatch_get_main_queue(), ^{
        CGPathRef path = CGPathCreateWithRect(bounds, NULL);
        self.debugLayer.path = path;
        [self.debugLayer setNeedsDisplay];
        CGPathRelease(path);
    });
#endif
}

#pragma mark - taking picture
-(void)startTakingPicture:(BOOL)flash{
    if ([self.pictureTakingLock tryLock]){
        
        [self enableFlash:YES];
        
        AVCaptureConnection *imageConnection = [self.imageOutput        connectionWithMediaType:AVMediaTypeVideo];
        UIDeviceOrientation curDeviceOrientation = [[UIDevice currentDevice] orientation];
        AVCaptureVideoOrientation avcaptureOrientation = (AVCaptureVideoOrientation)curDeviceOrientation;
        
        [imageConnection setVideoOrientation:avcaptureOrientation];
        [imageConnection setVideoScaleAndCropFactor:1];
        
        __block typeof (self) wself = self;
        [self.imageOutput captureStillImageAsynchronouslyFromConnection:imageConnection
                                                      completionHandler:^(CMSampleBufferRef imageDataSampleBuffer, NSError *error) {
                                                          [wself enableFlash:NO];
                                                          if (!error){
                                                              NSData *jpegData = [AVCaptureStillImageOutput jpegStillImageNSDataRepresentation:imageDataSampleBuffer];
                                                              //回调图片
                                                              [wself onPictureTaken:jpegData];
                                                          }
                                                          
                                                          [wself.pictureTakingLock unlock];
        }];
    }
}

#pragma mark - 回调
-(void)onFaceDetected:(CGRect)bounds{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        //第一个参数：x
        lua_pushnumber(L, bounds.origin.x);
        //第二个参数：y
        lua_pushnumber(L, bounds.origin.y);
        //第三个参数：宽
        lua_pushnumber(L, bounds.size.width);
        //第四个参数：高
        lua_pushnumber(L, bounds.size.height);
        
        [self lv_callLuaCallback:LVCAMERA_ONFACEDETECTED key2:nil argN:4];
    }
}

-(void)onPictureTaken:(NSData*)imageData{
    lua_State* L = self.lv_luaviewCore.l;
    if( L ) {
        lua_checkstack32(L);
        if (imageData){
            //保存到本地文件再传给脚本层, 路径为temp/LVCamera
            NSString *path = NSTemporaryDirectory();
            //获取当前毫秒数
            NSString *filename = [path stringByAppendingFormat:@"%.0f.jpg", (double)CFAbsoluteTimeGetCurrent()];
            
            NSError *error = nil;
            [imageData writeToFile:filename options:NSDataWritingAtomic error:&error];
            
            UIImage *image = [UIImage imageWithData:imageData];
            
            lua_pushstring(L, filename.UTF8String);
            lua_pushnumber(L, image.size.width);
            lua_pushnumber(L, image.size.height);
        }else{
            lua_pushstring(L, "");
            lua_pushnumber(L, 0);
            lua_pushnumber(L, 0);
        }
        
        [self lv_callLuaCallback:LVCAMERA_ONPICTURETAKEN key2:nil argN:3];
    }
}

#pragma mark - LuaViewSDK register methods

static int lvNewCameraView (lua_State *L){
    Class c = [LVUtil upvalueClass:L defaultClass:[LVCamera class]];
    
    LVCamera* camera = [[c alloc] init:L];
    
    NEW_USERDATA(userData, View);
    userData->object = CFBridgingRetain(camera);
    camera.lv_userData = userData;
    
    luaL_getmetatable(L, META_TABLE_Camera);
    lua_setmetatable(L, -2);
        
    LuaViewCore* father = LV_LUASTATE_VIEW(L);
    if( father ){
        [father containerAddSubview:camera];
    }
        
    return 1; /* new userdatum is already on the stack */
}

static int lvHasPermission(lua_State *L){
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authStatus == AVAuthorizationStatusRestricted || authStatus ==AVAuthorizationStatusDenied)
    {
        //无权限返回
        lua_pushboolean(L, 0);
    } else {
        lua_pushboolean(L, 1);
    }
    
    return 1;
}

static int lvEnableFaceDetect(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCamera* camera = (__bridge LVCamera *)(user->object);
        if ( lua_gettop(L)>=2 ){
            BOOL enable = lua_toboolean(L, 2);
            [camera enableFaceDetect:enable];
            return 0;
        }
    }
    return 0;
}

static int lvTakePicture(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCamera* camera = (__bridge LVCamera *)(user->object);
        
        BOOL flash = NO;
        
        if ( lua_gettop(L)>=2 ){
            flash = lua_toboolean(L, 2);
            return 0;
        }
        
        [camera startTakingPicture:flash];
    }
    return 0;
}

static int __gc(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    releaseUserDataCamera(user);
    
    return 0;
}

static int __tostring(lua_State *L){
    LVUserDataInfo * user = (LVUserDataInfo *)lua_touserdata(L, 1);
    if( user ){
        LVCamera* camera =  (__bridge LVCamera *)(user->object);
        NSString* s = [NSString stringWithFormat:@"LVUserDataCamera: %@", camera ];
        lua_pushstring(L, s.UTF8String);
        return 1;
    }
    return 0;
}

static void releaseUserDataCamera(LVUserDataInfo* user){
    if( user && user->object ){
        LVCamera* camera = CFBridgingRelease(user->object);
        user->object = NULL;
        if( camera ){
            camera.lv_userData = NULL;
            camera.lv_luaviewCore = nil;
        }
    }
}

// lua脚本层对应的库和类名
+(int) lvClassDefine:(lua_State *)L globalName:(NSString*) globalName{
    
    lv_defineGlobalFunc("CameraViewPermission", lvHasPermission, L);
    
    [LVUtil reg:L clas:self cfunc:lvNewCameraView globalName:globalName defaultName:@"CameraView"];
    
    //方法列表
    const struct luaL_Reg memberFunctions [] = {
        {"enableFaceDetect", lvEnableFaceDetect},
        {"takePicture", lvTakePicture},
        {"__gc", __gc },
        {"__tostring", __tostring },
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L,META_TABLE_Camera);
    
    luaL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    luaL_openlib(L, NULL, memberFunctions, 0);
    
    return 1;
}

@end

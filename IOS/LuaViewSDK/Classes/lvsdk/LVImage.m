//
//  LVImageView.m
//  lv5.1.4
//
//  Created by dongxicheng on 12/19/14.
//  Copyright (c) 2014 dongxicheng. All rights reserved.
//

#import "LVImage.h"
#import "LVBaseView.h"
#import "LVUtil.h"
#import "LVData.h"
//#import <TBCDNImage.h>
#import <Accelerate/Accelerate.h>
#import "LVNinePatchImage.h"
#import "lV.h"
#import "lVauxlib.h"
#import "lVlib.h"
#import "lVstate.h"
#import "lVgc.h"

@interface LVImage ()
@property (nonatomic,strong) id functionTag;
@property (nonatomic,strong) UIImageView* blurImageView;
@property (nonatomic,strong) UIVisualEffectView *blurEffectView;
@property (nonatomic,assign) BOOL needCallLuaFunc;
@property (nonatomic,strong) UITapGestureRecognizer* tapGesture;
@property (nonatomic,strong) id errorInfo;
@property(nonatomic,assign) BOOL lv_isCallbackAddClickGesture;// 支持Callback 点击事件
@end

@implementation LVImage


-(id) init:(lv_State*) l{
    self = [super init];
    if( self ){
        self.lv_lview = (__bridge LView *)(l->lView);
        self.contentMode = UIViewContentModeScaleAspectFill;
        self.functionTag = [[NSMutableString alloc] init];
        self.backgroundColor = [UIColor clearColor];
        self.clipsToBounds = YES;
        self.lv_isCallbackAddClickGesture = YES;
    }
    return self;
}

-(void) setWebImageUrl:(NSURL*) url finished:(LVLoadFinished) finished{
}

-(void) callLuaDelegate:(id) obj{
    lv_State* L = self.lv_lview.l;
    if( L ) {
        lv_checkstack(L, 4);
        lv_pushboolean(L, obj?0:1);
        [LVUtil pushRegistryValue:L key:self.functionTag];
        lv_runFunctionWithArgs(L, 1, 0);
    }
    [LVUtil unregistry:L key:self.functionTag];
}

-(void) setImageByName:(NSString*) imageName{
    if( imageName==nil )
        return;
    
    if( [LVUtil isExternalUrl:imageName] ){
        //CDN image
        __weak LVImage* weakImageView = self;
        [self setWebImageUrl:[NSURL URLWithString:imageName] finished:^(id errorInfo){
            if( weakImageView.needCallLuaFunc ) {
                weakImageView.errorInfo = errorInfo;
                [weakImageView performSelectorOnMainThread:@selector(callLuaDelegate:) withObject:errorInfo waitUntilDone:NO];
            }
        }];
    } else {
        // local Image
        UIImage* image = [self.lv_lview.bundle imageWithName:imageName];
        if ( [LVNinePatchImage isNinePathImageName:imageName] ) {
            image = [LVNinePatchImage createNinePatchImage:image];
            [self setImage:image];
        } else {
            [self setImage:image];
        }
    }
}

-(void) setImageByData:(NSData*) data{
    if ( data ) {
        UIImage* image = [[UIImage alloc] initWithData:data];
        [self setImage:image];
    }
}

-(void) canelWebImageLoading{
    // [self cancelCurrentImageLoad]; // 取消上一次CDN加载
}
-(void) cancelImageLoadAndClearCallback:(lv_State*)L{
    [self canelWebImageLoading];
    [NSObject cancelPreviousPerformRequestsWithTarget:self]; // 取消回调脚本
    [LVUtil unregistry:L key:self.functionTag]; // 清除脚本回调
}

-(void) dealloc{
    LVUserDataInfo* userData = self.lv_userData;
    if( userData ){
        userData->object = NULL;
    }
}

+ (UIImage *)applyLightEffect:(UIImage*)image
{
    UIColor *tintColor = [UIColor colorWithWhite:1.0 alpha:0.3];
    return [LVImage applyBlurWithImage:image radius:30 tintColor:tintColor saturationDeltaFactor:1.8 maskImage:nil];
}

+(UIImage *)applyExtraLightEffect:(UIImage*)image
{
    UIColor *tintColor = [UIColor colorWithWhite:0.97 alpha:0.87];
    return [LVImage applyBlurWithImage:image radius:20 tintColor:tintColor saturationDeltaFactor:1.8 maskImage:nil];
}

+ (UIImage *)applyDarkEffect:(UIImage*)image
{
    UIColor *tintColor = [UIColor colorWithWhite:0.11 alpha:0.73];
    return [LVImage applyBlurWithImage:image radius:20 tintColor:tintColor saturationDeltaFactor:1.8 maskImage:nil];
}

-(UIImage*) viewShot:(UIView*) view  rect:(CGRect) rect{
    UIGraphicsBeginImageContextWithOptions(view.bounds.size, YES, 1);
    [view.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    CGImageRef imageRef = CGImageCreateWithImageInRect(img.CGImage, rect);
    UIImage *resultImg = [UIImage imageWithCGImage:imageRef];
    CGImageRelease(imageRef);
    return resultImg;
}

- (void)renderLayerWithView:(LVImage*)view style:(UIBlurEffectStyle)style
{
    [self renderLayerWithView:view style:style userSystemBlur:NO];
}

- (void)renderLayerWithView:(UIView*)view style:(UIBlurEffectStyle)style userSystemBlur:(BOOL) userSystemBlur
{
    if( userSystemBlur && [LVUtil ios8] ) {
        if( self.blurEffectView==nil )  {
            [self.blurEffectView removeFromSuperview];
            UIBlurEffect *blurEffect = [UIBlurEffect effectWithStyle:style];
            UIVisualEffectView *blurEffectView = [[UIVisualEffectView alloc] initWithEffect:blurEffect];
            blurEffectView.frame = self.bounds;
            self.blurEffectView = blurEffectView;
            [self addSubview:self.blurEffectView];
        }
    } else
    {
        if( self.blurImageView==nil ){
            self.blurImageView = [[UIImageView alloc] initWithFrame:self.bounds];
            self.blurImageView.clipsToBounds = YES;
            [self addSubview:self.blurImageView];
        }
        CGRect rect = [self convertRect:self.bounds toView:view];
        __block UIImage *image = [self viewShot:view rect:rect];
        
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            switch (style) {
                case UIBlurEffectStyleExtraLight:
                    image = [LVImage applyExtraLightEffect:image];
                    break;
                case UIBlurEffectStyleLight:
                    image = [LVImage applyLightEffect:image];
                    break;
                default:
                    image = [LVImage applyDarkEffect:image];
                    break;
            }
            
            dispatch_sync(dispatch_get_main_queue(), ^{
                self.blurImageView.image = image;
            });
        });
    }
}
static Class g_class = nil;

+ (void) setDefaultStyle:(Class) c{
    if( [c isSubclassOfClass:[LVImage class]] ) {
        g_class = c;
    }
}

#pragma -mark ImageView
static int lvNewImageView(lv_State *L) {
    if( g_class == nil ){
        g_class = [LVImage class];
    }
    NSString* imageName = lv_paramString(L, 1);
    
    LVImage* imageView = [[g_class alloc] init:L];
    [imageView setImageByName:imageName];
    {
        NEW_USERDATA(userData, View);
        userData->object = CFBridgingRetain(imageView);
        imageView.lv_userData = userData;
        
        lvL_getmetatable(L, META_TABLE_UIImageView );
        lv_setmetatable(L, -2);
    }
    LView* view = (__bridge LView *)(L->lView);
    if( view ){
        [view containerAddSubview:imageView];
    }
    return 1; /* new userdatum is already on the stack */
}

static int setImage (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView cancelImageLoadAndClearCallback:L];
            if( lv_type(L, 3) == LV_TFUNCTION ) {
                [LVUtil registryValue:L key:imageView.functionTag stack:3];
                imageView.needCallLuaFunc = YES;
            } else {
                imageView.needCallLuaFunc = NO;
            }
            if ( lv_type(L, 2)==LV_TSTRING ) {
                NSString* imageName = lv_paramString(L, 2);// 2
                if( imageName ){
                    [imageView setImageByName:imageName];
                    lv_pushvalue(L,1);
                    return 1;
                }
            } else if ( lv_type(L, 2)==LV_TUSERDATA ) {
                LVUserDataInfo * userdata = (LVUserDataInfo *)lv_touserdata(L, 2);
                LVData* lvdata = (__bridge LVData *)(userdata->object);
                if( LVIsType(userdata, Data) ) {
                    [imageView setImageByData:lvdata.data];
                    lv_pushvalue(L,1);
                    return 1;
                }
            } else {
                // 清理图片
                imageView.image = nil;
            }
        }
    }
    return 0;
}

//static int resizeImage (lv_State *L) {
//    LVUserDataView * user = (LVUserDataView *)lv_touserdata(L, 1);
//    if( user ){
//        LVImage* imageView = (__bridge LVImage *)(user->view);
//        if ( [imageView isKindOfClass:[LVImage class]] ) {
//            if( lv_gettop(L)>=5 ) {
//                float top = lv_tonumber(L, 2);
//                float left = lv_tonumber(L, 3);
//                float bottom = lv_tonumber(L, 4);
//                float right = lv_tonumber(L, 5);
//                UIImage* image = imageView.image;
//                UIEdgeInsets edgeInset = UIEdgeInsetsMake(top, left, bottom, right);
//                if ( lv_gettop(L)>=6 ) {
//                    image = [image resizableImageWithCapInsets:edgeInset];
//                    imageView.image = image;
//                } else {
//                    float model = lv_tonumber(L, 6);
//                    image = [image resizableImageWithCapInsets:edgeInset resizingMode:model];
//                    imageView.image = image;
//                }
//            }
//        }
//    }
//    return 0;
//}


static int scaleType (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            if( lv_gettop(L)>=2 ) {
                int model = lv_tonumber(L, 2);// 2
                [imageView setContentMode:model];
                return 0;
            } else {
                UIViewContentMode model = imageView.contentMode;
                lv_pushnumber(L, model);
                return 1;
            }
        }
    }
    return 0;
}


//static int render (lv_State *L) {
//    LVUserDataView * user1 = (LVUserDataView *)lv_touserdata(L, 1);
//    LVUserDataView * user2 = (LVUserDataView *)lv_touserdata(L, 2);
//    if( user1 && user2 && LVIsType(user1, LVUserDataView) && LVIsType(user2, LVUserDataView)){
//        LVImage* imageView = (__bridge LVImage *)(user1->view);
//        UIView* view = (__bridge UIView *)(user2->view);
//        if ( [imageView isKindOfClass:[LVImage class]]
//            && [view isKindOfClass:[UIView class]] ) {
//            int color = 0;
//            if( lv_gettop(L)>=3 ) {
//                color = lv_tonumber(L, 3);
//            }
//            [imageView renderLayerWithView:view style:color userSystemBlur:NO];
//        }
//    }
//    return 0;
//}
//
//static int renderSystemApi (lv_State *L) {
//    LVUserDataView * user1 = (LVUserDataView *)lv_touserdata(L, 1);
//    LVUserDataView * user2 = (LVUserDataView *)lv_touserdata(L, 2);
//    if( user1 && user2 && LVIsType(user1, LVUserDataView) && LVIsType(user2, LVUserDataView)){
//        LVImage* imageView = (__bridge LVImage *)(user1->view);
//        LVImage* view = (__bridge LVImage *)(user2->view);
//        if ( [imageView isKindOfClass:[LVImage class]]
//            && [view isKindOfClass:[UIView class]] ) {
//            int color = 0;
//            if( lv_gettop(L)>=3 ) {
//                color = lv_tonumber(L, 3);
//            }
//            [imageView renderLayerWithView:view style:color userSystemBlur:YES];
//        }
//    }
//    return 0;
//}

static int startAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( L && user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            NSArray* urlArray = lv_luaTableToArray(L,2);
            float repeatCount = 1;
            float duration = 0.3;
            if( lv_gettop(L)>=3 ){
                duration = lv_tonumber(L, 3);
            }
            if( lv_gettop(L)>=4 ){
                repeatCount = lv_tonumber(L, 4);
            }
            LView* lview = (__bridge LView *)(L->lView);
            LVBundle* bundle = lview.bundle;
            NSMutableArray  *arrayM=[NSMutableArray array];
            for (NSString* url in urlArray) {
                UIImage* image = [bundle imageWithName:url];
                if( image ) {
                    [arrayM addObject:image];
                }
            }
            [imageView setAnimationImages:arrayM];//设置动画数组
            [imageView setAnimationDuration:duration];//设置动画播放时间
            [imageView setAnimationRepeatCount:repeatCount];//设置动画播放次数
            [imageView startAnimating];//开始动画
        }
    }
    return 0;
}

static int stopAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            [imageView stopAnimating];
            return 0;
        }
    }
    return 0;
}

static int isAnimating (lv_State *L) {
    LVUserDataInfo * user = (LVUserDataInfo *)lv_touserdata(L, 1);
    if( user ){
        LVImage* imageView = (__bridge LVImage *)(user->object);
        if ( [imageView isKindOfClass:[LVImage class]] ) {
            lv_pushboolean(L, imageView.isAnimating?1:0);
            return 1;
        }
    }
    lv_pushboolean(L, 0);
    return 1;
}

+(int) classDefine:(lv_State *) L {
    {
        lv_pushcfunction(L, lvNewImageView);
        lv_setglobal(L, "Image");
    }
    const struct lvL_reg memberFunctions [] = {
        {"image",  setImage},
        {"scaleType",  scaleType},
        
        {"startAnimationImages",  startAnimating},
        {"stopAnimationImages",  stopAnimating},
        {"isAnimationImages",  isAnimating},
        
//        {"render",  render},
//        {"renderSystemApi",  renderSystemApi},
//
//        {"resizeImage",  resizeImage},
        
        {NULL, NULL}
    };
    
    lv_createClassMetaTable(L, META_TABLE_UIImageView);
    
    lvL_openlib(L, NULL, [LVBaseView baseMemberFunctions], 0);
    lvL_openlib(L, NULL, memberFunctions, 0);
    
    const char* keys[] = { "addView", NULL};// 移除多余API
    lv_luaTableRemoveKeys(L, keys );
    return 1;
}


//----------------------------------------------------------------------------------------


+(UIImage *)applyBlurWithImage:(UIImage*)image radius:(CGFloat)blurRadius tintColor:(UIColor *)tintColor saturationDeltaFactor:(CGFloat)saturationDeltaFactor maskImage:(UIImage *)maskImage
{
    // check pre-conditions
    if (image.size.width < 1 || image.size.height < 1) {
        LVError(@" *** error: invalid size: (%.2f x %.2f). Both dimensions must be >= 1: %@", image.size.width, image.size.height, image);
        return nil;
    }
    if (!image.CGImage) {
        LVError(@" *** error: image must be backed by a CGImage: %@", image);
        return nil;
    }
    if (maskImage && !maskImage.CGImage) {
        LVError(@" *** error: maskImage must be backed by a CGImage: %@", maskImage);
        return nil;
    }
    
    CGRect imageRect = { CGPointZero, image.size };
    UIImage *effectImage = image;
    
    BOOL hasBlur = blurRadius > __FLT_EPSILON__;
    BOOL hasSaturationChange = fabs(saturationDeltaFactor - 1.) > __FLT_EPSILON__;
    if (hasBlur || hasSaturationChange) {
        UIGraphicsBeginImageContextWithOptions(image.size, NO, [[UIScreen mainScreen] scale]);
        CGContextRef effectInContext = UIGraphicsGetCurrentContext();
        CGContextScaleCTM(effectInContext, 1.0, -1.0);
        CGContextTranslateCTM(effectInContext, 0, -image.size.height);
        CGContextDrawImage(effectInContext, imageRect, image.CGImage);
        
        vImage_Buffer effectInBuffer;
        effectInBuffer.data     = CGBitmapContextGetData(effectInContext);
        effectInBuffer.width    = CGBitmapContextGetWidth(effectInContext);
        effectInBuffer.height   = CGBitmapContextGetHeight(effectInContext);
        effectInBuffer.rowBytes = CGBitmapContextGetBytesPerRow(effectInContext);
        
        UIGraphicsBeginImageContextWithOptions(image.size, NO, [[UIScreen mainScreen] scale]);
        CGContextRef effectOutContext = UIGraphicsGetCurrentContext();
        vImage_Buffer effectOutBuffer;
        effectOutBuffer.data     = CGBitmapContextGetData(effectOutContext);
        effectOutBuffer.width    = CGBitmapContextGetWidth(effectOutContext);
        effectOutBuffer.height   = CGBitmapContextGetHeight(effectOutContext);
        effectOutBuffer.rowBytes = CGBitmapContextGetBytesPerRow(effectOutContext);
        
        if (hasBlur) {
            // A description of how to compute the box kernel width from the Gaussian
            // radius (aka standard deviation) appears in the SVG spec:
            // http://www.w3.org/TR/SVG/filters.html#feGaussianBlurElement
            //
            // For larger values of 's' (s >= 2.0), an approximation can be used: Three
            // successive box-blurs build a piece-wise quadratic convolution kernel, which
            // approximates the Gaussian kernel to within roughly 3%.
            //
            // let d = floor(s * 3*sqrt(2*pi)/4 + 0.5)
            //
            // ... if d is odd, use three box-blurs of size 'd', centered on the output pixel.
            //
            CGFloat inputRadius = blurRadius * [[UIScreen mainScreen] scale];
            uint32_t radius = floor(inputRadius * 3. * sqrt(2 * M_PI) / 4 + 0.5);
            if (radius % 2 != 1) {
                radius += 1; // force radius to be odd so that the three box-blur methodology works.
            }
            vImageBoxConvolve_ARGB8888(&effectInBuffer, &effectOutBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
            vImageBoxConvolve_ARGB8888(&effectOutBuffer, &effectInBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
            vImageBoxConvolve_ARGB8888(&effectInBuffer, &effectOutBuffer, NULL, 0, 0, radius, radius, 0, kvImageEdgeExtend);
        }
        BOOL effectImageBuffersAreSwapped = NO;
        if (hasSaturationChange) {
            CGFloat s = saturationDeltaFactor;
            CGFloat floatingPointSaturationMatrix[] = {
                0.0722 + 0.9278 * s,  0.0722 - 0.0722 * s,  0.0722 - 0.0722 * s,  0,
                0.7152 - 0.7152 * s,  0.7152 + 0.2848 * s,  0.7152 - 0.7152 * s,  0,
                0.2126 - 0.2126 * s,  0.2126 - 0.2126 * s,  0.2126 + 0.7873 * s,  0,
                0,                    0,                    0,  1,
            };
            const int32_t divisor = 256;
            NSUInteger matrixSize = sizeof(floatingPointSaturationMatrix)/sizeof(floatingPointSaturationMatrix[0]);
            int16_t saturationMatrix[matrixSize];
            for (NSUInteger i = 0; i < matrixSize; ++i) {
                saturationMatrix[i] = (int16_t)roundf(floatingPointSaturationMatrix[i] * divisor);
            }
            if (hasBlur) {
                vImageMatrixMultiply_ARGB8888(&effectOutBuffer, &effectInBuffer, saturationMatrix, divisor, NULL, NULL, kvImageNoFlags);
                effectImageBuffersAreSwapped = YES;
            }
            else {
                vImageMatrixMultiply_ARGB8888(&effectInBuffer, &effectOutBuffer, saturationMatrix, divisor, NULL, NULL, kvImageNoFlags);
            }
        }
        if (!effectImageBuffersAreSwapped)
            effectImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        if (effectImageBuffersAreSwapped)
            effectImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
    }
    
    // set up output context
    UIGraphicsBeginImageContextWithOptions(image.size, NO, [[UIScreen mainScreen] scale]);
    CGContextRef outputContext = UIGraphicsGetCurrentContext();
    CGContextScaleCTM(outputContext, 1.0, -1.0);
    CGContextTranslateCTM(outputContext, 0, -image.size.height);
    
    // draw base image
    CGContextDrawImage(outputContext, imageRect, image.CGImage);
    
    // draw effect image
    if (hasBlur) {
        CGContextSaveGState(outputContext);
        if (maskImage) {
            CGContextClipToMask(outputContext, imageRect, maskImage.CGImage);
        }
        CGContextDrawImage(outputContext, imageRect, effectImage.CGImage);
        CGContextRestoreGState(outputContext);
    }
    
    // add in color tint
    if (tintColor) {
        CGContextSaveGState(outputContext);
        CGContextSetFillColorWithColor(outputContext, tintColor.CGColor);
        CGContextFillRect(outputContext, imageRect);
        CGContextRestoreGState(outputContext);
    }
    
    // output image is ready
    UIImage *outputImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return outputImage;
}

-(NSString*) description{
    return [NSString stringWithFormat:@"<Image(0x%x) frame = %@>", (int)[self hash], NSStringFromCGRect(self.frame) ];
}

@end

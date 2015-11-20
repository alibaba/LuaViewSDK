

#import "LVNinePatchImage.h"
#import "LVUtil.h"

#define BYTES_PER_PIXEL 4

@implementation LVNinePatchImage

+ (unsigned char*)getRGBAsFromImage:(UIImage*)image count:(NSInteger)count {
    CGImageRef imageRef = [image CGImage];
    NSUInteger width = CGImageGetWidth(imageRef);
    NSUInteger height = CGImageGetHeight(imageRef);
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    
    unsigned char* rawData = (unsigned char*)calloc(height * width * 4, sizeof(unsigned char));
    
    NSUInteger bytesPerRow = BYTES_PER_PIXEL * width;
    NSUInteger bitsPerComponent = 8;
    CGContextRef context = CGBitmapContextCreate(rawData, width, height,
                                                 bitsPerComponent, bytesPerRow, colorSpace,
                                                 kCGImageAlphaPremultipliedLast | kCGBitmapByteOrder32Big);
    CGColorSpaceRelease(colorSpace);

    CGContextDrawImage(context, CGRectMake(0, 0, width, height), imageRef);
    CGContextRelease(context);
    return rawData;
}

inline static CGFloat getImageAphaAtPoint(const unsigned char* rawData, NSInteger width, NSInteger height , NSInteger pointX, NSInteger pointY){
    NSInteger bytesPerRow = BYTES_PER_PIXEL * width;
    NSInteger byteIndex = (bytesPerRow * pointY) + pointX * BYTES_PER_PIXEL ;
    CGFloat alpha = (rawData[byteIndex + 3] * 1.0) / 255.0;
    return alpha;
}

+ (BOOL) isNinePathImageName:(NSString*) name{
    if ( [name hasSuffix:@".9"] || [name hasSuffix:@".9.png"] || [name hasSuffix:@".9@2x.png"] ) {
        return YES;
    }
    return NO;
}

+ (UIImage*)createNinePatchImageNamed:(NSString*)name {
    if ( [self isNinePathImageName:name] ) {
        UIImage* oriImage = [UIImage imageNamed:name];
        if ( oriImage==nil ) {
            LVLog(@"createNinePatchImageNamed: The input image is nil");
            return nil;
        }
        return [self createResizableImageFromNinePatchImage:oriImage];
    }
    LVLog(@"createNinePatchImageNamed: Image name is not ended with .9");
    return nil;
}

+ (UIImage*)createNinePatchImage:(UIImage*)image
{
    return [self createResizableImageFromNinePatchImage:image];
}

+ (UIImage*)crop:(CGRect)r image0:(UIImage*)image0
{
    r = CGRectMake(r.origin.x * image0.scale,
                      r.origin.y * image0.scale,
                      r.size.width * image0.scale,
                      r.size.height * image0.scale);
    
    CGImageRef imageRef = CGImageCreateWithImageInRect([image0 CGImage], r);
    UIImage* ret = [UIImage imageWithCGImage:imageRef
                                          scale:image0.scale
                                    orientation:image0.imageOrientation];
    CGImageRelease(imageRef);
    return ret;
}

+ (UIImage*)createResizableImageFromNinePatchImage:(UIImage*)ninePatchImage
{
    CGImageRef imageRef = [ninePatchImage CGImage];
    NSInteger width = CGImageGetWidth(imageRef);
    NSInteger height = CGImageGetHeight(imageRef);
    
    
    NSMutableArray* topBarRgba = [NSMutableArray arrayWithCapacity:0];
    NSMutableArray* leftBarRgba = [NSMutableArray arrayWithCapacity:0];
    {
        const unsigned char* rawData = [self getRGBAsFromImage:ninePatchImage count:width*height ];
        for( int x=1; x<width-1; x++ ){
            CGFloat alpha = getImageAphaAtPoint(rawData, width, height, x, 0);
            [topBarRgba addObject:@(alpha)];
        }
        for (int y = 0; y < height; y++ ) {
            CGFloat alpha = getImageAphaAtPoint(rawData, width, height, 0, y);
            [leftBarRgba addObject:@(alpha)];
        }
        free( (void*)rawData );
    }

    float top = -1, left = -1, bottom = -1, right = -1;
    int count = (int)topBarRgba.count;
    for (int i = 0; i <= count - 1; i++) {
        NSNumber* alpha = topBarRgba[i];
        if ( alpha.floatValue == 1) {
            left = i;
            break;
        }
    }
    // LVLog(@"The .9 PNG format error!!!.   left==-1");
    for (int i = count - 1; i >= 0; i--) {
        NSNumber* alpha = topBarRgba[i];
        if ( alpha.floatValue == 1) {
            right = i;
            break;
        }
    }
    // LVLog(@"The .9 PNG format error!!!.   right==-1");
    for (int i = left + 1; i <= right - 1; i++) {
        NSNumber* alpha = topBarRgba[i];
        if ( alpha.floatValue < 1) {
            LVLog(@"The 9-patch PNG format is not support. 1");
        }
    }
    count = (int) leftBarRgba.count;
    for (int i = 0; i <= count - 1; i++) {
        NSNumber* alpha = leftBarRgba[i];
        if ( alpha.floatValue == 1) {
            top = i;
            break;
        }
    }
    //LVLog(@"The .9 PNG format error!!!.   top==-1");
    for (int i = count - 1; i >= 0; i--) {
        NSNumber* alpha = leftBarRgba[i];
        if ( alpha.floatValue == 1) {
            bottom = i;
            break;
        }
    }
    //LVLog(@"The .9 PNG format error!!!.   bottom==-1");
    for (int i = top + 1; i <= bottom - 1; i++) {
        NSNumber* alpha = leftBarRgba[i];
        if ( alpha.floatValue == 0) {
            LVLog(@"The 9-patch PNG format is not support.2");
        }
    }
    if ( top>=0 && left>=0 && bottom>=0 && right>=0 ) {
        UIImage* cropImg = [self crop:CGRectMake(1, 1, ninePatchImage.size.width - 2, ninePatchImage.size.height - 2) image0:ninePatchImage];
        float scale0 = cropImg.scale;
        float scale2 = [UIScreen mainScreen].scale;
        if ( scale2>scale0 ) {
            cropImg = [[UIImage alloc] initWithCGImage:cropImg.CGImage
                                                 scale:scale2
                                             orientation:UIImageOrientationUp];
            float big = scale0/scale2;
            UIImage* retImg = [cropImg resizableImageWithCapInsets:UIEdgeInsetsMake(top*big, left*big, bottom*big, right*big)];
            return retImg;
        } else {
            UIImage* retImg = [cropImg resizableImageWithCapInsets:UIEdgeInsetsMake(top, left, bottom, right) ];
            return retImg;
        }
    }
    return ninePatchImage;
}

@end

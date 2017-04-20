

#import <UIKit/UIKit.h>

@interface LVNinePatchImage : NSObject

+ (UIImage*) createNinePatchImageNamed:(NSString*)name;
+ (UIImage*) createNinePatchImage:(UIImage*)image;
+ (BOOL) isNinePathImageName:(NSString*) name;

@end

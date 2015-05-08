//
//  ViewController.h
//  t-boardpro
//
//  Created by GLB-254 on 5/4/15.
//  Copyright (c) 2015 SocioBoard. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FHSTwitterEngine.h"
#import <sqlite3.h>
#import "TwitterHelperClass.h"
@interface ViewController : UIViewController<FHSTwitterEngineAccessTokenDelegate,UIActionSheetDelegate>
{
    UIImageView * coverBackgroundView;
    NSString * loadAccesstoken;
    sqlite3 *_databaseHandle;
    TwitterHelperClass * twitHelperObj;
    UIActionSheet * loginOptions;
    UIImageView * themeImage;
    UIView * viewSchedule;
    NSUserDefaults * userDefault;
}
@end

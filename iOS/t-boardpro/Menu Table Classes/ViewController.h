//
//  ViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FHSTwitterEngine.h"
#import <sqlite3.h>
#import "MBProgressHUD.h"
#import "TwitterHelperClass.h"
@interface ViewController : UIViewController<FHSTwitterEngineAccessTokenDelegate,UIActionSheetDelegate,UIScrollViewDelegate>
{
    UIImageView * coverBackgroundView;
    NSString * loadAccesstoken;
    sqlite3 *_databaseHandle;
    TwitterHelperClass * twitHelperObj;
    UIActionSheet * loginOptions;
    UIImageView * themeImage;
    NSUserDefaults * userDefault;
    UIScrollView * scrollViewPaging;
    UIPageControl * pageController;
    MBProgressHUD * HUD;
}
@end


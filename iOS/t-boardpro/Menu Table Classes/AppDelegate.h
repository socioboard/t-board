//
//  AppDelegate.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FHSTwitterEngine.h"
#import <sqlite3.h>
@interface AppDelegate : UIResponder <UIApplicationDelegate>
{
    UINavigationController *navController_;
    sqlite3 *_databaseHandle;
    NSDictionary* localNotifyDict;
}

+(void)setToLabel:(UILabel*)lbl Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size Color:(UIColor*)color;
+(void)setButton:(UIButton*)btn Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size TitleColor:(UIColor*)t_color ShadowColor:(UIColor*)s_color;
@property (strong, nonatomic) UIWindow *window;


@end


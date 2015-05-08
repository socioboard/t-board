//
//  AppDelegate.h
//  t-boardpro
//
//  Created by GLB-254 on 5/4/15.
//  Copyright (c) 2015 SocioBoard. All rights reserved.
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

@property (strong, nonatomic) UIWindow *window;


@end


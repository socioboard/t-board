//
//  AppDelegate.m
//  t-boardpro
//
//  Created by GLB-254 on 5/4/15.
//  Copyright (c) 2015 SocioBoard. All rights reserved.
//

#import "AppDelegate.h"
#import "FHSTwitterEngine.h"
#import "TwitterHelperClass.h"
#import <sqlite3.h>
#import "Singleton.h"
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0)
    {
        [application registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
        [application registerForRemoteNotifications];
    }
    else
    {
        [application registerForRemoteNotificationTypes:
         (UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert)];
    }
    
    [application setMinimumBackgroundFetchInterval:UIApplicationBackgroundFetchIntervalMinimum];
    //Handle Local Notification
    UILocalNotification *localNotif = [launchOptions objectForKey:UIApplicationLaunchOptionsLocalNotificationKey];
    if (localNotif)
    {
        localNotifyDict=localNotif.userInfo;
        [Singleton sharedSingleton].localNotificatonDict=localNotifyDict;
        
    }
    else
    {
        
    }
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *strCheckFirstRun = [userDefault objectForKey:@"firstrun"];
    NSLog(@"CheckFirstRun==%@",[userDefault objectForKey:@"firstrun"]);
    //[self saveinSqlite];
    if (!strCheckFirstRun)
    {
        [userDefault setObject:@"1" forKey:@"firstrun"];
        [userDefault setObject:@"NoUser" forKey:@"MainUserLogin"];
        //Methods To Create Table in Sqlite.
        [self saveinSqlite];
        [self createScheduleSchema];
    }
    
    
    return YES;
}
-(void)cancelSchedule
{
    [[[[[UIApplication sharedApplication] keyWindow] subviews] lastObject] removeFromSuperview];
}
-(void)postSchedule:(UILocalNotification*)notif
{
    NSDictionary * dict=localNotifyDict;
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    long date = [[dict objectForKey:@"TimeStamp"] intValue];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:date];
    
}
#pragma mark Sqlite DB and Retrive--
-(void)saveinSqlite
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    //NSLog(@"%@",paths);
    // Check to see if the database file already exists
    
    
    /*if (databaseAlreadyExists == YES) {
     return;
     }*/
    
    // Open the database and store the handle as a data member
    if (sqlite3_open([databasePath UTF8String], &_databaseHandle) == SQLITE_OK)
    {
        // Create the database if it doesn't yet exists in the file system
        
        
        // Create the PERSON table
        const char *sqlStatement = "CREATE TABLE  TwitterData (ID INTEGER PRIMARY KEY AUTOINCREMENT, AccessToken TEXT, TwitterId TEXT,UserScreenName TEXT,USERNAME TEXT,IMAGEURL TEXT,EntryDate datetime default current_timestamp)";
        
        char *error;
        if (sqlite3_exec(_databaseHandle, sqlStatement, NULL, NULL, &error) == SQLITE_OK)
        {
            NSLog(@"table created");
            // Create the ADDRESS table with foreign key to the PERSON table
            
            NSLog(@"Database and tables created.");
        }
        else
        {
            NSLog(@"````Error: %s", error);
        }
    }
    
    
}
-(void)createScheduleSchema
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    
    // Open the database and store the handle as a data member
    if (sqlite3_open([databasePath UTF8String], &_databaseHandle) == SQLITE_OK)
    {
        // Create the database if it doesn't yet exists in the file system
        
        
        // Create the PERSON table
        const char *sqlStatement = "CREATE TABLE  ScheduleData1(ID INTEGER PRIMARY KEY AUTOINCREMENT, AccessToken TEXT, TwitterId TEXT,UserScreenName TEXT,IMAGEPOST BLOB,EntryDate INTEGER)";
        
        char *error;
        if (sqlite3_exec(_databaseHandle, sqlStatement, NULL, NULL, &error) == SQLITE_OK)
        {
            NSLog(@"table created");
            // Create the ADDRESS table with foreign key to the PERSON table
            
            NSLog(@"Database and tables created.");
        }
        else
        {
            NSLog(@"````Error: %s", error);
        }
    }
    
    
}

#pragma mark Local Notification
-(NSDictionary*)jsonDictionaryFromString:(NSString*)message
{
    NSData* data = [message dataUsingEncoding:NSUTF8StringEncoding];
    NSError * error=nil;
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];//response object is your response from server as NSData
    if(error)
    {
        NSLog(@"error in creating json%@",error);
    }
    return json;
}

- (void)application:(UIApplication *)app didReceiveLocalNotification:(UILocalNotification *)notif
{
    // Handle the notificaton when the app is running
    NSLog(@"Recieved Notification %@",notif.alertBody);
    NSDictionary * dict=notif.userInfo;
    [Singleton sharedSingleton].localNotificatonDict=notif.userInfo;
    [Singleton sharedSingleton].userTwitterScreenName=[dict objectForKey:@"UserScreenName"];
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    long date = [[dict objectForKey:@"TimeStamp"] intValue];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:date];
    //     [[FHSTwitterEngine sharedEngine]postTweet:@"Tweet From Scheduling"];
    
}

- (void)application:(UIApplication *)application
didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    completionHandler(UIBackgroundFetchResultNewData);
    NSLog(@"Remote Notification userInfo is %@", userInfo);
    
    NSNumber *contentID = userInfo[@"content-id"];
    // Do something with the content ID
    
    if([contentID isEqualToNumber:[NSNumber numberWithInt:42]])
    {
        [[FHSTwitterEngine sharedEngine]postTweet:@"Tweet From the Background 2"];
    }
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end

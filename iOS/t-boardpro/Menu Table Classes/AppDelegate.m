//
//  AppDelegate.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "AppDelegate.h"
#import "FHSTwitterEngine.h"
#import "TwitterHelperClass.h"
#import <sqlite3.h>
#import "SingletonTboard.h"
#import <Parse/Parse.h>
#import "Flurry.h"
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
    
//    [[FHSTwitterEngine sharedEngine]permanentlySetConsumerKey:@"Bo2bBYAYVQYNjTlozocTUBa3t" andSecret:@"cE6Md2hlVw4EhS9Y7V41s28MUKXv4djy54t0T486H74GSjp1jH"];
    //[[FHSTwitterEngine sharedEngine]loadAccessToken];
    
     [Parse setApplicationId:@"4stpsQ6w7p9uRUb8ajuwlyU8yG4m7snBgfIHkKxv" clientKey:@"Knb6BZ5E5hyo03YsQFiGXEYNF5WOnC7UEQBcaOnJ"];
    [PFAnalytics trackAppOpenedWithLaunchOptions:launchOptions];
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
    
    UILocalNotification *localNotif = [launchOptions objectForKey:UIApplicationLaunchOptionsLocalNotificationKey];
    if (localNotif)
    {
        localNotifyDict=localNotif.userInfo;
        [SingletonTboard sharedSingleton].localNotificatonDict=localNotifyDict;
       
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
        [self saveinSqlite];
        [self createScheduleSchema];
        [self createUserStatsTable];
    }
  // [Flurry startSession:@"WPKFZMR8PFT9D8HM2NFZ"];
    return YES;
}
- (void)application:(UIApplication *)application
didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)newDeviceToken
{
    // Store the deviceToken in the current installation and save it to Parse.
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];
    NSString *strInstallationId = currentInstallation.installationId;
    NSLog(@"Installation ID -=-= %@",strInstallationId);
    [currentInstallation setDeviceTokenFromData:newDeviceToken];
    //    currentInstallation.channels = @[@"global"];
    [currentInstallation saveInBackground];
}

-(void)cancelSchedule
{
    [[[[[UIApplication sharedApplication] keyWindow] subviews] lastObject] removeFromSuperview];
}
-(void)postSchedule:(UILocalNotification*)notif
{
    NSDictionary * dict=localNotifyDict;
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:[dict objectForKey:@"TimeStamp"]];
 
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
        const char *sqlStatement = "CREATE TABLE  TwitterData (ID INTEGER PRIMARY KEY AUTOINCREMENT, AccessToken TEXT, TwitterId TEXT,UserScreenName TEXT,USERNAME TEXT,IMAGEURL TEXT,BannerImageUrl TEXT,EntryDate datetime default current_timestamp,FollowingIds TEXT,FollowerIds TEXT)";
        
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
-(void)createUserStatsTable
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
        const char *sqlStatement = "CREATE TABLE IF NOT EXISTS UserStats (ID INTEGER PRIMARY KEY AUTOINCREMENT, TwitterId TEXT,FollowingIds TEXT,FollowerIds TEXT,EntryDate TEXT)";
        
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
        const char *sqlStatement = "CREATE TABLE  ScheduleData1(ID INTEGER PRIMARY KEY AUTOINCREMENT, AccessToken TEXT, TwitterId TEXT,UserScreenName TEXT,IMAGEPOST BLOB,EntryDate TEXT,UserText TEXT)";
        
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
     [SingletonTboard sharedSingleton].localNotificatonDict=notif.userInfo;
    [SingletonTboard sharedSingleton].mainUser=[dict objectForKey:@"UserScreenName"];
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:[dict objectForKey:@"TimeStamp"]];
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

-(BOOL)openSessionLogin
{
       
//    if ([FHSTwitterEngine sharedEngine].isAuthorized) {
//        NSLog(@"a");
//        
//        
//        
//    }
//    else{
//        NSLog(@"Display Login ");
//                
//    }

    return YES;
}


+(void)setToLabel:(UILabel*)lbl Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size Color:(UIColor*)color
{
    lbl.textColor = color;
    if (txt != nil) {
        lbl.text = txt;
    }
    if (font != nil) {
        lbl.font = [UIFont fontWithName:font size:_size];
    }
}

+(void)setButton:(UIButton*)btn Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size TitleColor:(UIColor*)t_color ShadowColor:(UIColor*)s_color
{
    [btn setTitle:txt forState:UIControlStateNormal];
    [btn setTitleColor:t_color forState:UIControlStateNormal];
    if (s_color != nil) {
        [btn setTitleShadowColor:s_color forState:UIControlStateNormal];
    }
    if (font != nil) {
        btn.titleLabel.font = [UIFont fontWithName:font size:_size];
    }
    else{
        btn.titleLabel.font = [UIFont systemFontOfSize:_size];
    }
    
    if (font!=nil) {
        btn.titleLabel.font= [UIFont fontWithName:font size:_size];
    }
    else{
        
        btn.titleLabel.font=[UIFont systemFontOfSize:_size];
    }
}
- (void)applicationWillResignActive:(UIApplication *)application
{
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

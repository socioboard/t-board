//
//  SingletonTboard.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SingletonTboard.h"
#import "Reachability.h"
static SingletonTboard *sharedSingleton;

@implementation SingletonTboard

+(SingletonTboard*)sharedSingleton
{
    @synchronized(self)
    {
        
        if(!sharedSingleton)
        {
            sharedSingleton=[[SingletonTboard alloc]init];
        }
    }return sharedSingleton;
}
+ (BOOL)networkCheck {
    
    Reachability *wifiReach = [Reachability reachabilityForInternetConnection];
    NetworkStatus netStatus = [wifiReach currentReachabilityStatus];
    
    switch (netStatus)
    {
        case NotReachable:
        {
            NSLog(@"NETWORKCHECK: Not Connected");
            [[[UIAlertView alloc] initWithTitle:@"Error" message:@"Check internet connection." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles: nil] show];
            return NO;
            break;
        }
        case ReachableViaWWAN:
        {
            NSLog(@"NETWORKCHECK: Connected Via WWAN");
            return YES;
            break;
        }
        case ReachableViaWiFi:
        {
            NSLog(@"NETWORKCHECK: Connected Via WiFi");
            return YES;
            break;
        }
    }
    return NO;
}
#pragma Mark Update Follow Unfollow
#pragma mark Update Follow/Unfollow Locally
+(void)updateFollowArray:(NSString*)idToAdd
{
    //Always add on follower
    NSMutableArray * followerArray=[[NSMutableArray alloc]initWithArray: [SingletonTboard sharedSingleton].followerData];
    [followerArray addObject:idToAdd];
    [SingletonTboard sharedSingleton].followerData=[followerArray copy];
}
+(void)updateFollowingArray:(NSString*)idToRemove
{
    //Remove from Following
    NSMutableArray * followerArray=[[NSMutableArray alloc]initWithArray: [SingletonTboard sharedSingleton].followingData];
    [followerArray removeObject:idToRemove];
    [SingletonTboard sharedSingleton].followingData=[followerArray copy];
}
-(void)fetchListOfFollow_Unfollow
{
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            
            id followingId=[[FHSTwitterEngine sharedEngine] getFriendsIDs];
            NSLog(@"Following id %@",followingId);
            if([followingId isKindOfClass:[NSError class]])
            {
                
            }
            else
            {
                [SingletonTboard sharedSingleton].followingData=[followingId objectForKey:@"ids"];
            }
            id followersId=[[FHSTwitterEngine sharedEngine]getFollowersIDs];
            if([followersId isKindOfClass:[NSError class]])
            {
                
            }
            else
            {
                [SingletonTboard sharedSingleton].followerData=[followersId objectForKey:@"ids"];
            }
            
            NSLog(@"Followers id %@",followersId);
            //Save follow and follower in Sqlite
            //--------------
            NSDateFormatter * dateFormatter=[[NSDateFormatter alloc]init];
            [dateFormatter setDateFormat:@"MM-dd"];
            NSDate * date=[NSDate date];
            NSString * todayDay=[dateFormatter stringFromDate:date];
             NSString * savedOldDate=[[NSUserDefaults standardUserDefaults]objectForKey:@"DateOfAppOpened"];
            if(![todayDay isEqualToString:savedOldDate])
            {
            twitterHelperObj=[[TwitterHelperClass alloc]init];
            [twitterHelperObj saveFollowingAndFollower:[SingletonTboard sharedSingleton].followingData followerIds:[SingletonTboard sharedSingleton].followerData entryDate:todayDay];
            [self saveTodayDayInSqlite];
            }
        }
    });
    
}
-(void)saveTodayDayInSqlite
{
    NSDateFormatter * dateFormatter=[[NSDateFormatter alloc]init];
    [dateFormatter setDateFormat:@"MM-dd"];
    NSDate * date=[NSDate date];
    NSString * todayDay=[dateFormatter stringFromDate:date];
    [[NSUserDefaults standardUserDefaults]setObject:todayDay forKey:@"DateOfAppOpened"];
   
}
@end

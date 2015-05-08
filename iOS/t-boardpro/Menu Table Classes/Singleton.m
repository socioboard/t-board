//
//  Singleton.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "Singleton.h"
#import "Reachability.h"
static Singleton *sharedSingleton;

@implementation Singleton

+(Singleton*)sharedSingleton
{
    @synchronized(self)
    {
        
        if(!sharedSingleton)
        {
            sharedSingleton=[[Singleton alloc]init];
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
@end

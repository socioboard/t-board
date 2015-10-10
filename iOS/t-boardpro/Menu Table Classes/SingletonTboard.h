//
//  SingletonTboard.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TwitterHelperClass.h"
#import <UIKit/UIKit.h>
@interface SingletonTboard : NSObject
{
    TwitterHelperClass * twitterHelperObj;
}
@property (strong,nonatomic) NSString * currectUserTwitterId,*userTwitterScreenName,*accessTokenCurrent;
@property (strong,nonatomic) NSString * mainUser,*mainUserRealName;
@property (strong,nonatomic) NSDictionary * localNotificatonDict;
@property (strong,nonatomic) UIImage * imageUser;
@property (nonatomic,strong) id userimage;
@property (strong,nonatomic) NSString * imageUrl;
@property (strong,nonatomic) NSDictionary * twitterData;
@property (nonatomic)        BOOL    newAccountAdded;
@property (nonatomic,strong) NSArray * allDataUser,*followerData,*followingData;
@property (nonatomic,strong) NSString * tweetCount,*followerCount,*followingCount,*bannerImageUrl;
#pragma mark--
+(SingletonTboard*)sharedSingleton;
+ (BOOL)networkCheck;
+(void)updateFollowArray:(NSString*)idToAdd;
+(void)updateFollowingArray:(NSString*)idToRemove;
-(void)fetchListOfFollow_Unfollow;
@end

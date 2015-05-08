//
//  Singleton.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
@interface Singleton : NSObject
{
    
}
@property (strong,nonatomic) NSString * currectUserTwitterId,*userTwitterScreenName,*accessTokenCurrent;
@property (strong,nonatomic) NSString * mainUser,*mainUserRealName;
@property (strong,nonatomic) NSDictionary * localNotificatonDict;
@property (strong,nonatomic) UIImage * imageUser;
@property (nonatomic,strong) id userimage;
@property (strong,nonatomic) NSString * imageUrl;
@property (strong,nonatomic) NSDictionary * twitterData;
@property (nonatomic)BOOL newAccountAdded;
@property (nonatomic,strong) NSString * tweetCount,*followerCount,*followingCount;
+(Singleton*)sharedSingleton;
+ (BOOL)networkCheck;
@end

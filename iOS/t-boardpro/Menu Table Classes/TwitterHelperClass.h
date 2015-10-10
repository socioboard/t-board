//
//  TwitterHelperClass.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <sqlite3.h>
@interface TwitterHelperClass : NSObject
{
    sqlite3 *_databaseHandle;
    UIImage * userImageToStore;
    NSString * imageUrl,*bannerImageUrl;
 }
-(BOOL)retriveTwitterDataSqlite:(NSString *)twitterId;
-(void)saveUserDetailInSqlite:(NSString *)AccessToken username:(NSString *)Username;
-(void)saveFollowingAndFollower:(NSArray *)followingIds followerIds   :(NSArray *)followerIds entryDate:(NSString *)entryDate;
-(void)saveScheduleInSqlite:(NSString *)AccessToken userImage:(UIImage *)UserImage date:(int)date userText:(NSString *)userText twitterId:(NSString*)twitterId UserScreenName:(NSString*)userScreenName;
-(BOOL)retriveAndScheduleSqlite:(NSString *)text date:(NSString*)date;
-(NSArray*)retriveUserStatus:(NSString*)currentUserTwitterId;
-(void)deleteEntryFromSqlit:(NSString*)entryDate;
-(void)deleteUserFromSqlit:(NSString*)userTwitterScreenName;
//--------------------------------------------
-(NSArray *)fetchOwnTweet:(NSString*)sinceId;
-(NSArray *)fetchOwnTweet:(NSString*)userId sinceOrMaxID:(NSString*)sinceOrMaxID;
-(NSArray *)fetchOwnMention:(NSString*)userId sinceOrMaxID:(NSString*)sinceOrMaxID;
-(NSArray*)retriveScheduleInSqlite;
-(NSArray *)fetchTimeline:(NSString*)userId;
-(NSArray *)fetchOwnMention:(int)userId;
-(void)updateSqlite:(NSString *)accessToken twitterId:(NSString *)twitterId;
-(void)fetchUserNameAndImage:(NSString *)usernameL;
@end

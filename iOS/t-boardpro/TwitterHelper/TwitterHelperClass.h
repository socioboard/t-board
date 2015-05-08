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
    NSString * imageUrl;
 }
-(BOOL)retriveTwitterDataSqlite:(NSString *)twitterId;
-(void)saveUserDetailInSqlite:(NSString *)AccessToken username:(NSString *)Username;
-(void)saveScheduleInSqlite:(NSString *)AccessToken userImage:(UIImage *)UserImage date:(int)date;
-(BOOL)retriveAndScheduleSqlite:(NSString *)text date:(long)date;
-(void)deleteEntryFromSqlit:(int)entryDate;
//--------------------------------------------
-(NSArray *)fetchTimeline;
-(void)updateSqlite:(NSString *)accessToken twitterId:(NSString *)twitterId;
-(void)fetchUserNameAndImage:(NSString *)usernameL;
@end

//
//  TwitterHelperClass.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "TwitterHelperClass.h"
#import <sqlite3.h>
#import "FHSTwitterEngine.h"
#import "SDWebImageManager.h"
#import "SingletonTboard.h"
@implementation TwitterHelperClass

#pragma mark SQL METHODS

-(BOOL)retriveTwitterDataSqlite:(NSString *)twitterId
{
    
    BOOL check_Update;
    check_Update=FALSE;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from TwitterData where TwitterId = \"%@\"",twitterId];
    sqlite3_stmt *stmt=nil;
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    
    if (sqlite3_prepare_v2(_databaseHandle, [query UTF8String], -1, &stmt, NULL)== SQLITE_OK)
    {
        NSLog(@"prepared");
    }
    else
        NSLog(@"error");
    // sqlite3_step(stmt);
    @try
    {
        
        while(sqlite3_step(stmt)==SQLITE_ROW)
        {
            char *twitterId = (char *) sqlite3_column_text(stmt,2);
            NSString *strTwitterId= [NSString  stringWithUTF8String:twitterId];
            if([strTwitterId isEqualToString:[SingletonTboard sharedSingleton].currectUserTwitterId])
            {
                check_Update=TRUE;
            }
        }
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
    if(check_Update)
    {
        return YES;
    }
    else
    {
    return false;
    }
}
-(void)saveUserDetailInSqlite:(NSString *)AccessToken username:(NSString *)Username
{
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    /*NSString * query=[NSString stringWithFormat:@"INSERT INTO mydata3(FIRSTNAME,LASTNAME) VALUES(\"%@\",\"%@\")",[arraybhai objectAtIndex:0],[arraybhai objectAtIndex:1]];*/
    const char * query="insert into TwitterData(AccessToken,TwitterId,UserScreenName,USERNAME,IMAGEURL,BannerImageUrl) values(?,?,?,?,?,?)";
    
    sqlite3_stmt *inset_statement;
   
       if (sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK) {
        NSLog(@"Error to Open");
        return;
    }
    
    if (sqlite3_prepare_v2(_databaseHandle, query , -1,&inset_statement, NULL) != SQLITE_OK ) {
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        NSLog(@"Error to Prepare");
        
    }
    sqlite3_bind_text(inset_statement,1, [AccessToken UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 2, [[SingletonTboard sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3,[[SingletonTboard sharedSingleton].mainUser UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 4, [Username UTF8String], -1, SQLITE_TRANSIENT);
     sqlite3_bind_text(inset_statement, 5, [imageUrl UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement,6, [bannerImageUrl UTF8String], -1, SQLITE_TRANSIENT);
        if(sqlite3_step(inset_statement) == SQLITE_DONE)
        {
     
        NSLog(@"Success");
        }
    NSArray * prevoiusArray=[[NSUserDefaults standardUserDefaults]objectForKey:TwitterUserScreenName];
    NSMutableArray * twitterNameArr=[[NSMutableArray alloc]initWithArray:prevoiusArray];
    [twitterNameArr addObject:[SingletonTboard sharedSingleton].mainUser];
    [[NSUserDefaults standardUserDefaults]setObject:twitterNameArr forKey:TwitterUserScreenName];
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);

    if([SingletonTboard sharedSingleton].newAccountAdded)
    {
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ReloadAccountTable_NewAccount" object:nil];
        [SingletonTboard sharedSingleton].newAccountAdded=false;
    }
    
}
-(void)updateSqlite:(NSString *)accessToken twitterId:(NSString *)twitterId
{

    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    
    const char * query="update TwitterData set AccessToken=?,TwitterId=?,IMAGEURL=? where USERNAME=?";
    
    sqlite3_stmt *inset_statement;
    
    
    if (sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK) {
        NSLog(@"Error to Open");
        return;
    }
    
    if (sqlite3_prepare_v2(_databaseHandle, query , -1,&inset_statement, NULL) != SQLITE_OK ) {
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        NSLog(@"Error to Prepare");
        
    }
    sqlite3_bind_text(inset_statement,1,[accessToken UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 2, [[SingletonTboard sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3, [[SingletonTboard sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
     sqlite3_bind_text(inset_statement, 4, [[SingletonTboard sharedSingleton].mainUser UTF8String], -1, SQLITE_TRANSIENT);
    if(sqlite3_step(inset_statement)== SQLITE_DONE)
    {
    
    }
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);
}
-(void)saveFollowingAndFollower:(NSArray *)followingIds followerIds:(NSArray *)followerIds entryDate:(NSString *)entryDate
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    /*NSString * query=[NSString stringWithFormat:@"INSERT INTO mydata3(FIRSTNAME,LASTNAME) VALUES(\"%@\",\"%@\")",[arraybhai objectAtIndex:0],[arraybhai objectAtIndex:1]];*/
    const char * query="insert into UserStats(TwitterId,FollowingIds,FollowerIds,EntryDate) values(?,?,?,?)";
    
    sqlite3_stmt *inset_statement;
    
    if (sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK) {
        NSLog(@"Error to Open");
        return;
    }
    
    if (sqlite3_prepare_v2(_databaseHandle, query , -1,&inset_statement, NULL) != SQLITE_OK ) {
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        NSLog(@"Error to Prepare");
        
    }
    NSString * followingIdStr=[NSString stringWithFormat:@"%d",[followingIds count]];
    NSString * followersIdStr=[NSString stringWithFormat:@"%d",[followerIds count]];

    sqlite3_bind_text(inset_statement,1,[[SingletonTboard sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 2,[followingIdStr UTF8String] , -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3,[followersIdStr UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 4, [entryDate UTF8String], -1, SQLITE_TRANSIENT);
       if(sqlite3_step(inset_statement) == SQLITE_DONE)
    {
        
        NSLog(@"Success");
    }
}

-(BOOL)retriveAndScheduleSqlite:(NSString *)text date:(NSString *)date
{
      NSLog(@"Date of Scheduling %@",date);
    
       NSDictionary * localData=[SingletonTboard sharedSingleton].localNotificatonDict;
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            BOOL check_Update;
    check_Update=FALSE;
    NSString * dateFunction;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
            //Converting Date into String
            NSString * dateStr=[localData objectForKey:@"TimeStamp"];
    NSString *query = [NSString stringWithFormat:@"select * from ScheduleData1 where UserScreenName = \"%@\" and EntryDate = \"%@\"",[localData objectForKey:@"UserScreenName"],dateStr];
    sqlite3_stmt *stmt=nil;
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    
    if (sqlite3_prepare_v2(_databaseHandle, [query UTF8String], -1, &stmt, NULL)== SQLITE_OK)
    {
        NSLog(@"prepared");
    }
    else
        NSLog(@"error");
    // sqlite3_step(stmt);
    @try
    {
        
        while(sqlite3_step(stmt)==SQLITE_ROW)
        {
            dispatch_async(dispatch_get_main_queue(), ^(void){
                
                
            });
            char *twitterId = (char *) sqlite3_column_text(stmt,2);
            NSString *strTwitterId= [NSString  stringWithUTF8String:twitterId];
            char *image= (char *) sqlite3_column_blob(stmt, 4);
            int rawdatalength=sqlite3_column_bytes(stmt, 4);
            NSData*data=[NSData dataWithBytes:image length:rawdatalength];
            char * date  = (char *) sqlite3_column_text(stmt,5);
            dateFunction=[NSString stringWithUTF8String:date];
            id returned=[[FHSTwitterEngine sharedEngine] postTweet:[localData objectForKey:@"Text"] withImageData:data];
            if ([returned isKindOfClass:[NSError class]])
            {
                NSLog(@"Erro in Tweet %@",returned);
                UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
                [noInternet show];

            }
            else
            {
                NSLog(@"Schedule post");
                dispatch_async(dispatch_get_main_queue(), ^(void){
                 
                    
                });
                

                   check_Update=TRUE;
               
            }
        }
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
   
    if(check_Update)
    {
        sqlite3_close(_databaseHandle);
        [self deleteEntryFromSqlit:dateFunction];
    }
    else
    {
        
    }
        }
    });
    return YES;
}
-(NSArray*)retriveUserStatus:(NSString*)currentUserTwitterId
{
    NSMutableArray * tempArray=[[NSMutableArray alloc]init];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from UserStats where TwitterId='%@'",[SingletonTboard sharedSingleton].currectUserTwitterId];
    sqlite3_stmt *stmt=nil;
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    
    if (sqlite3_prepare_v2(_databaseHandle, [query UTF8String], -1, &stmt, NULL)== SQLITE_OK)
    {
        NSLog(@"prepared");
    }
    else
        NSLog(@"error");
    // sqlite3_step(stmt);
    @try
    {
        while(sqlite3_step(stmt)==SQLITE_ROW)
        {
            //-----
            NSMutableDictionary * tempDict=[[NSMutableDictionary alloc
                                             ]init];
            //--------
            char *followingIds = (char *) sqlite3_column_text(stmt,2);
            char *followerIds = (char *) sqlite3_column_text(stmt,3);
            char *entryDate=(char*)sqlite3_column_text(stmt,4);
          NSString * followingCount=[NSString stringWithUTF8String:followingIds];
            NSString * followerCount=[NSString stringWithUTF8String:followerIds];
            NSLog(@"following anf folloewr array %@ %s",followingCount,followerIds);
            [tempDict setObject:followingCount forKey:@"FollowingArray"];
            [tempDict setObject:followerCount forKey:@"FollowerArray"];
            [tempDict setObject:[NSString stringWithUTF8String:entryDate]   forKey:@"EntryDate"];
            [tempArray addObject:tempDict];

        }
        
        
        
        
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
    return tempArray;
}
-(void)deleteEntryFromSqlit:(NSString*)entryDate
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"-------%@",paths);
    sqlite3_stmt *stmt=nil;
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    
    const char *sql = "Delete from ScheduleData1 where UserScreenName=? and EntryDate=?";
    
    
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    if(sqlite3_prepare_v2(_databaseHandle, sql, -1, &stmt, NULL)!=SQLITE_OK)
    {
        NSLog(@"error to prepare");
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        
        
    }
    
    NSLog(@"user Screen ame %@ %@",[SingletonTboard sharedSingleton].mainUser,entryDate);
    sqlite3_bind_text(stmt, 1, [[SingletonTboard sharedSingleton].mainUser UTF8String], -1, SQLITE_TRANSIENT);
     sqlite3_bind_text(stmt, 2, [entryDate UTF8String], -1, SQLITE_TRANSIENT);

//     sqlite3_bind_text(stmt,2, [entryDate UTF8String], -1, SQLITE_TRANSIENT);
    
    if(sqlite3_step(stmt)==SQLITE_DONE)
    {
        
    }
    else
    {
        NSLog(@"Error in Sqlite %d",sqlite3_step(stmt));
    }
   
    sqlite3_finalize(stmt);
}
-(void)deleteUserFromSqlit:(NSString*)userTwitterScreenName
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"-------%@",paths);
    sqlite3_stmt *stmt=nil;
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    
    const char *sql = "Delete from TwitterData where UserScreenName=?";
    
    
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    if(sqlite3_prepare_v2(_databaseHandle, sql, -1, &stmt, NULL)!=SQLITE_OK)
    {
        NSLog(@"error to prepare");
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        
        
    }
    
       sqlite3_bind_text(stmt, 1,[userTwitterScreenName UTF8String], -1, SQLITE_TRANSIENT);
    
    
    if(sqlite3_step(stmt)==SQLITE_DONE)
    {
        
    }
    else
    {
        NSLog(@"Error in Sqlite %d",sqlite3_step(stmt));
    }
    
    sqlite3_finalize(stmt);
}
#pragma mark Schedule
-(void)saveScheduleInSqlite:(NSString *)AccessToken userImage:(UIImage *)UserImage date:(int)date userText:(NSString *)userText twitterId:(NSString*)twitterId UserScreenName:(NSString*)userScreenName
{
    NSData * bind=UIImagePNGRepresentation(UserImage);
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    /*NSString * query=[NSString stringWithFormat:@"INSERT INTO mydata3(FIRSTNAME,LASTNAME) VALUES(\"%@\",\"%@\")",[arraybhai objectAtIndex:0],[arraybhai objectAtIndex:1]];*/
    const char * query;
    if(UserImage)
    {
         query="insert into ScheduleData1(AccessToken,TwitterId,UserScreenName,IMAGEPOST,EntryDate,UserText) values(?,?,?,?,?,?)";
    }
    else
    {
         query="insert into ScheduleData1(AccessToken,TwitterId,UserScreenName,EntryDate,UserText) values(?,?,?,?,?)";
    }
   
    
    sqlite3_stmt *inset_statement;
    
    if (sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK) {
        NSLog(@"Error to Open");
        return;
    }
    
    if (sqlite3_prepare_v2(_databaseHandle, query , -1,&inset_statement, NULL) != SQLITE_OK ) {
        NSLog(@"%s Prepare failure '%s' (%1d)", __FUNCTION__, sqlite3_errmsg(_databaseHandle), sqlite3_errcode(_databaseHandle));
        NSLog(@"Error to Prepare");
        
    }
    sqlite3_bind_text(inset_statement,1, [AccessToken UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 2,[twitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3,[userScreenName UTF8String], -1, SQLITE_TRANSIENT);
   if(UserImage)
   {
       sqlite3_bind_blob(inset_statement, 4,  [bind bytes], (int)[bind length], SQLITE_TRANSIENT);
       sqlite3_bind_int(inset_statement,5,date);

   }
    else
    {
        sqlite3_bind_int(inset_statement,4,date);

    }
    sqlite3_bind_text(inset_statement, 5,[userText UTF8String], -1, SQLITE_TRANSIENT);
    if(sqlite3_step(inset_statement) == SQLITE_DONE)
    {
        
        NSLog(@"Success");
    }
   
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);
    
    
}
-(NSArray*)retriveScheduleInSqlite
{
    NSMutableArray * addValuesTemp=[[NSMutableArray alloc]init];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSLog(@"%@",paths);
    
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from ScheduleData1"];
    sqlite3_stmt *stmt=nil;
    if(sqlite3_open([databasePath UTF8String], &_databaseHandle)!=SQLITE_OK)
        NSLog(@"error to open");
    
    if (sqlite3_prepare_v2(_databaseHandle, [query UTF8String], -1, &stmt, NULL)== SQLITE_OK)
    {
        NSLog(@"prepared");
    }
    else
        NSLog(@"error");
    // sqlite3_step(stmt);
    @try
    {
       
        while(sqlite3_step(stmt)==SQLITE_ROW)
        {
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]init];
           //--
            char *twitterId = (char *) sqlite3_column_text(stmt,2);
            NSString *strTwitterId=[NSString stringWithUTF8String:twitterId];
            //--
            char * twitterScreenName=(char*)sqlite3_column_text(stmt,3);
            NSString *twitterScreenNameStr=[NSString stringWithUTF8String:twitterScreenName];
            //--
            char * twiiterDate=(char*)sqlite3_column_text(stmt,5);
            NSString * strTwitterDate=[NSString stringWithUTF8String:twiiterDate];
            //--
            char * twiiterText=(char*)sqlite3_column_text(stmt,6);
            NSString * strTwitterText=[NSString stringWithUTF8String:twiiterText];
            //--
            [dictTemp setObject:strTwitterId forKey:@"UserTwitterId"];
            [dictTemp setObject:twitterScreenNameStr forKey:@"TwitterScreenName"];
            [dictTemp setObject:strTwitterText forKey:@"TwitterText"];
            [dictTemp setObject:strTwitterDate forKey:@"TwitterDate"];
            [addValuesTemp addObject:dictTemp];
        }
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
    
    return addValuesTemp;
}

#pragma mark Twitter Methods
-(NSArray *)fetchTimeline:(NSString*)sinceId
{
   
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
    id returned=[[FHSTwitterEngine sharedEngine]getHomeTimelineSinceIDAndMaxId:@"" maxID:sinceId count:20];
    NSString * title;
    NSString * message;
    if ([returned isKindOfClass:[NSError class]])
    {
    NSError *error = (NSError *)returned;
    title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
    message = error.localizedDescription;
        NSLog(@"Error in finding home timeline %@ %@",message,title);
    }
    else
    {
    NSLog(@"Home timeline%@",returned);
    
    for (int i=0;i<[returned count]; i++)
    {
        NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
        //----------------------------
        NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
        NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
        id dictOfdict=[dictTimelineRow objectForKey:@"user"];
        //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
        [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
        [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
        [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
        [tempForEachRow setObject:[dictOfdict objectForKey:@"screen_name"] forKey:@"ScreenName"];
        [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorite_count"] forKey:@"FavouriteCount"];
        [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorited"] forKey:@"BooleanFavourite"];
        [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweet_count"] forKey:@"RetweetCount"];
          [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweeted"] forKey:@"BooleanRetweeted"];
        [userData addObject:tempForEachRow];
        //----------------------------
    }
    //Copy the data in whole data
    }
    }
    else
    {
        NSLog(@"Login to account again");
    }
    NSLog(@" Home Timeline Data %@",userData);
    return [NSArray arrayWithArray:userData];
}
-(void)fetchUserNameAndImage:(NSString *)usernameL
{
    
    id returned=[[FHSTwitterEngine sharedEngine]getTimelineForUser:usernameL isID:YES count:1];
    NSString * title;
    NSString * message;
    if ([returned isKindOfClass:[NSError class]])
    {
        NSError *error = (NSError *)returned;
        title = [NSString stringWithFormat:@"Error %d",error.code];
        message = error.localizedDescription;
    }
    else
    {
        NSLog(@"timeline%@",returned);
        
        [self fetchUserImage:returned];
       
    }//else inner
}
-(NSArray *)fetchOwnTweet:(NSString*)userId
{
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
        id returned=[[FHSTwitterEngine sharedEngine]getTimelineForUser:userId isID:YES count:20];
        NSString * title;
        NSString * message;
        if ([returned isKindOfClass:[NSError class]])
        {
            NSError *error = (NSError *)returned;
            title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
            message = error.localizedDescription;
            NSLog(@"Error in finding  timeline %@ %@",message,title);
        }
        else
        {
            NSLog(@"timeline%@",returned);
            
            for (int i=0;i<[returned count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"screen_name"] forKey:@"ScreenName"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorite_count"] forKey:@"FavouriteCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorited"] forKey:@"BooleanFavourite"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweet_count"] forKey:@"RetweetCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweeted"] forKey:@"BooleanRetweeted"];
                [userData addObject:tempForEachRow];
                //----------------------------
            }
            //Copy the data in whole data
        }
    }
    else
    {
        NSLog(@"Login to account again");
    }
    NSLog(@" Home Timeline Data %@",userData);
    return [NSArray arrayWithArray:userData];
}
-(NSArray *)fetchOwnTweet:(NSString*)userId sinceOrMaxID:(NSString*)sinceOrMaxID
{
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
        id returned=[[FHSTwitterEngine sharedEngine]getTimelineForUser:userId isID:YES count:20 sinceID:sinceOrMaxID maxID:@""];
        NSString * title;
        NSString * message;
        if ([returned isKindOfClass:[NSError class]])
        {
            NSError *error = (NSError *)returned;
            title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
            message = error.localizedDescription;
            NSLog(@"Error in finding  timeline %@ %@",message,title);
        }
        else
        {
            NSLog(@"timeline%@",returned);
            
            for (int i=0;i<[returned count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"screen_name"] forKey:@"ScreenName"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorite_count"] forKey:@"FavouriteCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorited"] forKey:@"BooleanFavourite"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweet_count"] forKey:@"RetweetCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweeted"] forKey:@"BooleanRetweeted"];
                [userData addObject:tempForEachRow];
                //----------------------------
            }
            //Copy the data in whole data
        }
    }
    else
    {
        NSLog(@"Login to account again");
    }
    NSLog(@" Home Timeline Data %@",userData);
    return [NSArray arrayWithArray:userData];
}

#pragma mark---
-(NSArray *)fetchOwnMention:(int)userId
{
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
        id returned=[[FHSTwitterEngine sharedEngine]getMentionsTimelineWithCount:20];
        NSString * title;
        NSString * message;
        if ([returned isKindOfClass:[NSError class]])
        {
            NSError *error = (NSError *)returned;
            title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
            message = error.localizedDescription;
            NSLog(@"Error in finding  timeline %@ %@",message,title);
        }
        else
        {
            NSLog(@"timeline%@",returned);
            
            for (int i=0;i<[returned count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"screen_name"] forKey:@"ScreenName"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorite_count"] forKey:@"FavouriteCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorited"] forKey:@"BooleanFavourite"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweet_count"] forKey:@"RetweetCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweeted"] forKey:@"BooleanRetweeted"];
                [userData addObject:tempForEachRow];
                //----------------------------
            }
            //Copy the data in whole data
        }
    }
    else
    {
        NSLog(@"Login to account again");
    }
    NSLog(@" Home Timeline Data %@",userData);
    return [NSArray arrayWithArray:userData];
}
-(NSArray *)fetchOwnMention:(NSString*)userId sinceOrMaxID:(NSString*)sinceOrMaxID
{
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
        id returned=[[FHSTwitterEngine sharedEngine]getMentionsTimelineWithCount:20 sinceID:sinceOrMaxID maxID:@""];
        NSString * title;
        NSString * message;
        if ([returned isKindOfClass:[NSError class]])
        {
            NSError *error = (NSError *)returned;
            title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
            message = error.localizedDescription;
            NSLog(@"Error in finding  timeline %@ %@",message,title);
        }
        else
        {
            NSLog(@"timeline%@",returned);
            
            for (int i=0;i<[returned count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"screen_name"] forKey:@"ScreenName"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorite_count"] forKey:@"FavouriteCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"favorited"] forKey:@"BooleanFavourite"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweet_count"] forKey:@"RetweetCount"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"retweeted"] forKey:@"BooleanRetweeted"];
                [userData addObject:tempForEachRow];
                //----------------------------
            }
            //Copy the data in whole data
        }
    }
    else
    {
        NSLog(@"Login to account again");
    }
    NSLog(@" Home Timeline Data %@",userData);
    return [NSArray arrayWithArray:userData];
}

-(void)fetchUserImage:(id)data
{
    NSDictionary * userData;
    NSDictionary * dictForImage;
    NSString * urlForImage;
    NSString *userNameLbl;
    if([data count]>0)
    {
        userData=[data objectAtIndex:0];
        dictForImage=[userData objectForKey:@"user"];
        urlForImage=[dictForImage objectForKey:@"profile_image_url_https"];
        userNameLbl=[dictForImage objectForKey:@"name"];
    }
    else
    {
        urlForImage=@" ";
        userNameLbl=@" ";
    }
    imageUrl=urlForImage;
    bannerImageUrl=[dictForImage objectForKey:@"profile_banner_url"];
    [SingletonTboard sharedSingleton].imageUrl=urlForImage;
    [SingletonTboard sharedSingleton].bannerImageUrl=bannerImageUrl;
    
    //bannerImageUrl=
   //Fetch Data From Sqlite
    if([self retriveTwitterDataSqlite:[SingletonTboard sharedSingleton].currectUserTwitterId])
    {
    //Update if exist
    [self updateSqlite:[SingletonTboard sharedSingleton].accessTokenCurrent twitterId:[SingletonTboard sharedSingleton].currectUserTwitterId];
    }
    else
    {
        //insert first entry
    [self saveUserDetailInSqlite:[SingletonTboard sharedSingleton].accessTokenCurrent username:userNameLbl];
    }
}
@end

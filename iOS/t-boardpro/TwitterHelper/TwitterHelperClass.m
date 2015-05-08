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
#import "Singleton.h"
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
           
            
            if([strTwitterId isEqualToString:[Singleton sharedSingleton].currectUserTwitterId])
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
    const char * query="insert into TwitterData(AccessToken,TwitterId,UserScreenName,USERNAME,IMAGEURL) values(?,?,?,?,?)";
    
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
    sqlite3_bind_text(inset_statement, 2, [[Singleton sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3,[[Singleton sharedSingleton].userTwitterScreenName UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 4, [Username UTF8String], -1, SQLITE_TRANSIENT);
     sqlite3_bind_text(inset_statement, 5, [imageUrl UTF8String], -1, SQLITE_TRANSIENT);
        if(sqlite3_step(inset_statement) == SQLITE_DONE)
        {
     
        NSLog(@"Success");
        }
    NSArray * prevoiusArray=[[NSUserDefaults standardUserDefaults]objectForKey:@"Twitter Name"];
    NSMutableArray * twitterNameArr=[[NSMutableArray alloc]initWithArray:prevoiusArray];
    [twitterNameArr addObject:[Singleton sharedSingleton].userTwitterScreenName];
    [[NSUserDefaults standardUserDefaults]setObject:twitterNameArr forKey:@"Twitter Name"];
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);

    if([Singleton sharedSingleton].newAccountAdded)
    {
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ReloadAccountTable_NewAccount" object:nil];
        [Singleton sharedSingleton].newAccountAdded=false;
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
    sqlite3_bind_text(inset_statement, 2, [[Singleton sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3, [[Singleton sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
     sqlite3_bind_text(inset_statement, 4, [[Singleton sharedSingleton].userTwitterScreenName UTF8String], -1, SQLITE_TRANSIENT);
    if(sqlite3_step(inset_statement)== SQLITE_DONE)
    {
    
    }
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);
}
-(BOOL)retriveAndScheduleSqlite:(NSString *)text date:(long)date
{
      NSLog(@"Date of Scheduling %ld",date);
    
       NSDictionary * localData=[Singleton sharedSingleton].localNotificatonDict;
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            BOOL check_Update;
    check_Update=FALSE;
    int dateFunction;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from ScheduleData1 where UserScreenName = \"%@\" and EntryDate = \"%d\"",[localData objectForKey:@"UserScreenName"],[[localData objectForKey:@"TimeStamp"]intValue]];
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
            int date = sqlite3_column_int(stmt,5);
            dateFunction=date;
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
-(void)deleteEntryFromSqlit:(int)entryDate
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
    
    NSLog(@"user Screen ame %@ %d",[Singleton sharedSingleton].userTwitterScreenName,entryDate);
    sqlite3_bind_text(stmt, 1, [[Singleton sharedSingleton].userTwitterScreenName UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_int(stmt,2,entryDate);

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
-(void)saveScheduleInSqlite:(NSString *)AccessToken userImage:(UIImage *)UserImage date:(int)date
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
         query="insert into ScheduleData1(AccessToken,TwitterId,UserScreenName,IMAGEPOST,EntryDate) values(?,?,?,?,?)";
    }
    else
    {
         query="insert into ScheduleData1(AccessToken,TwitterId,UserScreenName,EntryDate) values(?,?,?,?)";
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
    sqlite3_bind_text(inset_statement, 2, [[Singleton sharedSingleton].currectUserTwitterId UTF8String], -1, SQLITE_TRANSIENT);
    sqlite3_bind_text(inset_statement, 3,[[Singleton sharedSingleton].userTwitterScreenName UTF8String], -1, SQLITE_TRANSIENT);
   if(UserImage)
   {
       sqlite3_bind_blob(inset_statement, 4,  [bind bytes], (int)[bind length], SQLITE_TRANSIENT);
       sqlite3_bind_int(inset_statement,5,date);

   }
    else
    {
        sqlite3_bind_int(inset_statement,4,date);

    }
   

    if(sqlite3_step(inset_statement) == SQLITE_DONE)
    {
        
        NSLog(@"Success");
    }
   
    sqlite3_finalize(inset_statement);
    sqlite3_close(_databaseHandle);
    
    
}

#pragma mark Twitter Methods
-(NSArray *)fetchTimeline
{
    NSMutableArray * userData=[[NSMutableArray alloc]init];
    if([[FHSTwitterEngine sharedEngine]isAuthorized])
    {
    id returned=[[FHSTwitterEngine sharedEngine]getHomeTimelineSinceID:[Singleton sharedSingleton].currectUserTwitterId count:20];
    NSString * title;
    NSString * message;
    if ([returned isKindOfClass:[NSError class]])
    {
    NSError *error = (NSError *)returned;
    title = [NSString stringWithFormat:@"Error %d",error.code];
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
-(void)fetchUserImage:(id)data
{
    
    NSMutableDictionary * containName_Image=[[NSMutableDictionary alloc]init];
    NSDictionary * userData=[data objectAtIndex:0];
    NSDictionary * dictForImage=[userData objectForKey:@"user"];
    NSString * urlForImage=[dictForImage objectForKey:@"profile_image_url_https"];
    NSLog(@"image of user%@ and name %@",urlForImage,[dictForImage objectForKey:@"name"]);
    NSString *userNameLbl=[dictForImage objectForKey:@"name"];
    [containName_Image setObject:userNameLbl forKey:@"NameOfAccountHolder"];
    [containName_Image setObject:urlForImage forKey:@"ImageUrlAccountHolder"];
//    NSURL *url=[NSURL URLWithString:urlForImage];
    imageUrl=urlForImage;
   if([self retriveTwitterDataSqlite:[Singleton sharedSingleton].currectUserTwitterId])
    {
            //Update if exist
    [self updateSqlite:[Singleton sharedSingleton].accessTokenCurrent twitterId:[Singleton sharedSingleton].currectUserTwitterId];
    }
    else
    {
        //insert first entry
    [self saveUserDetailInSqlite:[Singleton sharedSingleton].accessTokenCurrent username:userNameLbl];
    }
}

@end

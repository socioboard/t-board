//
//  ViewController.m
//  t-boardpro
//
//  Created by GLB-254 on 5/4/15.
//  Copyright (c) 2015 SocioBoard. All rights reserved.
//

#import "ViewController.h"
#import "AppDelegate.h"
#import "FHSTwitterEngine.h"
#import "CustomMenuViewController.h"
#import "Feeds.h"
#import "UnfollowViewController.h"
#import "SchedulingView.h"
#import "ProfileUserViewcontrollerViewController.h"
#import "Singleton.h"
#import <sqlite3.h>
#import "Reachability.h"
#import "TwitterHelperClass.h"
#import "SearchingViewController.h"
@interface ViewController ()

@end

@implementation ViewController
-(void)viewDidAppear:(BOOL)animated
{
    NSString * checkAlreadyLogin=[[NSUserDefaults standardUserDefaults]objectForKey:@"MainUserLogin"];
    
    if([checkAlreadyLogin isEqualToString:@"NoUser"])
    {
        themeImage=[[UIImageView alloc]init];
        themeImage.frame=CGRectMake(0, 0,SCREEN_WIDTH,SCREEN_HEIGHT);
        themeImage.image=[UIImage imageNamed:@"main_view.png"];
        themeImage.userInteractionEnabled=YES;
        [self.view addSubview:themeImage];
        twitHelperObj=[[TwitterHelperClass alloc]init];
        [self uiOfMainPage];
        
    }
    else
    {
        if([Singleton sharedSingleton].localNotificatonDict)
        {
            [self showPopOfSchedule];
        }
        else
        {
            [self loadTheUserAlreadyLogin];
        }
    }
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(twitterLogin) name:@"TwitterLogin" object:nil];
    //Register Twitter App.
    [[FHSTwitterEngine sharedEngine]permanentlySetConsumerKey:TwiterConsumerKey andSecret:TwitterSecretKey];
    [[FHSTwitterEngine sharedEngine] setDelegate:self];
    
    //Code for Auto Login, Check Previous User
    NSString * checkAlreadyLogin=[[NSUserDefaults standardUserDefaults]objectForKey:@"MainUserLogin"];
    if([checkAlreadyLogin isEqualToString:@"NoUser"])
    {
        //For Local Notification open by app
        if([Singleton sharedSingleton].localNotificatonDict)
        {
            //For Local Notification open by app
            
            [self showPopOfSchedule];
        }
        else
        {
            themeImage=[[UIImageView alloc]init];
            themeImage.frame=CGRectMake(0, 0,SCREEN_WIDTH,SCREEN_HEIGHT);
            themeImage.image=[UIImage imageNamed:@"main_view.png"];
            themeImage.userInteractionEnabled=YES;
            [self.view addSubview:themeImage];
            twitHelperObj=[[TwitterHelperClass alloc]init];
            //Ui Method for View
            [self uiOfMainPage];
        }
    }
    else
    {
        
    }
}
-(void)uiOfMainPage
{
    
    UIButton * addHome=[[UIButton alloc]init];
    addHome.frame=CGRectMake(self.view.frame.size.width/2-105,self.view.frame.size.height/2+140,215, 41);
    addHome.layer.borderWidth=1;
    addHome.layer.borderColor=[UIColor whiteColor].CGColor;
    addHome.layer.cornerRadius=5;
    addHome.clipsToBounds=YES;
    [addHome setBackgroundImage:[UIImage imageNamed:@"connect_with_twitter.png"] forState:UIControlStateNormal];
    [addHome addTarget:self action:@selector(twitterLogin) forControlEvents:UIControlEventTouchUpInside];
    [themeImage addSubview:addHome];
}
-(void)twitterLogin
{
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:@"Twitter Name"];
    NSLog(@"Twitter Name %@",tempArray);
    if([Singleton networkCheck])
    {
        
        if(tempArray.count==0)
        {
            UIViewController *loginController = [[FHSTwitterEngine sharedEngine]loginControllerWithCompletionHandler:^(BOOL success) {
                
                [self performSelector:@selector(loginCheckComplete) withObject:nil afterDelay:1];
                NSLog(success?@"L0L success":@"O noes!!! Loggen faylur!!!");
            }];
            [self presentViewController:loginController animated:YES completion:nil];
            
        }
        else
        {
            [self addingActionSheet];
            
        }
        
    }
    else
    {
        UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [noInternet show];
    }
}
-(void)showPopOfSchedule
{
    viewSchedule=[[UIView alloc]initWithFrame:[UIScreen mainScreen].bounds];
    viewSchedule.frame=CGRectMake(20,110, SCREEN_WIDTH-40, 200);
    viewSchedule.backgroundColor=[UIColor whiteColor];
    viewSchedule.layer.borderColor=[UIColor blackColor].CGColor;
    viewSchedule.layer.borderWidth=1;
    UIBezierPath *shadowPath = [UIBezierPath bezierPathWithRect:viewSchedule.bounds];
    viewSchedule.layer.masksToBounds = NO;
    viewSchedule.layer.shadowColor = [UIColor blackColor].CGColor;
    viewSchedule.layer.shadowOffset = CGSizeMake(0.0f, 5.0f);
    viewSchedule.layer.shadowOpacity = 0.5f;
    viewSchedule.layer.shadowPath = shadowPath.CGPath;
    [self.view addSubview:viewSchedule];
    
    UILabel * lblMessage=[[UILabel alloc]init];
    lblMessage.frame=CGRectMake(0,50,viewSchedule.frame.size.width,40);
    lblMessage.textColor=ThemeColor;
    lblMessage.font=[UIFont boldSystemFontOfSize:15];
    lblMessage.textAlignment=NSTextAlignmentCenter;
    lblMessage.text=@"It's time to Schedule";
    [viewSchedule addSubview:lblMessage];
    
    UIButton *cancelBtn=[[UIButton alloc]init];
    cancelBtn.frame=CGRectMake(30,viewSchedule.frame.size.height-60,80,40);
    [cancelBtn setTitle:@"Cancel" forState:UIControlStateNormal];
    [cancelBtn setBackgroundImage:[UIImage imageNamed:@"cancel.png"] forState:UIControlStateNormal];
    [cancelBtn addTarget:self action:@selector(cancelSchedule) forControlEvents:UIControlEventTouchUpInside];
    [viewSchedule addSubview:cancelBtn];
    
    UIButton *acceptBtn=[[UIButton alloc]init];
    acceptBtn.frame=CGRectMake(viewSchedule.frame.size.width-110,viewSchedule.frame.size.height-60, 80,40);
    [acceptBtn addTarget:self action:@selector(postSchedule:) forControlEvents:UIControlEventTouchUpInside];
    [cancelBtn setBackgroundImage:[UIImage imageNamed:@"post.png"] forState:UIControlStateNormal];
    acceptBtn.backgroundColor=[UIColor redColor];
    [viewSchedule addSubview:acceptBtn];
}
-(void)postSchedule:(id)sender
{
    NSDictionary * dict=[Singleton sharedSingleton].localNotificatonDict;
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    long date = [[dict objectForKey:@"TimeStamp"] intValue];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:date];
    [self loadTheUserAlreadyLogin];
}
-(void)cancelSchedule
{
    //Cancel the Schedule
    [viewSchedule removeFromSuperview];
    [self loadTheUserAlreadyLogin];
}
-(NSString *) loadAccessToken
{
    NSLog(@"Access Token = =%@",[[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"]);
    
    return [[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"];
}

-(void) storeAccessToken:(NSString *)accessToken
{
    //Current AccessToken get
    [[NSUserDefaults standardUserDefaults]setObject:accessToken forKey:@"SavedAccessHTTPBody"];
    [Singleton sharedSingleton].accessTokenCurrent=accessToken;
    [self retriveAcesssTokenandDetail:accessToken];
}
-(void)retriveAcesssTokenandDetail:(NSString *)detail
{
    [Singleton sharedSingleton].newAccountAdded=TRUE;
    NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"];
    NSRange r1 = [accessToken rangeOfString:@"&user_id="];
    NSRange r2 = [accessToken rangeOfString:@"&screen_name="];
    NSRange rSub = NSMakeRange(r1.location + r1.length, r2.location - r1.location - r1.length);
    NSString *sub = [accessToken substringWithRange:rSub];
    [Singleton sharedSingleton].currectUserTwitterId=sub;
    NSLog(@"sub %@",sub);
    NSArray * arrayTemp=[accessToken componentsSeparatedByString:@"screen_name="];
    NSLog(@"username %@",[arrayTemp objectAtIndex:1]);
    [Singleton sharedSingleton].userTwitterScreenName=[arrayTemp objectAtIndex:1];
    //main user in singleton
    [Singleton sharedSingleton].mainUser=[arrayTemp objectAtIndex:1];
    [twitHelperObj fetchUserNameAndImage:[Singleton sharedSingleton].userTwitterScreenName];
    [[NSUserDefaults standardUserDefaults]setObject:accessToken  forKey:[Singleton sharedSingleton].currectUserTwitterId];
    
}
-(void)loginCheckComplete
{
    [[FHSTwitterEngine sharedEngine]loadAccessToken];
    [[NSUserDefaults standardUserDefaults]setObject:[Singleton sharedSingleton].mainUser forKey:@"MainUserLogin"];
    CustomMenuViewController * mainMenu=[ViewController goTOHomeView];
    [self presentViewController:mainMenu animated:YES completion:nil];
    
}
+(CustomMenuViewController*)goTOHomeView
{
    
    Feeds *follow = [[Feeds alloc]init];
    follow.title=@"Feeds";
    NSLog(@"Title =- %@",follow.title);
    UnfollowViewController *unfollow = [[UnfollowViewController alloc] init];
    unfollow.title = @"All following";//[ViewController languageSelectedStringForKey:@"Topic"];
    
    ProfileUserViewcontrollerViewController *profile = [[ProfileUserViewcontrollerViewController alloc] initWithNibName:@"ProfileUserViewcontrollerViewController" bundle:nil];
    profile.title=@"Profile";
    
    UINavigationController *followNavi = [[UINavigationController alloc] initWithRootViewController:follow];
    followNavi.navigationBar.hidden = YES;
    SearchingViewController * searchObj=[[SearchingViewController alloc]initWithNibName:@"SearchingViewController" bundle:nil];
    searchObj.title=@"Copy Followers";
    UINavigationController *unfollowNavi = [[UINavigationController alloc] initWithRootViewController:unfollow];
    unfollowNavi.navigationBar.hidden = YES;
    
    SchedulingView * schView=[[SchedulingView alloc]initWithNibName:@"SchedulingView" bundle:nil];
    schView.title=@"Takeoff";
    UINavigationController *scheduleNavi = [[UINavigationController alloc] initWithRootViewController:schView];
    scheduleNavi.navigationBar.hidden = YES;
    
    CustomMenuViewController *customMenuView =[[CustomMenuViewController alloc] init];
    customMenuView.numberOfSections = 1;
    customMenuView.viewControllers = @[followNavi,unfollowNavi,profile,searchObj,scheduleNavi];
    
    return customMenuView;
}
#pragma mark--------
-(void)addingActionSheet
{
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:@"Twitter Name"];
    NSLog(@"Twitter Name %@",tempArray);
    if([tempArray count]>=1)
    {
        loginOptions = [[UIActionSheet alloc] initWithTitle:@"Select a twitter account" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:nil];
        for (int i=0; i<[tempArray count]; i++)
        {
            [loginOptions addButtonWithTitle:[tempArray objectAtIndex:i]];
        }
        [loginOptions addButtonWithTitle:@"Other Account"];
    }
    
    [loginOptions showInView:self.view];
}
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:@"Twitter Name"];
    NSLog(@"Twitter Name %@",tempArray);
    NSLog(@"No of buttons %ld",(long)[actionSheet numberOfButtons]);
    if(buttonIndex==[tempArray count]+1)
    {
        UIViewController *loginController = [[FHSTwitterEngine sharedEngine]loginControllerWithCompletionHandler:^(BOOL success) {
            
            [self performSelector:@selector(loginCheckComplete) withObject:nil afterDelay:1];
            NSLog(success?@"L0L success":@"O noes!!! Loggen faylur!!!");
        }];
        [self presentViewController:loginController animated:YES completion:nil];
    }
    else if(buttonIndex>0)
    {
        [Singleton sharedSingleton].mainUser=[tempArray objectAtIndex:buttonIndex-1];
        [self retriveTwitterSqlite];
    }
}

-(void)loadTheUserAlreadyLogin
{
    [Singleton sharedSingleton].mainUser=[[NSUserDefaults standardUserDefaults]objectForKey:@"MainUserLogin"];
    [self retriveTwitterSqlite];
}
-(void)retriveTwitterSqlite
{
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from TwitterData where UserScreenName='%@'",[Singleton sharedSingleton].mainUser];
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
            char *accessToken = (char *) sqlite3_column_text(stmt,1);
            char *twitterId = (char *) sqlite3_column_text(stmt,2);
            NSString *straccessToken=[NSString stringWithUTF8String:accessToken];
            [[NSUserDefaults standardUserDefaults]setObject:straccessToken  forKey:@"SavedAccessHTTPBody"];
            [Singleton sharedSingleton].currectUserTwitterId=[NSString stringWithUTF8String:twitterId];
            [Singleton sharedSingleton].accessTokenCurrent=straccessToken;
            [[FHSTwitterEngine sharedEngine]loadAccessToken];
            
            
        }
        CustomMenuViewController * mainMenu=[ViewController goTOHomeView];
        [self presentViewController:mainMenu animated:YES completion:nil];
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

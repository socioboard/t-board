//
//  ViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "ViewController.h"
#import "AppDelegate.h"
#import "FHSTwitterEngine.h"
#import "CustomMenuViewControllerTboard.h"
#import "FeedsViewController.h"
#import "FollowingViewControllerTboard.h"
#import "FollowedByViewControllerTboard.h"
#import "SchedulingViewControllerTboard.h"
#import "TweetOnTimeLineTboard.h"
#import "SingletonTboard.h"
#import <sqlite3.h>
#import "Reachability.h"
#import "TwitterHelperClass.h"
#import "NonFollowerViewControllerTboard.h"
#import "UserOverlappingTboard.h"
#import "FansViewControllerTboard.h"
#import "MutualViewControllerTboard.h"
#import "SearchingViewControllerTboard.h"
#import "StatsViewControllerTboard.h"
@interface ViewController ()
{
    UIImageView * bgImagView;
}
@end

@implementation ViewController
-(void)viewDidAppear:(BOOL)animated
{
    NSString * checkAlreadyLogin=[[NSUserDefaults standardUserDefaults]objectForKey:MainuserLogin];

    if([checkAlreadyLogin isEqualToString:@"NoUser"])
    {
            twitHelperObj=[[TwitterHelperClass alloc]init];
        [self uiOfMainPage];

    }
    else
    {
        if([SingletonTboard sharedSingleton].localNotificatonDict)
        {
            [self showPopOfSchedule];
        }
        else
        {
//            [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
           
            [self loadTheUserAlreadyLogin];
        }
    }
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(twitterLogin) name:@"TwitterLogin" object:nil];
    [[FHSTwitterEngine sharedEngine]permanentlySetConsumerKey:TwiterConsumerKey andSecret:TwitterSecretKey];
    [[FHSTwitterEngine sharedEngine] setDelegate:self];
    self.view.backgroundColor=[UIColor whiteColor];
    // Do any additional setup after loading the view, typically from a nib.
    UIView * headerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 40)];
    headerView.backgroundColor=[UIColor colorWithRed:(CGFloat)0/255 green:(CGFloat)177/255 blue:(CGFloat)255/255 alpha:1];
    [self.view addSubview:headerView];

    NSString * checkAlreadyLogin=[[NSUserDefaults standardUserDefaults]objectForKey:MainuserLogin];
    if([checkAlreadyLogin isEqualToString:@"NoUser"])
    {
    if([SingletonTboard sharedSingleton].localNotificatonDict)
    {
        [self showPopOfSchedule];
    }
    else
    {
    twitHelperObj=[[TwitterHelperClass alloc]init];
    [self uiOfMainPage];
    }
    }
    else
    {
    bgImagView=[[UIImageView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    bgImagView.image=[UIImage imageNamed:@"LaunchImage320*480.png"];
    [self.view addSubview:bgImagView];
    }
}
#pragma mark ===============================
#pragma mark Scroll view Methods
#pragma mark ===============================

-(void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    
     CGFloat viewWidth=scrollView.contentOffset.x;
    float pageNo=viewWidth/SCREEN_WIDTH;;
    
        if(pageController)
        {
            pageController.currentPage = pageNo;
        }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
}

-(void)uiOfMainPage
{
    UIImageView * twtLogo=[[UIImageView alloc]init];
    twtLogo.frame=CGRectMake(20, 100, SCREEN_WIDTH-40, 80);
    twtLogo.image=[UIImage imageNamed:@"transparent_logo.png"];
    [self.view addSubview:twtLogo];
    
    UIButton * addHome=[[UIButton alloc]init];
    addHome.frame=CGRectMake(self.view.frame.size.width/2-105,self.view.frame.size.height-80,215, 41);
    addHome.layer.borderWidth=1;
    addHome.layer.borderColor=[UIColor whiteColor].CGColor;
    addHome.layer.cornerRadius=5;
    addHome.clipsToBounds=YES;
    [addHome setBackgroundImage:[UIImage imageNamed:@"connect_with_twitter_new.png"] forState:UIControlStateNormal];
    [addHome addTarget:self action:@selector(twitterLogin) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:addHome];
}
#pragma mark 

-(void)addPaging
{
    // Scroll View
    
    scrollViewPaging = [[UIScrollView alloc]initWithFrame:CGRectMake(0, 0,SCREEN_WIDTH,SCREEN_HEIGHT-120)];
    scrollViewPaging.backgroundColor=[UIColor clearColor];
    scrollViewPaging.delegate=self;
    scrollViewPaging.pagingEnabled=YES;
    [scrollViewPaging setContentSize:CGSizeMake(scrollViewPaging.frame.size.width*3, scrollViewPaging.frame.size.height)];
    
    // page control
    pageController = [[UIPageControl alloc]initWithFrame:CGRectMake(0, SCREEN_HEIGHT-120, 320, 36)];
    pageController.backgroundColor=[UIColor clearColor];
    pageController.numberOfPages=3;
    [pageController addTarget:self action:@selector(pageChanged) forControlEvents:UIControlEventValueChanged];
    
    CGFloat x=0;
    for(int i=1;i<4;i++)
    {
        UIImageView *image = [[UIImageView alloc] initWithFrame:CGRectMake(x+30,20,SCREEN_WIDTH-60,SCREEN_HEIGHT-140)];
        [image setImage:
         [self imageWithImage:[UIImage imageNamed:[NSString stringWithFormat:@"SS%d",i]] scaledToSize:CGSizeMake(SCREEN_WIDTH-60, SCREEN_HEIGHT-140)]];
        
        [scrollViewPaging addSubview:image];
        
        x+=SCREEN_WIDTH;
    }
    [self.view addSubview:scrollViewPaging];
    
    [self.view addSubview:pageController];
}
- (void)pageChanged
{
    int pageNumber = pageController.currentPage;
    CGRect frame=scrollViewPaging.frame;
    frame.origin.x = frame.size.width*pageNumber;
    frame.origin.y=0;
    [scrollViewPaging scrollRectToVisible:frame animated:YES];
}
#pragma mark login methods
-(void)twitterLogin
{
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:TwitterUserScreenName];
    NSLog(@"Twitter Name %@",tempArray);
    if([SingletonTboard networkCheck])
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
-(NSString *) loadAccessToken
{
    NSLog(@"Access Token = =%@",[[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"]);
    
    return [[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"];
}

-(void) storeAccessToken:(NSString *)accessToken
{
    //Current AccessToken get
    [[NSUserDefaults standardUserDefaults]setObject:accessToken forKey:@"SavedAccessHTTPBody"];
    [SingletonTboard sharedSingleton].accessTokenCurrent=accessToken;
    [self retriveAcesssTokenandDetail:accessToken];
}
-(void)retriveAcesssTokenandDetail:(NSString *)detail
{
    
    NSString * accessToken=[[NSUserDefaults standardUserDefaults]objectForKey:@"SavedAccessHTTPBody"];
    NSRange r1 = [accessToken rangeOfString:@"&user_id="];
    NSRange r2 = [accessToken rangeOfString:@"&screen_name="];
    NSRange rSub = NSMakeRange(r1.location + r1.length, r2.location - r1.location - r1.length);
    NSString *sub = [accessToken substringWithRange:rSub];
    [SingletonTboard sharedSingleton].currectUserTwitterId=sub;
    NSLog(@"sub %@",sub);
    r1 = [accessToken rangeOfString:@"&screen_name="];
    r2 = [accessToken rangeOfString:@"&x_auth_expires"];
    rSub = NSMakeRange(r1.location + r1.length, r2.location - r1.location - r1.length);
    sub = [accessToken substringWithRange:rSub];
    
    //main user in SingletonTboard
    [SingletonTboard sharedSingleton].mainUser=sub;
    twitHelperObj=[[TwitterHelperClass alloc]init];
    [twitHelperObj fetchUserNameAndImage:[SingletonTboard sharedSingleton].mainUser];
    [[NSUserDefaults standardUserDefaults]setObject:accessToken  forKey:[SingletonTboard sharedSingleton].currectUserTwitterId];
    
}
-(void)loginCheckComplete
{
    [[FHSTwitterEngine sharedEngine]loadAccessToken];
    [[NSUserDefaults standardUserDefaults]setObject:[SingletonTboard sharedSingleton].mainUser forKey:MainuserLogin];
    [[SingletonTboard sharedSingleton] fetchListOfFollow_Unfollow];
    CustomMenuViewControllerTboard * mainMenu=[ViewController goTOHomeView];
    [self presentViewController:mainMenu animated:YES completion:nil];
    NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    for (NSHTTPCookie *cookie in [storage cookies])
    {
        [storage deleteCookie:cookie];
    }
    [[NSURLCache sharedURLCache] removeAllCachedResponses];

}
#pragma mark Schedule
-(void)postSchedule:(id)sender
{
    NSDictionary * dict=[SingletonTboard sharedSingleton].localNotificatonDict;
    TwitterHelperClass * twitterHelper=[[TwitterHelperClass alloc]init];
    [twitterHelper retriveAndScheduleSqlite:[dict objectForKey:@"Text"] date:[dict objectForKey:@"TimeStamp"]];
    [self loadTheUserAlreadyLogin];
}
-(void)showPopOfSchedule
{
    UIView * viewSchedule=[[UIView alloc]initWithFrame:[UIScreen mainScreen].bounds];
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

-(void)cancelSchedule
{
    
}
+(CustomMenuViewControllerTboard*)goTOHomeView
{
    TweetOnTimeLineTboard * tweetOntimeline=[[TweetOnTimeLineTboard alloc]initWithNibName:@"TweetOnTimeLine" bundle:nil];
    tweetOntimeline.title=@"Tweet";
    
    FeedsViewController *feedView = [[FeedsViewController alloc]init];
    feedView.title=@"Feeds";
    NSLog(@"Title =- %@",feedView.title);
    
    FollowingViewControllerTboard *unfollow = [[FollowingViewControllerTboard alloc] init];
    unfollow.title = @"Following";//[ViewController languageSelectedStringForKey:@"Topic"];
    
    FollowedByViewControllerTboard * followerUser = [[FollowedByViewControllerTboard alloc]init];
    followerUser.title=@"Followed By";
    NSLog(@"Title =- %@",followerUser.title);
    
    SearchingViewControllerTboard * searchObj=[[SearchingViewControllerTboard alloc]initWithNibName:@"SearchingViewController" bundle:nil];
    searchObj.title=@"Search";
    
    FansViewControllerTboard * fanView=[[FansViewControllerTboard alloc]init];
    fanView.title=@"Fans";
    
    UINavigationController *unfollowNavi = [[UINavigationController alloc] initWithRootViewController:unfollow];
    unfollowNavi.navigationBar.hidden = YES;
    
    MutualViewControllerTboard * mutualFollower=[[MutualViewControllerTboard alloc]init];
    mutualFollower.title=@"Mutual Follower";
    

    NonFollowerViewControllerTboard * nonFollower=[[NonFollowerViewControllerTboard alloc]initWithNibName:@"NonFollowerViewController" bundle:nil];
    nonFollower.title=@"Non Follower";
   
//    StatsViewControllerTboard * statsObj=[[StatsViewControllerTboard alloc]init];
//    statsObj.title=@"Statistics";
//    
    UINavigationController *feedNavi = [[UINavigationController alloc] initWithRootViewController:feedView];
    feedNavi.navigationBar.hidden = YES;
   
    UserOverlappingTboard * userOverlap=[[UserOverlappingTboard alloc]initWithNibName:@"UserOverlapping" bundle:nil];
    userOverlap.title=@"User Overlapping";
    
    SchedulingViewControllerTboard * schView=[[SchedulingViewControllerTboard alloc]initWithNibName:@"SchedulingView" bundle:nil];
    schView.title=@"Schedule";
    UINavigationController *scheduleNavi = [[UINavigationController alloc] initWithRootViewController:schView];
    scheduleNavi.navigationBar.hidden = YES;
  
    
    CustomMenuViewControllerTboard *customMenuView =[[CustomMenuViewControllerTboard alloc] init];
    customMenuView.numberOfSections = 1;
    customMenuView.viewControllers = @[tweetOntimeline,feedNavi, unfollowNavi,followerUser,searchObj,fanView,mutualFollower,nonFollower,userOverlap,scheduleNavi];
    
    return customMenuView;
}
#pragma mark--------
-(void)addingActionSheet
{
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:TwitterUserScreenName];
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
    NSArray * tempArray=[[NSUserDefaults standardUserDefaults]objectForKey:TwitterUserScreenName];
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
        [SingletonTboard sharedSingleton].mainUser=[tempArray objectAtIndex:buttonIndex-1];
                [self retriveTwitterSqlite];
    }
}

-(void)loadTheUserAlreadyLogin
{
    [SingletonTboard sharedSingleton].mainUser=[[NSUserDefaults standardUserDefaults]objectForKey:MainuserLogin];
    [self retriveTwitterSqlite];
}
-(void)retriveTwitterSqlite
{
    
            NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
            
            NSLog(@"%@",paths);
            NSString *documentsDirectory = [paths objectAtIndex:0];
            NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
            // Check to see if the database file already exists
            NSString *query = [NSString stringWithFormat:@"select * from TwitterData where TwitterId='%@'",[SingletonTboard sharedSingleton].mainUser];
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
                   // char *screenName=(char*)sqlite3_column_text(stmt,3);
                    NSString *straccessToken=[NSString stringWithUTF8String:accessToken];
                    [[NSUserDefaults standardUserDefaults]setObject:straccessToken  forKey:@"SavedAccessHTTPBody"];
                    [SingletonTboard sharedSingleton].currectUserTwitterId=[NSString stringWithUTF8String:twitterId];
                    [SingletonTboard sharedSingleton].accessTokenCurrent=straccessToken;
                    
                    [[FHSTwitterEngine sharedEngine]loadAccessToken];
                   

                }
                [NSThread detachNewThreadSelector:@selector(fetchUserDataFromTwiiter) toTarget:self withObject:nil];
                
                NSLog(@"main user %@",[SingletonTboard sharedSingleton].mainUser);
                CustomMenuViewControllerTboard * mainMenu=[ViewController goTOHomeView];
                [bgImagView removeFromSuperview];
                [self presentViewController:mainMenu animated:YES completion:nil];
               
               
            }
            @catch(NSException *e)
            {
                NSLog(@"%@",e);
            }
}
-(void)fetchUserDataFromTwiiter
{
    id returnData=[[FHSTwitterEngine sharedEngine]getProfileDetail:[SingletonTboard sharedSingleton].mainUser andSize:FHSTwitterEngineImageSizeNormal];
    if([returnData isKindOfClass:[NSError class]])
    {
        
    }
    else
    {
        NSLog(@"returned data %@",returnData);
        [SingletonTboard sharedSingleton].tweetCount=[returnData objectForKey:@"statuses_count"];
        [SingletonTboard sharedSingleton].followerCount=[returnData objectForKey:@"followers_count"];
        [SingletonTboard sharedSingleton].imageUrl=[returnData objectForKey:@"profile_image_url_https"];
        [SingletonTboard sharedSingleton].followingCount=[returnData objectForKey:@"friends_count"];
        [SingletonTboard sharedSingleton].mainUser=[NSString stringWithFormat:@"%@",[returnData objectForKey:@"screen_name"]];
        [SingletonTboard sharedSingleton].bannerImageUrl=[returnData objectForKey:@"profile_banner_url"];
        [[SingletonTboard sharedSingleton] fetchListOfFollow_Unfollow];
  
    }
  
}
#pragma mark Resize Images
- (UIImage *)imageWithImage:(UIImage *)image scaledToSize:(CGSize)newSize {
    //UIGraphicsBeginImageContext(newSize);
    // In next line, pass 0.0 to use the current device's pixel scaling factor (and thus account for Retina resolution).
    // Pass 1.0 to force exact pixel size.
    UIGraphicsBeginImageContextWithOptions(newSize, NO, 0.0);
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return newImage;
}
#pragma mark -
#pragma mark - Loading View mbprogresshud

-(void) showHUDLoadingView:(NSString *)strTitle
{
    HUD = [[MBProgressHUD alloc] init];
    [self.view addSubview:HUD];
    //HUD.delegate = self;
    //HUD.labelText = [strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    HUD.detailsLabelText=[strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    [HUD show:YES];
}

-(void) hideHUDLoadingView
{
    [HUD removeFromSuperview];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

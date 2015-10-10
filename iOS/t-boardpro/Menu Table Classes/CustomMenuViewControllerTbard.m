
//
//  CustomMenuViewController.m
//  MOVYT
//
//  Created by Sumit Ghosh on 27/05/14.
//  Copyright (c) 2014 Sumit Ghosh. All rights reserved.
//

#import "CustomMenuViewControllerTboard.h"
#import <objc/runtime.h>
#import "ViewController.h"
#import "AppDelegate.h"
#import "TableCustomCell.h"
#import "SingletonTboard.h"
#import "SDWebImage/UIImageView+WebCache.h"
#import "FollowedByViewControllerTboard.h"
#import "FHSTwitterEngine.h"
#import <sqlite3.h>
@interface CustomMenuViewControllerTboard ()
{
    NSInteger updateValue;
}
@property (nonatomic,strong)UITabBar *customTabBar;
@end

@implementation CustomMenuViewControllerTboard
@synthesize viewControllers = _viewControllers;




#pragma mark -
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
- (BOOL)prefersStatusBarHidden {
    return YES;
}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
//    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
       [self.menuTableView reloadData];
}
//Received Notification Method
-(void) reloadMenuTable:(NSNotification *)notify{
    
    id name = [notify object];
    if ([name isKindOfClass:[NSString class]]) {
        
        if ([name isEqualToString:@"LoggedInWithBroadCast"]){
            self.isSignIn = YES;
            
        }
        else if ([name isEqualToString:@"LoggedIn"]){
            self.isSignIn = YES;
        }
        [self.menuTableView reloadData];
    }
}

#pragma mark -
-(void) setViewControllers:(NSArray *)viewControllers
{
    
    _viewControllers = [viewControllers copy];
    
    for (UIViewController *viewController in _viewControllers ) {
        [self addChildViewController:viewController];
        
        viewController.view.frame = CGRectMake(0, 90,[UIScreen mainScreen].bounds.size.width ,[UIScreen mainScreen].bounds.size.height);
        [viewController didMoveToParentViewController:self];
    }
}
-(void) setSecondSectionViewControllers:(NSArray *)secondSectionViewControllers{
    
    _secondSectionViewControllers = [secondSectionViewControllers copy];
    
    for (UIViewController *viewController in _secondSectionViewControllers ) {
        [self addChildViewController:viewController];
        
        viewController.view.frame = CGRectMake(0, 90,[UIScreen mainScreen].bounds.size.width , [UIScreen mainScreen].bounds.size.height);
        [viewController didMoveToParentViewController:self];
    }
}
-(void) setSelectedViewController:(UIViewController *)selectedViewController{
    _selectedViewController = selectedViewController;
}

-(void) setSelectedIndex:(NSInteger)selectedIndex{
    _selectedIndex = selectedIndex;
}

-(NSArray *) getAllViewControllers{
    return self.viewControllers;
}
-(void) setSelectedSection:(NSInteger)selectedSection{
    _selectedSection = selectedSection;
}
#pragma mark -
- (void)viewDidLoad
{
    [super viewDidLoad];
    menuTableImages=[NSArray arrayWithObjects:@"tweet.png",@"home_feeds_icon.png",@"friends_icon.png",@"group.png",@"tagsearch.png",@"fans.png",@"mutualfollwers.png",@"non_followers.png",@"overlappingfollowers.png",@"schedule_icon.png",nil];
    //-------
    buttonTitleAccTable=[NSArray arrayWithObjects:@"Add Accounts",@"Feedback",nil];
    //--------
    accImageArray=[NSArray arrayWithObjects:@"add_acount_icon.png",@"feed_back_icon.png",nil];
    //----------
    accountTableArray=[[NSMutableArray alloc]init];
    userTwitterId=[[NSMutableArray alloc]init];
    //-------
    screenSize = [UIScreen mainScreen].bounds;
    
    userDefault = [NSUserDefaults standardUserDefaults];
    //----
    self.profileView=[[ProfileViewControllerTboard alloc]init];
    self.profileView.title=@"Profile";
    //----
    // Do any additional setup after loading the view.
  self.view.backgroundColor = [UIColor whiteColor];
    
    self.screen_height = [UIScreen mainScreen].bounds.size.height;
    self.isSignIn = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadMenuTable:) name:@"UpdateMenuTable" object:nil];
    
    //Add View SubView;
    self.mainsubView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, self.screen_height)];
    NSLog(@"Main sub view frame X=-=- %f \n Y == %f",[UIScreen mainScreen].bounds.origin.x,[UIScreen mainScreen].bounds.origin.y);
    self.mainsubView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.mainsubView];
    
    //Add Header View
    CGFloat hh;
    CGRect frame_b;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)
    {
        hh = 75;
        frame_b = CGRectMake(680, 30,30,21);
        
    }
    else
    {
        hh = 55;
        
        frame_b = CGRectMake(20, 20,30,21);
//        if(IS_IPHONE_6)
//        {
//            frame_b = CGRectMake(310, 20, 45, 25);
//        }
//        else if (IS_IPHONE_6P)
//        {
//            frame_b = CGRectMake(349, 20, 45, 25);
//
//        }
    }
    CGRect frame = CGRectMake(0, 0, screenSize.size.width, hh);
    
    self.headerView = [[UIView alloc] initWithFrame:frame];
    self.headerView.backgroundColor =ThemeColor;
    [self.mainsubView addSubview:self.headerView];
    
    NSLog(@"Width menu== %f",screenSize.size.width);
    self.userImageOnTop=[[UIImageView alloc]initWithFrame:CGRectMake(screenSize.size.width-60,5,44,44)];
    self.userImageOnTop.clipsToBounds=YES;
    self.userImageOnTop.image=[UIImage imageNamed:@"ic_menu_friendslist.png"];
    self.userImageOnTop.userInteractionEnabled=YES;
    UITapGestureRecognizer * tapOnImage=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(openAccountTabel:)];
    [self.userImageOnTop addGestureRecognizer:tapOnImage];
    [self.headerView addSubview:self.userImageOnTop];
    //=======================================
    // Add Container View
    frame = CGRectMake(0,55, screenSize.size.width, screenSize.size.height);
    self.contentContainerView = [[UIView alloc] initWithFrame:frame];
    self.contentContainerView.backgroundColor = [UIColor whiteColor];
    self.contentContainerView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
    [self.mainsubView addSubview:self.contentContainerView];
    //------------------
    
    self.menuButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.menuButton.frame = frame_b;
    self.menuButton.titleLabel.font = [UIFont systemFontOfSize:9.0f];
    self.menuButton.titleLabel.shadowOffset = CGSizeMake(0.0f, 0.0f);
    
    //self.menuButton.titleLabel.layer.
    [self.menuButton addTarget:self action:@selector(menuButtonClciked:) forControlEvents:UIControlEventTouchUpInside];
    [self.menuButton setBackgroundImage:[UIImage imageNamed:@"menu.png"] forState:UIControlStateNormal];
    
    [self.headerView addSubview:self.menuButton];
    
    //===================================
    
    //Add Menu Lable
    self.menuLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 15,SCREEN_WIDTH, 30)];
    self.menuLabel.backgroundColor = [UIColor clearColor];
    self.menuLabel.font = [UIFont fontWithName:@"HelveticaNeue-Bold" size:20];
    self.menuLabel.textColor = [UIColor whiteColor];
    self.menuLabel.textAlignment = NSTextAlignmentCenter;
    self.menuLabel.text = _selectedViewController.title;
    [self.headerView addSubview:self.menuLabel];
    
    //====================================
    self.selectedIndex = 0;
    self.selectedViewController = [_viewControllers objectAtIndex:1];
    [self updateViewContainer];
    //left and right tabel
    [self createMenuTableView];
    //Adding Swipr Gesture
    self.swipeGestureLeft = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleSwipeGestureLeft:)];
    self.swipeGestureLeft.direction = UISwipeGestureRecognizerDirectionLeft;
    [self.mainsubView addGestureRecognizer:self.swipeGestureLeft];
          //===============
    self.swipeGestureRight = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleSwipeGestureRight:)];
    self.swipeGestureRight.direction = UISwipeGestureRecognizerDirectionRight;
    [self.mainsubView addGestureRecognizer:self.swipeGestureRight];
    //-------------
    //[self fetchuserData];
    [self createAccountTable];
    
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(retriveTwitterSqlite) name:@"ReloadAccountTable_NewAccount" object:nil];
    [self retriveTwitterSqlite];

}
#pragma mark -
#pragma mark -Create Table
-(void)createAccountTable
{
    
        self.selectedIndex = 0;
        self.accountTableView = [[UITableView alloc] initWithFrame:CGRectMake(screenSize.size.width-220,0,220, self.screen_height) style:UITableViewStylePlain];
        
        self.accountTableView.backgroundColor = [UIColor whiteColor];
        
        self.accountTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.accountTableView.delegate = self;
        self.accountTableView.dataSource = self;
        
    [self.view insertSubview:self.accountTableView belowSubview:self.mainsubView];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,220, 80);
    self.accountTableView.tableFooterView=footerView;
    //-----
     headerImage=[[UIImageView alloc]init];
    headerImage.frame=CGRectMake(0,0,220,220);
    if([SingletonTboard sharedSingleton].bannerImageUrl)
    {
    [headerImage sd_setImageWithURL:[NSURL URLWithString:[SingletonTboard sharedSingleton].bannerImageUrl] placeholderImage:[UIImage imageNamed:@""]];
        headerImage.backgroundColor=[UIColor colorWithRed:(CGFloat)112/255 green:(CGFloat)213/255 blue:(CGFloat)240/255 alpha:1];
    }
    else
    {
        headerImage.image=[UIImage imageNamed:@""];
         headerImage.backgroundColor=ThemeColor;
    }
   // [self.view insertSubview:headerImage belowSubview:self.mainsubView];
    self.accountTableView.tableHeaderView=headerImage;
    ///--------------------
    profileImageAccTable=[[UIImageView alloc]init];
    profileImageAccTable.frame=CGRectMake(10, headerImage.frame.size.height-120,80, 80);
    profileImageAccTable.layer.cornerRadius=40;
    profileImageAccTable.clipsToBounds=YES;
    profileImageAccTable.image=[UIImage imageNamed:@"place_holder.png"];
    NSURL * profileImageUrl=[NSURL URLWithString:[SingletonTboard sharedSingleton].imageUrl];
    [profileImageAccTable sd_setImageWithURL:profileImageUrl placeholderImage:[UIImage imageNamed:@""]];
    [headerImage addSubview:profileImageAccTable];
    
    //---------------------
    nameLblAccTabel=[[UILabel alloc]init];
    nameLblAccTabel.frame=CGRectMake(10,headerImage.frame.size.height-40, 200, 20);
    nameLblAccTabel.text=[NSString stringWithFormat:@"%@",[SingletonTboard sharedSingleton].mainUser];
    //[nameLblAccTabel sizeToFit];
    nameLblAccTabel.textAlignment=NSTextAlignmentCenter;
    nameLblAccTabel.textColor=[UIColor whiteColor];
    nameLblAccTabel.font=[UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
    [headerImage addSubview:nameLblAccTabel];
    //------------------
   }
-(void)addAccount
{
     [SingletonTboard sharedSingleton].newAccountAdded=TRUE;
    UIViewController *loginController = [[FHSTwitterEngine sharedEngine]loginControllerWithCompletionHandler:^(BOOL success)
    {
       
        NSHTTPCookieStorage *storage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
        for (NSHTTPCookie *cookie in [storage cookies])
        {
            [storage deleteCookie:cookie];
        }
        [[NSURLCache sharedURLCache] removeAllCachedResponses];
        //------------------------------
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
       
        NSLog(success?@"L0L success":@"O noes!!! Loggen faylur!!!");
    }];
    [self presentViewController:loginController animated:YES completion:nil];
}
-(void)createSetting:(UIButton*)button
{
    accUserDictionary=[twitterCredsOfUser objectAtIndex:(int)button.tag];
    
    deleteUserPopUp=[[UIView alloc]initWithFrame:CGRectMake(10,SCREEN_HEIGHT/2-100, SCREEN_WIDTH-20,200)];
    deleteUserPopUp.backgroundColor=[UIColor whiteColor];
    deleteUserPopUp.layer.shadowColor=[UIColor blackColor].CGColor;
    deleteUserPopUp.layer.shadowOffset=CGSizeMake(0, 0);
    deleteUserPopUp.layer.shadowOpacity=.5;
    [self.view addSubview:deleteUserPopUp];
    
    UILabel * deleteMessage=[[UILabel alloc]init];
    deleteMessage.frame=CGRectMake(10, deleteUserPopUp.frame.size.height/2-40, deleteUserPopUp.frame.size.width-20,30);
    deleteMessage.text=@"Do You want to remove the account";
    [deleteUserPopUp addSubview:deleteMessage];
    
    UIButton * deleteButton=[[UIButton alloc]init];
    deleteButton.frame=CGRectMake(deleteUserPopUp.frame.size.width/2-90, deleteUserPopUp.frame.size.height-60,80, 40);
    deleteButton.backgroundColor=ThemeColor;
    [deleteButton setTitle:@"Remove" forState:UIControlStateNormal];
    [deleteButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [deleteButton addTarget:self action:@selector(deleteUserPopUp) forControlEvents:UIControlEventTouchUpInside];
    [deleteUserPopUp addSubview:deleteButton];
    
    UIButton * cancelButton=[[UIButton alloc]init];
    [cancelButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    cancelButton.frame=CGRectMake(deleteUserPopUp.frame.size.width/2+10,deleteUserPopUp.frame.size.height-60,80, 40);
    [cancelButton setTitle:@"Cancel" forState:UIControlStateNormal];
    cancelButton.backgroundColor=ThemeColor;
    [cancelButton addTarget:self action:@selector(cancelRemovePopUp) forControlEvents:UIControlEventTouchUpInside];
    [deleteUserPopUp addSubview:cancelButton];

}
-(void)deleteUserPopUp
{
    NSString * screeName=[accUserDictionary objectForKey:@"TwitterUserName"];
    TwitterHelperClass * twtHelper=[[TwitterHelperClass alloc]init];
    [twtHelper deleteUserFromSqlit:screeName];
    //------Removed From Nsuser Default and Update It.
   NSMutableArray * twitterUserScreenNames=[[NSMutableArray alloc]initWithArray:[[NSUserDefaults standardUserDefaults]objectForKey:TwitterUserScreenName]];
    [twitterUserScreenNames removeObject:screeName];
    NSArray * removedTwitterScreeName=[twitterUserScreenNames copy];
    [[NSUserDefaults standardUserDefaults]setObject:removedTwitterScreeName forKey:TwitterUserScreenName];
    //----------------Remove From Local Data
    NSMutableArray * twittterAccTableData=[[NSMutableArray alloc]initWithArray:twitterCredsOfUser];
    
    for (int i=0;i<twittterAccTableData.count;i++)
    {
        NSString * getScreeName=[[twittterAccTableData objectAtIndex:i] objectForKey:@"TwitterUserName"];
        if([getScreeName isEqualToString:screeName])
        {
            [twittterAccTableData removeObjectAtIndex:i];
        }
    }
    twitterCredsOfUser=[twittterAccTableData copy];
    //Load another user if present else log out
    if([screeName isEqualToString:[SingletonTboard sharedSingleton].mainUser])
    {
        if([removedTwitterScreeName count]>0)
        {
            //Load User Present in Array
            
            [self loadUserSelected:0];
        }
        else
        {
            [[NSUserDefaults standardUserDefaults]setObject:@"NoUser" forKey:MainuserLogin];
            [self dismissViewControllerAnimated:YES completion:nil];
        }
 
    }
    
    //UI Changes
    [deleteUserPopUp removeFromSuperview];
    [self.accountTableView reloadData];
    
}

-(void)cancelRemovePopUp
{
    [deleteUserPopUp removeFromSuperview];
}
-(void)loggedOut
{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"LogOutFromSetting" object:nil];
    [[NSUserDefaults standardUserDefaults]setObject:@"NoUser" forKey:MainuserLogin];
    [self dismissViewControllerAnimated:YES completion:nil];
}
-(void)createMenuTableView
{
    
    if (!self.menuTableView)
    {
        self.selectedIndex = 0;
        self.menuTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,0,SCREEN_WIDTH-50, self.screen_height) style:UITableViewStylePlain];
        self.menuTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.menuTableView.delegate = self;
        self.menuTableView.dataSource = self;
        self.menuTableView.backgroundColor=[UIColor whiteColor];
    }
    else
    {
        [self.menuTableView reloadData];
    }
    
    [self.view insertSubview:self.menuTableView belowSubview:self.mainsubView];
    
    //Add Table Header
    UIView * headerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0,SCREEN_WIDTH-150,80)];
    headerView.backgroundColor=ThemeColor;
    self.menuTableView.tableHeaderView=headerView;
    
    userImage=[[UIImageView alloc]init];
    userImage.image=[UIImage imageNamed:@"place_holder.png"];
    userImage.frame=CGRectMake(10, 15, 30,30);
    userImage.layer.cornerRadius=15;
    userImage.userInteractionEnabled=YES;
    userImage.clipsToBounds=YES;
    NSURL * imageUrl=[NSURL URLWithString:[SingletonTboard sharedSingleton].imageUrl];
    [userImage sd_setImageWithURL:imageUrl placeholderImage:[UIImage imageNamed:@""]];
    [headerView addSubview:userImage];
    //-------------
    UITapGestureRecognizer * tapOnUserImage=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(addProfileView)];
    [userImage addGestureRecognizer:tapOnUserImage];
    //------------
    userNameLbl=[[UILabel alloc]init];
    userNameLbl.frame=CGRectMake(60,15,200,25);
    userNameLbl.font=[UIFont boldSystemFontOfSize:20];
    userNameLbl.textColor=[UIColor whiteColor];
    userNameLbl.text= [SingletonTboard sharedSingleton].mainUserRealName;
    [headerView addSubview:userNameLbl];
    //------------
    userScreenNameLbl=[[UILabel alloc]init];
    userScreenNameLbl.text=[SingletonTboard sharedSingleton].mainUser;
    userScreenNameLbl.textColor=[UIColor whiteColor];
    userScreenNameLbl.font=[UIFont systemFontOfSize:15];
    userScreenNameLbl.frame=CGRectMake(60,40,200, 20);
    [headerView addSubview:userScreenNameLbl];
    ///-------------
    
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0, SCREEN_WIDTH-120,100);
    self.menuTableView.tableFooterView=footerView;
    UIImageView * logoImage=[[UIImageView alloc]init];
    logoImage.frame=CGRectMake(15,10,200, 34);
    logoImage.image=[UIImage imageNamed:@"tboardpro.png"];
    [footerView addSubview:logoImage];
}

#pragma mark -

-(void)handleSwipeGestureRight:(UISwipeGestureRecognizer *)swipeGesture
{
    self.mainsubView.backgroundColor=[UIColor whiteColor];
    if (self.mainsubView.frame.origin.x==0) {
        self.menuTableView.hidden=NO;
    [UIView animateWithDuration:.5 animations:^{
        self.mainsubView.frame = CGRectMake(SCREEN_WIDTH-50, 0,screenSize.size.width, screenSize.size.height);
        if(IS_IPHONE_5||IS_IPHONE_4_OR_LESS)
        {
            self.mainsubView.frame = CGRectMake(SCREEN_WIDTH-50, 0,screenSize.size.width, screenSize.size.height);
        }

        self.accountTableView.hidden=YES;
        addAccount.hidden=YES;
    }completion:^(BOOL finish){
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:@"Slide"];

    }];
    }
    else
    {

    [UIView animateWithDuration:.5 animations:^{
        self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height);
        
    }completion:^(BOOL finish){
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];

        self.swipeGestureRight.direction = UISwipeGestureRecognizerDirectionRight;
    }];
    }
}
-(void)handleSwipeGestureLeft:(UISwipeGestureRecognizer *)swipeGesture
{

        if (self.mainsubView.frame.origin.x==0)
        {
            self.menuTableView.hidden=YES;
            [UIView animateWithDuration:.5 animations:^{
                    self.mainsubView.frame = CGRectMake(-220, 0,screenSize.size.width, screenSize.size.height);
                    
                }completion:^(BOOL finish){
                    self.accountTableView.hidden=NO;
                    addAccount.hidden=NO;
                    [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:@"Slide"];
                }];
            
        }
        else
        {
        self.menuTableView.hidden=NO;
            [UIView animateWithDuration:.5 animations:^{
                    

                    self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height);
                    
                }completion:^(BOOL finish){
                    [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];

                }];
           }
}
-(void)openAccountTabel:(UIGestureRecognizer*)recognizer
{
    if (self.mainsubView.frame.origin.x==0)
    {
        self.menuTableView.hidden=YES;
        [UIView animateWithDuration:.5 animations:^{
            self.mainsubView.frame = CGRectMake(-220, 0,screenSize.size.width, screenSize.size.height);
            
        }completion:^(BOOL finish){
            self.accountTableView.hidden=NO;
            addAccount.hidden=NO;
            [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:@"Slide"];
        }];
        
    }
    else
    {
        self.menuTableView.hidden=NO;
        [UIView animateWithDuration:.5 animations:^{
            
            
            self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height);
            
        }completion:^(BOOL finish){
            [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
            
        }];
    }
 
}
#pragma mark -
-(void) menuButtonClciked:(id)sender
{
    
    if (self.mainsubView.frame.origin.x>=120)
    {
        [UIView animateWithDuration:.5 animations:^
        {
            self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height);
            
        }completion:^(BOOL finish){
            self.accountTableView.hidden=NO;
            addAccount.hidden=NO;
            [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:@"SlideChanged"];
            
        }];
    }
    else{
        self.menuTableView.hidden=NO;
        [UIView animateWithDuration:.5 animations:^
        {
            self.accountTableView.hidden=YES;
            addAccount.hidden=YES;
            
            if(IS_IPHONE_4_OR_LESS||IS_IPHONE_5)
            {
                 self.mainsubView.frame = CGRectMake(SCREEN_WIDTH-50, 0,screenSize.size.width, screenSize.size.height);
            }
            else
            {
              self.mainsubView.frame = CGRectMake(SCREEN_WIDTH-50, 0,screenSize.size.width, screenSize.size.height);
            }
        }completion:^(BOOL finish)
        {
            
            [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:@"Slide"];

        }];
    }
    
    
}

#pragma mark -
#pragma mark TableView Delegate and DataSource
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    if(tableView==self.accountTableView)
    {
        return 2;
    }
    else
    {
    return 1;
    }
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(tableView==self.menuTableView)
    {
    if (section==0)
    {
        
        return self.viewControllers.count;
    }
    }
    else if (tableView==self.accountTableView)
    {
        NSLog(@"Account array %lu",(unsigned long)twitterCredsOfUser.count);
        if(section==0)
        {
            return [twitterCredsOfUser count];
 
        }
        else if (section==1)
        {
           return 2;
        }
    }
    return 0;
}

-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==self.menuTableView)
    {
        cell.contentView.backgroundColor=[UIColor whiteColor];
        UIBezierPath *shadowPath = [UIBezierPath bezierPathWithRect:CGRectMake(0,0, cell.contentView.frame.size.width,cell.contentView.frame.size.height+5)];
        cell.contentView.layer.masksToBounds = YES;
        cell.contentView.layer.shadowColor = [UIColor blackColor].CGColor;
        cell.contentView.layer.shadowOffset = CGSizeMake(5,5);  /*Change value of X n Y as per your need of shadow to appear to like right bottom or left bottom or so on*/
        cell.contentView.layer.shadowOpacity = 0.5f;
        cell.contentView.layer.shadowRadius=.1f;
        cell.contentView.layer.shadowPath = shadowPath.CGPath;

    /*UIView * seperatorLine=[[UIView alloc]initWithFrame:CGRectMake(0,cell.contentView.frame.size.height-1.2,cell.contentView.frame.size.width,0.5)];
        seperatorLine.alpha=.5;
        seperatorLine.backgroundColor=[UIColor grayColor];
        [cell.contentView addSubview:seperatorLine];
    cell.backgroundColor=[UIColor whiteColor];
    cell.textLabel.textColor = [UIColor blackColor];
    cell.textLabel.font = [UIFont boldSystemFontOfSize:14.0f];*/
    }
    else
    {
        
    }
}
-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell Identifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:nil];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    //Check Section
    if(tableView==self.menuTableView)
    {
    NSString * title=[NSString stringWithFormat:@"%@",[(UIViewController *)[_viewControllers objectAtIndex:indexPath.row] title]];
    NSLog(@"Title = %@",title);
    UIImageView * image=[[UIImageView alloc]initWithFrame:CGRectMake(5, 10,30,30)];
    [cell.contentView addSubview:image];
    image.image=[UIImage imageNamed:[menuTableImages objectAtIndex:indexPath.row]];
    UILabel * txtLbl=[[UILabel alloc]initWithFrame:CGRectMake(60, 10, 150, 20)];
    txtLbl.font=[UIFont systemFontOfSize:15];
    [cell.contentView addSubview:txtLbl];
    txtLbl.text=title;
    }
    else if(tableView==self.accountTableView)
    {
        
        TableCustomCell *cellLocal = [tableView dequeueReusableCellWithIdentifier:nil];
        
        if (cellLocal == nil)
        {
            cellLocal = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"accounTable"];
            cellLocal.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        
        if(indexPath.section==0)
        {
        NSDictionary * localDict=[twitterCredsOfUser objectAtIndex:indexPath.row];
        NSURL * fetchImage=[NSURL URLWithString:[localDict objectForKey:@"UserImageUrl"]];
        [cellLocal.userImage sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@"place_holder.png"]];

        cellLocal.settingBtn.frame=CGRectMake(165,5,45,45);
        cellLocal.settingBtn.tag=indexPath.row;
            [cellLocal.settingBtn addTarget:self action:@selector(createSetting:) forControlEvents:UIControlEventTouchUpInside];
               cellLocal.userName.text=[localDict objectForKey:@"TwitterUserName"];
        }
        else if (indexPath.section==1)
        {
            cellLocal.userName.hidden=YES;
            cellLocal.settingBtn.hidden=YES;
            //------------
            UIImageView * imgViewFooter=[[UIImageView alloc]init];
            imgViewFooter.image=[UIImage imageNamed:[accImageArray objectAtIndex:indexPath.row]];
            imgViewFooter.frame=CGRectMake(10, 10, 30, 30);
            imgViewFooter.layer.cornerRadius=15;
            [cellLocal.contentView addSubview:imgViewFooter];
            //------------
            UILabel * title=[[UILabel alloc]init];
            title.frame=CGRectMake(50, 10, 180, 20);
            title.text=[buttonTitleAccTable objectAtIndex:indexPath.row];
            title.textColor=[UIColor blackColor];
            [cellLocal.contentView addSubview:title];
            
        }
        
        return cellLocal;
    }
    
    return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==self.menuTableView)
    {
    //Dismiss Menu TableView with Animation
    [UIView animateWithDuration:.5 animations:^{
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height);
        
    }completion:^(BOOL finished){
        //After completion
        //first check if new selected view controller is equals to previously selected view controller
        UIViewController *newViewController = [_viewControllers objectAtIndex:indexPath.row];
        NSLog(@"view controller %@",_viewControllers);
        if ([newViewController isKindOfClass:[UINavigationController class]])
        {
            [(UINavigationController *)newViewController popToRootViewControllerAnimated:YES];
        }
        if (self.selectedIndex==indexPath.row  && self.selectedSection == indexPath.section)
        {
          //  return;
        }
        
        _selectedSection = indexPath.section;
        _selectedIndex = indexPath.row;
        
        [self getSelectedViewControllers:newViewController];
        updateValue = 0;
    }];
    self.topicButton.hidden=YES;
    }
    else if(tableView==self.accountTableView)
    {
        if(indexPath.section==0)
        {
            [self loadUserSelected:(int)indexPath.row];
        }
        if(indexPath.section==1)
        {
            if(indexPath.row==0)
            {
            [self addAccount];
            }
            else if (indexPath.row==1)
            {
                NSURL *url = [NSURL URLWithString:@"http://twtboardpro.com/"];
                [[UIApplication sharedApplication] openURL:url];
            }
        }
    }
    
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==self.accountTableView)
    {
        return 55;
    }
    else
    {
        return 48.5;
    }
}
#pragma mark -
-(void) getSelectedViewControllers:(UIViewController *)newViewController{
    // selected new view controller
    UIViewController *oldViewController = _selectedViewController;
    
    if (newViewController != nil) {
        [oldViewController.view removeFromSuperview];
        _selectedViewController = newViewController;
        
        //Update Container View with selected view controller view
        [self updateViewContainer];
        //Check Delegate assign or not
    }
}
-(void) updateViewContainer
{
    self.selectedViewController.view.autoresizingMask = UIViewAutoresizingFlexibleHeight;
    
    self.selectedViewController.view.frame = self.contentContainerView.bounds;
    self.menuLabel.text=self.selectedViewController.title;
    NSLog(@"menu label -=- %@",self.menuLabel.text);
    
    [self.contentContainerView addSubview:self.selectedViewController.view];
    
}

-(BOOL)retriveTwitterSqlite
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {

    BOOL check_Update;
    check_Update=FALSE;
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    
    NSLog(@"%@",paths);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *databasePath = [documentsDirectory stringByAppendingPathComponent:@"TwitterAccountsDataBase.sqlite"];
    // Check to see if the database file already exists
    NSString *query = [NSString stringWithFormat:@"select * from TwitterData"];
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
        NSMutableArray * credsMutArray=[[NSMutableArray alloc]init];
        while(sqlite3_step(stmt)==SQLITE_ROW)
        {
            NSMutableDictionary * credsDict=[[NSMutableDictionary alloc]init];
            char *accessToken = (char *) sqlite3_column_text(stmt,1);
            char *twitterId = (char *) sqlite3_column_text(stmt,2);
            char *userScreenName=(char *)sqlite3_column_text(stmt,3);
            char *userName=(char *)sqlite3_column_text(stmt,4);
            char *imageUrl= (char *) sqlite3_column_text(stmt,5);
            char *bannerImageUrl= (char *) sqlite3_column_text(stmt,6);
            //------
            NSString *strTwitterId=[NSString  stringWithUTF8String:twitterId];
            NSString *strUserName=[NSString stringWithUTF8String:userScreenName];
            NSString *straccessToken=[NSString stringWithUTF8String:accessToken];
            NSString *strProfilename=[NSString stringWithUTF8String:userName];
            NSString * bannerImageStr;
            if(bannerImageUrl)
            {
            bannerImageStr=[NSString stringWithUTF8String:bannerImageUrl];
            [credsDict setObject:bannerImageStr forKey:@"BannerImageUrl"];

            }

            NSLog(@"Multiple Acounts %@",strTwitterId);
            //Main User
            if([strUserName isEqualToString:[SingletonTboard sharedSingleton].mainUser])
            {
            [SingletonTboard sharedSingleton].currectUserTwitterId=strTwitterId;
            [SingletonTboard sharedSingleton].mainUserRealName=strProfilename;
            NSString * profilePicStr=[NSString stringWithUTF8String:imageUrl];
                [SingletonTboard sharedSingleton].imageUrl=profilePicStr;
                if(bannerImageStr)
                {
                    [SingletonTboard sharedSingleton].bannerImageUrl=bannerImageStr;
                }
                else
                {
                   [SingletonTboard sharedSingleton].bannerImageUrl=@"Default";
                }
                [self performSelectorOnMainThread:@selector(setProfilePic) withObject:nil waitUntilDone:false];
            }
            [userTwitterId addObject:strTwitterId];
            [credsDict setObject:straccessToken forKey:@"AccessTokenTwitter"];
            [credsDict setObject:strTwitterId forKey:@"TwitterId"];
            [credsDict setObject:strUserName forKey:@"TwitterUserName"];
            [credsDict setObject:strProfilename forKey:@"ProfileName"];
            [credsDict setObject:[NSString stringWithUTF8String:imageUrl] forKey:@"UserImageUrl"];
            [credsMutArray addObject:credsDict];
        }
       
        twitterCredsOfUser=[NSArray arrayWithArray:credsMutArray];
        [SingletonTboard sharedSingleton].allDataUser=twitterCredsOfUser;
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                if([SingletonTboard sharedSingleton].newAccountAdded)
                {
                NSLog(@"twiite id of current user %@",[SingletonTboard sharedSingleton].currectUserTwitterId);
                    [UIView animateWithDuration:.5 animations:^{
                        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
                        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height-45);
                        
                    }completion:^(BOOL finished)
                    {
                        [[NSNotificationCenter defaultCenter]postNotificationName:@"ReloadTimeLine" object:nil];
                    }];
                  
                }
                   [self.accountTableView reloadData];
                   
                }
            });

        }
    });
    return false;
}
-(void)setProfilePic
{
      NSURL * imageUrl=[NSURL URLWithString:[SingletonTboard sharedSingleton].imageUrl];
    [userImage sd_setImageWithURL:imageUrl placeholderImage:[UIImage imageNamed:@""]];
    userNameLbl.text=[SingletonTboard sharedSingleton].mainUserRealName;
    userScreenNameLbl.text=[SingletonTboard sharedSingleton].mainUser;
 nameLblAccTabel.text=[NSString stringWithFormat:@"%@",[SingletonTboard sharedSingleton].mainUser];
    [profileImageAccTable sd_setImageWithURL:imageUrl placeholderImage:[UIImage imageNamed:@""]];
    NSLog(@"User Screen Name and User Name %@ %@",[SingletonTboard sharedSingleton].mainUserRealName,[SingletonTboard sharedSingleton].mainUser);
    if([[SingletonTboard sharedSingleton].bannerImageUrl isEqualToString:@"Default"])
    {
        headerImage.image=[UIImage imageNamed:@""];
    }
    else
    {
        NSURL * bannerImageUrl=[NSURL URLWithString:[SingletonTboard sharedSingleton].bannerImageUrl];
        [headerImage sd_setImageWithURL:bannerImageUrl placeholderImage:nil];
    }

    
}
-(void)loadUserSelected:(int)index
{
    NSDictionary * localDict=[twitterCredsOfUser objectAtIndex:index];
    NSLog(@"local Dict on switching User %@",localDict);
    if([[localDict objectForKey:@"TwitterId"] isEqualToString:[SingletonTboard sharedSingleton].currectUserTwitterId])
    {
        return;
    }
    [SingletonTboard sharedSingleton].currectUserTwitterId=[localDict objectForKey:@"TwitterId"];
   [SingletonTboard sharedSingleton].mainUser=[localDict objectForKey:@"TwitterUserName"];
    //Update Accountabel Name and Image
    nameLblAccTabel.text=[NSString stringWithFormat:@"%@",[localDict objectForKey:@"TwitterUserName"]];
     [SingletonTboard sharedSingleton].imageUrl=[localDict objectForKey:@"UserImageUrl"];
    NSURL * profilePicUrl=[NSURL URLWithString:[localDict objectForKey:@"UserImageUrl"]];
    NSURL * imageUrl=[NSURL URLWithString:[SingletonTboard sharedSingleton].imageUrl];
    [userImage sd_setImageWithURL:imageUrl placeholderImage:[UIImage imageNamed:@""]];
    userNameLbl.text=[localDict objectForKey:@"ProfileName"];
    userScreenNameLbl.text=[SingletonTboard sharedSingleton].mainUser;
    
    [profileImageAccTable sd_setImageWithURL:profilePicUrl placeholderImage:[UIImage imageNamed:@""]];
    
    if([localDict objectForKey:@"BannerImageUrl"])
    {
        NSURL * bannerImageUrl=[NSURL URLWithString:[localDict objectForKey:@"BannerImageUrl"]];
        [headerImage sd_setImageWithURL:bannerImageUrl placeholderImage:nil];
    }
    else
    {
        headerImage.image=[UIImage imageNamed:@""];
    }
    //------------
    [[NSUserDefaults standardUserDefaults]setObject:[localDict objectForKey:@"AccessTokenTwitter"] forKey:@"SavedAccessHTTPBody"];
    [[FHSTwitterEngine sharedEngine]loadAccessToken];
    NSLog(@"twiite id of current user %@",[SingletonTboard sharedSingleton].currectUserTwitterId);
    //--------
    [[NSUserDefaults standardUserDefaults]setObject:[SingletonTboard sharedSingleton].mainUser forKey:MainuserLogin];
    //-------
    [UIView animateWithDuration:.5 animations:^{
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height-45);
        //User Image
        NSURL * fetchImage=[NSURL URLWithString:[localDict objectForKey:@"UserImageUrl"]];
        //            [self.userImageOnTop sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@"twitter-profile-egg.png"]];
        //-------------------
    }completion:^(BOOL finished){
        [[SingletonTboard sharedSingleton]fetchListOfFollow_Unfollow];

        [[NSNotificationCenter defaultCenter]postNotificationName:@"ReloadTimeLine" object:nil];
    }];

}
#pragma Mark Showing Profile
-(void)addProfileView
{
    //Dismiss Menu TableView with Animation
    [UIView animateWithDuration:.5 animations:^{
        [[NSNotificationCenter defaultCenter]postNotificationName:@"ChangeBackground" object:nil];
        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height);
        
    }completion:^(BOOL finished){
        //After completion
        //first check if new selected view controller is equals to previously selected view controller
        UIViewController *newViewController = (UIViewController*)self.profileView;
        NSLog(@"view controller %@",_viewControllers);
        if ([newViewController isKindOfClass:[UINavigationController class]])
        {
            [(UINavigationController *)newViewController popToRootViewControllerAnimated:YES];
        }
        
        [self getSelectedViewControllers:newViewController];
        updateValue = 0;
    }];
    self.topicButton.hidden=YES;

}

#pragma mark-----
@end

static void * const kMyPropertyAssociatedStorageKey = (void*)&kMyPropertyAssociatedStorageKey;

@implementation UIViewController (CustomMenuViewControllerItem)
@dynamic customMenuViewController;

static char const * const orderedElementKey;

-(void) setCustomMenuViewController:(CustomMenuViewControllerTboard *)customMenuViewController{
    
    NSLog(@"cc==%@",customMenuViewController.viewControllers);
    
    objc_setAssociatedObject(self, &orderedElementKey, customMenuViewController,OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

-(CustomMenuViewControllerTboard *) customMenuViewController{
    
    if (objc_getAssociatedObject(self, &orderedElementKey) != nil)
    {
        NSLog(@"Element: %@", objc_getAssociatedObject(self, orderedElementKey));
    }
    
    NSLog(@"Element: %@", objc_getAssociatedObject(self, &orderedElementKey));
    //    return objc_getAssociatedObject(self, @selector(customMenuViewController));
    return objc_getAssociatedObject(self, orderedElementKey);
    //return  self.customMenuViewController;
}
//-(void)dealloc
//{
//    NSLog(@"Dealloc Called");
//    [[NSNotificationCenter defaultCenter]removeObserver:self];
//}
@end

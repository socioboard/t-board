
//
//  CustomMenuViewController.m
//  MOVYT
//
//  Created by Sumit Ghosh on 27/05/14.
//  Copyright (c) 2014 Sumit Ghosh. All rights reserved.
//

#import "CustomMenuViewController.h"
#import <objc/runtime.h>
#import "ViewController.h"
#import "AppDelegate.h"
#import "TableCustomCell.h"
#import "Singleton.h"
#import "SettingView.h"
#import "UIImageView+WebCache.h"
#import "FHSTwitterEngine.h"
#import <sqlite3.h>
@interface CustomMenuViewController ()
{
    NSInteger updateValue;
}
@property (nonatomic,strong)UITabBar *customTabBar;
@end

@implementation CustomMenuViewController
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
//    [[NSNotificationCenter defaultCenter]removeObserver:self];
}
-(void)viewDidAppear:(BOOL)animated
{
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
-(void) setViewControllers:(NSArray *)viewControllers{
    
    _viewControllers = [viewControllers copy];
    
    for (UIViewController *viewController in _viewControllers ) {
        [self addChildViewController:viewController];
        
        viewController.view.frame = CGRectMake(0, 90,[UIScreen mainScreen].bounds.size.width , [UIScreen mainScreen].bounds.size.height-140);
        [viewController didMoveToParentViewController:self];
    }
}
-(void) setSecondSectionViewControllers:(NSArray *)secondSectionViewControllers{
    
    _secondSectionViewControllers = [secondSectionViewControllers copy];
    
    for (UIViewController *viewController in _secondSectionViewControllers ) {
        [self addChildViewController:viewController];
        
        viewController.view.frame = CGRectMake(0, 90,[UIScreen mainScreen].bounds.size.width , [UIScreen mainScreen].bounds.size.height-90);
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
    menuTableImages=[NSArray arrayWithObjects:@"Feed.png",@"followby.png",@"followedby.png",@"find_friend.png",@"post_schedule.png",nil];
    //-------
    accountTableArray=[[NSMutableArray alloc]init];
    userTwitterId=[[NSMutableArray alloc]init];
    //-------
    screenSize = [UIScreen mainScreen].bounds;
    
    userDefault = [NSUserDefaults standardUserDefaults];
    
    // Do any additional setup after loading the view.
  self.view.backgroundColor = [UIColor whiteColor];
    
    self.screen_height = [UIScreen mainScreen].bounds.size.height;
    self.isSignIn = NO;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reloadMenuTable:) name:@"UpdateMenuTable" object:nil];
    
    //Add View SubView;
    self.mainsubView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, self.screen_height-45)];
    NSLog(@"Main sub view frame X=-=- %f \n Y == %f",[UIScreen mainScreen].bounds.origin.x,[UIScreen mainScreen].bounds.origin.y);
    self.mainsubView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.mainsubView];
    
    //Add Header View
    CGFloat hh;
    CGRect frame_b;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        hh = 75;
        frame_b = CGRectMake(680, 30, 45, 25);
        
    }
    else{
        hh = 55;
        
        frame_b = CGRectMake(20, 20, 45, 25);
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
    self.userImageOnTop=[[UIImageView alloc]initWithFrame:CGRectMake(screenSize.size.width-50,10, 30, 30)];
    self.userImageOnTop.layer.cornerRadius=15;
    self.userImageOnTop.clipsToBounds=YES;
    self.userImageOnTop.backgroundColor=[UIColor greenColor];
    [self.headerView addSubview:self.userImageOnTop];
    //=======================================
    // Add Container View
    frame = CGRectMake(0,55, screenSize.size.width, screenSize.size.height-55);
    self.contentContainerView = [[UIView alloc] initWithFrame:frame];
    self.contentContainerView.backgroundColor = [UIColor grayColor];
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
    self.menuLabel.font = [UIFont boldSystemFontOfSize:15];
    self.menuLabel.textColor = [UIColor whiteColor];
    self.menuLabel.textAlignment = NSTextAlignmentCenter;
    self.menuLabel.text = _selectedViewController.title;
    [self.headerView addSubview:self.menuLabel];
    
    //====================================
    self.selectedIndex = 0;
    self.selectedViewController = [_viewControllers objectAtIndex:0];
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
    if (!self.accountTableView)
    {
        self.selectedIndex = 0;
        self.accountTableView = [[UITableView alloc] initWithFrame:CGRectMake(screenSize.size.width-140,55,140, self.screen_height-120) style:UITableViewStylePlain];
        
        self.accountTableView.backgroundColor = [UIColor whiteColor];
        
        self.accountTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.accountTableView.delegate = self;
        self.accountTableView.dataSource = self;
        
    }
    else
    {
        [self.accountTableView reloadData];
    }
    
    [self.view insertSubview:self.accountTableView belowSubview:self.mainsubView];
    UIView * viewLogo=[[UIView alloc]init];
    viewLogo.frame=CGRectMake(screenSize.size.width-140, 0, 140, 55);
    viewLogo.backgroundColor=ThemeColor;
    [self.view insertSubview:viewLogo belowSubview:self.mainsubView];
    UILabel * accountLbl=[[UILabel alloc]init];
    accountLbl.frame=CGRectMake(5,15, 140, 20);
    accountLbl.text=@"Accounts";
    accountLbl.textAlignment=NSTextAlignmentCenter;
    accountLbl.textColor=[UIColor whiteColor];
    [viewLogo addSubview:accountLbl];
    //------------------
    UIButton * addAccount=[[UIButton alloc]init];
    addAccount.frame=CGRectMake(screenSize.size.width-140,screenSize.size.height-80,140,29);
    [addAccount setBackgroundImage:[UIImage imageNamed:@"add_aacount.png"] forState:UIControlStateNormal];
    [addAccount addTarget:self action:@selector(addAccount) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:addAccount belowSubview:self.mainsubView];

}
-(void)addAccount
{
    UIViewController *loginController = [[FHSTwitterEngine sharedEngine]loginControllerWithCompletionHandler:^(BOOL success) {
        [Singleton sharedSingleton].newAccountAdded=TRUE;
       
        NSLog(success?@"L0L success":@"O noes!!! Loggen faylur!!!");
    }];
    [self presentViewController:loginController animated:YES completion:nil];
}
-(void)createSetting
{
    SettingView * settingView=[[SettingView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [self.view addSubview:settingView];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(loggedOut) name:@"LogOutFromSetting" object:nil];
}
-(void)loggedOut
{
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"LogOutFromSetting" object:nil];
    [[NSUserDefaults standardUserDefaults]setObject:@"NoUser" forKey:@"MainUserLogin"];
    [self dismissViewControllerAnimated:YES completion:nil];
}
-(void) createMenuTableView
{
    
    if (!self.menuTableView)
    {
        self.selectedIndex = 0;
        self.menuTableView = [[UITableView alloc] initWithFrame:CGRectMake(0,55,180, self.screen_height-140) style:UITableViewStylePlain];
        
        self.menuTableView.backgroundColor =  [UIColor colorWithRed:(CGFloat)39/255 green:(CGFloat)39/255 blue:(CGFloat)41/255 alpha:1];
        
        self.menuTableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        self.menuTableView.delegate = self;
        self.menuTableView.dataSource = self;
        self.menuTableView.backgroundColor=[UIColor clearColor];
    }
    else
    {
        [self.menuTableView reloadData];
    }
    
    [self.view insertSubview:self.menuTableView belowSubview:self.mainsubView];
    UIView * viewLogo=[[UIView alloc]init];
    viewLogo.frame=CGRectMake(0, 0, 180, 55);
    viewLogo.backgroundColor=ThemeColor;
    [self.view insertSubview:viewLogo belowSubview:self.mainsubView];
    UIImageView * logoImage=[[UIImageView alloc]init];
    logoImage.frame=CGRectMake(25,10,120, 30);
    logoImage.image=[UIImage imageNamed:@"tboardpro.png"];
    [viewLogo addSubview:logoImage];
    
}

#pragma mark -

-(void)handleSwipeGestureRight:(UISwipeGestureRecognizer *)swipeGesture
{

    if (self.mainsubView.frame.origin.x==0) {

    [UIView animateWithDuration:.5 animations:^{
        self.mainsubView.frame = CGRectMake(180, 0,screenSize.size.width, screenSize.size.height-45);
    }completion:^(BOOL finish){
    }];
    }
    else
    {

    [UIView animateWithDuration:.5 animations:^{
        self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height-45);
        
    }completion:^(BOOL finish){
        
        self.swipeGestureRight.direction = UISwipeGestureRecognizerDirectionRight;
    }];
    }
}
-(void)handleSwipeGestureLeft:(UISwipeGestureRecognizer *)swipeGesture
{
        if (self.mainsubView.frame.origin.x==0)
        {
                
                [UIView animateWithDuration:.5 animations:^{
                    self.mainsubView.frame = CGRectMake(-140, 0,screenSize.size.width, screenSize.size.height-45);
                }completion:^(BOOL finish){
                }];
            
        }
        else
        {
        
                [UIView animateWithDuration:.5 animations:^{
                    self.mainsubView.frame = CGRectMake(0, 0,screenSize.size.width, screenSize.size.height-45);
                    
                }completion:^(BOOL finish){
                    
                }];
           }
}

#pragma mark -
-(void) menuButtonClciked:(id)sender
{
    
    if (self.mainsubView.frame.origin.x>=120) {
        
        [UIView animateWithDuration:.5 animations:^{
            self.mainsubView.frame = CGRectMake(-140, 0,screenSize.size.width, screenSize.size.height-45);
            
        }completion:^(BOOL finish){
            
        }];
    }
    else{
        [UIView animateWithDuration:.5 animations:^{
            self.mainsubView.frame = CGRectMake(180, 0,screenSize.size.width, screenSize.size.height-45);
        }completion:^(BOOL finish){
        }];
    }
    
    
}

#pragma mark -
#pragma mark TableView Delegate and DataSource
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    
    return 1;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(tableView==self.menuTableView)
    {
    if (section==0) {
        
        return self.viewControllers.count;
    }
    }
    else if (tableView==self.accountTableView)
    {
        NSLog(@"Account array %lu",(unsigned long)twitterCredsOfUser.count);
        return [twitterCredsOfUser count];
    }
    return 0;
}

-(void) tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==self.menuTableView)
    {
    UIColor *firstColor =  [UIColor colorWithRed:(CGFloat)255/255 green:(CGFloat)255/255 blue:(CGFloat)255/255 alpha:1];
    UIColor *secColor = [UIColor colorWithRed:(CGFloat)245/255 green:(CGFloat)245/255 blue:(CGFloat)245/255 alpha:1];
    CAGradientLayer *layer = [CAGradientLayer layer];
    layer.frame = cell.contentView.frame;
    layer.colors = [NSArray arrayWithObjects:(id)firstColor.CGColor,(id)secColor.CGColor, nil];
    
    [cell.contentView.layer insertSublayer:layer atIndex:0];
    cell.textLabel.textColor = [UIColor blackColor];
    cell.textLabel.font = [UIFont boldSystemFontOfSize:14.0f];
    }
    else
    {
        UIColor *firstColor =  [UIColor colorWithRed:(CGFloat)255/255 green:(CGFloat)255/255 blue:(CGFloat)255/255 alpha:1];
        UIColor *secColor = [UIColor colorWithRed:(CGFloat)245/255 green:(CGFloat)245/255 blue:(CGFloat)245/255 alpha:1];
        CAGradientLayer *layer = [CAGradientLayer layer];
        layer.frame = cell.contentView.frame;
        layer.colors = [NSArray arrayWithObjects:(id)firstColor.CGColor,(id)secColor.CGColor, nil];
        
        [cell.contentView.layer insertSublayer:layer atIndex:0];
    }
}
-(UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell Identifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    //Check Section
    if(tableView==self.menuTableView)
    {
    NSString * title=[NSString stringWithFormat:@"%@",[(UIViewController *)[_viewControllers objectAtIndex:indexPath.row] title]];
    NSLog(@"Title = %@",title);
    cell.imageView.image=[UIImage imageNamed:[menuTableImages objectAtIndex:indexPath.row]];
    cell.textLabel.text=title;
    }
    else if(tableView==self.accountTableView)
    {
        
        TableCustomCell *cellLocal = [tableView dequeueReusableCellWithIdentifier:@"accounTable"];
        
        if (cellLocal == nil)
        {
            cellLocal = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"accounTable"];
            cellLocal.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        NSDictionary * localDict=[twitterCredsOfUser objectAtIndex:indexPath.row];
        NSURL * fetchImage=[NSURL URLWithString:[localDict objectForKey:@"UserImageUrl"]];
        cellLocal.settingBtn.tag=indexPath.row;
        [cellLocal.settingBtn addTarget:self action:@selector(createSetting) forControlEvents:UIControlEventTouchUpInside];
        [cellLocal.userImage sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@""]];
        cellLocal.userName.text=[localDict objectForKey:@"ProfileName"];
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
        
        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height-45);
        
    }completion:^(BOOL finished){
        //After completion
        //first check if new selected view controller is equals to previously selected view controller
        UIViewController *newViewController = [_viewControllers objectAtIndex:indexPath.row];
        NSLog(@"view controller %@",_viewControllers);
        if ([newViewController isKindOfClass:[UINavigationController class]]) {
            [(UINavigationController *)newViewController popToRootViewControllerAnimated:YES];
        }
        if (self.selectedIndex==indexPath.row  && self.selectedSection == indexPath.section) {
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
        NSDictionary * localDict=[twitterCredsOfUser objectAtIndex:indexPath.row];
        [Singleton sharedSingleton].currectUserTwitterId=[localDict objectForKey:@"TwitterId"];
        [[NSUserDefaults standardUserDefaults]setObject:[localDict objectForKey:@"AccessTokenTwitter"] forKey:@"SavedAccessHTTPBody"];
        [[FHSTwitterEngine sharedEngine]loadAccessToken];
        NSLog(@"twiite id of current user %@",[Singleton sharedSingleton].currectUserTwitterId);
        [UIView animateWithDuration:.5 animations:^{
            
            self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height-45);
           //User Image
            NSURL * fetchImage=[NSURL URLWithString:[localDict objectForKey:@"UserImageUrl"]];
            [self.userImageOnTop sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@""]];
            //-------------------
        }completion:^(BOOL finished){
              [[NSNotificationCenter defaultCenter]postNotificationName:@"ReloadTimeLine" object:nil];
        }];
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
            NSString *strTwitterId=[NSString  stringWithUTF8String:twitterId];
            NSString *strUserName=[NSString stringWithUTF8String:userScreenName];
            NSString *straccessToken=[NSString stringWithUTF8String:accessToken];
            NSString *strProfilename=[NSString stringWithUTF8String:userName];
            char *imageUrl= (char *) sqlite3_column_text(stmt,5);

            NSLog(@"Multiple Acounts %@",strTwitterId);
            //Main User
            if([strUserName isEqualToString:[Singleton sharedSingleton].mainUser])
            {
            [Singleton sharedSingleton].userTwitterScreenName=[NSString stringWithUTF8String:userScreenName];
            [Singleton sharedSingleton].currectUserTwitterId=strTwitterId;
            [Singleton sharedSingleton].mainUserRealName=strUserName;
           
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                    [Singleton sharedSingleton].imageUrl=[NSString stringWithUTF8String:imageUrl];
                    NSURL * urlFetchImage=[NSURL URLWithString:[Singleton sharedSingleton].imageUrl];
                    [self.userImageOnTop sd_setImageWithURL:urlFetchImage placeholderImage:[UIImage imageNamed:@""]];
                    
                }
            });
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
    }
    @catch(NSException *e)
    {
        NSLog(@"%@",e);
    }
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                if([Singleton sharedSingleton].newAccountAdded)
                {
                NSLog(@"twiite id of current user %@",[Singleton sharedSingleton].currectUserTwitterId);
                    [UIView animateWithDuration:.5 animations:^{
                        
                        self.mainsubView.frame = CGRectMake(0, 0, screenSize.size.width, screenSize.size.height-45);
                        
                    }completion:^(BOOL finished){
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


#pragma mark-----
@end

static void * const kMyPropertyAssociatedStorageKey = (void*)&kMyPropertyAssociatedStorageKey;

@implementation UIViewController (CustomMenuViewControllerItem)
@dynamic customMenuViewController;

static char const * const orderedElementKey;

-(void) setCustomMenuViewController:(CustomMenuViewController *)customMenuViewController{
    
    NSLog(@"cc==%@",customMenuViewController.viewControllers);
    
    objc_setAssociatedObject(self, &orderedElementKey, customMenuViewController,OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

-(CustomMenuViewController *) customMenuViewController{
    
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

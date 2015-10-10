//
//  CustomMenuViewController.h
//  MOVYT
//
//  Created by Sumit Ghosh on 27/05/14.
//  Copyright (c) 2014 Sumit Ghosh. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <sqlite3.h>
#import "ProfileViewControllerTboard.h"
@interface CustomMenuViewControllerTboard : UIViewController<UITableViewDataSource, UITableViewDelegate, UITabBarDelegate>
{
    
    NSUserDefaults *userDefault;
    UIButton * addAccount;
    //----------
    CGRect screenSize;
    NSString * urlForImage;
    sqlite3 *_databaseHandle;
    NSArray * twitterCredsOfUser;
    NSMutableArray * accountTableArray;
    NSMutableArray * userTwitterId;
    NSArray * menuTableImages;
    UIImageView * headerImage;
    UIImageView * userImage;
    NSArray * buttonTitleAccTable;
    NSArray * accImageArray;
    UIImageView * profileImageAccTable;
    UILabel * nameLblAccTabel;
    UILabel * userNameLbl;
    UILabel * userScreenNameLbl;
    UIView * deleteUserPopUp;
    NSDictionary * accUserDictionary;
}


@property (nonatomic,strong)ProfileViewControllerTboard * profileView;
@property (nonatomic, assign) BOOL isSignIn;

@property (nonatomic, assign) CGFloat screen_height;

@property (nonatomic, strong) UIButton *menuButton;
@property (nonatomic, strong) UIButton *backButton;
@property (nonatomic, strong) UIButton *topicButton;
@property (nonatomic, strong) UIView *headerView;
@property (nonatomic, strong) UIView *boosterView;
@property (nonatomic, strong) UIView *contentContainerView;
@property (nonatomic, strong) UILabel *menuLabel;
@property (nonatomic, strong) UITableView *menuTableView,*accountTableView;
@property (nonatomic, strong) UILabel *firstSectionHeader;
@property (nonatomic, strong) UILabel *secondHeaderLabel;
@property (nonatomic,strong)UIImageView * userImageOnTop;


@property (nonatomic, copy) NSArray *viewControllers;
@property (nonatomic, copy) NSArray *tabViewControllersArray,*titleOfTabBar;
@property (nonatomic, strong) NSArray *secondSectionViewControllers;
@property (nonatomic, assign) NSInteger numberOfSections;

@property (nonatomic, copy) UIViewController *selectedViewController;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, assign) NSInteger selectedSection;

@property (nonatomic, strong) UIView *mainsubView;

@property (nonatomic, strong) UISwipeGestureRecognizer *swipeGestureLeft,*swipeGestureRight;


-(NSArray *) getAllViewControllers;
@end

@interface UIViewController (CustomMenuViewControllerItem)

@property (nonatomic, strong) CustomMenuViewControllerTboard *customMenuViewController;
//-(CustomMenuViewController *)firstAvailableViewController;
@end

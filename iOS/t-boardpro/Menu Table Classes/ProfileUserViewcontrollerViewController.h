//
//  ProfileUserViewcontrollerViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@interface ProfileUserViewcontrollerViewController : UIViewController<UITableViewDelegate,UITableViewDataSource>
{
    UILabel * tweetCountLbl;
    UILabel * followingCountLbl;
    UILabel * followedByCountLbl;
    UILabel * nameLbl;
    UITableView * userTweetTable;
    NSArray * wholeData;
    MBProgressHUD * HUD;

}
@property (strong, nonatomic)  UILabel *editUserImage;
@property (strong, nonatomic)  UIImageView *userImageView;
@property (strong, nonatomic)  UILabel *currentUserName;

@end

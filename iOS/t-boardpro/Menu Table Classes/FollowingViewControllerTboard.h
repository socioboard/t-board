//
//  UnfollowViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@interface FollowingViewControllerTboard : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    NSMutableArray * followersIdList;
    int currentSelection;
    UITextField * myTextField;
    MBProgressHUD * HUD;
    UIView * backView;
    NSMutableArray * followerScreenName;
    NSString * tweetScreenName;
    BOOL refresh;
    long long nextCursor;
    NSArray * friendsId;
    UIView * nooneView;

}
@property(nonatomic,strong)UITableView * showFollowing;
@property(nonatomic,strong)NSArray * alldata;

@end

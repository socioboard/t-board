//
//  FollowersView.h
//  TwitterBoard
//
//  Created by GLB-254 on 5/8/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
@interface FollowedByViewControllerTboard : UIViewController<UITableViewDelegate,UITableViewDataSource>
{
    int currentSelection;
    MBProgressHUD * HUD;
    UIView *backView;
    NSString * tweetScreenName;
    NSArray * friendsId;
    NSMutableArray * followerScreenName,*followersIdList;
}
@property(nonatomic,strong)NSArray * alldata;
@property(nonatomic,strong)UITableView * showFollowing;
@end

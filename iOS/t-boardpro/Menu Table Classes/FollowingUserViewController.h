//
//  FollowingUserViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/25/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@interface FollowingUserViewController : UIViewController<UITableViewDelegate,UITableViewDataSource>
{
    MBProgressHUD * HUD;
}
@property(nonatomic,strong)UITableView * showFollowing;
@property(nonatomic,strong)NSArray * allData;
@property(nonatomic,strong)NSString * nameUser;

@end

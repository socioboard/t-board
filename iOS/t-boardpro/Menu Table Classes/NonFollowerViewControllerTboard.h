//
//  NonFollowerViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 7/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
@interface NonFollowerViewControllerTboard : UIViewController
{
    NSMutableArray * nonFollowerScreenName;
    NSMutableArray * nonFollowersIdList;
    BOOL refresh;
    int currentSelection,offSet;
    NSString * tweetScreenName;
    NSArray * nonFollowerCount;
    BOOL runOnceFlag;
    MBProgressHUD * HUD;
    UIView * backView;
    NSArray * nonFollowerArr;
}
@property(nonatomic,strong)UITableView * showNonFollower;
@property(nonatomic,strong)NSArray * allData;
@end

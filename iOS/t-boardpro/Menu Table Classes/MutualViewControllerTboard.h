//
//  MutualView.h
//  TwitterBoard
//
//  Created by GLB-254 on 7/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
@interface MutualViewControllerTboard : UIViewController
{
    NSMutableArray * nonFollowerScreenName,*nonFollowersIdList;
    NSMutableArray * mutualFollowerID;
    int currentSelection;
    NSString * tweetScreenName;
    BOOL refresh;
    MBProgressHUD * HUD;
    UIView * backView;
}
@property(nonatomic,strong)NSArray * allData;
@property(nonatomic,strong)UITableView * showMutualFollower;
@end

//
//  SearchingViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/23/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@interface SearchingViewController : UIViewController<UITextFieldDelegate,UITableViewDataSource,UITableViewDelegate>
{
    NSString * searchQuery;
    UITableView * toShowSearchUser;
    NSMutableArray * searchUserIds;
    MBProgressHUD * HUD;

}
@property(nonatomic,strong)NSArray * alldata,*friendsId;
@end

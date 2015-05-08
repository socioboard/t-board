//
//  FollwViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TwitterHelperClass.h"
#import "MBProgressHUD.h"
@interface Feeds : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    UITableView * followTableView;
    NSArray * wholeData;
    TwitterHelperClass * twittHelperObj;
    MBProgressHUD * HUD;
}

@end

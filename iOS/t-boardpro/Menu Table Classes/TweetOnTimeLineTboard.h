//
//  TweetOnTimeLine.h
//  TwitterBoard
//
//  Created by GLB-254 on 5/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TweetOnTimeLineTboard.h"
#import "MBProgressHUD.h"
@interface TweetOnTimeLineTboard : UIViewController<UITextViewDelegate,UITableViewDataSource,UITableViewDelegate>
{
    UITableView * userListTable;
    NSMutableArray * selectedData;
    UIView * userListView;
    UILabel * seclectedAccount;
    UITextView * txtView;
    MBProgressHUD * HUD;
    BOOL accountSelected;

}

@end

//
//  FansView.h
//  TwitterBoard
//
//  Created by GLB-254 on 7/22/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
@interface FansViewControllerTboard : UIViewController
{
    NSMutableArray * fansScreenName,*fansIdList;
    NSMutableArray * fansFollowerID;
    NSArray * fansCountArr;
    int currentSelection,offSet;
    BOOL refresh,firstRun;
    MBProgressHUD * HUD;
    UIView * backView;

}
@property(nonatomic,strong)NSArray * allData;
@property(nonatomic,strong)UITableView * fanTable;
@end

//
//  SettingView.h
//  TwitterBoard
//
//  Created by GLB-254 on 5/1/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SettingView : UIView <UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate>
{
    UILabel * nameLbl,*tweetCountLbl,*followedByCountLbl,*followingCountLbl;
}
@property(nonatomic,strong)UIButton * logOutBtn;
@property(nonatomic,strong)id customMenu;
@end

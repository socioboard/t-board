//
//  TableCustomCell.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TableCustomCell : UITableViewCell
{
    
}
@property(nonatomic,strong)UIImageView * userImage;
@property(nonatomic,strong)UIButton * add_minusButton,*tweetButton,*settingBtn;
@property(nonatomic,strong)UILabel * tweetCount,*headingLabel;
@property(nonatomic,strong)UILabel * userNameDesc,*userName,*descriptionLbl,*followerCount,*followingCount;
@property(nonatomic,strong)UIView * cellFooterView;
@property(nonatomic,strong)UITextView *myView;
@end

//
//  TableCustomCell.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TTTAttributedLabel.h"
@interface TableCustomCell : UITableViewCell<TTTAttributedLabelDelegate>
{
    UIView * backViewCell;
    
}
@property(nonatomic,strong)UIImageView * userImage;
@property(nonatomic,strong)UIButton * add_minusButton,*tweetButton,*settingBtn,*favouriteButton;
@property(nonatomic,strong)UIButton * reTweetButton;
@property(nonatomic,strong)UILabel * tweetCount,*headingLabel,*favouriteLbl,*nameLblFeed,* reTweetCount;
@property(nonatomic,strong)TTTAttributedLabel * userNameDesc,*userName,*descriptionLbl,*followerCount,*followingCount,*myView;
@property(nonatomic,strong)UIView * blankView;
@property(nonatomic,strong)UILabel * nameInSchedule,*descriptionInSchedule,*dateLblInSchedule,*deleteLabelInSchedule;
@property(nonatomic,strong)UIView * cellFooterView,*cellBackgroundView,*endingLine;
@property(nonatomic,strong)UIImageView *backGroundImageViewOfCell;
@end

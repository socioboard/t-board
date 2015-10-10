//
//  TableCustomCell.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "TableCustomCell.h"

@implementation TableCustomCell

- (void)awakeFromNib {
    // Initialization code
}
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self)
    {
    if([reuseIdentifier isEqualToString:@"Follow"])
    {
        self.backGroundImageViewOfCell=[[UIImageView alloc]initWithFrame:CGRectMake(5, 0, SCREEN_WIDTH-10, 140)];
        self.backGroundImageViewOfCell.userInteractionEnabled=YES;
//        self.backGroundImageViewOfCell.layer.borderWidth=.5;
//        self.backGroundImageViewOfCell.layer.borderColor=[UIColor grayColor].CGColor;
//       self.backGroundImageViewOfCell.layer.shadowOffset = CGSizeMake(.25,.5);
//        self.backGroundImageViewOfCell.layer.shadowRadius = 0;
//        self.backGroundImageViewOfCell.layer.shadowOpacity = 0.5;
        UIImage *targetImage=[UIImage imageNamed:@"blank_topic_bg.png"];
        self.backGroundImageViewOfCell.image=targetImage;
        [self.contentView addSubview:self.backGroundImageViewOfCell];
        
        self.cellFooterView=[[UIView alloc]init];
        self.cellFooterView.userInteractionEnabled=YES;
        self.cellFooterView.frame=CGRectMake(0,self.contentView.frame.size.height-40, SCREEN_WIDTH,50);
        self.cellFooterView.hidden=YES;
      // [self.backGroundImageViewOfCell addSubview:self.cellFooterView];
        self.endingLine=[[UIView alloc]initWithFrame:CGRectMake(80, 50, SCREEN_WIDTH, 1)];
        self.endingLine.backgroundColor=[UIColor grayColor];
        [self.backGroundImageViewOfCell addSubview:self.endingLine];
        self.nameLblFeed=[[UILabel alloc]init];
        self.nameLblFeed.frame=CGRectMake(70, 5,220,20);
        self.nameLblFeed.font=[UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
        self.nameLblFeed.textColor=ThemeColor;
        self.nameLblFeed.numberOfLines=0;
        self.nameLblFeed.text=@"@sukhmeet singh";
        [self.backGroundImageViewOfCell addSubview:self.nameLblFeed];
        
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(20, 20, 40, 40);
        self.userImage.layer.cornerRadius=20;
        self.userImage.clipsToBounds=YES;
        self.userImage.backgroundColor=[UIColor redColor];
        [self.backGroundImageViewOfCell addSubview:self.userImage];
        
        self.userNameDesc=[[TTTAttributedLabel alloc]init];
        self.userNameDesc.frame=CGRectMake(75,35,220, 80);
        self.userNameDesc.font=[UIFont fontWithName:@"HelveticaNeue" size:15];
        self.userNameDesc.numberOfLines=0;
        self.userNameDesc.lineBreakMode=NSLineBreakByWordWrapping;
        self.userNameDesc.enabledTextCheckingTypes = NSTextCheckingTypeLink;
        self.userNameDesc.delegate=self;
        [self.backGroundImageViewOfCell addSubview:self.userNameDesc];
        self.favouriteLbl=[[UILabel alloc]initWithFrame:CGRectMake(SCREEN_WIDTH-35,115,40,20)];
        self.favouriteLbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        [self.backGroundImageViewOfCell addSubview:self.favouriteLbl];
        self.favouriteButton=[[UIButton alloc]initWithFrame:CGRectMake(SCREEN_WIDTH-70,115,30,30)];
        [self.backGroundImageViewOfCell addSubview:self.favouriteButton];
        self.tweetButton=[[UIButton alloc]init];
        self.tweetButton.frame=CGRectMake(80,115,30,25);
        [self.tweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_reply_focused.png"] forState:UIControlStateNormal];
        [self.backGroundImageViewOfCell addSubview:self.tweetButton];
        //-0----
        self.reTweetButton=[[UIButton alloc]init];
        self.reTweetButton.frame=CGRectMake(SCREEN_WIDTH/2-15,115, 30,25);
        [self.reTweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_rt_off_focused.png"] forState:UIControlStateNormal];
        [self.contentView addSubview:self.reTweetButton];
        self.reTweetCount=[[UILabel alloc]init];
        self.reTweetCount.frame=CGRectMake(SCREEN_WIDTH/2+19, 118, 80, 25);
        self.reTweetCount.text=@"7.979k";
        [self.contentView addSubview:self.reTweetCount];
        //-0----

    }
    else if ([reuseIdentifier isEqualToString:@"accounTable"])
    {
        self.userName=[[TTTAttributedLabel alloc]init];
        self.userName.frame=CGRectMake(40, 0,100,40);
        self.userName.numberOfLines=0;
        self.userName.lineBreakMode=NSLineBreakByTruncatingTail;
        self.userName.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        [self.contentView addSubview:self.userName];
        
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(5, 5,30,30);
        self.userImage.layer.cornerRadius=15;
        self.userImage.clipsToBounds=YES;
        [self.contentView addSubview:self.userImage];
        
         self.settingBtn=[[UIButton alloc]init];
        self.settingBtn.frame=CGRectMake(150, 10,20,20);
        [self.settingBtn setBackgroundImage:[UIImage imageNamed:@"delete_account_icon.png"] forState:UIControlStateNormal];
        self.settingBtn.backgroundColor=[UIColor clearColor];
        [self.contentView addSubview:self.settingBtn];
        
        
    }
    else if ([reuseIdentifier isEqualToString:@"All Follower"])
    {
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(5, 20, 40, 40);
        self.userImage.layer.cornerRadius=20;
        self.userImage.clipsToBounds=YES;
        self.userImage.backgroundColor=[UIColor redColor];
        [self.contentView addSubview:self.userImage];
        
        self.userNameDesc=[[TTTAttributedLabel alloc]init];
        self.userNameDesc.frame=CGRectMake(50, 5, 220,20);
        self.userNameDesc.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:17];;
        self.userNameDesc.numberOfLines=0;
        self.userNameDesc.lineBreakMode=NSLineBreakByWordWrapping;
        [self.contentView addSubview:self.userNameDesc];
        
        self.add_minusButton=[[UIButton alloc]init];
        self.add_minusButton.frame=CGRectMake(SCREEN_WIDTH-80,10,60,30);
        self.add_minusButton.layer.cornerRadius=5;
        [self.add_minusButton setBackgroundImage:[UIImage imageNamed:@"unfollow.png"] forState:UIControlStateNormal];
        [self.contentView addSubview:self.add_minusButton];
        self.cellFooterView=[[UIView alloc]init];
        
        self.cellFooterView.frame=CGRectMake(0,self.contentView.frame.size.height-40, self.contentView.frame.size.width,50);
        self.cellFooterView.backgroundColor=ThemeColor;
        self.cellFooterView.hidden=YES;
         [self.contentView addSubview:self.cellFooterView];
       
        self.tweetButton=[[UIButton alloc]init];
        self.tweetButton.frame=CGRectMake(20,10,80,25);
        [self.tweetButton setTitle:@"tweet" forState:UIControlStateNormal];
        self.tweetButton.titleEdgeInsets=UIEdgeInsetsMake(0, 15, 0, 0);
        self.tweetButton.layer.cornerRadius=5;
        self.tweetButton.clipsToBounds=YES;
        self.tweetButton.layer.borderWidth=1;
        self.tweetButton.layer.borderColor=[UIColor whiteColor].CGColor;
        [self.cellFooterView addSubview:self.tweetButton];
        UIImageView * tweetImage=[[UIImageView alloc]init];
        tweetImage.frame=CGRectMake(2, 2, 20,20);
        tweetImage.image=[UIImage imageNamed:@"followby.png"];
        [self.tweetButton addSubview:tweetImage];
        
        //cell is the TableView's cell
        [self.contentView addSubview:self.myView];
        self.myView=[[TTTAttributedLabel alloc]init];
        self.myView.frame=CGRectMake(50,27,SCREEN_WIDTH-110,50);
        self.myView.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        self.myView.numberOfLines=0;
        self.myView.lineBreakMode=NSLineBreakByWordWrapping;
        [self.contentView addSubview:self.myView];
        
//        self.descriptionLbl=[[UILabel alloc]init];
//        self.descriptionLbl.frame=CGRectMake(50,27,self.contentView.frame.size.width-110,50);
//        self.descriptionLbl.font=[UIFont systemFontOfSize:15];
//        self.descriptionLbl.text=@"Follower Count";
//        self.descriptionLbl.numberOfLines=0;
//        self.descriptionLbl.lineBreakMode=NSLineBreakByTruncatingTail;
//        [self.contentView addSubview:self.descriptionLbl];
        
        self.tweetCount=[[UILabel alloc]init];
        self.tweetCount.frame=CGRectMake(40,40,65,16);
        self.tweetCount.text=@"10";
        self.tweetCount.textAlignment=NSTextAlignmentCenter;
        self.tweetCount.textColor=[UIColor grayColor];
        self.tweetCount.font=[UIFont fontWithName:@"Superclarendon-Italic" size:15];
        [self.contentView addSubview:self.tweetCount];
        
        UILabel * tweetLabel=[[UILabel alloc]init];
        tweetLabel.frame=CGRectMake(40,60,65,13);
        tweetLabel.textAlignment=NSTextAlignmentCenter;
        tweetLabel.textColor=[UIColor grayColor];
        tweetLabel.text=@"Tweets";
        tweetLabel.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:12];
        [self.contentView addSubview:tweetLabel];
        //-----------------------------
        CGFloat hhFollowing=tweetLabel.frame.origin.x+tweetLabel.frame.size.width+10;
        self.followingCount=[[TTTAttributedLabel alloc]init];
        self.followingCount.frame=CGRectMake(hhFollowing,40,65,16);
        self.followingCount.text=@"5";
        self.followingCount.textColor=[UIColor grayColor];
        self.followingCount.textAlignment=NSTextAlignmentCenter;
        self.followingCount.font=[UIFont fontWithName:@"Superclarendon-Italic" size:15];
        [self.contentView addSubview:self.followingCount];
        
        UILabel * followingbLbl=[[UILabel alloc]init];
        followingbLbl.frame=CGRectMake(hhFollowing,60,65,20);
        followingbLbl.text=@"Following";
        followingbLbl.textColor=[UIColor grayColor];
        followingbLbl.textAlignment=NSTextAlignmentCenter;
        followingbLbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:12];
        [self.contentView addSubview:followingbLbl];
        //-----
        CGFloat hhfollowerCount=followingbLbl.frame.origin.x+followingbLbl.frame.size.width+10;
       
        //-----------
        self.followerCount=[[TTTAttributedLabel alloc]init];
        self.followerCount.frame=CGRectMake(hhfollowerCount,40,65,16);
        self.followerCount.textAlignment=NSTextAlignmentCenter;
        self.followerCount.textColor=[UIColor grayColor];
        self.followerCount.font=[UIFont fontWithName:@"Superclarendon-Italic" size:15];
        [self.contentView addSubview:self.followerCount];
        
        UILabel * followerLbl=[[UILabel alloc]init];
        followerLbl.frame=CGRectMake(hhfollowerCount,60,65,20);
        followerLbl.text=@"Follower";
        followerLbl.textColor=[UIColor grayColor];
        followerLbl.textAlignment=NSTextAlignmentCenter;
        followerLbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:12];
        [self.contentView addSubview:followerLbl];
    }
   else if ([reuseIdentifier isEqualToString:@"Setting"])
   {
       self.headingLabel=[[UILabel alloc]init];
       self.headingLabel.frame=CGRectMake(20,5,120,30);
       self.headingLabel.textColor=[UIColor whiteColor];
       [self.contentView addSubview:self.headingLabel];
   }
   else if([reuseIdentifier isEqualToString:@"Tweet"])
   {
       self.backGroundImageViewOfCell=[[UIImageView alloc]initWithFrame:CGRectMake(5, 0, SCREEN_WIDTH-10, 140)];
       self.backGroundImageViewOfCell.userInteractionEnabled=YES;
       self.backGroundImageViewOfCell.layer.shadowOffset = CGSizeMake(0,0);
       self.backGroundImageViewOfCell.layer.shadowRadius = 0;
       self.backGroundImageViewOfCell.layer.shadowOpacity = 0.5;
       UIImage *targetImage=[UIImage imageNamed:@"blank_topic_bg.png"];
       self.backGroundImageViewOfCell.image=targetImage;
       [self.contentView addSubview:self.backGroundImageViewOfCell];
       
       self.nameLblFeed=[[UILabel alloc]init];
       self.nameLblFeed.frame=CGRectMake(70, 5,220,20);
       self.nameLblFeed.font=[UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
       self.nameLblFeed.textColor=ThemeColor;
       self.nameLblFeed.numberOfLines=0;
       self.nameLblFeed.text=@"@sukhmeet singh";
       [self.backGroundImageViewOfCell addSubview:self.nameLblFeed];
       
       self.userImage=[[UIImageView alloc]init];
       self.userImage.frame=CGRectMake(20, 20, 40, 40);
       self.userImage.layer.cornerRadius=20;
       self.userImage.clipsToBounds=YES;
       self.userImage.backgroundColor=[UIColor redColor];
       [self.backGroundImageViewOfCell addSubview:self.userImage];
       
       self.userNameDesc=[[TTTAttributedLabel alloc]init];
       self.userNameDesc.frame=CGRectMake(80,35,220, 80);
       self.userNameDesc.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
       self.userNameDesc.numberOfLines=0;
       self.userNameDesc.lineBreakMode=NSLineBreakByWordWrapping;
       self.userNameDesc.enabledTextCheckingTypes = NSTextCheckingTypeLink;
       self.userNameDesc.delegate=self;
       [self.backGroundImageViewOfCell addSubview:self.userNameDesc];
   }
   else if ([reuseIdentifier isEqualToString:@"ShowUserList"])
   {
       self.userImage=[[UIImageView alloc]init];
       self.userImage.frame=CGRectMake(5, 5,30,30);
       self.userImage.layer.cornerRadius=15;
       self.userImage.clipsToBounds=YES;
       self.userImage.backgroundColor=[UIColor redColor];
       [self.contentView addSubview:self.userImage];
       
       self.nameLblFeed=[[UILabel alloc]init];
       self.nameLblFeed.frame=CGRectMake(70, 5,220,20);
       self.nameLblFeed.font=[UIFont fontWithName:@"HelveticaNeue-Bold" size:15];
       self.nameLblFeed.textColor=ThemeColor;
       self.nameLblFeed.numberOfLines=0;
       self.nameLblFeed.text=@"";
       [self.contentView addSubview:self.nameLblFeed];

   }
   else if ([reuseIdentifier isEqualToString:@"ScheduleTable"])
   {
       self.blankView=[[UIView alloc]initWithFrame:CGRectMake(2, 2,SCREEN_WIDTH-16, 120)];
       self.blankView.layer.borderColor=[UIColor blackColor].CGColor;
       self.blankView.layer.borderWidth=1;
       [self.contentView addSubview:self.blankView];
       //Vertical Line
       UIView * verticalLine=[[UIView alloc]init];
       verticalLine.frame=CGRectMake(self.contentView.frame.size.width/2-1, 0,2,35);
       verticalLine.backgroundColor=[UIColor grayColor];
       [self.blankView addSubview:verticalLine];
       //Horizontal Line
       UIView * horizontalLine=[[UIView alloc]init];
       horizontalLine.backgroundColor=[UIColor grayColor];
       horizontalLine.frame=CGRectMake(0,36, SCREEN_WIDTH-16,2);
       [self.blankView addSubview:horizontalLine];
       //Name
       CGFloat heightForName=horizontalLine.frame.size.height+horizontalLine.frame.origin.y+5;
       self.nameInSchedule=[[UILabel alloc]initWithFrame:CGRectMake(20, heightForName, self.contentView.frame.size.width-40, 20)];
       [self.contentView addSubview:self.nameInSchedule];
       
       //Description
       CGFloat heightForDescription=self.nameInSchedule.frame.size.height+self.nameInSchedule.frame.origin.y+10;
       self.descriptionInSchedule=[[UILabel alloc]initWithFrame:CGRectMake(20,heightForDescription, self.contentView.frame.size.width-40, 20)];
       [self.contentView addSubview:self.descriptionInSchedule];
       //Pin imageview
       UIImageView * pinImage=[[UIImageView alloc
                                ]initWithFrame:CGRectMake(5, 5, 30,30)];
       pinImage.image=[UIImage imageNamed:@"pin.png"];
       [self.contentView addSubview:pinImage];
       
       //Delete imageview
       UIImageView * deleteImage=[[UIImageView alloc
                                ]initWithFrame:CGRectMake(5, 5, 30,30)];
       deleteImage.image=[UIImage imageNamed:@"pin.png"];
       [self.contentView addSubview:deleteImage];
       //Date Label
       self.dateLblInSchedule=[[UILabel alloc]initWithFrame:CGRectMake(45, 5, 100, 20)];
       self.dateLblInSchedule.text=@"22/08/1992";
       [self.blankView addSubview:self.dateLblInSchedule];
       //Delete label
       self.deleteLabelInSchedule=[[UILabel alloc]initWithFrame:CGRectMake(verticalLine.frame.origin.x+40, 5,100, 20)];
       self.deleteLabelInSchedule.text=@"23:45";
       [self.blankView addSubview:self.deleteLabelInSchedule];
       
   }
    
    }
    return self;
}

#pragma mark - TTTAttributedLabelDelegate

- (void)attributedLabel:(__unused TTTAttributedLabel *)label
   didSelectLinkWithURL:(NSURL *)url
{
    [[UIApplication sharedApplication] openURL:url];
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

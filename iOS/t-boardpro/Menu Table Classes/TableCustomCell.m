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
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(20, 20, 40, 40);
        self.userImage.layer.cornerRadius=20;
        self.userImage.clipsToBounds=YES;
        self.userImage.backgroundColor=[UIColor redColor];
        [self.contentView addSubview:self.userImage];
        
        self.userNameDesc=[[UILabel alloc]init];
        self.userNameDesc.frame=CGRectMake(80, 5, 220, 80);
        self.userNameDesc.font=[UIFont systemFontOfSize:15];
        self.userNameDesc.numberOfLines=0;
        self.userNameDesc.lineBreakMode=NSLineBreakByWordWrapping;
        [self.contentView addSubview:self.userNameDesc];
        
    }
    else if ([reuseIdentifier isEqualToString:@"accounTable"])
    {
        self.userName=[[UILabel alloc]init];
        self.userName.frame=CGRectMake(40, 0,60,40);
        self.userName.numberOfLines=0;
        self.userName.lineBreakMode=NSLineBreakByTruncatingTail;
        self.userName.font=[UIFont systemFontOfSize:15];
        [self.contentView addSubview:self.userName];
        
        self.userImage=[[UIImageView alloc]init];
        self.userImage.frame=CGRectMake(5, 5,30,30);
        self.userImage.layer.cornerRadius=15;
        self.userImage.clipsToBounds=YES;
        [self.contentView addSubview:self.userImage];
        
         self.settingBtn=[[UIButton alloc]init];
        self.settingBtn.frame=CGRectMake(105, 10,30,30);
        [self.settingBtn setBackgroundImage:[UIImage imageNamed:@"setting.png"] forState:UIControlStateNormal];
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
        
        self.userNameDesc=[[UILabel alloc]init];
        self.userNameDesc.frame=CGRectMake(50, 5, 220,20);
        self.userNameDesc.font=[UIFont systemFontOfSize:17];
        self.userNameDesc.numberOfLines=0;
        self.userNameDesc.lineBreakMode=NSLineBreakByWordWrapping;
        [self.contentView addSubview:self.userNameDesc];
        
        self.add_minusButton=[[UIButton alloc]init];
        self.add_minusButton.frame=CGRectMake(SCREEN_WIDTH-60,40,30,30);
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
        
        self.myView = [[UITextView alloc] initWithFrame:CGRectMake(50,27,SCREEN_WIDTH-110,50)];
        self.myView.editable = NO;
        self.myView.font=[UIFont systemFontOfSize:15];
        self.myView.dataDetectorTypes = UIDataDetectorTypeLink;
        //cell is the TableView's cell
        [self.contentView addSubview:self.myView];
        
        
//        self.descriptionLbl=[[UILabel alloc]init];
//        self.descriptionLbl.frame=CGRectMake(50,27,self.contentView.frame.size.width-110,50);
//        self.descriptionLbl.font=[UIFont systemFontOfSize:15];
//        self.descriptionLbl.text=@"Follower Count";
//        self.descriptionLbl.numberOfLines=0;
//        self.descriptionLbl.lineBreakMode=NSLineBreakByTruncatingTail;
//        [self.contentView addSubview:self.descriptionLbl];
        
        self.followerCount=[[UILabel alloc]init];
        self.followerCount.frame=CGRectMake(SCREEN_WIDTH-60,95, 50,13);
        self.followerCount.textAlignment=NSTextAlignmentCenter;
        self.followerCount.font=[UIFont boldSystemFontOfSize:13];
        [self.contentView addSubview:self.followerCount];

        UILabel * followerLbl=[[UILabel alloc]init];
        followerLbl.frame=CGRectMake(SCREEN_WIDTH-60,105, 50,20);
        followerLbl.text=@"Follower";
        followerLbl.font=[UIFont boldSystemFontOfSize:10];
        [self.contentView addSubview:followerLbl];
        //-----------------------------
        self.followingCount=[[UILabel alloc]init];
        self.followingCount.frame=CGRectMake(SCREEN_WIDTH/2-25,95, 50,13);
        self.followingCount.text=@"5";
        self.followingCount.textAlignment=NSTextAlignmentCenter;
        self.followingCount.font=[UIFont boldSystemFontOfSize:13];
        [self.contentView addSubview:self.followingCount];
        
        UILabel * followingbLbl=[[UILabel alloc]init];
        followingbLbl.frame=CGRectMake(SCREEN_WIDTH/2-25,105, 50,20);
        followingbLbl.text=@"Following";
        followingbLbl.font=[UIFont boldSystemFontOfSize:10];
        [self.contentView addSubview:followingbLbl];
        
        self.tweetCount=[[UILabel alloc]init];
        self.tweetCount.frame=CGRectMake(28,95,75,13);
        self.tweetCount.text=@"10";
        self.tweetCount.font=[UIFont boldSystemFontOfSize:13];
        [self.contentView addSubview:self.tweetCount];
        
        UILabel * tweetLabel=[[UILabel alloc]init];
        tweetLabel.frame=CGRectMake(30,105,75,13);
        tweetLabel.text=@"Tweets";
        tweetLabel.font=[UIFont boldSystemFontOfSize:10];
        [self.contentView addSubview:tweetLabel];
    }
   else if ([reuseIdentifier isEqualToString:@"Setting"])
   {
       self.headingLabel=[[UILabel alloc]init];
       self.headingLabel.frame=CGRectMake(20,5,120,30);
       self.headingLabel.textColor=[UIColor whiteColor];
       [self.contentView addSubview:self.headingLabel];
   }
    }
    return self;
}
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end

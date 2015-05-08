//
//  SettingView.m
//  TwitterBoard
//
//  Created by GLB-254 on 5/1/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SettingView.h"
#import "Singleton.h"
#import "TableCustomCell.h"
#import "UIImageView+WebCache.h"
@implementation SettingView

-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
    self.backgroundColor=[UIColor grayColor];
    
    UIImageView * imgView=[[UIImageView alloc]init];
    imgView.frame=CGRectMake(20, 20, 60, 60);
    imgView.layer.cornerRadius=30;
    imgView.clipsToBounds=YES;
    imgView.layer.borderWidth=1;
    imgView.layer.borderColor=[UIColor whiteColor].CGColor;
    NSURL * fetchImage=[NSURL URLWithString:[Singleton sharedSingleton].imageUrl];
    [imgView sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@""]];
    [self addSubview:imgView];
    
    nameLbl=[self initializeLabel:nameLbl];
    nameLbl.text=[Singleton sharedSingleton].mainUserRealName;
    nameLbl.frame=CGRectMake(90,20,200,40);
    nameLbl.textAlignment=NSTextAlignmentLeft;
    [self addSubview:nameLbl];
   
    tweetCountLbl=[self initializeLabel:tweetCountLbl];
    tweetCountLbl.text=[NSString stringWithFormat:@"Tweets \n   %@",[Singleton sharedSingleton].tweetCount];
    tweetCountLbl.numberOfLines=0;
    tweetCountLbl.lineBreakMode=NSLineBreakByTruncatingTail;
    tweetCountLbl.frame=CGRectMake(20,120,80,45);
    [self addSubview:tweetCountLbl];
       
    followedByCountLbl=[self initializeLabel:followedByCountLbl];
    followedByCountLbl.text=[NSString stringWithFormat:@"Follower \n   %@",[Singleton sharedSingleton].followerCount];
    followedByCountLbl.frame=CGRectMake(SCREEN_WIDTH/2-40,120,80,45);
    followedByCountLbl.numberOfLines=0;
    followedByCountLbl.lineBreakMode=NSLineBreakByTruncatingTail;
    [self addSubview:followedByCountLbl];
        
    followingCountLbl=[self initializeLabel:followingCountLbl];
    followingCountLbl.text=[NSString stringWithFormat:@"Following \n   %@",[Singleton sharedSingleton].followingCount];
    followingCountLbl.frame=CGRectMake(SCREEN_WIDTH-90,120,100,45);;
    followingCountLbl.numberOfLines=0;
    followingCountLbl.lineBreakMode=NSLineBreakByTruncatingTail;
    [self addSubview:followingCountLbl];
    
    UIButton * closeBtn=[[UIButton alloc]init];
    closeBtn.frame=CGRectMake(SCREEN_WIDTH-90,20,80, 40);
        [closeBtn setBackgroundImage:[UIImage imageNamed:@"close.png"] forState:UIControlStateNormal];
        [closeBtn setTitle:@"Close" forState:UIControlStateNormal];
    [closeBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [closeBtn addTarget:self action:@selector(closeTheView) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:closeBtn];
        
    self.logOutBtn=[[UIButton alloc]init];
     self.logOutBtn.frame=CGRectMake(20,SCREEN_WIDTH/2+40, SCREEN_WIDTH-40, 40);
     self.logOutBtn.backgroundColor=Header_Text_Color;
    [ self.logOutBtn setTitle:@"Remove Account" forState:UIControlStateNormal];
    [self.logOutBtn addTarget:self action:@selector(logOutBtn:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview: self.logOutBtn];
        
    }
    return  self;
}

-(UILabel *)initializeLabel:(UILabel *)label
{
    label = [[UILabel alloc] init];
    label.textColor = [UIColor blackColor];
    label.font =[UIFont boldSystemFontOfSize:15];
    return label;
}
#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Setting";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    cell.contentView.backgroundColor=[UIColor grayColor];
    if(indexPath.row==0)
    {
        cell.headingLabel.text=@"Email";
        UITextField * txtField=[[UITextField alloc]init];
        txtField.frame=CGRectMake(20,60, cell.contentView.frame.size.width-40,40);
        txtField.backgroundColor=[UIColor whiteColor];
        txtField.delegate=self;
        UIView *paddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 20)];
        txtField.leftView = paddingView;
        txtField.leftViewMode = UITextFieldViewModeAlways;
        txtField.placeholder=@"Enter Your Email Id";
        [cell.contentView addSubview:txtField];

    }
    else if (indexPath.row==1)
    {
        cell.headingLabel.text=@"Log Out";
       self.logOutBtn=[[UIButton alloc]init];
        self.logOutBtn.frame=CGRectMake(20,60, cell.contentView.frame.size.width-40,40);
        [self.logOutBtn setTitle:@"Log Out" forState:UIControlStateNormal];
        [self.logOutBtn addTarget:self action:@selector(logOutBtn:) forControlEvents:UIControlEventTouchUpInside];
        self.logOutBtn.titleLabel.textColor=[UIColor whiteColor];
        [self.logOutBtn setBackgroundColor:[UIColor blackColor]];
        [cell.contentView addSubview:self.logOutBtn];
 
    }
    else
    {
        cell.headingLabel.text=@"Log Out";
 
    }
    return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    
    return 1;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 160;
}

-(void)logOutBtn:(id)sender
{
    [self removeFromSuperview];
    [[NSNotificationCenter defaultCenter]postNotificationName:@"LogOutFromSetting" object:nil];
}

-(void)closeTheView
{
    [self removeFromSuperview];
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end

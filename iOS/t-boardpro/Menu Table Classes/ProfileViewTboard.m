//
//  ProfileView.m
//  TwitterBoard
//
//  Created by GLB-254 on 5/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "ProfileViewTboard.h"
#import "UIImageView+WebCache.h"
#import "SingletonTboard.h"
@implementation ProfileViewTboard
-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        self.backgroundColor=[UIColor colorWithRed:(CGFloat)220/255 green:(CGFloat)220/255 blue:(CGFloat)220/255 alpha:1];
        profileImage.hidden=NO;
        nameView.hidden=NO;
        bannerImage=[[UIImageView alloc]initWithFrame:CGRectMake(0, 10, SCREEN_WIDTH,170)];
        bannerImage.image=[UIImage imageNamed:@"banner.jpg"];
        [self addSubview:bannerImage];
        
        closeBtn=[[UIButton alloc]initWithFrame:CGRectMake(SCREEN_WIDTH-30,0, 30, 30)];
        [closeBtn setImage:[UIImage imageNamed:@"close_btnPopUp.png"] forState:UIControlStateNormal];
        closeBtn.layer.cornerRadius=15;
        closeBtn.clipsToBounds=YES;
        closeBtn.backgroundColor=[UIColor whiteColor];
        [closeBtn addTarget:self action:@selector(closeTheView) forControlEvents:UIControlEventTouchUpInside];
        [self insertSubview:closeBtn aboveSubview:self];

        
        profileImage=[[UIImageView alloc]initWithFrame:CGRectMake(10,100, 70, 70)];
        profileImage.image=[UIImage imageNamed:@""];
        profileImage.layer.borderColor=[UIColor whiteColor].CGColor;
        profileImage.layer.borderWidth=2;
        profileImage.backgroundColor=[UIColor redColor];
        [self addSubview:profileImage];
        
        nameView=[[UIView alloc]initWithFrame:CGRectMake(SCREEN_WIDTH/3-20, 120, SCREEN_WIDTH-SCREEN_WIDTH/3-20, 50)];
        nameView.backgroundColor=[UIColor whiteColor];
        [self addSubview:nameView];
        
        
       
        
        lblName=[[UILabel alloc]initWithFrame:CGRectMake(10,5, 200,20)];
        lblName.text=@"Sukhmeet singh";
        lblName.font=[UIFont systemFontOfSize:15];
        lblName.textColor=[UIColor blackColor];
        [nameView addSubview:lblName];
      
        //---------
        twtView=[[UIView alloc]initWithFrame:CGRectMake(10, 180, SCREEN_WIDTH-20,100)];
        twtView.backgroundColor=[UIColor whiteColor];
        [self addSubview:twtView];
        
        lblNameTwt=[[UILabel alloc]initWithFrame:CGRectMake(10, 30, 200, 20)];
        lblNameTwt.text=@"@SukhMeet100";
        lblNameTwt.font=[UIFont systemFontOfSize:15];
        [nameView addSubview:lblNameTwt];

        
        tweetLbl=[[UILabel alloc]initWithFrame:CGRectMake(10,10, 100,15)];
        tweetLbl.text=@"Tweets:";
        tweetLbl.font=[UIFont systemFontOfSize:13];
        tweetLbl.textColor=[UIColor grayColor];
        [twtView addSubview:tweetLbl];
        
        tweetlbl2 =[[UILabel alloc]initWithFrame:CGRectMake(120,10, 100,15)];
        tweetlbl2.font=[UIFont systemFontOfSize:13];
        tweetlbl2.text=@"7777";
        tweetlbl2.textColor=[UIColor blackColor];
        [twtView addSubview:tweetlbl2];
        
        followerLbl=[[UILabel alloc]initWithFrame:CGRectMake(10,30, 100,15)];
        followerLbl.text=@"Following:";
        followerLbl.font=[UIFont systemFontOfSize:13];
        followerLbl.textColor=[UIColor grayColor];
        [twtView addSubview:followerLbl];
     
        followerLbl2 =[[UILabel alloc]initWithFrame:CGRectMake(120,30, 100,15)];
        followerLbl2.font=[UIFont systemFontOfSize:13];
        followerLbl2.text=@"77";
        followerLbl2.textColor=[UIColor blackColor];
        [twtView addSubview:followerLbl2];

        
        followByLbl=[[UILabel alloc]initWithFrame:CGRectMake(10,50, 100,15)];
        followByLbl.font=[UIFont systemFontOfSize:13];
        followByLbl.textColor=[UIColor grayColor];
        followByLbl.text=@"Followed By:";
        [twtView addSubview:followByLbl];
       
        followByLbl2 =[[UILabel alloc]initWithFrame:CGRectMake(120,50, 100,15)];
        followByLbl2.font=[UIFont systemFontOfSize:13];
        followByLbl2.text=@"77";
        followByLbl2.textColor=[UIColor blackColor];
        [twtView addSubview:followByLbl2];
        
        createdAtLbl=[[UILabel alloc]initWithFrame:CGRectMake(10,70,100,15)];
        createdAtLbl.font=[UIFont systemFontOfSize:13];
        createdAtLbl.textColor=[UIColor grayColor];
        createdAtLbl.text=@"Created at:";
        [twtView addSubview:createdAtLbl];
        
        createdAtLbl2 =[[UILabel alloc]initWithFrame:CGRectMake(120,70, 200,20)];
        createdAtLbl2.font=[UIFont systemFontOfSize:13];
        createdAtLbl2.text=@"25-july-2010";
//        lblNameTwt.font=[UIFont systemFontOfSize:11];
        createdAtLbl2.textColor=[UIColor blackColor];
        [twtView addSubview:createdAtLbl2];
        //----
        
        favView=[[UIView alloc]initWithFrame:CGRectMake(10,290, SCREEN_WIDTH-20,60)];
        favView.backgroundColor=[UIColor whiteColor];
        [self addSubview:favView];
//---
        UILabel *favourites =[[UILabel alloc]initWithFrame:CGRectMake(10,5, 100,20)];
        favourites.text=@"Favourites:";
        favourites.textColor=[UIColor grayColor];
        [favView addSubview:favourites];
       
        favouritesCount =[[UILabel alloc]initWithFrame:CGRectMake(40,25,100,20)];
        favouritesCount.textAlignment=NSTextAlignmentLeft;
        favouritesCount.text=@"10";
        favouritesCount.textColor=[UIColor blackColor];
        [favView addSubview:favouritesCount];
        
        UIButton *tweetBtn = [[UIButton alloc]initWithFrame:CGRectMake(20,SCREEN_HEIGHT-110,SCREEN_WIDTH-40,40)];
        [tweetBtn addTarget:self action:@selector(followedAction) forControlEvents:UIControlEventTouchUpInside];
        [tweetBtn setBackgroundColor:ThemeColor];
        [tweetBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [tweetBtn setTitle:@"Tweet" forState:UIControlStateNormal];
        tweetBtn.titleLabel.font=[UIFont systemFontOfSize:11];
        //[self addSubview:tweetBtn];
        
    }
    return self;
}
+(void)setToLabel:(UILabel*)lbl Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size Color:(UIColor*)color
{
    lbl.textColor = color;
    if (txt != nil) {
        lbl.text = txt;
    }
    if (font != nil) {
        lbl.font = [UIFont fontWithName:font size:_size];
    }
}

+(void)setButton:(UIButton*)btn Text:(NSString*)txt WithFont:(NSString*)font FSize:(float)_size TitleColor:(UIColor*)t_color ShadowColor:(UIColor*)s_color
{
    [btn setTitle:txt forState:UIControlStateNormal];
    [btn setTitleColor:t_color forState:UIControlStateNormal];
    if (s_color != nil) {
        [btn setTitleShadowColor:s_color forState:UIControlStateNormal];
    }
    if (font != nil) {
        btn.titleLabel.font = [UIFont fontWithName:font size:_size];
    }
    else{
        btn.titleLabel.font = [UIFont systemFontOfSize:_size];
    }
    
    if (font!=nil) {
        btn.titleLabel.font= [UIFont fontWithName:font size:_size];
    }
    else{
        
        btn.titleLabel.font=[UIFont systemFontOfSize:_size];
    }
}
-(void)fetchUserClickedTimeline
{
    id result=[[FHSTwitterEngine sharedEngine]getProfileDetail:self.userScreenName andSize:FHSTwitterEngineImageSizeNormal];
    NSLog(@"user detail %@",result);
    if ([result isKindOfClass:[NSError class]])
    {
        
    }
    else
    {

    tweetlbl2.text=[NSString stringWithFormat:@"%ld",(long)[[result objectForKey:@"statuses_count"] integerValue]];
    followerLbl2.text=[NSString stringWithFormat:@"%ld",(long)[[result objectForKey:@"followers_count"] integerValue]];
    followByLbl2.text=[NSString stringWithFormat:@"%ld",(long)[[result objectForKey:@"friends_count"] integerValue]];
    lblName.text=[result objectForKey:@"name"];
        NSString * createdDate=[NSString stringWithFormat:@"%@",[result objectForKey:@"created_at"]];
        NSArray * stringArray=[createdDate componentsSeparatedByString:@"+"];
    createdAtLbl2.text=[stringArray objectAtIndex:0];
   lblNameTwt.text=[NSString stringWithFormat:@"@%@",[result objectForKey:@"screen_name"]];;
    
    favouritesCount.text=[NSString stringWithFormat:@"%ld",(long)[[result objectForKey:@"favourites_count"] integerValue]];
    NSURL * bannerImageUrl=[NSURL URLWithString:[result objectForKey:@"profile_banner_url"]];
    NSURL * profileImageUrl=[NSURL URLWithString:[result objectForKey:@"profile_image_url"]];
    [bannerImage sd_setImageWithURL:bannerImageUrl placeholderImage:[UIImage imageNamed:@""]];
    [profileImage sd_setImageWithURL:profileImageUrl placeholderImage:[UIImage imageNamed:@""]];
        if([self.profileView isEqualToString:@"MainUser"])
        {
            closeBtn.hidden=YES;
            bannerImage.frame=CGRectMake(0, 0,SCREEN_WIDTH,170);
        }
        else
        {
            
        }

    }
}
-(void)followedAction
{
    NSLog(@"All User Data %@",[SingletonTboard sharedSingleton].allDataUser);
    NSArray * allUserData=[SingletonTboard sharedSingleton].allDataUser;
    for(int i=0;i<[allUserData count];i++)
    {
        NSDictionary * dict=[allUserData objectAtIndex:i];
        NSString *straccessToken=[dict objectForKey:@"AccessTokenTwitter"];
       [[NSUserDefaults standardUserDefaults]setObject:straccessToken  forKey:@"SavedAccessHTTPBody"];
       [[FHSTwitterEngine sharedEngine]loadAccessToken];
        id returnedData=[[FHSTwitterEngine sharedEngine]followUser:self.userScreenName isID:NO];
        NSLog(@"returned result after follow %@",returnedData);
    }
    [[NSUserDefaults standardUserDefaults]setObject:[SingletonTboard sharedSingleton].accessTokenCurrent  forKey:@"SavedAccessHTTPBody"];
    [[FHSTwitterEngine sharedEngine]loadAccessToken];

}
-(void)userPersonalDetail
{
    
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

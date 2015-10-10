//
//  ProfileView.h
//  TwitterBoard
//
//  Created by GLB-254 on 5/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ProfileViewTboard : UIView
{
    UIImageView * profileImage;
    UILabel * lblName, * lblNameTwt;
    UILabel * tweetLbl,* followerLbl,* followByLbl,*createdAtLbl,*favouritesLbl;
    UIImageView * bannerImage;
    UIView *nameView, *twtView, *favView;
    UILabel *favouritesCount;
    UILabel *tweetlbl2,*followerLbl2,*followByLbl2,*createdAtLbl2;
    UIButton * closeBtn;
}
@property(nonatomic,strong)NSString * profileView;
@property(nonatomic,strong)NSString * userScreenName;
@property(nonatomic,strong)NSDictionary * userDetails;
-(void)fetchUserClickedTimeline;
@end

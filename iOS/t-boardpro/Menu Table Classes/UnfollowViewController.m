//
//  UnfollowViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "UnfollowViewController.h"
#import "FHSTwitterEngine.h"
#import "Singleton.h"
#import "TableCustomCell.h"
#import "UIImageView+WebCache.h"
#import "FollowingUserViewController.h"
#import "MBProgressHUD.h"

@interface UnfollowViewController ()

@end

@implementation UnfollowViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    //-------------
    followersIdList=[[NSMutableArray alloc]init];
    currentSelection=-1;
    //---------------
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(findFollowingList) name:@"ReloadTimeLine" object:nil];
    //Method for Ui of View

    [self tableToShowFollowUser];
    if([Singleton networkCheck])
    {
        //Find following list of User
        [self findFollowingList];
    }
    else
    {
        UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [noInternet show];
    }
    // Do any additional setup after loading the view.
}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    

}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];

}
-(void)findFollowingList
{
    [self showHUDLoadingView:nil];

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {

            [followersIdList removeAllObjects];
    if ([FHSTwitterEngine sharedEngine].isAuthorized)
    {
        id returned=[[FHSTwitterEngine sharedEngine]listFriendsForUser:[Singleton sharedSingleton].currectUserTwitterId isID:YES withCursor:@"-1"];
        if ([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            NSLog(@"friends list  %@",[returned objectForKey:@"users"]);
            NSArray * array=[returned objectForKey:@"users"];
            NSMutableArray * tempArray=[[NSMutableArray alloc]init];
            for (int i=0;i<[array count];i++)
            {
                NSMutableDictionary * tempDict=[[NSMutableDictionary alloc]init];
                //--------------------
                NSDictionary * dict=[array objectAtIndex:i];
                NSLog(@"friends list  %@",[dict objectForKey:@"profile_image_url_https"]);
                [tempDict setObject:[dict objectForKey:@"profile_image_url_https"] forKey:@"ImageUrl"];
                [tempDict setObject:[dict objectForKey:@"name"] forKey:@"FollowerName"];
                [tempDict setObject:[dict objectForKey:@"description"] forKey:@"dataDesc"];
                [tempDict setObject:[dict objectForKey:@"followers_count"] forKey:@"FollowersCount"];
                [tempDict setObject:[dict objectForKey:@"friends_count"] forKey:@"FriendCount"];
                [tempDict setObject:[dict objectForKey:@"statuses_count"] forKey:@"TweetCount"];
                [followersIdList addObject:[dict objectForKey:@"id_str"]];
                [tempArray addObject:tempDict];
            }
          
            NSLog(@"Whole data %@",tempArray);
            self.alldata=[NSArray arrayWithArray:tempArray];
            NSLog(@"Whole data %@ %d",self.alldata,[self.alldata count]);
        }
        
    }
    else
    {
        NSLog(@"Not Authorized");
    }
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool {
                    [self.showFollowing reloadData];
                    [self hideHUDLoadingView];
                    // [av show];
                }
            });
        }
    });


}
#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier=@"All Follower";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    NSDictionary * dataDict=[self.alldata objectAtIndex:indexPath.section];
    cell.userNameDesc.text=[dataDict objectForKey:@"FollowerName"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"ImageUrl"]] placeholderImage:[UIImage imageNamed:@""]];
    cell.userImage.tag=indexPath.section;
    cell.userImage.userInteractionEnabled=YES;
    UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapOnImage:)];
    [cell.userImage addGestureRecognizer:tapGesture];
    cell.myView.text=[dataDict objectForKey:@"dataDesc"];
    cell.followerCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FollowersCount"] intValue]];
    cell.followingCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FriendCount"] intValue]];
    cell.tweetCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"TweetCount"] intValue]];
    //---------
    cell.add_minusButton.tag=indexPath.section;
    [cell.add_minusButton addTarget:self action:@selector(follow_unfollowButton:) forControlEvents:UIControlEventTouchUpInside];
    [cell.tweetButton addTarget:self action:@selector(tweetAction:) forControlEvents:UIControlEventTouchUpInside];
    cell.tweetButton.tag=indexPath.section;
    if(currentSelection==indexPath.section)
    {
        cell.cellFooterView.hidden=false;
        cell.cellFooterView.frame=CGRectMake(0,120, cell.contentView.frame.size.width,40);
        [UIView animateWithDuration:1 animations:^{
            cell.contentView.layer.opacity = 1.0f;
        }];

    }
    else
    {
        [UIView animateWithDuration:1 animations:^{
            cell.contentView.layer.opacity = 1.0f;
        }];

        cell.cellFooterView.hidden=true;
 
    }

    return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (currentSelection == indexPath.section)
    {
        currentSelection = -1;
        [tableView reloadData];
        return;
    }
      NSDictionary * dataDict=[self.alldata objectAtIndex:indexPath.section];
    NSLog(@"Follower Id %@",[dataDict objectForKey:@""]);
    NSInteger row = [indexPath section];
    currentSelection = row;
    [self.showFollowing reloadData];
    
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    
    return [self.alldata count];
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"index path section %ld",(long)indexPath.section);
    if(currentSelection==indexPath.section)
    {
        return 160;

    }
    else
    {
        return 140;
    }
}
#pragma mark Tweet User Timeline-------
-(void)tweetAction:(UIButton *)btn
{
    NSLog(@"button.tag %ld",(long)btn.tag);
    UIAlertView *myAlertView = [[UIAlertView alloc] initWithTitle:@"Tweet"
                                                                       message:@"Enter the text" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
    myAlertView.alertViewStyle = UIAlertViewStylePlainTextInput;
    [myAlertView show];

    
}
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSLog(@"Tweet Text%@", [alertView textFieldAtIndex:0].text);
//    NSData * data=UIImagePNGRepresentation([UIImage imageNamed:@"follow.png"]);
//    [[FHSTwitterEngine sharedEngine]postTweet:@"Hello" withImageData:data];
//    
    [[FHSTwitterEngine sharedEngine]postTweet:[alertView textFieldAtIndex:0].text];
}
-(void)tableToShowFollowUser
{
       self.showFollowing=[[UITableView alloc]initWithFrame:CGRectMake(0, 0,SCREEN_WIDTH, SCREEN_HEIGHT) style:UITableViewStylePlain];
    self.showFollowing.delegate=self;
    self.showFollowing.dataSource=self;
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,SCREEN_WIDTH, 80);
    self.showFollowing.tableFooterView=footerView;
    [self.view addSubview:self.showFollowing];
    
}
-(void)follow_unfollowButton:(UIButton *)btn
{
    NSLog(@"Follwer list id %@",[followersIdList objectAtIndex:btn.tag]);
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            
            
            if ([FHSTwitterEngine sharedEngine].isAuthorized)
            {
                id returned=[[FHSTwitterEngine sharedEngine]unfollowUser:[followersIdList  objectAtIndex:btn.tag] isID:YES];
                if ([returned isKindOfClass:[NSError class]])
                {
                }
                else
                {
                    dispatch_sync(dispatch_get_main_queue(), ^{
                        @autoreleasepool {
                            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:self.alldata];
                            [tempArray removeObjectAtIndex:btn.tag];
                            self.alldata=tempArray;
                            [self.showFollowing reloadData];                        }
                    });

                  
                    NSLog(@"unfollowed");
                }
        
                
            }
            else
            {
                NSLog(@"Not Authorized");
            }
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool {
                    // [av show];
                }
            });
        }
    });
  
}
#pragma mark
-(void)tapOnImage:(UITapGestureRecognizer*)recognizer
{
    NSDictionary * dict=[self.alldata objectAtIndex:recognizer.view.tag];
    [self fetchUser:[followersIdList objectAtIndex:recognizer.view.tag] nameOfUser:[dict objectForKey:@"FollowerName"]];
}
-(void)fetchUser:(NSString *)timeLineId nameOfUser:(NSString*)userName
{
        NSMutableArray * tempArray=[[NSMutableArray alloc]init];

    if ([FHSTwitterEngine sharedEngine].isAuthorized)
    {
        id returned=[[FHSTwitterEngine sharedEngine]getTimelineForUser:timeLineId isID:YES count:20];
        NSString * title;
        NSString * message;
        if ([returned isKindOfClass:[NSError class]])
        {
            NSError *error = (NSError *)returned;
            title = [NSString stringWithFormat:@"Error %ld",(long)error.code];
            message = error.localizedDescription;
            NSLog(@"Error in finding home timeline %@ %@",message,title);
        }
        else
        {
            NSLog(@"Home timeline%@",returned);
            for (int i=0;i<[returned count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[returned objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [tempArray addObject:tempForEachRow];
                //----------------------------
            }
            //Copy the data in whole data
        }
    }
    FollowingUserViewController * fllObj=[[FollowingUserViewController alloc]init];
    fllObj.allData=tempArray;
    fllObj.nameUser=userName;
    [self.navigationController pushViewController:fllObj animated:YES];
    
}
#pragma mark -
#pragma mark - Loading View mbprogresshud

-(void) showHUDLoadingView:(NSString *)strTitle
{
    HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:HUD];
    //HUD.delegate = self;
    //HUD.labelText = [strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    HUD.detailsLabelText=[strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    [HUD show:YES];
}

-(void) hideHUDLoadingView
{
    [HUD removeFromSuperview];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end

//
//  FollowersView.m
//  TwitterBoard
//
//  Created by GLB-254 on 5/8/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "FollowedByViewControllerTboard.h"
#import "FHSTwitterEngine.h"
#import "SingletonTboard.h"
#import "TableCustomCell.h"
#import "TwitterHelperClass.h"
#import "UIImageView+WebCache.h"
@interface FollowedByViewControllerTboard ()

@end

@implementation FollowedByViewControllerTboard
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
     [self getFollowerslist];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeBackGroundColor:) name:@"ChangeBackground" object:nil];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(getFollowerslist) name:@"ReloadTimeLine" object:nil];
    currentSelection=-1;
    followerScreenName=[[NSMutableArray alloc]init];
    followersIdList=[[NSMutableArray alloc]init];
    [self checkUserFriend];
    [self tableToShowFollowUser];
   
}
-(void)getFollowerslist
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
   id returned=[[FHSTwitterEngine sharedEngine]listFollowersForUser:[SingletonTboard sharedSingleton].currectUserTwitterId isID:YES withCursor:@"-1"];
    NSLog(@"Followers List %@",returned);
            
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
                    [followerScreenName addObject:[dict objectForKey:@"screen_name"]];
                    [followersIdList addObject:[dict objectForKey:@"id_str"]];
                    [tempArray addObject:tempDict];
                }
                NSLog(@"Whole data %@",tempArray);
                self.alldata=[NSArray arrayWithArray:tempArray];
                NSLog(@"Whole data %@ %lu",self.alldata,(unsigned long)[self.alldata count]);
            
        }
                dispatch_sync(dispatch_get_main_queue(), ^{
            @autoreleasepool
            {
                [self.showFollowing reloadData];
                [self hideHUDLoadingView];
            }
        });

        }});
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
    cell.contentView.layer.borderWidth=1;
    cell.contentView.layer.borderColor=[UIColor grayColor].CGColor;
    cell.contentView.layer.shadowOffset = CGSizeMake(0,0);
    cell.contentView.layer.shadowRadius = 0;
    cell.contentView.layer.shadowOpacity = 0.5;

    NSDictionary * dataDict=[self.alldata objectAtIndex:indexPath.section];
    cell.userNameDesc.text=[dataDict objectForKey:@"FollowerName"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"ImageUrl"]] placeholderImage:[UIImage imageNamed:@"place_holder.png"]];
    cell.userImage.tag=indexPath.section;
    cell.userImage.userInteractionEnabled=YES;
   // UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapOnImage:)];
  //  [cell.userImage addGestureRecognizer:tapGesture];
   // cell.myView.text=[dataDict objectForKey:@"dataDesc"];
    cell.followerCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FollowersCount"] intValue]];
    cell.followingCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FriendCount"] intValue]];
    cell.tweetCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"TweetCount"] intValue]];
    //---------
    cell.add_minusButton.tag=indexPath.section;
    [cell.add_minusButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    cell.add_minusButton.titleLabel.font=[UIFont boldSystemFontOfSize:8];
     [cell.add_minusButton setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
    if([friendsId containsObject:[followersIdList objectAtIndex:indexPath.section]])
    {
       
        [cell.add_minusButton setTitle:@"UNFOLLOW" forState:UIControlStateNormal];
        
    }
    else
    {
        [cell.add_minusButton setTitle:@"FOLLOW" forState:UIControlStateNormal];
    }
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
   /* if (currentSelection == indexPath.section)
    {
        currentSelection = -1;
        [tableView reloadData];
        return;
    }
    NSDictionary * dataDict=[self.alldata objectAtIndex:indexPath.section];
//    tweetScreenName=[followerScreenName objectAtIndex:indexPath.section];
    NSLog(@"Follower Id %@",[dataDict objectForKey:@""]);
    NSInteger row = [indexPath section];
    currentSelection = (int)row;
    [self.showFollowing reloadData];*/
    
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
    if(currentSelection==indexPath.section)
    {
        return 160;
        
    }
    else
    {
        return 80;
    }
}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return [[UIView alloc]initWithFrame:CGRectZero];
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if(section==0)
    {
        return 0;
    }
    return 10;
}

#pragma mark Tweet User Timeline-------
-(void)tweetAction:(UIButton *)btn
{
    NSLog(@"button.tag %ld",(long)btn.tag);
    UIAlertView *myAlertView = [[UIAlertView alloc] initWithTitle:@"Tweet"
                                                          message:@"Enter the text up to 140 characters only" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"OK", nil];
    myAlertView.alertViewStyle = UIAlertViewStylePlainTextInput;
    [myAlertView show];
    
    
}
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSLog(@"Tweet Text%@", [alertView textFieldAtIndex:0].text);
    NSData * data=UIImagePNGRepresentation([UIImage imageNamed:@"follow.png"]);
       [[FHSTwitterEngine sharedEngine]postTweet:@"Hello" withImageData:data];
    NSString * strReply=[NSString stringWithFormat:@"@%@ %@",tweetScreenName,[alertView textFieldAtIndex:0].text];
    [[FHSTwitterEngine sharedEngine]postTweet:strReply inReplyTo:nil];
}
-(void)tableToShowFollowUser
{
    self.showFollowing=[[UITableView alloc]initWithFrame:CGRectMake(5, 10,SCREEN_WIDTH-10, SCREEN_HEIGHT) style:UITableViewStylePlain];
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
    UIActivityIndicatorView *activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    activityIndicator.frame=CGRectMake(SCREEN_WIDTH/2-50, SCREEN_HEIGHT/2-50, 50, 50);
    [self.view addSubview:activityIndicator];
    [activityIndicator startAnimating];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            
            
            if ([FHSTwitterEngine sharedEngine].isAuthorized)
            {
                if([friendsId containsObject:[followersIdList objectAtIndex:btn.tag]])
                {
                    id returned=[[FHSTwitterEngine sharedEngine]unfollowUser:[followersIdList  objectAtIndex:btn.tag] isID:YES];
                    if ([returned isKindOfClass:[NSError class]])
                    {
                        
                    }
                    else
                    {
                        NSLog(@"unfollowed");
                        dispatch_async(dispatch_get_main_queue(), ^(void)
                                       {
                                          
                                           [btn setTitle:@"FOLLOW" forState:UIControlStateNormal];
                                           [activityIndicator stopAnimating];
                                       });
                        NSMutableArray * tempArr=[[NSMutableArray alloc]initWithArray:friendsId];
                        [tempArr removeObject:[followersIdList objectAtIndex:btn.tag]];
                        NSLog(@"Friend Id %@",tempArr);
                        friendsId=tempArr;
                        NSLog(@"Friend Id %@",friendsId);
                        
                    }
                    [SingletonTboard updateFollowingArray:[followersIdList  objectAtIndex:btn.tag]];
                    
                }
                else
                {
                    id returned=[[FHSTwitterEngine sharedEngine]followUser:[followersIdList  objectAtIndex:btn.tag] isID:YES];
                    if ([returned isKindOfClass:[NSError class]])
                    {
                    }
                    else
                    {
                        NSLog(@"followed");
                        dispatch_async(dispatch_get_main_queue(), ^(void)
                                       {
                                           [btn setTitle:@"UNFOLLOW" forState:UIControlStateNormal];
                                           NSMutableArray * tempArr=[[NSMutableArray alloc]initWithArray:friendsId];
                                           [tempArr addObject:[followersIdList objectAtIndex:btn.tag]];
                                           friendsId=tempArr;
                                            [activityIndicator stopAnimating];
                                       });
                    [SingletonTboard updateFollowArray:[followersIdList  objectAtIndex:btn.tag]];
                    }
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
//    NSDictionary * dict=[self.alldata objectAtIndex:recognizer.view.tag];
//    [self fetchUser:[followersIdList objectAtIndex:recognizer.view.tag] nameOfUser:[dict objectForKey:@"FollowerName"]];
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
//    FollowingUserViewController * fllObj=[[FollowingUserViewController alloc]init];
//    fllObj.allData=tempArray;
//    fllObj.nameUser=userName;
//    [self.navigationController pushViewController:fllObj animated:YES];
    
}

-(void)checkUserFriend
{
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            
            if([SingletonTboard networkCheck])
            {
                id getFollowerId=[[FHSTwitterEngine sharedEngine]getFriendsIDs];
                NSLog(@"followers id %@",getFollowerId);
                if([getFollowerId isKindOfClass:[NSError class]])
                {
               
                }
                else
                {
                     friendsId=[getFollowerId objectForKey:@"ids"];
                }
            }
            else
            {
                
            }
        }
    });
    
}

#pragma mark -
#pragma mark - Loading View mbprogresshud

-(void) showHUDLoadingView:(NSString *)strTitle
{
    [self performSelectorOnMainThread:@selector(loadOnMainThread:) withObject:nil waitUntilDone:YES];
}
-(void)loadOnMainThread:(NSString *)strTitle
{
    HUD = [[MBProgressHUD alloc] init];
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
-(void)changeBackGroundColor:(NSNotification*)notify
{
    NSString * notifyObj=notify.object;
    if([notifyObj isEqualToString:@"Slide"])
    {
        if(!backView)
        {
            backView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
            backView.backgroundColor=[[UIColor blackColor]colorWithAlphaComponent:.6];
        }
        [self.view addSubview:backView];
        
    }
    else
    {
        [backView removeFromSuperview];
        self.view.backgroundColor=[UIColor clearColor];
        
    }
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

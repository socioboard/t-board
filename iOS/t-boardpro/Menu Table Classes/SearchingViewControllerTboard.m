//
//  SearchingViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/23/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SearchingViewControllerTboard.h"
#import "SingletonTboard.h"
#import "FHSTwitterEngine.h"
#import "TableCustomCell.h"
#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"
@interface SearchingViewControllerTboard ()
{
}
@end

@implementation SearchingViewControllerTboard

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    //[self searchFunctionality];
    refresh=true;
    searchUserIds=[[NSMutableArray alloc]init];
    pageNumber=1;
    [self searchUi];
    
}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeBackGroundColor:) name:@"ChangeBackground" object:nil];
    [self checkUserFriend];
    //    [self fetchTimeline];
    
}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"ChangeBackground" object:nil];
}

-(void)searchUi
{
    /*UITextField * searchUser=[[UITextField alloc]init];
    searchUser.frame=CGRectMake(20,20,SCREEN_WIDTH-40,50);
    searchUser.delegate=self;
    searchUser.layer.borderWidth=1;
    searchUser.layer.shadowOffset = CGSizeMake(0,0);
    searchUser.layer.shadowRadius = 0;
    searchUser.layer.shadowColor = [UIColor blackColor].CGColor;
    searchUser.layer.shadowOpacity =1;
    UIView * bottomLine=[[UIView alloc]initWithFrame:CGRectMake(0, searchUser.frame.size.height-1, searchUser.frame.size.width, 1)];
    bottomLine.backgroundColor=[UIColor blackColor];
    [searchUser addSubview:bottomLine];

    UIView *paddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 5, 20)];
    searchUser.leftView = paddingView;
    searchUser.leftViewMode = UITextFieldViewModeAlways;
    searchUser.placeholder=@" Search";
    [self.view addSubview:searchUser];
    //-----------
    UIButton * searchBtn=[[UIButton alloc]init];
    [searchBtn setTitle:@"Show" forState:UIControlStateNormal];
    searchBtn.frame=CGRectMake(searchUser.frame.size.width-65,8,60, 30);
    searchBtn.layer.cornerRadius=5;
    searchBtn.backgroundColor=ThemeColor;
    [searchBtn addTarget:self action:@selector(searchFunctionality) forControlEvents:UIControlEventTouchUpInside];
    [searchUser addSubview:searchBtn];
    
    toShowSearchUser=[[UITableView alloc]init];
    toShowSearchUser.frame=CGRectMake(0,80,SCREEN_WIDTH,SCREEN_HEIGHT-140);
    toShowSearchUser.delegate=self;
    toShowSearchUser.dataSource=self;
    [self.view addSubview:toShowSearchUser];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0, SCREEN_WIDTH,140);
    toShowSearchUser.tableFooterView=footerView;*/
    toShowSearchUser=[[UITableView alloc]init];
    toShowSearchUser.frame=CGRectMake(0,80,SCREEN_WIDTH,SCREEN_HEIGHT-140);
    toShowSearchUser.delegate=self;
    toShowSearchUser.dataSource=self;
    [self.view addSubview:toShowSearchUser];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0, SCREEN_WIDTH,140);
    toShowSearchUser.tableFooterView=footerView;
    UIView * headerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0,SCREEN_WIDTH, 80)];
    headerView.backgroundColor=[UIColor colorWithRed:(CGFloat)50/255 green:(CGFloat)50/255 blue:(CGFloat)50/255 alpha:(CGFloat)1];
    [self.view addSubview:headerView];
    searchUser=[[UITextField alloc]initWithFrame:CGRectMake(10, 20, SCREEN_WIDTH/2+50, 30)];
    searchUser.delegate=self;
    searchUser.layer.borderColor=[UIColor blackColor].CGColor;
    searchUser.layer.borderWidth=0.5f;
    searchUser.layer.cornerRadius=5;
    searchUser.clipsToBounds=YES;
    searchUser.placeholder=@"Search";
    searchUser.font=[UIFont systemFontOfSize:12];
    searchUser.textColor=[UIColor blackColor];
    searchUser.backgroundColor=[UIColor whiteColor];
    searchUser.textAlignment=NSTextAlignmentCenter;
    [headerView addSubview:searchUser];
    
    UIButton * searchBtn=[UIButton buttonWithType:UIButtonTypeCustom];
    searchBtn.frame=CGRectMake(SCREEN_WIDTH/2+70, 20, (SCREEN_WIDTH-(SCREEN_WIDTH/2+80)), 30);
    [searchBtn setBackgroundImage:[UIImage imageNamed:@"search_btn.png"] forState:UIControlStateNormal];
    [searchBtn addTarget:self action:@selector(searchFunctionality) forControlEvents:UIControlEventTouchUpInside];
    [headerView addSubview:searchBtn];

    
}

-(void)searchFunctionality
{
    [searchUser resignFirstResponder];
    if([searchQuery isEqualToString:@""])
    {
        refresh=true;
        
        return;
    }
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            

    id searchList=[[FHSTwitterEngine sharedEngine] searchUsersWithQuery:searchQuery andCount:20];
    NSLog(@"search Result %@",searchList);
    if ([searchList isKindOfClass:[NSError class]])
    {
        
    }
    else
    {
        [searchUserIds removeAllObjects];
        
        NSMutableArray * arrayLocal=[[NSMutableArray alloc]init];
        NSArray * array=searchList;
        for (int i=0; i<[array count]; i++)
        {
            NSMutableDictionary * mutDict=[[NSMutableDictionary alloc]init];
            NSDictionary * localDict=[array objectAtIndex:i];
            NSLog(@"local dict %@",[localDict objectForKey:@"name"]);
            [mutDict setObject:[localDict objectForKey:@"name"] forKey:@"Name"];
            [mutDict setObject:[localDict objectForKey:@"followers_count"] forKey:@"FollowersCount"];
            [mutDict setObject:[localDict objectForKey:@"friends_count"] forKey:@"FriendCount"];
            [mutDict setObject:[localDict objectForKey:@"statuses_count"] forKey:@"Tweets"];
            [mutDict setObject:[localDict objectForKey:@"description"] forKey:@"Description"];
            [mutDict setObject:[localDict objectForKey:@"profile_image_url"] forKey:@"ProfileImage"];
            [searchUserIds addObject:[localDict objectForKey:@"id_str"]];
            [arrayLocal addObject:mutDict];
        }
        self.alldata=[NSArray arrayWithArray:arrayLocal];
    }
            dispatch_async(dispatch_get_main_queue(), ^(void){
                
                [self hideHUDLoadingView];
                refresh=false;
                [toShowSearchUser reloadData];
            });
  
        }});
}
- (void)scrollViewDidScroll:(UIScrollView *)aScrollView
{
    CGPoint offset = aScrollView.contentOffset;
    CGRect bounds = aScrollView.bounds;
    CGSize size = aScrollView.contentSize;
    UIEdgeInsets inset = aScrollView.contentInset;
    float y = offset.y + bounds.size.height - inset.bottom;
    float h = size.height;
    // NSLog(@"offset: %f", offset.y);
    // NSLog(@"content.height: %f", size.height);
    // NSLog(@"bounds.height: %f", bounds.size.height);
    // NSLog(@"inset.top: %f", inset.top);
    // NSLog(@"inset.bottom: %f", inset.bottom);
    // NSLog(@"pos: %f of %f", y, h);
    
    float reload_distance = 1;
    if(y > h + reload_distance)
    {
        if(!refresh)
        {
            refresh=true;
            if([self.alldata count]<180)
            {
                pageNumber++;
                [self searchFunctionality:pageNumber];
                NSLog(@"load more rows");
            }
        }
    }
}
-(void)searchFunctionality:(int)pageNo
{
    
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
            

   id searchList=[[FHSTwitterEngine sharedEngine] searchUsersWithQueryPage:searchQuery andCount:20 pageNumber:pageNo];
    NSLog(@"search Result %@",searchList);
    if ([searchList isKindOfClass:[NSError class]])
    {
    }
    else
    {
        NSMutableArray * arrayLocal=[[NSMutableArray alloc]init];
        NSArray * array=searchList;
        for (int i=0; i<[array count]; i++)
        {
            NSMutableDictionary * mutDict=[[NSMutableDictionary alloc]init];
            NSDictionary * localDict=[array objectAtIndex:i];
            NSLog(@"local dict %@",[localDict objectForKey:@"name"]);
            [mutDict setObject:[localDict objectForKey:@"name"] forKey:@"Name"];
            [mutDict setObject:[localDict objectForKey:@"followers_count"] forKey:@"FollowersCount"];
            [mutDict setObject:[localDict objectForKey:@"friends_count"] forKey:@"FriendCount"];
            [mutDict setObject:[localDict objectForKey:@"statuses_count"] forKey:@"Tweets"];
            [mutDict setObject:[localDict objectForKey:@"description"] forKey:@"Description"];
            [mutDict setObject:[localDict objectForKey:@"profile_image_url"] forKey:@"ProfileImage"];
            [searchUserIds addObject:[localDict objectForKey:@"id_str"]];
            [arrayLocal addObject:mutDict];
        }
        self.alldata=[self.alldata arrayByAddingObjectsFromArray:arrayLocal];
        NSLog(@"sel.allData Count %lu",(unsigned long)self.alldata.count);
    }
            dispatch_async(dispatch_get_main_queue(), ^(void){
                
                [self hideHUDLoadingView];
                refresh=false;
                [toShowSearchUser reloadData];
            });

           }});
}
#pragma mark Text Field Delegates
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    return YES;
}
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    
}
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    
    searchQuery=textField.text;
    [textField resignFirstResponder];
    
    return YES;
}
- (BOOL)textFieldShouldEndEditing:(UITextField *)textField
{
    return YES;
}
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    return YES;
}
-(void)textFieldDidEndEditing:(UITextField *)textField
{
    searchQuery=textField.text;
 
}
#pragma mark Table View Delegates

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
   
    
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
    return 80;
}
#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"All Follower";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    NSDictionary * dataDict=[self.alldata objectAtIndex:indexPath.section];
    cell.userNameDesc.text=[dataDict objectForKey:@"Name"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"ProfileImage"]] placeholderImage:[UIImage imageNamed:@"place_holder.png"]];
    //cell.myView.text=[dataDict objectForKey:@"Description"];
    cell.followerCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FollowersCount"] intValue]];
    cell.followingCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FriendCount"] intValue]];
    cell.tweetCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"Tweets"] intValue]];
    //---------
    cell.add_minusButton.tag=indexPath.section;
    [cell.add_minusButton setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
    if([self.friendsId containsObject:[searchUserIds objectAtIndex:indexPath.section]])
    {
        [cell.add_minusButton setTitle:@"UNFOLLOW" forState:UIControlStateNormal];
        cell.add_minusButton.titleLabel.font=[UIFont boldSystemFontOfSize:8];
 
    }
    else
    {
        [cell.add_minusButton setTitle:@"FOLLOW" forState:UIControlStateNormal];
        cell.add_minusButton.titleLabel.font=[UIFont boldSystemFontOfSize:8];

    }
    [cell.add_minusButton addTarget:self action:@selector(follow_unfollowButton:) forControlEvents:UIControlEventTouchUpInside];
    return cell;
}

#pragma mark----
-(void)follow_unfollowButton:(UIButton *)btn
{
    NSLog(@"Follwer list id %@",[searchUserIds objectAtIndex:btn.tag]);
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            
            
            if ([FHSTwitterEngine sharedEngine].isAuthorized)
            {
                if([self.friendsId containsObject:[searchUserIds objectAtIndex:btn.tag]])
                {
                    id returned=[[FHSTwitterEngine sharedEngine]unfollowUser:[searchUserIds  objectAtIndex:btn.tag] isID:YES];
                    if ([returned isKindOfClass:[NSError class]])
                    {
                        
                    }
                    else
                    {
                        NSLog(@"unfollowed");
                        dispatch_async(dispatch_get_main_queue(), ^(void)
                                       {
                        [btn setTitle:@"UNFOLLOW" forState:UIControlStateNormal];
                         btn.titleLabel.font=[UIFont boldSystemFontOfSize:8];
                        });
                        NSMutableArray * tempArr=[[NSMutableArray alloc]initWithArray:self.friendsId];
                        [tempArr removeObject:[searchUserIds objectAtIndex:btn.tag]];
                        NSLog(@"Friend Id %@",tempArr);
                        self.friendsId=tempArr;
                        NSLog(@"Friend Id %@",self.friendsId);

                    }

                    
                }
                else
                {
                    id returned=[[FHSTwitterEngine sharedEngine]followUser:[searchUserIds  objectAtIndex:btn.tag] isID:YES];
                    if ([returned isKindOfClass:[NSError class]])
                    {
                        
                    }
                    else
                    {
                        NSLog(@"followed");
                        dispatch_async(dispatch_get_main_queue(), ^(void)
                        {
                            [btn setTitle:@"UNFOLLOW" forState:UIControlStateNormal];
                            btn.titleLabel.font=[UIFont boldSystemFontOfSize:8];

                            NSMutableArray * tempArr=[[NSMutableArray alloc]initWithArray:self.friendsId];
                            [tempArr addObject:[searchUserIds objectAtIndex:btn.tag]];
                            self.friendsId=tempArr;
                        });
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
-(void)checkUserFriend
{
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {

            if([SingletonTboard networkCheck])
            {
                id getFollowerId=[[FHSTwitterEngine sharedEngine]getFriendsIDs];
                if ([getFollowerId isKindOfClass:[NSError class]])
                {
                    
                }
                else
                {
                NSLog(@"followers id %@",getFollowerId);
                self.friendsId=[getFollowerId objectForKey:@"ids"];
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
     [self performSelectorOnMainThread:@selector(addOnMainThread) withObject:nil waitUntilDone:YES];
}
-(void)addOnMainThread
{
    HUD = [[MBProgressHUD alloc] init];
    [self.view addSubview:HUD];
    //HUD.delegate = self;
    HUD.labelText = @"Loading..";
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

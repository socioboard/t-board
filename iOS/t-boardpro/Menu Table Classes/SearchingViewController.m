//
//  SearchingViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/23/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SearchingViewController.h"
#import "Singleton.h"
#import "FHSTwitterEngine.h"
#import "TableCustomCell.h"
#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"
@interface SearchingViewController ()

@end

@implementation SearchingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    //[self searchFunctionality];
    searchUserIds=[[NSMutableArray alloc]init];
    //Method for Ui of View

    [self searchUi];
    //Check already following user or not
    [self checkUserFriend];
}

-(void)searchUi
{
    UITextField * searchUser=[[UITextField alloc]init];
    searchUser.frame=CGRectMake(20,20,SCREEN_WIDTH-40,50);
    searchUser.delegate=self;
    searchUser.placeholder=@"Text to type";
    searchUser.layer.borderColor=[UIColor grayColor].CGColor;
    searchUser.layer.borderWidth=1;
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
    toShowSearchUser.tableFooterView=footerView;
    
}

-(void)searchFunctionality
{
    
 id searchList=[[FHSTwitterEngine sharedEngine] searchUsersWithQuery:searchQuery andCount:20];
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
        self.alldata=[NSArray arrayWithArray:arrayLocal];
    }
    [toShowSearchUser reloadData];
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
    NSLog(@"index path section %ld",(long)indexPath.section);
    return 160;
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
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"ProfileImage"]] placeholderImage:[UIImage imageNamed:@""]];
    cell.myView.text=[dataDict objectForKey:@"Description"];
    cell.followerCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FollowersCount"] intValue]];
    cell.followingCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FriendCount"] intValue]];
    cell.tweetCount.text=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"Tweets"] intValue]];
    //---------
    cell.add_minusButton.tag=indexPath.section;
    if([self.friendsId containsObject:[searchUserIds objectAtIndex:indexPath.section]])
    {
    [cell.add_minusButton setBackgroundImage:[UIImage imageNamed:@"unfollow.png"] forState:UIControlStateNormal];
 
    }
    else
    {
        [cell.add_minusButton setBackgroundImage:[UIImage imageNamed:@"follow.png"] forState:UIControlStateNormal];

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
                        dispatch_async(dispatch_get_main_queue(), ^(void){
                            [btn setBackgroundImage:[UIImage imageNamed:@"follow.png"] forState:UIControlStateNormal];
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
                        dispatch_async(dispatch_get_main_queue(), ^(void){
                            [btn setBackgroundImage:[UIImage imageNamed:@"unfollow.png"] forState:UIControlStateNormal];
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

            if([Singleton networkCheck])
            {
                id getFollowerId=[[FHSTwitterEngine sharedEngine]getFriendsIDs];
                NSLog(@"followers id %@",getFollowerId);
                self.friendsId=[getFollowerId objectForKey:@"ids"];
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

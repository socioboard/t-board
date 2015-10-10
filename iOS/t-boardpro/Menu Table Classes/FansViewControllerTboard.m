//
//  FansView.m
//  TwitterBoard
//
//  Created by GLB-254 on 7/22/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "FansViewControllerTboard.h"
#import "SingletonTboard.h"
#import "TableCustomCell.h"
#import "UIImageView+WebCache.h"
@interface FansViewControllerTboard ()<UITableViewDelegate,UITableViewDataSource>

@end

@implementation FansViewControllerTboard
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    offSet=20;
    currentSelection=-1;
    if(!firstRun)
    {
    [self getUserFans];
    firstRun=YES;
    }
    [self fetchDataOfFans:fansFollowerID];

    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeBackGroundColor:) name:@"ChangeBackground" object:nil];

}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"ChangeBackground" object:nil];
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    fansFollowerID=[[NSMutableArray alloc]init];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(getUserFans) name:@"ReloadTimeLine" object:nil];

    [self createTable];
}
-(void)getUserFans
{
    //Fans means you are not following others are following
    NSArray * followingIdArr=[SingletonTboard sharedSingleton].followingData;
    NSArray * followersIdArr=[SingletonTboard sharedSingleton].followerData;
    for(NSString * idFollowing in followersIdArr)
    {
        //Fans means you are not following others are following
        if(![followingIdArr containsObject:idFollowing])
        {
            [fansFollowerID addObject:idFollowing];
        }
    }
}
-(void)createTable
{
    self.fanTable=[[UITableView alloc]initWithFrame:CGRectMake(5,10,SCREEN_WIDTH-10, SCREEN_HEIGHT) style:UITableViewStylePlain];
    self.fanTable.delegate=self;
    self.fanTable.dataSource=self;
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,SCREEN_WIDTH, 80);
    self.fanTable.tableFooterView=footerView;
    [self.view addSubview:self.fanTable];
}
-(void)fetchDataOfFans:(NSArray*)nonFollowerArr
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    
    fansCountArr=[NSArray arrayWithArray:nonFollowerArr];
    NSArray *smallArray;
    if(nonFollowerArr.count>20)
    {
        smallArray = [nonFollowerArr subarrayWithRange:NSMakeRange(0, offSet)];
    }
    else
    {
        smallArray=nonFollowerArr;
    }
    id nonFollowerData=[[FHSTwitterEngine sharedEngine] lookupUsers:smallArray areIDs:YES];
    
    NSLog(@"non Follower Data %@",nonFollowerData);
    if ([nonFollowerData isKindOfClass:[NSError class]])
    {
        
    }
    else
    {
        NSMutableArray * tempArray=[[NSMutableArray alloc]init];
        for (int i=0;i<[nonFollowerData count];i++)
        {
            NSMutableDictionary * tempDict=[[NSMutableDictionary alloc]init];
            //--------------------
            NSDictionary * dict=[nonFollowerData objectAtIndex:i];
            NSLog(@"friends list  %@",[dict objectForKey:@"profile_image_url_https"]);
            [tempDict setObject:[dict objectForKey:@"profile_image_url_https"] forKey:@"ImageUrl"];
            [tempDict setObject:[dict objectForKey:@"name"] forKey:@"FollowerName"];
            [tempDict setObject:[dict objectForKey:@"description"] forKey:@"dataDesc"];
            [tempDict setObject:[dict objectForKey:@"followers_count"] forKey:@"FollowersCount"];
            [tempDict setObject:[dict objectForKey:@"friends_count"] forKey:@"FriendCount"];
            [tempDict setObject:[dict objectForKey:@"statuses_count"] forKey:@"TweetCount"];
            [fansScreenName addObject:[dict objectForKey:@"screen_name"]];
            [fansFollowerID addObject:[dict objectForKey:@"id_str"]];
            [tempArray addObject:tempDict];
            
        }
        refresh=false;
        
        NSLog(@"Whole data %@",tempArray);
        self.allData=[NSArray arrayWithArray:tempArray];
        NSLog(@"Whole data %@ %lu",self.allData,(unsigned long)[self.allData count]);
        //        dispatch_sync(dispatch_get_main_queue(), ^{
        //            @autoreleasepool {
        [self.fanTable reloadData];
        //  [self.showFollowing reloadData];
        //[self hideHUDLoadingView];
        // [av show];
        //            }
        //        });
        [self hideHUDLoadingView];
    }
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
    
    
    UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapOnImage:)];
    //[cell.userImage addGestureRecognizer:tapGesture];
    cell.userImage.tag=indexPath.section;
    cell.userImage.userInteractionEnabled=YES;
    
    NSDictionary * dataDict=[self.allData objectAtIndex:indexPath.section];
    
    cell.userNameDesc.text=[dataDict objectForKey:@"FollowerName"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"ImageUrl"]] placeholderImage:[UIImage imageNamed:@"place_holder.png"]];
    
    
    //---
    NSString * followercount=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FollowersCount"] intValue]];
    NSString * followingCount=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"FriendCount"] intValue]];
    
    NSString * tweetCount=[NSString stringWithFormat:@"%d",[[dataDict objectForKey:@"TweetCount"] intValue]];
    
    //---
    cell.followerCount.text=[self adjustText:followercount];
    cell.followingCount.text=[self adjustText:followingCount];
    cell.tweetCount.text=[self adjustText:tweetCount];
    //---------
    cell.add_minusButton.tag=indexPath.section;
    [cell.add_minusButton setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
    [cell.add_minusButton addTarget:self action:@selector(follow_unfollowButton:) forControlEvents:UIControlEventTouchUpInside];
    [cell.add_minusButton setTitle:@"FOLLOW" forState:UIControlStateNormal];
    cell.add_minusButton.titleLabel.font=[UIFont boldSystemFontOfSize:8];
    //[cell.tweetButton addTarget:self action:@selector(tweetAction:) forControlEvents:UIControlEventTouchUpInside];
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
-(void)tapOnImage:(UIGestureRecognizer*)recognizer
{
    
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //    if (currentSelection == indexPath.section)
    //    {
    //        currentSelection = -1;
    //        [tableView reloadData];
    //        return;
    //    }
//    NSDictionary * dataDict=[self.allData objectAtIndex:indexPath.section];
//    tweetScreenName=[fansScreenName objectAtIndex:indexPath.section];
    //    NSLog(@"Follower Id %@",[dataDict objectForKey:@""]);
    //    NSInteger row = [indexPath section];
    //    currentSelection = (int)row;
    //    [self.showFollowing reloadData];
    
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.allData count];
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
- (void)scrollViewDidScroll:(UIScrollView *)aScrollView
{
    CGPoint offset = aScrollView.contentOffset;
    CGRect bounds = aScrollView.bounds;
    CGSize size = aScrollView.contentSize;
    UIEdgeInsets inset = aScrollView.contentInset;
    float y = offset.y + bounds.size.height - inset.bottom;
    float h = size.height;
    float reload_distance = 1;
    if(y > h + reload_distance)
    {
        if(!refresh)
        {
            refresh=true;
            if(fansCountArr.count>offSet)
            {
                [self pagingCallNextArray];
                /*if(nextCursor!=0)
                 {
                 
                 }*/
            }
        }
    }
}
-(void)pagingCallNextArray
{
    NSMutableArray *smallArray=[[NSMutableArray alloc]init];
    if(fansCountArr.count>offSet+20)
    {
        for (int i=offSet+1; i<=offSet+20; i++)
        {
            [smallArray addObject:[fansCountArr objectAtIndex:i]];
            
        }
        offSet=offSet+20;
    }
    else
    {
        for (int i=offSet+1; i<fansCountArr.count; i++)
        {
            [smallArray addObject:[fansCountArr objectAtIndex:i]];
            
        }
        offSet=offSet+20;
    }
    id fansData=[[FHSTwitterEngine sharedEngine] lookupUsers:smallArray areIDs:YES];
    
    NSLog(@"non Follower Data %@",fansData);
    if ([fansData isKindOfClass:[NSError class]])
    {
        
    }
    else
    {
        NSMutableArray * tempArray=[[NSMutableArray alloc]init];
        for (int i=0;i<[fansData count];i++)
        {
            NSMutableDictionary * tempDict=[[NSMutableDictionary alloc]init];
            //--------------------
            NSDictionary * dict=[fansData objectAtIndex:i];
            NSLog(@"friends list  %@",[dict objectForKey:@"profile_image_url_https"]);
            [tempDict setObject:[dict objectForKey:@"profile_image_url_https"] forKey:@"ImageUrl"];
            [tempDict setObject:[dict objectForKey:@"name"] forKey:@"FollowerName"];
            [tempDict setObject:[dict objectForKey:@"description"] forKey:@"dataDesc"];
            [tempDict setObject:[dict objectForKey:@"followers_count"] forKey:@"FollowersCount"];
            [tempDict setObject:[dict objectForKey:@"friends_count"] forKey:@"FriendCount"];
            [tempDict setObject:[dict objectForKey:@"statuses_count"] forKey:@"TweetCount"];
            [fansScreenName addObject:[dict objectForKey:@"screen_name"]];
            [fansIdList addObject:[dict objectForKey:@"id_str"]];
            [tempArray addObject:tempDict];
            
        }
        NSMutableArray * initialData=[[NSMutableArray alloc]initWithArray:self.allData];
        NSLog(@"Whole data %@",tempArray);
        [initialData addObjectsFromArray:tempArray];
        self.allData=[NSArray arrayWithArray:initialData];
        NSLog(@"Whole data %@ %lu",self.allData,(unsigned long)[self.allData count]);
        //        dispatch_sync(dispatch_get_main_queue(), ^{
        //            @autoreleasepool {
        refresh=false;
        [self.fanTable reloadData];
        //  [self.showFollowing reloadData];
        //[self hideHUDLoadingView];
        // [av show];
        //            }
        //        });
        
    }
    
}
-(NSString *)adjustText:(NSString *)dataToCheck
{
    
    float  i=[dataToCheck floatValue];
    if (i>=10000000)
    {
        i=i/10000000;
        return [NSString stringWithFormat:@"%.2fCr",i];
    }
    else if (i>=100000)
    {
        i=i/100000;
        return [NSString stringWithFormat:@"%.2fM",i];
    }
    else if(i>=10000)
    {
        i=i/10000;
        return [NSString stringWithFormat:@"%.2fK",i];
    }
    
    
    return dataToCheck;
}
#pragma mark -
#pragma mark - Loading View mbprogresshud
-(void)follow_unfollowButton:(UIButton *)btn
{
    NSLog(@"Follwer list id %@",[fansFollowerID objectAtIndex:btn.tag]);
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool
        {
        if ([FHSTwitterEngine sharedEngine].isAuthorized)
            {
                id returned=[[FHSTwitterEngine sharedEngine]followUser:[fansFollowerID  objectAtIndex:btn.tag] isID:YES];
                if ([returned isKindOfClass:[NSError class]])
                {
                }
                else
                {
                    dispatch_sync(dispatch_get_main_queue(), ^{
                        @autoreleasepool {
                            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:self.allData];
                            [tempArray removeObjectAtIndex:btn.tag];
                            self.allData=tempArray;
                            [self.fanTable reloadData];                        }
                    });
                    
                    
                    NSLog(@"followed");
                }
                [SingletonTboard updateFollowArray:[fansFollowerID  objectAtIndex:btn.tag]];
                
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

-(void) showHUDLoadingView:(NSString *)strTitle
{
    if(!HUD)
    {
        HUD = [[MBProgressHUD alloc] init];
        [self.view addSubview:HUD];
    }
    //HUD.delegate = self;
    //HUD.labelText = [strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    HUD.detailsLabelText=[strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    [HUD show:YES];
}
-(void) hideHUDLoadingView
{
    [HUD removeFromSuperview];
    HUD=nil;
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

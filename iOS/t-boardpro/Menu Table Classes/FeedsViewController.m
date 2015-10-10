//
//  FollwViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "FeedsViewController.h"
#import "TableCustomCell.h"
#import <Social/Social.h>
#import "FHSTwitterEngine.h"
#import "ProfileViewTboard.h"
#import "SingletonTboard.h"
#import "SDWebImage/UIImageView+WebCache.h"
#import "TwitterHelperClass.h"
#import "MBProgressHUD.h"
@interface FeedsViewController ()

@end

@implementation FeedsViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    countEndOfLine=0;
    refreshTweet=false;
    self.view.backgroundColor=[UIColor colorWithRed:(CGFloat)220/255 green:(CGFloat)220/255 blue:(CGFloat)220/255 alpha:1];
    //----------Initialization-------------
    imagePicker=[[UIImagePickerController alloc]init];
    setFavoriteBool=[[NSMutableArray alloc]init];
    setReTweetBool=[[NSMutableArray alloc]init];
    setMentionFavoriteBool =[[NSMutableArray alloc]init];
    setMentionReTweetBool=[[NSMutableArray alloc]init];
    setTweetsFavoriteBool=[[NSMutableArray alloc]init];
    setTweetsReTweetBool=[[NSMutableArray alloc]init];
    selectedData=[[NSMutableArray alloc]init];
    //-----------
    //UI
    [self createHeader];
    [self createFollowTable];
    //Added ObserVer
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(reloadFeedView) name:@"ReloadTimeLine" object:nil];
    //Fetch Data
    [NSThread detachNewThreadSelector:@selector(favouriteCountList) toTarget:self withObject:nil];
     [NSThread detachNewThreadSelector:@selector(loadDataFromTwitter) toTarget:self withObject:nil];
}
-(void)loadDataFromTwitter
{
    //------------
    twittHelperObj=[[TwitterHelperClass alloc]init];
    wholeDataTweet=[[twittHelperObj fetchOwnTweet:[SingletonTboard sharedSingleton].currectUserTwitterId] copy];
    [self setFavouriteBoolOfTweet:wholeDataTweet];
    [self performSelectorOnMainThread:@selector(createTweetTable) withObject:nil waitUntilDone:YES];
    //-------------
    wholeDataMention=[twittHelperObj fetchOwnMention:20];
    [self setFavouriteBoolOfMention:wholeDataMention];
    [self performSelectorOnMainThread:@selector(createMentionTable) withObject:nil waitUntilDone:YES];
}
-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeBackGroundColor:) name:@"ChangeBackground" object:nil];
    if([SingletonTboard networkCheck])
    {
      
        [self fetchTimeline];
        
    }
    else
    {
        UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [noInternet show];
    }

//    [self fetchTimeline];
   
}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"ChangeBackground" object:nil];
}
#pragma mark---
-(void)fetchTimeline
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            
            wholeData=[twittHelperObj fetchTimeline:nil];
            [setFavoriteBool removeAllObjects];
            [setReTweetBool removeAllObjects];
            for(int i=0;i<[wholeData count];i++)
            {
               NSDictionary * dataDict=[wholeData objectAtIndex:i];
              [setFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
              [setReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
            }
            
        dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
            {
                    [followTableView reloadData];
                    [self hideHUDLoadingView];
                   // [av show];
            }
            });
        }
    });

}
-(void)reloadFeedView
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            //Feeds
            wholeData=[twittHelperObj fetchTimeline:nil];
            [setFavoriteBool removeAllObjects];
            [setReTweetBool removeAllObjects];
            for(int i=0;i<[wholeData count];i++)
            {
                NSDictionary * dataDict=[wholeData objectAtIndex:i];
                [setFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
                [setReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
            }
            //---Tweet--
            twittHelperObj=[[TwitterHelperClass alloc]init];
            wholeDataTweet=[[twittHelperObj fetchOwnTweet:[SingletonTboard sharedSingleton].currectUserTwitterId] copy];
            [self setFavouriteBoolOfTweet:wholeDataTweet];
            //-------------
            //---Mention---
            wholeDataMention=[twittHelperObj fetchOwnMention:20];
            [self setFavouriteBoolOfMention:wholeDataMention];
            //------------
          
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                    if(wholeData.count>0)
                    {
                        refresh=false;

                    }
                    else
                    {
                        refresh=true;
                    }
                    //--
                    if(wholeDataTweet.count>0)
                    {
                        refreshTweet=false;
                    }
                    else
                    {
                        refreshTweet=true;
                    }
                    //----
                    if(wholeDataMention.count>0)
                    {
                        refreshMention=false;
                    }
                    else
                    {
                        refreshMention=true;
                    }
                    [followTableView reloadData];
                    [mentionTableView reloadData];
                    [tweetTableView reloadData];

                    [self hideHUDLoadingView];
                    // [av show];
                }
            });
        }
    });
    
}

#pragma mark new ui
-(void)createHeader
{
    UIView * headerOfView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH,40)];
    [self.view addSubview:headerOfView];
    headerOfView.layer.shadowColor = [UIColor purpleColor].CGColor;
    headerOfView.layer.shadowOffset = CGSizeMake(5, 5);
    headerOfView.layer.shadowOpacity = 1;
    headerOfView.layer.shadowRadius = 1.0;
    NSArray * itemArray=[NSArray arrayWithObjects:@"HOME",@"ME",@"MENTIONS",nil];
    
    UISegmentedControl *segmentedControl = [[UISegmentedControl alloc] initWithItems:itemArray];
    segmentedControl.frame = CGRectMake(10,10,SCREEN_WIDTH-20,30);
    segmentedControl.tintColor=ThemeColor;
    [segmentedControl addTarget:self action:@selector(segmentControlAction:) forControlEvents: UIControlEventValueChanged];
    segmentedControl.selectedSegmentIndex = 0;
    [self.view addSubview:segmentedControl];
    UIView * lineView=[[UIView alloc]initWithFrame:CGRectMake(0,45, SCREEN_WIDTH,.6)];
    lineView.backgroundColor=[UIColor blackColor];
    [self.view addSubview:lineView];
}
-(void)segmentControlAction:(UISegmentedControl*)segmentControl
{
    if(segmentControl.selectedSegmentIndex==0)
    {
        followTableView.hidden=NO;
        tweetTableView.hidden=YES;
        mentionTableView.hidden=YES;
    }
    else if (segmentControl.selectedSegmentIndex==1)
    {
        followTableView.hidden=YES;
        tweetTableView.hidden=NO;
        mentionTableView.hidden=YES;
    }
    else if (segmentControl.selectedSegmentIndex==2)
    {
        followTableView.hidden=YES;
        tweetTableView.hidden=YES;
        mentionTableView.hidden=NO;
    }
    
}
-(void)createFollowTable
{
    followTableView=[[UITableView alloc]initWithFrame:CGRectMake(5,50,SCREEN_WIDTH-10,self.view.frame.size.height) style:UITableViewStylePlain];
       followTableView.dataSource=self;
    followTableView.bounces=YES;
    followTableView.delegate=self;
   followTableView.scrollIndicatorInsets=UIEdgeInsetsMake(0, 0, 0,-20);
    followTableView.separatorStyle=UITableViewCellSeparatorStyleNone;
    followTableView.backgroundColor=[UIColor clearColor];
    [self.view insertSubview:followTableView belowSubview:self.view];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,self.view.frame.size.width,70);
    followTableView.tableFooterView=footerView;
}
-(void)createTweetTable
{
    tweetTableView=[[UITableView alloc]initWithFrame:CGRectMake(5,50,SCREEN_WIDTH-10,self.view.frame.size.height) style:UITableViewStylePlain];
    tweetTableView.hidden=YES;
    tweetTableView.dataSource=self;
    tweetTableView.bounces=YES;
    tweetTableView.delegate=self;
    tweetTableView.scrollIndicatorInsets=UIEdgeInsetsMake(0, 0, 0,-20);
    tweetTableView.separatorStyle=UITableViewCellSeparatorStyleNone;
    tweetTableView.backgroundColor=[UIColor clearColor];
    [self.view insertSubview:tweetTableView belowSubview:self.view];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,self.view.frame.size.width,100);
    tweetTableView.tableFooterView=footerView;
  
}
-(void)createMentionTable
{
    mentionTableView=[[UITableView alloc]initWithFrame:CGRectMake(5,50,SCREEN_WIDTH-10,self.view.frame.size.height) style:UITableViewStylePlain];
    mentionTableView.hidden=YES;
    mentionTableView.dataSource=self;
    mentionTableView.bounces=YES;
    mentionTableView.delegate=self;
    mentionTableView.scrollIndicatorInsets=UIEdgeInsetsMake(0, 0, 0,-20);
    mentionTableView.separatorStyle=UITableViewCellSeparatorStyleNone;
    mentionTableView.backgroundColor=[UIColor clearColor];
    [self.view insertSubview:mentionTableView belowSubview:self.view];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,self.view.frame.size.width,100);
    mentionTableView.tableFooterView=footerView;
    
}



#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==followTableView)
    {
    static NSString *CellIdentifier = @"Follow";
    
    cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }

    cell.favouriteButton.tag=indexPath.section;
    cell.reTweetButton.tag=indexPath.section;
    cell.tweetButton.tag=indexPath.section;
    cell.cellFooterView.hidden=false;
    NSDictionary * dataDict=[wholeData objectAtIndex:indexPath.section];

   dynamicHeightOfRow=[self calculateHeight:cell.userNameDesc stringData:[dataDict objectForKey:FeedDescription]];
    //Setting values
    
    cell.userNameDesc.frame=CGRectMake(80, 35,220,dynamicHeightOfRow);
    if(IS_IPHONE_4_OR_LESS||IS_IPHONE_5)
    {
        cell.userNameDesc.frame=CGRectMake(70, 35,220,dynamicHeightOfRow);
   
    }
    cell.userNameDesc.text=[dataDict objectForKey:FeedDescription];
    //---
    CGFloat hhDesc=dynamicHeightOfRow+47;
    cell.endingLine.frame=CGRectMake(75, hhDesc+10, SCREEN_WIDTH-75, 1);
    cell.endingLine.backgroundColor=[UIColor grayColor];
    //---

    cell.backGroundImageViewOfCell.frame=CGRectMake(5, 0, SCREEN_WIDTH-20,dynamicHeightOfRow+90);
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:FeedImageUser]] placeholderImage:[UIImage imageNamed:@"place_holder.png"]];
    cell.nameLblFeed.text=[NSString stringWithFormat:@"@%@",[dataDict objectForKey:FeedUserScreenName]];
    cell.nameLblFeed.userInteractionEnabled=YES;
    cell.nameLblFeed.tag=indexPath.section;
    UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tappedOnName:)];
    [cell.nameLblFeed addGestureRecognizer:tapGesture];
    cell.favouriteLbl.text=[NSString stringWithFormat:@"%@",[dataDict objectForKey:FeedFavouriteCount]];
    //favoriteCount
   
    cell.reTweetCount.text=[NSString stringWithFormat:@"%@",[dataDict objectForKey:FeedRetweetCount]];
    BOOL favorite=[[setFavoriteBool objectAtIndex:indexPath.section] boolValue];
    if(favorite)
    {
        [cell.favouriteButton setBackgroundImage:[UIImage imageNamed:@"favorites.png"] forState:UIControlStateNormal];
    }
    else
    {
        [cell.favouriteButton setBackgroundImage:[UIImage imageNamed:@"ic_action_fave_off_focused.png"] forState:UIControlStateNormal];
    }
    BOOL retweet=[[setReTweetBool objectAtIndex:indexPath.section] boolValue];

    //---
   if(retweet)
   {
       [cell.reTweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_rt_on_focused.png"] forState:UIControlStateNormal];

   }
    else
    {
        [cell.reTweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_rt_off_focused.png"] forState:UIControlStateNormal];
  
    }
    //action of methods
         [cell.favouriteButton addTarget:self action:@selector(setFavourite:) forControlEvents:UIControlEventTouchUpInside];
    [cell.reTweetButton addTarget:self action:@selector(reTweetAction:) forControlEvents:UIControlEventTouchUpInside];
    [cell.tweetButton addTarget:self action:@selector(tweetView:) forControlEvents:UIControlEventTouchUpInside];
    //-----------Frames of all button
    cell.reTweetButton.frame=CGRectMake(SCREEN_WIDTH/2-15,dynamicHeightOfRow+55, 30,25);
    cell.reTweetCount.frame=CGRectMake(SCREEN_WIDTH/2+20,dynamicHeightOfRow+55,40,25);
    cell.tweetButton.frame=CGRectMake(80,dynamicHeightOfRow+55,30,25);
    cell.favouriteLbl.frame=CGRectMake(SCREEN_WIDTH-55,dynamicHeightOfRow+55,40,25);
    cell.favouriteButton.frame=CGRectMake(SCREEN_WIDTH-85,dynamicHeightOfRow+55,30,30);
    //------------
    }
    else if (tableView==userListTable)
    {
        static NSString *CellIdentifier = @"ShowUserList";
        
        cell = [tableView dequeueReusableCellWithIdentifier:nil];
        
        if (cell == nil)
        {
            cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:indexPath.section];
        [cell.userImage sd_setImageWithURL:[dict objectForKey:@"UserImageUrl"] placeholderImage:[UIImage imageNamed:@""]];
        cell.nameLblFeed.text=[dict objectForKey:@"TwitterUserName"];
        UIButton * checkBtn=[[UIButton alloc]init];
        checkBtn.frame=CGRectMake(cell.contentView.frame.size.width-60,5, 30, 30);
        if([selectedData containsObject:[dict objectForKey:@"TwitterUserName"]])
        {
            [checkBtn setBackgroundImage:[UIImage imageNamed:@"check_mark.png"] forState:UIControlStateNormal];
        }
        else
        {
            [checkBtn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
        }

        checkBtn.tag=indexPath.section;
        [checkBtn addTarget:self action:@selector(checkBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:checkBtn];
        return cell;

    }
    else if (tableView==tweetTableView||tableView==mentionTableView)
    {
        static NSString *CellIdentifier = @"Follow";
        
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
        
        if (cell == nil)
        {
            cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        NSDictionary * dataDict;
        if(tableView==tweetTableView)
        {
             dataDict=[wholeDataTweet objectAtIndex:indexPath.section];
        }
        else if (tableView==mentionTableView)
        {
           dataDict=[wholeDataMention objectAtIndex:indexPath.section];
        }
        
        cell.favouriteButton.tag=indexPath.section;
        cell.reTweetButton.tag=indexPath.section;
        cell.tweetButton.tag=indexPath.section;
        cell.cellFooterView.hidden=false;
       
        
        dynamicHeightOfRow=[self calculateHeight:cell.userNameDesc stringData:[dataDict objectForKey:@"Description"]];
        //Setting values
        
        cell.userNameDesc.frame=CGRectMake(80, 35,220,dynamicHeightOfRow);
        if(IS_IPHONE_4_OR_LESS||IS_IPHONE_5)
        {
            cell.userNameDesc.frame=CGRectMake(70, 35,220,dynamicHeightOfRow);
            
        }
        cell.userNameDesc.text=[dataDict objectForKey:@"Description"];
        //---
        CGFloat hhDesc=dynamicHeightOfRow+47;
        cell.endingLine.frame=CGRectMake(75, hhDesc+10, SCREEN_WIDTH-75, 1);
        cell.endingLine.backgroundColor=[UIColor grayColor];
        //---
        
        cell.backGroundImageViewOfCell.frame=CGRectMake(5, 0, SCREEN_WIDTH-20,dynamicHeightOfRow+90);
        [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:FeedImageUser]] placeholderImage:[UIImage imageNamed:@"place_holder.png"]];
        cell.nameLblFeed.text=[NSString stringWithFormat:@"@%@",[dataDict objectForKey:@"ScreenName"]];
        cell.nameLblFeed.userInteractionEnabled=YES;
        cell.nameLblFeed.tag=indexPath.section;
//        UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tappedOnName:)];
//        [cell.nameLblFeed addGestureRecognizer:tapGesture];
        cell.favouriteLbl.text=[NSString stringWithFormat:@"%@",[dataDict objectForKey:@"RetweetCount"]];
        //favoriteCount
        
        cell.reTweetCount.text=[NSString stringWithFormat:@"%@",[dataDict objectForKey:@"RetweetCount"]];
        BOOL favorite,retweet;
        if(tableView==tweetTableView)
        {
           favorite=[[setTweetsFavoriteBool objectAtIndex:indexPath.section] boolValue];
           retweet =[[setTweetsReTweetBool objectAtIndex:indexPath.section] boolValue];
            //action of methods
            [cell.favouriteButton addTarget:self action:@selector(setTweetFavourite:) forControlEvents:UIControlEventTouchUpInside];
            [cell.reTweetButton addTarget:self action:@selector(reTweetActionTweet:) forControlEvents:UIControlEventTouchUpInside];
            [cell.tweetButton addTarget:self action:@selector(tweetView:) forControlEvents:UIControlEventTouchUpInside];
        }
        else if (tableView==mentionTableView)
        {
           favorite=[[setMentionFavoriteBool objectAtIndex:indexPath.section] boolValue];
           retweet=[[setMentionReTweetBool objectAtIndex:indexPath.section] boolValue];
            //action of methods
            [cell.favouriteButton addTarget:self action:@selector(setMentionFavourite:) forControlEvents:UIControlEventTouchUpInside];
            [cell.reTweetButton addTarget:self action:@selector(reTweetActionMention:) forControlEvents:UIControlEventTouchUpInside];
            [cell.tweetButton addTarget:self action:@selector(tweetView:) forControlEvents:UIControlEventTouchUpInside];
        }
       
        if(favorite)
        {
            [cell.favouriteButton setBackgroundImage:[UIImage imageNamed:@"favorites.png"] forState:UIControlStateNormal];
        }
        else
       {
            [cell.favouriteButton setBackgroundImage:[UIImage imageNamed:@"ic_action_fave_off_focused.png"] forState:UIControlStateNormal];
        }
    
       
        
        //---
        if(retweet)
        {
            [cell.reTweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_rt_on_focused.png"] forState:UIControlStateNormal];
        }
        else
        {
            [cell.reTweetButton setBackgroundImage:[UIImage imageNamed:@"ic_action_rt_off_focused.png"] forState:UIControlStateNormal];
           
        }
//
//        //-----------Frames of all button
        cell.reTweetButton.frame=CGRectMake(SCREEN_WIDTH/2-15,dynamicHeightOfRow+55, 30,25);
        cell.reTweetCount.frame=CGRectMake(SCREEN_WIDTH/2+20,dynamicHeightOfRow+55,40,25);
        cell.tweetButton.frame=CGRectMake(80,dynamicHeightOfRow+55,30,25);
        cell.favouriteLbl.frame=CGRectMake(SCREEN_WIDTH-55,dynamicHeightOfRow+55,40,25);
        cell.favouriteButton.frame=CGRectMake(SCREEN_WIDTH-85,dynamicHeightOfRow+55,30,30);
//        //------------
    }
    else if (tableView==userListTable)
    {
        static NSString *CellIdentifier = @"ShowUserList";
        
        cell = [tableView dequeueReusableCellWithIdentifier:nil];
        
        if (cell == nil)
        {
            cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
            cell.selectionStyle = UITableViewCellSelectionStyleNone;
        }
        NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:indexPath.section];
        [cell.userImage sd_setImageWithURL:[dict objectForKey:@"UserImageUrl"] placeholderImage:[UIImage imageNamed:@""]];
        cell.nameLblFeed.text=[dict objectForKey:@"TwitterUserName"];
        UIButton * checkBtn=[[UIButton alloc]init];
        checkBtn.frame=CGRectMake(cell.contentView.frame.size.width-60,5, 30, 30);
        if([selectedData containsObject:[dict objectForKey:@"TwitterUserName"]])
        {
            [checkBtn setBackgroundImage:[UIImage imageNamed:@"check_mark.png"] forState:UIControlStateNormal];
        }
        else
        {
            [checkBtn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
        }
        
        checkBtn.tag=indexPath.section;
        [checkBtn addTarget:self action:@selector(checkBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        [cell.contentView addSubview:checkBtn];
        return cell;
  
    }
    return cell;
}
-(void)tappedOnName:(UIGestureRecognizer *)recognizer
{
    NSLog(@"tag on the label %ld",(long)recognizer.view.tag);
    NSDictionary * dataDict=[wholeData objectAtIndex:recognizer.view.tag];
    NSLog(@"tapped name data %@",dataDict);
    NSString * userName=[dataDict objectForKey:@"ScreenName"];
    ProfileViewTboard * profileView=[[ProfileViewTboard alloc]initWithFrame:CGRectMake(0, 0,SCREEN_WIDTH,SCREEN_HEIGHT)];
    profileView.userScreenName=userName;
    [profileView fetchUserClickedTimeline];
    [self.view addSubview:profileView];
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
   
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    if(tableView==followTableView)
    {
    return [wholeData count];
    }
    else if (tableView==tweetTableView)
    {
        return [wholeDataTweet count];
    }
    else if (tableView==mentionTableView)
    {
        return [wholeDataMention count];
    }
    else
    {
        return [SingletonTboard sharedSingleton].allDataUser.count;
    }
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (tableView==followTableView)
    {
        NSDictionary * dataDict=[wholeData objectAtIndex:indexPath.section];
        UILabel * lbl=[[TTTAttributedLabel alloc]init];
        lbl.frame=CGRectMake(80,35,220, 80);
        lbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        lbl.numberOfLines=0;
        lbl.lineBreakMode=NSLineBreakByWordWrapping;
        dynamicHeightOfRow=[self calculateHeight:lbl stringData:[dataDict objectForKey:FeedDescription]];
        CGFloat height = MAX(dynamicHeightOfRow+90,80);
        return height;
    }
    else if(tableView==tweetTableView)
    {
        NSDictionary * dataDict=[wholeDataTweet objectAtIndex:indexPath.section];
        UILabel * lbl=[[TTTAttributedLabel alloc]init];
        lbl.frame=CGRectMake(80,35,220, 80);
        lbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        lbl.numberOfLines=0;
        lbl.lineBreakMode=NSLineBreakByWordWrapping;
        dynamicHeightOfRow=[self calculateHeight:lbl stringData:[dataDict objectForKey:@"Description"]];
        CGFloat height = MAX(dynamicHeightOfRow+90,80);
        return height;
  
    }
    else if (tableView==mentionTableView)
    {
        NSDictionary * dataDict=[wholeDataMention objectAtIndex:indexPath.section];
        UILabel * lbl=[[TTTAttributedLabel alloc]init];
        lbl.frame=CGRectMake(80,35,220, 80);
        lbl.font=[UIFont fontWithName:@"HelveticaNeue-Medium" size:15];
        lbl.numberOfLines=0;
        lbl.lineBreakMode=NSLineBreakByWordWrapping;
        dynamicHeightOfRow=[self calculateHeight:lbl stringData:[dataDict objectForKey:@"Description"]];
        CGFloat height = MAX(dynamicHeightOfRow+90,80);
        return height;
  
    }
    else
    {
        return 50;
    }
    

}
- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return [[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH-10,5)];
}
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if(section==0)
    {
        return 0;
    }
    return 5;
}
- (void)scrollViewDidScroll:(UIScrollView *)aScrollView
{
    if(aScrollView==followTableView)
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
        if([wholeData count]<180)
        {
        NSDictionary * dataDict=[wholeData lastObject];
        NSLog(@"data %@",[dataDict objectForKey:@"FollowerId"]);
        NSString * idFetch=[dataDict objectForKey:@"FollowerId"];
        [self fetchTimeline:idFetch];
        NSLog(@"load more rows");
        }
        }
    }//reload condition
    }//Scroll View
    else if (aScrollView==tweetTableView)
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
            if(!refreshTweet)
            {
                refreshTweet=true;
                if([wholeDataTweet count]<180)
                {
                    NSDictionary * dataDict=[wholeDataTweet lastObject];
                    NSLog(@"data %@",[dataDict objectForKey:@"FollowerId"]);
                    NSString * idFetch=[dataDict objectForKey:@"FollowerId"];
                    [self fetchTweetsReload:idFetch];
                    NSLog(@"load more rows");
                }
            }
        }//reload condition
 
    }
    else if (aScrollView==mentionTableView)
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
            if(!refreshMention)
            {
                refreshMention=true;
                if([wholeDataMention count]<180)
                {
                    NSDictionary * dataDict=[wholeDataMention lastObject];
                    NSLog(@"data %@",[dataDict objectForKey:@"FollowerId"]);
                    NSString * idFetch=[dataDict objectForKey:@"FollowerId"];
                    [self fetchMentionsReload:idFetch];
                    NSLog(@"load more rows");
                }
            }
        }//reload condition
  
    }
}
-(CGFloat)calculateHeight:(UILabel *)lbl stringData:(NSString *)stringData
{
    // Create a paragraph style with the desired line break mode
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineBreakMode = NSLineBreakByWordWrapping;
    
    // Create the attributes dictionary with the font and paragraph style
    NSDictionary *attributes = @{
                                 NSFontAttributeName:lbl.font,
                                 NSParagraphStyleAttributeName:paragraphStyle
                                 };
    
    // Call boundingRectWithSize:options:attributes:context for the string
    CGRect textRect = [stringData boundingRectWithSize:CGSizeMake(220,80)
                                           options:NSStringDrawingUsesLineFragmentOrigin
                                        attributes:attributes
                                           context:nil];
    
    float height = textRect.size.height;
   // NSLog(@"height of row %f",height);
    return height;
}
#pragma mark----
-(void)favouriteCountList
{
   id favorite= [[FHSTwitterEngine sharedEngine]getFavoritesForUser:[SingletonTboard sharedSingleton].currectUserTwitterId isID:YES andCount:20];
    NSLog(@"Favourite Count %@",favorite);
}
#pragma mark Favourite Methods
-(void)setFavourite:(UIButton *)button
{
    BOOL favoriteCheck=[[setFavoriteBool objectAtIndex:button.tag]boolValue];
    BOOL setFavBool=!favoriteCheck;
    [setFavoriteBool setObject:[NSNumber numberWithBool:setFavBool] atIndexedSubscript:button.tag];
    if(!favoriteCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeData objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:YES];
        NSLog(@"returned data %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeData];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeData objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeData=[NSArray arrayWithArray:tempArray];
            [self reloadTabelAfterFavourite:button.tag tableToReload:followTableView];
            
        }
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeData objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:NO];
        NSLog(@"returned data %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeData];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeData objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeData=[NSArray arrayWithArray:tempArray];
            
 [self reloadTabelAfterFavourite:button.tag tableToReload:followTableView];        }
    }
}


-(void)setTweetFavourite:(UIButton *)button
{
    BOOL favoriteCheck=[[setTweetsFavoriteBool objectAtIndex:button.tag]boolValue];
    BOOL setFavBool=!favoriteCheck;
    [setTweetsFavoriteBool setObject:[NSNumber numberWithBool:setFavBool] atIndexedSubscript:button.tag];
    
    if(!favoriteCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeDataTweet objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:YES];
        NSLog(@"returned data %@",returned);
        
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataTweet];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataTweet objectAtIndex:button.tag]];
            
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
           
            wholeDataTweet=[NSArray arrayWithArray:tempArray];
            [self reloadTabelAfterFavourite:button.tag tableToReload:tweetTableView];
       
        }
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeDataTweet objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:NO];
        NSLog(@"returned data %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
           
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataTweet];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataTweet objectAtIndex:button.tag]];
            
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataTweet=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:tweetTableView];
        }
        
        
        
    }
}
-(void)setMentionFavourite:(UIButton *)button
{
    BOOL favoriteCheck=[[setMentionFavoriteBool objectAtIndex:button.tag]boolValue];
    BOOL setFavBool=!favoriteCheck;
    [setMentionFavoriteBool setObject:[NSNumber numberWithBool:setFavBool] atIndexedSubscript:button.tag];
    
    if(!favoriteCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeDataMention objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:YES];
        NSLog(@"returned data %@",returned);
        
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataMention];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataMention objectAtIndex:button.tag]];
            
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            
            wholeDataMention=[NSArray arrayWithArray:tempArray];
            [self reloadTabelAfterFavourite:button.tag tableToReload:mentionTableView];
            
        }
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeDataMention objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]markTweet:[dict objectForKey:@"FollowerId"] asFavorite:NO];
        NSLog(@"returned data %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataMention];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataMention objectAtIndex:button.tag]];
            
            int tempVar=[[dict objectForKey:FeedFavouriteCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedFavouriteCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataMention=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:mentionTableView];
        }
        
        
        
    }
}
-(void)reloadTabelAfterFavourite:(int)tag tableToReload:(UITableView*)tableReload
{
    ///--------------------Reload Table-------------
    // Build the two index paths
    NSIndexPath* indexPath1 = [NSIndexPath indexPathForRow:0 inSection:tag];
    // Add them in an index path array
    NSArray* indexArray = [NSArray arrayWithObjects:indexPath1, nil];
    // Launch reload for the two index path
    [tableReload reloadRowsAtIndexPaths:indexArray withRowAnimation:UITableViewRowAnimationFade];
    //[followTableView reloadData];
  
}
-(void)reTweetAction:(UIButton*)button
{
    NSDictionary * dict=[wholeData objectAtIndex:button.tag];
    NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
    
    BOOL retweetCheck=[[setReTweetBool objectAtIndex:button.tag]boolValue];
    BOOL setRetweetBool=!retweetCheck;
    
    if(!retweetCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeData objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]retweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);

        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeData];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeData objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeData=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:followTableView];
            
        }
        
        
        
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeData objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]destroyTweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeData];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeData objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeData=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:followTableView];
            
        }
        
        
        
    }

    
    
}
-(void)reTweetActionTweet:(UIButton*)button
{
    NSDictionary * dict=[wholeDataTweet objectAtIndex:button.tag];
    NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
    
    BOOL retweetCheck=[[setTweetsReTweetBool objectAtIndex:button.tag]boolValue];
    BOOL setRetweetBool=!retweetCheck;
    
    if(!retweetCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeDataTweet objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]retweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);
        
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setTweetsReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataTweet];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataTweet objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataTweet=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:tweetTableView];
            
        }
        
        
        
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeDataTweet objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]destroyTweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setTweetsReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataTweet];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataTweet objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataTweet=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:tweetTableView];
            
        }
        
        
        
    }
    
    
    
}
-(void)reTweetActionMention:(UIButton*)button
{
    NSDictionary * dict=[wholeDataMention objectAtIndex:button.tag];
    NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
    
    BOOL retweetCheck=[[setMentionReTweetBool objectAtIndex:button.tag]boolValue];
    BOOL setRetweetBool=!retweetCheck;
    
    if(!retweetCheck)
    {
        //Set to Favourite
        NSDictionary * dict=[wholeDataMention objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]retweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);
        
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setMentionReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataMention];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataMention objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar++;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataMention=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:mentionTableView];
            
        }
        
        
        
    }
    else
    {
        //Set to undo Favourite
        NSDictionary * dict=[wholeDataMention objectAtIndex:button.tag];
        NSLog(@"Follower Id %@",[dict objectForKey:@"FollowerId"]);
        id returned=[[FHSTwitterEngine sharedEngine]destroyTweet:[dict objectForKey:@"FollowerId"]];
        NSLog(@"returned data of retweet %@",returned);
        if([returned isKindOfClass:[NSError class]])
        {
            
        }
        else
        {
            [setMentionReTweetBool setObject:[NSNumber numberWithBool:setRetweetBool] atIndexedSubscript:button.tag];
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataMention];
            NSMutableDictionary * dictTemp=[[NSMutableDictionary alloc]initWithDictionary:[wholeDataMention objectAtIndex:button.tag]];
            int tempVar=[[dict objectForKey:FeedRetweetCount] intValue];
            tempVar--;
            [dictTemp setObject:[NSNumber numberWithInt:tempVar] forKey:FeedRetweetCount];
            [tempArray replaceObjectAtIndex:button.tag withObject:dictTemp];
            wholeDataMention=[NSArray arrayWithArray:tempArray];
            
            [self reloadTabelAfterFavourite:button.tag tableToReload:mentionTableView];
            
        }
        
        
        
    }
    
    
    
}


#pragma mark Tweet Methods
-(void)tweetView:(UIButton *)tweetBtn
{
    NSDictionary * detail=[wholeData objectAtIndex:tweetBtn.tag];
//------
    UIView * parentBgTweetView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)];
    [self.view insertSubview:parentBgTweetView belowSubview:self.view];
    UITapGestureRecognizer * tapOnParentView=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapOnParentBg)];
    [parentBgTweetView addGestureRecognizer:tapOnParentView];
    NSDictionary * dataDict=[wholeData objectAtIndex:tweetBtn.tag];
    tweetIdToPost=[dataDict objectForKey:@"FollowerId"];
    //UI
    bgTweetView=[[UIView alloc]init];
    bgTweetView.frame=CGRectMake(10, SCREEN_HEIGHT/2-180,SCREEN_WIDTH-20,270);
    bgTweetView.backgroundColor=[UIColor grayColor];
    bgTweetView.layer.borderColor=[UIColor blackColor].CGColor;
    bgTweetView.layer.borderWidth=1;
    [parentBgTweetView addSubview:bgTweetView];
    //Cross Button
    crossButton=[[UIButton alloc]init];
    crossButton.frame=CGRectMake(SCREEN_WIDTH-30,bgTweetView.frame.origin.y-10, 30, 30);
    crossButton.layer.cornerRadius=15;
    crossButton.clipsToBounds=YES;
    crossButton.backgroundColor=[UIColor whiteColor];
    [crossButton setBackgroundImage:[UIImage imageNamed:@"close_btnPopUp.png"] forState:UIControlStateNormal];
    [crossButton addTarget:self action:@selector(cancelTweetAction) forControlEvents:UIControlEventTouchUpInside];
    [parentBgTweetView addSubview:crossButton];
    //-----
    UIView * addUserView=[[UIView alloc]initWithFrame:CGRectMake(10, 20, bgTweetView.frame.size.width-20, 50)];
    addUserView.backgroundColor=[UIColor whiteColor];
    addUserView.userInteractionEnabled=YES;
    [bgTweetView addSubview:addUserView];
        
    UIImageView * addUserImage=[[UIImageView alloc]initWithFrame:CGRectMake(10, 5,30,40)];
    addUserImage.userInteractionEnabled=YES;
    addUserImage.image=[UIImage imageNamed:@"ic_menu_invite.png"];
    [addUserView addSubview:addUserImage];
    UITapGestureRecognizer * tapGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapGestureAddAcc)];
    [addUserImage addGestureRecognizer:tapGesture];
        
    UILabel * addUserLbl=[[UILabel alloc]initWithFrame:CGRectMake(45, 5, 100, 30)];
    addUserLbl.textColor=[UIColor grayColor];
    addUserLbl.text=@"Add Users";
    [addUserImage addSubview:addUserLbl];
        
    seclectedAccount=[[UILabel alloc]initWithFrame:CGRectMake(bgTweetView.frame.size.width-120, 5, 120,30)];
    seclectedAccount.textColor=[UIColor grayColor];
    seclectedAccount.text=[NSString stringWithFormat:@"Selected :%@",@0];
        [addUserImage addSubview:seclectedAccount];
        
    yFrTxtView=addUserView.frame.origin.y+60;
    txtViewTweet=[[UITextView alloc]initWithFrame:CGRectMake(10,yFrTxtView,bgTweetView.frame.size.width-80,60)];
    txtViewTweet.text=[NSString stringWithFormat:@"@%@",[detail objectForKey:FeedUserScreenName]];
    txtViewTweet.backgroundColor=[UIColor whiteColor];
    txtViewTweet.returnKeyType=UIReturnKeyDone;
    txtViewTweet.delegate=self;
    [bgTweetView addSubview:txtViewTweet];
        
  //  CGFloat yFrTweetBtn=txtViewTweet.frame.origin.y+130;
        
    UIButton * tweetBtnAction=[[UIButton alloc]initWithFrame:CGRectMake(bgTweetView.frame.size.width-60, yFrTxtView+20,50,20)];
    [tweetBtnAction addTarget:self action:@selector(tweetActoin)forControlEvents:UIControlEventTouchUpInside];
    tweetBtnAction.backgroundColor=ThemeColor;
    tweetBtnAction.titleLabel.font=[UIFont boldSystemFontOfSize:15];
    [tweetBtnAction setTitle:@"Tweet" forState:UIControlStateNormal];
    tweetBtnAction.titleLabel.font=[UIFont boldSystemFontOfSize:10];
    [bgTweetView addSubview:tweetBtnAction];
    //-----------
    bgTweetView.frame=CGRectMake(10, SCREEN_HEIGHT/2-180,SCREEN_WIDTH-20, yFrTxtView+80);
}
-(void)tapGestureAddAcc
{
    userListView=[[UIView alloc]initWithFrame:CGRectMake(0,0, SCREEN_WIDTH, SCREEN_HEIGHT-50)];
    [self.view addSubview:userListView];
    
    userListTable=[[UITableView alloc]initWithFrame:CGRectMake(5,100,userListView.frame.size.width-10, SCREEN_HEIGHT-50) style:UITableViewStylePlain];
    userListTable.delegate=self;
    userListTable.dataSource=self;
    userListTable.separatorStyle=UITableViewCellSelectionStyleNone;
    CGFloat tableHeight=[[SingletonTboard sharedSingleton].allDataUser count]*48.5+80;
    userListTable.frame=CGRectMake(5,0,userListView.frame.size.width-10,tableHeight);
    [userListView addSubview:userListTable];
    //--
    UIView *footerView=[[UIView alloc]initWithFrame:CGRectMake(0,0,SCREEN_WIDTH,50)];
    footerView.backgroundColor=[UIColor whiteColor];
    //--
    UIView * sepreatorLine=[[UIView alloc]initWithFrame:CGRectMake(0, 1,SCREEN_WIDTH, 1)];
    sepreatorLine.backgroundColor=[UIColor blackColor];
    [footerView addSubview:sepreatorLine];
    //--
    UIButton * doneBtn=[[UIButton alloc]initWithFrame:CGRectMake(SCREEN_WIDTH/2-40, 5, 80,30)];
    [doneBtn setTitle:@"Done" forState:UIControlStateNormal];
    [doneBtn addTarget:self action:@selector(doneSelectonOfUser) forControlEvents:UIControlEventTouchUpInside];
    doneBtn.backgroundColor=ThemeColor;
    [footerView addSubview:doneBtn];
    
    userListTable.tableFooterView=footerView;
    userListTable.backgroundColor=[UIColor grayColor];
    
    userListView.backgroundColor=[[UIColor blackColor] colorWithAlphaComponent:0.4];
    userListView.frame=CGRectMake(0, 10,SCREEN_WIDTH,SCREEN_HEIGHT);
    
}
-(void)checkBtnAction:(UIButton *)btn
{
    NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:btn.tag];
    if([selectedData containsObject:[dict objectForKey:@"TwitterUserName"]])
    {
        [selectedData removeObject:[dict objectForKey:@"TwitterUserName"]];
        [btn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
        
    }
    else
    {
        [selectedData addObject:[dict objectForKey:@"TwitterUserName"]];
        [btn setBackgroundImage:[UIImage imageNamed:@"check_mark.png"] forState:UIControlStateNormal];
    }
    NSLog(@"Selected User %@",selectedData);
    
}
-(void)doneSelectonOfUser
{
    [userListView removeFromSuperview];
    seclectedAccount.text=[NSString stringWithFormat:@"Selected:%lu",(unsigned long)[selectedData count]];
}
-(void)tapOnParentBg
{
    [txtViewTweet resignFirstResponder];
}
-(void)backAction
{
    [backViewTweet removeFromSuperview];
}
-(void)selectImage
{
    UIActionSheet *actionSheet;
    if(!actionSheet)
    actionSheet= [[UIActionSheet alloc] initWithTitle:@"Choose Photo" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Take Photo",@"Select from Gallery", nil];
    [actionSheet showInView:[UIApplication sharedApplication].keyWindow];
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
    if (buttonIndex==0)
    {
        imagePicker.sourceType=UIImagePickerControllerSourceTypeCamera;
        [self presentViewController:imagePicker animated:YES completion:nil];
    }
    else if (buttonIndex==1)
    {
        imagePicker.sourceType=UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentViewController:imagePicker animated:YES completion:nil];
    }
}

-(void)tweetActoin
{
    
    if(imageToPost)
    {
        NSData * data=UIImageJPEGRepresentation(imageToPost,0.5);
        [[FHSTwitterEngine sharedEngine]postTweet:textToTweet withImageData:data];
    }
    else
    {
        [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
       id returned=[[FHSTwitterEngine sharedEngine]postTweet:textToTweet inReplyTo:tweetIdToPost];
        NSLog(@"tweet response %@",returned);
        UIAlertView * tweetPosted=[[UIAlertView alloc]initWithTitle:@"" message:@"Tweet Posted" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [tweetPosted show];
        [self hideHUDLoadingView];
        NSArray *subviews = [self.view subviews];
        [[subviews lastObject] removeFromSuperview];

    }
    
    
    
    [backViewTweet removeFromSuperview];
}
-(void)cancelTweetAction
{
    NSArray *subviews = [self.view subviews];
    [[subviews lastObject] removeFromSuperview];
}
#pragma mark Text Field Delegates

- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    maxLength.hidden=YES;
    return YES;
}
- (void)textViewDidBeginEditing:(UITextView *)textView
{
     textToTweet=textView.text;
}
- (void)textViewDidEndEditing:(UITextView *)textView
{
    countEndOfLine=0;
}
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    NSUInteger characterCount = [textView.text length];
    if(characterCount>=140)
    {
        UIAlertView * alertTweet=[[UIAlertView alloc]initWithTitle:@"" message:@"Word Limit Exceeded than 40" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [alertTweet show];
    }
     textToTweet=[NSString stringWithFormat:@"%@%@",textView.text,text];
    return YES;
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [txtViewTweet resignFirstResponder];

}

#pragma mark to Fetch Pagenation Feed
-(void)fetchTimeline:(NSString *)sinceID
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            //Removing Repeated Feed
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeData];
            [tempArray removeLastObject];
            wholeData=[tempArray arrayByAddingObjectsFromArray:[twittHelperObj fetchTimeline:sinceID]];
            for(int i=0;i<[wholeData count];i++)
            {
                NSDictionary * dataDict=[wholeData objectAtIndex:i];
                [setFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
                [setReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];

            }

            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool {
                    
                    refresh=false;
                    [followTableView reloadData];
                    [self hideHUDLoadingView];
                    // [av show];
                }
            });
        }
    });
    
}
-(void)fetchTweetsReload:(NSString *)sinceOrMaxId
{
       NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataTweet];
    if([tempArray count]<20)
    {
        refreshTweet=YES;
        return;
    }
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
   
    
   
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            

            
            if([self checkTheRepetationOfData:tempArray newData:[twittHelperObj fetchOwnTweet:[SingletonTboard sharedSingleton].currectUserTwitterId sinceOrMaxID:sinceOrMaxId]])
            {
                dispatch_sync(dispatch_get_main_queue(), ^{
                    @autoreleasepool
                    {
                        refreshTweet=YES;
                        [self hideHUDLoadingView];
                        return;
                    }
                });

               
            }
            [tempArray removeLastObject];
            wholeDataTweet=[tempArray arrayByAddingObjectsFromArray: [twittHelperObj fetchOwnTweet:[SingletonTboard sharedSingleton].currectUserTwitterId sinceOrMaxID:sinceOrMaxId]];
            [setTweetsFavoriteBool removeAllObjects];
            [setTweetsReTweetBool removeAllObjects];
            for(int i=0;i<[wholeDataTweet count];i++)
            {
                NSDictionary * dataDict=[wholeDataTweet objectAtIndex:i];
                [setTweetsFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
                [setTweetsReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
            }
            
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                    refreshTweet=false;
                    [tweetTableView reloadData];
                    [self hideHUDLoadingView];
                    // [av show];
                }
            });
        }
    });
    
}
-(void)fetchMentionsReload:(NSString *)sinceOrMaxId
{
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            NSMutableArray * tempArray=[[NSMutableArray alloc]initWithArray:wholeDataMention];
            [tempArray removeLastObject];
           BOOL dataStatus=[self checkTheRepetationOfData:tempArray newData:[twittHelperObj fetchOwnMention:[SingletonTboard sharedSingleton].currectUserTwitterId sinceOrMaxID:sinceOrMaxId]];
            if(dataStatus)
            {
                dispatch_sync(dispatch_get_main_queue(), ^{
                    @autoreleasepool
                    {
                        refreshMention=true;
                        [self hideHUDLoadingView];
                        // [av show];
                    }
                });

                return;
            }
            else
            {
                
            }
            wholeDataMention=[tempArray arrayByAddingObjectsFromArray: [twittHelperObj fetchOwnMention:[SingletonTboard sharedSingleton].currectUserTwitterId sinceOrMaxID:sinceOrMaxId]];
            [setTweetsFavoriteBool removeAllObjects];
            [setTweetsReTweetBool removeAllObjects];
            
            for(int i=0;i<[wholeDataTweet count];i++)
            {
                NSDictionary * dataDict=[wholeDataTweet objectAtIndex:i];
                [setMentionFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
                [setMentionReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
            }
            
            dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool
                {
                    refreshMention=false;
                    [mentionTableView reloadData];
                    [self hideHUDLoadingView];
                    // [av show];
                }
            });
        }
    });
    
}
#pragma Check Repetation
-(BOOL)checkTheRepetationOfData:(NSArray*)previousData newData:(NSArray*)newData
{
    for (int i=0;i<previousData.count; i++)
    {
        NSDictionary * localDict=[previousData objectAtIndex:i];
        NSString * feedId=[localDict objectForKey:@"FollowerId"];
        for (int j=0; j<newData.count; i++)
        {
            NSDictionary * newLocalDict=[previousData objectAtIndex:i];
            NSString * newDataID=[newLocalDict objectForKey:@"FollowerId"];

            if([feedId isEqualToString:newDataID])
            {
                return YES;
            }
        }
    }
    return false;
}
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingImage:(UIImage *)image editingInfo:(NSDictionary *)editingInfo {
    
    NSLog(@"Image Info -=-= %@", editingInfo);
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    NSLog(@"Image Info Picking Media-=-= %@", info);
    
    imageToPost=[info objectForKey:@"UIImagePickerControllerOriginalImage"];
    placeHolderImage.image=imageToPost;
    // Upload image
    
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark -
#pragma mark - Loading View mbprogresshud

-(void) showHUDLoadingView:(NSString *)strTitle
{
     [self performSelectorOnMainThread:@selector(addProgressView) withObject:nil waitUntilDone:YES];
  
}
-(void)addProgressView
{
    if(!HUD)
    {
        HUD = [[MBProgressHUD alloc] init];
        HUD.backgroundColor=[UIColor clearColor];
        [self.view addSubview:HUD];
    }
    //HUD.delegate = self;
    //HUD.labelText = [strTitle isEqualToString:@""] ? @"Loading...":strTitle;
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
        self.view.backgroundColor=[UIColor colorWithRed:(CGFloat)220/255 green:(CGFloat)220/255 blue:(CGFloat)220/255 alpha:1];
  
    }
}

#pragma mark
-(void)setFavouriteBoolOfTweet:(NSArray*)tweetArray
{
   
        for(int i=0;i<[tweetArray count];i++)
        {
            NSDictionary * dataDict=[tweetArray objectAtIndex:i];
            [setTweetsFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
            [setTweetsReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
        }
  
    
}
-(void)setFavouriteBoolOfMention:(NSArray*)tweetArray
{
    
    for(int i=0;i<[tweetArray count];i++)
    {
        NSDictionary * dataDict=[tweetArray objectAtIndex:i];
        [setMentionFavoriteBool addObject:[dataDict objectForKey:FeedFavouriteSet]];
        [setMentionReTweetBool addObject:[dataDict objectForKey:FeedRetweetSet]];
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

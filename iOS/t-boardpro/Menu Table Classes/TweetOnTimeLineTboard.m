//
//  TweetOnTimeLine.m
//  TwitterBoard
//
//  Created by GLB-254 on 5/19/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "TweetOnTimeLineTboard.h"
#import "TableCustomCell.h"
#import "SingletonTboard.h"
#import "UIImageView+WebCache.h"
@interface TweetOnTimeLineTboard ()

@end

@implementation TweetOnTimeLineTboard

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.view.backgroundColor=[UIColor colorWithRed:(CGFloat)220/255 green:(CGFloat)220/255 blue:(CGFloat)220/255 alpha:1];
    selectedData=[[NSMutableArray alloc]init];
    [self createUi];
}
-(void)createUi
{
    UIView * addUserView=[[UIView alloc]initWithFrame:CGRectMake(10, 20, SCREEN_WIDTH-20, 50)];
    addUserView.backgroundColor=[UIColor whiteColor];
    addUserView.userInteractionEnabled=YES;
    [self.view addSubview:addUserView];
    
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
    
    seclectedAccount=[[UILabel alloc]initWithFrame:CGRectMake(SCREEN_WIDTH-120, 5, 120,30)];
    seclectedAccount.textColor=[UIColor grayColor];
    seclectedAccount.text=[NSString stringWithFormat:@"Selected :%@",@0];
    [addUserImage addSubview:seclectedAccount];
    
    CGFloat yFrTxtView=addUserView.frame.origin.y+60;
    txtView=[[UITextView alloc]initWithFrame:CGRectMake(10,yFrTxtView,SCREEN_WIDTH-20,120)];
    txtView.delegate=self;
    [self.view addSubview:txtView];
    
    CGFloat yFrTweetBtn=txtView.frame.origin.y+130;
    
    UIView * tweetBtnView=[[UIView alloc]initWithFrame:CGRectMake(10,yFrTweetBtn, SCREEN_WIDTH-20,40)];
    tweetBtnView.backgroundColor=[UIColor clearColor];
    [self.view addSubview:tweetBtnView];
    
    UIButton * tweetBtnAction=[[UIButton alloc]initWithFrame:CGRectMake(5, 5,tweetBtnView.frame.size.width-10, 40)];
    [tweetBtnAction addTarget:self action:@selector(tweetAction:)forControlEvents:UIControlEventTouchUpInside];
    tweetBtnAction.backgroundColor=ThemeColor;
    tweetBtnAction.titleLabel.font=[UIFont boldSystemFontOfSize:15];
    [tweetBtnAction setTitle:@"Tweet" forState:UIControlStateNormal];
    [tweetBtnView addSubview:tweetBtnAction];

}
-(void)tapGestureAddAcc
{
    userListView=[[UIView alloc]initWithFrame:CGRectMake(0, 10, SCREEN_WIDTH, SCREEN_HEIGHT-50)];
    userListView.backgroundColor=[[UIColor blackColor] colorWithAlphaComponent:0.4];

    [self.view addSubview:userListView];
    
    userListTable=[[UITableView alloc]init];
  
    userListTable.delegate=self;
    userListTable.dataSource=self;
    userListTable.bounces=NO;
    userListTable.separatorStyle=UITableViewCellSeparatorStyleSingleLine;
    [userListView addSubview:userListTable];
    //--
    CGFloat heightTable=[SingletonTboard sharedSingleton].allDataUser.count*48.5;
      userListTable.frame=CGRectMake(5,10,userListView.frame.size.width-10,heightTable+30);
    UIView *headerView=[[UIView alloc]initWithFrame:CGRectMake(0,0,SCREEN_WIDTH,30)];
    headerView.backgroundColor=[UIColor whiteColor];
    userListTable.tableHeaderView=headerView;
    //--
    UIView * sepreatorLine=[[UIView alloc]initWithFrame:CGRectMake(0,headerView.frame.size.height-1,SCREEN_WIDTH, 1)];
    sepreatorLine.backgroundColor=[UIColor blackColor];
    [headerView addSubview:sepreatorLine];

    //--
    UIButton * doneBtn=[[UIButton alloc]initWithFrame:CGRectMake(0,0, SCREEN_WIDTH,30)];
    [doneBtn setTitle:@"Done" forState:UIControlStateNormal];
    [doneBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [doneBtn addTarget:self action:@selector(doneSelectonOfUser) forControlEvents:UIControlEventTouchUpInside];
    doneBtn.backgroundColor=ThemeColor;
    [headerView addSubview:doneBtn];
    //---
    UIView *footerView=[[UIView alloc]initWithFrame:CGRectMake(0,0,SCREEN_WIDTH,10)];
    footerView.backgroundColor=[UIColor whiteColor];
    userListTable.tableFooterView=footerView;

    
    
}
-(void)doneSelectonOfUser
{
    if(selectedData.count>0)
    {
        accountSelected=true;
    }
    else
    {
        accountSelected=false;
  
    }
    [userListView removeFromSuperview];
    seclectedAccount.text=[NSString stringWithFormat:@"Selected:%lu",(unsigned long)[selectedData count]];
}
-(void)tweetAction:(UIButton*)tweetActionBtn
{
    if([txtView.text isEqualToString:@""]||[txtView.text isEqualToString:@" "])
    {
        return;
    }
    else if (!accountSelected)
    {
       UIAlertView * alertAcc= [[UIAlertView alloc]initWithTitle:@"" message:@"Select Account First" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [alertAcc show];
        return;
    }
    [NSThread detachNewThreadSelector:@selector(showHUDLoadingView:) toTarget:self withObject:nil];
    NSArray * allUserData=[SingletonTboard sharedSingleton].allDataUser;
    for (int i=0; i<[selectedData count]; i++)
    {
        NSDictionary * dict=[allUserData objectAtIndex:i];
        if([selectedData containsObject:[dict objectForKey:@"TwitterUserName"]])
        {
        NSString *straccessToken=[dict objectForKey:@"AccessTokenTwitter"];
        [[NSUserDefaults standardUserDefaults]setObject:straccessToken  forKey:@"SavedAccessHTTPBody"];
        [[FHSTwitterEngine sharedEngine]loadAccessToken];
        id returnedData=[[FHSTwitterEngine sharedEngine]postTweet:txtView.text];
        NSLog(@"returned data %@",returnedData);
        
        }
        txtView.text=@"";
        UIAlertView * tweetPosted=[[UIAlertView alloc]initWithTitle:@"" message:@"Tweet Posted" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [tweetPosted show];
        
    }
    [self hideHUDLoadingView];

  
}

#pragma mark -
#pragma mark - Loading View mbprogresshud

-(void) showHUDLoadingView:(NSString *)strTitle
{
    [self addOnMainThread];
}
-(void)addOnMainThread
{
    if(!HUD)
    {
        HUD = [[MBProgressHUD alloc] init];
        HUD.backgroundColor=[UIColor clearColor];
        [self.view addSubview:HUD];
    }
    //HUD.delegate = self;
    //HUD.labelText = [strTitle isEqualToString:@""] ? @"Loading...":strTitle;
    HUD.detailsLabelText= @"Loading...";
    [HUD show:YES];
   
}

-(void) hideHUDLoadingView
{
    
    [HUD removeFromSuperview];
    HUD=nil;
}

#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"ShowUserList";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:nil];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:indexPath.row];
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
    checkBtn.tag=indexPath.row;
    [checkBtn addTarget:self action:@selector(checkBtnAction:) forControlEvents:UIControlEventTouchUpInside];
    [cell.contentView addSubview:checkBtn];
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
        return [SingletonTboard sharedSingleton].allDataUser.count;

}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 48.5;
}
-(UILabel *)initializeLabel:(UILabel *)label
{
    label = [[UILabel alloc] init];
    label.textColor = [UIColor blackColor];
    label.font =[UIFont boldSystemFontOfSize:15];
    return label;
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
    //NSLog(@"height of row %f",height);
    return height;
}
-(void)checkBtnAction:(UIButton *)btn
{
    NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:btn.tag];
    NSLog(@"Selected Data %@",selectedData);
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
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [txtView resignFirstResponder];
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

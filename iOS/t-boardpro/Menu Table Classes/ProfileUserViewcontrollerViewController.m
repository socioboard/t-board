//
//  ProfileUserViewcontrollerViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/21/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "ProfileUserViewcontrollerViewController.h"
#import "FHSTwitterEngine.h"
#import "TableCustomCell.h"
#import "UIImageView+WebCache.h"
#import "Singleton.h"
#import "MBProgressHUD.h"
@interface ProfileUserViewcontrollerViewController ()

@end

@implementation ProfileUserViewcontrollerViewController
-(void)viewDidAppear:(BOOL)animated
{
    //Set Varable on View Appear
    NSURL * fetchImage=[NSURL URLWithString:[Singleton sharedSingleton].imageUrl];
    [self.userImageView sd_setImageWithURL:fetchImage placeholderImage:[UIImage imageNamed:@""]];
    NSLog(@"user Image %@",[Singleton sharedSingleton].imageUrl);
    nameLbl.text=[Singleton sharedSingleton].mainUserRealName;
     
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(userTimeLine) name:@"ReloadTimeLine" object:nil];

    if([Singleton networkCheck])
    {
        //Fetch User Time Line
        [self userTimeLine];
    }
    else
    {
        UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [noInternet show];
    }
    //Method for Ui of View
    [self createProfileUi];
    // Do any additional setup after loading the view from its nib.
}
-(void)userTimeLine
{
    dispatch_async(GCDBackgroundThread, ^{
        @autoreleasepool {
   id userTimeline=[[FHSTwitterEngine sharedEngine]getTimelineForUser:[Singleton sharedSingleton].currectUserTwitterId isID:YES count:20];
            NSMutableArray * userData=[[NSMutableArray alloc]init];
   NSLog(@"Returned data %@",userTimeline);
            for (int i=0;i<[userTimeline count]; i++)
            {
                NSMutableDictionary * tempForEachRow=[[NSMutableDictionary alloc]init];
                //----------------------------
                NSDictionary * dictTimelineRow=[userTimeline objectAtIndex:i];
                NSLog(@"text desc  %@",[dictTimelineRow objectForKey:@"text"]);
                id dictOfdict=[dictTimelineRow objectForKey:@"user"];
                //NSLog(@"profile url  %@",[dictOfdict objectForKey:@"profile_image_url"]);
                [Singleton sharedSingleton].tweetCount=[dictOfdict objectForKey:@"statuses_count"];
                [Singleton sharedSingleton].followerCount=[dictOfdict objectForKey:@"followers_count"];
                [Singleton sharedSingleton].followingCount=[dictOfdict objectForKey:@"friends_count"];
                
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"text"] forKey:@"Description"];
                [tempForEachRow setObject:[dictOfdict objectForKey:@"profile_image_url"] forKey:@"Userimage"];
                [tempForEachRow setObject:[dictTimelineRow objectForKey:@"id_str"] forKey:@"FollowerId"];
                [userData addObject:tempForEachRow];
                //----------------------------
            }
            wholeData=[NSArray arrayWithArray:userData];
            dispatch_async(dispatch_get_main_queue(), ^(void){
                
                [userTweetTable reloadData];
                
            });
        }});
}
-(void)createProfileUi
{
    
    
    self.userImageView=[[UIImageView alloc]init];
    self.userImageView.frame=CGRectMake(20,20,60,60);
    self.userImageView.layer.cornerRadius=30;
    self.userImageView.clipsToBounds=YES;
    [self.view addSubview:self.userImageView];
    
    
    nameLbl=[self initializeLabel:nameLbl];
    nameLbl.text=@"My Name";
    nameLbl.frame=CGRectMake(90,20,200,40);
    nameLbl.textAlignment=NSTextAlignmentLeft;
    [self.view addSubview:nameLbl];
    [self userTweetTable];

    
}
-(void)userTweetTable
{
    userTweetTable=[[UITableView alloc]initWithFrame:CGRectMake(0, 110,SCREEN_WIDTH,SCREEN_HEIGHT-50) style:UITableViewStylePlain];
    userTweetTable.dataSource=self;
    userTweetTable.delegate=self;
    userTweetTable.backgroundColor=[UIColor grayColor];
    [self.view addSubview:userTweetTable];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0, SCREEN_WIDTH,120);
    userTweetTable.tableFooterView=footerView;
}

#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Follow";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    NSDictionary * dataDict=[wholeData objectAtIndex:indexPath.row];
    cell.userNameDesc.text=[dataDict objectForKey:@"Description"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"Userimage"]] placeholderImage:[UIImage imageNamed:@""]];
    return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    [tableView reloadData];
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    
    return 1;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [wholeData count];
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 100;
}


-(UILabel *)initializeLabel:(UILabel *)label
{
    label = [[UILabel alloc] init];
    label.textColor = [UIColor blackColor];
    label.font =[UIFont boldSystemFontOfSize:15];
    return label;
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

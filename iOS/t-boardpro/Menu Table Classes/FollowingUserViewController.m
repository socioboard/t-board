//
//  FollowingUserViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/25/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "FollowingUserViewController.h"
#import "TableCustomCell.h"
#import "UIImageView+WebCache.h"
#import "MBProgressHUD.h"
@interface FollowingUserViewController ()

@end

@implementation FollowingUserViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //Method for Ui of View
    [self tableToShowUser];
    // Do any additional setup after loading the view from its nib.
}
-(void)tableToShowUser
{
    UILabel * headerLable=[[UILabel alloc]init];
    headerLable.frame=CGRectMake(0, 0, SCREEN_WIDTH, 60);
    headerLable.text=self.nameUser;
    headerLable.backgroundColor=ThemeColor;
    headerLable.textAlignment=NSTextAlignmentCenter;
    headerLable.textColor=[UIColor whiteColor];
    [self.view addSubview:headerLable];
    
    UIButton * backBtn=[[UIButton alloc]init];
    backBtn.frame=CGRectMake(5,10,30,30);
    [backBtn setBackgroundImage:[UIImage imageNamed:@"back_btnForall.png"] forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(popView:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    
    self.showFollowing=[[UITableView alloc]initWithFrame:CGRectMake(0, 60,SCREEN_WIDTH, SCREEN_HEIGHT) style:UITableViewStylePlain];
    self.showFollowing.delegate=self;
    self.showFollowing.dataSource=self;
    [self.view addSubview:self.showFollowing];
    UIView * footerView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH,100)];
    self.showFollowing.tableFooterView=footerView;
    
}
-(void)popView:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}
#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier=@"Follow";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    NSDictionary * dataDict=[self.allData objectAtIndex:indexPath.section];
    cell.userNameDesc.text=[dataDict objectForKey:@"Description"];
    [cell.userImage sd_setImageWithURL:[NSURL URLWithString:[dataDict objectForKey:@"Userimage"]] placeholderImage:[UIImage imageNamed:@""]];
    
    
    return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary * dataDict=[self.allData objectAtIndex:indexPath.section];
    NSLog(@"Follower Id %@",[dataDict objectForKey:@""]);
    
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
    NSLog(@"index path section %ld",(long)indexPath.section);
   
        return 140;
    
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

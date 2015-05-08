//
//  FollwViewController.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "Feeds.h"
#import "TableCustomCell.h"
#import <Social/Social.h>
#import "FHSTwitterEngine.h"
#import "Singleton.h"
#import "UIImageView+WebCache.h"
#import "TwitterHelperClass.h"
#import "MBProgressHUD.h"
@interface Feeds ()

@end

@implementation Feeds

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(fetchTimeline) name:@"ReloadTimeLine" object:nil];
    
    twittHelperObj=[[TwitterHelperClass alloc]init];
    
    //Method for Ui of View
    [self createFollowTable];
    if([Singleton networkCheck])
    {
      //Fetch Home Timeline of User
    [self fetchTimeline];
    }
    else
    {
        UIAlertView * noInternet=[[UIAlertView alloc]initWithTitle:@"Error" message:@"Check your Connection" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        [noInternet show];
    }
}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    
//    [self fetchTimeline];
   
}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
}
-(void)fetchTimeline
{
    [self showHUDLoadingView:nil];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @autoreleasepool {
            
             wholeData=[twittHelperObj fetchTimeline];
                               dispatch_sync(dispatch_get_main_queue(), ^{
                @autoreleasepool {
                    
                   //Show Ui
                    
                    [followTableView reloadData];
                    [self hideHUDLoadingView];
                   // [av show];
                }
            });
        }
    });

}
-(void)createFollowTable
{
    followTableView=[[UITableView alloc]initWithFrame:CGRectMake(0, 0, self.view.frame.size.width,self.view.frame.size.height) style:UITableViewStylePlain];
       followTableView.dataSource=self;
    followTableView.delegate=self;
    followTableView.backgroundColor=[UIColor whiteColor];
    [self.view addSubview:followTableView];
    UIView * footerView=[[UIView alloc]init];
    footerView.frame=CGRectMake(0, 0,self.view.frame.size.width,70);
    followTableView.tableFooterView=footerView;
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    
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
    NSLog(@"Follower Id %@",[dataDict objectForKey:@"FollowerId"]);
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
    return 120;
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

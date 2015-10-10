//
//  StatsViewControllerTboard.m
//  TwitterBoard
//
//  Created by GLB-254 on 9/14/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "StatsViewControllerTboard.h"
#import "GraphView.h"
#import "TwitterHelperClass.h"
#import "SingletonTboard.h"
@interface StatsViewControllerTboard ()
{
    GraphView * graphView;
    float maxValueInGraph;
}
@end

@implementation StatsViewControllerTboard

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    TwitterHelperClass * twitterObj=[[TwitterHelperClass alloc]init];
    NSArray * retrivedData=[twitterObj retriveUserStatus:[SingletonTboard sharedSingleton].currectUserTwitterId];
    NSLog(@"Retrived data from sqlite user stats %@",retrivedData);
    [self setTheGraphValues:retrivedData];
    [self animateView];
}
-(void)setTheGraphValues:(NSArray*)retriveData
{
    NSMutableArray * followingArrayCounts=[[NSMutableArray alloc]init];
    NSMutableArray * followerArrayCounts=[[NSMutableArray alloc]init];
    for (int i=0; i<1; i++)
    {
        NSDictionary * tempDict=[retriveData objectAtIndex:i];
        NSString * followingStr=[NSString stringWithFormat:@"%d",[[tempDict objectForKey:@"FollowingArray"] intValue]];
         NSString * followerStr=[NSString stringWithFormat:@"%d",[[tempDict objectForKey:@"FollowerArray"] intValue]];
        [followingArrayCounts addObject:followingStr];
        [followerArrayCounts addObject:followerStr];
    }
    followingCountGraph=[NSArray arrayWithArray:followingArrayCounts];
    followerCountGraph=[NSArray arrayWithArray:followerArrayCounts];
    int max1 = [[followerCountGraph valueForKeyPath:@"@max.intValue"] intValue];
     int max2 = [[followingCountGraph valueForKeyPath:@"@max.intValue"] intValue];
    if(max1>max2)
    {
        maxValueInGraph=max1;
    }
    else
    {
        maxValueInGraph=max2;
    }
    int reminder=(int)maxValueInGraph%10;
    reminder=10-reminder;
    maxValueInGraph=maxValueInGraph+reminder;
    int x = maxValueInGraph;
    maxValueInGraph = ((int)((x-1)/50) + 1)*50;
    [self addDemoGraph];
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //------Title Label
    UILabel * titleLbl=[[UILabel alloc]init];
    titleLbl.frame=CGRectMake(0, 0, SCREEN_WIDTH, 20);
    titleLbl.textAlignment=NSTextAlignmentCenter;
    titleLbl.text=@"Following Stats";
    titleLbl.font=[UIFont systemFontOfSize:15];
    titleLbl.textColor=[UIColor blackColor];
    [self.view addSubview:titleLbl];
    //---------
   
}
-(void)addDemoGraph
{

    //-------------------------
    NSDateFormatter * formatter=[[NSDateFormatter alloc]init];
    [formatter setDateFormat:@"MM-dd"];
    NSDate * curentDate=[NSDate date];
    NSString * dateStr=[formatter stringFromDate:curentDate];
    //-------------------------
    
    float difference=maxValueInGraph/5;
    float graphDifference=60;
    NSMutableArray * yCordinates=[[NSMutableArray alloc]init];
    for (int i=0; i<5; i++)
    {
        NSString * valueOfY=[NSString stringWithFormat:@"%.0f",difference*(i+1)];
        [yCordinates addObject:valueOfY];
    }
    graphView=[GraphView alloc];
    graphView.coordinateX=[NSArray arrayWithObjects:dateStr,nil];
    graphView.lineColorArray=@[[UIColor colorWithRed:(CGFloat)20/255 green:(CGFloat)200/255 blue:(CGFloat)254/255 alpha:1],[UIColor colorWithRed:(CGFloat)255/255 green:(CGFloat)60/255 blue:(CGFloat)40/255 alpha:1]];
    graphView.coordinateY=[yCordinates copy];
    graphView.scale=graphDifference/difference;
    
    graphView.graphValueArray=@[followerCountGraph,followingCountGraph];
    graphView.backgroundColor=[UIColor whiteColor];
    graphView=[graphView initWithFrame:CGRectMake(0,0,SCREEN_WIDTH,SCREEN_HEIGHT)];
    [self.view addSubview:graphView];
    //----------
    
}
-(void)animateView
{
    graphView.transform=CGAffineTransformMakeScale(0, 0);
    graphView.alpha = 0.0;
    [UIView animateWithDuration:1.5
                          delay: 0.5
                        options: UIViewAnimationOptionCurveEaseIn
                     animations:^{
                         graphView.transform=CGAffineTransformMakeScale(1,1);
                         graphView.alpha=1.0;
                     }
                     completion:nil];  // no completion handler
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

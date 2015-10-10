//
//  SchedulingView.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/27/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SchedulingViewControllerTboard.h"
#import "TwitterHelperClass.h"
#import "TableCustomCell.h"
#import "SingletonTboard.h"
#import "UIImageView+WebCache.h"
@interface SchedulingViewControllerTboard ()
{
    UILabel * headerLabel;
}
@end

@implementation SchedulingViewControllerTboard

- (void)viewDidLoad
{
    [super viewDidLoad];
    selectedData=[[NSMutableArray alloc]init];
    
    TwitterHelperClass * twitterObj=[[TwitterHelperClass alloc]init];
    NSLog(@"Scheduled OBJ %@",[twitterObj retriveScheduleInSqlite]);
    scheduleDataToDisplay=[NSArray arrayWithArray:[twitterObj retriveScheduleInSqlite]];
    [self headerOfView];
    if(scheduleDataToDisplay.count>0)
    {
    [self newUiScheduleTable];
    }
    // [self scheduleViewUi];
    // Do any additional setup after loading the view from its nib.
}
-(void)headerOfView
{
    UIView * headerView=[[UIView alloc]initWithFrame:CGRectMake(20,5,SCREEN_WIDTH-40,40)];
    headerView.backgroundColor=[UIColor whiteColor];
    headerView.layer.shadowOpacity=.5;
    headerView.layer.shadowOffset=CGSizeMake(0, 0);
    headerView.layer.shadowColor=[UIColor grayColor].CGColor;
    headerView.layer.shadowRadius=1;
    [self.view addSubview:headerView];
    
     headerLabel=[[UILabel alloc]initWithFrame:CGRectMake(5,10, SCREEN_WIDTH, 20)];
    headerLabel.text=[NSString stringWithFormat:@"Scheduled tweets:%ld",(unsigned long)scheduleDataToDisplay.count];
    headerLabel.font=[UIFont fontWithName:@"TimesNewRomanPS-BoldMT" size:15];
    headerLabel.textAlignment=NSTextAlignmentLeft;
    [headerView addSubview:headerLabel];
    
    UILabel * plusSign=[[UILabel alloc]init];
    plusSign.frame=CGRectMake(SCREEN_WIDTH-100,10,20,20);
    plusSign.text=@"+";
    plusSign.textColor=[UIColor blackColor];
    plusSign.font=[UIFont fontWithName:@"DINCondensed-Bold" size:15];
    [headerView addSubview:plusSign];
    
    UIButton * scheduleAddButton=[[UIButton alloc]init];
    scheduleAddButton.frame=CGRectMake(SCREEN_WIDTH-80,5,30,30);
    [scheduleAddButton setBackgroundImage:[UIImage imageNamed:@"schedule_icon.png"] forState:UIControlStateNormal];
    [scheduleAddButton addTarget:self action:@selector(scheduleTheMessage) forControlEvents:UIControlEventTouchUpInside];
    [headerView addSubview:scheduleAddButton];

}
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(changeBackGroundColor:) name:@"ChangeBackground" object:nil];
    //    [self fetchTimeline];
    
}
-(void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:YES];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:@"ChangeBackground" object:nil];
}
-(void)retriveUser
{
    
}
-(void)newUiScheduleTable
{
    if(! self.scheduleTable)
    {
    self.scheduleTable=[[UITableView alloc]initWithFrame:CGRectMake(5,50, SCREEN_WIDTH-10,SCREEN_HEIGHT-80)];
    self.scheduleTable.delegate=self;
    self.scheduleTable.dataSource=self;
    [self.view addSubview:self.scheduleTable];
    UIView * tableFooterView=[[UIView alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH-10,40)];
    self.scheduleTable.tableFooterView=tableFooterView;
    }
    else
    {
        [self.scheduleTable reloadData];
    }
    headerLabel.text=[NSString stringWithFormat:@"Scheduled tweets:%ld",(unsigned long)scheduleDataToDisplay.count];
}
-(void)scheduleViewUi
{
    composeView=[[UIView alloc]init];
    composeView .frame=CGRectMake(5, 5, SCREEN_WIDTH-10, SCREEN_HEIGHT-70);
    composeView.backgroundColor=[UIColor colorWithRed:(CGFloat)220/255 green:(CGFloat)220/255 blue:(CGFloat)220/255 alpha:1];
    composeView.layer.borderWidth=1;
    composeView.layer.borderColor=[UIColor blackColor].CGColor;
    [self.view addSubview:composeView];
    //-----------
   
    
   //Back Button
    UIButton * backBtn=[[UIButton alloc]init];
    backBtn.frame=CGRectMake(10,5,30,30);
    [backBtn setBackgroundImage:[UIImage imageNamed:@"back_btnForall.png"] forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(cancelSchedule) forControlEvents:UIControlEventTouchUpInside];
    [composeView addSubview:backBtn];
    //-----------
    UIView * headerView=[[UIView alloc]initWithFrame:CGRectMake(10,60, composeView.frame.size.width-20, 40)];
    headerView.userInteractionEnabled=YES;
    headerView.layer.opacity=.5;
    headerView.backgroundColor=[UIColor whiteColor];
    [composeView addSubview:headerView];
    
    UIImageView * scheduleImage=[[UIImageView alloc]init];
    scheduleImage.frame=CGRectMake(10,10,30,30);
    scheduleImage.image=[UIImage imageNamed:@"ic_menu_today.png"];
    [headerView addSubview:scheduleImage];
    scheduleImage.userInteractionEnabled=YES;
    UITapGestureRecognizer * tapClockGesture=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapToShowTime)];
    [scheduleImage addGestureRecognizer:tapClockGesture];
    
    dateLbl=[[UILabel alloc]init];
    dateLbl.frame=CGRectMake(scheduleImage.frame.origin.x+35,5, 150, 40);
    dateLbl.text=@"MM/dd/yy";
    dateLbl.font=[UIFont systemFontOfSize:13];
    [headerView addSubview:dateLbl];
    
    UIImageView * clockImage=[[UIImageView alloc]init];
    clockImage.frame=CGRectMake(composeView.frame.size.width-140,5,30,30);
    clockImage.image=[UIImage imageNamed:@"perm_group_device_alarms.png"];
    [headerView addSubview:clockImage];

    timeLbl=[[UILabel alloc]initWithFrame:CGRectMake(clockImage.frame.origin.x+35,5, 80, 40)];
    timeLbl.text=@"HH:mm:ss";
    timeLbl.font=[UIFont systemFontOfSize:13];
    [headerView addSubview:timeLbl];
    
    UIView * scheduleView=[[UIView alloc]initWithFrame:CGRectMake(10,headerView.frame.origin.y+60,composeView.frame.size.width-20,120)];
    scheduleTxtView=[[UITextView alloc]init];
    scheduleTxtView.frame=CGRectMake(0,0,composeView.frame.size.width-20,120);
    scheduleTxtView.delegate=self;
    scheduleTxtView.backgroundColor=[UIColor clearColor];
    scheduleTxtView.layer.borderColor=[UIColor blackColor].CGColor;
    scheduleTxtView.layer.cornerRadius=5;
    scheduleTxtView.layer.borderWidth=1;
    [scheduleView addSubview:scheduleTxtView];
    [composeView addSubview:scheduleView];

    placeHolderLbl=[[UILabel alloc]init];
    placeHolderLbl.text=@"Enter Your Text Here..";
    placeHolderLbl.textColor=[UIColor grayColor];
    placeHolderLbl.font=[UIFont boldSystemFontOfSize:13];
    placeHolderLbl.frame=CGRectMake(3, 0, 140, 20);
    [scheduleTxtView insertSubview:placeHolderLbl atIndex:1];
    
    UIView * addUserView=[[UIView alloc]initWithFrame:CGRectMake(10, scheduleView.frame.origin.y+130,composeView.frame.size.width-20, 40)];
    addUserView.backgroundColor=[UIColor whiteColor];
    [composeView addSubview:addUserView];
    
    UIImageView * addUserImage=[[UIImageView alloc]initWithFrame:CGRectMake(10,10,20,20)];
    addUserImage.image=[UIImage imageNamed:@"ic_menu_invite.png"];
    addUserImage.userInteractionEnabled=YES;
    [addUserView addSubview:addUserImage];
    UITapGestureRecognizer * tapImage=[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(tapAddAccount)];
    [addUserImage addGestureRecognizer:tapImage];

    UILabel * addUserLbl=[[UILabel alloc]init];
    addUserLbl.frame=CGRectMake(addUserImage.frame.origin.x+50,10, 120,20);
    addUserLbl.text=@"Add Users";
    [addUserView addSubview:addUserLbl];
    
    lblUserNo=[[UILabel alloc]initWithFrame:CGRectMake(composeView.frame.size.width-80,10, 60,20)];
    lblUserNo.text=[NSString stringWithFormat:@"%lu",(unsigned long)selectedData.count];
    [addUserView addSubview:lblUserNo];
    
   UIView * scheduleBtnView=[[UIView alloc]init];
    scheduleBtnView.frame=CGRectMake(10,addUserView.frame.origin.y+60, composeView.frame.size.width-20,70);
    [composeView addSubview:scheduleBtnView];
    
    
    UIButton * scheduleBtn=[[UIButton alloc]init];
    scheduleBtn.frame=CGRectMake(5,20,scheduleBtnView.frame.size.width-10,30);
    [scheduleBtn addTarget:self action:@selector(setSchedule) forControlEvents:UIControlEventTouchUpInside];
    scheduleBtn.backgroundColor=ThemeColor;
    [scheduleBtn setTitle:@"Schedule" forState:UIControlStateNormal];
    [scheduleBtnView addSubview:scheduleBtn];
    /*
    postImage=[[UIImageView alloc]init];
    postImage.frame=CGRectMake(0,0,SCREEN_WIDTH-120,120);
    postImage.alpha=.4;
    [scheduleTxtView insertSubview:postImage atIndex:0];
    
    UIButton * sendButton=[[UIButton alloc]init];
    sendButton.frame=CGRectMake(SCREEN_WIDTH-80,80,70, 40);
    [sendButton setBackgroundImage:[UIImage imageNamed:@"send.png"] forState:UIControlStateNormal];
    sendButton.backgroundColor=ThemeColor;
    [sendButton addTarget:self action:@selector(setSchedule) forControlEvents:UIControlEventTouchUpInside];
    [composeView addSubview:sendButton];
    
    UIView * buttonTray=[[UIButton alloc]init];
    buttonTray.frame=CGRectMake(0,170,SCREEN_WIDTH, 40);
    [self.view addSubview:buttonTray];
    CAGradientLayer *gradient = [CAGradientLayer layer];
    gradient.frame = CGRectMake(0, 0,SCREEN_WIDTH, 40);
    UIColor *firstColor =ThemeColor;
    UIColor *lastColor =[UIColor colorWithRed:(CGFloat)248/255 green:(CGFloat)248/255 blue:(CGFloat)255/255 alpha:0.5];
    gradient.colors = [NSArray arrayWithObjects:(id)[firstColor CGColor], (id)[lastColor CGColor],(id)[firstColor CGColor], nil];
    [buttonTray.layer insertSublayer:gradient atIndex:0];
    
    UIButton  *schedulButton = [UIButton buttonWithType:UIButtonTypeCustom];
    schedulButton.frame = CGRectMake(40,5,30,30);
    [schedulButton setBackgroundImage:[UIImage imageNamed:@"Scheduler.png"] forState:UIControlStateNormal];
    [schedulButton addTarget:self action:@selector(displayDatePicker:) forControlEvents:UIControlEventTouchUpInside];
    [buttonTray addSubview:schedulButton];
   
    UIButton  *imageToUploadBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    imageToUploadBtn.frame = CGRectMake(SCREEN_WIDTH-50,5,30,30);
    [imageToUploadBtn setBackgroundImage:[UIImage imageNamed:@"camera.png"] forState:UIControlStateNormal];
    [imageToUploadBtn addTarget:self action:@selector(imageUploadFromCameraAction:) forControlEvents:UIControlEventTouchUpInside];
    [buttonTray addSubview:imageToUploadBtn];
    
    UIButton  *imageToUploadGalaryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    imageToUploadGalaryBtn.frame = CGRectMake(SCREEN_WIDTH/2-15,5,30,30);
    [imageToUploadGalaryBtn setBackgroundImage:[UIImage imageNamed:@"galary.png"] forState:UIControlStateNormal];
    [imageToUploadGalaryBtn addTarget:self action:@selector(imageUploadAction:) forControlEvents:UIControlEventTouchUpInside];
    [buttonTray addSubview:imageToUploadGalaryBtn];
*/
}


-(void)tapAddAccount
{
    userListView=[[UIView alloc]initWithFrame:CGRectMake(0, 10, SCREEN_WIDTH, SCREEN_HEIGHT-50)];
    [composeView addSubview:userListView];
    
    userListTable=[[UITableView alloc]initWithFrame:CGRectMake(5,100,userListView.frame.size.width-10, SCREEN_HEIGHT-50) style:UITableViewStylePlain];
    userListTable.delegate=self;
    userListTable.dataSource=self;
    userListTable.separatorStyle=UITableViewCellSelectionStyleNone;
    CGFloat tableHeight=[[SingletonTboard sharedSingleton].allDataUser count]*48.5;
    userListTable.frame=CGRectMake(5, 10,userListView.frame.size.width-10,tableHeight+100);
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
    doneBtn.backgroundColor=[UIColor grayColor];
    [footerView addSubview:doneBtn];
    
    userListTable.tableFooterView=footerView;
    userListTable.backgroundColor=[UIColor grayColor];
    
    userListView.backgroundColor=[[UIColor blackColor] colorWithAlphaComponent:0.4];
    userListView.frame=CGRectMake(0, 10,SCREEN_WIDTH,SCREEN_HEIGHT);
    
}
-(void)doneSelectonOfUser
{
    [userListView removeFromSuperview];
    lblUserNo.text=[NSString stringWithFormat:@"%lu",(unsigned long)selectedData.count];
}
-(void)cancelSchedule
{
    [composeView removeFromSuperview];
}
#pragma mark Picker
-(void) imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    //get selected image
    UIImage *selImage = [info objectForKey:UIImagePickerControllerOriginalImage];
    postImage.contentMode = UIViewContentModeScaleAspectFit;
    postImage.backgroundColor=[UIColor colorWithPatternImage:selImage];
    imageToUpload=selImage;
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)imageUploadAction:(id)sender
{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    [self presentViewController:imagePicker animated:YES completion:nil];
}
-(void)imageUploadFromCameraAction:(id)sender
{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.sourceType=UIImagePickerControllerSourceTypeCamera;
    [self presentViewController:imagePicker animated:YES completion:nil];
}
-(void)setSchedule
{
   
    if(textToSend&&![textToSend isEqualToString:@""])
    {
        [self sendLocalNotification:nil];
        UIAlertView * alert=[[UIAlertView alloc]initWithTitle:@"Schedule" message:@"Schedule Submitted!" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [alert show];
        scheduleTxtView.text=@"";
         dateLbl.text=@"";
        timeLbl.text=@"";
        TwitterHelperClass * twitterObj=[[TwitterHelperClass alloc]init];
        NSLog(@"Scheduled OBJ %@",[twitterObj retriveScheduleInSqlite]);
         scheduleDataToDisplay=[NSArray arrayWithArray:[twitterObj retriveScheduleInSqlite]];
        [composeView removeFromSuperview];
        [self newUiScheduleTable];
 
    }
    else
    {
        UIAlertView * alert=[[UIAlertView alloc]initWithTitle:@"Warning" message:@"Text cannot be empty" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [alert show];
    }
    
}
-(void) displayDatePicker:(id)sender
{
    
    [UIView animateWithDuration:.5 animations:^{
        if (self.datePicker) {
            self.pickerView.hidden=NO;
            self.datePicker.date = [NSDate date];
            self.pickerView.frame = CGRectMake(0,SCREEN_HEIGHT-260,SCREEN_WIDTH,250);
        }
        else{
            //ui for date picker
            self.pickerView = [[UIView alloc] initWithFrame:CGRectMake(0,SCREEN_HEIGHT-260, SCREEN_WIDTH,250)];
            [composeView addSubview:self.pickerView];
            self.pickerView.backgroundColor = [UIColor whiteColor];
            CAGradientLayer *gradient = [CAGradientLayer layer];
            gradient.frame = CGRectMake(0, 0,SCREEN_WIDTH, 40);
            UIColor *firstColor = [UIColor colorWithRed:(CGFloat)0 green:(CGFloat)49/255 blue:(CGFloat)129/255 alpha:1.0];
            UIColor *lastColor =[UIColor colorWithRed:(CGFloat)0/255 green:(CGFloat)157/255 blue:(CGFloat)219/255 alpha:0.5];
            gradient.colors = [NSArray arrayWithObjects:(id)[firstColor CGColor], (id)[lastColor CGColor],(id)[firstColor CGColor], nil];

            [self.pickerView.layer insertSublayer:gradient atIndex:0];
            
//            //-----------------------------------------------
//            //Add Send Button
            UIButton *setTimeButton = [UIButton buttonWithType:UIButtonTypeCustom];
            setTimeButton.frame = CGRectMake(20, 7,100, 27);
            [setTimeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            setTimeButton.titleLabel.font = [UIFont fontWithName:@"Bodoni 72 Oldstyle" size:14.0f];
           [setTimeButton addTarget:self action:@selector(selectedDate:) forControlEvents:UIControlEventTouchUpInside];
            setTimeButton.layer.backgroundColor=[UIColor colorWithRed:(CGFloat)250/255 green:(CGFloat)174/255 blue:(CGFloat)220/255 alpha:.1].CGColor;;
            setTimeButton.layer.borderWidth=1.0f;
            setTimeButton.layer.borderColor = [UIColor colorWithRed:(CGFloat)0 green:(CGFloat)55/255 blue:(CGFloat)136/255 alpha:1].CGColor;
            setTimeButton.layer.cornerRadius = 5.0f;
            setTimeButton.clipsToBounds = YES;
            [setTimeButton setTitle:@"Set Time" forState:UIControlStateNormal];
            [self.pickerView addSubview:setTimeButton];
//            
//            
//            //----------------------------------------------
//            //Add Cancel Button
           UIButton *cancelTimer = [UIButton buttonWithType:UIButtonTypeCustom];
            cancelTimer.frame = CGRectMake(SCREEN_WIDTH-120, 7,100, 27);
            cancelTimer.titleLabel.font = [UIFont fontWithName:@"Bodoni 72 Oldstyle" size:14.0f];
            [cancelTimer addTarget:self action:@selector(hidePickerView:) forControlEvents:UIControlEventTouchUpInside];
             cancelTimer.layer.backgroundColor=[UIColor colorWithRed:(CGFloat)250/255 green:(CGFloat)174/255 blue:(CGFloat)220/255 alpha:.1].CGColor;;
            cancelTimer.layer.borderWidth=1.0f;
            cancelTimer.layer.borderColor = [UIColor colorWithRed:(CGFloat)0 green:(CGFloat)55/255 blue:(CGFloat)136/255 alpha:1].CGColor;
           cancelTimer.layer.cornerRadius = 5.0f;
            cancelTimer.clipsToBounds = YES;
            [cancelTimer setTitle:@"Cancel Button" forState:UIControlStateNormal];
            [self.pickerView addSubview:cancelTimer];
            self.datePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 40,SCREEN_WIDTH,200)];
            self.datePicker.datePickerMode = UIDatePickerModeDateAndTime;
            self.datePicker.minimumDate = [NSDate date];
            [self.pickerView addSubview:self.datePicker];
            
        }
    }];
    
    
}
//selecte date
-(void) selectedDate: (id)sender
{
    NSLog(@"Date Selected %@",self.datePicker);
    [UIView animateWithDuration:.5 animations:^{
        
        self.pickerView.hidden=YES;
        
        NSDate * dateOfAchedule=self.datePicker.date;
        NSLog(@"Date Of Schedule %@",dateOfAchedule);
        
        NSDateFormatter *dateFormatter=[[NSDateFormatter alloc]init];
        dateFormatter.dateFormat = @"MM/dd/yy";
        
        NSString *dateString = [dateFormatter stringFromDate:dateOfAchedule];
        
        NSDateFormatter *timeFormatter=[[NSDateFormatter alloc]init];
        timeFormatter.dateFormat = @"HH:mm:ss";
        NSString *timeString =[timeFormatter stringFromDate:dateOfAchedule];
        
        dateLbl.text=dateString;
        timeLbl.text=timeString;
        
    }];

}
-(void) hidePickerView: (id)sender{
    //self.pickerView.hidden = YES;
    [UIView animateWithDuration:.5 animations:^{
        
        self.pickerView.hidden=YES;
    }];
    
}
-(void)sendLocalNotification:(NSDateComponents*)dateComps
{
    for(int i=0;i<[selectedData count];i++)
    {
    UILocalNotification * localNotification = [[UILocalNotification alloc] init];
    // current time plus 9000(2.5 hrs) secs
    localNotification.fireDate = self.datePicker.date;
    time_t unixTime = (time_t) [[NSDate date] timeIntervalSince1970];
    NSNumber * numbToStore=[NSNumber numberWithLong:unixTime];
        NSString * tmeStampStr=[NSString stringWithFormat:@"%@",numbToStore];
    NSMutableDictionary * dictMessage=[[NSMutableDictionary alloc]init];
//-----------User Data
    NSDictionary * dict=[selectedData objectAtIndex:i];
    NSLog(@"selected user dict %@",dict);
    [dictMessage setObject:[dict objectForKey:@"TwitterUserName"] forKey:@"UserScreenName"];
    [dictMessage setObject:textToSend forKey:@"Text"];
    [dictMessage setObject:tmeStampStr forKey:@"TimeStamp"];
    localNotification.userInfo=dictMessage;
    localNotification.alertBody =@"Time to Post";
    localNotification.soundName = UILocalNotificationDefaultSoundName;
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    TwitterHelperClass * twtSave=[[TwitterHelperClass alloc]init];
  [twtSave saveScheduleInSqlite:[dict objectForKey:@"AccessTokenTwitter"] userImage:imageToUpload date:(int)unixTime userText:textToSend twitterId:[dict objectForKey:@"TwitterId"] UserScreenName:[dict objectForKey:@"TwitterUserName"]];
    [scheduleTxtView resignFirstResponder];
    }
    
}
-(NSString*)convertingIntoJsonString:(NSDictionary*)dict
{
    
    NSError *error = nil;
    NSData *json;
    NSString *jsonString;
    // Dictionary convertable to JSON ?
    if ([NSJSONSerialization isValidJSONObject:dict])
    {
        // Serialize the dictionary
        json = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
        
        // If no errors, let's view the JSON
        if (json != nil && error == nil)
        {
            jsonString = [[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding];
            jsonString=[NSString stringWithFormat:@"%@\r",jsonString];
            NSLog(@"JSON: %@", jsonString);
        }
    }
    return jsonString;
}
#pragma mark Show Date Time
-(void)tapToShowTime
{
    [UIView animateWithDuration:.5 animations:^{
        if (self.datePicker) {
            self.pickerView.hidden=NO;
            self.datePicker.date = [NSDate date];
            self.pickerView.frame = CGRectMake(0,SCREEN_HEIGHT-260,SCREEN_WIDTH,250);
        }
        else{
            //ui for date picker
            self.pickerView = [[UIView alloc] initWithFrame:CGRectMake(0,SCREEN_HEIGHT-260, SCREEN_WIDTH,250)];
            [composeView addSubview:self.pickerView];
            self.pickerView.backgroundColor = [UIColor whiteColor];
            /*CAGradientLayer *gradient = [CAGradientLayer layer];
            gradient.frame = CGRectMake(0, 0,SCREEN_WIDTH, 40);
            UIColor *firstColor = [UIColor colorWithRed:(CGFloat)0 green:(CGFloat)49/255 blue:(CGFloat)129/255 alpha:1.0];
            UIColor *lastColor =[UIColor colorWithRed:(CGFloat)0/255 green:(CGFloat)157/255 blue:(CGFloat)219/255 alpha:0.5];
            gradient.colors = [NSArray arrayWithObjects:(id)[firstColor CGColor], (id)[lastColor CGColor],(id)[firstColor CGColor], nil];
            
            [self.pickerView.layer insertSublayer:gradient atIndex:0];*/
            
            //            //-----------------------------------------------
            //            //Add Send Button
            UIButton *setTimeButton = [UIButton buttonWithType:UIButtonTypeCustom];
            setTimeButton.frame = CGRectMake(20, 7,100, 27);
            [setTimeButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
            setTimeButton.titleLabel.font = [UIFont fontWithName:@"Bodoni 72 Oldstyle" size:14.0f];
            [setTimeButton addTarget:self action:@selector(selectedDate:) forControlEvents:UIControlEventTouchUpInside];
            setTimeButton.layer.borderWidth=1.0f;
            setTimeButton.layer.borderColor = [UIColor blackColor].CGColor;
            setTimeButton.layer.cornerRadius = 5.0f;
            setTimeButton.clipsToBounds = YES;
            [setTimeButton setTitle:@"Set Time" forState:UIControlStateNormal];
            [setTimeButton setBackgroundColor:[UIColor whiteColor]];
            [self.pickerView addSubview:setTimeButton];
            //Add Cancel Button
            UIButton *cancelTimer = [UIButton buttonWithType:UIButtonTypeCustom];
            cancelTimer.frame = CGRectMake(SCREEN_WIDTH-120, 7,100, 27);
            cancelTimer.titleLabel.font = [UIFont fontWithName:@"Bodoni 72 Oldstyle" size:14.0f];
            [cancelTimer addTarget:self action:@selector(hidePickerView:) forControlEvents:UIControlEventTouchUpInside];
            cancelTimer.layer.borderWidth=1.0f;
            cancelTimer.layer.borderColor =[UIColor blackColor].CGColor;
            cancelTimer.layer.cornerRadius = 5.0f;
            cancelTimer.clipsToBounds = YES;
            [cancelTimer setTitle:@"Cancel Button" forState:UIControlStateNormal];
            [cancelTimer setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
            [cancelTimer setBackgroundColor:[UIColor whiteColor]];
            [self.pickerView addSubview:cancelTimer];
            
            self.datePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 40,SCREEN_WIDTH,200)];
            self.datePicker.datePickerMode = UIDatePickerModeDateAndTime;
            self.datePicker.minimumDate = [NSDate date];
            [self.pickerView addSubview:self.datePicker];
            
        }
    }];
  
}
-(void)checkBtnAction:(UIButton *)btn
{
    NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:btn.tag];
    
    
    
    if([selectedData containsObject:dict])
    {
        [selectedData removeObject:[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:btn.tag]];
        [btn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
        
    }
    else
    {
        [selectedData addObject:[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:btn.tag]];
        [btn setBackgroundImage:[UIImage imageNamed:@"check_mark.png"] forState:UIControlStateNormal];
    }
    NSLog(@"Selected User %@",selectedData);
    
}

#pragma mark Text View Delegates
-(void) textViewDidBeginEditing:(UITextView *)textView
{
   if([textView.text isEqualToString:@""])
   {
       placeHolderLbl.text=@"";
   }
}
-(void)textViewDidChange:(UITextView *)textView
{
    NSLog(@"String %@",textView.text);
    textToSend=textView.text;
}
-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    
        return YES;
}
-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    if([scheduleTxtView.text isEqualToString:@""])
    {
        placeHolderLbl.text=@"Enter Your Text Here..";
    }
    [scheduleTxtView resignFirstResponder];
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
#pragma mark Table View Delegates
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier=@"ScheduleTable";
    
    TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(tableView==self.scheduleTable)
    {
    if (cell == nil)
    {
        cell = [[TableCustomCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
        NSDictionary * tempDict=[scheduleDataToDisplay objectAtIndex:indexPath.section];
        cell.nameInSchedule.text=[NSString stringWithFormat:@"@%@",[tempDict objectForKey:@"TwitterScreenName"]];
        cell.nameInSchedule.textColor=ThemeColor;
        
        cell.descriptionInSchedule.text=[tempDict objectForKey:@"TwitterText"];
        NSDate * dateOfAchedule=[NSDate dateWithTimeIntervalSince1970:[[tempDict objectForKey:@"TwitterDate"] doubleValue]];
        NSLog(@"Date Of Schedule %@",dateOfAchedule);
       
        NSDateFormatter *dateFormatter=[[NSDateFormatter alloc]init];
        dateFormatter.dateFormat = @"MM/dd/yy";
        
        NSString *dateString = [dateFormatter stringFromDate:dateOfAchedule];
        NSDateFormatter *timeFormatter=[[NSDateFormatter alloc]init];
        timeFormatter.dateFormat = @"HH:mm:ss a";
        
        NSString *timeString =[timeFormatter stringFromDate:dateOfAchedule];
        cell.dateLblInSchedule.text=dateString;
        cell.deleteLabelInSchedule.text=timeString;
        return cell;
    }
    if(tableView==userListTable)
    {
        TableCustomCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ShowUserList"];
        
        cell=[[TableCustomCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ShowUserList"];
        
        NSDictionary * dict=[[SingletonTboard sharedSingleton].allDataUser objectAtIndex:indexPath.section];
        [cell.userImage sd_setImageWithURL:[dict objectForKey:@"UserImageUrl"] placeholderImage:[UIImage imageNamed:@""]];
        cell.nameLblFeed.text=[dict objectForKey:@"TwitterUserName"];
        cell.nameLblFeed.textColor=ThemeColor;
        UIButton * checkBtn=[[UIButton alloc]init];
        checkBtn.frame=CGRectMake(cell.contentView.frame.size.width-60,5, 30, 30);
        [checkBtn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
        checkBtn.tag=indexPath.section;
        [checkBtn addTarget:self action:@selector(checkBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        if([selectedData count]>0)
        {
        if([[SingletonTboard sharedSingleton].allDataUser containsObject:[selectedData objectAtIndex:indexPath.section]])
        {
           [checkBtn setBackgroundImage:[UIImage imageNamed:@"check_mark.png"] forState:UIControlStateNormal];
        }
        else
        {
            [checkBtn setBackgroundImage:[UIImage imageNamed:@"uncheck.png"] forState:UIControlStateNormal];
   
        }
        }
        [cell.contentView addSubview:checkBtn];
        return cell;

    }
        return cell;
}
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}
-(NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    if(tableView==userListTable)
    {
        return [[SingletonTboard sharedSingleton].allDataUser count];
    }
    return scheduleDataToDisplay.count;
}
-(NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(tableView==userListTable)
    {
        return 50;
    }
    else if (tableView==self.scheduleTable)
    {
       
            return 140;
     
    }
    return 0;
}
-(void)checkBtnAction
{
    
}
-(void)scheduleTheMessage
{
    [self scheduleViewUi];
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

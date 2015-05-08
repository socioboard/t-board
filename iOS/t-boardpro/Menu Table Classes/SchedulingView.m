//
//  SchedulingView.m
//  TwitterBoard
//
//  Created by GLB-254 on 4/27/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import "SchedulingView.h"
#import "TwitterHelperClass.h"
#import "Singleton.h"
@interface SchedulingView ()

@end

@implementation SchedulingView

- (void)viewDidLoad {
    [super viewDidLoad];
    //Method for Ui of View

    [self scheduleViewUi];
    // Do any additional setup after loading the view from its nib.
}
-(void)scheduleViewUi
{
    composeView=[[UIView alloc]init];
    composeView .frame=[UIScreen mainScreen].bounds;
    [self.view addSubview:composeView];
    
    scheduleTxtView=[[UITextView alloc]init];
    scheduleTxtView.frame=CGRectMake(20,40,SCREEN_WIDTH-120,120);
    scheduleTxtView.delegate=self;
    scheduleTxtView.backgroundColor=[UIColor clearColor];
    scheduleTxtView.layer.borderColor=[UIColor blackColor].CGColor;
    scheduleTxtView.layer.borderWidth=1;
    [composeView addSubview:scheduleTxtView];
    
    placeHolderLbl=[[UILabel alloc]init];
    placeHolderLbl.text=@"Enter Your Text Here..";
    placeHolderLbl.textColor=[UIColor grayColor];
    placeHolderLbl.font=[UIFont boldSystemFontOfSize:13];
    placeHolderLbl.frame=CGRectMake(3, 0, 140, 20);
    [scheduleTxtView insertSubview:placeHolderLbl atIndex:1];
    
    postImage=[[UIImageView alloc]init];
    postImage.frame=CGRectMake(0,0,SCREEN_WIDTH-120,120);
    postImage.alpha=.4;
    [scheduleTxtView insertSubview:postImage atIndex:0];
    
    UIButton * sendButton=[[UIButton alloc]init];
    sendButton.frame=CGRectMake(SCREEN_WIDTH-80,80,70, 40);
    [sendButton setBackgroundImage:[UIImage imageNamed:@"sendBtn.png"] forState:UIControlStateNormal];
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
    //On Action of Schedule Button Local Notificaton is set.
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
 
    }
    else
    {
        UIAlertView * alert=[[UIAlertView alloc]initWithTitle:@"Warning" message:@"Text cannot be empty" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [alert show];
    }
    
}
-(void) displayDatePicker:(id)sender{
    
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
    UILocalNotification * localNotification = [[UILocalNotification alloc] init];
    // current time plus 9000(2.5 hrs) secs
    localNotification.fireDate = self.datePicker.date;
    time_t unixTime = (time_t) [[NSDate date] timeIntervalSince1970];
    NSNumber * numbToStore=[NSNumber numberWithLong:unixTime];
    NSMutableDictionary * dictMessage=[[NSMutableDictionary alloc]init];
    [dictMessage setObject:[Singleton sharedSingleton].userTwitterScreenName forKey:@"UserScreenName"];
    [dictMessage setObject:textToSend forKey:@"Text"];
    [dictMessage setObject:numbToStore forKey:@"TimeStamp"];
    localNotification.userInfo=dictMessage;
    localNotification.alertBody =@"Time to Post";
    localNotification.soundName = UILocalNotificationDefaultSoundName;
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    TwitterHelperClass * twtSave=[[TwitterHelperClass alloc]init];
    [twtSave saveScheduleInSqlite:[Singleton sharedSingleton].accessTokenCurrent userImage:imageToUpload date:(int)unixTime];
    [scheduleTxtView resignFirstResponder];

    
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

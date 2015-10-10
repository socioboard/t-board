//
//  SchedulingView.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/27/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SchedulingViewControllerTboard : UIViewController<UITextViewDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate,UITableViewDataSource,UITableViewDelegate>
{
    UITextView * scheduleTxtView;
    UIImage * imageToUpload;
    UIView * composeView,*backView;
    UIImageView * postImage;
    UILabel * placeHolderLbl;
    NSString * textToSend;
    UIView * userListView;
    UITableView *userListTable;
    NSMutableArray * selectedData;
    NSArray * scheduleDataToDisplay;
    UILabel * dateLbl;
    UILabel * timeLbl;
    UILabel * lblUserNo;
}
@property (nonatomic, strong) UIDatePicker *datePicker;
@property (nonatomic,strong) UITableView * scheduleTable;
@property (nonatomic, strong) UIView *pickerView;
@end

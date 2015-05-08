//
//  SchedulingView.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/27/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SchedulingView : UIViewController<UITextViewDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate>
{
    UITextView * scheduleTxtView;
    UIImage * imageToUpload;
    UIView * composeView;
    UIImageView * postImage;
    UILabel * placeHolderLbl;
    NSString * textToSend;
}
@property (nonatomic, strong) UIDatePicker *datePicker;
@property (nonatomic, strong) UIView *pickerView;
@end

//
//  FollwViewController.h
//  TwitterBoard
//
//  Created by GLB-254 on 4/18/15.
//  Copyright (c) 2015 globussoft. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TwitterHelperClass.h"
#import "MBProgressHUD.h"
#import "TableCustomCell.h"
#import "TTTAttributedLabel.h"
@interface FeedsViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UITextViewDelegate,UIActionSheetDelegate>
{
    UITableView * followTableView;
    UITableView * tweetTableView;
    UITableView * mentionTableView;

    UITableView * userListTable;
//---
    UILabel * maxLength;
    UILabel * seclectedAccount;
//------
    UIView * userListView;
    UIView * backView;
    UIView * backViewTweet;
    UIView * bgTweetView;
    //---
    NSArray * wholeData;
    NSArray * wholeDataTweet;
    NSArray * wholeDataMention;

    //--
    int currentSelection;
    int currentSelectionForFavorite,favoriteCountInc;

    //--
    TwitterHelperClass * twittHelperObj;
    //--
    MBProgressHUD * HUD;
    //--
    BOOL refresh,refreshTweet,refreshMention;
    int countEndOfLine;
    TableCustomCell *cell;
    //--
    UIImage * imageToPost;
    UIImagePickerController * imagePicker;
    UIImageView * placeHolderImage;

    //--
    NSString * textToTweet,*tweetIdToPost;
    //---
    CGFloat dynamicHeightOfRow;
    //--
    NSMutableArray * setFavoriteBool,*setReTweetBool,*selectedData;
    NSMutableArray * setTweetsFavoriteBool,*setTweetsReTweetBool;
    NSMutableArray * setMentionFavoriteBool,*setMentionReTweetBool;

    //--
    UITextView * txtViewTweet;
    CGFloat yFrTxtView;
//------
    UIButton * crossButton;
}

@end

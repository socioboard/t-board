//
//  GraphView.h
//  GraphExample
//
//  Created by GBS-ios on 9/6/14.
//  Copyright (c) 2014 Globussoft. All rights reserved.
//

#define kGraphHeight 10//300
#define kDefaultGraphWidth 310//900
#define kOffsetX 60
#define kStepX 60
#define kGraphBottom SCREEN_HEIGHT-120//270
#define kGraphTop SCREEN_HEIGHT-150-40*5
#define kStepY 60
#define kOffsetY 60
#define kstartPoint 30
#define kCircleRadius 3

#import <CoreText/CoreText.h>
#import <UIKit/UIKit.h>

@interface GraphView : UIView

@property (nonatomic, strong) NSArray *coordinateX;
@property (nonatomic, strong) NSArray *coordinateY;


//--------------------------------------

@property (nonatomic, strong) NSArray *graphValueArray;
@property (nonatomic, strong) NSArray *lineColorArray;
@property (nonatomic, copy) UIColor *defaultColor;
@property (nonatomic,assign) float scale;
//==============
-(id) initWithFrame:(CGRect)frame andDefaultLineColor:(UIColor *)lineColor;
//==========


@end

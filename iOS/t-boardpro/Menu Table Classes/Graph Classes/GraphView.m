//
//  GraphView.m
//  GraphExample
//
//  Created by GBS-ios on 9/6/14.
//  Copyright (c) 2014 Globussoft. All rights reserved.
//

#import "GraphView.h"

@implementation GraphView
@synthesize defaultColor;


- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        // Initialization code
        self.defaultColor = [UIColor whiteColor];
        UILabel * titleLbl=[[UILabel alloc]initWithFrame:CGRectMake(0, 0, SCREEN_WIDTH, 40)];
        titleLbl.font=[UIFont systemFontOfSize:12];
        titleLbl.textAlignment=NSTextAlignmentCenter;
        titleLbl.text=@"FOLLOWERS ANALYSIS";
        titleLbl.textColor=[UIColor blackColor];
        [self addSubview:titleLbl];
        //--------------
        UILabel * xAxisLabel=[[UILabel alloc]initWithFrame:CGRectMake(0, kGraphBottom+20, SCREEN_WIDTH,16)];
        xAxisLabel.font=[UIFont systemFontOfSize:15];
        xAxisLabel.textAlignment=NSTextAlignmentCenter;
        xAxisLabel.text=@"——YEAR 2015——";
        [self addSubview:xAxisLabel];
        //--------------
        UILabel * yAxisLabel=[[UILabel alloc]initWithFrame:CGRectMake(-60,120, SCREEN_WIDTH-180, 20)];
        yAxisLabel.transform = CGAffineTransformMakeRotation(-(M_PI)/2);
        yAxisLabel.text=@"--Followers List--";
        [self addSubview:yAxisLabel];
        //---Following Follower Mutual Labels-----
        UILabel * followingLbl=[[UILabel alloc]init];
        followingLbl.frame=CGRectMake(0, SCREEN_HEIGHT-80, 115, 20);
        followingLbl.textColor=[self.lineColorArray objectAtIndex:0];
        followingLbl.textAlignment=NSTextAlignmentLeft;
        followingLbl.text=@"--Following--";
        [self addSubview:followingLbl];
        //---
        UILabel * followerLbl=[[UILabel alloc]init];
        followerLbl.frame=CGRectMake(115, SCREEN_HEIGHT-80, 120, 20);
        followerLbl.textAlignment=NSTextAlignmentLeft;
        followerLbl.textColor=[self.lineColorArray objectAtIndex:1];
        followerLbl.text=@"--Followers--";
        [self addSubview:followerLbl];
        //---
        
    }
    return self;
}

-(id) initWithFrame:(CGRect)frame andDefaultLineColor:(UIColor *)lineColor{
    
    self = [super initWithFrame:frame];
    if (self)
    {
        self.defaultColor = lineColor;
       
    }
    return self;
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    [self addGradient];
    // Drawing code
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetLineWidth(context, 1.0);
    CGContextSetStrokeColorWithColor(context, [[UIColor whiteColor] CGColor]);
    
    CGContextMoveToPoint(context, kstartPoint, 0);
    CGContextAddLineToPoint(context, kstartPoint, kGraphBottom);
    
    CGContextAddLineToPoint(context, self.frame.size.width, kGraphBottom);
    CGContextStrokePath(context);
    CGContextSaveGState(context);
    
    
    CGFloat dash[] = {2.0, 2.0};
    CGContextSetLineDash(context, 0.0, dash, 2);

    //======================================
    CGContextSetLineWidth(context, 0.6);
    CGContextSetStrokeColorWithColor(context, [[UIColor blackColor] CGColor]);
    //changed from gray
    // How many lines?
    int howMany = 7;//(kDefaultGraphWidth - kOffsetX) / kStepX;
    
    // Here the lines go
    for (int i = 0; i < howMany; i++)
    {
        CGContextMoveToPoint(context,30+ kOffsetX + i * kStepX, kGraphBottom);
        CGContextAddLineToPoint(context, 30+kOffsetX + i * kStepX, kGraphBottom+2);
    }
    
    
    //-------------------------
    int howManyHorizontal =4;//(kGraphBottom - kGraphTop - kOffsetY) / kStepY;
    for (int i = 0; i <= howManyHorizontal; i++)
    {
        CGContextMoveToPoint(context, 30, kGraphBottom - kOffsetY - i * kStepY);
        CGContextAddLineToPoint(context, kDefaultGraphWidth, kGraphBottom - kOffsetY - i * kStepY);
    }
    CGContextStrokePath(context);
    CGContextSaveGState(context);
    //========================================
   
    [self drawCoordinateXWithContext:context];
    [self drawCoordinateYWithContext:context];
    
    //==================
    
    for (int i =0; i < self.graphValueArray.count; i++) {
     
        NSArray *valueArray = [self.graphValueArray objectAtIndex:i];
        UIColor *color = self.defaultColor;
        
        if (self.lineColorArray.count>i) {
            color = [self.lineColorArray objectAtIndex:i];
        }
        
        [self drawLineGraphWithContext:context andValues:valueArray andColor:color];
        
    }
    
}
-(void)addGradient
{
    NSMutableArray *marrColors=[[NSMutableArray alloc]initWithObjects:(__bridge id)[UIColor colorWithRed:(CGFloat)247/255 green:(CGFloat)247/255 blue:(CGFloat)247/255 alpha:1].CGColor,(__bridge id)[UIColor colorWithRed:(CGFloat)215/255 green:(CGFloat)215/255 blue:(CGFloat)215/255 alpha:1].CGColor, nil];
    CGContextRef ref = UIGraphicsGetCurrentContext();
    CFArrayRef colors =(__bridge CFArrayRef)([NSArray arrayWithArray:marrColors]);
    CGColorSpaceRef colorSpc = CGColorSpaceCreateDeviceRGB();
    CGGradientRef gradient = CGGradientCreateWithColors(colorSpc, colors, Nil);
    CGContextDrawLinearGradient(ref, gradient , CGPointMake(0.0, 0.0), CGPointMake(0,self.frame.size.height), kCGGradientDrawsAfterEndLocation);

}
#pragma mark -
//Set X Coodinate

-(void) drawCoordinateXWithContext:(CGContextRef)context{
    
    CGContextSetTextMatrix(context, CGAffineTransformMake(1.0, 0.0, 0.0, -1.0, 0.0, 0.0));
    for (int i =0; i<self.coordinateX.count; i++) {
        int a = i+1;
        float x =  a*kStepX+28;
        CGMutablePathRef path = CGPathCreateMutable();
//        CGRect textFrame = CGRectMake(x, kGraphBottom + 13, 30, 20);
        CGRect textFrame = CGRectMake(x, kGraphBottom+10,60, 20);
        CGPathAddRect(path, NULL, textFrame);
        NSAttributedString* attString = [[NSAttributedString alloc]initWithString:[NSString stringWithFormat:@"%@",[self.coordinateX objectAtIndex:i]]];
        CTFramesetterRef frameSetter = CTFramesetterCreateWithAttributedString((CFAttributedStringRef) attString);
        CTFrameRef frame = CTFramesetterCreateFrame(frameSetter, CFRangeMake(0, [attString length]), path, NULL);
        CTFrameDraw(frame, context);
        CFRelease(frame);
        CFRelease(frameSetter);
        CFRelease(path);
    }
}

//Set YCordinate
-(void) drawCoordinateYWithContext:(CGContextRef)context{
    
    CGContextSetTextMatrix(context, CGAffineTransformMake(1.0, 0.0, 0.0, -1.0, 0.0, 0.0));
    UIFont *font = [UIFont systemFontOfSize:10];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:font,NSFontAttributeName, nil];
    
    for (int i =0; i<self.coordinateY.count; i++) {
        
        int a = i+1;
        float y = kGraphBottom - a*kStepY;
        
        CGMutablePathRef path = CGPathCreateMutable();
        
//        CGRect textFrame = CGRectMake(5, y + 13, 25, 20);
        CGRect textFrame = CGRectMake(kstartPoint, y-18, 25, 20);
        CGPathAddRect(path, NULL, textFrame);
        NSAttributedString* attString = [[NSAttributedString alloc]initWithString:[NSString stringWithFormat:@"%@",[self.coordinateY objectAtIndex:i]]attributes:dict];
        CTFramesetterRef frameSetter = CTFramesetterCreateWithAttributedString((CFAttributedStringRef) attString);
        CTFrameRef frame = CTFramesetterCreateFrame(frameSetter, CFRangeMake(0, [attString length]), path, NULL);
        CTFrameDraw(frame, context);
        CFRelease(frame);
        CFRelease(frameSetter);
        CFRelease(path);
    }
}



#pragma mark -
-(void) drawLineGraphWithContext:(CGContextRef)context andValues:(NSArray *)valueArray andColor:(UIColor *)color{
    
    CGContextSetLineWidth(context, 1.0);
    
    CGContextSetStrokeColorWithColor(context, [color CGColor]);
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextSetLineDash(context, 0.0, nil, 0);
    
    CGContextBeginPath(context);
    CGContextMoveToPoint(context, kstartPoint, kGraphBottom);
    
    for (int i = 0; i<valueArray.count; i++) {
        
        //CGContextAddLineToPoint(context, i*kStepX+kstartPoint, kGraphBottom-i*kStepY);
        NSString *str = [NSString stringWithFormat:@"%@",[valueArray objectAtIndex:i]];
        int a = i + 1;
        CGFloat n = [str floatValue]*self.scale;
        
        CGContextAddLineToPoint(context, a*kStepX+kstartPoint, kGraphBottom-n);
        
    }
    CGContextDrawPath(context, kCGPathStroke);
    
    
    //----Add Circle
    for (int i=0; i<valueArray.count; i++) {
        
        int a = i+1;
        float x = a*kStepX+kstartPoint;
        NSString *str = [NSString stringWithFormat:@"%@",[valueArray objectAtIndex:i]];
        CGFloat n = [str floatValue]*self.scale;
        float y = kGraphBottom-n;
        //Draw value Text
        [self displayValueLabel:x ycordinate:y text:str context:context color:color];
        
        CGRect rect = CGRectMake(x - kCircleRadius, y - kCircleRadius, 2 * kCircleRadius, 2 * kCircleRadius);
        CGContextAddEllipseInRect(context, rect);
    }
    CGContextDrawPath(context, kCGPathFillStroke);

}
-(void)displayValueLabel:(int)xCordinate ycordinate:(int)yCordinate text:(NSString*)textToDisplay context:(CGContextRef)context color:(UIColor*)color
{
    
    CGContextSetTextMatrix(context, CGAffineTransformMake(1.0, 0.0, 0.0, -1.0, 0.0, 0.0));
     CGContextSetStrokeColorWithColor(context, [color CGColor]);
    UIFont *font = [UIFont systemFontOfSize:10];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:font,NSFontAttributeName,color,NSForegroundColorAttributeName, nil];
    
        CGMutablePathRef path = CGPathCreateMutable();
        
        //        CGRect textFrame = CGRectMake(5, y + 13, 25, 20);
        CGRect textFrame = CGRectMake(xCordinate,yCordinate, 25, 20);
        CGPathAddRect(path, NULL, textFrame);
        NSAttributedString* attString = [[NSAttributedString alloc]initWithString:textToDisplay attributes:dict];
        CTFramesetterRef frameSetter = CTFramesetterCreateWithAttributedString((CFAttributedStringRef) attString);
        CTFrameRef frame = CTFramesetterCreateFrame(frameSetter, CFRangeMake(0, [attString length]), path, NULL);
        CTFrameDraw(frame, context);
        CFRelease(frame);
        CFRelease(frameSetter);
        CFRelease(path);
 
   
}
@end

![](http://i.imgur.com/0wivQLV.png)
**t-board** is a multiple twitter account management app, it helps you to login to multiple twitter accounts from your iOS or Android device and do various twitter activities like tweeting, re-tweeting, favorites, follow, unfollow, scheduling tweets etc. Its a a very powerful twitter marketing automation app.

Features:
===========

> ** Multi accounts manager:**Multi accounts manager: You can manage your multiple twitter accounts and easily switch between accounts without logging in again and again . ![](http://i.imgur.com/9zWtCz8.png)

>**All features:** This is the compact list of features available in application.![](http://i.imgur.com/TaGlRFB.jpg) 

>**Feeds view:** You can check all your Feeds here.![](http://i.imgur.com/zwxqlod.png)
![](http://i.imgur.com/kYRU2j0.png) 

>**All Following:** You can see list of user whom you are following in this view.![](http://i.imgur.com/hOaHz0V.png)

>**Tweet to Follower:** You Can tweet to a User you are following.![](http://i.imgur.com/RTbiYVG.png)

>**Copy Follower:**Search any user by typing username and set rang to follow that users folowers. ![](http://i.imgur.com/koGsuyF.png)

>**Tweet schedule:** You can schedule your message to post on future date.![](http://i.imgur.com/wDhGK5C.png)
 ![](http://i.imgur.com/JQUikXW.png) 

>**Search User/Trends:** You search perticular user OR top trending topics here.![](http://i.imgur.com/LqScOR7.png)
![](http://i.imgur.com/6kEXfw1.png)

>**Overlaping Users:** Trace all overlapping followers and followings with another user here.![](http://i.imgur.com/9QQ6gwY.png)
 

 Installation Guide Android:
===========

The easiest way to build is to install Android IDE, Once installed, then you can import the project into Android Studio:


1.	Open File
	
2.	Import Project.

	![](http://i.imgur.com/d1NkOaE.png) 
	![](http://i.imgur.com/MfBTyow.png) 
	
3.	Select tboardpro , android support library, Graph library and viewpager library.

4.	Add project libraries into tboardpro project.

	![](http://i.imgur.com/zBOZbI2.png)
	
5.	Clean and build all projects after importing.
	
6. Now for configuring twitter API keys you have procced to **src package**  -->
   **com.socioboard.t_board_pro ** -->
   **SplashActivity** class

7. insert your API key credentials to method **initializeTwitterkeys("xxkeyxxx", "xxxsecretx");** inside @onCreate() method.
   ![](http://i.imgur.com/pDf37U4.png)	

After building and configuring the project while running on your device ,you might find that your device doesn't let you install your build if you already have the version from Google Play installed. This is standard Android security as it won't let you directly replace an app that's been signed with a different key. Manually uninstall tboardpro from your device and you will then be able to install your own built version.

**Step by step Video instruction link**  ( https://youtu.be/ZMw6CK8JHkM )

**NOTE :: it is highly recommended that you shoud use android Android SDK 5.0.0 or above in eclipse.**


 Installation Guide IOS:
===========
1. Extract the downloaded t-boardpro.zip file.

2. Open the t-boardpro folder,you will find t-boardpro.xcodeProject file and double click on that folder to open Xcode.
 ![](http://i.imgur.com/KvoNsL4.png)
3. Click on project file from Project navigator and click on the t-boardpro under the Targets.
4. Click on General , set the proper Bundle Identifier and go to Build settings menu, change Proovisioning profile under Code Signing.
 ![](http://i.imgur.com/nf2pBHd.png)
5. Go to Prefix File and set the client_id and client_secrete id . 
6 Build and run the application.￼
 ![](http://i.imgur.com/eIyaWTj.png)

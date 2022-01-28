[![](https://jitpack.io/v/jampez77/UCE-Handler.svg)](https://jitpack.io/#jampez77/UCE-Handler) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![GitHub stars](https://img.shields.io/github/stars/jampez77/UCE-Handler.svg?style=social&label=Star)](https://GitHub.com/jampez77/UCE-Handler/stargazers) 

<!-- [![](https://jitpack.io/v/jampez77/UCE-Handler/month.svg)](https://jitpack.io/#jampez77/UCE-Handler) [![](https://jitpack.io/v/jampez77/UCE-Handler/week.svg)](https://jitpack.io/#jampez77/UCE-Handler) -->

# My Contributions
### This is a fork of the brilliant [UCE Handler](https://github.com/RohitSurwase/UCE-Handler) by [Rohit Sahebrao Surwase](https://github.com/RohitSurwase). 

# UCE Handler

[Play Store Demo Here](https://play.google.com/store/apps/details?id=com.jampez.uce_handler) (All crashes from the example app can be seen [here](https://github.com/jampez77/UCE-Handler/issues))
![Example Animation](https://github.com/jampez77/UCE-Handler/raw/master/art/uce_feature.png) 

### Android library which lets you take control of Android App's uncaught exceptions. View, Copy or Share exceptions details including other useful info easily.
Tracking down all exceptions is the crucial part of the development. We could just expect that we have handled all exceptions. But whatever we do, we come across it with the so-called pop-up saying “Unfortunately, App has stopped”, that is why it is called uncaught-exceptions.

### Generate support tickets for your project in GitHub, GitLab or BitBucket (or all 3 of them!!)
Fully integrate with your source control and get crashes reported directly to the issues board or a GitHub, GitLab or BitBucket project. The shown the issue number, linking them to the specific crash. So no more searching through crash logs trying to locate a user specific issue.

[See Example Crash Log](https://github.com/jampez77/UCE-Handler/issues/16)

![GitHub Logo](https://github.com/jampez77/UCE-Handler/raw/master/art/github_icon.png)  
Seamlessly integrate with your GitHub project with the `setGithubService` method. All you need is your `Username`, `Repository Name` and a [Personal Access Token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token).

![BitBucket Logo](https://github.com/jampez77/UCE-Handler/raw/master/art/bitbucket_icon.png)  
Integrating with a BitBucket project is just as easy. When using the `setBitBucketService` method you will need to include your `Username`, `Project Name`, `Repository Name` and an [App Password](https://support.atlassian.com/bitbucket-cloud/docs/app-passwords/)

![GitLab Logo](https://github.com/jampez77/UCE-Handler/raw/master/art/gitlab_icon.png) 
It's even easier to integrate with a GitLab project, this can be done by using the `setGitLabService` method and including your `Project ID` & [Access Token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html)

### Highly Customisable 
Change the colour scheme, text, and icons to suit you and make UCE-Handler look like any other part of your app.
![Customisable Options](https://github.com/jampez77/UCE-Handler/raw/master/art/customise-options.png) 


![Example Animation](https://github.com/jampez77/UCE-Handler/raw/master/art/uce_example.gif)


## Features
* Android App lifecycle aware.
* Catches all uncaught exceptions gracefully.
* Displays separate screen with multiple options whenever an App crashes.
* View, Copy & Share crash logs easily.
* Easily send crash logs directly to a GitHub, GitLab or BitBucket project!
* Completely close the crashed/unstable Application.

## Logged Information
* Device/mobile info.
* Application info.
* Crash log.
* Activity track. //optional
* All log files are placed in a separate folder.

### Each Log file is named upon App's name so you can identify and distinguish files easily if you have added this library in multiple projects/applications.

## Getting Started
Add this library to your Android project and initialize it in your Application class.

# Setup
In your Project's build.gradle file:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

In your Application's or Module's build.gradle file:

	dependencies {
	        api 'com.github.jampez77:UCE-Handler:2.0.1'
	}

In your Application class:
* Initialize library using builder pattern.
    
		class MyApplication : Application() {
            override fun onCreate() {
                super.onCreate()
                //Initialize UCE Handler library
                val uceHandlerBuilder = UCEHandler.Builder(applicationContext)
                uceHandlerBuilder.setTrackActivitiesEnabled(true)
                uceHandlerBuilder.setIconDrawable(R.mipmap.ic_launcher)
                uceHandlerBuilder.setBackgroundModeEnabled(true)
                uceHandlerBuilder.setIssueMode(Mode.Manual)
                uceHandlerBuilder.setGithubService(Github.Builder()
                       .setAccessToken("<< github access token >>")
                       .setRepoName("<< repo name >>")
                       .setUsername("<< username >>")
                )
                uceHandlerBuilder.setBitBucketService(BitBucket.Builder()
                       .setUsername("<< username >>")
                       .setAppPassword("<< app password >>")
                       .setProjectName("<< project name >>")
                       .setRepoName("<< repo name >>")
                )
                uceHandlerBuilder.setGitLabService(GitLab.Builder()
                       .setAccessToken("<< access token >>")
                       .setProjectID("<< project id >>")
                )
                uceHandlerBuilder.build()
            }
        }

##### Kotlin way of initialization

        UCEHandler.Builder(applicationContext).build()
	
##### For those of you who are still using Eclipse + ADT, you need to add UCEDefaultActivity manually in your App's manifest. (As suggested by [Caceresenzo](https://github.com/RohitSurwase/UCE-Handler/issues/2#issuecomment-385262850))

	<application>
	    ...
	    <activity
		android:name="com.jampez.uceh.UCEDefaultActivity"
		android:process=":error_activity"/>
	</application>

### Optional Parameters
##### .setUCEHEnabled(true/false)
//  default 'true'
 =>  Enable/Disable UCE_Handler.
##### .setTrackActivitiesEnabled(true/false)
//  default 'false'
 =>  Choose whether you want to track the flow of activities the user/tester has taken or not.
##### .setBackgroundModeEnabled(true/false)
//  default 'true'
 =>  Choose if you want to catch exceptions while app is in background.
#### .setCanViewErrorLog(true/false)
// default 'true'
=> Choose if you would like 'View Error Log' button to be shown.
#### .setCanShareErrorLog(true/false)
// default 'true'
=> Choose if you would like 'Share Error Log' button to be shown.
#### .setIconDrawable(int drawable)
// default null
=> Choose if you want an icon to be shown below the top text view.
#### .setBackgroundColour(int color)
// default white
=> Choose if you want to change the main background colour.
#### .setBackgroundTextColour(int color)
// default black
=> Choose if you want to change the main background text colour.
#### .setButtonColour(int color)
// default black
=> Choose if you want to change the buttons background colour.
#### .setButtonTextColour(int color)
// default white
=> Choose if you want to change the buttons text colour.
#### .setErrorLogMessage(string)
// default "Help developers by providing error details. Thank you for your support."
=> Set the text shown in the upper TextView
### .setIssueMode(Mode)
// default manual
=> Choose is support tickets should be set manually or automatically
### .setIssueButtonText(string)
// default "Create a Support Ticket"
=> define text shown in support ticket button (only shows in manual mode)
### .setGithubService(Github.Builder)
// default null
=> defines a GitHub integration with `Access Token`, `Repo Name` & `Username`
### .setBitBucketService(BitBucket.Builder)
// default null
=> defines a BitBucket integration with `Username`, `App Password`, `Project Name` & `Repo Name`
### .setGitLabService(GitLab.Builder)
// default null
=> defines a GitLab integration with `Access Token` & `Project ID`

#### 'Save Error Log' will work only if your app already has storage permission as library does not ask for it.

## Authors & Contributers
* [**Jamie Pezone**](https://github.com/jampez77) - *Github / Bitbucket / GitLab Integration*
* [**Rohit Surwase**](https://github.com/RohitSurwase) - *Initial work* 

## License
Copyright © 2018 Rohit Sahebrao Surwase.
This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details

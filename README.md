[![](https://jitpack.io/v/jampez77/UCE-Handler.svg)](https://jitpack.io/#jampez77/UCE-Handler) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![GitHub stars](https://img.shields.io/github/stars/jampez77/UCE-Handler.svg?style=social&label=Star)](https://GitHub.com/jampez77/UCE-Handler/stargazers) 

<!-- [![](https://jitpack.io/v/jampez77/UCE-Handler/month.svg)](https://jitpack.io/#jampez77/UCE-Handler) [![](https://jitpack.io/v/jampez77/UCE-Handler/week.svg)](https://jitpack.io/#jampez77/UCE-Handler) -->

# My Contributions
### This is a fork of the brilliant [UCE Handler](https://github.com/RohitSurwase/UCE-Handler) by [Rohit Sahebrao Surwase](https://github.com/RohitSurwase). This is not all that different from his repo in all honesty. I have just added a few customisable options.

# UCE Handler

[Play Store Demo Here](https://play.google.com/store/apps/details?id=com.jampez.uce_handler)

### Android library which lets you take control of Android App's uncaught exceptions. View, Copy, Share, Save and Email exceptions details including other useful info easily.
Tracking down all exceptions is the crucial part of the development. We could just expect that we have handled all exceptions. But whatever we do, we come across it with the so-called pop-up saying “Unfortunately, App has stopped”, that is why it is called uncaught-exceptions.

Why should you use this library? Read the answer - [Handling Uncaught-Exceptions in Android](https://android.jlelse.eu/handling-uncaught-exceptions-in-android-d818ffb20181)

![Example Animation](https://github.com/jampez77/UCE-Handler/raw/master/art/uce_feature.png)         ![Example Animation](https://github.com/jampez77/UCE-Handler/raw/master/art/uce_handler_example.gif)


## Features
* Android App lifecycle aware.
* Catches all uncaught exceptions gracefully.
* Displays separate screen with multiple options whenever an App crashes.
* View, Copy, Share, and Save crash logs easily.
* Email crash log along with the .txt file with multiple developers/receipients.
* Completely close the crashed/unstable Application.

## Logged Information
* Device/mobile info.
* Application info.
* Crash log.
* Activity track. //optional
* All log files are placed in a separate folder.

### Each Log file is named upon App's name so you can identify and distinguish files easily if you have added this library in multiple projects/applications.


## Example
Download the example app [here](https://github.com/jampez77/UCE-Handler/raw/master/UCE_Handler_Example.apk)

## Getting Started
Add this library to your Android project and initialize it in your Application class. Additionaly you can add developer's email addresses who will get the email of crash log along with the .txt file attached.

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
	        api 'com.github.jampez77:UCE-Handler:1.4.6'
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
#### .setCanCopyErrorLog(true/false)
// default 'true'
=> Choose if you would like 'Copy Error Log' button to be shown.
#### .setCanShareErrorLog(true/false)
// default 'true'
=> Choose if you would like 'Share Error Log' button to be shown.
#### .setCanSaveErrorLog(true/false)
// default 'true'
=> Choose if you would like 'Save Error Log' button to be shown.
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
#### .setShowAsDialog(true/false)
// default false
=> Choose if you want the layout to be shown in a dialog or a fullscreen.
#### .setShowTitle(true/false)
// default true
=> Choose if you want the main title to be shown.
#### .setErrorLogMessage(string)
// default "Help developers by providing error details. Thank you for your support."
=> Set the text shown in the upper TextView
##### .addCommaSeparatedEmailAddresses("abc@gmail.com, pqr@gmail.com,...)
// default - empty
 =>  Add comma separated email addresses who will receive the crash logs. 'Email Error Log' button will be hidden if this is not called.

#### 'Save Error Log' will work only if your app already has storage permission as library does not ask for it.

## Authors & Contributers
* [**Jamie Pezone**](https://github.com/jampez77)
* [**Rohit Surwase**](https://github.com/RohitSurwase) - *Initial work* - [API-Calling-Flow](https://github.com/RohitSurwase/API-Calling-Flow) , [AndroidDesignPatterns](https://github.com/RohitSurwase/AndroidDesignPatterns) , [News App Using Kotlin, MVP](https://github.com/RohitSurwase/News-Kotlin-MVP) ,  [Linkaive - Android App on Play Store](https://play.google.com/store/apps/details?id=com.rohitss.saveme)

## License
Copyright © 2018 Rohit Sahebrao Surwase.
This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details

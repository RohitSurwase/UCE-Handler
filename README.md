[![](https://jitpack.io/v/RohitSurwase/UCE-Handler.svg)](https://jitpack.io/#RohitSurwase/UCE-Handler) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active) [![GitHub stars](https://img.shields.io/github/stars/RohitSurwase/UCE-Handler.svg?style=social&label=Star)](https://GitHub.com/RohitSurwase/UCE-Handler/stargazers)

# UCE Handler
### Android library which lets you View, Copy, Share, Save and Email Application's Crash Logs easily.

<img src="https://github.com/RohitSurwase/UCE_Handler/raw/master/art/feature_screen.png" alt="Library Feature Screen"   width="200" height="350" title="Library Feature Screen" />

## Getting Started
It is so easy. Just add library this to your Android project and initialize in in your Application class. You can add multiple developers' email addresses who will get the email of crash log along with the .txt file..

## Features
* Andoid App lifecycle aware.
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

### Log files are named upon App's name so you can identify and distinguish files easily if you have added this library in multiple projects/applications.

## Setup
In your Project's build.gradle file:

	allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
	}

In your Application's or Module's build.gradle file:


	dependencies {
        compile 'com.github.RohitSurwase.UCE-Handler:uce_handler:1.0'
	}

In your Application class:
* Initialize library using builder pattern.
* Add comma separated email addresses who will receive crash logs. //optional
    
		public class MyApplication extends Application {
		@Override public void onCreate() { super.onCreate();
			// Other Stuff
			new UCEHandler.Builder(this)
				.addCommaSeparatedEmailAddresses("abc@gmail.com, pqr@gmail.com,...)
				.build();
		} }

# Optional Parameters
### .setUCEHEnabled(true/false)
//  default 'true'
Enable/disable UCE_Handler.
### .setTrackActivitiesEnabled(true/false)
//  default 'false'
Choose whether you want to track the flow of activities the user/tester has taken or not.
### .setBackgroundModeEnabled(true/false)
//  default 'true'
Choose if you want to catch exceptions while app is in background.

### 'Save Error Log' will work only if your app already has storage permission as library does not ask for it.

## Authors & Contributers

* [**Rohit Surwase**](https://github.com/RohitSurwase) - *Initial work* - [API-Calling-Flow](https://github.com/RohitSurwase/API-Calling-Flow) , [AndroidDesignPatterns](https://github.com/RohitSurwase/AndroidDesignPatterns) , [News App Using Kotlin, MVP](https://github.com/RohitSurwase/News-Kotlin-MVP) ,  [Linkaive - Android App on Play Store](https://play.google.com/store/apps/details?id=com.rohitss.saveme)

## License
Copyright © 2018 Rohit Sahebrao Surwase.
This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](LICENSE.md) file for details
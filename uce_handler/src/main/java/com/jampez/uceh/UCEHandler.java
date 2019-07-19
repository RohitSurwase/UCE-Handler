/*
 *
 *  * Copyright © 2018 Rohit Sahebrao Surwase.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.jampez.uceh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

/**
 * <b></b>
 * <p>This class is used to </p>
 * Created by Rohit.
 */
public final class UCEHandler {
    static final String EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE";
    static final String EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG";
    private final static String TAG = "UCEHandler";
    private static final String UCE_HANDLER_PACKAGE_NAME = "com.jampez.uceh";
    private static final String DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os";
    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1
    private static final int MAX_ACTIVITIES_IN_LOG = 50;
    private static final String SHARED_PREFERENCES_FILE = "uceh_preferences";
    private static final String SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp";
    private static final String ASK_FOR_ERROR_LOG = "Help developers by providing error details.\nThank you for your support.";
    private static final String COPYRIGHT_INFO = "UCE Handler courtesy of\nCopyright © 2018 Rohit Sahebrao Surwase.";
    private static final Deque<String> activityLog = new ArrayDeque<>(MAX_ACTIVITIES_IN_LOG);
    static String COMMA_SEPARATED_EMAIL_ADDRESSES;
    @SuppressLint("StaticFieldLeak")
    private static boolean isInBackground = true;
    private static boolean isBackgroundMode;
    private static boolean isUCEHEnabled;
    private static boolean isTrackActivitiesEnabled;
    private static boolean isDialog;
    private static boolean canViewErrorLog;
    private static boolean canCopyErrorLog;
    private static boolean canShareErrorLog;
    private static boolean canSaveErrorLog;
    private static boolean showTitle;
    private static int backgroundDrawable;
    private static int backgroundColour;
    private static int backgroundTextColour;
    private static int buttonColour;
    private static int buttonTextColour;
    private static String errorLogMessage;
    private static String copyrightInfo;
    private static WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);

    private UCEHandler(Builder builder) {
        isUCEHEnabled = builder.isUCEHEnabled;
        isTrackActivitiesEnabled = builder.isTrackActivitiesEnabled;
        isBackgroundMode = builder.isBackgroundModeEnabled;
        backgroundDrawable = builder.backgroundDrawable;
        backgroundColour = builder.backgroundColour;
        backgroundTextColour = builder.backgroundTextColour;
        buttonColour = builder.buttonColour;
        buttonTextColour = builder.buttonTextColour;
        isDialog = builder.isDialog;
        canViewErrorLog = builder.canViewErrorLog;
        canCopyErrorLog = builder.canCopyErrorLog;
        canShareErrorLog = builder.canShareErrorLog;
        canSaveErrorLog = builder.canSaveErrorLog;
        showTitle = builder.showTitle;
        errorLogMessage = builder.errorLogMessage;
        copyrightInfo = builder.copyrightInfo;
        COMMA_SEPARATED_EMAIL_ADDRESSES = builder.commaSeparatedEmailAddresses;
        setUCEHandler(builder.context);
    }

    private static void setUCEHandler(final Context context) {
        try {
            if (context != null) {
                final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (oldHandler != null && oldHandler.getClass().getName().startsWith(UCE_HANDLER_PACKAGE_NAME)) {
                    Log.e(TAG, "UCEHandler was already installed, doing nothing!");
                } else {
                    if (oldHandler != null && !oldHandler.getClass().getName().startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                        Log.e(TAG, "You already have an UncaughtExceptionHandler. If you use a custom UncaughtExceptionHandler, it should be initialized after UCEHandler! Installing anyway, but your original handler will not be called.");
                    }
                    final Application application = (Application) context.getApplicationContext();
                    //Setup UCE Handler.
                    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(@NonNull Thread thread, final @NonNull Throwable throwable) {
                            if (isUCEHEnabled) {
                                Log.e(TAG, "App crashed, executing UCEHandler's UncaughtExceptionHandler", throwable);
                                if (hasCrashedInTheLastSeconds(application)) {
                                    Log.e(TAG, "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?", throwable);
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable);
                                        return;
                                    }
                                } else {
                                    setLastCrashTimestamp(application, new Date().getTime());
                                    if (!isInBackground || isBackgroundMode) {
                                        final Intent intent = new Intent(application, UCEDefaultActivity.class);
                                        StringWriter sw = new StringWriter();
                                        PrintWriter pw = new PrintWriter(sw);
                                        throwable.printStackTrace(pw);
                                        String stackTraceString = sw.toString();
                                        if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
                                            String disclaimer = " [stack trace too large]";
                                            stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
                                        }
                                        intent.putExtra(EXTRA_STACK_TRACE, stackTraceString);
                                        if (isTrackActivitiesEnabled) {
                                            StringBuilder activityLogStringBuilder = new StringBuilder();
                                            while (!activityLog.isEmpty()) {
                                                activityLogStringBuilder.append(activityLog.poll());
                                            }
                                            intent.putExtra(EXTRA_ACTIVITY_LOG, activityLogStringBuilder.toString());
                                        }
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        application.startActivity(intent);
                                    } else {
                                        if (oldHandler != null) {
                                            oldHandler.uncaughtException(thread, throwable);
                                            return;
                                        }
                                        //If it is null (should not be), we let it continue and kill the process or it will be stuck
                                    }
                                }
                                final Activity lastActivity = lastActivityCreated.get();
                                if (lastActivity != null) {
                                    lastActivity.finish();
                                    lastActivityCreated.clear();
                                }
                                killCurrentProcess();
                            } else if (oldHandler != null) {
                                //Pass control to old uncaught exception handler
                                oldHandler.uncaughtException(thread, throwable);
                            }
                        }
                    });
                    application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        int currentlyStartedActivities = 0;

                        @Override
                        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                            if (activity.getClass() != UCEDefaultActivity.class) {
                                lastActivityCreated = new WeakReference<>(activity);
                            }
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
                            }
                        }

                        @Override
                        public void onActivityStarted(@NonNull Activity activity) {
                            currentlyStartedActivities++;
                            isInBackground = (currentlyStartedActivities == 0);
                        }

                        @Override
                        public void onActivityResumed(@NonNull Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
                            }
                        }

                        @Override
                        public void onActivityPaused(@NonNull Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");
                            }
                        }

                        @Override
                        public void onActivityStopped(@NonNull Activity activity) {
                            currentlyStartedActivities--;
                            isInBackground = (currentlyStartedActivities == 0);
                        }

                        @Override
                        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                        }

                        @Override
                        public void onActivityDestroyed(@NonNull Activity activity) {
                            if (isTrackActivitiesEnabled) {
                                activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
                            }
                        }
                    });
                }
                Log.i(TAG, "UCEHandler has been installed.");
            } else {
                Log.e(TAG, "Context can not be null");
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "UCEHandler can not be initialized. Help making it better by reporting this as a bug.", throwable);
        }
    }

    /**
     * INTERNAL method that tells if the app has crashed in the last seconds.
     * This is used to avoid restart loops.
     *
     * @return true if the app has crashed in the last seconds, false otherwise.
     */
    private static boolean hasCrashedInTheLastSeconds(Context context) {
        long lastTimestamp = getLastCrashTimestamp(context);
        long currentTimestamp = new Date().getTime();
        return (lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 3000);
    }

    @SuppressLint("ApplySharedPref")
    private static void setLastCrashTimestamp(Context context, long timestamp) {
        context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit();
    }

    private static void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private static long getLastCrashTimestamp(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1);
    }

    static void closeApplication(Activity activity) {
        activity.finish();
        killCurrentProcess();
    }

    static int getBackgroundDrawable(){
        return backgroundDrawable;
    }

    static int getBackgroundColour(){
        return backgroundColour;
    }

    static int getBackgroundTextColour(){
        return backgroundTextColour;
    }

    static int getButtonColour(){
        return buttonColour;
    }

    static int getButtonTextColour(){
        return buttonTextColour;
    }

    static boolean getShowAsDialog(){
        return isDialog;
    }

    static boolean getShowTitle(){
        return showTitle;
    }

    static boolean getCanViewErrorLog(){
        return canViewErrorLog;
    }

    static boolean getCanCopyErrorLog(){
        return canCopyErrorLog;
    }

    static boolean getCanShareErrorLog(){
        return canShareErrorLog;
    }

    static boolean getCanSaveErrorLog(){
        return canSaveErrorLog;
    }

    static String getErrorLogMessage(){
        return errorLogMessage;
    }

    static String getCopyrightInfo(){
        return copyrightInfo;
    }

    static String getCommaSeparatedEmailAddresses(){
        return COMMA_SEPARATED_EMAIL_ADDRESSES;
    }

    @SuppressWarnings("deprecation")
    static int getColourFromInt(Context context, int id) {
        try {
            final int version = Build.VERSION.SDK_INT;
            if (version >= 23)
                return ContextCompat.getColor(context, id);
            else
                return context.getResources().getColor(id);
        }catch (Exception e){
            return R.color.white;
        }
    }

    @SuppressWarnings("deprecation")
    static Drawable getDrawableFromInt(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        try {
            if (version >= 21)
                return ContextCompat.getDrawable(context, id);
            else
                return context.getResources().getDrawable(id);
        }catch (Exception e){
            return null;
        }
    }

    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder {
        private Context context;
        private boolean isUCEHEnabled = true;
        private String commaSeparatedEmailAddresses;
        private boolean isTrackActivitiesEnabled = false;
        private boolean isBackgroundModeEnabled = true;
        private boolean isDialog = false;
        private boolean canViewErrorLog = true;
        private boolean canCopyErrorLog = true;
        private boolean canShareErrorLog = true;
        private boolean canSaveErrorLog = true;
        private boolean showTitle = true;
        private int backgroundDrawable;
        private int backgroundColour = R.color.white;
        private int backgroundTextColour = R.color.black;
        private int buttonColour = R.color.black;
        private int buttonTextColour = R.color.white;
        private String errorLogMessage = ASK_FOR_ERROR_LOG;
        private String copyrightInfo = COPYRIGHT_INFO;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUCEHEnabled(boolean isUCEHEnabled) {
            this.isUCEHEnabled = isUCEHEnabled;
            return this;
        }

        public Builder setTrackActivitiesEnabled(boolean isTrackActivitiesEnabled) {
            this.isTrackActivitiesEnabled = isTrackActivitiesEnabled;
            return this;
        }

        public Builder setBackgroundModeEnabled(boolean isBackgroundModeEnabled) {
            this.isBackgroundModeEnabled = isBackgroundModeEnabled;
            return this;
        }

        public Builder setCanViewErrorLog(boolean canViewErrorLog){
            this.canViewErrorLog = canViewErrorLog;
            return this;
        }

        public Builder setCanCopyErrorLog(boolean canCopyErrorLog){
            this.canCopyErrorLog = canCopyErrorLog;
            return this;
        }

        public Builder setCanShareErrorLog(boolean canShareErrorLog){
            this.canShareErrorLog = canShareErrorLog;
            return this;
        }

        public Builder setCanSaveErrorLog(boolean canSaveErrorLog){
            this.canSaveErrorLog = canSaveErrorLog;
            return this;
        }

        public Builder setBackgroundDrawable(int backgroundDrawable){
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        public Builder setBackgroundColour(int backgroundColour){
            this.backgroundColour = backgroundColour;
            return this;
        }

        public Builder setBackgroundTextColour(int backgroundTextColour){
            this.backgroundTextColour = backgroundTextColour;
            return this;
        }

        public Builder setButtonColour(int buttonColour){
            this.buttonColour = buttonColour;
            return this;
        }

        public Builder setButtonTextColour(int buttonTextColour){
            this.buttonTextColour = buttonTextColour;
            return this;
        }

        public Builder setShowAsDialog(boolean isDialog){
            this.isDialog = isDialog;
            return this;
        }

        public Builder setShowTitle(boolean showTitle){
            this.showTitle = showTitle;
            return this;
        }

        public Builder setErrorLogMessage(String errorLogMessage){
            this.errorLogMessage = errorLogMessage;
            return this;
        }

        public Builder addCommaSeparatedEmailAddresses(String commaSeparatedEmailAddresses) {
            this.commaSeparatedEmailAddresses = (commaSeparatedEmailAddresses != null) ? commaSeparatedEmailAddresses : "";
            return this;
        }

        public UCEHandler build() {
            return new UCEHandler(this);
        }
    }
}
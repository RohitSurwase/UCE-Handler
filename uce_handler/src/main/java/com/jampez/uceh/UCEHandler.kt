package com.jampez.uceh

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@Suppress("DEPRECATION", "unused")
class UCEHandler private constructor(builder: Builder) {
    class Builder(internal val context: Context) {
        internal var isUCEHEnabled = true
        internal var commaSeparatedEmailAddresses: String? = null
        internal var isTrackActivitiesEnabled = false
        internal var isBackgroundModeEnabled = true
        internal var isDialog = false
        internal var canViewErrorLog = true
        internal var canCopyErrorLog = true
        internal var canShareErrorLog = true
        internal var canSaveErrorLog = true
        internal var showTitle = true
        internal var iconDrawable = 0
        internal var backgroundColour = R.color.white
        internal var backgroundTextColour = R.color.black
        internal var buttonColour = R.color.black
        internal var buttonTextColour = R.color.white
        internal var errorLogMessage = ASK_FOR_ERROR_LOG
        internal val copyrightInfo = COPYRIGHT_INFO
        fun setUCEHEnabled(isUCEHEnabled: Boolean): Builder {
            this.isUCEHEnabled = isUCEHEnabled
            return this
        }

        fun setTrackActivitiesEnabled(isTrackActivitiesEnabled: Boolean): Builder {
            this.isTrackActivitiesEnabled = isTrackActivitiesEnabled
            return this
        }

        fun setBackgroundModeEnabled(isBackgroundModeEnabled: Boolean): Builder {
            this.isBackgroundModeEnabled = isBackgroundModeEnabled
            return this
        }

        fun setCanViewErrorLog(canViewErrorLog: Boolean): Builder {
            this.canViewErrorLog = canViewErrorLog
            return this
        }

        fun setCanCopyErrorLog(canCopyErrorLog: Boolean): Builder {
            this.canCopyErrorLog = canCopyErrorLog
            return this
        }

        fun setCanShareErrorLog(canShareErrorLog: Boolean): Builder {
            this.canShareErrorLog = canShareErrorLog
            return this
        }

        fun setCanSaveErrorLog(canSaveErrorLog: Boolean): Builder {
            this.canSaveErrorLog = canSaveErrorLog
            return this
        }

        /**
         * Does some thing in old style.
         *
         */
        @Deprecated("use {@link #setIconDrawable(int)} instead.")
        fun setBackgroundDrawable(backgroundDrawable: Int): Builder {
            iconDrawable = backgroundDrawable
            return this
        }

        fun setIconDrawable(iconDrawable: Int): Builder {
            this.iconDrawable = iconDrawable
            return this
        }

        fun setBackgroundColour(backgroundColour: Int): Builder {
            this.backgroundColour = backgroundColour
            return this
        }

        fun setBackgroundTextColour(backgroundTextColour: Int): Builder {
            this.backgroundTextColour = backgroundTextColour
            return this
        }

        fun setButtonColour(buttonColour: Int): Builder {
            this.buttonColour = buttonColour
            return this
        }

        fun setButtonTextColour(buttonTextColour: Int): Builder {
            this.buttonTextColour = buttonTextColour
            return this
        }

        fun setShowAsDialog(isDialog: Boolean): Builder {
            this.isDialog = isDialog
            return this
        }

        fun setShowTitle(showTitle: Boolean): Builder {
            this.showTitle = showTitle
            return this
        }

        fun setErrorLogMessage(errorLogMessage: String): Builder {
            this.errorLogMessage = errorLogMessage
            return this
        }

        fun addCommaSeparatedEmailAddresses(commaSeparatedEmailAddresses: String?): Builder {
            this.commaSeparatedEmailAddresses = commaSeparatedEmailAddresses ?: ""
            return this
        }

        fun build(): UCEHandler {
            return UCEHandler(this)
        }

    }

    companion object {
        const val EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE"
        const val EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG"
        private const val TAG = "UCEHandler"
        private const val UCE_HANDLER_PACKAGE_NAME = "com.jampez.uceh"
        private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
        private const val MAX_STACK_TRACE_SIZE = 131071 //128 KB - 1
        private const val MAX_ACTIVITIES_IN_LOG = 50
        private const val SHARED_PREFERENCES_FILE = "uceh_preferences"
        private const val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp"
        private const val ASK_FOR_ERROR_LOG = "Help developers by providing error details.\nThank you for your support."
        private const val COPYRIGHT_INFO = "UCE Handler courtesy of\nCopyright © 2018 Rohit Sahebrao Surwase."
        private val activityLog: Deque<String> = ArrayDeque(MAX_ACTIVITIES_IN_LOG)
        var commaSeparatedEmailAddresses: String? = null

        @SuppressLint("StaticFieldLeak")
        private var isInBackground = true
        private var isBackgroundMode: Boolean = false
        private var isUCEHEnabled: Boolean = false
        private var isTrackActivitiesEnabled: Boolean = false
        var showAsDialog: Boolean = false
        var canViewErrorLog: Boolean = false
        var canCopyErrorLog: Boolean = false
        var canShareErrorLog: Boolean = false
        var canSaveErrorLog: Boolean = false
        var showTitle: Boolean = false

        /**
         * Does some thing in old style.
         *
         */
        var iconDrawable: Int = 0
        var backgroundColour: Int = 0
        var backgroundTextColour: Int = 0
        var buttonColour: Int = 0
        var buttonTextColour: Int = 0
        var errorLogMessage: String = ""
        var copyrightInfo: String = ""
        private var lastActivityCreated = WeakReference<Activity?>(null)
        private fun setUCEHandler(context: Context?) {
            try {
                if (context != null) {
                    val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                    if (oldHandler != null && oldHandler.javaClass.name.startsWith(UCE_HANDLER_PACKAGE_NAME)) {
                        Log.e(TAG, "UCEHandler was already installed, doing nothing!")
                    } else {
                        if (oldHandler != null && !oldHandler.javaClass.name.startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                            Log.e(TAG, "You already have an UncaughtExceptionHandler. If you use a custom UncaughtExceptionHandler, it should be initialized after UCEHandler! Installing anyway, but your original handler will not be called.")
                        }
                        val application = context.applicationContext as Application
                        //Setup UCE Handler.
                        Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { thread, throwable ->
                            if (isUCEHEnabled) {
                                Log.e(TAG, "App crashed, executing UCEHandler's UncaughtExceptionHandler", throwable)
                                if (hasCrashedInTheLastSeconds(application)) {
                                    Log.e(TAG, "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?", throwable)
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable)
                                        return@UncaughtExceptionHandler
                                    }
                                } else {
                                    setLastCrashTimestamp(application, Date().time)
                                    if (!isInBackground || isBackgroundMode) {
                                        val intent = Intent(application, UCEDefaultActivity::class.java)
                                        val sw = StringWriter()
                                        val pw = PrintWriter(sw)
                                        throwable.printStackTrace(pw)
                                        var stackTraceString = sw.toString()
                                        if (stackTraceString.length > MAX_STACK_TRACE_SIZE) {
                                            val disclaimer = " [stack trace too large]"
                                            stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length) + disclaimer
                                        }
                                        intent.putExtra(EXTRA_STACK_TRACE, stackTraceString)
                                        if (isTrackActivitiesEnabled) {
                                            val activityLogStringBuilder = StringBuilder()
                                            while (!activityLog.isEmpty()) {
                                                activityLogStringBuilder.append(activityLog.poll())
                                            }
                                            intent.putExtra(EXTRA_ACTIVITY_LOG, activityLogStringBuilder.toString())
                                        }
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        application.startActivity(intent)
                                    } else {
                                        if (oldHandler != null) {
                                            oldHandler.uncaughtException(thread, throwable)
                                            return@UncaughtExceptionHandler
                                        }
                                        //If it is null (should not be), we let it continue and kill the process or it will be stuck
                                    }
                                }
                                val lastActivity = lastActivityCreated.get()
                                if (lastActivity != null) {
                                    lastActivity.finish()
                                    lastActivityCreated.clear()
                                }
                                killCurrentProcess()
                            } else oldHandler?.uncaughtException(thread, throwable)
                        })
                        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                            var currentlyStartedActivities = 0
                            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
                                if (activity.javaClass != UCEDefaultActivity::class.java) {
                                    lastActivityCreated = WeakReference(activity)
                                }
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add("""${dateFormat.format(Date())}: ${activity.javaClass.simpleName} created
""")
                                }
                            }

                            override fun onActivityStarted(activity: Activity) {
                                currentlyStartedActivities++
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivityResumed(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add("""${dateFormat.format(Date())}: ${activity.javaClass.simpleName} resumed
""")
                                }
                            }

                            override fun onActivityPaused(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add("""${dateFormat.format(Date())}: ${activity.javaClass.simpleName} paused
""")
                                }
                            }

                            override fun onActivityStopped(activity: Activity) {
                                currentlyStartedActivities--
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                            override fun onActivityDestroyed(activity: Activity) {
                                if (isTrackActivitiesEnabled) {
                                    activityLog.add("""${dateFormat.format(Date())}: ${activity.javaClass.simpleName} destroyed
""")
                                }
                            }
                        })
                    }
                    Log.i(TAG, "UCEHandler has been installed.")
                } else {
                    Log.e(TAG, "Context can not be null")
                }
            } catch (throwable: Throwable) {
                Log.e(TAG, "UCEHandler can not be initialized. Help making it better by reporting this as a bug.", throwable)
            }
        }

        /**
         * INTERNAL method that tells if the app has crashed in the last seconds.
         * This is used to avoid restart loops.
         *
         * @return true if the app has crashed in the last seconds, false otherwise.
         */
        private fun hasCrashedInTheLastSeconds(context: Context): Boolean {
            val lastTimestamp = getLastCrashTimestamp(context)
            val currentTimestamp = Date().time
            return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < 3000
        }

        @SuppressLint("ApplySharedPref")
        private fun setLastCrashTimestamp(context: Context, timestamp: Long) {
            context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit().putLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, timestamp).commit()
        }

        private fun killCurrentProcess() {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        private fun getLastCrashTimestamp(context: Context): Long {
            return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getLong(SHARED_PREFERENCES_FIELD_TIMESTAMP, -1)
        }

        fun closeApplication(activity: Activity) {
            activity.finish()
            killCurrentProcess()
        }

        fun getColourFromInt(context: Context, id: Int): Int {
            return try {
                val version = Build.VERSION.SDK_INT
                if (version >= 23) ContextCompat.getColor(context, id) else context.resources.getColor(id)
            } catch (e: Exception) {
                R.color.white
            }
        }

        fun getDrawableFromInt(context: Context, id: Int): Drawable? {
            val version = Build.VERSION.SDK_INT
            return try {
                if (version >= 21) ContextCompat.getDrawable(context, id) else context.resources.getDrawable(id)
            } catch (e: Exception) {
                null
            }
        }
    }

    init {
        isUCEHEnabled = builder.isUCEHEnabled
        isTrackActivitiesEnabled = builder.isTrackActivitiesEnabled
        isBackgroundMode = builder.isBackgroundModeEnabled
        iconDrawable = builder.iconDrawable
        backgroundColour = builder.backgroundColour
        backgroundTextColour = builder.backgroundTextColour
        buttonColour = builder.buttonColour
        buttonTextColour = builder.buttonTextColour
        showAsDialog = builder.isDialog
        canViewErrorLog = builder.canViewErrorLog
        canCopyErrorLog = builder.canCopyErrorLog
        canShareErrorLog = builder.canShareErrorLog
        canSaveErrorLog = builder.canSaveErrorLog
        showTitle = builder.showTitle
        errorLogMessage = builder.errorLogMessage
        copyrightInfo = builder.copyrightInfo
        commaSeparatedEmailAddresses = builder.commaSeparatedEmailAddresses
        setUCEHandler(builder.context)
    }
}
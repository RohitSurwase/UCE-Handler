package com.jampez.uceh

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class UCEDefaultActivity : Activity() {
    private var strCurrentErrorLog: String? = null
    private var txtFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState?: Bundle())
        if (Build.VERSION.SDK_INT > 21) setTheme(android.R.style.Theme_Material) else setTheme(android.R.style.Theme)
        if (UCEHandler.showAsDialog) {
            if (Build.VERSION.SDK_INT > 21) setTheme(android.R.style.Theme_Material_Dialog) else setTheme(android.R.style.Theme_Dialog)
            setFinishOnTouchOutside(false)
        }
        if (!UCEHandler.showTitle) requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.default_error_activity)
        if (UCEHandler.showAsDialog) window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        findViewById<View>(R.id.main_layout).setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.backgroundColour))
        (findViewById<View>(R.id.unexpected_error) as TextView).setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.backgroundTextColour))
        val errorLogMessage = findViewById<TextView>(R.id.error_log_message)
        errorLogMessage.text = UCEHandler.errorLogMessage
        errorLogMessage.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.backgroundTextColour))
        val copyrightInfo = findViewById<TextView>(R.id.copyright_info)
        copyrightInfo.text = UCEHandler.copyrightInfo
        copyrightInfo.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.backgroundTextColour))
        (findViewById<View>(R.id.background_image) as ImageView).setImageDrawable(UCEHandler.getDrawableFromInt(applicationContext, UCEHandler.iconDrawable))
        val viewErrorLog = findViewById<Button>(R.id.button_view_error_log)
        if (UCEHandler.canViewErrorLog) {
            viewErrorLog.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
            viewErrorLog.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
            viewErrorLog.setOnClickListener {
                val dialog = AlertDialog.Builder(this@UCEDefaultActivity)
                        .setTitle("Error Log")
                        .setMessage(getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent))
                        .setPositiveButton("Copy Log & Close"
                        ) { dialog, _ ->
                            copyErrorToClipboard()
                            dialog.dismiss()
                        }
                        .setNeutralButton("Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        .show()
                val textView = dialog.findViewById<TextView>(android.R.id.message)
                textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            }
        } else viewErrorLog.visibility = View.GONE
        val copyErrorLog = findViewById<Button>(R.id.button_copy_error_log)
        if (UCEHandler.canCopyErrorLog) {
            copyErrorLog.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
            copyErrorLog.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
            copyErrorLog.setOnClickListener { copyErrorToClipboard() }
        } else copyErrorLog.visibility = View.GONE
        val shareErrorLog = findViewById<Button>(R.id.button_share_error_log)
        if (UCEHandler.canShareErrorLog) {
            shareErrorLog.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
            shareErrorLog.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
            shareErrorLog.setOnClickListener { shareErrorLog() }
        } else shareErrorLog.visibility = View.GONE
        val saveErrorLog = findViewById<Button>(R.id.button_save_error_log)
        if (UCEHandler.canSaveErrorLog) {
            saveErrorLog.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
            saveErrorLog.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
            saveErrorLog.setOnClickListener { saveErrorLogToFile() }
        } else saveErrorLog.visibility = View.GONE
        val emailLog = findViewById<Button>(R.id.button_email_error_log)
        if (UCEHandler.commaSeparatedEmailAddresses != null) {
            emailLog.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
            emailLog.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
            emailLog.setOnClickListener { emailErrorLog() }
        } else emailLog.visibility = View.GONE
        val closeApp = findViewById<Button>(R.id.button_close_app)
        closeApp.setTextColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonTextColour))
        closeApp.setBackgroundColor(UCEHandler.getColourFromInt(applicationContext, UCEHandler.buttonColour))
        closeApp.setOnClickListener { UCEHandler.closeApplication(this@UCEDefaultActivity) }
    }

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    private fun getVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun emailErrorLog() {
        saveErrorLogToFile()
        val errorLog = getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent)
        val emailAddressArray: Array<String> = UCEHandler.commaSeparatedEmailAddresses!!.trim { it <= ' ' }.split("\\s*,\\s*").toTypedArray()
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddressArray)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(this@UCEDefaultActivity) + " Application Crash Error Log")
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_welcome_note) + errorLog)
        if (txtFile!!.exists()) {
            val filePath = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", txtFile!!)
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath)
        }
        startActivity(Intent.createChooser(emailIntent, "Email Error Log"))
    }

    private fun getActivityLogFromIntent(intent: Intent): String? {
        return intent.getStringExtra(UCEHandler.EXTRA_ACTIVITY_LOG)
    }

    private fun getStackTraceFromIntent(intent: Intent): String? {
        return intent.getStringExtra(UCEHandler.EXTRA_STACK_TRACE)
    }

    private fun saveErrorLogToFile() {
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (isSDPresent && isExternalStorageWritable) {
            val currentDate = Date()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            var strCurrentDate = dateFormat.format(currentDate)
            strCurrentDate = strCurrentDate.replace(" ", "_")
            val errorLogFileName = getApplicationName(this@UCEDefaultActivity) + "_Error-Log_" + strCurrentDate
            val errorLog = getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent)

            val fullPath = externalCacheDir!!.absolutePath+ "/AppErrorLogs_UCEH/"
            val outputStream: FileOutputStream
            try {
                val file = File(fullPath)
                val fileMade = file.mkdir()
                Log.d("fileMade", fileMade.toString() + "")
                txtFile = File("$fullPath$errorLogFileName.txt")
                val newFile = txtFile!!.createNewFile()
                Log.d("newFile", newFile.toString() + "")
                outputStream = FileOutputStream(txtFile!!)
                outputStream.write(errorLog!!.toByteArray())
                outputStream.close()
                if (txtFile!!.exists()) {
                    Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("REQUIRED", "This app does not have write storage permission to save log file.")
                Toast.makeText(this, "Storage Permission Not Found", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun shareErrorLog() {
        val errorLog = getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        } else {
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        share.putExtra(Intent.EXTRA_SUBJECT, "Application Crash Error Log")
        share.putExtra(Intent.EXTRA_TEXT, errorLog)
        startActivity(Intent.createChooser(share, "Share Error Log"))
    }

    private fun copyErrorToClipboard() {
        val errorInformation = getAllErrorDetailsFromIntent(this@UCEDefaultActivity, intent)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("View Error Log", errorInformation)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@UCEDefaultActivity, "Error Log Copied", Toast.LENGTH_SHORT).show()
    }

    private fun getAllErrorDetailsFromIntent(context: Context, intent: Intent): String? {
        return if (TextUtils.isEmpty(strCurrentErrorLog)) {
            val lineSeparator = "\n"
            val errorReport = StringBuilder()
            errorReport.append("***** UCE HANDLER Library ")
            errorReport.append("\n***** DEVICE INFO \n")
            errorReport.append("Brand: ")
            errorReport.append(Build.BRAND)
            errorReport.append(lineSeparator)
            errorReport.append("Device: ")
            errorReport.append(Build.DEVICE)
            errorReport.append(lineSeparator)
            errorReport.append("Model: ")
            errorReport.append(Build.MODEL)
            errorReport.append(lineSeparator)
            errorReport.append("Manufacturer: ")
            errorReport.append(Build.MANUFACTURER)
            errorReport.append(lineSeparator)
            errorReport.append("Product: ")
            errorReport.append(Build.PRODUCT)
            errorReport.append(lineSeparator)
            errorReport.append("SDK: ")
            errorReport.append(Build.VERSION.SDK_INT)
            errorReport.append(lineSeparator)
            errorReport.append("Release: ")
            errorReport.append(Build.VERSION.RELEASE)
            errorReport.append(lineSeparator)
            errorReport.append("\n***** APP INFO \n")
            val versionName = getVersionName(context)
            errorReport.append("Version: ")
            errorReport.append(versionName)
            errorReport.append(lineSeparator)
            val currentDate = Date()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val firstInstallTime = getFirstInstallTimeAsString(context, dateFormat)
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ")
                errorReport.append(firstInstallTime)
                errorReport.append(lineSeparator)
            }
            val lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat)
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ")
                errorReport.append(lastUpdateTime)
                errorReport.append(lineSeparator)
            }
            errorReport.append("Current Date: ")
            errorReport.append(dateFormat.format(currentDate))
            errorReport.append(lineSeparator)
            errorReport.append("\n***** ERROR LOG \n")
            errorReport.append(getStackTraceFromIntent(intent))
            errorReport.append(lineSeparator)
            val activityLog = getActivityLogFromIntent(intent)
            errorReport.append(lineSeparator)
            if (activityLog != null) {
                errorReport.append("\n***** USER ACTIVITIES \n")
                errorReport.append("User Activities: ")
                errorReport.append(activityLog)
                errorReport.append(lineSeparator)
            }
            errorReport.append("\n***** END OF LOG *****\n")
            strCurrentErrorLog = errorReport.toString()
            strCurrentErrorLog
        } else {
            strCurrentErrorLog
        }
    }

    private fun getFirstInstallTimeAsString(context: Context, dateFormat: DateFormat): String {
        val firstInstallTime: Long
        return try {
            firstInstallTime = context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            dateFormat.format(Date(firstInstallTime))
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    private fun getLastUpdateTimeAsString(context: Context, dateFormat: DateFormat): String {
        val lastUpdateTime: Long
        return try {
            lastUpdateTime = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
            dateFormat.format(Date(lastUpdateTime))
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
}
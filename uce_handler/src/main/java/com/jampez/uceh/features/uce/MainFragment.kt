package com.jampez.uceh.features.uce

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.jampez.uceh.R
import com.jampez.uceh.features.github.Github
import com.jampez.uceh.utils.getColorCompat
import com.jampez.uceh.utils.getDrawableCompat
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.background_image
import kotlinx.android.synthetic.main.fragment_main.button_view_error_log
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private var strCurrentErrorLog: String? = null

    private val viewModel: UCEViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        main_layout.setBackgroundColor(context?.getColorCompat(UCEHandler.backgroundColour)!!)
        title.setTextColor(context?.getColorCompat(UCEHandler.backgroundTextColour)!!)

        subtitle.text = UCEHandler.errorLogMessage
        subtitle.setTextColor(context?.getColorCompat(UCEHandler.backgroundTextColour)!!)

        background_image.setImageDrawable(context?.getDrawableCompat(UCEHandler.iconDrawable)!!)

        //make sure we have a service
        if(UCEHandler.canCreateSupportTicket) {

            //set any button text, implement click listener for manual mode
            //and update visible UI elements
            //otherwise post issue right away
            if(UCEHandler.issueCreationMode == Github.Mode.Manual){
                button_create_support_ticket.visibility = VISIBLE
                support_ticket_request_progress.visibility = INVISIBLE

                if(UCEHandler._githubService!!.buttonText != ""){
                    button_create_support_ticket.text = UCEHandler._githubService!!.buttonText
                }

                button_create_support_ticket.setOnClickListener {
                    postGithubIssue()
                }
            } else {
                postGithubIssue()
            }

        }

        button_view_error_log.setTextColor(context?.getColorCompat(UCEHandler.buttonTextColour)!!)
        button_view_error_log.setBackgroundColor(context?.getColorCompat(UCEHandler.buttonColour)!!)
        button_view_error_log.setOnClickListener { AlertDialog.Builder(context)
                .setTitle("Error Log")
                .setMessage(getAllErrorDetailsFromIntent())
                .setNeutralButton("Close"
                ) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun postGithubIssue(){
        if(context != null){
            button_create_support_ticket.visibility = INVISIBLE
            support_ticket_request_progress.visibility = VISIBLE

            viewModel.postGithubIssue(getAllErrorDetailsFromIntent()).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Log.d("github response", it.data.toString())

                var responseText = "There was an issue creating the issue"
                val message = it.data?.message
                responseText = "$responseText: \n$message"
                if(message == null || message.isNullOrEmpty()){
                    val supportTickerNumber = it.data?.number
                    val supportTicketTitle = it.data?.title

                    responseText = "$supportTicketTitle " +
                            "\n\nSorry about that! " +
                            "\n\nYour support ticket number is: $supportTickerNumber\n\n" +
                            "Please use this when talking to our support team about this issue."

                }

                support_ticket_response.text = responseText
                support_ticket_response.visibility = VISIBLE
                button_create_support_ticket.visibility = INVISIBLE
                support_ticket_request_progress.visibility = INVISIBLE
            })
        }
    }

    private fun getAllErrorDetailsFromIntent(): String? {
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
            val versionName = getVersionName()
            errorReport.append("Version: ")
            errorReport.append(versionName)
            errorReport.append(lineSeparator)
            val currentDate = Date()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val firstInstallTime = getFirstInstallTimeAsString(dateFormat)
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ")
                errorReport.append(firstInstallTime)
                errorReport.append(lineSeparator)
            }
            val lastUpdateTime = getLastUpdateTimeAsString(dateFormat)
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ")
                errorReport.append(lastUpdateTime)
                errorReport.append(lineSeparator)
            }
            errorReport.append("Current Date: ")
            errorReport.append(dateFormat.format(currentDate))
            errorReport.append(lineSeparator)
            errorReport.append("\n***** ERROR LOG \n")
            errorReport.append(activity?.intent?.let { getStackTraceFromIntent(it) })
            errorReport.append(lineSeparator)
            val activityLog = activity?.intent?.let { getActivityLogFromIntent(it) }
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

    private fun getVersionName(): String {
        return if(context != null){
            try {
                val packageInfo = requireContext().packageManager
                        .getPackageInfo(requireContext().packageName, 0)
                packageInfo.versionName
            } catch (e: Exception) {
                "Unknown"
            }
        } else {
            "Unknown"
        }
    }

    private fun getActivityLogFromIntent(intent: Intent): String? {
        return intent.getStringExtra(UCEHandler.EXTRA_ACTIVITY_LOG)
    }

    private fun getStackTraceFromIntent(intent: Intent): String? {
        return intent.getStringExtra(UCEHandler.EXTRA_STACK_TRACE)
    }

    private fun getFirstInstallTimeAsString(dateFormat: DateFormat): String {
        val firstInstallTime: Long
        return if(context != null){
            try {
                firstInstallTime = requireContext().packageManager
                        .getPackageInfo(requireContext().packageName, 0).firstInstallTime
                dateFormat.format(Date(firstInstallTime))
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }
        } else{
            ""
        }
    }

    private fun getLastUpdateTimeAsString(dateFormat: DateFormat): String {
        val lastUpdateTime: Long
        return if(context != null){
            try {
                lastUpdateTime = requireContext().packageManager
                        .getPackageInfo(requireContext().packageName, 0).lastUpdateTime
                dateFormat.format(Date(lastUpdateTime))
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }
        } else {
            ""
        }
    }
}

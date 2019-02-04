package com.jampez.uceh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static com.jampez.uceh.UCEHandler.closeApplication;
import static com.jampez.uceh.UCEHandler.getBackgroundColour;
import static com.jampez.uceh.UCEHandler.getBackgroundDrawable;
import static com.jampez.uceh.UCEHandler.getBackgroundTextColour;
import static com.jampez.uceh.UCEHandler.getButtonColour;
import static com.jampez.uceh.UCEHandler.getButtonTextColour;
import static com.jampez.uceh.UCEHandler.getCanCopyErrorLog;
import static com.jampez.uceh.UCEHandler.getCanSaveErrorLog;
import static com.jampez.uceh.UCEHandler.getCanShareErrorLog;
import static com.jampez.uceh.UCEHandler.getCanViewErrorLog;
import static com.jampez.uceh.UCEHandler.getColourFromInt;
import static com.jampez.uceh.UCEHandler.getCommaSeparatedEmailAddresses;
import static com.jampez.uceh.UCEHandler.getCopyrightInfo;
import static com.jampez.uceh.UCEHandler.getDrawableFromInt;
import static com.jampez.uceh.UCEHandler.getErrorLogMessage;
import static com.jampez.uceh.UCEHandler.getShowAsDialog;
import static com.jampez.uceh.UCEHandler.getShowTitle;

/**
 * <b></b>
 * <p>This class is used to </p>
 * Created by Rohit.
 */
public final class UCEDefaultActivity extends Activity {
    private String strCurrentErrorLog;
    File txtFile;

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT > 21)
            setTheme(android.R.style.Theme_Material);
        else
            setTheme(android.R.style.Theme);

        if(getShowAsDialog()) {
            if(Build.VERSION.SDK_INT > 21)
                setTheme(android.R.style.Theme_Material_Dialog);
            else
                setTheme(android.R.style.Theme_Dialog);

            this.setFinishOnTouchOutside(false);
        }

        if(!getShowTitle())
            requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.default_error_activity);

        if(getShowAsDialog())
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        findViewById(R.id.main_layout).setBackgroundColor(getColourFromInt(getApplicationContext(), getBackgroundColour()));

        ((TextView)findViewById(R.id.unexpected_error)).setTextColor(getColourFromInt(getApplicationContext(), getBackgroundTextColour()));

        TextView errorLogMessage = findViewById(R.id.error_log_message);

        errorLogMessage.setText(getErrorLogMessage());
        errorLogMessage.setTextColor(getColourFromInt(getApplicationContext(), getBackgroundTextColour()));

        TextView copyrightInfo = findViewById(R.id.copyright_info);

        copyrightInfo.setText(getCopyrightInfo());
        copyrightInfo.setTextColor(getColourFromInt(getApplicationContext(), getBackgroundTextColour()));

        ((ImageView)findViewById(R.id.background_image)).setImageDrawable(getDrawableFromInt(getApplicationContext(), getBackgroundDrawable()));

        Button viewErrorLog = findViewById(R.id.button_view_error_log);
        if(getCanViewErrorLog()) {
            viewErrorLog.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
            viewErrorLog.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
            viewErrorLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(UCEDefaultActivity.this)
                            .setTitle("Error Log")
                            .setMessage(getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent()))
                            .setPositiveButton("Copy Log & Close",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            copyErrorToClipboard();
                                            dialog.dismiss();
                                        }
                                    })
                            .setNeutralButton("Close",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                    TextView textView = dialog.findViewById(android.R.id.message);
                    if (textView != null) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    }
                }
            });
        }else
            viewErrorLog.setVisibility(GONE);

        Button copyErrorLog = findViewById(R.id.button_copy_error_log);
        if(getCanCopyErrorLog()) {
            copyErrorLog.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
            copyErrorLog.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
            copyErrorLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyErrorToClipboard();
                }
            });
        }else
            copyErrorLog.setVisibility(GONE);

        Button shareErrorLog = findViewById(R.id.button_share_error_log);
        if(getCanShareErrorLog()) {
            shareErrorLog.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
            shareErrorLog.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
            shareErrorLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareErrorLog();
                }
            });
        }else
            shareErrorLog.setVisibility(GONE);

        Button saveErrorLog = findViewById(R.id.button_save_error_log);
        if(getCanSaveErrorLog()) {
            saveErrorLog.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
            saveErrorLog.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
            saveErrorLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveErrorLogToFile();
                }
            });
        }else
            saveErrorLog.setVisibility(GONE);

        Button emailLog = findViewById(R.id.button_email_error_log);
        if(getCommaSeparatedEmailAddresses() != null) {
            emailLog.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
            emailLog.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
            emailLog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailErrorLog();
                }
            });
        }else
            emailLog.setVisibility(GONE);

        Button closeApp = findViewById(R.id.button_close_app);
        closeApp.setTextColor(getColourFromInt(getApplicationContext(), getButtonTextColour()));
        closeApp.setBackgroundColor(getColourFromInt(getApplicationContext(), getButtonColour()));
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApplication(UCEDefaultActivity.this);
            }
        });
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void emailErrorLog() {
        saveErrorLogToFile();
        String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        String[] emailAddressArray = UCEHandler.COMMA_SEPARATED_EMAIL_ADDRESSES.trim().split("\\s*,\\s*");
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddressArray);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getApplicationName(UCEDefaultActivity.this) + " Application Crash Error Log");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_welcome_note) + errorLog);
        if (txtFile.exists()) {
            Uri filePath = Uri.fromFile(txtFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath);
        }
        startActivity(Intent.createChooser(emailIntent, "Email Error Log"));
    }

    private String getActivityLogFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.EXTRA_ACTIVITY_LOG);
    }

    private String getStackTraceFromIntent(Intent intent) {
        return intent.getStringExtra(UCEHandler.EXTRA_STACK_TRACE);
    }

    private void saveErrorLogToFile() {
        boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent && isExternalStorageWritable()) {
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String strCurrentDate = dateFormat.format(currentDate);
            strCurrentDate = strCurrentDate.replace(" ", "_");
            String errorLogFileName = getApplicationName(UCEDefaultActivity.this) + "_Error-Log_" + strCurrentDate;
            String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
            String fullPath = Environment.getExternalStorageDirectory() + "/AppErrorLogs_UCEH/";
            FileOutputStream outputStream;
            try {
                File file = new File(fullPath);
                boolean fileMade = file.mkdir();
                Log.d("fileMade", fileMade+"");
                txtFile = new File(fullPath + errorLogFileName + ".txt");
                boolean newFile = txtFile.createNewFile();
                Log.d("newFile", newFile+"");
                outputStream = new FileOutputStream(txtFile);
                outputStream.write(errorLog.getBytes());
                outputStream.close();
                if (txtFile.exists()) {
                    Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("REQUIRED", "This app does not have write storage permission to save log file.");
                Toast.makeText(this, "Storage Permission Not Found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void shareErrorLog() {
        String errorLog = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Application Crash Error Log");
        share.putExtra(Intent.EXTRA_TEXT, errorLog);
        startActivity(Intent.createChooser(share, "Share Error Log"));
    }

    private void copyErrorToClipboard() {
        String errorInformation = getAllErrorDetailsFromIntent(UCEDefaultActivity.this, getIntent());
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("View Error Log", errorInformation);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(UCEDefaultActivity.this, "Error Log Copied", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAllErrorDetailsFromIntent(Context context, Intent intent) {
        if (TextUtils.isEmpty(strCurrentErrorLog)) {
            String LINE_SEPARATOR = "\n";
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("***** UCE HANDLER Library ");
            errorReport.append("\n***** DEVICE INFO \n");
            errorReport.append("Brand: ");
            errorReport.append(Build.BRAND);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Device: ");
            errorReport.append(Build.DEVICE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Model: ");
            errorReport.append(Build.MODEL);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Manufacturer: ");
            errorReport.append(Build.MANUFACTURER);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Product: ");
            errorReport.append(Build.PRODUCT);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("SDK: ");
            errorReport.append(Build.VERSION.SDK);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("Release: ");
            errorReport.append(Build.VERSION.RELEASE);
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n***** APP INFO \n");
            String versionName = getVersionName(context);
            errorReport.append("Version: ");
            errorReport.append(versionName);
            errorReport.append(LINE_SEPARATOR);
            Date currentDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String firstInstallTime = getFirstInstallTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(firstInstallTime)) {
                errorReport.append("Installed On: ");
                errorReport.append(firstInstallTime);
                errorReport.append(LINE_SEPARATOR);
            }
            String lastUpdateTime = getLastUpdateTimeAsString(context, dateFormat);
            if (!TextUtils.isEmpty(lastUpdateTime)) {
                errorReport.append("Updated On: ");
                errorReport.append(lastUpdateTime);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("Current Date: ");
            errorReport.append(dateFormat.format(currentDate));
            errorReport.append(LINE_SEPARATOR);
            errorReport.append("\n***** ERROR LOG \n");
            errorReport.append(getStackTraceFromIntent(intent));
            errorReport.append(LINE_SEPARATOR);
            String activityLog = getActivityLogFromIntent(intent);
            errorReport.append(LINE_SEPARATOR);
            if (activityLog != null) {
                errorReport.append("\n***** USER ACTIVITIES \n");
                errorReport.append("User Activities: ");
                errorReport.append(activityLog);
                errorReport.append(LINE_SEPARATOR);
            }
            errorReport.append("\n***** END OF LOG *****\n");
            strCurrentErrorLog = errorReport.toString();
            return strCurrentErrorLog;
        } else {
            return strCurrentErrorLog;
        }
    }

    private String getFirstInstallTimeAsString(Context context, DateFormat dateFormat) {
        long firstInstallTime;
        try {
            firstInstallTime = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
            return dateFormat.format(new Date(firstInstallTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private String getLastUpdateTimeAsString(Context context, DateFormat dateFormat) {
        long lastUpdateTime;
        try {
            lastUpdateTime = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).lastUpdateTime;
            return dateFormat.format(new Date(lastUpdateTime));
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
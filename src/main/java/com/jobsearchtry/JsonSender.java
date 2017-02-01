package com.jobsearchtry;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;

import com.jobsearchtry.utils.GlobalData;
import com.jobsearchtry.utils.UtilService;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class JsonSender implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDfltExceptionHandler;
    private JsonSender S_mInstance;
    private Context mCurContext;
    private String mPkg_VersionName;
    private String mPkg_PackageName;
    private String mCtx_FilePath;
    private String mPkg_OSBld_PhoneModel;
    private String mPkg_OSBld_AndroidVersion;
    private String mPkg_OSBld_Board;
    private String mPkg_OSBld_Brand;
    private String mPkg_OSBld_Device;
    private String mPkg_OSBld_Display;
    private String mPkg_OSBld_FingerPrint;
    private String mPkg_OSBld_Host;
    private String mPkg_OSBld_ID;
    private String mPkg_OSBld_Model;
    private String mPkg_OSBld_Product;
    private String mPkg_OSBld_Tags;
    private long mPkg_OSBld_Time;
    private String mPkg_OSBld_Type;
    private String mPkg_OSBld_User;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Date CurDate = new Date();
        String Report = "Error Report collected on : " + CurDate.toString() + "\n\n";
        Report += "Environment Details : \n";
        Report += "===================== \n";
        Report += CreateInformationString();
        Report += "Stack : \n";
        Report += "======= \n";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        Report += stacktrace + "\n";
        Throwable cause = e.getCause();
        while (cause != null) {
            Report += "Cause : \n";
            Report += "======= \n";
            cause.printStackTrace(printWriter);
            Report += result.toString();
            cause = cause.getCause();
        }
        printWriter.close();
        Report += "**** End of current Report ***";
        SaveAsFile(Report);
        CheckCrashErrorAndSendMail(mCurContext);
        mDfltExceptionHandler.uncaughtException(t, e);
    }

    void getInstance() {
        if (S_mInstance == null)
            S_mInstance = new JsonSender();
    }

    void Init(Context context) {
        mDfltExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mCurContext = context;
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize);
    }

    private long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return (totalBlocks * blockSize);
    }

    private void CollectPackageInformation(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            mPkg_VersionName = pi.versionName;
            mPkg_PackageName = pi.packageName;
            mCtx_FilePath = context.getFilesDir().getAbsolutePath();
            mPkg_OSBld_PhoneModel = android.os.Build.MODEL;
            mPkg_OSBld_AndroidVersion = android.os.Build.VERSION.RELEASE;
            mPkg_OSBld_Board = android.os.Build.BOARD;
            mPkg_OSBld_Brand = android.os.Build.BRAND;
            mPkg_OSBld_Device = android.os.Build.DEVICE;
            mPkg_OSBld_Display = android.os.Build.DISPLAY;
            mPkg_OSBld_FingerPrint = android.os.Build.FINGERPRINT;
            mPkg_OSBld_Host = android.os.Build.HOST;
            mPkg_OSBld_ID = android.os.Build.ID;
            mPkg_OSBld_Model = android.os.Build.MODEL;
            mPkg_OSBld_Product = android.os.Build.PRODUCT;
            mPkg_OSBld_Tags = android.os.Build.TAGS;
            mPkg_OSBld_Time = android.os.Build.TIME;
            mPkg_OSBld_Type = android.os.Build.TYPE;
            mPkg_OSBld_User = android.os.Build.USER;
        } catch (Exception ignored) {
        }
    }

    private String CreateInformationString() {
        CollectPackageInformation(mCurContext);
        String ReturnVal;
        ReturnVal = "  Version  : " + mPkg_VersionName + "\n";
        ReturnVal += "  Package  : " + mPkg_PackageName + "\n";
        ReturnVal += "  FilePath : " + mCtx_FilePath + "\n\n";
        ReturnVal += "  Package Data \n";
        ReturnVal += "      Phone Model : " + mPkg_OSBld_PhoneModel + "\n";
        ReturnVal += "      Android Ver : " + mPkg_OSBld_AndroidVersion + "\n";
        ReturnVal += "      Board       : " + mPkg_OSBld_Board + "\n";
        ReturnVal += "      Brand       : " + mPkg_OSBld_Brand + "\n";
        ReturnVal += "      Device      : " + mPkg_OSBld_Device + "\n";
        ReturnVal += "      Display     : " + mPkg_OSBld_Display + "\n";
        ReturnVal += "      Finger Print: " + mPkg_OSBld_FingerPrint + "\n";
        ReturnVal += "      Host        : " + mPkg_OSBld_Host + "\n";
        ReturnVal += "      ID          : " + mPkg_OSBld_ID + "\n";
        ReturnVal += "      Model       : " + mPkg_OSBld_Model + "\n";
        ReturnVal += "      Product     : " + mPkg_OSBld_Product + "\n";
        ReturnVal += "      Tags        : " + mPkg_OSBld_Tags + "\n";
        ReturnVal += "      Time        : " + mPkg_OSBld_Time + "\n";
        ReturnVal += "      Type        : " + mPkg_OSBld_Type + "\n";
        ReturnVal += "      User        : " + mPkg_OSBld_User + "\n";
        ReturnVal += "  Internal Memory\n";
        ReturnVal += "      Total    : " + (getTotalInternalMemorySize() / 1024) + "k\n";
        ReturnVal += "      Available: " + (getAvailableInternalMemorySize() / 1024) + "k\n\n";
        return ReturnVal;
    }

    private void SaveAsFile(String ErrorContent) {
        try {
            long timestamp = System.currentTimeMillis();
            String ErrFileName = "stack-" + timestamp + ".stacktrace";
            FileOutputStream trace = mCurContext.openFileOutput(ErrFileName, Context.MODE_PRIVATE);
            trace.write(ErrorContent.getBytes());
            trace.flush();
            trace.close();
        } catch (Exception ignored) {
        }
    }

    private String[] GetCrashErrorFileList() {
        File dir = mCurContext.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".stacktrace");
            }
        };
        return dir.list(filter);
    }

    void CheckCrashErrorAndSendMail(Context _context) {
        try {
            if (null == mCtx_FilePath) {
                mCtx_FilePath = _context.getFilesDir().getAbsolutePath();
            }
            String[] reportFilesList = GetCrashErrorFileList();
            TreeSet<String> sortedFiles = new TreeSet<>();
            sortedFiles.addAll(Arrays.asList(reportFilesList));
            if (0 < reportFilesList.length) {
                String line;
                String WholeErrorText = "";
                int curIndex = 0;
                final int MaxSendMail = 5;
                for (String curString : sortedFiles) {
                    if (curIndex++ <= MaxSendMail) {
                        WholeErrorText += "New Trace collected :\n";
                        WholeErrorText += "=====================\n ";
                        String filePath = mCtx_FilePath + "/" + curString;
                        BufferedReader input = new BufferedReader(new FileReader(filePath));
                        while ((line = input.readLine()) != null) {
                            WholeErrorText += line + "\n";
                        }
                        input.close();
                    }
                    File curFile = new File(mCtx_FilePath + "/" + curString);
                    curFile.delete();
                }
                if (new UtilService().isNetworkAvailable(_context)) {
                    Log.e("report",WholeErrorText);
                    new sendCrashReport().execute(WholeErrorText);
                }
                //SendCrashErrorMail(_context, WholeErrorText, "mercy.krishnaveni@gmail.com");
            }
        } catch (Exception ignored) {
        }
    }

    private class sendCrashReport extends AsyncTask<String, String, String> {
        String pendingresponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            RequestBody formBody = new FormBody.Builder()
                    .add("content", args[0])
                    .build();
            Request request = new Request.Builder()
                    .url(GlobalData.url + "sendcrashreport.php")
                    .post(formBody).build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                pendingresponse = response.body().string();
            } catch (IOException ignored) {
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            super.onPostExecute(file_url);
        }
    }

//    private void SendCrashErrorMail(Context _context, String ErrorContent, String mailTo) {
//        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mercy.krishnaveni@gmail.com"});
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, " Try - Crash Report");
//        sendIntent.putExtra(Intent.EXTRA_TEXT, ErrorContent + "\n");
//        sendIntent.setType("message/rfc822");
//        _context.startActivity(sendIntent);
//    }
}
